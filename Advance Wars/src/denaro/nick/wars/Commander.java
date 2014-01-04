package denaro.nick.wars;

public class Commander
{
	public Commander(String name)
	{
		this.name=name;
		attackPower=100;
		defencePower=100;
	}
	
	public int attackPower()
	{
		return(attackPower);
	}
	
	public int defencePower()
	{
		return(defencePower);
	}
	
	private String name;
	private int attackPower;
	private int defencePower;
}
