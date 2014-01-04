package denaro.nick.wars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import denaro.nick.core.Sprite;


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
	}
	
	@Override
	public Image image()
	{
		BufferedImage image=new BufferedImage(sprite().width(),sprite().height(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=image.createGraphics();
		g.drawImage(super.image(), 0, 0, null);
		if(team!=null)
		{
			Main.swapPalette(image,team,0);
			g.drawImage(sprite().subimage(imageIndex(),1),0,0,null);
		}
		
		return(image);
	}
	
	public static Building copy(Building other, Team team)
	{
		Building building=new Building(other.name(),team);
		building.defence(other.defence());
		building.movementCosts(other.movementCosts());
		building.visionBoost(other.visionBoost());
		building.hiding(other.hiding());
		
		return(building);
	}
	
	private int health;
	private Team team;
}
