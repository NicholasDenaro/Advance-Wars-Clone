package denaro.nick.wars;

import java.util.ArrayList;

public class UnitWeapon
{
	public UnitWeapon(int weaponID)
	{
		this.weaponID=weaponID;
		effectiveAgainst=new ArrayList<Integer>();
		for(int i=0;i<Unit.numberOfAttackableUnits(weaponID);i++)
			if(Unit.baseAttack(weaponID,i)!=-1)
				effectiveAgainst.add(i);
		finalized=false;
	}
	
	public int weaponID()
	{
		return(weaponID);
	}
	
	public UnitWeapon ammo(int ammo)
	{
		if(!finalized)
			usesAmmo=true;
		this.ammo=ammo;
		this.maxAmmo=ammo;
		return(this);
	}
	
	public int ammo()
	{
		return(ammo);
	}
	
	public boolean hasAmmo()
	{
		if(!usesAmmo)
			return(true);
		return(ammo>0);
	}
	
	public void useAmmo()
	{
		if(usesAmmo)
			ammo--;
	}
	
	public void fillAmmo()
	{
		ammo=maxAmmo;
	}
	
	public UnitWeapon complete()
	{
		finalized=true;
		return(this);
	}
	
	public ArrayList<Integer> effectiveAgainst()
	{
		return(effectiveAgainst);
	}
	
	public boolean isEffectiveAggainst(Integer target)
	{
		return(effectiveAgainst.contains(target));
	}
	
	public static UnitWeapon copy(UnitWeapon other)
	{
		UnitWeapon weapon=new UnitWeapon(other.weaponID);
		weapon.finalized=other.finalized;
		weapon.usesAmmo=other.usesAmmo;
		weapon.maxAmmo=other.maxAmmo;
		weapon.ammo=other.ammo;
		weapon.effectiveAgainst=other.effectiveAgainst;
		return(weapon);
	}
	
	private ArrayList<Integer> effectiveAgainst;
	private int weaponID;
	private boolean usesAmmo;
	private int ammo;
	private int maxAmmo;
	
	private boolean finalized;
}
