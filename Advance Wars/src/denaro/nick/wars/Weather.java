package denaro.nick.wars;

import denaro.nick.core.Identifiable;

public class Weather extends Identifiable
{
	private Weather(String name, boolean fog, int visionLoss)
	{
		this.name=name;
		this.fog=fog;
		this.visionLoss=visionLoss;
	}
	
	public String name()
	{
		return(name);
	}
	
	public boolean fog()
	{
		return(fog);
	}
	
	public int visionLoss()
	{
		return(visionLoss);
	}
	
	private String name;
	private boolean fog;
	private int visionLoss;
	
	public static Weather clear=new Weather("Clear",false,0);
	public static Weather rainy=new Weather("Rainy",true,1);
	public static Weather snowy=new Weather("Snowy",true,0);
}
