package denaro.nick.wars;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

import denaro.nick.core.Entity;
import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameMap;
import denaro.nick.core.GameView2D;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;


public class Main
{
	public static void main(String[] args)
	{
		engine=(GameEngineByTick)GameEngineByTick.instance();
		engine.setTicksPerSecond(60);
		engine.setFramesPerSecond(60);
		
		createSprites();
		
		createUnits();
		
		createTerrain();
		
		createTeams();
		
		createWeather();
		
		/*System.out.print("Play map? ");
		
		String command=getInput();
		
		if(command==null||command.isEmpty())
		{
			createEditor();
		}
		else
		{
			Map map=loadMap(command);
			createBattle(map);
		}*/
		
		currentMode=new GameModeMenu();
		Location location=new Location();
		Entity entity=new Entity(Sprite.sprite("Homepage"),new Point.Double(0,0))
		{
			@Override
			public void tick()
			{
			}
		};
		location.addEntity(entity);
		engine.location(location);
		engine.requestFocus(currentMode);
		engine.view(new GameModeMenuView(240,160,2,2));
		
		menu=null;
		
		GameFrame frame=new GameFrame("Game",engine);
		frame.setVisible(true);
		Dimension screen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screen.width/2-frame.getWidth()/2,screen.height/2-frame.getHeight()/2);
		engine.addGameViewListener(frame);
		
		engine.start();
	}
	
	public static String getInput()
	{
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		String command;
		try
		{
			command=in.readLine();
			return(command);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return(null);
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
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Player Info.png"));
			new Sprite("Player Info",image,-1,-1,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Terrain.png"));
			new Sprite("Terrain",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Buildings.png"));
			new Sprite("Buildings",image,16,32,new Point(0,16));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Units.png"));
			new Sprite("Units",image,16,16,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Cursor.png"));
			new Sprite("Cursor",image,-1,-1,new Point(2,2));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Color Palette.png"));
			new Sprite("Color Palette",image,-1,-1,new Point(0,0));
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Font.png"));
			new GameFont("Map Font",image,8,10);
			
			image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Homepage.png"));
			new Sprite("Homepage",image,-1,-1,new Point(0,0));//http://retronoob.deviantart.com/art/Retro-War-quot-Advance-Wars-quot-75833354
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void createUnits()
	{
		unitMap=new GameMap<Unit>();
		stringToUnitID=new HashMap<String,Integer>();
		
		try
		{
			infantry=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			infantry.cost(1000);
			infantry.addWeapon(new UnitWeapon(0));
			infantry.defenceID(0);
			infantry.canCapture(true);
			infantry.imageIndex(0);
			infantry.movement(3);
			infantry.vision(2);
			infantry.fuel(99);
			infantry.movementType(MovementType.FOOT);
			infantry.finalize();
			unitMap.add(infantry);
			stringToUnitID.put("infantry",infantry.id());
			
			mech=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			mech.cost(3000);
			mech.addWeapon(new UnitWeapon(2).ammo(3));
			mech.addWeapon(new UnitWeapon(1));
			mech.defenceID(1);
			mech.canCapture(true);
			mech.imageIndex(4);
			mech.movement(2);
			mech.vision(2);
			mech.fuel(70);
			mech.movementType(MovementType.FOOT);
			mech.finalize();
			unitMap.add(mech);
			stringToUnitID.put("mech",mech.id());
			
			tank=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			tank.cost(7000);
			tank.addWeapon(new UnitWeapon(3).ammo(9));
			tank.addWeapon(new UnitWeapon(4));
			tank.defenceID(2);
			tank.imageIndex(12);
			tank.movement(6);
			tank.vision(3);
			tank.fuel(70);
			tank.movementType(MovementType.TREAD);
			tank.finalize();
			unitMap.add(tank);
			stringToUnitID.put("tank",tank.id());
			
			mdTank=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			mdTank.cost(16000);
			mdTank.addWeapon(new UnitWeapon(5).ammo(8));
			mdTank.addWeapon(new UnitWeapon(6));
			mdTank.defenceID(3);
			mdTank.imageIndex(16);
			mdTank.movement(5);
			mdTank.vision(1);
			mdTank.fuel(50);
			mdTank.movementType(MovementType.TREAD);
			mdTank.finalize();
			unitMap.add(mdTank);
			stringToUnitID.put("mdTank",mdTank.id());
			
			recon=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			recon.cost(4000);
			recon.addWeapon(new UnitWeapon(7));
			recon.defenceID(4);
			recon.imageIndex(8);
			recon.movement(8);
			recon.vision(5);
			recon.fuel(80);
			recon.movementType(MovementType.TIRES);
			recon.finalize();
			unitMap.add(recon);
			stringToUnitID.put("recon",recon.id());
			
			aa=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			aa.cost(8000);
			aa.addWeapon(new UnitWeapon(8).ammo(9));
			aa.defenceID(5);
			aa.imageIndex(32);
			aa.movement(6);
			aa.vision(2);
			aa.fuel(60);
			aa.movementType(MovementType.TREAD);
			aa.finalize();
			unitMap.add(aa);
			stringToUnitID.put("aa",aa.id());
			
			missiles=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			missiles.cost(12000);
			missiles.addWeapon(new UnitWeapon(8).ammo(6));
			missiles.defenceID(6);
			missiles.imageIndex(36);
			missiles.movement(4);
			missiles.vision(5);
			missiles.fuel(50);
			missiles.movementType(MovementType.TIRES);
			missiles.finalize();
			unitMap.add(missiles);
			stringToUnitID.put("missiles",missiles.id());
			
			artillery=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			artillery.cost(6000);
			artillery.addWeapon(new UnitWeapon(10).ammo(9));
			artillery.defenceID(7);
			artillery.imageIndex(24);
			artillery.movement(5);
			artillery.vision(1);
			artillery.fuel(50);
			artillery.movementType(MovementType.TREAD);
			artillery.attackRange(new Point(2,3));
			artillery.finalize();
			unitMap.add(artillery);
			stringToUnitID.put("artillery",artillery.id());
			
			rockets=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			rockets.cost(18000);
			rockets.addWeapon(new UnitWeapon(10).ammo(6));
			rockets.defenceID(8);
			rockets.imageIndex(28);
			rockets.movement(5);
			rockets.vision(1);
			rockets.fuel(50);
			rockets.movementType(MovementType.TREAD);
			rockets.attackRange(new Point(3,4));
			rockets.finalize();
			unitMap.add(rockets);
			stringToUnitID.put("rockets",rockets.id());
			
			apc=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			apc.cost(5000);
			apc.defenceID(9);
			apc.imageIndex(20);
			apc.movement(6);
			apc.vision(1);
			apc.fuel(70);
			apc.movementType(MovementType.TREAD);
			apc.cargoCount(1);
			apc.cargoType(0,1);
			apc.finalize();
			unitMap.add(apc);
			stringToUnitID.put("apc",apc.id());
		}
		catch(UnitFinalizedException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void createTerrain()
	{
		terrainMap=new GameMap<Terrain>();
		
		plain=new Terrain("Plain",new int[]{1,1,1,1,99,99,1});
		plain.defence(1);
		plain.imageIndex(9);
		terrainMap.add(plain);
		
		mountain=new Terrain("Mtn",new int[]{2,1,99,99,99,99,1});
		mountain.defence(4);
		mountain.visionBoost(1);
		mountain.imageIndex(19);
		terrainMap.add(mountain);
		
		forest=new Terrain("Wood",new int[]{1,1,2,3,99,99,1});
		forest.defence(3);
		forest.hiding(true);
		forest.imageIndex(21);
		terrainMap.add(forest);
		
		road=new Terrain("Road",new int[]{1,1,1,1,99,99,1});
		road.defence(0);
		road.imageIndex(25);
		terrainMap.add(road);
		
		river=new Terrain("River",new int[]{2,1,99,99,99,99,1});
		river.defence(0);
		river.imageIndex(1);
		terrainMap.add(river);
		
		bridge=new Terrain("Bridge",new int[]{1,1,1,1,99,99,1});
		bridge.defence(0);
		bridge.imageIndex(7);
		terrainMap.add(bridge);
		
		shoal=new Terrain("Shoal",new int[]{1,1,1,1,99,1,1});
		shoal.defence(0);
		shoal.imageIndex(4);
		terrainMap.add(shoal);
		
		sea=new Terrain("Sea",new int[]{99,99,99,99,1,1,1});
		sea.defence(0);
		sea.imageIndex(3);
		terrainMap.add(sea);
		
		reef=new Terrain("Reef",new int[]{99,99,99,99,2,2,1});
		reef.defence(2);
		reef.imageIndex(5);
		terrainMap.add(reef);
		
		city=new Building("City",null,new int[]{1,1,1,1,99,99,1});
		city.defence(4);
		city.imageIndex(0);
		terrainMap.add(city);
		
		base=new Building("Base",null,new int[]{1,1,1,1,99,99,1});
		base.addSelling("Infantry", unitMap.get(stringToUnitID.get("infantry")));
		base.addSelling("Mech", unitMap.get(stringToUnitID.get("mech")));
		base.addSelling("Recon", unitMap.get(stringToUnitID.get("recon")));
		base.addSelling("Tank", unitMap.get(stringToUnitID.get("tank")));
		base.addSelling("Md. Tank", unitMap.get(stringToUnitID.get("mdTank")));
		base.addSelling("APC", unitMap.get(stringToUnitID.get("apc")));
		base.addSelling("Artillery", unitMap.get(stringToUnitID.get("artillery")));
		base.addSelling("Rockets", unitMap.get(stringToUnitID.get("rockets")));
		base.addSelling("A-Air", unitMap.get(stringToUnitID.get("aa")));
		base.addSelling("Missiles", unitMap.get(stringToUnitID.get("missiles")));
		base.defence(3);
		base.imageIndex(1);
		terrainMap.add(base);
		
		airport=new Building("Airport",null,new int[]{1,1,1,1,99,99,1});
		airport.defence(3);
		airport.imageIndex(2);
		terrainMap.add(airport);
		
		port=new Building("Port",null,new int[]{1,1,1,1,1,1,1});
		port.defence(3);
		port.imageIndex(3);
		terrainMap.add(port);
		
		hq=new Building("HQ",null,new int[]{1,1,1,1,99,99,1});
		hq.hq(true);
		hq.defence(5);
		hq.imageIndex(4);
		terrainMap.add(hq);
	}
	
	public static void createTeams()
	{
		teamMap=new GameMap<Team>();
		
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
		weatherMap=new GameMap<Weather>();
		
		weatherMap.add(Weather.sunny);
		weatherMap.add(Weather.rainy);
		weatherMap.add(Weather.snowy);
	}
	
	public static void createMap()
	{
		if(!new File("Test Map.mp").exists())
		{
			testMap=new Map("Test Room",8,8);
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
			
			saveMap(testMap);
		}
		else
		{
			loadMap("Test Map");
		}
		engine.location(testMap);
	}
	
	public static void createBattle(Map map, BattleSettings settings)
	{
		engine.location(map);
		
		ArrayList<Integer> teamsId=map.teams();
		ArrayList<Team> teams=new ArrayList<Team>();
		
		for(Integer id:teamsId)
			teams.add(teamMap.get(id));
		
		
		battle=new Battle(map,teams,settings);
		Main.currentMode=battle;
		
		//battle.weather(Weather.foggy);
		
		engine.addKeyListener(battle);
		engine.requestFocus(battle);
		
		BattleView view=new BattleView(240,160,2,2);
		engine.view(view);
	}
	
	public static void createEditor(Map map)
	{
		editor=new MapEditor();
		
		if(map==null)
		{
			System.out.print("Map name: ");
			String name=getInput();
			System.out.print("Map width: ");
			Integer width=new Integer(getInput());
			System.out.print("Map height: ");
			Integer height=new Integer(getInput());
			
			editor.createNewMap(name, width, height);
		}
		else
		{
			editor.map(map);
		}
		engine.location(editor.map());
		currentMode=editor;
		
		engine.addKeyListener(editor);
		engine.requestFocus(editor);
		
		EditorView view=new EditorView(240,160,2,2);
		editor.addCursorListener(view);
		engine.view(view);
		System.out.println(engine.view());
	}
	
	public static void openMenu(Menu menu)
	{
		if(Main.menu!=null)
		{
			Menu child=Main.menu;
			while(child.child()!=null)
				child=child.child();
			
			child.child(menu);
			engine.requestFocus(menu);
		}
		else
		{
			Main.menu=menu;
			engine.requestFocus(menu);
		}
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
			engine.requestFocus(currentMode);
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
	
	public static ArrayList<String> getMapList()
	{
		ArrayList<String> maps=new ArrayList<String>();
		
		File f=new File("maps/");
		if(f.isDirectory())
		{
			FilenameFilter filter=new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return(name.contains(".mp"));
				}
			};
			
			String[] fnames=f.list(filter);
			for(String name:fnames)
				maps.add(name.substring(0,name.indexOf(".")));
		}
		return(maps);
	}
	
	public static Map loadMap(String mapName)
	{
		try
		{
			Map map=Main.readMap(new ObjectInputStream(new FileInputStream("maps/"+mapName+".mp")));
			return(map);
		}
		catch(ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return(null);
	}
	
	public static Map readMap(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		int id=in.readInt();
		Map map=new Map((String)in.readObject(),in.readInt(),in.readInt());
		
		
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
			Unit unit=Unit.copy((Unit)unitMap.get(id),(Team)teamMap.get(teamId));
			int health=in.readInt();
			int weaponCount=in.readInt();
			for(int i=0;i<weaponCount;i++)
			{
				int ammo=in.readInt();
				unit.weapon(i).ammo(ammo);
			}
			int fuel=in.readInt();
			unit.health(health);
			unit.fuel(fuel);
			return(unit);
		}
	}
	
	public static void saveMap(Map map)
	{
		try
		{
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File("maps/"+map.name()+".mp")));
			writeMap(oos,map);
			oos.close();
			System.out.println("map saved?!");
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void writeMap(ObjectOutputStream out, Map map) throws IOException
	{
		out.writeInt(map.id());
		out.writeObject(map.name());
		out.writeInt(map.width());
		out.writeInt(map.height());
		
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
		out.writeInt(unit.numberOfWeapons());
		for(int i=0;i<unit.numberOfWeapons();i++)
			out.writeInt(unit.weapon(i).ammo());
		out.writeInt(unit.fuel());
	}
	
	public static GameEngineByTick engine()
	{
		return(engine);
	}
	
	public static Menu menu;
	
	public static Battle battle;
	public static MapEditor editor;
	public static GameMode currentMode;
	
	public static TeamColorPalette colorPalette;
	
	private static GameEngineByTick engine;
	public static final int TILESIZE=16;
	
	
	public static GameMap<Unit> unitMap;
	public static GameMap<Terrain> terrainMap;
	public static GameMap<Team> teamMap;
	public static GameMap<Weather> weatherMap;
	
	public static HashMap<String,Integer> stringToUnitID;
	
	public static Unit infantry;
	public static Unit mech;
	public static Unit recon;
	public static Unit tank;
	public static Unit mdTank;
	public static Unit apc;
	public static Unit artillery;
	public static Unit rockets;
	public static Unit aa;
	public static Unit missiles;
	public static Unit fighter;
	public static Unit bomber;
	public static Unit bcopter;
	public static Unit tcopter;
	public static Unit bship;
	public static Unit cruiser;
	public static Unit lander;
	public static Unit sub;
	
	public static Terrain plain;
	public static Terrain mountain;
	public static Terrain forest;
	public static Terrain road;
	public static Terrain river;
	public static Terrain bridge;
	public static Terrain shoal;
	public static Terrain sea;
	public static Terrain reef;
	
	public static Building hq;
	public static Building city;
	public static Building base;
	public static Building airport;
	public static Building port;
	
	public static Team teamOrangeStar;
	public static Team teamBlueMoon;
	public static Team teamGreenEarth;
	
	public static Map testMap;
}
