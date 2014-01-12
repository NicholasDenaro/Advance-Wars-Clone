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
	}
	
	public int weaponID()
	{
		return(weaponID);
	}
	
	public UnitWeapon ammo(int ammo)
	{
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
		return(!usesAmmo||ammo>0);
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
}
