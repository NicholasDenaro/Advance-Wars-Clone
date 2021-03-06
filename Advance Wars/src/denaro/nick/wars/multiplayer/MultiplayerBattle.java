package denaro.nick.wars.multiplayer;

import java.awt.Point;
import java.awt.event.KeyEvent;

import denaro.nick.core.controller.ControllerEvent;
import denaro.nick.server.Message;
import denaro.nick.wars.Battle;
import denaro.nick.wars.BattleSettings;
import denaro.nick.wars.Building;
import denaro.nick.wars.Main;
import denaro.nick.wars.Map;
import denaro.nick.wars.Team;
import denaro.nick.wars.Unit;

public class MultiplayerBattle extends Battle
{

	public MultiplayerBattle(Map map, BattleSettings settings)
	{
		super(map,null,settings);
		myTeam=-1;
	}
	
	public boolean attackUnit(Point attackerPoint, Point defenderPoint)
	{
		Message message=new Message(ServerClient.UNITATTACK);
		moveMessage(message);
		message.addInt(defenderPoint.x);
		message.addInt(defenderPoint.y);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);//TODO check to see if this is the correct return, or if it even matters
	}
	
	@Override
	public boolean loadUnit()
	{
		Message message=new Message(ServerClient.UNITLOAD);
		moveMessage(message);
		message.addInt(cursor().x);
		message.addInt(cursor().y);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);
	}
	
	@Override
	public boolean unloadUnit(int cargoslot, Point point)
	{
		Message message=new Message(ServerClient.UNITUNLOAD);
		moveMessage(message);
		message.addInt(cargoslot);
		message.addInt(point.x);
		message.addInt(point.y);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);
	}
	
	public void moveMessage(Message message)
	{
		message.addInt(path().first().x);
		message.addInt(path().first().y);
		message.addInt(path().points().size());
		for(int i=0;i<path().points().size();i++)
		{
			message.addInt(path().points().get(i).x);
			message.addInt(path().points().get(i).y);
		}
	}
	
	@Override
	public boolean moveUnit()
	{
		Message message=new Message(ServerClient.UNITMOVE);
		moveMessage(message);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);
	}
	
	@Override
	public boolean unitCaptureBuilding(Unit unit, Point destination)
	{
		Message message=new Message(ServerClient.UNITCAPTURE);
		moveMessage(message);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);
	}
	
	public void myTeam(int myTeam)
	{
		this.myTeam=myTeam;
	}
	
	public Team myTeam()
	{
		return(teams()[myTeam]);
	}
	
	public boolean purchaseUnit(Unit unit)
	{
		Main.engine().requestFocus(null);
		Message message=new Message(ServerClient.PURCHASEUNIT);
		message.addInt(unit.id());
		message.addInt(cursor().x);
		message.addInt(cursor().y);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);
	}
	
	public boolean hideUnit()
	{
		Message message=new Message(ServerClient.UNITHIDE);
		moveMessage(message);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);
	}
	
	public boolean unHideUnit()
	{
		Message message=new Message(ServerClient.UNITUNHIDE);
		moveMessage(message);
		Main.client.addMessage(message);
		Main.client.sendMessages();
		return(false);
	}
	
	@Override
	public void endTurn()
	{
		Message message=new Message(ServerClient.ENDTURN);
		Main.client.addMessage(message);
		Main.client.sendMessages();
	}
	
	@Override
	public void actionPerformed(ControllerEvent event)
	{
		if(event.action()==ControllerEvent.PRESSED)
		{
			if(isInputLocked())
				return;
			
			if(myTeam!=whosTurn().id())
			{
				if(event.code()==Main.LEFT)
					moveCursorLeft();
				if(event.code()==Main.RIGHT)
					moveCursorRight();
				if(event.code()==Main.UP)
					moveCursorUp();
				if(event.code()==Main.DOWN)
					moveCursorDown();
			}
			else
			{
				super.actionPerformed(event);
			}
		}
		else if(event.action()==ControllerEvent.RELEASED)
		{
			
		}
	}
	
	/*@Override
	public void keyPressed(KeyEvent ke)
	{
		if(isInputLocked())
			return;
		
		if(myTeam!=whosTurn().id())
		{
			if(ke.getKeyCode()==KeyEvent.VK_LEFT)
				moveCursorLeft();
			if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
				moveCursorRight();
			if(ke.getKeyCode()==KeyEvent.VK_UP)
				moveCursorUp();
			if(ke.getKeyCode()==KeyEvent.VK_DOWN)
				moveCursorDown();
		}
		else
		{
			super.keyPressed(ke);
		}
	}*/

	private int myTeam;
}
