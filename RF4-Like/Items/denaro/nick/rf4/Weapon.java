package denaro.nick.rf4;

import java.awt.geom.Point2D.Double;

import denaro.nick.core.Sprite;

public class Weapon extends Item
{

	public Weapon(Sprite sprite,Double point, WeaponType weapon)
	{
		super(sprite,point,ItemType.weapon);
		this.weapon=weapon;
	}
	
	public WeaponType weapon()
	{
		return(weapon);
	}
	
	private WeaponType weapon;
}

enum WeaponType{sword,axe,spear,wand,hammer,dual};