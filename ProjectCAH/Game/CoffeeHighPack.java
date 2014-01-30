import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;


public class CoffeeHighPack
{
	public String name;
	
	private HashMap<Integer,CoffeeHighCard> whiteCards;
	private HashMap<Integer,CoffeeHighCard> blackCards;
	private int cardCount;
	
	public CoffeeHighPack(String n)
	{
		name=n;
		whiteCards=new HashMap<Integer,CoffeeHighCard>();
		blackCards=new HashMap<Integer,CoffeeHighCard>();
		cardCount=0;
	}
	
	public int getWhiteCardCount()
	{
		return(whiteCards.size());
	}
	public int getBlackCardCount()
	{
		return(blackCards.size());
	}
	
	public CoffeeHighCard getWhiteCard(int i)
	{
		if(whiteCards.containsKey(i))
			return(whiteCards.get(i));
		return(null);
	}
	public CoffeeHighCard getBlackCard(int i)
	{
		if(blackCards.containsKey(i))
			return(blackCards.get(i));
		return(null);
	}
	
	public void parsePack(File f)
	{
		System.out.println("Parsing pack...");
		try
		{
			BufferedReader in=new BufferedReader(new FileReader(f));
			String s;
			String cardType="";
			while((s=in.readLine())!=null)
			{
				//System.out.println(s);
				if(s.charAt(0)=='[')
				{
					cardType=s.substring(s.indexOf("[")+1, s.indexOf("]"));
					//System.out.println("card type: "+cardType);
					cardCount=0;
				}
				while(s.contains("<"))
				{
					String text=s.substring(s.indexOf(">")+1, s.indexOf("<"));
					s=s.replace(">"+text+"<","");
					CoffeeHighCard card=new CoffeeHighCard(cardCount,text,"white".equals(cardType));
					if("white".equals(cardType))
					{
						whiteCards.put(cardCount, card);
						//System.out.println("New Card: "+cardType+" "+cardCount+" "+text);
					}
					else if("black".equals(cardType))
					{
						blackCards.put(cardCount, card);
						//System.out.println("New Card: "+cardType+" "+cardCount+" "+text);
					}
					cardCount+=1;
				}
			}
		}
		catch(Exception ex)
		{
			System.out.println("ERROR READING CARDS: "+ex);
		}
	}
}
