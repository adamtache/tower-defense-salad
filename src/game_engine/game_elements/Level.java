package game_engine.game_elements;

import java.util.ArrayList;
import java.util.List;

import game_engine.properties.Position;

/*
* Internal API that is used in order to represent levels within a game. More specifically, 
* this API will be responsible for dealing with transitions in between waves of enemies, as well as 
* keeping track of the order in which waves occur, and the initial conditions for waves.
*/
public class Level extends MapPiece{
	
	private Wave myCurrentWave;
	private List<Wave> myWaves;
	private Position mySpawnPosition;
	
	public Level(String name, Position spawn){
		super(name);
		setID(getWorkspace().getIDFactory().createID(this));
		mySpawnPosition = spawn;
		initialize();
	}
	
	private void initialize(){
		myWaves = new ArrayList<>();
	}
	
	/*
	* This API will allow the player to start a new wave. Returns true if next started, false if not.
	*/
	public void playNextWave(){
		checkCurrentWaveFinished();
		Wave nextWave = getNextWave();
		if(nextWave != null){
			myCurrentWave = nextWave;
		}
	}
	
	public void setCurrentWave(int wave){
		checkCurrentWaveFinished();
		myCurrentWave = myWaves.get(wave);
	}
	
	private void checkCurrentWaveFinished(){
		if(!myCurrentWave.isFinished()){
			// TODO: Throw exception "Current wave not finished"
			
		}
	}
	
	/*
	* Returns boolean value on whether or not the player has completed the current wave
	*/
	public boolean isFinished(){
		return myCurrentWave.isFinished();
	}
	
	public int wavesLeft(){
		int numWavesLeft = 0;
		for(Wave wave : myWaves){
			if(!wave.isFinished()){
				numWavesLeft++;
			}
		}
		return numWavesLeft;
	}
	
	/*
	* Allows the level to set new waves for the level 
	*/
	public void addWave(Wave newWave){
		myWaves.add(newWave);
	}
	
	public Position getSpawnPosition(){
		return mySpawnPosition;
	}
	
	private Wave getNextWave(){
		for(int x=0; x<myWaves.size(); x++){
			if(myWaves.get(x).equals(myCurrentWave)){
				int nextWaveIndex = x+1;
				waveBoundsCheck(nextWaveIndex);
				return myWaves.get(nextWaveIndex);
			}
		}
		return null;
	}
	
	private void waveBoundsCheck(int index){
		if(index < 0 || index > myWaves.size()){
			throw new IndexOutOfBoundsException();
		}
	}
	
	public String toString() {
		return getID() + ", Number of Waves: " + myWaves.size();
	}
	
	public String toFile(){
		return getID();
	}

}
