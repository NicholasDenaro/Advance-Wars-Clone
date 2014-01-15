package denaro.nick.wars;

import java.awt.Color;

import denaro.nick.core.Identifiable;

public class Team extends Identifiable
{
	public Team(String name, int color)
	{
		this.name=name;
		this.color=color;
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
	
	public void addFunds(int funds)
	{
		this.funds+=funds;
	}
	
	public void funds(int funds)
	{
		this.funds=funds;
	}
	
	public Commander commander()
	{
		return(commander);
	}
	
	public static boolean sameTeam(Team team1, Team team2)
	{
		if(team1==null||team2==null)
			return(false);
		return(team1.id()==team2.id());
	}
	
	public static Team copy(Team other, Commander commander)
	{
		Team team=new Team(other.name,other.color);
		team.id(other.id());
		System.out.println("name: "+other.name);
		System.out.println("commander: "+commander.name());
		team.commander=commander;
		team.funds=other.funds;
		
		return(team);
	}
	
	private String name;
	private int color;
	private Commander commander;
	
	private int funds;
}
