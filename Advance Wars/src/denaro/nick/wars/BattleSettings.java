package denaro.nick.wars;

public class BattleSettings
{
	public BattleSettings()
	{
		startingFunds=0;
		fundsPerTurn=1000;
		fogOfWar=false;
		weather=0;
		animationSpeed=2;
	}
	
	public int animationSpeed()
	{
		return(animationSpeed);
	}
	
	public void startingFunds(int startingFunds)
	{
		this.startingFunds=startingFunds;
	}
	
	public int startingFunds()
	{
		return(startingFunds);
	}
	
	public void fundsPerTurn(int fundsPerTurn)
	{
		this.fundsPerTurn=fundsPerTurn;
	}
	
	public int fundsPerTurn()
	{
		return(fundsPerTurn);
	}
	
	public void fogOfWar(boolean fogOfWar)
	{
		this.fogOfWar=fogOfWar;
	}
	
	public boolean fogOfWar()
	{
		return(fogOfWar);
	}
	
	public void weather(int weather)
	{
		this.weather=weather;
	}
	
	public int weather()
	{
		return(weather);
	}
	
	private int startingFunds;
	private int fundsPerTurn;
	private boolean fogOfWar;
	private int weather;
	private int animationSpeed;
}
