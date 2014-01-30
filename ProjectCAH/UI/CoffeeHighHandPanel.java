import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class CoffeeHighHandPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
	private ArrayList<CoffeeHighCardHolder> cards;
	private int selected;
	private double scroll;
	private int xScroll;
	private boolean scrolling;
	private CoffeeHighVotePanel votePanel;
	
	public CoffeeHighHandPanel()
	{
		Random r=new Random();
		cards=new ArrayList<CoffeeHighCardHolder>(0);
		setMinimumSize(new Dimension(1000,151));
		CoffeeHighCardList cardList=new CoffeeHighCardList();
		/*cardList.loadPack("Original");
		for(int i=0;i<7;i+=1)
			addCard(cardList.getPack("Original").getWhiteCard((int) (r.nextDouble()*500)));*/
		selected=-1;
		scroll=0;
		scrolling=false;
		xScroll=-1;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		votePanel=null;
	}
	
	public void setVotePanel(CoffeeHighVotePanel v)
	{
		votePanel=v;
	}
	public void addCard(CoffeeHighCard card)
	{
		CoffeeHighCardHolder holder=new CoffeeHighCardHolder(card);
		cards.add(holder);
		repaint();
	}
	public void addCard(CoffeeHighCardHolder card)
	{
		//System.out.println("Card added.");
		//CoffeeHighCardHolder holder=new CoffeeHighCardHolder(card);
		card.setSelected(false);
		cards.add(card);
		int length=cards.size()*151;
		if(scroll>(length-640.0)/length)
			scroll=(length-640.0)/length;
		if(scroll<0)
			scroll=0;
		repaint();
	}
	public void removeCard(CoffeeHighCardHolder card)
	{
		card.setSelected(false);
		cards.remove(card);
		int length=cards.size()*151;
		if(scroll>(length-640.0)/length)
			scroll=(length-640.0)/length;
		if(scroll<0)
			scroll=0;
		repaint();
	}
	
	public CoffeeHighCardHolder getSelectedCard()
	{
		return(cards.get(selected));
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int length=cards.size()*151;
		for(int i=0;i<cards.size();i+=1)
		{
			cards.get(i).draw(g,(int) (i*151-scroll*length),0);
		}
		System.out.println("length: "+length);
		if(length>640)
		{
			g.setColor(Color.BLUE);
			g.fillRect((int) (scroll*640), 151, (int) Math.max(10,(length-(length-640.0))/length*640), 15);
		}
		if(votePanel.getJudge())
		{
			g.setColor(new Color(0,0,0,155));
			g.fillRect(0, 0, cards.size()*151, 200);
			g.setColor(Color.BLACK);
			Font font=new Font("Courier New",Font.BOLD,50);
			FontMetrics fm=g.getFontMetrics(font);
			g.setFont(font);
			g.drawString("You're Judging", 640/2+-fm.stringWidth("You're Judging")/2, 75+15);
		}
		else if(votePanel.getVoting())
		{
			g.setColor(new Color(0,0,0,155));
			g.fillRect(0, 0, cards.size()*151, 200);
			g.setColor(Color.BLACK);
			Font font=new Font("Courier New",Font.BOLD,30);
			FontMetrics fm=g.getFontMetrics(font);
			g.setFont(font);
			g.drawString("Submition phase is over.", 640/2+-fm.stringWidth("Submition phase is over.")/2, 75+15);
		}
	}

	@Override
	public void mouseClicked(MouseEvent me)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		if(votePanel.getVoting()||votePanel.getJudge())
			return;
		// TODO Auto-generated method stub
		if((me.getButton()==me.BUTTON1))
		{
			if(me.getClickCount()==1)
			{
				//System.out.println("y:"+me.getY());
				if(me.getY()<151)
				{
					int length=cards.size()*151;
					if(selected!=-1)
						cards.get(selected).setSelected(false);
					cards.get((int)((scroll*length)+me.getX())/151).setSelected(true);
					selected=(int)((scroll*length)+me.getX())/151;
					repaint();
				}
				else
				{
					scrolling=true;
					xScroll=me.getX();
				}
			}
			else if(me.getClickCount()==2)
			{
				if(votePanel.addCardToVote(cards.get(selected)))
				{
					removeCard(cards.get(selected));
					selected=-1;
					votePanel.repaint();
				}
					
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
		if(votePanel.getVoting()||votePanel.getJudge())
			return;
		// TODO Auto-generated method stub
		xScroll=-1;
	}

	@Override
	public void mouseEntered(MouseEvent me)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent me)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseDragged(MouseEvent me)
	{
		if(votePanel.getVoting()||votePanel.getJudge())
			return;
		//System.out.println("dragged"+me.getButton());
		//System.out.println("xScroll: "+xScroll);
		if(xScroll!=-1)
		{
			if(scrolling)
			{
				int length=cards.size()*151;
				scroll+=(me.getX()-xScroll)/(640*1.0);
				//System.out.println("scroll= "+scroll);
				xScroll=me.getX();
				
				if(scroll>(length-640.0)/length)
					scroll=(length-640.0)/length;
				if(scroll<0)
					scroll=0;
				repaint();
			}
			
		}
		
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent me)
	{
		if(votePanel.getVoting()||votePanel.getJudge())
			return;
		int length=cards.size()*151;
		scroll+=(me.getWheelRotation()*16)/(640*1.0);

		if(scroll>(length-640.0)/length)
			scroll=(length-640.0)/length;
		if(scroll<0)
			scroll=0;
		repaint();
		
	}

	
}
