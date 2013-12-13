package denaro.nick.rf4;

public class ItemAttribute
{
	public ItemAttribute(byte attribute, double value)
	{
		this.attribute=attribute;
		this.value=value;
	}
	
	private byte attribute;
	private double value;
	
	public static byte HEALTH=0;
	public static byte VITALITY=0;
	public static byte STRENGTH=0;
	public static byte DEXTERITY=0;
	public static byte INTELLIGENCE=0;
	public static byte WISDOM=0;
}
