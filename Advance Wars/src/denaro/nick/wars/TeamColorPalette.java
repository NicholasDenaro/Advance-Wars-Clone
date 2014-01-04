package denaro.nick.wars;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;


public class TeamColorPalette
{

	public TeamColorPalette(BufferedImage image)
	{
		colors=new Color[image.getWidth()][image.getHeight()];
		System.out.println(colors.length+", "+colors[0].length);
		for(int value=0;value<colors.length;value++)
		{
			for(int index=0;index<colors[0].length;index++)
			{
				colors[value][index]=new Color(image.getRGB(value, index));
			}
		}
	}
	
	public Color swapColor(Color color, int value, int base)
	{
		for(int i=0;i<colors[base].length;i++)
		{
			if(colors[base][i].equals(color))
				return(colors[value][i]);
		}
		return(color);
	}
	
	private Color[][] colors;
}
