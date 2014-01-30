import java.awt.Container;
import java.awt.Dimension;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class CoffeeHighServerFrame extends JFrame
{
	public JTextArea console;
	private CoffeeHighServer server;
	
	public static void main(String[] args)
	{
		new CoffeeHighServerFrame();
	}
	
	public CoffeeHighServerFrame()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		console=new JTextArea()
		{
			public void append(String s)
			{
				super.append(s+"\n");
				this.setCaretPosition(this.getText().length());
			}
		};
		console.setEditable(false);
		console.setMinimumSize(new Dimension(350,150));
		console.append("Welcome to the server.");
		server=new CoffeeHighServer(this);
		Container c=getContentPane();
		c.setPreferredSize(new Dimension(350,150));
		c.add(new JScrollPane(console));
		setVisible(true);
		pack();
	}
	
	public boolean registerUser(String user, String pass)
	{
		return(server.registerUser(user, pass));
	}
	
	public boolean loginUser(String user, String pass)
	{
		return(server.loginUser(user, pass));
	}
	
	public void removeClient(Socket soc)
	{
		server.removeClient(soc);
	}
}
