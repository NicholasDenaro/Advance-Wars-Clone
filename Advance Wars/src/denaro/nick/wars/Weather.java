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
	public static Weather rain=new Weather("Rain",true,1);
	public static Weather snow=new Weather("Snow",true,2);
}
