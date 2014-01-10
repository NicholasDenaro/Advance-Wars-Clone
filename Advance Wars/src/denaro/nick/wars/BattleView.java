package denaro.nick.wars;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import denaro.nick.core.GameView2D;
import denaro.nick.core.Location;
import denaro.nick.core.Sprite;

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
	
	public void drawInfo(Battle battle, Graphics2D g)
	{
		Point cursor=battle.cursor();
		
		//terrain info
		Terrain terrain=battle.map().terrain(cursor.x, cursor.y);
		
		Composite oldComposite=g.getComposite();
		g.setColor(Color.black);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g.fillRect(this.getWidth()-32,this.getHeight()-52,32,52);
		g.setComposite(oldComposite);
		
		g.drawImage(terrain.image(),this.getWidth()-24-terrain.sprite().anchor().x,this.getHeight()-44-terrain.sprite().anchor().y,null);
		
		//terrain type
		Image img=GameFont.fonts.get("Map Font").stringToImage(terrain.name());
		g.drawImage(img,this.getWidth()-16-img.getWidth(null)/2, this.getHeight()-42-img.getHeight(null),null);
		
		//terrain defence
		img=GameFont.fonts.get("Map Font").stringToImage("def "+terrain.defence());
		g.drawImage(img,this.getWidth()-16-img.getWidth(null)/2, this.getHeight()-22,null);
		
		
		if(terrain instanceof Building)
		{
			Building building=(Building)terrain;
			//building capture
			img=GameFont.fonts.get("Map Font").stringToImage("cap"+(building.health()<10?" ":"")+building.health());
			g.drawImage(img,this.getWidth()-16-img.getWidth(null)/2, this.getHeight()-12,null);
		}
		
		//unit info
		Unit unit=battle.unitIfVisible(cursor.x, cursor.y);
		if(unit!=null)
		{
			oldComposite=g.getComposite();
			g.setColor(Color.black);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			g.fillRect(this.getWidth()-64,this.getHeight()-52,31,52);
			g.setComposite(oldComposite);
			
			g.drawImage(unit.image(),this.getWidth()-56-unit.sprite().anchor().x,this.getHeight()-44-unit.sprite().anchor().y,null);
			
			//unit health
			int health=(unit.health()+5)/10;
			img=GameFont.fonts.get("Map Font").stringToImage("%"+(health<10?" ":"")+health);
			g.drawImage(img,this.getWidth()-48-img.getWidth(null)/2, this.getHeight()-26,null);
			
			//unit ammo
			img=GameFont.fonts.get("Map Font").stringToImage("@"+(unit.ammo()<99?" ":"")+unit.ammo());
			g.drawImage(img,this.getWidth()-48-img.getWidth(null)/2, this.getHeight()-17,null);
			
			//unit fuel
			img=GameFont.fonts.get("Map Font").stringToImage("#"+(unit.fuel()<10?" ":"")+unit.fuel());
			g.drawImage(img,this.getWidth()-48-img.getWidth(null)/2, this.getHeight()-9,null);
		}
	}
	
	@Override
	public void drawLocation(Location currentLocation, Graphics2D g)
	{
		if(currentLocation instanceof Map && Main.currentMode instanceof Battle)
		{
			Map map=(Map)currentLocation;
			Battle battle=(Battle)Main.currentMode;
			
			drawTerrain(map,g);
			
			drawAttackSpaces(battle,g);
			
			drawUnits(map,g);
	
			drawMoveableArea(battle,g);
			drawPath(battle,g);
	
			drawGrid(map,g);
			
			drawCursor(g);
			
			drawMenus(g);
			
			drawInfo(battle,g);
		}
	}
	
	//private Battle battle;
}
