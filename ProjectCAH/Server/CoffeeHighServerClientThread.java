import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


public class CoffeeHighServerClientThread extends Thread
{
	private Socket socket;
	private boolean running;
	private CoffeeHighServerFrame frame;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private CoffeeHighServerGame game;
	
	public CoffeeHighServerClientThread(CoffeeHighServerFrame f,Socket s)
	{
		frame=f;
		socket=s;
		game=null;
		running=false;
		try
		{
			out=new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in=new ObjectInputStream(socket.getInputStream());
		}
		catch(Exception ex)
		{
			frame.console.append("ERROR: "+ex);
		}
		start();
	}
	
	public void setGame(CoffeeHighServerGame g)
	{
		game=g;
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
					frame.console.append("connected!");
					out.writeChar('c');
					out.flush();
				}
				if(c=='l')
				{
					frame.console.append("Login Attempt.");
					String user=(String)in.readObject();
					char[] p=(char[])in.readObject();
					String pass="";
					for(char a:p)
						pass+=a;
					frame.console.append("\tuser: "+user);
					frame.console.append("\tpass: "+pass);
					boolean login=frame.loginUser(user, pass);
					frame.console.append("\nLogin "+login);
					
					out.writeChar('l');
					out.writeBoolean(login);
					out.flush();
				}
				if(c=='r')
				{
					frame.console.append("register Attempt.");
					String user=(String)in.readObject();
					char[] p=(char[])in.readObject();
					String pass="";
					for(char a:p)
						pass+=a;
					frame.console.append("\tuser: "+user);
					frame.console.append("\tpass: "+pass);
					boolean register=frame.registerUser(user, pass);
					frame.console.append("\nRegister "+register);
					
					out.writeChar('r');
					out.writeBoolean(register);
					out.flush();
				}
				if(c=='d')
				{
					char t=in.readChar();
					if(t=='w')
					{
						out.writeChar('d');
						out.writeChar('w');
						int card=game.dealWhiteCard();
						String p=game.getPack('w',card);
						out.writeObject(p);
						out.writeInt(game.getCard('w',card));
						out.flush();
					}
					if(t=='b')
					{
						out.writeChar('d');
						out.writeChar('b');
						out.writeInt(game.dealBlackCard());
						out.flush();
					}
				}
			}
			catch(Exception ex)
			{
				frame.console.append("ERROR: "+ex);
				running=false;
				frame.removeClient(socket);
			}
		}
	}
}
