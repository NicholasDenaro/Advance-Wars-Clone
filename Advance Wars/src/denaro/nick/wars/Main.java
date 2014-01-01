package denaro.nick.wars;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameView2D;
import denaro.nick.core.Sprite;


public class Main
{
	public static void main(String[] args)
	{
		engine=(GameEngineByTick)GameEngineByTick.instance();
		engine.setTicksPerSecond(60);
		engine.setFramesPerSecond(60);
		WarView view=new WarView(240,160,1,1);
		engine.view(view);
		
		GameFrame frame=new GameFrame("Game",engine);
		frame.setVisible(true);
		
		createSprites();
		
		createUnits();
		
		createTerrain();
		
		createTeams();
		
		createMap();
		
		battle=new Battle(testMap, new Team[]{teamOrangeStar,teamBlueMoon},new Commander[]{null,null});
		
		menu=null;
		
		engine.start();
	}
	
	public static void createSprites()
	{
		BufferedImage image=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		g.setColor(Color.gray);
		g.drawString("I", 6, 12);
		new Sprite("Infantry",image,16,16,new Point(0,0));
		
		image=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		g=image.createGraphics();
		g.setColor(Color.gray);
		g.drawString("R", 3, 12);
		new Sprite("Recon",image,16,16,new Point(0,0));
		
		try
		{
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Path.png"));
			new Sprite("Path",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Arrow.png"));
			new Sprite("Arrow",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Action Menu.png"));
			new Sprite("Action Menu",image,80,16,new Point(0,0));
		}
		catch(IOException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	public static void createUnits()
	{
		infantry=new Unit(Sprite.sprite("Infantry"),new Point.Double(0,0),null);
		infantry.movement(4);
		infantry.vision(3);
		infantry.movementType(MovementType.FOOT);
		
		recon=new Unit(Sprite.sprite("Recon"),new Point.Double(0,0),null);
		recon.movement(6);
		recon.vision(5);
		recon.movementType(MovementType.WHEEL);
	}
	
	public static void createTerrain()
	{
		plain=new Terrain("Plain",new int[]{1,1,1,1,1,1,999,999,1});
		mountain=new Terrain("Mountain",new int[]{2,1,999,999,1,1,999,999,1});
		mountain.visionBoost(1);
	}
	
	public static void createTeams()
	{
		teamOrangeStar=new Team("Orange Star",Color.orange);
		teamBlueMoon=new Team("Blue Moon",Color.blue);
		
	}
	
	public static void createMap()
	{
		testMap=new Map("Test Room",12,12);
		testMap.weather(Weather.foggy);
		testMap.addUnit(Unit.copy(infantry,teamOrangeStar),3,4);
		testMap.addUnit(Unit.copy(infantry,teamOrangeStar),5,4);
		testMap.addUnit(Unit.copy(recon,teamBlueMoon),10,4);
		engine.location(testMap);
		for(int a=0;a<testMap.height();a++)
		{
			for(int i=0;i<testMap.width();i++)
			{
				testMap.setTerrain(plain,i,a);
				if(i==a)
					testMap.setTerrain(mountain,i,a);
			}
		}
		engine.addKeyListener(testMap);
		engine.requestFocus(testMap);
	}
	
	public static void closeMenu()
	{
		Menu child=Main.menu;
		while(child.child()!=null)
			child=child.child();
		
		if(child!=menu)
		{
			Menu parent=Main.menu;
			while(parent.child()!=child)
				parent=parent.child();
			parent.child(null);
		}
		else
		{
			menu=null;
			engine.requestFocus(testMap);
		}
	}
	
	public static GameEngineByTick engine()
	{
		return(engine);
	}
	
	public static Menu menu;
	
	public static Battle battle;
	
	private static GameEngineByTick engine;
	public static final int TILESIZE=16;
	
	public static Unit infantry;
	public static Unit recon;
	
	public static Terrain plain;
	public static Terrain mountain;
	
	public static Team teamOrangeStar;
	public static Team teamBlueMoon;
	
	public static Map testMap;
}
