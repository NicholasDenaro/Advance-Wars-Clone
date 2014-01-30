import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JTextField;


public class CoffeeHighFrame extends JFrame
{
	private Container container;
	private CoffeeHighVotePanel votePanel;
	private CoffeeHighChatPanel chatPanel;
	private CoffeeHighHandPanel handPanel;
	private CoffeeHighClientThread socket;
	
	/*public static void main(String[] args)
	{
		new CoffeeHighFrame();
	}*/
	
	public CoffeeHighFrame(CoffeeHighClientThread thread)
	{
		super("Game");
		socket=thread;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		container=getContentPane();
		setVisible(true);
		createGui();
		//setBounds(0,0,640,480);
		//pack();
		revalidate();
		/*CoffeeHighCardList cardList=new CoffeeHighCardList();
		cardList.loadPack("Original");*/
		//handPanel.addCard(cardList.getPack("Original").getWhiteCard(1));
		//container.add(new CoffeeHighCardHolder(cardList.getPack("Original").getWhiteCard(3)));
		pack();
	}
	
	public void addWhiteCard(String p,int i)
	{
		handPanel.addCard(socket.getCardList().getPack(p).getWhiteCard(i));
	}
	public void addBlackCard(String p,int i)
	{
		handPanel.addCard(socket.getCardList().getPack(p).getBlackCard(i));
	}
	
	public void createGui()
	{
		GridBagConstraints gbc=new GridBagConstraints();
		container.setLayout(new GridBagLayout());
		chatPanel=new CoffeeHighChatPanel(640);
		handPanel=new CoffeeHighHandPanel();
		votePanel=new CoffeeHighVotePanel(handPanel);
		handPanel.setVotePanel(votePanel);
		
		gbc.fill=gbc.HORIZONTAL;
		
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.ipady=310;
		container.add(votePanel,gbc);
		
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.ipady=155;
		container.add(handPanel,gbc);
		
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.ipady=0;
		container.add(chatPanel,gbc);
	}
}
