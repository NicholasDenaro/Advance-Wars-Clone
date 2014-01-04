package denaro.nick.wars;

public class Battle
{
	public Battle(Map map, Team[] teams)
	{
		this.map=map;
		this.teams=teams;
		turn=-1;
		nextTurn();
	}
	
	public Map map()
	{
		return(map);
	}
	
	public Team whosTurn()
	{
		return(teams[turn]);
	}
	
	public void nextTurn()
	{
		turn=++turn%teams.length;
		map.resetFog();
		map.clearFogForTeam(whosTurn());
		map.enableUnitsForTeam(whosTurn());
	}
	
	private Map map;
	
	private int turn;
	
	private Team[] teams;
}
