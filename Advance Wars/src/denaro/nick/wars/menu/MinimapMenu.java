package denaro.nick.wars.menu;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import denaro.nick.wars.GameModeSelector;
import denaro.nick.wars.Main;

public class MinimapMenu extends Menu
{
	
	public MinimapMenu(Menu child,Point point)
	{
		super(child,point);
		origin=new Point(point.x,point.y);
	}

	@Override
	public int columns()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int rows()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Image image()
	{
		if(Main.currentMode instanceof GameModeSelector)
		{
			BufferedImage image=Main.loadMap(((GameModeSelector)Main.currentMode).action(((GameModeSelector)Main.currentMode).cursor().y)).minimap();
			point().x=origin.x-image.getWidth()/2;
			point().y=origin.y-image.getHeight()/2;
			return(image);
		}
		else
			return(null);
	}
	
	private Point origin;
}
