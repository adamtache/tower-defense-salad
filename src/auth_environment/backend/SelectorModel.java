package auth_environment.backend;

import java.util.Collection;
import java.util.List;

import game_engine.properties.Position;

/**
 * Created by BrianLin on 4/9/16.
 * Team member responsible: Brian
 *
 * This class holds the selected Game Element index and the coordinates of the selected tile. 
 * These values will be passed to the Backend using the getter methods.  
 * 
 * Pass this class to anything that will modify the selected Element or Coordinates. 
 */

public class SelectorModel implements ISelector {
	
	private int elementIndex;
	private List<Position> myPositions; 
	
	public SelectorModel() {}; 

	@Override
	public void chooseElement(int index) {
		this.elementIndex = index; 
	}

	@Override
	public int getElementIndex() {
		return this.elementIndex;
	}

	@Override
	public void printPositions() {
		this.myPositions.stream().forEach(s -> System.out.println("(" + s.getX() + "," + s.getY()+ ")"));
	}

	@Override
	public void printIndex() {
		System.out.println("GameElement index of: " + this.elementIndex);
	}

	@Override
	public void choosePosition(double x, double y) {
		this.myPositions.add(new Position(x,y));
	}

	@Override
	public Collection<Position> getPositions() {
		return this.myPositions;
	}

}
