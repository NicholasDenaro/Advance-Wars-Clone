import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class CoffeeHighClientLoginFrame extends JFrame implements ActionListener
{
	private JTextField username;
	private JPasswordField password;
	private JButton loginButton;
	private JButton registerButton;
	private CoffeeHighClientThread client;
	
	public static void main(String[] args)
	{
		new CoffeeHighClientLoginFrame();
	}
	
	public CoffeeHighClientLoginFrame()
	{
		super("Login");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createGui();
		setVisible(true);
		pack();
		setSize(getWidth()+20,getHeight()+20);
		
		client=new CoffeeHighClientThread();
		System.out.println("Client~: "+client);
	}

	public void createGui()
	{
		GridBagConstraints gbc=new GridBagConstraints();
		Container c=getContentPane();
		c.setLayout(new GridBagLayout());
		gbc.insets=new Insets(2,10,2,10);
		gbc.fill=gbc.HORIZONTAL;
		
		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth=1;
		gbc.ipadx=100;
		c.add(new JLabel("Username:"),gbc);
		
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=2;
		username=new JTextField();
		c.add(username,gbc);
		
		gbc.gridx=0;
		gbc.gridy=3;
		gbc.gridwidth=1;
		c.add(new JLabel("Password:"),gbc);
		
		gbc.gridx=0;
		gbc.gridy=4;
		gbc.gridwidth=2;
		gbc.ipadx=100;
		password=new JPasswordField();
		c.add(password,gbc);
		
		gbc.gridx=2;
		gbc.gridy=0;
		gbc.gridwidth=1;
		gbc.gridheight=2;
		registerButton=new JButton("Register");
		registerButton.addActionListener(this);
		gbc.ipadx=0;
		c.add(registerButton,gbc);
		
		gbc.gridx=2;
		gbc.gridy=3;
		gbc.gridwidth=1;
		gbc.gridheight=2;
		loginButton=new JButton("Login");
		loginButton.addActionListener(this);
		gbc.ipadx=0;
		c.add(loginButton,gbc);
	}
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() instanceof JButton)
		{
			JButton b=(JButton)ae.getSource();
			if(b.getText().equals("Login"))
			{
				System.out.println("Loging in...");
				System.out.println("client: "+client);
				client.sendMessage('l',new Object[]{username.getText(),password.getPassword()});
			}
			if(b.getText().equals("Register"))
			{
				System.out.println("Registering...");
				System.out.println("client: "+client);
				client.sendMessage('r',new Object[]{username.getText(),password.getPassword()});
			}
		}
	}
}