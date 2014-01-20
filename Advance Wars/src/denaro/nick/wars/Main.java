package denaro.nick.wars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import denaro.nick.core.Entity;
import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameMap;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;
import denaro.nick.server.MyOutputStream;
import denaro.nick.wars.multiplayer.GameClient;
import denaro.nick.wars.multiplayer.MultiplayerBattle;


public class Main
{
	public static void main(String[] args)
	{
		engine=(GameEngineByTick)GameEngineByTick.instance();
		engine.setTicksPerSecond(60);
		engine.setFramesPerSecond(60);
		
		loadAssets();
		
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
		
		gotoMainMenu();
		
		menu=null;
		
		GameFrame frame=new GameFrame("Game",engine);
		frame.setVisible(true);
		Dimension screen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screen.width/2-frame.getWidth()/2,screen.height/2-frame.getHeight()/2);
		engine.addGameViewListener(frame);
		
		engine.start();
	}
	
	public static void loadAssets()
	{
		createSprites();
		
		createUnits();
		
		createTerrain();
		
		createTeams();
		
		createWeather();
	}
	
	public static void gotoMainMenu()
	{
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
			infantry.addWeapon(new UnitWeapon(0).complete());
			infantry.defenceID(0);
			infantry.canCapture(true);
			infantry.imageIndex(0);
			infantry.movement(3);
			infantry.vision(2);
			infantry.fuel(99);
			infantry.movementType(MovementType.FOOT);
			infantry.complete();
			unitMap.add(infantry);
			stringToUnitID.put("infantry",infantry.id());
			
			mech=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			mech.cost(3000);
			mech.addWeapon(new UnitWeapon(2).ammo(3).complete());
			mech.addWeapon(new UnitWeapon(1).complete());
			mech.defenceID(1);
			mech.canCapture(true);
			mech.imageIndex(4);
			mech.movement(2);
			mech.vision(2);
			mech.fuel(70);
			mech.movementType(MovementType.FOOT);
			mech.complete();
			unitMap.add(mech);
			stringToUnitID.put("mech",mech.id());
			
			tank=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			tank.cost(7000);
			tank.addWeapon(new UnitWeapon(3).ammo(9).complete());
			tank.addWeapon(new UnitWeapon(4).complete());
			tank.defenceID(2);
			tank.imageIndex(12);
			tank.movement(6);
			tank.vision(3);
			tank.fuel(70);
			tank.movementType(MovementType.TREAD);
			tank.complete();
			unitMap.add(tank);
			stringToUnitID.put("tank",tank.id());
			
			mdTank=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			mdTank.cost(16000);
			mdTank.addWeapon(new UnitWeapon(5).ammo(8).complete());
			mdTank.addWeapon(new UnitWeapon(6).complete());
			mdTank.defenceID(3);
			mdTank.imageIndex(16);
			mdTank.movement(5);
			mdTank.vision(1);
			mdTank.fuel(50);
			mdTank.movementType(MovementType.TREAD);
			mdTank.complete();
			unitMap.add(mdTank);
			stringToUnitID.put("mdTank",mdTank.id());
			
			recon=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			recon.cost(4000);
			recon.addWeapon(new UnitWeapon(7).complete());
			recon.defenceID(4);
			recon.imageIndex(8);
			recon.movement(8);
			recon.vision(5);
			recon.fuel(80);
			recon.movementType(MovementType.TIRES);
			recon.complete();
			unitMap.add(recon);
			stringToUnitID.put("recon",recon.id());
			
			aa=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			aa.cost(8000);
			aa.addWeapon(new UnitWeapon(8).ammo(9).complete());
			aa.defenceID(5);
			aa.imageIndex(32);
			aa.movement(6);
			aa.vision(2);
			aa.fuel(60);
			aa.movementType(MovementType.TREAD);
			aa.complete();
			unitMap.add(aa);
			stringToUnitID.put("aa",aa.id());
			
			missiles=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			missiles.cost(12000);
			missiles.addWeapon(new UnitWeapon(8).ammo(6).complete());
			missiles.defenceID(6);
			missiles.imageIndex(36);
			missiles.movement(4);
			missiles.vision(5);
			missiles.fuel(50);
			missiles.movementType(MovementType.TIRES);
			missiles.complete();
			unitMap.add(missiles);
			stringToUnitID.put("missiles",missiles.id());
			
			artillery=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			artillery.cost(6000);
			artillery.addWeapon(new UnitWeapon(10).ammo(9).complete());
			artillery.defenceID(7);
			artillery.imageIndex(24);
			artillery.movement(5);
			artillery.vision(1);
			artillery.fuel(50);
			artillery.movementType(MovementType.TREAD);
			artillery.attackRange(new Point(2,3));
			artillery.complete();
			unitMap.add(artillery);
			stringToUnitID.put("artillery",artillery.id());
			
			rockets=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			rockets.cost(18000);
			rockets.addWeapon(new UnitWeapon(10).ammo(6).complete());
			rockets.defenceID(8);
			rockets.imageIndex(28);
			rockets.movement(5);
			rockets.vision(1);
			rockets.fuel(50);
			rockets.movementType(MovementType.TREAD);
			rockets.attackRange(new Point(3,4));
			rockets.complete();
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
			apc.complete();
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
		teamOrangeStar=new Team("Orange Star",1);
		teamMap.add(teamOrangeStar);
		teamBlueMoon=new Team("Blue Moon",2);
		teamMap.add(teamBlueMoon);
		teamGreenEarth=new Team("Green Earth",3);
		teamMap.add(teamGreenEarth);
		
		commanderMap=new GameMap<Commander>();
		
		commanderMap.add(new Commander("Nell"));
		commanderMap.add(new Commander("Andy"));
	}
	
	public static void createWeather()
	{
		weatherMap=new GameMap<Weather>();
		
		weatherMap.add(Weather.clear);
		weatherMap.add(Weather.rainy);
		weatherMap.add(Weather.snowy);
	}
	
	public static MultiplayerBattle createMultiplayerBattle(Map map, BattleSettings settings)
	{
		MultiplayerBattle battle=new MultiplayerBattle(map,settings);
		/*battle.turn(-1);
		battle.nextTurn();*/
		return(battle);
	}
	
	public static Battle createBattle(Map map, BattleSettings settings, ArrayList<Commander> commanders)
	{
		ArrayList<Integer> teamsId=map.teams();
		ArrayList<Team> teams=new ArrayList<Team>();
		
		System.out.println("creating battle...");
		System.out.println("commanders: "+commanders);
		
		for(Integer id:teamsId)
			teams.add(Team.copy(teamMap.get(id),commanders.get(id)));
		
		
		Battle battle=new Battle(map,teams,settings);
		battle.turn(-1);
		battle.nextTurn();
		return(battle);
	}
	
	public static void startBattle(Battle battle)
	{
		fixTeams(battle);
		engine.location(battle.map());
		Main.currentMode=battle;
		
		engine.location(battle.map());
		
		engine.addKeyListener(battle);
		engine.requestFocus(battle);
		
		BattleView view=new BattleView(240,160,2,2);
		engine.view(view);
		
		System.out.println("battle started!");
	}
	
	public static void createEditor(Map map)
	{
		MapEditor editor=new MapEditor();
		
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
	
	public static void closeAllMenus()
	{
		menu=null;
		engine.requestFocus(currentMode);
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
	
	public static ArrayList<String> getBattleList()
	{
		ArrayList<String> maps=new ArrayList<String>();
		
		File f=new File("battles/");
		if(f.isDirectory())
		{
			FilenameFilter filter=new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return(name.contains(".bt"));
				}
			};
			
			String[] fnames=f.list(filter);
			for(String name:fnames)
				maps.add(name.substring(0,name.indexOf(".")));
		}
		return(maps);
	}
	
	public static ByteBuffer readFromStream(ObjectInputStream ois) throws IOException
	{
		ByteBuffer buffer;
		ArrayList<Byte> bytes=new ArrayList<Byte>();
		try
		{
			while(true)
			{
				bytes.add(ois.readByte());
			}
		}
		catch(EOFException ex)
		{
			//end of file breaks while! =D
		}
		ois.close();
		buffer=ByteBuffer.allocate(bytes.size());
		for(Byte b:bytes)
			buffer.put(b);
		
		return(buffer);
	}
	
	public static void fixTeams(Battle battle)
	{
		Map map=battle.map();
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				if(map.terrain(i,a) instanceof Building)
				{
					Building building=(Building)map.terrain(i,a);
					building.team(battle.team(building.team()));
				}
				
				if(map.unit(i,a)!=null)
				{
					Unit unit=map.unit(i,a);
					unit.team(battle.team(unit.team()));
				}
			}
		}
	}
	
	public static MultiplayerBattle loadMultiplayerBattle(String battleName, ByteBuffer buffer)
	{
		System.out.println("loading battle");
		try
		{
			MyInputStream in;
			if(buffer==null)
			{
				in=new MyInputStream(new FileInputStream("battles/"+battleName+".bt"));
				in.read();
			}
			else
				in=new MyInputStream(buffer);
			int turn=in.readInt();
			
			
			ArrayList<Team> teams=new ArrayList<Team>();
			
			int size=in.readInt();
			for(int i=0;i<size;i++)
			{
				int teamid=in.readInt();
				int funds=in.readInt();
				int comid=in.readInt();
				Team team=Team.copy(Main.teamMap.get(teamid),Main.commanderMap.get(comid));
				team.funds(funds);
				teams.add(team);
			}
			
			BattleSettings settings=new BattleSettings();
			
			int startingFunds=in.readInt();
			int fundsPerTurn=in.readInt();
			boolean fogOfWar=in.readBoolean();
			int weather=in.readInt();
			
			settings.startingFunds(startingFunds);
			settings.fundsPerTurn(fundsPerTurn);
			settings.fogOfWar(fogOfWar);
			settings.weather(weather);
			
			int width=in.readInt();
			int height=in.readInt();
			
			boolean fog[][]=new boolean[width][height];
			
			for(int a=0;a<height;a++)
			{
				for(int i=0;i<width;i++)
				{
					fog[i][a]=in.readBoolean();
				}
			}
			
			Map map=readMap(in);
			
			MultiplayerBattle battle=new MultiplayerBattle(map,settings);
			battle.turn(turn);
			battle.fog(fog);
			//fixTeams(battle);
			//TODO remember to fix teams before starting!
			
			return(battle);
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
	
	public static Battle loadBattle(String battleName, ByteBuffer buffer)
	{
		System.out.println("loading battle");
		try
		{
			MyInputStream in;
			if(buffer==null)
			{
				in=new MyInputStream(new FileInputStream("battles/"+battleName+".bt"));
				in.read();
			}
			else
				in=new MyInputStream(buffer);
			
			
			int turn=in.readInt();
			
			ArrayList<Team> teams=new ArrayList<Team>();
			
			int size=in.readInt();
			for(int i=0;i<size;i++)
			{
				int teamid=in.readInt();
				int funds=in.readInt();
				int comid=in.readInt();
				Team team=Team.copy(Main.teamMap.get(teamid),Main.commanderMap.get(comid));
				team.funds(funds);
				teams.add(team);
			}
			
			BattleSettings settings=new BattleSettings();
			
			int startingFunds=in.readInt();
			int fundsPerTurn=in.readInt();
			boolean fogOfWar=in.readBoolean();
			int weather=in.readInt();
			
			settings.startingFunds(startingFunds);
			settings.fundsPerTurn(fundsPerTurn);
			settings.fogOfWar(fogOfWar);
			settings.weather(weather);
			
			int width=in.readInt();
			int height=in.readInt();
			
			boolean fog[][]=new boolean[width][height];
			
			for(int a=0;a<height;a++)
			{
				for(int i=0;i<width;i++)
				{
					fog[i][a]=in.readBoolean();
				}
			}
			
			Map map=readMap(in);
			
			Battle battle=new Battle(map,teams,settings);
			battle.turn(turn);
			battle.fog(fog);
			if(!teams.isEmpty())
				fixTeams(battle);
			//TODO remember to fix the teams later
			
			return(battle);
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
	
	public static Map loadMap(String mapName)
	{
		try
		{
			MyInputStream in=new MyInputStream(new FileInputStream("maps/"+mapName+".mp"));
			in.read();
			//System.out.println("remaining bytes:"+in.remaining());
			Map map=Main.readMap(in);
			in.close();
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
	
	public static Map readMap(MyInputStream in) throws IOException, ClassNotFoundException
	{
		//TODO fix this shit for big maps?
		int id=in.readInt();
		Map map=new Map(in.readString(),in.readInt(),in.readInt());
		//System.out.println("map name: "+map.name());
		//System.out.println("map dims: "+map.width()+","+map.height());
		
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				Terrain terrain=readTerrain(in);
				map.setTerrain(terrain,i,a);
				
			}
		}
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				Unit unit=readUnit(in);
				map.addUnit(unit,i,a);
			}
		}
		return(map);
	}
	
	private static Terrain readTerrain(MyInputStream in) throws IOException
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
	
	public static Unit readUnit(MyInputStream in) throws IOException
	{
		int id=in.readInt();
		if(id==-1)
			return(null);
		else
		{
			//System.out.println("remaining: "+in.remaining());
			int teamId=in.readInt();
			Unit unit=Unit.copy((Unit)unitMap.get(id),(Team)teamMap.get(teamId));
			boolean enabled=in.readBoolean();
			int health=in.readInt();
			int weaponCount=in.readInt();
			for(int i=0;i<weaponCount;i++)
			{
				int ammo=in.readInt();
				unit.weapon(i).ammo(ammo);
			}
			int fuel=in.readInt();
			unit.enabled(enabled);
			unit.health(health);
			unit.fuel(fuel);
			return(unit);
		}
	}
	
	public static Message saveBattle(Battle battle)
	{
		try
		{
			FileOutputStream fos=new FileOutputStream(new File("battles/"+battle.map().name()+".bt"));
			Message message=new Message();
			
			//message.addInt(battle.whosTurn().id());
			message.addInt(battle.turn());
			ArrayList<Team> teams=battle.teams();
			if(teams!=null)
			{
				message.addInt(teams.size());
				for(int i=0;i<teams.size();i++)
				{
					Team team=teams.get(i);
					//System.out.println("team: "+team.id()+", "+team.name());
					message.addInt(team.id());
					message.addInt(team.funds());
					message.addInt(team.commander().id());
				}
			}
			else
				message.addInt(0);
			
			BattleSettings settings=battle.settings();
			
			message.addInt(settings.startingFunds());
			message.addInt(settings.fundsPerTurn());
			message.addBoolean(settings.fogOfWar());
			message.addInt(settings.weather());
			
			
			message.addInt(battle.map().width());
			message.addInt(battle.map().height());
			
			for(int a=0;a<battle.map().height();a++)
			{
				for(int i=0;i<battle.map().width();i++)
				{
					message.addBoolean(battle.fog(i,a));
				}
			}
			
			writeMap(message,battle.map());
			
			MyOutputStream out=new MyOutputStream(fos);
			out.addMessage(message);
			out.flush(message.size());
			out.close();
			
			System.out.println("battle saved?!");
			return(message);
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
	
	public static void saveMap(Map map)
	{
		try
		{
			FileOutputStream fos=new FileOutputStream(new File("maps/"+map.name()+".mp"));
			Message message=new Message();
			writeMap(message,map);
			
			MyOutputStream out=new MyOutputStream(fos);
			out.addMessage(message);
			System.out.println("message size: "+message.size());
			out.flush(message.size());
			out.close();
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
	
	public static void writeMap(Message message, Map map) throws IOException
	{
		//TODO fix this shit for big maps?
		message.addInt(map.id());
		message.addString(map.name());
		message.addInt(map.width());
		message.addInt(map.height());
		
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				writeTerrain(message,map.terrain(i, a));
			}
		}
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				writeUnit(message,map.unit(i, a));
			}
		}
	}
	
	private static void writeTerrain(Message message, Terrain terrain) throws IOException
	{
		message.addInt(terrain.id());
		if(terrain instanceof Building)
		{
			Building building=(Building)terrain;
			if(building.team()!=null)
				message.addInt(building.team().id());
			else
				message.addInt(-1);
			message.addInt(building.health());
		}
			
	}
	
	private static void writeUnit(Message message, Unit unit) throws IOException
	{
		if(unit==null)
		{
			message.addInt(-1);
			return;
		}
		message.addInt(unit.id());
		if(unit.team()!=null)
			message.addInt(unit.team().id());
		else
			message.addInt(-1);
		message.addBoolean(unit.enabled());
		message.addInt(unit.health());
		message.addInt(unit.numberOfWeapons());
		for(int i=0;i<unit.numberOfWeapons();i++)
			message.addInt(unit.weapon(i).ammo());
		message.addInt(unit.fuel());
	}
	
	public static GameEngineByTick engine()
	{
		return(engine);
	}
	
	public static Menu menu;
	
	public static GameMode currentMode;
	
	public static TeamColorPalette colorPalette;
	
	private static GameEngineByTick engine;
	public static final int TILESIZE=16;
	
	
	public static GameMap<Unit> unitMap;
	public static GameMap<Terrain> terrainMap;
	public static GameMap<Team> teamMap;
	public static GameMap<Weather> weatherMap;
	public static GameMap<Commander> commanderMap;
	
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
	
	public static GameClient client;
	
	//public static Map testMap;
}
