package denaro.nick.wars;

public class UnitFinalizedException extends Exception
{
	public UnitFinalizedException(Unit unit)
	{
		this.unit=unit;
	}
	
	public Unit unit()
	{
		return(unit);
	}
	
	private Unit unit;
}
