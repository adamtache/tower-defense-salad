package game_engine.affectors;

import java.util.ArrayList;
import java.util.List;

import game_engine.functions.Function;
import game_engine.game_elements.Unit;
import game_engine.games.IPlayerEngineInterface;
import game_engine.properties.UnitProperties;
import game_engine.timelines.EndEvent;

public class Affector {

	private List<Double> baseNumbers;
	// this specifies how many ticks the affector applies its effect (it's "time to live")
	private int TTL;
	private int elapsedTime;
	private List<Function> myFunctions;
	private IPlayerEngineInterface engineWorkspace;
//	private List<EndEvent> myEndEvents;

	/**
	 * Applies an effect to a unit by altering the 
	 * UnitProperties of a GameElement object. The effect is determined
	 * by the implementation of the method (this could involve)
	 * decrementing health, increasing/decreasing speed, etc.
	 * The overall effect is dependent on which properties are changed
	 *
	 * @param  properties  A UnitProperties object that represents the current state of the GameElement 
	 *
	 *
	 */

	public Affector(List<Function> functions){
		this.myFunctions = functions;
		this.elapsedTime = 0;
//		myEndEvents = new ArrayList<>();
	}
	
//	public Affector(List<Function> functions, List<EndEvent> endEvents){
//		this.myFunctions = functions;
//		this.myEndEvents = endEvents;
//		this.elapsedTime = 0;
//	}
	
	public void setWorkspace(IPlayerEngineInterface workspace){
		this.engineWorkspace = workspace;
	}

	public Affector(){
		this.elapsedTime = 0;
	}

	public Affector copyAffector() {
		//may need to copy functions too
		Affector copy = null;
		try {
			copy = (Affector) Class.forName(this.getClass().getName())
					.getConstructor(List.class)
					.newInstance(this.getFunctions());
			copy.setWorkspace(this.getEngineWorkspace());
//			copy.setEndEvents(this.myEndEvents);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		copy.setBaseNumbers(this.getBaseNumbers());
		copy.setTTL(this.getTTL());
		return copy;
	}

	public void apply(Unit u) {
//		for(EndEvent endEvent : myEndEvents){
//			if(endEvent.checkEvent(u))
//				setElapsedTimeToDeath();
//		}
		updateElapsedTime();
	}

	public int getElapsedTime(){
		return elapsedTime;
	}

	public void updateElapsedTime(){
		elapsedTime++;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public List<Double> getBaseNumbers () {
		return baseNumbers;
	}

	public void setBaseNumbers (List<Double> baseNumbers) {
		this.baseNumbers = baseNumbers;
	}

	public int getTTL () {
		return TTL;
	}

	public void setElapsedTimeToDeath() {
		this.setElapsedTime(this.getTTL());
	}

	public void setTTL(int TTL) {
		this.TTL = TTL;
	}

	public List<Function> getFunctions(){
		return myFunctions;
	}

	public IPlayerEngineInterface getEngineWorkspace() {
		return engineWorkspace;
	}

	public void setEngineWorkspace(IPlayerEngineInterface engineWorkspace) {
		this.engineWorkspace = engineWorkspace;
	}
	
	public boolean collisionDeath(Unit unit){
		if(unit.hasCollided()){
			this.setElapsedTimeToDeath();
			return true;
		}
		return false;
	}
	
//	public void setEndEvents(List<EndEvent> endEvents){
//		this.myEndEvents = endEvents;
//	}
//	
//	public void addEndEvent(EndEvent endEvent){
//		this.myEndEvents.add(endEvent);
//	}

}