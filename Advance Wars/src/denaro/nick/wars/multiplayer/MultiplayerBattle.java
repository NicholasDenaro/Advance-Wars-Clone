package denaro.nick.wars.multiplayer;

import java.util.ArrayList;

import denaro.nick.server.Message;
import denaro.nick.wars.Battle;
import denaro.nick.wars.BattleSettings;
import denaro.nick.wars.Main;
import denaro.nick.wars.Map;
import denaro.nick.wars.Team;

public class MultiplayerBattle extends Battle
{

	public MultiplayerBattle(Map map, ArrayList<Team> teams, BattleSettings settings)
	{
		super(map,teams,settings);
		started=false;
	}
	
	public boolean started()
	{
		return(started);
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
	public void loadUnit()
	{
		
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
	
	private boolean started;
}
