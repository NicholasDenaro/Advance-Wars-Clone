package denaro.nick.wars;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameMap;
import denaro.nick.core.GameView2D;
import denaro.nick.core.Sprite;


public class Main
{
	public static void main(String[] args)
	{
		engine=(GameEngineByTick)GameEngineByTick.instance();
		engine.setTicksPerSecond(60);
		engine.setFramesPerSecond(60);
		BattleView view=new BattleView(240,160,1,1);
		engine.view(view);
		
		GameFrame frame=new GameFrame("Game",engine);
		frame.setVisible(true);
		
		createSprites();
		
		createUnits();
		
		createTerrain();
		
		createTeams();
		
		createWeather();
		
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
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Buy Menu.png"));
			new Sprite("Buy Menu",image,-1,-1,new Point(0,0));
			
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
		unitMap=new GameMap();
		
		try
		{
			infantry=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),0);
			infantry.weapon1(new UnitWeapon(0,0,1,2,3,4,5,6));
			infantry.weapon2(null);
			infantry.canCapture(true);
			infantry.imageIndex(0);
			infantry.movement(3);
			infantry.vision(3);
			infantry.movementType(MovementType.FOOT);
			infantry.finalize();
			unitMap.add(infantry);
			
			mech=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),1);
			mech.weapon1(new UnitWeapon(1,0,1));
			mech.weapon2(new UnitWeapon(2,2,3,4,5,6));
			mech.canCapture(true);
			mech.imageIndex(4);
			mech.movement(2);
			mech.vision(3);
			mech.ammo(9);
			mech.movementType(MovementType.FOOT);
			mech.finalize();
			unitMap.add(mech);
			
			recon=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),2);
			recon.weapon1(new UnitWeapon(7,0,1,2,3,4,5,6));
			recon.weapon2(null);
			recon.imageIndex(8);
			recon.movement(8);
			recon.vision(5);
			recon.movementType(MovementType.TIRES);
			recon.finalize();
			unitMap.add(recon);
			
			tank=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),3);
			tank.weapon1(new UnitWeapon(3,0,1));
			tank.weapon2(new UnitWeapon(4,2,3,4,5,6));
			tank.imageIndex(12);
			tank.movement(6);
			tank.vision(3);
			tank.ammo(9);
			tank.movementType(MovementType.TREAD);
			tank.finalize();
			unitMap.add(tank);
			
			artillery=new Unit(Sprite.sprite("Units"),new Point.Double(0,0),6);
			artillery.weapon1(null);
			artillery.weapon2(new UnitWeapon(10,0,1,2,3,4,5,6));
			artillery.imageIndex(24);
			artillery.movement(5);
			artillery.vision(1);
			artillery.ammo(9);
			artillery.movementType(MovementType.TREAD);
			artillery.attackRange(new Point(2,3));
			artillery.finalize();
			unitMap.add(artillery);
		}
		catch(UnitFinalizedException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void createTerrain()
	{
		terrainMap=new GameMap();
		
		plain=new Terrain("Plain",new int[]{1,1,1,1,999,999,1});
		plain.defence(1);
		plain.imageIndex(9);
		terrainMap.add(plain);
		
		mountain=new Terrain("Mtn",new int[]{2,1,999,999,999,999,1});
		mountain.defence(4);
		mountain.visionBoost(1);
		mountain.imageIndex(19);
		terrainMap.add(mountain);
		
		forest=new Terrain("Wood",new int[]{1,1,2,3,999,999,1});
		forest.defence(3);
		forest.hiding(true);
		forest.imageIndex(21);
		terrainMap.add(forest);
		
		city=new Building("City",null,new int[]{1,1,1,1,999,999,1});
		city.defence(4);
		city.imageIndex(0);
		terrainMap.add(city);
		
		base=new Building("Base",null,new int[]{1,1,1,1,999,999,1});
		base.addSelling("Infantry", (Unit)unitMap.get(0), 1000);
		base.addSelling("Mech", (Unit)unitMap.get(1), 3000);
		base.addSelling("Recon", (Unit)unitMap.get(2), 4000);
		base.addSelling("Tank", (Unit)unitMap.get(3), 7000);
		base.addSelling("Artillery", (Unit)unitMap.get(4), 6000);
		base.defence(4);
		base.imageIndex(1);
		terrainMap.add(base);
	}
	
	public static void createTeams()
	{
		teamMap=new GameMap();
		
		colorPalette=new TeamColorPalette((BufferedImage)Sprite.sprite("Color Palette").subimage(0));
		teamOrangeStar=new Team("Orange Star",1,new Commander("None"));
		teamMap.add(teamOrangeStar);
		teamBlueMoon=new Team("Blue Moon",2,new Commander("None"));
		teamMap.add(teamBlueMoon);
		teamGreenEarth=new Team("Green Earth",3,new Commander("None"));
		teamMap.add(teamGreenEarth);
	}
	
	public static void createWeather()
	{
		weatherMap=new GameMap();
		
		weatherMap.add(Weather.sunny);
		weatherMap.add(Weather.foggy);
		weatherMap.add(Weather.rainy);
		weatherMap.add(Weather.snowy);
	}
	
	public static void createMap()
	{
		if(!new File("Test Map.mp").exists())
		{
			testMap=new Map("Test Room",8,8);
			testMap.weather(Weather.foggy);
			testMap.addUnit(Unit.copy(infantry,teamOrangeStar),3,4);
			Unit damagedUnit=Unit.copy(infantry,teamOrangeStar);
			damagedUnit.health(67);
			testMap.addUnit(damagedUnit,5,4);
			testMap.addUnit(Unit.copy(artillery,teamOrangeStar),3,7);
			testMap.addUnit(Unit.copy(recon,teamBlueMoon),1,4);
			testMap.addUnit(Unit.copy(infantry,teamBlueMoon), 4, 5);
			testMap.addUnit(Unit.copy(infantry,teamGreenEarth), 4, 6);
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
			testMap.setTerrain(Building.copy(city,teamOrangeStar),3,2);
			testMap.setTerrain(Building.copy(city,null),4,2);
			testMap.setTerrain(Building.copy(base,teamOrangeStar),5,2);
			testMap.setTerrain(Building.copy(base,teamBlueMoon),6,2);
			try
			{
				ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File("Test Map.mp")));
				writeMap(oos,testMap);
				oos.close();
				System.out.println("map saved?!");
			}
			catch(FileNotFoundException ex)
			{
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			catch(IOException ex)
			{
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		else
		{
			System.out.println("trying to read!");
			try
			{
				testMap=Main.readMap(new ObjectInputStream(new FileInputStream("Test Map.mp")));
			}
			catch(ClassNotFoundException ex)
			{
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			catch(FileNotFoundException ex)
			{
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			catch(IOException ex)
			{
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			System.out.println("Done reading!");
		}
		engine.location(testMap);
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
				Color color=new Color(image.getRGB(i,a),true);
				g.setColor(colorPalette.swapColor(color, team==null?0:team.color(),base));
				color=null;
				g.fillRect(i, a, 1, 1);
			}
		}
		g.dispose();
	}
	
	public static Map readMap(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		int id=in.readInt();
		Map map=new Map((String)in.readObject(),in.readInt(),in.readInt());
		map.weather((Weather)weatherMap.get(in.readInt()));
		
		
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				map.setTerrain(readTerrain(in),i,a);
			}
		}
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				map.addUnit(readUnit(in),i,a);
			}
		}
		in.close();
		return(map);
	}
	
	private static Terrain readTerrain(ObjectInputStream in) throws IOException
	{
		int id=in.readInt();
		if(terrainMap.get(id) instanceof Building)
		{
			int teamId=in.readInt();
			int health=in.readInt();
			Building building=Building.copy((Building)terrainMap.get(id), (Team)teamMap.get(teamId));
			building.health(health);
			return(building);
		}
		else
			return((Terrain)terrainMap.get(id));
	}
	
	public static Unit readUnit(ObjectInputStream in) throws IOException
	{
		int id=in.readInt();
		if(id==-1)
			return(null);
		else
		{
			int teamId=in.readInt();
			int health=in.readInt();
			int ammo=in.readInt();
			int fuel=in.readInt();
			Unit unit=Unit.copy((Unit)unitMap.get(id),(Team)teamMap.get(teamId));
			unit.health(health);
			unit.ammo(ammo);
			unit.fuel(fuel);
			return(unit);
		}
	}
	
	public static void writeMap(ObjectOutputStream out, Map map) throws IOException
	{
		out.writeInt(map.id());
		out.writeObject(map.name());
		out.writeInt(map.width());
		out.writeInt(map.height());
		
		out.writeInt(map.weather().id());
		
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				writeTerrain(out,map.terrain(i, a));
			}
		}
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				writeUnit(out,map.unit(i, a));
			}
		}
	}
	
	private static void writeTerrain(ObjectOutputStream out, Terrain terrain) throws IOException
	{
		out.writeInt(terrain.id());
		if(terrain instanceof Building)
		{
			Building building=(Building)terrain;
			if(building.team()!=null)
				out.writeInt(building.team().id());
			else
				out.writeInt(-1);
			out.writeInt(building.health());
		}
			
	}
	
	private static void writeUnit(ObjectOutputStream out, Unit unit) throws IOException
	{
		if(unit==null)
		{
			out.writeInt(-1);
			return;
		}
		out.writeInt(unit.id());
		out.writeInt(unit.team().id());
		out.writeInt(unit.health());
		out.writeInt(unit.ammo());
		out.writeInt(unit.fuel());
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
	
	
	public static GameMap unitMap;
	public static GameMap terrainMap;
	public static GameMap teamMap;
	public static GameMap weatherMap;
	
	
	public static Unit infantry;
	public static Unit mech;
	public static Unit recon;
	public static Unit tank;
	public static Unit artillery;
	
	public static Terrain plain;
	public static Terrain mountain;
	public static Terrain forest;
	
	public static Building city;
	public static Building base;
	
	public static Team teamOrangeStar;
	public static Team teamBlueMoon;
	public static Team teamGreenEarth;
	
	//public static HashMap<Integer,Terrain> terrainMap;
	
	public static Map testMap;
}
