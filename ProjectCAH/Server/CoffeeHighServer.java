import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class CoffeeHighServer extends Thread
{
	private CoffeeHighServerFrame frame;
	private boolean running;
	private ServerSocket server;
	private HashMap<Socket, CoffeeHighServerClientThread> clients;
	private RandomAccessFile raf;
	
	public CoffeeHighCardList cardList;
	
	public CoffeeHighServer(CoffeeHighServerFrame f)
	{
		frame=f;
		cardList=new CoffeeHighCardList();
		cardList.loadPack("Original");
		
		running=false;
		try
		{
			server=new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress("",9400));
			frame.console.append("Server bound to: "+server.getInetAddress());
			clients=new HashMap<Socket, CoffeeHighServerClientThread>();
			
			File file=new File("Users.txt");
			if(!file.exists())
				System.out.println("New file created: "+file.createNewFile());
			raf=new RandomAccessFile(file,"rw");
		}
		catch(Exception ex)
		{
			frame.console.append("Error: "+ex);
		}
		start();
	}
	
	public void run()
	{
		running=true;
		while(running)
		{
			try
			{
				Socket s=server.accept();
				if(!clients.containsKey(s))
					clients.put(s, new CoffeeHighServerClientThread(frame,s));
			}
			catch(Exception ex)
			{
				frame.console.append("Error: "+ex);
			}
		}
	}
	
	public boolean loginUser(String user,String pass)
	{
		try
		{
			raf.seek(0);
			String s;
			while((s=raf.readLine()) != null)
			{
				if(user.equals(s.substring(0, s.indexOf(':'))))
				{
					if(pass.equals(s.substring(s.indexOf(':')+1)))
						return(true);
					else
						return(false);
				}
					
			}
			return(false);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: "+ex);
			return(false);
		}
	}
	public boolean registerUser(String user,String pass)
	{
		try
		{
			//System.out.println("Begin register");
			//System.out.println("raf="+raf);
			raf.seek(0);
			String s;
			while((s=raf.readLine()) != null)
			{
				if(user.equals(s.substring(0, s.indexOf(':'))))
					return(false);
			}
			//System.out.println("user not found");
			//raf.seek(raf.length());
			raf.writeBytes(user+":"+pass+"\n");
			//System.out.println("registered");
			return(true);
		}
		catch(Exception ex)
		{
			System.out.println("ERROR: "+ex);
			return(false);
		}
	}
	
	public void removeClient(Socket soc)
	{
		clients.remove(soc);
	}
}
