import java.io.File;
import java.net.URI;
import java.util.HashMap;


public class CoffeeHighCardList
{
	private HashMap<String,CoffeeHighPack> packs;
	
	public CoffeeHighCardList()
	{
		packs=new HashMap<String,CoffeeHighPack>();
	}
	
	public void loadPack(String d)
	{
		try
		{
			URI u=this.getClass().getResource(d+".txt").toURI();
			System.out.println("url: "+u);
			File f;
			if(u!=null)
			{
					f = new File(u.getPath());
					System.out.println("file: "+f.getPath());
					CoffeeHighPack pack=new CoffeeHighPack(d);
					pack.parsePack(f);
					packs.put(d, pack);
			}
		}
		catch(Exception ex)
		{
			System.out.println("ERROR FINDING DECK: "+ex);
		}
	}
	
	public CoffeeHighPack getPack(String s)
	{
		if(packs.containsKey(s))
			return(packs.get(s));
		return(null);
	}
}
