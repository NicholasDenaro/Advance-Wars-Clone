package denaro.nick.wars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import denaro.nick.core.Pair;
import denaro.nick.core.Sprite;
import denaro.nick.wars.listener.BuildingListener;


public class Building extends Terrain
{
	
	public Building(String name, Team team)
	{
		this(name,team,Terrain.defaultMovementCosts);
	}
	
	public Building(String name, Team team, int[] movementCosts)
	{
		super(name, movementCosts);
		sprite(Sprite.sprite("Buildings"));
		this.team=team;
		health=20;
		spawnListNames=new ArrayList<String>();
		spawnListUnits=new ArrayList<Unit>();
	}
	
	public Building addBuildingListener(BuildingListener listener)
	{
		if(listener==null)
		{
			System.out.println("ERROR!!!");
			new Exception().printStackTrace();
			
		}
		if(buildingListeners==null)
			buildingListeners=new ArrayList<BuildingListener>();
		
		if(!buildingListeners.contains(listener))
			buildingListeners.add(listener);
		
		return(this);
	}
	
	public Building removeBuildingListener(BuildingListener listener)
	{
		if(buildingListeners==null)
			buildingListeners=new ArrayList<BuildingListener>();

		buildingListeners.remove(listener);
		
		return(this);
	}
	
	public void buildingCaptured(Team newTeam)
	{
		if(buildingListeners==null)
			buildingListeners=new ArrayList<BuildingListener>();
		
		for(BuildingListener listener:buildingListeners)
			listener.buildingCaptured(this, team, newTeam);
	}
	
	public void hq(boolean hq)
	{
		this.hq=hq;
	}
	
	public boolean hq()
	{
		return(hq);
	}
	
	public void health(int health)
	{
		this.health=health;
	}
	
	public int health()
	{
		return(health);
	}
	
	public void damage(int damage)
	{
		health-=damage;
	}
	
	public Team team()
	{
		return(team);
	}
	
	public void team(Team team)
	{
		this.team=team;
		image=null;
		image();
	}
	
	public void addSelling(String name, Unit unit)
	{
		spawnListNames.add(name);
		spawnListUnits.add(unit);
	}
	
	public boolean canSpawnUnits()
	{
		return(!spawnListNames.isEmpty());
	}
	
	public ArrayList<String> spawnListNames()
	{
		return(spawnListNames);
	}
	
	public ArrayList<Unit> spawnListUnits()
	{
		return(spawnListUnits);
	}
	
	@Override
	public Image image()
	{
		if(image==null)
		{
			image=new BufferedImage(sprite().width(),sprite().height(),BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=image.createGraphics();
			g.drawImage(super.image(), 0, 0, null);
			if(team!=null)
			{
				Main.swapPalette(image,team,0);
				g.drawImage(sprite().subimage(imageIndex(),1),0,0,null);
			}
			g.dispose();
		}
		return(image);
	}
	
	@Override
	public Image image(int index)
	{
		return(image());
	}
	
	public void update(Terrain terrain)
	{
		super.update(terrain);
		Building other=(Building)terrain;
		this.team=other.team;
		this.health=other.health;
		this.image=null;
	}
	
	public static Building copy(Building other, Team team)
	{
		Building building=new Building(other.name(),team);
		building.id(other.id());
		building.hq=other.hq;
		building.imageIndex(other.imageIndex());
		building.defence(other.defence());
		building.movementCosts(other.movementCosts());
		building.visionBoost(other.visionBoost());
		building.hiding(other.hiding());
		building.spawnListNames=other.spawnListNames;
		building.spawnListUnits=other.spawnListUnits;
		building.image=null;
		return(building);
	}
	
	private int health;
	private Team team;
	private boolean hq;
	
	private BufferedImage image;
	private ArrayList<String> spawnListNames;
	private ArrayList<Unit> spawnListUnits;
	
	private ArrayList<BuildingListener> buildingListeners;
}
