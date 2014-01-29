package denaro.nick.wars;

public class TerrainDirections
{

	public TerrainDirections(Map map)
	{
		directions=new int[map.width()][map.height()];
		for(int a=0;a<map.height();a++)
		{
			for(int i=0;i<map.width();i++)
			{
				directions[i][a]=-1;
			}
		}
	}
	
	public void changeDirection(int direction, int x, int y)
	{
		directions[x][y]=direction;
	}
	
	public int direction(int x, int y)
	{
		return(directions[x][y]);
	}
	
	private int[][] directions;
}
