package denaro.nick.wars.listener;

import java.awt.Point;

import denaro.nick.wars.Unit;

public interface UnitListener
{
	public void unitDestroyed(Unit unit);
	
	public void unitKilled(Unit unit);
	
	public void unitCreated(Unit unit, Point location);
	
	public void unitAttacked(Unit unit, int damage);
	
	public void unitSpawned(Unit unit);
}
