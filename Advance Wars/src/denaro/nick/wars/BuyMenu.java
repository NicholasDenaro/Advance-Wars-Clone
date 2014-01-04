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
			prices[i]=building.spawnListPrices().get(i);
		}
		
		cursor=0;
	}
	
	public int cursor()
	{
		return(cursor);
	}
	
	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			cursor=(--cursor+actions.length)%actions.length;
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			cursor=++cursor%actions.length;
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			if(Main.battle.whosTurn().funds()>=prices[cursor])
			{
				Main.battle.whosTurn().addFunds(-prices[cursor]);
				Main.battle.map().addUnit(Unit.copy(units[cursor],Main.battle.whosTurn()), Main.battle.map().cursor().x, Main.battle.map().cursor().y);
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
		
		Sprite sprite=Sprite.sprite("Buy Menu");
		
		BufferedImage image=new BufferedImage(sprite.width(),sprite.height(),BufferedImage.TYPE_INT_ARGB);
		if(child()!=null)
			return(image);
		Graphics2D g=image.createGraphics();
		g.drawImage(sprite.subimage(0), 0, 0, null);
		Main.swapPalette(image, Main.battle.whosTurn(), 0);
		g.setFont(new Font(g.getFont().getName(),Font.BOLD,13));
		
		FontMetrics fm=g.getFontMetrics();
		
		g.setColor(Color.black);

		for(int i=0;i<actions.length;i++)
		{
			if(Main.battle.whosTurn().funds()<prices[i])
				g.drawImage(units[i].image(), 4, 10+i*18, null);
			else
			{
				units[i].enabled(true);
				g.drawImage(units[i].image(), 4, 10+i*18, null);
				units[i].enabled(false);
			}
			if(Main.battle.whosTurn().funds()<prices[i])
				g.setColor(Color.gray);
			else
				g.setColor(Color.black);
			g.drawString(actions[i], 20, 10+i*18+fm.getHeight()-2);
			g.drawString(""+prices[i], 120-fm.stringWidth(""+prices[i]), 10+i*18+fm.getHeight()-2);
			
		}
		
		g.setColor(Color.black);
		g.drawRect(0, 10+cursor*18-1, sprite.width()-1, 18);
		
		return(image);
	}
	
	private String[] actions;
	private Unit[] units;
	private int[] prices;
	private int cursor;
}
