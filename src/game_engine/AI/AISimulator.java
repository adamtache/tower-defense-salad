package game_engine.AI;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import game_engine.GameEngineInterface;
import game_engine.game_elements.Branch;
import game_engine.game_elements.Unit;
import game_engine.handlers.VisibilityHandler;
import game_engine.interfaces.ICollisionDetector;
import game_engine.physics.CollisionDetector;
import game_engine.properties.Position;


/**
 * This class is a utility Artificial Intelligence simulator that allows for simulated search
 * problems using visibility graphs and the AI searcher.
 * A simulated search problem runs a simulation on copies of game engine objects and tests for
 * collisions and valid path finding.
 * 
 * @author adamtache
 *
 */

public class AISimulator {

	private GameEngineInterface myEngine;
	private AISearcher myAISearcher;
	private AIHandler myAIHandler;
	private VisibilityHandler myVisibility;
	private ICollisionDetector collisionDetector;

	public AISimulator(GameEngineInterface engine){
		this.myEngine = engine;
		this.myAISearcher = engine.getAIController().getAISearcher();
		this.myAIHandler = engine.getAIController().getAIHandler();
		this.myVisibility = new VisibilityHandler(engine);
		collisionDetector = new CollisionDetector(engine);
	}

	public boolean simulateTowerPlacement(Unit obstacle) {
		List<Position> visibleNodes = myVisibility.getVisibleNodes(obstacle);
		List<Unit> activeAI = myAIHandler.getActiveAIEnemies();
		if(activeAI.size() == 0){
			return true;
		}
		List<Position> goals = myEngine.getLevelController().getCurrentLevel().getGoals();
		BFSTuple myBFS = myAISearcher.getBFSTuple(goals, visibleNodes);
		if(!myAISearcher.isValidSearch(myBFS)){
			return false;
		}
		HashMap<Unit, List<Branch>> newPaths = new HashMap<>();
		for(Unit e : activeAI){
			Branch currBranch = e.getProperties().getMovement().getCurrentBranch();
			Position currPos = e.getProperties().getPosition();
			Position movingTowards = e.getProperties().getMovement().getMovingTowards();
			List<Branch> newPath = movingTowards.equals(currBranch.getLastPosition()) ? myBFS.getPathTo(currBranch.getFirstPosition(), currBranch, currPos) : myBFS.getPathTo(currBranch.getLastPosition(), currBranch, currPos);
			if(newPath == null || simulatedCollision(e, newPath, obstacle)){
				return false;
			}
			else{
				newPaths.put(e, newPath);
			}
		}
		myAIHandler.getActiveAIEnemies().stream().forEach(e -> e.getProperties().getMovement().setBranches(newPaths.get(e)));
		return true;
	}

	private boolean simulatedCollision (Unit enemy, List<Branch> newBranches, Unit obstacle) {
		List<Unit> obstacles = myEngine.getUnitController().getUnitType("Tower");
		List<Unit> obstaclesCopy =
				obstacles.stream().map(o -> o.copyShallowUnit()).collect(Collectors.toList());
		obstaclesCopy.add(obstacle.copyShallowUnit());
		for (Branch b : newBranches) {
			for (Position pos : b.getPositions()) {
				List<Position> enemyBounds =
						enemy.getProperties().getBounds().getUseableBounds(pos);
				if (collisionDetector.simulatedObstacleCollisionCheck(enemyBounds, obstaclesCopy)) {
					return true;
				}
			}
		}
		return false;
	}

}