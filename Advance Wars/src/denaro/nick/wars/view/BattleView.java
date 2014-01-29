package denaro.nick.wars.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import denaro.nick.core.GameView2D;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;
import denaro.nick.wars.Battle;
import denaro.nick.wars.GameFont;
import denaro.nick.wars.Main;
import denaro.nick.wars.Map;
import denaro.nick.wars.Path;
import denaro.nick.wars.multiplayer.MultiplayerBattle;

public class BattleView extends MapView
{

	public BattleView(int width, int height, double hscale, double vscale)
	{
		super(width, height, hscale, vscale);
		// TODO Auto-generated constructor stub
	}
	
	public void drawMoveableArea(Battle battle, Graphics2D g)
	{
		if(battle.moveableArea()==null)
			return;
		g.setColor(Color.cyan);
		Composite oldComposite=g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		for(int a=0;a<battle.map().height();a++)
		{
			for(int i=0;i<battle.map().width();i++)
			{
				if(battle.moveableArea(i,a))
					g.fillRect(i*Main.TILESIZE, a*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
			}
		}
		g.setComposite(oldComposite);
	}
	
	public void drawPath(Battle battle, Graphics2D g)
	{
		Path path;
		if((path=battle.path())!=null)
		{
			g.drawImage(path.image(battle.map().width(),battle.map().height()),0,0,null);
		}
	}
	
	public void drawAttackSpaces(Battle battle, Graphics2D g)
	{
		if(battle.attackableArea()!=null)
		{
			g.setColor(Color.red);
			Composite oldComposite=g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
			
			for(int a=0;a<battle.map().height();a++)
			{
				for(int i=0;i<battle.map().width();i++)
				{
					if(battle.attackableArea(i,a))
						g.fillRect(i*Main.TILESIZE, a*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
				}
			}
			
			g.setComposite(oldComposite);
		}
	}
	
	
	
	public void drawPlayerInfo(Battle battle, Graphics2D g)
	{
		Sprite sprite=Sprite.sprite("Player Info");
		
		BufferedImage img=new BufferedImage(sprite.width(),sprite.height(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D gimg=img.createGraphics();
		
		//draw bg
		gimg.drawImage(sprite.subimage(0),0,0,null);
		if(battle instanceof MultiplayerBattle==false)
			Main.swapPalette(img,battle.whosTurn(),0);
		else
			Main.swapPalette(img,((MultiplayerBattle)battle).myTeam(),0);
		
		//draw day//draw funds
		boolean drawDay=false;
		if(drawDay)
		{
			gimg.drawImage(GameFont.fonts.get("Map Font").stringToImage("Day"),12,6,null);
			Image txt=GameFont.fonts.get("Map Font").stringToImage(""+battle.day());
			gimg.drawImage(txt,66-txt.getWidth(null),6,null);
		}
		else
		{
			
			gimg.drawImage(GameFont.fonts.get("Map Font").stringToImage("G"),5,6,null);
			Image txt;
			if(battle instanceof MultiplayerBattle==false)
				txt=GameFont.fonts.get("Map Font").stringToImage(""+battle.whosTurn().funds());
			else
				txt=GameFont.fonts.get("Map Font").stringToImage(""+((MultiplayerBattle)battle).myTeam().funds());
			gimg.drawImage(txt,66-txt.getWidth(null),6,null);
		}
		
		//draw img
		int xPos=0;
		if((Main.currentMode.cursor().y-view().y)*Main.TILESIZE<sprite.height()*1.2&&(Main.currentMode.cursor().x-view().x)*Main.TILESIZE<sprite.width()*1.2)
		{
			xPos=width()-sprite.width();
		}
		
		g.drawImage(img,xPos,0,null);
	}
	
	@Override
	public void drawLocation(Location currentLocation, Graphics2D g)
	{
		if(currentLocation instanceof Map==false)
			return;
		
		offsetGraphics(g,true);
		
		/*if(Main.currentMode instanceof MultiplayerBattle)
		{
			Map map=(Map)currentLocation;
			MultiplayerBattle battle=(MultiplayerBattle)Main.currentMode;
			
			drawTerrain(map,g);
			
			if(battle.started())
			{
				g.translate(0,Main.TILESIZE);
				
				drawAttackSpaces(battle,g);
				
				drawUnits(map,g);
		
				drawMoveableArea(battle,g);
				drawPath(battle,g);
		
				drawGrid(map,g);
				
				drawCursor(g);
				
				drawMenus(g);
				
				offsetGraphics(g,false);
				
				g.translate(0,-Main.TILESIZE);
				
				drawInfo(map,g);
				
				drawPlayerInfo(battle,g);
			}
			else
			{
				GameView2D view=(GameView2D)Main.engine().view();
				Image image=GameFont.fonts.get("Map Font").stringToImage("Waiting...");
				g.drawImage(image,view.width()/2-image.getWidth(null)/2,view.height()/2,null);
			}
		}
		else */if(Main.currentMode instanceof Battle)
		{
			Map map=(Map)currentLocation;
			Battle battle=(Battle)Main.currentMode;
			
			g.translate(0,Main.TILESIZE);
			
			drawTerrain(map,g);
			
			drawAttackSpaces(battle,g);
			
			drawUnits(map,g);
	
			drawMoveableArea(battle,g);
			drawPath(battle,g);
	
			drawGrid(map,g);
			
			drawCursor(g);
			
			drawMenus(g);
			
			offsetGraphics(g,false);
			
			g.translate(0,-Main.TILESIZE);
			
			drawInfo(map,g);
			
			drawPlayerInfo(battle,g);
			
			if(battle.whosTurn()!=battle.myTeam())
			{
				GameView2D view=(GameView2D)Main.engine().view();
				Image image=GameFont.fonts.get("Map Font").stringToImage("Waiting For Turn");
				g.drawImage(image,view.width()/2-image.getWidth(null)/2,view.height()/2,null);
			}
		}
	}
	
	//private Battle battle;
}
