import java.awt.Color;
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
import java.util.HashMap;

import javax.swing.JPanel;


public class CoffeeHighVotePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
	private CoffeeHighHandPanel handPanel;
	private ArrayList<CoffeeHighVote> votes;
	private Font font;
	private String voter;
	private boolean button;
	private CoffeeHighCardHolder card;
	private int numCards;
	private HashMap<Integer,CoffeeHighCardHolder> tempvote;
	private boolean voting;
	private int numVotes;
	private double scroll;
	private int yScroll;
	private boolean scrolling;
	private int selected;
	private boolean judge;
	
	public CoffeeHighVotePanel(CoffeeHighHandPanel h)
	{
		handPanel=h;
		votes=new ArrayList<CoffeeHighVote>(0);
		setMinimumSize(new Dimension(640,320));
		font=new Font("Courier New",Font.BOLD,12);
		voter="null";
		button=false;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		CoffeeHighCardList cardList=new CoffeeHighCardList();
		cardList.loadPack("Original");
		card=new CoffeeHighCardHolder(cardList.getPack("Original").getBlackCard(11));
		numCards=1;
		numVotes=157;
		tempvote=new HashMap<Integer,CoffeeHighCardHolder>(0);
		voting=false;
		judge=false;
		scroll=0;
		yScroll=-1;
		scrolling=false;
		selected=-1;
		addVote("Chris",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(347))
			});
		addVote("Jeff",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(123))
			});
		addVote("Will",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(263))
			});
		/*addVote("Chris",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(347)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(21)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(342))
				});
		addVote("Tylor",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(234)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(324)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(158))
				});
		addVote("Jimmy",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(512)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(234)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(12))
				});
		addVote("Eric",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(521)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(24)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(76))
				});*/
		/*addVote("Eric",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(521)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(24)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(76))
				});
		addVote("Eric",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(521)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(24)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(76))
				});
		addVote("Eric",new CoffeeHighCardHolder[]{
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(521)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(24)),
				new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(76))
				});*/
		//addCardToVote(cardList.getPack("Original").getWhiteCard(5));
		//addCardToVote(cardList.getPack("Original").getWhiteCard(10));
	}
	
	public void addVote(String s, CoffeeHighCardHolder[] c)
	{
		if(votes.size()<numVotes)
		{
			votes.add(new CoffeeHighVote(s,c));
			//System.out.println("Vote Added...");
		}
		
	}
	public boolean addCardToVote(CoffeeHighCardHolder ch)
	{
		if(tempvote.size()<numCards)
		{
			for(int i=0;i<numCards;i+=1)
			{
				if(!tempvote.containsKey(i))
				{
					tempvote.put(i, ch);
					return(true);
				}
				
			}
			
		}
		return(false);
	}
	public void removeCardFromVote(int i)
	{
		tempvote.remove(i);
	}
	public void setNumCards(int i)
	{
		numCards=i;
	}
	public boolean getVoting()
	{
		return(voting);
	}
	public boolean getJudge()
	{
		return(judge);
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setFont(font);
		FontMetrics fm=g.getFontMetrics(font);
		
		card.draw(g, 0, 15);
		
		if(voting)
		{
			int length=votes.size()*151;
			if(length>305)
			{
				g.setColor(Color.BLUE);
				//System.out.println("scroll length: "+((length-(length-305.0))/length*305));
				g.fillRect(153, (int) (15+scroll*305), 16, (int)Math.max(10,(length-(length-305.0))/length*305));
			}
			
			
			for(int i=0;i<votes.size();i+=1)
			{
				votes.get(i).draw(g,(int) (15-scroll*length+i*151));
			}
			if(judge)
			{
				g.setColor(Color.RED);
				if(selected==-1)
					g.setColor(Color.GRAY);

				g.fillRect(0, 151+15, 151, 30);
				g.setColor(Color.BLACK);
				
				if(selected==-1)
					g.drawString("Select a card", 151/2-fm.stringWidth("Select a card")/2, 15+151+15);
				else
					g.drawString("Vote", 151/2-fm.stringWidth("Vote")/2, 15+151+15);
			}
		}
		else
		{
			if(!judge)
			{
				g.setColor(Color.GRAY);
				if(tempvote.size()==numCards)
					g.setColor(Color.BLUE);
				g.fillRoundRect(0, 300, 80, 20, 10, 10);
				
				g.setColor(Color.BLACK);
				if(tempvote.size()==numCards)
					g.drawString("Vote!", 40-fm.stringWidth("Vote!")/2, 315);
				else
					g.drawString("Queue", 40-fm.stringWidth("Queue")/2, 315);
				
				for(int i=0;i<numCards;i+=1)
				{
					if(tempvote.containsKey(i))
						tempvote.get(i).draw(g, 80+i*151, 320-151);
					else
						g.drawRoundRect(80+i*151, 320-151, 150, 150, 20, 20);
				}
			}
			else
			{
				Font f=new Font("Courier New",Font.BOLD,50);
				g.setFont(f);
				FontMetrics fm1=g.getFontMetrics();
				g.setColor(Color.BLACK);
				g.drawString("Please wait.", (151+640)/2-fm1.stringWidth("Please wait.")/2, 151+15);
			}
		}
		g.setFont(font);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 650, 13);
		g.setColor(Color.BLACK);
		if(!judge)
			g.drawString("Voter: "+voter, 320-fm.stringWidth("Voter: "+voter)/2, 10);
		else
			g.drawString("You are voting.", 320-fm.stringWidth("You are voting.")/2, 10);
	}

	@Override
	public void mouseClicked(MouseEvent me)
	{
		// TODO Auto-generated method stub
		/*System.out.println("button: "+me.getButton());
		System.out.println("modifiers: "+me.getModifiers());
		System.out.println("click count: "+me.getClickCount());*/
		if(!voting)
		{
			if(me.getButton()==me.BUTTON1)
			{
				if(me.getClickCount()==1)
				{
					if((me.getX()>0)&&(me.getX()<80)&&(me.getY()>300))
					{
						if(tempvote.size()<numCards)
						{
							CoffeeHighCardHolder card=handPanel.getSelectedCard();
							handPanel.removeCard(card);
							card.setSelected(false);
							this.addCardToVote(card);
							repaint();
						}
						
						
					}
				}
				else if(me.getClickCount()==2)
				{
					System.out.println("double click");
					if((me.getX()>80)&&(me.getX()<80+numCards*151)&&(me.getY()>150))
					{
						int index=(me.getX()-80)/151;
						if(tempvote.containsKey(index))
						{
							handPanel.addCard(tempvote.get(index));
							tempvote.remove(index);
							repaint();
						}
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent me)
	{
		// TODO Auto-generated method stub
		if((me.getX()<80)&&(me.getY()>300))
			button=true;
		if((me.getX()>152)&&(me.getX()<152+16)&&(me.getY()>15))
		{
			yScroll=me.getY();
			scrolling=true;
		}
		if(judge)
		{
			if(me.getButton()==me.BUTTON1)
			{
				if((me.getY()>15)&&(me.getX()>171)&&(true)&&me.getX()<171+151+(votes.get(0).getVoteLength()-1)*141)
				{
					int length=votes.size()*151;
					int index=(int) (me.getY()-15+scroll*length)/151;
					if(selected!=-1)
						votes.get(selected).setSelected(false);
					if(index<votes.size())
					{
						votes.get(index).setSelected(true);
						selected=index;
					}
						
					repaint();
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent me)
	{
		// TODO Auto-generated method stub
		if(button)
		{
			//send vote
		}
		button=false;
		yScroll=-1;
		scrolling=false;
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
		if(yScroll!=-1)
		{
			if(scrolling)
			{
				int length=votes.size()*151;
				//System.out.println("me.getY()="+me.getY());
				scroll+=(me.getY()-yScroll)/(302*1.0);
				//System.out.println("scroll= "+scroll);
				yScroll=me.getY();
				
				if(scroll>(length-305.0)/length)
					scroll=(length-305.0)/length;
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
		int length=votes.size()*151;
		scroll+=(me.getWheelRotation()*8)/(302*1.0);

		if(scroll>(length-305.0)/length)
			scroll=(length-305.0)/length;
		if(scroll<0)
			scroll=0;
		repaint();
		
	}
}
