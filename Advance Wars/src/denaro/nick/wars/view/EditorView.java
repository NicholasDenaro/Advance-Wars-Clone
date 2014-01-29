package denaro.nick.wars.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import denaro.nick.core.GameView;
import denaro.nick.core.Sprite;
import denaro.nick.wars.Main;
import denaro.nick.wars.MapEditor;

public class EditorView extends MapView
{

	public EditorView(int width, int height, double hscale, double vscale)
	{
		super(width, height, hscale, vscale);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void drawCursor(Graphics2D g)
	{
		Sprite cursor=Sprite.sprite("Cursor");
		g.drawImage(cursor.subimage(0),Main.currentMode.cursor().x*Main.TILESIZE-cursor.anchor().x, Main.currentMode.cursor().y*Main.TILESIZE-cursor.anchor().y,null);
		
		if(Main.currentMode instanceof MapEditor)
		{
			MapEditor editor=(MapEditor)Main.currentMode;
			if(editor.selected()!=null)
			{
				BufferedImage image=new BufferedImage(editor.selected().getWidth(null),editor.selected().getHeight(null),BufferedImage.TYPE_INT_ARGB);
				Graphics2D gimg=image.createGraphics();
				gimg.drawImage(editor.selected(), 0, 0, null);
				Main.swapPalette(image, Main.teamMap.get(editor.selectedTeam()), 0);
				g.drawImage(image,Main.currentMode.cursor().x*Main.TILESIZE+(int)(0.75*Main.TILESIZE), Main.currentMode.cursor().y*Main.TILESIZE+(int)(0.75*Main.TILESIZE),null);
			}
		}
	}
}
