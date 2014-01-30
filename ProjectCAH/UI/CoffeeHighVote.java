import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CoffeeHighVote
{
	private String name;
	private ArrayList<CoffeeHighCardHolder> cards;
	
	public CoffeeHighVote(String s,CoffeeHighCardHolder[] c)
	{
		name=s;
		cards=new ArrayList<CoffeeHighCardHolder>(Arrays.asList(c));
	}
	
	public int getVoteLength()
	{
		return(cards.size());
	}
	
	public void setSelected(boolean s)
	{
		for(int i=0;i<cards.size();i+=1)
			cards.get(i).setSelected(s);
	}
	
	public void draw(Graphics g,int y)
	{
		int x=171;
		for(int i=0;i<cards.size();i+=1)
		{
			cards.get(i).draw(g,x,y);
			x+=140;
		}
	}
}
