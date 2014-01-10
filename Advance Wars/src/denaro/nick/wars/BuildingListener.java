package denaro.nick.wars;

public interface BuildingListener
{
	public void buildingCaptured(Building building,Team oldTeam, Team newTeam);
}
