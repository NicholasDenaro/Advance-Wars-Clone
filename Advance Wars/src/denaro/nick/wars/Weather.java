package denaro.nick.wars;

import denaro.nick.core.Identifiable;

public class Weather extends Identifiable
{
	private Weather(String name, boolean fog, int visionLoss)
	{
		this.fog=fog;
		this.visionLoss=visionLoss;
	}
	
	public boolean fog()
	{
		return(fog);
	}
	
	public int visionLoss()
	{
		return(visionLoss);
	}
	
	private boolean fog;
	private int visionLoss;
	
	public static Weather sunny=new Weather("Sunny",false,0);
	public static Weather foggy=new Weather("Foggy",true,0);
	public static Weather rainy=new Weather("Rainy",true,1);
	public static Weather snowy=new Weather("Snowy",true,0);
}
