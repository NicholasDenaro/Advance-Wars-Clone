package denaro.nick.wars;

public abstract class BattleAction
{
	public abstract void init();
	
	public abstract void callFunction();
	
	public abstract boolean shouldEnd();
}
