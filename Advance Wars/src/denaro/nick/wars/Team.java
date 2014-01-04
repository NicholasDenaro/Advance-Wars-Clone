package denaro.nick.wars;

import java.awt.Color;

public class Team
{
	public Team(String name, int color, Commander commander)
	{
		this.name=name;
		this.color=color;
		this.commander=commander;
		funds=0;
	}
	
	public String name()
	{
		return(name);
	}
	
	public int color()
	{
		return(color);
	}
	
	public int funds()
	{
		return(funds);
	}
	
	public Commander commander()
	{
		return(commander);
	}
	
	private String name;
	private int color;
	private Commander commander;
	
	private int funds;
}
