package denaro.nick.wars;

public abstract class BattleAction
{
	/**
	 * initialize nothing
	 */
	public void init()
	{
		
	}
	
	public abstract void callFunction();
	
	/**
	 * Returns true
	 * @return - default true
	 */
	public boolean shouldEnd()
	{
		return(true);
	}
}
