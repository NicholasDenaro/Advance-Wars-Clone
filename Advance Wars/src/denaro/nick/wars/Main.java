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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import denaro.nick.core.Entity;
import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameMap;
import denaro.nick.core.GameView2D;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.server.Message;
import denaro.nick.server.MyInputStream;
import denaro.nick.server.MyOutputStream;
import denaro.nick.wars.menu.Menu;
import denaro.nick.wars.multiplayer.GameClient;
import denaro.nick.wars.multiplayer.MultiplayerBattle;
import denaro.nick.wars.view.BattleView;
import denaro.nick.wars.view.EditorView;
import denaro.nick.wars.view.GameModeMenuView;


public class Main
{
	public static void main(String[] args)
	{
		engine=(GameEngineByTick)GameEngineByTick.instance();
		engine.setTicksPerSecond(60);
		engine.setFramesPerSecond(60);
		
		loadAssets();
		
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
		
		createTeams();
		
		createUnits();
		
		createTerrain();
		
		
		
		createWeather();
	}
	
	public static void gotoMainMenu()
	{
		currentMode=new GameModeSelector();
		Location location=new Location();
		Entity entity=new Entity(Sprite.sprite("Homepage"),new Point.Double(0,0))
		{
			@Override
			public void tick()
			{
			}
		};
		engine.addEntity(entity,location);
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
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Path.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Path.png"));
			image=ImageIO.read(new FileInputStream("resources/Path.png"));
			new Sprite("Path",image,16,16,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Arrow.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Arrow.png"));
			image=ImageIO.read(new FileInputStream("resources/Arrow.png"));
			new Sprite("Arrow",image,16,16,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Action Menu.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Action Menu.png"));
			image=ImageIO.read(new FileInputStream("resources/Action Menu.png"));
			new Sprite("Action Menu",image,80,16,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Buy Menu.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Buy Menu.png"));
			image=ImageIO.read(new FileInputStream("resources/Buy Menu.png"));
			new Sprite("Buy Menu",image,-1,-1,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Player Info.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Player Info.png"));
			image=ImageIO.read(new FileInputStream("resources/Player Info.png"));
			new Sprite("Player Info",image,-1,-1,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Terrain.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Terrain.png"));
			image=ImageIO.read(new FileInputStream("resources/Terrain.png"));
			new Sprite("Terrain",image,16,16,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Buildings.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Buildings.png"));
			image=ImageIO.read(new FileInputStream("resources/Buildings.png"));
			new Sprite("Buildings",image,16,32,new Point(0,16));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Units.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Units.png"));
			image=ImageIO.read(new FileInputStream("resources/Units.png"));
			new Sprite("Units",image,16,16,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Cursor.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Cursor.png"));
			image=ImageIO.read(new FileInputStream("resources/Cursor.png"));
			new Sprite("Cursor",image,-1,-1,new Point(2,2));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Color Palette.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Color Palette.png"));
			image=ImageIO.read(new FileInputStream("resources/Color Palette.png"));
			new Sprite("Color Palette",image,-1,-1,new Point(0,0));
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Font.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Font.png"));
			image=ImageIO.read(new FileInputStream("resources/Font.png"));
			new GameFont("Map Font",image,8,10);
			
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("Homepage.png"));
			//image=ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/Homepage.png"));
			image=ImageIO.read(new FileInputStream("resources/Homepage.png"));
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
			apc.maxCargo(1);
			apc.cargoType(0,1);
			apc.complete();
			unitMap.add(apc);
			stringToUnitID.put("apc",apc.id());
			
			fighter=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			fighter.cost(20000);
			fighter.addWeapon(new UnitWeapon(18).ammo(9).complete());
			fighter.defenceID(16);
			fighter.imageIndex(40);
			fighter.movement(9);
			fighter.vision(2);
			fighter.fuel(99);
			fighter.movementType(MovementType.AIR);
			fighter.complete();
			unitMap.add(fighter);
			stringToUnitID.put("fighter",fighter.id());
			
			bomber=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			bomber.cost(22000);
			bomber.addWeapon(new UnitWeapon(18).complete());
			bomber.defenceID(17);
			bomber.imageIndex(44);
			bomber.movement(7);
			bomber.vision(2);
			bomber.fuel(99);
			bomber.movementType(MovementType.AIR);
			bomber.complete();
			unitMap.add(bomber);
			stringToUnitID.put("bomber",bomber.id());
			
			bcopter=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			bcopter.cost(9000);
			bcopter.addWeapon(new UnitWeapon(16).ammo(6).complete());
			bcopter.addWeapon(new UnitWeapon(17).complete());
			bcopter.defenceID(15);
			bcopter.imageIndex(48);
			bcopter.movement(6);
			bcopter.vision(3);
			bcopter.fuel(99);
			bcopter.movementType(MovementType.AIR);
			bcopter.complete();
			unitMap.add(bcopter);
			stringToUnitID.put("bcopter",bcopter.id());
			
			tcopter=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			tcopter.cost(5000);
			tcopter.defenceID(14);
			tcopter.imageIndex(52);
			tcopter.movement(6);
			tcopter.vision(2);
			tcopter.fuel(99);
			tcopter.movementType(MovementType.AIR);
			tcopter.maxCargo(2);
			tcopter.cargoType(0,1);
			tcopter.complete();
			unitMap.add(tcopter);
			stringToUnitID.put("tcopter",tcopter.id());
			
			bship=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			bship.cost(28000);
			bship.addWeapon(new UnitWeapon(15).ammo(9).complete());
			bship.defenceID(13);
			bship.imageIndex(56);
			bship.movement(5);
			bship.vision(2);
			bship.fuel(99);
			bship.movementType(MovementType.SHIP);
			bship.attackRange(new Point(2,6));
			bship.complete();
			unitMap.add(bship);
			stringToUnitID.put("bship",bship.id());
			
			cruiser=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			cruiser.cost(18000);
			cruiser.addWeapon(new UnitWeapon(12).ammo(9).complete());
			cruiser.addWeapon(new UnitWeapon(13).complete());
			cruiser.defenceID(11);
			cruiser.imageIndex(60);
			cruiser.movement(6);
			cruiser.vision(3);
			cruiser.fuel(99);
			cruiser.movementType(MovementType.SHIP);
			cruiser.maxCargo(2);
			cruiser.cargoType(stringToUnitID.get("fighter"),stringToUnitID.get("bomber"),stringToUnitID.get("bcopter"));
			cruiser.complete();
			unitMap.add(cruiser);
			stringToUnitID.put("cruiser",cruiser.id());
			
			lander=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			lander.cost(12000);
			lander.defenceID(10);
			lander.imageIndex(64);
			lander.movement(6);
			lander.vision(1);
			lander.fuel(99);
			lander.movementType(MovementType.TRANS);
			lander.maxCargo(2);
			lander.cargoType(0,1,2,3,4,5,6,7,8,9);
			lander.complete();
			unitMap.add(lander);
			stringToUnitID.put("lander",lander.id());
			
			sub=new Unit(Sprite.sprite("Units"),new Point.Double(0,0));
			sub.cost(20000);
			sub.addWeapon(new UnitWeapon(14).ammo(6).complete());
			sub.defenceID(12);
			sub.imageIndex(68);
			sub.movement(5);
			sub.vision(5);
			sub.fuel(60);
			sub.canHide(true);
			sub.movementType(MovementType.SHIP);
			unitMap.add(sub);
			stringToUnitID.put("sub",sub.id());
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
		plain.imageIndex(0);
		plain.color(new Color(0,150,0));
		terrainMap.add(plain);
		
		mountain=new Terrain("Mtn",new int[]{2,1,99,99,99,99,1});
		mountain.defence(4);
		mountain.visionBoost(1);
		mountain.imageIndex(2);
		mountain.color(new Color(75,30,0));
		terrainMap.add(mountain);
		
		forest=new Terrain("Wood",new int[]{1,1,2,3,99,99,1});
		forest.defence(3);
		forest.hiding(true);
		forest.imageIndex(1);
		forest.color(new Color(0,75,0));
		terrainMap.add(forest);
		
		road=new Terrain("Road",new int[]{1,1,1,1,99,99,1});
		road.defence(0);
		road.imageIndex(9);
		road.addTileMap(new int[][]{
				new int[]{8,9,10},
				new int[]{16,17,18},
				new int[]{24,25,26}
		});
		road.color(new Color(150,150,150));
		terrainMap.add(road);
		
		river=new Terrain("River",new int[]{2,1,99,99,99,99,1});
		river.defence(0);
		river.imageIndex(12);
		river.addTileMap(new int[][]{
				new int[]{11,12,13},
				new int[]{19,20,21},
				new int[]{27,28,29}
		});
		river.addTileMap(new int[][]{
				new int[]{35,36,37},
				new int[]{43,44,45},
				new int[]{51,52,53}
		});
		river.color(new Color(0,60,150));
		terrainMap.add(river);
		
		bridge=new Terrain("Bridge",new int[]{1,1,1,1,99,99,1});
		bridge.defence(0);
		bridge.imageIndex(5);
		bridge.addTileMap(new int[][]{
				new int[]{5,6}
		});
		bridge.color(new Color(150,150,150));
		terrainMap.add(bridge);
		
		shoal=new Terrain("Shoal",new int[]{1,1,1,1,99,1,1});
		shoal.defence(0);
		shoal.imageIndex(33);
		shoal.addTileMap(new int[][]{
				new int[]{32,33,34},
				new int[]{40,41,42},
				new int[]{48,49,50}
		});
		shoal.color(new Color(255,200,0));
		terrainMap.add(shoal);
		
		sea=new Terrain("Sea",new int[]{99,99,99,99,1,1,1});
		sea.defence(0);
		sea.imageIndex(3);
		sea.color(new Color(0,0,150));
		terrainMap.add(sea);
		
		reef=new Terrain("Reef",new int[]{99,99,99,99,2,2,1});
		reef.defence(2);
		reef.imageIndex(4);
		reef.color(new Color(0,0,75));
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
		airport.addSelling("Fighter", unitMap.get(stringToUnitID.get("fighter")));
		airport.addSelling("Bomber", unitMap.get(stringToUnitID.get("bomber")));
		airport.addSelling("B Copter", unitMap.get(stringToUnitID.get("bcopter")));
		airport.addSelling("T Copter", unitMap.get(stringToUnitID.get("tcopter")));
		airport.defence(3);
		airport.imageIndex(2);
		terrainMap.add(airport);
		
		port=new Building("Port",null,new int[]{1,1,1,1,1,1,1});
		port.addSelling("B Ship", unitMap.get(stringToUnitID.get("bship")));
		port.addSelling("Cruiser", unitMap.get(stringToUnitID.get("cruiser")));
		port.addSelling("Lander", unitMap.get(stringToUnitID.get("lander")));
		port.addSelling("Sub", unitMap.get(stringToUnitID.get("sub")));
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
		weatherMap.add(Weather.foggy);
		weatherMap.add(Weather.rainy);
		weatherMap.add(Weather.snowy);
	}
	
	public static MultiplayerBattle createMultiplayerBattle(Map map, BattleSettings settings)
	{
		MultiplayerBattle battle=new MultiplayerBattle(map,settings);
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
		return(battle);
	}
	
	public static void startBattle(Battle battle)
	{
		fixTeams(battle);
		battle.start();
		engine.location(battle.map());
		Main.currentMode=battle;
		
		engine.location(battle.map());
		
		engine.addKeyListener(battle);
		engine.requestFocus(battle);
		
		BattleView view=new BattleView(240,160,2,2);
		Main.currentMode.addCursorListener(view);
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
	
	public static Menu currentMenu()
	{
		if(Main.menu!=null)
		{
			Menu child=Main.menu;
			while(child.child()!=null)
				child=child.child();
			return(child);
		}
		return(null);
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
		if(child==null)
		{
			System.out.println("~ERROR~: The menu you are trying to close does not exist...");
			return;
		}
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
				g.fillRect(i, a, 1, 1);
				color=null;
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
	
	public static ByteBuffer readFromStream(FileInputStream ois) throws IOException
	{
		ByteBuffer buffer;
		ArrayList<Byte> bytes=new ArrayList<Byte>();
		try
		{
			while(true)
			{
				bytes.add((byte)ois.read());
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
		if(Main.stringToMapID.get(mapName)!=null)
			return(Main.mapMap.get(Main.stringToMapID.get(mapName)));
		try
		{
			MyInputStream in=new MyInputStream(new FileInputStream("maps/"+mapName+".mp"));
			in.read();
			//System.out.println("remaining bytes:"+in.remaining());
			Map map=Main.readMap(in);
			in.close();
			Main.mapMap.add(map);
			Main.stringToMapID.put(mapName,map.id());
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
		int id=in.readInt();
		Map map=new Map(in.readString(),in.readInt(),in.readInt());
		
		//read terrain
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				Terrain terrain=readTerrain(in);
				map.setTerrain(terrain,i,a);
				
			}
		}
		//read terrain directions
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				int direction=in.readInt();
				map.terrainDirections().changeDirection(direction,i,a);
			}
		}
		//read units
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
	
	public static Terrain readTerrain(MyInputStream in) throws IOException
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
			int cargoCount=in.readInt();
			for(int i=0;i<cargoCount;i++)
			{
				unit.setCargo(readUnit(in),i);
			}
			int fuel=in.readInt();
			boolean hidden=in.readBoolean();
			unit.enabled(enabled);
			unit.hidden(hidden);
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
		
		//write terrain
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				writeTerrain(message,map.terrain(i, a));
			}
		}
		//write terrain directions
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				message.addInt(map.terrainDirections().direction(i,a));
			}
		}
		//write units
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				writeUnit(message,map.unit(i, a));
			}
		}
	}
	
	public static void writeTerrain(Message message, Terrain terrain) throws IOException
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
	
	public static void writeUnit(Message message, Unit unit) throws IOException
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
		message.addInt(unit.maxCargo());
		for(int i=0;i<unit.maxCargo();i++)
			writeUnit(message,unit.cargo(i));
		
		message.addInt(unit.fuel());
		message.addBoolean(unit.isHidden());
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
	public static final int MINIMAPSIZE=4;
	
	
	public static GameMap<Unit> unitMap;
	public static GameMap<Terrain> terrainMap;
	public static GameMap<Team> teamMap;
	public static GameMap<Weather> weatherMap;
	public static GameMap<Commander> commanderMap;
	public static GameMap<Map> mapMap=new GameMap<Map>();
	
	public static HashMap<String,Integer> stringToUnitID;
	public static HashMap<String,Integer> stringToMapID=new HashMap<String,Integer>();
	
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
	public static Unit fighter;//def 16
	public static Unit bomber;//def 17
	public static Unit bcopter;//def 15
	public static Unit tcopter;//def 14
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
