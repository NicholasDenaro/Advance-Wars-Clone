package denaro.nick.wars.menu;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import denaro.nick.core.Sprite;
import denaro.nick.wars.Main;
import denaro.nick.wars.Terrain;

public class TerrainDirectionSelectionMenu extends SelectionMenu
{
	
	public TerrainDirectionSelectionMenu(Menu child, Point point,Terrain terrain)
	{
		super(child,point);
		directions=new ArrayList<Integer>();
		for(int e=0;e<terrain.tileMap().size();e++)
		{
			for(int a=0;a<terrain.tileMap().get(e)[0].length;a++)
			{
				for(int i=0;i<terrain.tileMap().get(e).length;i++)
				{
					directions.add(terrain.tileMap().get(e)[i][a]);
				}
			}
		}
		cursor(new Point(0,0));
		selectionHeight(directions.size()/columns()+1);
	}
	
	@Override
	public int getSelection()
	{
		if(cursor().x+cursor().y*columns()<directions.size())
			return(directions.get(cursor().x+cursor().y*columns()));
		else
			return(-1);
		
	}
	
	@Override
	public void drawSelections(BufferedImage image, Graphics2D g)
	{
		for(int a=0;a<rows();a++)
		{
			for(int i=0;i<columns();i++)
			{
				if(i+a*columns()<directions.size())
					g.drawImage(Sprite.sprite("Terrain").subimage(directions.get(i+a*columns())), i*(Main.TILESIZE+4)+4-Sprite.sprite("Terrain").anchor().x, a*(Main.TILESIZE+4)+4-Sprite.sprite("Terrain").anchor().y, null);
			}
		}
	}
	
	private ArrayList<Integer> directions;
}
