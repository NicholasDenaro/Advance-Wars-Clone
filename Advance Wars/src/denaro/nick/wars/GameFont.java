package denaro.nick.wars;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GameFont
{
	public GameFont(String name, BufferedImage image, int width, int height)
	{
		this.name=name;
		this.image=image;
		this.width=width;
		this.height=height;
		
		letters=new HashMap<Character, BufferedImage>();
		words=new HashMap<String, BufferedImage>();
		
		for(char ch='a';ch<='z';ch++)
		{
			letters.put(ch, trimImage(characterToImage(ch)));
		}
		
		for(char ch='0';ch<='9';ch++)
		{
			letters.put(ch, trimImage(characterToImage(ch)));
		}
		
		letters.put(' ', trimImage(characterToImage(' ')));
		letters.put('%', trimImage(characterToImage('%')));
		letters.put('@', trimImage(characterToImage('@')));
		letters.put('#', trimImage(characterToImage('#')));
		letters.put('*', trimImage(characterToImage('*')));
		letters.put('+', trimImage(characterToImage('+')));
		
		fonts.put(name, this);
	}
	
	private BufferedImage characterToImage(char ch)
	{
		int windex=(image.getWidth()/(width));
		if(ch==' ')
			return(image.getSubimage((26+9+1)%windex*width, (26+9+1)/windex*height, width, height));
		if(ch=='%')
			return(image.getSubimage((26+9+2)%windex*width, (26+9+2)/windex*height, width, height));
		if(ch=='@')
			return(image.getSubimage((26+9+3)%windex*width, (26+9+3)/windex*height, width, height));
		if(ch=='#')
			return(image.getSubimage((26+9+4)%windex*width, (26+9+4)/windex*height, width, height));
		if(ch=='*')
			return(image.getSubimage((26+9+6)%windex*width, (26+9+6)/windex*height, width, height));
		if(ch=='+')
			return(image.getSubimage((26+9+7)%windex*width, (26+9+7)/windex*height, width, height));
		if(ch>='a'&&ch<='z')
			return(image.getSubimage(((ch-'a')%windex)*width, ((ch-'a')/windex)*height, width, height));
		if(ch>='0'&&ch<='9')
			return(image.getSubimage(((26+ch-'0')%windex)*width, ((26+ch-'0')/windex)*height, width, height));
		else
			return(null);
	}
	
	private BufferedImage trimImage(BufferedImage trimImage)
	{
		int imgWidth=-1;
		for(int i=trimImage.getWidth()-1;i>=0&&imgWidth==-1;i--)
		{
			for(int a=0;a<trimImage.getHeight();a++)
			{
				if(new Color(trimImage.getRGB(i,a),true).getAlpha()!=0)
				{
					imgWidth=i+1;
					break;
				}
			}
		}
		if(imgWidth==-1)
			imgWidth=8;
		BufferedImage img=new BufferedImage(imgWidth,trimImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g=img.createGraphics();
		g.drawImage(trimImage, 0, 0, null);
		
		return(img);
	}
	
	public Image stringToImage(String string)
	{
		string=string.toLowerCase();
		if(words.get(string)==null)
		{
			//System.out.println(string);
			BufferedImage stringImage=new BufferedImage(string.length()*width,height,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g=stringImage.createGraphics();
			int currentWidth=0;
			for(int i=0;i<string.length();i++)
			{
				BufferedImage img=letters.get(string.charAt(i));
				//System.out.println(currentWidth);
				g.drawImage(img, currentWidth, 0, null);
				currentWidth+=img.getWidth();
			}
			words.put(string,trimImage(stringImage));
			return(words.get(string));
			//return(trimImage(stringImage));
		}
		else
			return(words.get(string));
	}
	
	public static HashMap<String,GameFont> fonts=new HashMap<String,GameFont>();
	
	private HashMap<Character,BufferedImage> letters;
	private HashMap<String,BufferedImage> words;
	private String name;
	private BufferedImage image;
	private int width;
	private int height;
}
