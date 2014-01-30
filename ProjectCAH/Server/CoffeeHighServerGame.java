import java.net.Socket;
import java.util.HashMap;


public class CoffeeHighServerGame
{
	private String name;
	private HashMap<Integer,Socket> users;
	private boolean started;
	private int maxUsers;
	private String password;
	private int judge;
	private CoffeeHighServerDeck deck;
	
	public CoffeeHighServerGame(String n, int m, String p)
	{
		name=n;
		users=new HashMap<Integer,Socket>();
		started=false;
		maxUsers=m;
		password=p;
		judge=0;
		deck=new CoffeeHighServerDeck();
	}
	
	public boolean joinGame(String p)
	{
		if(users.size()<maxUsers)
			return(p.equals(password));
		return(false);
	}
	
	public String getPack(char c, int i)
	{
		return(deck.getPack(c, i));
	}
	
	public int getCard(char c,int i)
	{
		return(deck.getCard(c,i));
	}
	
	public void addPack(CoffeeHighPack pack)
	{
		deck.addPack(pack);
	}
	
	public int dealWhiteCard()
	{
		return(deck.dealWhiteCard());
	}
	
	public int dealBlackCard()
	{
		return(deck.dealBlackCard());
	}
}
