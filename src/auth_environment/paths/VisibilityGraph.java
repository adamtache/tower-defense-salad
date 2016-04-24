package auth_environment.paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import game_engine.GameEngineInterface;
import game_engine.game_elements.Branch;
import game_engine.game_elements.Unit;
import game_engine.physics.CollisionDetector;
import game_engine.physics.EncapsulationChecker;
import game_engine.properties.Position;

public class VisibilityGraph {

	private EncapsulationChecker myEncapsulator;
	private GameEngineInterface myEngine;

	public VisibilityGraph(GameEngineInterface engine){
		myEncapsulator = new EncapsulationChecker();
		myEngine = engine;
	}

	public List<Branch> getVisibilityBranches() {
		return getVisibilityBranches(myEngine.getTowers());
	}

	public List<Branch> getSimulatedPlacementBranches(Unit obstacle){
		List<Unit> obstacles = new ArrayList<>();
		obstacles.add(obstacle);
		obstacles.addAll(myEngine.getTowers());
		return getVisibilityBranches(obstacles);
	}

	public boolean isValidMap(List<Branch> visibilityBranches){
		for(Position goal : myEngine.getCurrentLevel().getGoals()){
			for(Position spawn : myEngine.getCurrentLevel().getSpawns()){
				if(!BFSPossible(visibilityBranches, spawn, goal)){
					System.out.println("NOT VALIDATED");
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isAccessibleFrom(Branch b, Position p){
		List<Branch> visibilityBranches = getVisibilityBranches();
		return BFSPossible(visibilityBranches, b.getFirstPosition(), p);
	}

	private List<Branch> getVisibilityBranches(List<Unit> obstacles){
		List<Branch> branchesToFilter = getBranchesToFilter(obstacles);
		List<Branch> copyBranchesToFilter = branchesToFilter.stream().map(b -> b.copyBranch()).collect(Collectors.toList());
		PathGraph pg = new PathGraph(myEngine.getBranches());
		List<Branch> branches = pg.copyGraph().getBranches();
		List<Branch> copyBranches = branches.stream().map(b -> b.copyBranch()).collect(Collectors.toList());
		for(int y=0; y<copyBranchesToFilter.size(); y++){
			for(int x=0; x<copyBranches.size(); x++){
				if(copyBranchesToFilter.get(y).equals(copyBranches.get(x))){
					Branch removed = copyBranches.remove(x);
					x--;
					for(Branch b : copyBranches){
						for(int z=0; z < b.getNeighbors().size(); z++){
							if(b.getNeighbors().get(z).equals(removed)){
								b.getNeighbors().remove(z);
								z--;
							}
						}
					}
				}
			}	
		}
		return copyBranches;
	}

	private List<Branch> getBranchesToFilter(List<Unit> obstacles){
		Set<Branch> removalList = new HashSet<>();
		List<Branch> copyBranches =  myEngine.getBranches().stream().map(b -> b.copyBranch()).collect(Collectors.toList());
		List<Unit> obstacleList = obstacles;
		List<Unit> copyObstacleList = obstacleList.stream().map(o -> o.copyShallowUnit()).collect(Collectors.toList());
		for(Unit o : copyObstacleList){
			for(Branch b : copyBranches){
				for(Position pos : b.getPositions()){
					if(myEncapsulator.encapsulatesBounds(Arrays.asList(pos), CollisionDetector.getUseableBounds(o.getProperties().getBounds(), o.getProperties().getPosition()))){
//						System.out.println("POS: "+pos+" BOUNDS: " + CollisionDetector.getUseableBounds(o.getProperties().getBounds(), pos));
						removalList.add(b);
					}
				}
			}
		}
		return new ArrayList<Branch>(removalList);
	}

	private boolean BFSPreCheck(List<Branch> visibilityBranches, Position spawn){
		Branch start = myEngine.findBranchForSpawn(spawn);
		boolean contained = false;
		for(Branch v : visibilityBranches){
			if(v.equals(start)){
				contained = true;
			}
		}
		return contained;
	}

	private boolean BFSPossible(List<Branch> visibilityBranches, Position spawn, Position goal){
		if(!BFSPreCheck(visibilityBranches, spawn)){
			return false;
		}
		Branch start = myEngine.findBranchForSpawn(spawn);
		Branch copyStart = start.copyBranch();
		Queue<Branch> queue = new LinkedList<>();
		List<Branch> visited = new ArrayList<>();
		queue.add(copyStart);
		while(!queue.isEmpty()){
			Branch branch = (Branch) queue.remove();
			Branch child = null;
			while((child = getUnvisitedChildNode(branch, visited, visibilityBranches)) != null){
				visited.add(child);
				queue.add(child);
			}
		}
		for(Branch b : visited){
			if(b.getPositions().contains(goal)){
				return true;
			}
		}
		return false;
	}

	private Branch getUnvisitedChildNode(Branch branch, List<Branch> visited, List<Branch> visible) {
		List<Branch> neighbors = branch.getNeighbors();
		List<Branch> visibleNeighbors = new ArrayList<>();
		for(Branch b : neighbors){
			for(Branch v : visible){
				if(!visited.contains(b)){
					if(b.equals(v)){
						visibleNeighbors.add(b);
					}
				}
			}
		}
		if(visibleNeighbors.size() == 0){
			return null;
		}
		return visibleNeighbors.get(0);
	}

}