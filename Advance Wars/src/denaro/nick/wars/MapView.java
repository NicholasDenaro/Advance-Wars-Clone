package denaro.nick.wars;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
	
	public Point view()
	{
		return(view);
	}
	
	public void addMapViewListener(MapViewListener listener)
	{
		if(mapViewListeners==null)
			mapViewListeners=new ArrayList<MapViewListener>();
		
		if(!mapViewListeners.contains(listener))
			mapViewListeners.add(listener);
	}
	
	public void removeMapViewListener(MapViewListener listener)
	{
		if(mapViewListeners==null)
			mapViewListeners=new ArrayList<MapViewListener>();
		
		mapViewListeners.remove(listener);
	}
	
	public void viewMoved()
	{
		if(mapViewListeners==null)
			mapViewListeners=new ArrayList<MapViewListener>();
		
		for(MapViewListener listener:mapViewListeners)
			listener.viewMoved(view);
	}
	
	@Override
	public void cursorMoved(Point cursor)
	{
		if((cursor.x-view.x)*Main.TILESIZE>width()-1)
		{
			view.x++;
			viewMoved();
		}
		if((cursor.x-view.x)*Main.TILESIZE<0)
		{
			view.x--;
			viewMoved();
		}
		if((cursor.y-view.y)*Main.TILESIZE>height()-1)
		{
			view.y++;
			viewMoved();
		}
		if((cursor.y-view.y)*Main.TILESIZE<0)
		{
			view.y--;
			viewMoved();
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
				Unit unit=map.unit(i,a);
				int x=i;
				int y=a;
				if(unit!=null)
				{
					x=(int)(unit.point().x/Main.TILESIZE);
					y=(int)(unit.point().y/Main.TILESIZE);
				}
				if((Main.currentMode instanceof Battle==false)||((!((Battle)Main.currentMode).weather().fog())||(!((Battle)Main.currentMode).fog(x,y))))
				{
					if(unit!=null)
					{
						if(!unit.enabled())
						{
							Image unitImg=unit.image();
							BufferedImage image=new BufferedImage(unitImg.getWidth(null),unitImg.getHeight(null),BufferedImage.TYPE_INT_ARGB);
							Graphics2D gimg=image.createGraphics();
							gimg.drawImage(unitImg, 0, 0, null);
							gimg.setColor(Color.black);
							gimg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
							gimg.fillRect(0, 0, image.getWidth(), image.getHeight());
							//g.drawImage(image,i*Main.TILESIZE,a*Main.TILESIZE,null);
							g.drawImage(image,(int)unit.point().x,(int)unit.point().y,null);
						}
						else
						{
							//g.drawImage(map.unit(i, a).image(),i*Main.TILESIZE,a*Main.TILESIZE,null);
							g.drawImage(unit.image(),(int)unit.point().x,(int)unit.point().y,null);
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
	
	public void drawInfo(Map map, Graphics2D g)
	{
		Point cursor=Main.currentMode.cursor();
		
		//terrain info
		Terrain terrain=map.terrain(cursor.x, cursor.y);
		
		Composite oldComposite=g.getComposite();
		g.setColor(Color.black);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		g.fillRect(this.width()-32,this.height()-52,32,52);
		g.setComposite(oldComposite);
		
		g.drawImage(terrain.image(),this.width()-24-terrain.sprite().anchor().x,this.height()-44-terrain.sprite().anchor().y,null);
		
		//terrain type
		Image img=GameFont.fonts.get("Map Font").stringToImage(terrain.name());
		g.drawImage(img,this.width()-16-img.getWidth(null)/2, this.height()-42-img.getHeight(null),null);
		
		//terrain defence
		img=GameFont.fonts.get("Map Font").stringToImage("def "+terrain.defence());
		g.drawImage(img,this.width()-16-img.getWidth(null)/2, this.height()-22,null);
		
		
		if(terrain instanceof Building)
		{
			Building building=(Building)terrain;
			//building capture
			img=GameFont.fonts.get("Map Font").stringToImage("cap"+(building.health()<10?" ":"")+building.health());
			g.drawImage(img,this.width()-16-img.getWidth(null)/2, this.height()-12,null);
		}
		
		//unit info
		Unit unit=map.unit(cursor.x,cursor.y);
		if(Main.currentMode instanceof Battle)
			unit=((Battle)Main.currentMode).unitIfVisible(cursor.x, cursor.y);
		if(unit!=null)
		{
			oldComposite=g.getComposite();
			g.setColor(Color.black);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			g.fillRect(this.width()-64,this.height()-52,31,52);
			g.setComposite(oldComposite);
			
			g.drawImage(unit.image(),this.width()-56-unit.sprite().anchor().x,this.height()-44-unit.sprite().anchor().y,null);
			
			//unit health
			int health=(unit.health()+5)/10;
			img=GameFont.fonts.get("Map Font").stringToImage("%"+(health<10?" ":"")+health);
			g.drawImage(img,this.width()-48-img.getWidth(null)/2, this.height()-26,null);
			
			if(unit.numberOfWeapons()!=0)
			{
				//unit ammo
				img=GameFont.fonts.get("Map Font").stringToImage("@"+(unit.weapon(0).ammo()<99?" ":"")+unit.weapon(0).ammo());
				g.drawImage(img,this.width()-48-img.getWidth(null)/2, this.height()-17,null);
			}
			
			//unit fuel
			img=GameFont.fonts.get("Map Font").stringToImage("#"+(unit.fuel()<10?" ":"")+unit.fuel());
			g.drawImage(img,this.width()-48-img.getWidth(null)/2, this.height()-9,null);
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
			
			drawInfo(map,g);
			
			drawMenus(g);
		}
		
		offsetGraphics(g,false);
	}
	
	private Point view;
	
	private ArrayList<MapViewListener> mapViewListeners;
}
