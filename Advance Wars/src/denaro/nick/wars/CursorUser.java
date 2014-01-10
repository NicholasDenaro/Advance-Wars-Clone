package denaro.nick.wars;

import java.awt.Point;
import java.util.ArrayList;

public abstract class CursorUser
{
	public abstract int columns();
	
	public abstract int rows();
	
	public void cursor(Point point)
	{
		cursor=point;
	}
	
	public Point cursor()
	{
		return(cursor);
	}
	
	public void addCursorListener(CursorListener listener)
	{
		if(cursorListeners==null)
			cursorListeners=new ArrayList<CursorListener>();
		if(!cursorListeners.contains(listener))
			cursorListeners.add(listener);
	}
	
	public void removeCursorListener(CursorListener listener)
	{
		if(cursorListeners==null)
			cursorListeners=new ArrayList<CursorListener>();
		cursorListeners.remove(listener);
	}
	
	public void updateCursorListeners()
	{
		if(cursorListeners==null)
			cursorListeners=new ArrayList<CursorListener>();
		for(CursorListener listener:cursorListeners)
		{
			listener.cursorMoved(cursor());
		}
	}
	
	public void moveCursorLeft()
	{
		if(cursor.x>0)
			cursor.x--;
		updateCursorListeners();
	}
	
	public void moveCursorRight()
	{
		if(cursor.x<columns()-1)
			cursor.x++;
		updateCursorListeners();
	}
	
	public void moveCursorUp()
	{
		if(cursor.y>0)
			cursor.y--;
		updateCursorListeners();
	}
	
	public void moveCursorDown()
	{
		if(cursor.y<rows()-1)
			cursor.y++;
		updateCursorListeners();
	}
	
	private Point cursor;
	
	private ArrayList<CursorListener> cursorListeners;
}
