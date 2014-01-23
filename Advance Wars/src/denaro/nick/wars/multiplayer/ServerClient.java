package denaro.nick.wars.multiplayer;

import java.awt.Point;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;

import denaro.nick.server.Client;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;
import denaro.nick.wars.Battle;
import denaro.nick.wars.Main;
import denaro.nick.wars.Path;
import denaro.nick.wars.Team;
import denaro.nick.wars.Unit;

public class ServerClient extends Client
{

	public ServerClient(Socket socket, String name) throws IOException
	{
		super(socket);
		session=null;
		this.name=name;
	}

	public String name()
	{
		return(name);
	}
	
	@Override
	public void handleMessages(MyInputStream in, int messageid) throws IOException
	{
		Message mes;
		switch(messageid)
		{
			case SESSIONS:
				Set<String> sessions=MainServer.sessions();
				mes=new Message(messageid);
				mes.addInt(sessions.size());
				for(String s:sessions)
					mes.addString(s);
				addMessage(mes);
				sendMessages();
			return;
			case NEWSESSION:
				String sesname=in.readString();
				
				int size=in.readInt();
				ByteBuffer buffer=ByteBuffer.allocate(size);
				buffer.put(in.readBytes(size));
				Battle battle=Main.loadBattle(null,buffer);
				
				if(!sesname.equalsIgnoreCase("back"))
					session=MainServer.addSession(sesname,battle);
				if(session!=null)
					session.addClient(this);
				boolean added=session!=null;
				System.out.println("added: "+added);
				System.out.println("session players: "+session.players());
				mes=new Message(messageid);
				mes.addBoolean(added);
				if(added)
				{
					System.out.println("added!");
					Message batmes=Main.saveBattle(battle);
					mes.addInt(batmes.size());
					mes.addMessage(batmes);
					
					mes.addString(name);
					mes.addInt(0);
				}
				addMessage(mes);
				sendMessages();
			return;
			case JOINSESSION:
				sesname=in.readString();
				mes=new Message(messageid);
				if((session=MainServer.session(sesname))!=null&&!session.isFull())
				{
					Battle bat=session.battle();
					Message batmes=Main.saveBattle(bat);
					mes.addInt(batmes.size());
					mes.addMessage(batmes);
					
					int index=session.firstOpenSlot();
					
					mes.addInt(index);
					
					size=session.size();
					mes.addInt(size);
					for(int i=0;i<size;i++)
					{
						mes.addString(session.player(i));
						mes.addBoolean(session.isReady(i));
					}
					mes.addString(name);
					
					addMessage(mes);
					sendMessages();
					
					session.sendMessage(new Message(PLAYERJOINEDSESSION).addInt(index).addString(name));
					
					session.addClient(this);
				}
			return;
			case PLAYERREADY:
				int commander=in.readInt();
				session.clientReady(this,commander);
				mes=new Message(messageid);
				int pindex=session.playerIndex(this);
				mes.addInt(pindex);
				mes.addBoolean(session.isReady(pindex));
				session.sendMessage(mes);
				session.checkIfReady();
			return;
			case LEAVESESSION:
				mes=new Message(messageid);
				mes.addInt(session.removeClient(this));
				session.sendMessage(mes);
			return;
			case ENDTURN:
				battle=session.battle();
				battle.nextTurn();
				System.out.println("turn: "+battle.turn());
				mes=new Message(messageid);
				session.sendMessage(mes);
				mes=new Message(UPDATEMAP);
				for(int a=0;a<session.battle().map().height();a++)
				{
					for(int i=0;i<session.battle().map().width();i++)
					{
						Main.writeTerrain(mes,session.battle().map().terrain(i,a));
					}
				}
				for(int a=0;a<session.battle().map().height();a++)
				{
					for(int i=0;i<session.battle().map().width();i++)
					{
						Main.writeUnit(mes,session.battle().map().unit(i,a));
					}
				}
				session.sendMessage(mes);
				
				mes=new Message(UPDATETEAM);
				mes.addInt(battle.teams().get(session.playerIndex(this)).funds());
				session.whosTurnClient().addMessage(mes);
				session.whosTurnClient().sendMessages();
			return;
			case PURCHASEUNIT:
				int unitid=in.readInt();
				Point spawnLocation=new Point(in.readInt(),in.readInt());
				int cost=Main.unitMap.get(unitid).cost();
				Team team=session.team(this);
				if(Team.sameTeam(session.battle().whosTurn(),team))
				{
					mes=new Message(messageid);
					if(team.funds()>=cost)
					{
						session.battle().spawnUnit(Unit.copy(Main.unitMap.get(unitid),team,false),spawnLocation);
						mes.addBoolean(true);
						mes.addInt(unitid);
					}
					else
					{
						mes.addBoolean(false);
					}
					addMessage(mes);
					sendMessages();
				}
				else
				{
					System.out.println("?ERROR?: sent message during wrong turn.");
				}
			return;
			case UNITMOVE:
				int xpos=in.readInt();
				int ypos=in.readInt();
				Unit unit=session.battle().map().unit(xpos,ypos);
				int pathsize=in.readInt();
				Path path=new Path(session.battle().map(),unit.movementType(), unit.movement());
				path.start(in.readInt(),in.readInt());
				for(int i=0;i<pathsize-1;i++)
					path.addPoint(in.readInt(),in.readInt());
				if(path.isValid())
				{
					session.battle().moveUnitAlongPath(unit,path);
					mes=new Message(messageid);
					mes.addInt(xpos);
					mes.addInt(ypos);
					mes.addInt(path.points().size());
					for(int i=0;i<pathsize;i++)
					{
						mes.addInt(path.points().get(i).x);
						mes.addInt(path.points().get(i).y);
					}
					session.sendMessage(mes);
					//addMessage(mes);
					//sendMessages();
				}
				else
				{
					mes=new Message(messageid);
					mes.addBoolean(false);
					addMessage(mes);
					sendMessages();
				}
			return;
			case UNITUNITE:
			return;
			case UNITATTACK:
				xpos=in.readInt();
				ypos=in.readInt();
				unit=session.battle().map().unit(xpos,ypos);
				pathsize=in.readInt();
				path=new Path(session.battle().map(),unit.movementType(), unit.movement());
				path.start(in.readInt(),in.readInt());
				for(int i=0;i<pathsize-1;i++)
					path.addPoint(in.readInt(),in.readInt());
				
				Point attackerPoint=new Point(path.first().x,path.first().y);
				Point defenderPoint=new Point(in.readInt(),in.readInt());
				if(path.isValid())
				{
					//attack message
					mes=new Message(UNITATTACK);
					Unit attacker=session.battle().map().unit(attackerPoint.x,attackerPoint.y);
					Unit defender=session.battle().map().unit(defenderPoint.x,defenderPoint.y);
					session.battle().selectedUnit(attacker);
					session.battle().path(path);
					boolean attacked=session.battle().attackUnit(attackerPoint,defenderPoint);
					mes.addBoolean(attacked);
					if(attacked)
					{
						mes.addInt(path.last().x);
						mes.addInt(path.last().y);
						Main.writeUnit(mes,attacker);
						
						mes.addInt(defenderPoint.x);
						mes.addInt(defenderPoint.y);
						Main.writeUnit(mes,defender);
					}
					else
					{
						//do nothing! wooo
					}
					Message movemes=new Message(UNITMOVE);
					movemes.addInt(xpos);
					movemes.addInt(ypos);
					movemes.addInt(path.points().size());
					for(int i=0;i<pathsize;i++)
					{
						movemes.addInt(path.points().get(i).x);
						movemes.addInt(path.points().get(i).y);
					}
					mes.addMessage(movemes);
					session.sendMessage(mes);
				}
				else
				{
					mes=new Message(messageid);
					mes.addBoolean(false);
					addMessage(mes);
					sendMessages();
				}
			return;
		}
	}
	
	private BattleSession session;
	
	
	public static final int SESSIONS=5;
	public static final int NEWSESSION=6;
	public static final int JOINSESSION=7;
	public static final int PLAYERJOINEDSESSION=8;
	public static final int PLAYERREADY=9;
	public static final int LEAVESESSION=10;
	public static final int STARTSESSION=11;
	
	public static final int UPDATETEAM=15;
	public static final int UPDATEMAP=16;
	public static final int ENDTURN=17;
	
	public static final int PURCHASEUNIT=19;
	public static final int UNITMOVE=20;
	public static final int UNITUNITE=21;
	public static final int UNITATTACK=22;
	public static final int UNITCAPTURE=23;
	public static final int UNITLOAD=24;
	public static final int UNITUNLOAD=25;

	@Override
	public int maxMessageSize()
	{
		return(1024*10);
	}
	
	private String name;
}
