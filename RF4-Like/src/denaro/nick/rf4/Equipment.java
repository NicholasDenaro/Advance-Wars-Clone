package denaro.nick.rf4;

public class Equipment
{
	public Equipment()
	{
		
	}
	
	public void equip(Item item)
	{
		if(item.type()==ItemType.head)
			head=item;
		else if(item.type()==ItemType.weapon)
			weapon=(Weapon)item;
	}
	
	public Weapon weapon()
	{
		return(weapon);
	}
	
	private Item head;
	private Item torso;
	private Item feet;
	private Item accessory;
	private Weapon weapon;
	private Item shield;
}
