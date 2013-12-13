package denaro.nick.rf4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import denaro.nick.core.GameEngine;
import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameView;
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
		GameView2D view=new GameView2D(240,160,1,1);
		engine.view(view);
		
		GameFrame frame=new GameFrame("Game",engine);
		frame.setVisible(true);
		
		Location testRoom=new Location();
		
		engine.location(testRoom);
		
		createSprites();
		
		Player player=new Player(Sprite.sprite("Player"),new Point.Double(16,16));
		testRoom.addEntity(player);
		
		engine.addKeyListener(player);
		engine.requestFocus(player);
		
		
		Wall wall=new Wall(Sprite.sprite("Wall"),new Point.Double(64,64));
		engine.location().addEntity(wall);
		
		Item helmet=new Item(Sprite.sprite("Helmet"),new Point.Double(80,80),ItemType.head);
		engine.location().addEntity(helmet);
		
		Weapon sword=new Weapon(Sprite.sprite("Sword"),new Point.Double(120,120),WeaponType.sword);
		engine.location().addEntity(sword);
		
		
		engine.start();
	}
	
	public static void createSprites()
	{
		BufferedImage playerImage=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=playerImage.createGraphics();
		g.setColor(new Color(0,0,255));
		g.fillRect(0,0,16,16);
		new Sprite("Player",playerImage,16,16,new Point(8,16));
		
		BufferedImage wallImage=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		g=wallImage.createGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,16,16);
		new Sprite("Wall",wallImage,16,16,new Point(0,0));
		
		BufferedImage itemImage=new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		g=itemImage.createGraphics();
		g.setColor(new Color(200,150,50));
		g.fillOval(0,0,16,16);
		new Sprite("Helmet",itemImage,16,16,new Point(0,0));
		
		try
		{
			new Sprite("Sword","SwordSwing.png",32,32,new Point(0,0));
		}
		catch(IOException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
	}
	
	public static GameEngineByTick engine()
	{
		return(engine);
	}
	
	private static GameEngineByTick engine;
}
