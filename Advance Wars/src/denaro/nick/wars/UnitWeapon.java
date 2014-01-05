package denaro.nick.wars;

import java.util.ArrayList;

public class UnitWeapon
{
	public UnitWeapon(int weaponID, int... targets)
	{
		this.weaponID=weaponID;
		effectiveAgainst=new ArrayList<Integer>();
		for(int i:targets)
			effectiveAgainst.add(i);
	}
	
	public int weaponID()
	{
		return(weaponID);
	}
	
	public ArrayList<Integer> effectiveAgainst()
	{
		return(effectiveAgainst);
	}
	
	public boolean isEffectiveAggainst(Integer target)
	{
		return(effectiveAgainst.contains(target));
	}
	
	private ArrayList<Integer> effectiveAgainst;
	private int weaponID;
}
