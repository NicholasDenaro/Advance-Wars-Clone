package denaro.nick.wars;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import denaro.nick.core.Sprite;

public class BuyMenu extends Menu
{

	public BuyMenu(Menu child, Point point, Building building)
	{
		super(child, point);
		
		actions=new String[building.spawnListNames().size()];
		units=new Unit[building.spawnListNames().size()];
		prices=new int[building.spawnListNames().size()];
		
		for(int i=0;i<building.spawnListNames().size();i++)
		{
			actions[i]=building.spawnListNames().get(i);
			units[i]=building.spawnListUnits().get(i);
			prices[i]=units[i].cost();
		}
		
		cursor(new Point(0,0));
		show=0;
	}
	
	public int rows()
	{
		return(actions.length);
	}
	
	public int columns()
	{
		return(0);
	}
	
	@Override
	public void moveCursorUp()
	{
		super.moveCursorUp();
		if(cursor().y<show)
			show=cursor().y;
	}
	
	@Override
	public void moveCursorDown()
	{
		super.moveCursorDown();
		if(cursor().y>=show+6)
			show++;
	}
	
	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			moveCursorUp();
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			moveCursorDown();
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			Battle battle=(Battle)Main.currentMode;
			if(battle.whosTurn().funds()>=prices[cursor().y])
			{
				battle.whosTurn().addFunds(-prices[cursor().y]);
				Unit adding=Unit.copy(units[cursor().y],battle.whosTurn());
				battle.spawnUnit(adding, battle.cursor());
				Main.closeMenu();
			}
		}
		
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
		}
	}
	
	@Override
	public Image image()
	{
		Battle battle=(Battle)Main.currentMode;
		
		Sprite sprite=Sprite.sprite("Buy Menu");
		
		BufferedImage image=new BufferedImage(sprite.width(),sprite.height(),BufferedImage.TYPE_INT_ARGB);
		if(child()!=null)
			return(image);
		Graphics2D g=image.createGraphics();
		g.drawImage(sprite.subimage(0), 0, 0, null);
		Main.swapPalette(image, battle.whosTurn(), 0);
		g.setFont(new Font(g.getFont().getName(),Font.BOLD,13));
		
		FontMetrics fm=g.getFontMetrics();
		
		g.setColor(Color.black);

		for(int i=show;i<Math.min(show+6,actions.length);i++)
		{
			g.drawImage(units[i].image(), 4, 10+(i-show)*18, null);
			
			if(battle.whosTurn().funds()<prices[i])
			{
				Main.swapPalette(image, null, 1);
			}
			else
			{
				Main.swapPalette(image, battle.whosTurn(), 1);
			}
			
			if(battle.whosTurn().funds()<prices[i])
				g.setColor(Color.gray);
			else
				g.setColor(Color.black);
			g.drawString(actions[i], 20, 10+(i-show)*18+fm.getHeight()-2);
			g.drawString(""+prices[i], 120-fm.stringWidth(""+prices[i]), 10+(i-show)*18+fm.getHeight()-2);
			
		}
		
		g.setColor(Color.black);
		g.drawRect(0, 10+(cursor().y-show)*18-1, sprite.width()-1, 18);
		
		return(image);
	}
	
	private int show;
	private String[] actions;
	private Unit[] units;
	private int[] prices;
}
