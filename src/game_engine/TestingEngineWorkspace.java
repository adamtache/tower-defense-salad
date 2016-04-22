package game_engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import auth_environment.paths.PathGraphFactory;
import auth_environment.paths.PathHandler;
import auth_environment.paths.PathNode;
import game_engine.IDFactory;
import game_engine.TestingEngineWorkspace;
import game_engine.affectors.Affector;
import game_engine.factories.AffectorFactory;
import game_engine.factories.EnemyFactory;
import game_engine.factories.FunctionFactory;
import game_engine.factories.TerrainFactory;
import game_engine.factories.TimelineFactory;
import game_engine.factories.TowerFactory;
import game_engine.game_elements.Branch;
import game_engine.game_elements.Level;
import game_engine.game_elements.Unit;
import game_engine.game_elements.Wave;
import game_engine.games.GameEngineInterface;
import game_engine.games.Timer;
import game_engine.libraries.AffectorLibrary;
import game_engine.libraries.FunctionLibrary;
import game_engine.physics.CollisionDetector;
import game_engine.physics.EncapsulationDetector;
import game_engine.properties.Position;
import game_engine.properties.UnitProperties;
import game_engine.score_updates.EnemyDeathScoreUpdate;
import game_engine.score_updates.EnemyWinScoreUpdate;
import game_engine.score_updates.ScoreUpdate;
import game_engine.store_elements.Store;
import game_engine.wave_goals.EnemyNumberWaveGoal;
import game_engine.wave_goals.WaveGoal;


public class TestingEngineWorkspace implements GameEngineInterface {

    private int nextWaveTimer;
    private boolean pause;
    private List<Level> myLevels;
    private List<Branch> myBranches;
    private double score;

    private WaveGoal waveGoal;
    private ScoreUpdate scoreUpdate;
    private List<Unit> myTowers;
    private List<Unit> myEnemys;
    private List<Unit> myProjectiles;

    private CollisionDetector myCollider;
    private EncapsulationDetector myEncapsulator;

    private Level myCurrentLevel;
    private IDFactory myIDFactory;
    private double myBalance;
    private int myLives;
    private Store myStore;

    private FunctionFactory myFunctionFactory;
    private AffectorFactory myAffectorFactory;
    private EnemyFactory myEnemyFactory;
    private TowerFactory myTowerFactory;

    private List<Affector> myAffectors;

    private List<Unit> myTerrains;
    private TerrainFactory myTerrainFactory;

    private List<Position> myGoals;

    private TimelineFactory myTimelineFactory;

    public TestingEngineWorkspace () {};

    public void setUpEngine (Double test) {
        score = 0;
        waveGoal = new EnemyNumberWaveGoal();
        scoreUpdate = new EnemyDeathScoreUpdate();
        myLives = 3;
        myLevels = new ArrayList<>();
        myBranches = new ArrayList<>();
        // Branch p2 = new Branch("DirtNew");
        // p2.addPosition(new Position(0, 30));
        // p2.addPosition(new Position(200, 30));
        // p2.addPosition(new Position(200, 200));
        // p2.addPosition(new Position(400, 200));
        // p2.addPosition(new Position(400, 525));
        // myPaths.add(p2);
        myIDFactory = new IDFactory();
        myProjectiles = new ArrayList<>();
        // projectiles must be intialized before towers
        myFunctionFactory = new FunctionFactory();
        myAffectorFactory = new AffectorFactory(myFunctionFactory);
        myTimelineFactory = new TimelineFactory(myAffectorFactory.getAffectorLibrary());
        myEnemyFactory =
                new EnemyFactory(myAffectorFactory.getAffectorLibrary(),
                                 myTimelineFactory.getTimelineLibrary());
        myEnemys = new ArrayList<>();
        myTowerFactory = new TowerFactory(myAffectorFactory.getAffectorLibrary());
        myTowers = new ArrayList<>();
        myStore = new Store(500);
        makeDummyTowers();
        myTerrainFactory = new TerrainFactory(myAffectorFactory.getAffectorLibrary());
        myTerrains = makeDummyTerrains();
        myCollider = new CollisionDetector(this);
        myEncapsulator = new EncapsulationDetector(this);
        myBalance = 0;
        nextWaveTimer = 0;
        myCurrentLevel = makeDummyLevel();
        myLevels.add(myCurrentLevel);
        myGoals = myCurrentLevel.getGoals();
        myAffectorFactory.getAffectorLibrary().getAffectors().stream()
                .forEach(a -> a.setWorkspace(this));
        this.makeDummyUpgrades();
    }

    private List<Unit> makeDummyTowers () {
        Position position2 = new Position(200, 300);
        Unit t =
                myTowerFactory.createHomingTower("Tower", myProjectiles,
                                                 Collections.unmodifiableList(myTowers),
                                                 position2);
        Unit t2 =
                myTowerFactory.createTackTower("TackTower", myProjectiles,
                                               Collections.unmodifiableList(myTowers),
                                               position2);
        // myStore.addBuyableTower(t, 100, 1);
        // myStore.addBuyableTower(t2, 300, 1);
        return new ArrayList<>(Arrays.asList(new Unit[] { t, t2 }));
    }

    private Level makeDummyLevel () {
        Level l = new Level("Dummy level", 3);
        PathHandler ph = new PathHandler();
        PathGraphFactory pgf = ph.getPGF();

        // myBranches.addAll(pgf.getBranches());
        // List<PathNode> paths = pgf.getPaths();
        // l.addAllPaths(paths);

        // For testing branching
        myBranches.addAll(pgf.getBranches());
        Branch pb1 = myBranches.get(0);
        Branch pb2 = myBranches.get(1);
        Branch pb3 = myBranches.get(2);
        Branch pb4 = myBranches.get(3);
        Branch pb5 = myBranches.get(4);
        Branch pb6 = myBranches.get(5);
        List<Branch> branches1 = Arrays.asList(pb1, pb6, pb2, pb4);
        List<Branch> branches2 = Arrays.asList(pb1, pb6, pb3, pb4);
        PathNode p = new PathNode(0);
        p.addBranch(pb1);
        p.addBranch(pb2);
        p.addBranch(pb3);
        p.addBranch(pb4);
        p.addBranch(pb5);
        p.addBranch(pb6);

        l.addPath(p);

        Wave w = new Wave("I'm not quite sure what goes here", 0);
        Unit AI1 = myEnemyFactory.createAIEnemy("Moab", pb1);
        Unit AI2 = myEnemyFactory.createAIEnemy("Moab", pb1);
        Unit e1 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches1);
        Unit e2 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches2);
        Unit e3 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches1);
        Unit e4 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches2);
        Unit AI3 = myEnemyFactory.createAIEnemy("Moab", pb1);
        Unit AI4 = myEnemyFactory.createAIEnemy("Moab", pb1);
        Unit rand1 = myEnemyFactory.createRandomEnemy("Enemy", pb1);
        Unit rand2 = myEnemyFactory.createRandomEnemy("Enemy", pb1);
        Unit rand3 = myEnemyFactory.createRandomEnemy("Enemy", pb1);
        Unit rand4 = myEnemyFactory.createRandomEnemy("Enemy", pb1);
        e1.getProperties().setHealth(50);
        e2.getProperties().setHealth(50);
        e3.getProperties().setHealth(50);
        e4.getProperties().setHealth(50);
        AI1.getProperties().setHealth(50);
        AI2.getProperties().setHealth(50);
        AI3.getProperties().setHealth(50);
        AI4.getProperties().setHealth(50);
        rand1.getProperties().setHealth(50);
        rand2.getProperties().setHealth(50);
        rand3.getProperties().setHealth(50);
        rand4.getProperties().setHealth(50);
        w.addSpawningUnit(e1, 0);
        w.addSpawningUnit(e2, 60);
        w.addSpawningUnit(e3, 60);
        w.addSpawningUnit(e4, 60);
        w.addSpawningUnit(AI1, 60);
        w.addSpawningUnit(AI2, 60);
        w.addSpawningUnit(AI3, 60);
        w.addSpawningUnit(AI4, 60);
        w.addSpawningUnit(rand1, 60);
        w.addSpawningUnit(rand2, 60);
        w.addSpawningUnit(rand3, 60);
        w.addSpawningUnit(rand4, 60);
        List<Unit> list = makeDummyTowers();
        w.addPlacingUnit(list.get(0), 0);
        w.addPlacingUnit(list.get(1), 0);
        l.setMyLives(5);
        l.addWave(w);
        Wave w2 = new Wave("I'm not quite sure what goes here", 1600);
        Unit e5 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches1);
        Unit e6 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches2);
        Unit e7 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches1);
        Unit e8 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy", branches2);
        e5.getProperties().setHealth(50);
        e6.getProperties().setHealth(50);
        e7.getProperties().setHealth(50);
        e8.getProperties().setHealth(50);
        w2.addSpawningUnit(e5, 0);
        w2.addSpawningUnit(e6, 60);
        w2.addSpawningUnit(e7, 60);
        w2.addSpawningUnit(e8, 60);
        List<Unit> list1 = makeDummyTowers();
        w2.addPlacingUnit(list1.get(0), 0);
        Wave w3 = new Wave("I'm not quite sure what goes here", 1600);
        Unit e9 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab", branches1);
        Unit e10 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab", branches2);
        Unit e11 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab", branches1);
        Unit e12 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab", branches2);
        e9.getProperties().setHealth(50);
        e10.getProperties().setHealth(50);
        e11.getProperties().setHealth(50);
        e12.getProperties().setHealth(50);
        w3.addSpawningUnit(e9, 0);
        w3.addSpawningUnit(e10, 60);
        w3.addSpawningUnit(e11, 60);
        w3.addSpawningUnit(e12, 60);
        List<Unit> list2 = makeDummyTowers();
        w3.addPlacingUnit(list2.get(1), 0);
        l.addWave(w3);
        l.addWave(w2);
        return l;
    }

    private void makeDummyUpgrades () {
        Affector affector =
                this.myAffectorFactory.getAffectorLibrary().getAffector("Constant", "HealthDamage");
        affector.setTTL(Integer.MAX_VALUE);
        // List<Affector> affList = new ArrayList<Affector>();
        // affList.add(affector);
        // Affector t = new Affector(affList);
        // List<AffectorTimeline> init = new ArrayList<AffectorTimeline>();
        // init.add(t);
        Unit u = new Unit("Interesting", Arrays.asList(affector), 1);
        u.addAffectorToApply(affector);
        myStore.addItem(u, 10);
    }

    private List<Unit> makeDummyTerrains () {
        List<Unit> ice = makeDummyIceTerrain();
        Unit spike = makeDummySpike();
        List<Unit> terrains = new ArrayList<>();
        terrains.addAll(ice);
        terrains.add(spike);
        return terrains;
    }

    private List<Unit> makeDummyIceTerrain () {
        Unit ice1 = myTerrainFactory.getTerrainLibrary().getTerrainByName("IceTerrain");
        List<Position> pos = new ArrayList<>();
        pos.add(new Position(0, 0));
        pos.add(new Position(30, 0));
        pos.add(new Position(30, 30));
        pos.add(new Position(0, 30));
        ice1.getProperties().setPosition(185, 155);
        ice1.getProperties().setBounds(pos);
        ice1.setTTL(Integer.MAX_VALUE);

        Unit ice2 = myTerrainFactory.getTerrainLibrary().getTerrainByName("IceTerrain");
        ice2.getProperties().setPosition(185, 185);
        ice2.getProperties().setBounds(pos);
        ice2.setTTL(Integer.MAX_VALUE);

        Unit ice3 = myTerrainFactory.getTerrainLibrary().getTerrainByName("IceTerrain");
        ice3.getProperties().setPosition(185, 185);
        ice3.getProperties().setBounds(pos);
        ice3.setTTL(Integer.MAX_VALUE);

        Unit ice4 = myTerrainFactory.getTerrainLibrary().getTerrainByName("IceTerrain");
        ice4.getProperties().setPosition(215, 185);
        ice4.getProperties().setBounds(pos);
        ice4.setTTL(Integer.MAX_VALUE);

        return new ArrayList<>(Arrays.asList(new Unit[] { ice1, ice2, ice3, ice4 }));
    }

    private Unit makeDummySpike () {
        Unit spike = myTerrainFactory.getTerrainLibrary().getTerrainByName("SpikesTerrain");
        List<Position> pos = new ArrayList<>();
        pos.add(new Position(0, 0));
        pos.add(new Position(0, 30));
        pos.add(new Position(30, 30));
        pos.add(new Position(30, 0));
        spike.getProperties().setPosition(185, 70);
        spike.getProperties().setBounds(pos);
        spike.setTTL(Integer.MAX_VALUE);
        return spike;
    }

    public String getGameStatus () {
        if (myCurrentLevel.getMyLives() <= 0) {
            return "Waves remaining: " + myCurrentLevel.wavesLeft() + ", Lives remaining: " + "0";
        }
        return "Waves remaining: " + myCurrentLevel.wavesLeft() +
               ", Lives remaining: " + myCurrentLevel.getMyLives();
    }

    public void addBalance (double money) {
        myBalance += money;
    }

    public void addLevel (Level level) {
        myLevels.add(level);
    }

    public void remove (Unit unit) {
        String className = unit.getClass().getSimpleName();
        String instanceVarName = "my" + className + "s";
        Field f = null;
        try {
            f = getClass().getDeclaredField(instanceVarName);
        }
        catch (NoSuchFieldException | SecurityException e1) {
            // TODO: womp exception
            e1.printStackTrace();
        }
        f.setAccessible(true);
        List<Unit> listInstanceVar = null;
        try {
            listInstanceVar = (List<Unit>) f.get(this);
        }
        catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO: womp exception
            e.printStackTrace();
        }
        listInstanceVar.remove(unit);
    }

    public void modifyTower (int activeTowerIndex, UnitProperties newProperties) {
        // towerBoundsCheck(activeTowerIndex);
        // myTowers.get(activeTowerIndex).setProperties(newProperties);
        //
    }

    // Getters

    public Level getCurrentLevel () {
        return myCurrentLevel;
    }

    public IDFactory getIDFactory () {
        return myIDFactory;
    }

    public double getBalance () {
        return myBalance;
    }

    public List<Level> getLevels () {
        return myLevels;
    }

    public List<Branch> getBranches () {
        return myBranches;
    }

    public List<Unit> getEnemies () {
        return myEnemys;
    }

    public List<Unit> getTowers () {
        return myTowers;
    }

    public List<Unit> getProjectiles () {
        return myProjectiles;
    }

    public FunctionFactory getFunctionFactory () {
        return myFunctionFactory;
    }

    public FunctionLibrary getFunctionLibrary () {
        return myFunctionFactory.getFunctionLibrary();
    }

    public EnemyFactory getEnemyFactory () {
        return myEnemyFactory;
    }

    public AffectorLibrary getAffectorLibrary () {
        return myAffectorFactory.getAffectorLibrary();
    }

    @Override
    public int getLives () {
        return myCurrentLevel.getMyLives();
    }

    public void clearProjectiles () {
        myProjectiles.forEach(t -> {
            t.setInvisible();
            t.setHasCollided(true);
        });
    }

    public List<Unit> getTerrains () {
        return myTerrains;
    }

    public List<String> saveGame () {
        // TODO Auto-generated method stub
        return null;
    }

    public void playLevel (int levelNumber) {
        myCurrentLevel = myLevels.get(levelNumber);
        pause = false;
    }

    public void playWave (int waveNumber) {
        // TODO: pause current wave
        myCurrentLevel.setCurrentWave(waveNumber);
    }

    public void continueWaves () {
        myCurrentLevel.playNextWave();
        pause = false;
    }

    // change this
    @Override
    public boolean addTower (String name, double x, double y) {
        // for(int i = 0; i < myStore.getTowerList().size(); i++) {
        // if(myStore.getTowerList().get(i).toString().equals(name)) {
        // Unit newTower = myStore.getTowerList().get(i).copyUnit();
        // newTower.getProperties().setPosition(x, y);
        // myTowers.add(newTower);
        // }
        // }
        System.out.println(name);
        Unit purchased = myStore.purchaseUnit(name);
        if (purchased != null) {
            Unit copy = purchased.copyUnit();
            copy.getProperties().setPosition(x, y);
            myTowers.add(copy);
            return true;
        }
        return false;
    }

    @Override
    public List<Unit> getTowerTypes () {
        return myStore.getTowerList();
    }

    public List<Affector> getAffectors () {
        return myAffectors;
    }

    @Override
    public void update () {
        List<Unit> placingUnits = myCurrentLevel.getCurrentWave().getPlacingUnits();
        myStore.clearBuyableUnits();
        placingUnits.stream().forEach(u -> myStore.addBuyableUnit(u, 100));
        nextWaveTimer++;
        boolean gameOver = myLives <= 0;
        if (!pause && !gameOver) {
            myTowers.forEach(t -> t.update());
            myEnemys.forEach(e -> e.update());
            myCollider.resolveEnemyCollisions(myProjectiles);
            myEncapsulator.resolveEncapsulations(myTerrains);
            Unit newE = myCurrentLevel.update();
            if (newE != null) {
                myEnemys.add(newE);
            }// tries to spawn new enemies using Waves
//            myStore.applyItem("Interesting", this.myEnemys);

        }
        if (myCurrentLevel.getNextWave() != null && waveGoal.reachedGoal(this)) {
            nextWaveTimer = 0;
            continueWaves();
        }
        if (myEnemys.size() == 0) {
            clearProjectiles();
        }
        myProjectiles.forEach(p -> p.update());
        myProjectiles.removeIf(p -> !p.isVisible());
        myTerrains.forEach(t -> t.update());
        scoreUpdate.updateScore(this, myCurrentLevel);
        // updateLives();

    }

    @Override
    public boolean isPaused () {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setPaused () {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isGameOver () {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Timer getTimer () {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void decrementLives () {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Position> getGoals () {
        return myGoals;
    }

    public List<Unit> getAllUnits () {
        List<Unit> units = new ArrayList<>();
        units.addAll(myTowers);
        units.addAll(myEnemys);
        units.addAll(myProjectiles);
        units.addAll(myTerrains);
        return units;
    }

    public int getNextWaveTimer () {
        return nextWaveTimer;
    }

    @Override
    public double getScore () {
        return score;
    }
    
    public void setScore (double score) {
        this.score = score;
    }
    
}
