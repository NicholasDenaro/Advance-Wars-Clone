import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.Style;


public class CoffeeHighCardHolder
{
	private CoffeeHighCard card;
	private ArrayList<String> text;
	private boolean selected;
	private Font font;
	private boolean visible;
	
	public CoffeeHighCardHolder(CoffeeHighCard c)
	{
		//System.out.println("Card: "+c.text);
		text=new ArrayList<String>(0);
		font=new Font("Courier New",Font.BOLD,12);
		selected=false;
		card=c;
		visible=true;
	}
	public void draw(Graphics g, int x, int y)
	{
		//System.out.println("painted CardHolder");
		//super.paintComponent(g);
		if(selected)
			g.setColor(Color.RED);
		else
			g.setColor(Color.WHITE);
		if(!card.color)
			g.setColor(Color.BLACK);
		g.fillRoundRect(x, y, 150, 150, 20, 20);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, 150, 150, 20, 20);
		
		g.setFont(font);
		FontMetrics fm=g.getFontMetrics();
		if(!visible)
		{
			g.drawString("Back of card", x+75-fm.stringWidth("Back of card")/2, y+80);
			return;
		}
			
		if(text.size()==0)
			createText(fm);
		g.setColor(Color.BLACK);
		if(!card.color)
			g.setColor(Color.WHITE);
		for(int i=0;i<text.size();i+=1)
			g.drawString(text.get(i), x+10, y+15+i*12);
		
		g.drawString(""+card.index, x+140-fm.stringWidth(""+card.index), y+144);
	}
	
	public CoffeeHighCard getCard()
	{
		return(card);
	}
	
	public void setSelected(boolean s)
	{
		selected=s;
	}
	
	public void setVisible(boolean v)
	{
		visible=v;
	}
	
	public ArrayList<String> getText()
	{
		return(text);
	}
	
	private void createText(FontMetrics fm)
	{
		//FontMetrics fm=this.getFontMetrics(font);
		int lineLength=(int) Math.floor(130/fm.stringWidth("a"));
		//System.out.println("line length: "+lineLength);
		String s=card.text;
		while(s.length()>lineLength)
		{
			int space=s.substring(0, lineLength+1).lastIndexOf(" ");
			if(space==-1)
				space=lineLength;
			text.add(s.substring(0,space).trim());
			s=s.replace(s.substring(0, space), "");
			s=s.trim();
			//System.out.println("s= "+s);
		}
		text.add(s.trim());
	}
}
