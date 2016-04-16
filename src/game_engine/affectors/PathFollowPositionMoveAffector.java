package game_engine.affectors;

import java.util.List;
import game_engine.functions.Function;
import game_engine.game_elements.Branch;
import game_engine.game_elements.Unit;
import game_engine.properties.Movement;
import game_engine.properties.Position;


/*
 * This class modifies units based on their set speed, as well as the path that they are supposed to
 * follow.
 * This is done by calling the apply method repeatedly based on their speed, and changing the
 * position of the unit
 * based on a sampled version of the path that has been drawn out for them.
 * 
 */
public class PathFollowPositionMoveAffector extends Affector {

	public PathFollowPositionMoveAffector(List<Function> functions){
		super(functions);
	}

	@Override
	public void apply (Unit u) {
		super.apply(u);
		if (this.getElapsedTime() <= this.getTTL()) {
			double speed = u.getProperties().getVelocity().getSpeed();
			Movement move = u.getProperties().getMovement();
			for (int i = 0; i < speed; i++) {
				Position next = getNextPosition(u);
				if(next == null){
					u.kill();
					setElapsedTimeToDeath();
					return;
				}
				u.getProperties().getPosition().setX(next.getX());
				u.getProperties().getPosition().setY(next.getY());
				u.getProperties().getVelocity().setDirection(getNextDirection(u));
			}
			this.updateElapsedTime();
		}
	}
	
	public Position getNextPosition(Unit u){
		Position currentPosition = u.getProperties().getPosition();
		Movement move = u.getProperties().getMovement();
		Branch currentBranch = move.getCurrentBranch();
		if(currentBranch == null){
			System.out.println("Workspace: " + getEngineWorkspace());
			getEngineWorkspace().decrementLives();
			return null;
		}
		Position next = currentBranch.getNextPosition(currentPosition);
		if(next == null){
			currentBranch = move.getNextBranch();
			if(currentBranch == null) {
				getEngineWorkspace().decrementLives();
				return null;
			}
			next = currentBranch.getFirstPosition();
		}
		return next;
	}

	public Double getNextDirection(Unit u){
		Position currentPosition = u.getProperties().getPosition();
		Movement move = u.getProperties().getMovement();
		if(currentPosition.equals(move.getLastBranch().getLastPosition())) {
			// END OF PATH
			return u.getProperties().getVelocity().getDirection();
		}
		return move.getCurrentBranch().getNextDirection(currentPosition);
	}

}