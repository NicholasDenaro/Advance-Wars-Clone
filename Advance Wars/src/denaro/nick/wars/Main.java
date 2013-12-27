package denaro.nick.wars;

import denaro.nick.core.GameEngineByTick;
import denaro.nick.core.GameFrame;
import denaro.nick.core.GameView2D;


public class Main
{
	public static void main(String[] args)
	{
		engine=(GameEngineByTick)GameEngineByTick.instance();
		engine.setTicksPerSecond(60);
		engine.setFramesPerSecond(60);
		GameView2D view=new GameView2D(240,160,1,1);
		engine.view(view);
		
		GameFrame frame=new GameFrame("Game",engine);
		frame.setVisible(true);
		
	}
	
	public static GameEngineByTick engine()
	{
		return(engine);
	}
	
	private static GameEngineByTick engine;
}
