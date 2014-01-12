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

public class MapView extends GameView2D implements CursorListener
{

	public MapView(int width, int height, double hscale, double vscale)
	{
		super(width, height, hscale, vscale);
		view=new Point(0,0);
	}
	
	@Override
	public void cursorMoved(Point cursor)
	{
		if((cursor.x-view.x)*Main.TILESIZE>width()-1)
		{
			view.x++;
		}
		if((cursor.x-view.x)*Main.TILESIZE<0)
		{
			view.x--;
		}
		if((cursor.y-view.y)*Main.TILESIZE>height()-1)
		{
			view.y++;
		}
		if((cursor.y-view.y)*Main.TILESIZE<0)
		{
			view.y--;
		}
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
				if(Main.currentMode instanceof Battle)
					if(((Battle)Main.currentMode).weather().fog())
						if(((Battle)Main.currentMode).fog(i, a))
							g.drawImage(addFog((BufferedImage)map.terrain(i,a).image()),i*Main.TILESIZE-sprite.anchor().x,a*Main.TILESIZE-sprite.anchor().y,null);
						else//no fog, so just draw
							g.drawImage((BufferedImage)map.terrain(i,a).image(),i*Main.TILESIZE-sprite.anchor().x,a*Main.TILESIZE-sprite.anchor().y,null);
					else
						g.drawImage((BufferedImage)map.terrain(i,a).image(),i*Main.TILESIZE-sprite.anchor().x,a*Main.TILESIZE-sprite.anchor().y,null);
				else//not a battle, so just draw
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
				if((Main.currentMode instanceof Battle==false)||((!((Battle)Main.currentMode).weather().fog())||(!((Battle)Main.currentMode).fog(i,a))))
				{
					if(map.unit(i,a)!=null)
					{
						if(!map.unit(i,a).enabled())
						{
							Image unitImg=map.unit(i, a).image();
							BufferedImage image=new BufferedImage(unitImg.getWidth(null),unitImg.getHeight(null),BufferedImage.TYPE_INT_ARGB);
							Graphics2D gimg=image.createGraphics();
							gimg.drawImage(unitImg, 0, 0, null);
							gimg.setColor(Color.black);
							gimg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
							gimg.fillRect(0, 0, image.getWidth(), image.getHeight());
							g.drawImage(image,i*Main.TILESIZE,a*Main.TILESIZE,null);
						}
						else
						{
							g.drawImage(map.unit(i, a).image(),i*Main.TILESIZE,a*Main.TILESIZE,null);
						}
						if(map.terrain(i, a) instanceof Building)
						{
							Building building=(Building)map.terrain(i, a);
							if(building.health()!=20)
							{
								g.drawImage(GameFont.fonts.get("Map Font").stringToImage("*"), i*Main.TILESIZE, a*Main.TILESIZE+8, null);
							}
						}
						int hp=(map.unit(i,a).health()+5)/10;
						if(hp>=0&&hp!=10)
						{
							if(hp==0)
								hp=1;
							g.drawImage(GameFont.fonts.get("Map Font").stringToImage(""+hp), i*Main.TILESIZE+8, a*Main.TILESIZE+8, null);
						}
						if(map.unit(i,a).hasCargo())
						{
							g.drawImage(GameFont.fonts.get("Map Font").stringToImage("+"), i*Main.TILESIZE, a*Main.TILESIZE+8, null);
						}
					}
				}
			}
		}
	}
	
	public void drawGrid(Map map, Graphics2D g)
	{
		Composite oldComposite=g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		g.setColor(Color.gray);
		for(int i=0;i<map.width();i++)
		{
			g.drawLine(i*Main.TILESIZE, 0, i*Main.TILESIZE, map.height()*Main.TILESIZE);
		}
		
		for(int a=0;a<map.height();a++)
		{
			g.drawLine(0,a*Main.TILESIZE,map.width()*Main.TILESIZE,a*Main.TILESIZE);
		}
		g.setComposite(oldComposite);
	}
	
	public void drawCursor(Graphics2D g)
	{
		Sprite cursor=Sprite.sprite("Cursor");
		g.drawImage(cursor.subimage(0),Main.currentMode.cursor().x*Main.TILESIZE-cursor.anchor().x, Main.currentMode.cursor().y*Main.TILESIZE-cursor.anchor().y,null);
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
	
	public void drawOrder()
	{
		
	}
	
	public void offsetGraphics(Graphics2D g, boolean first)
	{
		g.translate((first?-1:1)*view.x*Main.TILESIZE, (first?-1:1)*view.y*Main.TILESIZE);
	}
	
	@Override
	public void drawLocation(Location currentLocation, Graphics2D g)
	{
		offsetGraphics(g,true);
		
		if(currentLocation instanceof Map)
		{
			Map map=(Map)currentLocation;
			
			drawTerrain(map,g);
			
			drawUnits(map,g);

			drawGrid(map,g);
			
			drawCursor(g);
			
			drawMenus(g);
		}
		
		offsetGraphics(g,false);
	}
	
	private Point view;
}
