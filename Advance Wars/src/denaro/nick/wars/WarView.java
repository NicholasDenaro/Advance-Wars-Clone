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

public class WarView extends GameView2D
{

	public WarView(int width, int height, double hscale, double vscale)
	{
		super(width, height, hscale, vscale);
		// TODO Auto-generated constructor stub
	}
	
	public Image addFog(BufferedImage img)
	{
		BufferedImage image=new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D gimg=image.createGraphics();
		gimg.drawImage(img, 0, 0, null);
		gimg.setColor(Color.black);
		gimg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
		gimg.fillRect(0, 0, image.getWidth(), image.getHeight());
		return(image);
	}
	
	public void drawTerrain(Map map, Graphics2D g)
	{
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				Sprite sprite=map.terrain(i, a).sprite();
				if(map.weather().fog())
					if(map.fog(i, a))
						g.drawImage(addFog((BufferedImage)map.terrain(i,a).image()),i*Main.TILESIZE-sprite.anchor().x,a*Main.TILESIZE-sprite.anchor().y,null);
					else
						g.drawImage((BufferedImage)map.terrain(i,a).image(),i*Main.TILESIZE-sprite.anchor().x,a*Main.TILESIZE-sprite.anchor().y,null);
				else
					g.drawImage((BufferedImage)map.terrain(i,a).image(),i*Main.TILESIZE-sprite.anchor().x,a*Main.TILESIZE-sprite.anchor().y,null);
			}
		}
	}
	
	public void drawUnits(Map map, Graphics2D g)
	{
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				if(((!map.weather().fog())||(!map.fog(i,a)))&&(map.unit(i,a)!=null))
				{
					g.drawImage(map.unit(i, a).image(),i*Main.TILESIZE,a*Main.TILESIZE,null);
					if(map.terrain(i, a) instanceof Building)
					{
						Building building=(Building)map.terrain(i, a);
						if(building.health()!=20)
							g.drawImage(GameFont.fonts.get("Map Font").stringToImage("*"), i*Main.TILESIZE, a*Main.TILESIZE+8, null);
					}
				}
			}
		}
	}
	
	public void drawMoveableArea(Map map, Graphics2D g)
	{
		if(map.moveableArea()==null)
			return;
		g.setColor(Color.cyan);
		Composite oldComposite=g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				if(map.moveableArea(i,a))
					g.fillRect(i*Main.TILESIZE, a*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
			}
		}
		g.setComposite(oldComposite);
	}
	
	public void drawPath(Map map, Graphics2D g)
	{
		Path path;
		if((path=map.path())!=null)
		{
			g.drawImage(path.image(map.width(),map.height()),0,0,null);
		}
	}
	
	public void drawAttackSpaces(Map map, Graphics2D g)
	{
		if(map.attackableArea()!=null)
		{
			g.setColor(Color.red);
			Composite oldComposite=g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
			
			for(int a=0;a<map.height();a++)
			{
				for(int i=0;i<map.width();i++)
				{
					if(map.attackableArea(i,a))
						g.fillRect(i*Main.TILESIZE, a*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
				}
			}
			
			g.setComposite(oldComposite);
		}
	}
	
	public void drawGrid(Map map, Graphics2D g)
	{
		g.setColor(Color.gray);
		for(int i=0;i<map.width();i++)
		{
			g.drawLine(i*Main.TILESIZE, 0, i*Main.TILESIZE, map.height()*Main.TILESIZE);
		}
		
		for(int a=0;a<map.height();a++)
		{
			g.drawLine(0,a*Main.TILESIZE,map.width()*Main.TILESIZE,a*Main.TILESIZE);
		}
	}
	
	public void drawMenus(Graphics2D g)
	{
		if(Main.menu!=null)
		{
			Menu menu=Main.menu;
			g.drawImage(menu.image(),menu.point().x,menu.point().y,null);
			while((menu=menu.child())!=null)
				g.drawImage(menu.image(),menu.point().x,menu.point().y,null);
		}
	}
	
	public void drawInfo(Map map, Graphics2D g)
	{
		Point cursor=map.cursor();
		
		//terrain info
		Terrain terrain=map.terrain(cursor.x, cursor.y);
		
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
		Unit unit=map.unitIfVisible(cursor.x, cursor.y);
		if(unit!=null)
		{
			oldComposite=g.getComposite();
			g.setColor(Color.black);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			g.fillRect(this.getWidth()-64,this.getHeight()-52,31,52);
			g.setComposite(oldComposite);
			
			g.drawImage(unit.image(),this.getWidth()-56-terrain.sprite().anchor().x,this.getHeight()-44-terrain.sprite().anchor().y,null);
			
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
		if(currentLocation instanceof Map)
		{
			Map map=(Map)currentLocation;

			drawTerrain(map,g);
			
			drawAttackSpaces(map,g);
			
			drawUnits(map,g);

			drawMoveableArea(map,g);
			drawPath(map,g);

			//drawGrid(map,g);
			
			//draw cursor
			Sprite cursor=Sprite.sprite("Cursor");
			g.drawImage(cursor.subimage(0),map.cursor().x*Main.TILESIZE-cursor.anchor().x, map.cursor().y*Main.TILESIZE-cursor.anchor().y,null);
			
			//draw menus
			drawMenus(g);
			
			drawInfo(map,g);
		}
	}
}
