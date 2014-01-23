package denaro.nick.wars;

import java.awt.Point;

public interface UnitListener
{
	public void unitDestroyed(Unit unit);
	
	public void unitCreated(Unit unit, Point location);
	
	public void unitAttacked(Unit unit, int damage);
}
