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
		
		battle=new Battle(testMap, new Team[]{teamOrangeStar,teamBlueMoon,teamGreenEarth});
		
		menu=null;
		
		engine.start();
	}
	
	public static void createSprites()
	{
		BufferedImage image=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		
		try
		{
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Path.png"));
			new Sprite("Path",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Arrow.png"));
			new Sprite("Arrow",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Action Menu.png"));
			new Sprite("Action Menu",image,80,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Terrain.png"));
			new Sprite("Terrain",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Buildings.png"));
			new Sprite("Buildings",image,16,24,new Point(0,8));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Units.png"));
			new Sprite("Units",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Cursor.png"));
			new Sprite("Cursor",image,-1,-1,new Point(2,2));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Color Palette.png"));
			new Sprite("Color Palette",image,-1,-1,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Font.png"));
			new GameFont("Map Font",image,8,10);
		}
		catch(IOException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	public static void createUnits()
	{
		try
		{
			infantry=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),null);
			infantry.canCapture(true);
			infantry.imageIndex(0);
			infantry.movement(4);
			infantry.vision(3);
			infantry.movementType(MovementType.FOOT);
			infantry.finalize();
			
			recon=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),null);
			recon.imageIndex(8);
			recon.movement(6);
			recon.vision(5);
			recon.movementType(MovementType.TIRES);
			recon.finalize();
			
			artillery=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),null);
			artillery.imageIndex(24);
			artillery.movement(4);
			artillery.vision(3);
			artillery.movementType(MovementType.TIRES);
			artillery.attackRange(new Point(2,3));
			artillery.finalize();
		}
		catch(UnitFinalizedException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void createTerrain()
	{
		plain=new Terrain("Plain",new int[]{1,1,1,1,999,999,1});
		plain.defence(1);
		plain.imageIndex(9);
		
		mountain=new Terrain("Mtn",new int[]{2,1,999,999,999,999,1});
		mountain.defence(4);
		mountain.visionBoost(1);
		mountain.imageIndex(19);
		
		forest=new Terrain("Wood",new int[]{1,1,2,3,999,999,1});
		forest.defence(3);
		forest.hiding(true);
		forest.imageIndex(21);
		
		city=new Building("City",null,new int[]{1,1,1,1,999,999,1});
		city.defence(4);
		city.imageIndex(0);
	}
	
	public static void createTeams()
	{
		colorPalette=new TeamColorPalette((BufferedImage)Sprite.sprite("Color Palette").subimage(0));
		teamOrangeStar=new Team("Orange Star",1,new Commander("None"));
		teamBlueMoon=new Team("Blue Moon",2,new Commander("None"));
		teamGreenEarth=new Team("Green Earth",3,new Commander("None"));
	}
	
	public static void createMap()
	{
		testMap=new Map("Test Room",12,12);
		testMap.weather(Weather.foggy);
		testMap.addUnit(Unit.copy(infantry,teamOrangeStar),3,4);
		Unit damagedUnit=Unit.copy(infantry,teamOrangeStar);
		damagedUnit.health(67);
		testMap.addUnit(damagedUnit,5,4);
		testMap.addUnit(Unit.copy(artillery,teamOrangeStar),3,8);
		testMap.addUnit(Unit.copy(recon,teamBlueMoon),10,4);
		testMap.addUnit(Unit.copy(infantry,teamBlueMoon), 4, 5);
		testMap.addUnit(Unit.copy(infantry,teamGreenEarth), 4, 6);
		engine.location(testMap);
		for(int a=0;a<testMap.height();a++)
		{
			for(int i=0;i<testMap.width();i++)
			{
				testMap.setTerrain(plain,i,a);
				if(i==a)
					testMap.setTerrain(mountain,i,a);
				if(i==a+1)
					testMap.setTerrain(forest,i,a);
			}
		}
		testMap.setTerrain(Building.copy(city, teamOrangeStar), 3, 2);
		testMap.setTerrain(Building.copy(city, null), 4, 2);
		engine.addKeyListener(testMap);
		engine.requestFocus(testMap);
	}
	
	public static void openMenu(Menu menu)
	{
		Menu child=Main.menu;
		while(child.child()!=null)
			child=child.child();
		
		child.child(menu);
		engine.requestFocus(menu);
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
			engine.requestFocus(parent);
		}
		else
		{
			menu=null;
			engine.requestFocus(testMap);
		}
	}
	
	public static void swapPalette(BufferedImage image, Team team, int base)
	{
		Graphics2D g=image.createGraphics();
		for(int a=0;a<image.getHeight();a++)
		{
			for(int i=0;i<image.getWidth();i++)
			{
				g.setColor(colorPalette.swapColor(new Color(image.getRGB(i,a),true), team.color(),base));
				g.fillRect(i, a, 1, 1);
			}
		}
	}
	
	public static GameEngineByTick engine()
	{
		return(engine);
	}
	
	public static Menu menu;
	
	public static Battle battle;
	
	public static TeamColorPalette colorPalette;
	
	private static GameEngineByTick engine;
	public static final int TILESIZE=16;
	
	public static Unit infantry;
	public static Unit recon;
	public static Unit artillery;
	
	public static Terrain plain;
	public static Terrain mountain;
	public static Terrain forest;
	
	public static Building city;
	
	public static Team teamOrangeStar;
	public static Team teamBlueMoon;
	public static Team teamGreenEarth;
	
	public static Map testMap;
}
