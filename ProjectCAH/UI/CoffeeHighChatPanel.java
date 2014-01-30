import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class CoffeeHighChatPanel extends JPanel implements ActionListener
{
	//private Container container;
	private JTextField textbox;
	public JTextArea chat;
	
	
	public CoffeeHighChatPanel(int w)
	{
		setMinimumSize(new Dimension(w,0));
		textbox=new JTextField("");
		textbox.addActionListener(this);
		textbox.setFont(new Font("Courier New", Font.PLAIN, 12));
		chat=new JTextArea("");
		chat.setEditable(false);
		chat.setFont(new Font("Courier New", Font.PLAIN, 12));
		System.out.println("width: "+w);
		chat.setMinimumSize(new Dimension(w,0));
		textbox.setMinimumSize(new Dimension(w,0));
		constructChatPanel();
	}
	
	private void constructChatPanel()
	{
		GridBagLayout gbl=new GridBagLayout();
		setLayout(gbl);
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.insets=new Insets(1,1,1,1);
		gbc.fill=gbc.HORIZONTAL;
		
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.ipady=100;
		gbc.ipadx=(int) chat.getMinimumSize().width;
		add(new JScrollPane(chat),gbc);
		
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.ipady=0;
		add(textbox,gbc);
		
		setBackground(Color.BLACK);
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() instanceof JTextField)
		{
			String s=textbox.getText();
			FontMetrics fm=chat.getFontMetrics(chat.getFont());
			int lineLength=(int) Math.floor(chat.getSize().width/fm.stringWidth("a"))-4;
			while(s.length()>lineLength)
			{
				int space=s.substring(0, lineLength+1).lastIndexOf(" ");
				if(space==-1)
					space=lineLength;
				chat.append(s.substring(0,space).trim());
				s=s.replace(s.substring(0, space), "");
				s=s.trim();
				if(s.length()>0)
					chat.append("\n   ");
			}
			chat.append(s.trim()+"\n");
			chat.setCaretPosition(chat.getText().length());
			textbox.setText("");
		}
		
		/*if(ae.getSource() instanceof JTextField)
		{
			String s=textbox.getText();
			FontMetrics fm=chat.getFontMetrics(chat.getFont());
			int lineLength=(int) Math.floor(chat.getSize().width/fm.stringWidth("a"))-4;
			//System.out.println("Line length: "+lineLength);
			do
			{
				System.out.println("begin s= "+s);
				//System.out.println("s="+s);
				int space=lineLength-s.substring(0,Math.min(s.length(),lineLength)).lastIndexOf(" ");
				if(space>lineLength)
					space=0;
				//System.out.println("space: "+space);
				//System.out.println("append: "+s.substring(0,Math.min(s.length(),lineLength-space)));
				chat.append(s.substring(0,Math.min(s.length(),lineLength-space)).trim()+"\n");
				
				
				//s=s.substring(Math.min(s.length(),lineLength-space));
				s=s.replace(s.substring(0,Math.min(s.length(),lineLength-space)), "");
				
				//System.out.println("s.length: "+s.length());
				if(s.length()>0)
					chat.append("   ");
				System.out.println("end s= "+s);
			}while(s.length()>lineLength);
			chat.append(s.trim()+"\n");
			//chat.append(textbox.getText()+"\n");
			chat.setCaretPosition(chat.getText().length());
			textbox.setText("");
		}*/
		
	}
}
