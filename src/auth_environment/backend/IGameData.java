package auth_environment.backend;

import java.util.List;

import game_engine.game_elements.Level;
import game_engine.game_elements.Tower;

public interface IGameData {
	
	public List<Tower> getTowers();
	
	public Level getLevel(); 
	
	public void setTowers(List<Tower> towers);
	
	public void addTower(Tower tower);
	
	public void addLevel(Level level); 
	
}