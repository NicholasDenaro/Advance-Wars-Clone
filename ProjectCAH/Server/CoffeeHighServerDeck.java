import java.util.ArrayList;


public class CoffeeHighServerDeck
{
	private ArrayList<CoffeeHighPack> packs;
	private ArrayList<Integer> whiteCards, blackCards;
	
	public CoffeeHighServerDeck()
	{
		packs=new ArrayList<CoffeeHighPack>();
		whiteCards=new ArrayList<Integer>(0);
		blackCards=new ArrayList<Integer>(0);
	}
	
	public void addPack(CoffeeHighPack pack)
	{
		int nWhite=0;
		int nBlack=0;
		
		for(int i=0;i<packs.size();i+=1)
			nWhite+=packs.get(i).getWhiteCardCount();
		for(int i=0;i<packs.size();i+=1)
			nBlack+=packs.get(i).getBlackCardCount();
		
		for(int i=0;i<pack.getWhiteCardCount();i+=1)
			whiteCards.add(nWhite+i);
		for(int i=0;i<pack.getBlackCardCount();i+=1)
			blackCards.add(nBlack+i);
		
		packs.add(pack);
	}
	
	public String getPack(char c,int index)
	{
		String s=null;
		
		if(c=='w')
		{
			int a=1;
			int i=packs.get(0).getWhiteCardCount();
			while(index>i)
			{
				i+=packs.get(a).getWhiteCardCount();
				a+=1;
			}
			return(packs.get(a).name);
		}
		if(c=='b')
		{
			int a=1;
			int i=packs.get(0).getBlackCardCount();
			while(index>i)
			{
				i+=packs.get(a).getBlackCardCount();
				a+=1;
			}
			return(packs.get(a).name);
		}
		return(s);
	}
	
	public int getCard(char c,int index)
	{
		String s=null;
		
		if(c=='w')
		{
			int a=1;
			int i=packs.get(0).getWhiteCardCount();
			while(index>i)
			{
				i+=packs.get(a).getWhiteCardCount();
				a+=1;
			}
			return(i-index);
		}
		if(c=='b')
		{
			int a=1;
			int i=packs.get(0).getBlackCardCount();
			while(index>i)
			{
				i+=packs.get(a).getBlackCardCount();
				a+=1;
			}
			return(i-index);
		}
		return(-1);
	}
	
	public int dealWhiteCard()
	{
		int r=(int)(Math.random()*whiteCards.size());
		whiteCards.remove(r);
		return(r);
	}
	
	public int dealBlackCard()
	{
		int r=(int)(Math.random()*blackCards.size());
		blackCards.remove(r);
		return(r);
	}
}
