import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class CoffeeHighClientThread extends Thread
{
	private boolean running;
	private Socket socket;
	private CoffeeHighFrame game;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private CoffeeHighCardList cardList;
	
	public CoffeeHighClientThread()
	{
		cardList=new CoffeeHighCardList();
		running=false;
		socket=new Socket();
		try
		{
			socket.connect(new InetSocketAddress("",9400));
			out=new ObjectOutputStream(socket.getOutputStream());
			out.writeChar('c');
			out.flush();
			in=new ObjectInputStream(socket.getInputStream());
			game=null;
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: "+ex);
		}
		start();
	}
	
	public CoffeeHighCardList getCardList()
	{
		return(cardList);
	}
	
	public void sendMessage(char c, Object[] list)
	{
		try
		{
			out.writeChar(c);
			for(Object o:list)
				out.writeObject(o);
			out.flush();
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: "+ex);
		}
	}
	
	public void run()
	{
		running=true;
		
		while(running)
		{
			try
			{
				char c=in.readChar();
				if(c=='c')
				{
					System.out.println("Connected to the server!");
				}
				if(c=='l')
				{
					boolean login=in.readBoolean();
					if(login)
					{
						//Do shit to go to server list
						System.out.println("Successfully logged in.");
					}
					else
					{
						System.out.println("Login failed.");
					}
				}
				if(c=='r')
				{
					boolean register=in.readBoolean();
					if(register)
					{
						//Do shit to go to server list
						System.out.println("Successfully registered.");
					}
					else
					{
						System.out.println("Registration failed.");
					}
				}
				if(c=='g')
				{
					game=new CoffeeHighFrame(this);
				}
				if(c=='d')
				{
					char t=in.readChar();
					if(t=='w')
						game.addWhiteCard((String)in.readObject(),in.readInt());
					if(t=='b')
						game.addBlackCard((String)in.readObject(),in.readInt());
				}
			}
			catch(Exception ex)
			{
				System.out.println("ERROR: "+ex);
				running=false;
			}
		}
	}
}
