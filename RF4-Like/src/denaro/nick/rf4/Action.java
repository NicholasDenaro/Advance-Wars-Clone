package denaro.nick.rf4;

import denaro.nick.core.Entity;
import denaro.nick.core.GameEngineByTick;

public abstract class Action
{
	public Action(double duration)
	{
		this.duration=(long)(GameEngineByTick.MILLISECOND*duration);
	}
	
	public abstract void act(Entity entity);
	
	public long duration()
	{
		return(duration);
	}
	
	public Action start()
	{
		start=System.currentTimeMillis();
		return(this);
	}
	
	public boolean finished()
	{
		return(System.currentTimeMillis()-start>duration);
	}
	
	private long duration;
	private long start;
}
