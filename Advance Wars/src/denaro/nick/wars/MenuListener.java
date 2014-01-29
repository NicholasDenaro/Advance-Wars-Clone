package denaro.nick.wars;

import denaro.nick.wars.menu.Menu;

public interface MenuListener
{
	public void buttonPressed(Menu menu);
	
	public void menuClosed(Menu menu);
}
