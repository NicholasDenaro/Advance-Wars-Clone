package denaro.nick.wars;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import denaro.nick.core.GameView2D;
import denaro.nick.wars.listener.BuildingListener;
import denaro.nick.wars.listener.UnitListener;
import denaro.nick.wars.menu.Menu;

public class BattleStatistics extends Menu implements UnitListener, BuildingListener
{
	
	public BattleStatistics(Menu child,Point point,Team[] teams)
	{
		super(child,point);
		this.teams=teams.clone();
		unitsSpawned=new int[teams.length][Main.unitMap.size()];
		unitKills=new int[teams.length][Main.unitMap.size()];
		unitDeaths=new int[teams.length][Main.unitMap.size()];
		buildingsCaptured=new int[teams.length][Main.terrainMap.size()];
		
		cursor(new Point(0,0));
		teamTab=0;
		infoTab=0;
	}

	public void teams(Team[] teams)
	{
		this.teams=teams;
	}
	
	public int totalDeaths(int teamid)
	{
		int count=0;
		for(int i=0;i<unitDeaths[teamid].length;i++)
			count+=unitDeaths[teamid][i];
		return(count);
	}
	
	@Override
	public int columns()
	{
		if(cursor().y==0)
			return teams.length;
		else if(cursor().y==1)
			return(3);
		return(0);
	}
	
	@Override
	public int rows()
	{
		return 2;
	}
	
	@Override
	public Image image()
	{
		GameView2D view=(GameView2D)Main.engine().view();
		BufferedImage img=new BufferedImage(view.width(),view.height(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=img.createGraphics();
		
		FontMetrics fm=g.getFontMetrics();
		
		int width=0;
		
		String action="";
		//teams
		g.setColor(Color.white);
		g.fillRect(0,0,view.width(),view.height());
		for(int i=0;i<teams.length;i++)
		{
			if(teamTab==i)
				g.setColor(Color.black);
			else
				g.setColor(Color.gray);
			action=teams[i].name();
			if(cursor().y==0&&cursor().x==i)
				action=">"+action+"< ";
			else
				action="|"+action+"| ";
			g.drawString(action,width,12);
			width+=fm.stringWidth(action);
		}
		
		width=0;
		//tab0
		if(infoTab==0)
			g.setColor(Color.black);
		else
			g.setColor(Color.gray);
		action="Unit Deaths";
		if(cursor().y==1&&cursor().x==0)
			action=">"+action+"< ";
		else
			action="|"+action+"| ";
		g.drawString(action,width,24);
		width+=fm.stringWidth(action);
		//tab1
		if(infoTab==1)
			g.setColor(Color.black);
		else
			g.setColor(Color.gray);
		action="Unit Kills";
		if(cursor().y==1&&cursor().x==1)
			action=">"+action+"< ";
		else
			action="|"+action+"| ";
		g.drawString(action,width,24);
		width+=fm.stringWidth(action);
		//tab2
		if(infoTab==2)
			g.setColor(Color.black);
		else
			g.setColor(Color.gray);
		action="Captures";
		if(cursor().y==1&&cursor().x==2)
			action=">"+action+"< ";
		else
			action="|"+action+"| ";
		g.drawString(action,width,24);
		width+=fm.stringWidth(action);
		
		//info
		int offset=0;
		int length=0;
		if(infoTab==0)
			length=unitDeaths[teamTab].length;
		else if(infoTab==1)
			length=unitKills[teamTab].length;
		else if(infoTab==2)
			length=buildingsCaptured[teamTab].length;
		for(int i=0;i<length;i++)
		{
			if(infoTab==0)
			{
				if(unitDeaths[teamTab][i]!=0)
				{
					g.drawImage(Main.unitMap.get(i).image(),0,40+(offset-1)*17,null);
					g.drawString(": "+unitDeaths[teamTab][i],Main.TILESIZE,40+offset*17-2);
					offset++;
				}
			}
			else if(infoTab==1)
			{
				if(unitKills[teamTab][i]!=0)
				{
					g.drawImage(Main.unitMap.get(i).image(),0,40+(offset-1)*17,null);
					g.drawString(": "+unitKills[teamTab][i],Main.TILESIZE,40+offset*17-2);
					offset++;
				}
			}
			else if(infoTab==2)
			{
				if(buildingsCaptured[teamTab][i]!=0)
				{
					g.drawImage(Main.terrainMap.get(i).image(),0,48+(offset-1)*24-10,null);
					g.drawString(": "+buildingsCaptured[teamTab][i],Main.TILESIZE,48+offset*24-2);
					offset++;
				}
			}
		}
		
		Main.swapPalette(img,teams[teamTab],0);
		
		return(img);
	}

	@Override
	public void unitDestroyed(Unit unit)
	{
		unitDeaths[unit.team().id()][unit.id()]++;
	}

	@Override
	public void unitKilled(Unit unit)
	{
		unitKills[unit.team().id()][unit.id()]++;
	}
	
	@Override
	public void unitCreated(Unit unit,Point location)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitAttacked(Unit unit,int damage)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void unitSpawned(Unit unit)
	{
		unitsSpawned[unit.team().id()][unit.id()]++;
	}
	
	@Override
	public void buildingCaptured(Building building,Team oldTeam,Team newTeam)
	{
		buildingsCaptured[newTeam.id()][building.id()]++;
	}
	
	public void moveCursorLeft()
	{
		super.moveCursorLeft();
		if(cursor().y==0)
			teamTab=cursor().x;
		if(cursor().y==1)
			infoTab=cursor().x;
	}
	
	public void moveCursorRight()
	{
		super.moveCursorRight();
		if(cursor().y==0)
			teamTab=cursor().x;
		if(cursor().y==1)
			infoTab=cursor().x;
	}
	
	public void moveCursorUp()
	{
		super.moveCursorUp();
		if(cursor().y==0)
			cursor().x=teamTab;
		if(cursor().y==1)
			cursor().x=infoTab;
	}
	
	public void moveCursorDown()
	{
		super.moveCursorDown();
		if(cursor().y==0)
			cursor().x=teamTab;
		if(cursor().y==1)
			cursor().x=infoTab;
	}
	
	@Override
	public void keyPressed(KeyEvent ke)
	{
		if(ke.getKeyCode()==KeyEvent.VK_UP)
			moveCursorUp();
		if(ke.getKeyCode()==KeyEvent.VK_DOWN)
			moveCursorDown();
		if(ke.getKeyCode()==KeyEvent.VK_LEFT)
			moveCursorLeft();
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT)
			moveCursorRight();
		
		if(ke.getKeyCode()==KeyEvent.VK_X)
		{
			
		}
		if(ke.getKeyCode()==KeyEvent.VK_Z)
		{
			Main.closeMenu();
		}
	}
	
	private Team[] teams;
	
	private int[][] unitDeaths;
	
	private int[][] unitKills;
	
	private int[][] buildingsCaptured;
	
	private int[][] unitsSpawned;

	private int teamTab=0;
	
	private int infoTab=0;
}
