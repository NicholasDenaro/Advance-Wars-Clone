package denaro.nick.wars;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;

import denaro.nick.core.GameView2D;
import denaro.nick.core.Location;

public class WarView extends GameView2D
{

	public WarView(int width, int height, double hscale, double vscale)
	{
		super(width, height, hscale, vscale);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void drawLocation(Location currentLocation, Graphics2D g)
	{
		//super.drawLocation(currentLocation,g);
		
		if(currentLocation instanceof Map)
		{
			Map map=(Map)currentLocation;
			
			//draw the terrain
			for(int a=0;a<map.height();a++)
			{
				for(int i=0;i<map.width();i++)
				{
					g.drawImage(map.terrain(i,a).image(),i*Main.TILESIZE,a*Main.TILESIZE,null);
				}
			}
			
			//draw the units
			for(int a=0;a<map.height();a++)
			{
				for(int i=0;i<map.width();i++)
				{
					if(((!map.weather().fog())||(!map.fog(i,a)))&&(map.unit(i,a)!=null))
						g.drawImage(map.unit(i, a).image(),i*Main.TILESIZE,a*Main.TILESIZE,null);
				}
			}
			
			//draw fog
			g.setColor(Color.black);
			Composite oldComposite=g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			for(int a=0;a<map.height();a++)
			{
				for(int i=0;i<map.width();i++)
				{
					if(map.weather().fog())
						if(map.fog(i, a))
							g.fillRect(i*Main.TILESIZE, a*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
				}
			}
			g.setComposite(oldComposite);
			
			//draw available movement spaces
			//draw path
			Path path;
			if((path=map.path())!=null)
			{
				g.setColor(Color.cyan);
				oldComposite=g.getComposite();
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
				
				g.drawImage(path.image(map.width(),map.height()),0,0,null);
			}
			
			//draw the grid
			g.setColor(Color.gray);
			for(int i=0;i<map.width();i++)
			{
				g.drawLine(i*Main.TILESIZE, 0, i*Main.TILESIZE, map.height()*Main.TILESIZE);
			}
			
			for(int a=0;a<map.height();a++)
			{
				g.drawLine(0,a*Main.TILESIZE,map.width()*Main.TILESIZE,a*Main.TILESIZE);
			}
			
			//draw cursor
			g.setColor(Color.pink);
			//Stroke oldStroke=g.getStroke();
			BasicStroke stroke=new BasicStroke(2);
			g.setStroke(stroke);
			g.drawRect(map.cursor().x*Main.TILESIZE, map.cursor().y*Main.TILESIZE, Main.TILESIZE, Main.TILESIZE);
			//g.setStroke(oldStroke);
			
			//draw menus
			if(Main.menu!=null)
			{
				Menu menu=Main.menu;
				g.drawImage(menu.image(),menu.point().x,menu.point().y,null);
			}
		}
	}
}
