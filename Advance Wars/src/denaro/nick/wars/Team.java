package denaro.nick.wars;

import java.awt.Color;

public class Team
{
	public Team(String name, Color color)
	{
		this.name=name;
		this.color=color;
	}
	
	public String name()
	{
		return(name);
	}
	
	public Color color()
	{
		return(color);
	}
	
	private String name;
	private Color color;
}
