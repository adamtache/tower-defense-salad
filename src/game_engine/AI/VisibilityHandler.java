package game_engine.AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import auth_environment.paths.PathGraph;
import game_engine.GameEngineInterface;
import game_engine.game_elements.Branch;
import game_engine.game_elements.Unit;
import game_engine.physics.EncapsulationChecker;
import game_engine.properties.Position;


/**
 * This class is a visibility handler that allows access to visibility graphs and visibility
 * utilities of valid search problems.
 * A visibility graph is represented by branches valid after the consideration of obstacles.
 * 
 * @author adamtache
 *
 */

public class VisibilityHandler {

    private GameEngineInterface myEngine;

    public VisibilityHandler (GameEngineInterface engine) {
        myEngine = engine;
    }

    public List<Branch> getVisibilityBranches () {
        return getVisibilityBranches(myEngine.getUnitController().getUnitType("Tower"));
    }

    public List<Branch> getVisibilityBranches (Unit obstacle) {
        List<Unit> obstacles =
                myEngine.getUnitController().getUnitType("Tower").stream().map(t -> t.copyShallowUnit())
                        .collect(Collectors.toList());
        obstacles.add(obstacle);
        return getVisibilityBranches(obstacles);
    }

    private List<Branch> getVisibilityBranches (List<Unit> obstacles) {
        return filterObstacles(getFilteredBranches(obstacles));
    }

    private List<Branch> getFilteredBranches (List<Unit> obstacles) {
        List<Branch> branchesToFilter = getBranchesToFilter(obstacles);
        List<Branch> copyBranchesToFilter =
                branchesToFilter.stream().map(b -> b.copyBranch()).collect(Collectors.toList());
        return copyBranchesToFilter;
    }

    private List<Branch> filterObstacles (List<Branch> branchesToFilter) {
        PathGraph pg = new PathGraph(myEngine.getBranches());
        List<Branch> branches = pg.copyGraph().getBranches();
        List<Branch> copyBranches =
                branches.stream().map(b -> b.copyBranch()).collect(Collectors.toList());
        for (int y = 0; y < branchesToFilter.size(); y++) {
            for (int x = 0; x < copyBranches.size(); x++) {
                if (branchesToFilter.get(y).equals(copyBranches.get(x))) {
                    Branch removed = copyBranches.remove(x);
                    x--;
                    for (Branch b : copyBranches) {
                        for (int z = 0; z < b.getNeighbors().size(); z++) {
                            if (b.getNeighbors().get(z).equals(removed)) {
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

    private List<Branch> getBranchesToFilter (List<Unit> obstacles) {
        Set<Branch> removalList = new HashSet<>();
        List<Branch> copyBranches =
                myEngine.getBranches().stream().map(b -> b.copyBranch())
                        .collect(Collectors.toList());
        List<Unit> obstacleList = obstacles;
        List<Unit> copyObstacleList =
                obstacleList.stream().map(o -> o.copyShallowUnit()).collect(Collectors.toList());
        for (Unit o : copyObstacleList) {
            for (Branch b : copyBranches) {
                for (Position pos : b.getPositions()) {
                    if (EncapsulationChecker.encapsulates(Arrays.asList(pos), o.getProperties()
                            .getBounds().getUseableBounds(o.getProperties().getPosition()))) {
                        removalList.add(b);
                    }
                }
            }
        }
        return new ArrayList<Branch>(removalList);
    }

    public boolean positionVisibleCheck (List<Branch> visibilityBranches, Position spawn) {
        for (Branch v : visibilityBranches) {
            for (Position p : v.getPositions()) {
                if (p.equals(spawn)) {
                    return true;
                }
            }
        }
        return false;
    }

}