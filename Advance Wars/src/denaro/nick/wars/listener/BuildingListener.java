package denaro.nick.wars.listener;

import denaro.nick.wars.Building;
import denaro.nick.wars.Team;

public interface BuildingListener
{
	public void buildingCaptured(Building building,Team oldTeam, Team newTeam);
}
