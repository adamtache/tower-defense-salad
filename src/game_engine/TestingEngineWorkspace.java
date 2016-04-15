package game_engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import game_data.GameData;
import game_engine.affectors.Affector;
import game_engine.factories.AffectorFactory;
import game_engine.factories.EnemyFactory;
import game_engine.factories.FunctionFactory;
import game_engine.factories.TerrainFactory;
import game_engine.factories.TowerFactory;
import game_engine.game_elements.Enemy;
import game_engine.game_elements.Level;
import game_engine.game_elements.Branch;
import game_engine.game_elements.Terrain;
import game_engine.game_elements.Tower;
import game_engine.game_elements.Unit;
import game_engine.libraries.AffectorLibrary;
import game_engine.libraries.FunctionLibrary;
import game_engine.game_elements.Wave;
import game_engine.properties.Position;
import game_engine.properties.UnitProperties;


/**
 * This class represents the "workspace" for a single instance of a game.
 * This workspace holds game instance specific data.
 * 
 *
 */

public class TestingEngineWorkspace implements IPlayerEngineInterface {
	private int nextWaveTimer;
	private boolean pause;
	private List<Level> myLevels;
	private List<Branch> myPaths;

	private List<Unit> myTowers;
	private List<Unit> myEnemys;
	private List<Unit> myProjectiles;

	private CollisionDetector myCollider;
	private List<Tower> myTowerTypes;
	private Level myCurrentLevel;
	private IDFactory myIDFactory;
	private double myBalance;
	private int myLives;

	private FunctionFactory myFunctionFactory;
	private AffectorFactory myAffectorFactory;
	private EnemyFactory myEnemyFactory;
	private TowerFactory myTowerFactory;
	
	private List<Affector> myAffectors;

	private List<Unit> myTerrains;
	private TerrainFactory myTerrainFactory;

	public TestingEngineWorkspace(){
		setUpEngine(null);
	}
	
	public void setUpEngine (GameData gameData) {
		myLives = 3;
		myLevels = new ArrayList<>();
		myPaths = new ArrayList<>();
		Branch p2 = new Branch("DirtNew");
		p2.addPosition(new Position(0, 30));
		p2.addPosition(new Position(200, 30));
		p2.addPosition(new Position(200, 200));
		p2.addPosition(new Position(400, 200));
		p2.addPosition(new Position(400, 525));
		myPaths.add(p2);
		myTowerTypes = new ArrayList<>();
		myIDFactory = new IDFactory();
		myProjectiles = new ArrayList<>();
		// projectiles must be intialized before towers
		myFunctionFactory = new FunctionFactory();
		myAffectorFactory = new AffectorFactory(myFunctionFactory);
		myEnemyFactory = new EnemyFactory(myAffectorFactory.getAffectorLibrary());
		myEnemys = new ArrayList<>();
		myTowerFactory = new TowerFactory(myAffectorFactory.getAffectorLibrary());
		myTowers = new ArrayList<>();
		myTowerTypes = makeDummyTowers();
		myTerrainFactory = new TerrainFactory(myAffectorFactory.getAffectorLibrary());
		myTerrains = makeDummyTerrains();
		myCollider = new CollisionDetector(this);
		myBalance = 0;
		nextWaveTimer = 0;
		myCurrentLevel = makeDummyLevel();
		myLevels.add(myCurrentLevel);
		myAffectors = myAffectorFactory.getAffectorLibrary().getAffectors();
		myAffectors.stream().forEach(a -> a.setWorkspace(this));
	}

	private List<Tower> makeDummyTowers () {
		Position position2 = new Position(200, 300);
		Tower t =
				myTowerFactory.createHomingTower("Tower", myProjectiles,
						Collections.unmodifiableList(myTowers),
						position2);
		Tower t2 = 
				myTowerFactory.createTackTower("Tack", myProjectiles,
						Collections.unmodifiableList(myTowers),
						position2);
		return new ArrayList<>(Arrays.asList(new Tower[] { t, t2 }));
	}

	private Level makeDummyLevel () {
		Wave w = new Wave("I'm not quite sure what goes here", 0);
		Enemy e1 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		Enemy e2 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		Enemy e3 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		Enemy e4 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		e1.getProperties().setHealth(50);
		e2.getProperties().setHealth(50);
		e3.getProperties().setHealth(50);
		e4.getProperties().setHealth(50);
		w.addEnemy(e1, 0);
		w.addEnemy(e2, 60);
		w.addEnemy(e3, 60);
		w.addEnemy(e4, 60);
		Level l = new Level("still not sure", w, 3);
		l.addWave(w);
		Wave w2 = new Wave("I'm not quite sure what goes here", 240);
		Enemy e5 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		Enemy e6 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		Enemy e7 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		Enemy e8 = myEnemyFactory.createPathFollowPositionMoveEnemy("Enemy");
		e5.getProperties().setHealth(50);
		e6.getProperties().setHealth(50);
		e7.getProperties().setHealth(50);
		e8.getProperties().setHealth(50);
		w2.addEnemy(e5, 0);
		w2.addEnemy(e6, 60);
		w2.addEnemy(e7, 60);
		w2.addEnemy(e8, 60);
		Wave w3 = new Wave("I'm not quite sure what goes here", 240);
		Enemy e9 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab");
		Enemy e10 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab");
		Enemy e11 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab");
		Enemy e12 = myEnemyFactory.createPathFollowPositionMoveEnemy("Moab");
		e9.getProperties().setHealth(50);
		e10.getProperties().setHealth(50);
		e11.getProperties().setHealth(50);
		e12.getProperties().setHealth(50);
		w3.addEnemy(e9, 0);
		w3.addEnemy(e10, 60);
		w3.addEnemy(e11, 60);
		w3.addEnemy(e12, 60);
		l.addWave(w3);
		l.addWave(w2);
		return l;
	}

	private List<Unit> makeDummyTerrains () {
		List<Terrain> ice = makeDummyIceTerrain();
		Terrain spike = makeDummySpike();
		List<Unit> terrains = new ArrayList<>();
		terrains.addAll(ice);
		terrains.add(spike);
		return terrains;
	}

	private List<Terrain> makeDummyIceTerrain () {
		Terrain ice1 = myTerrainFactory.getTerrainLibrary().getTerrainByName("Ice");
		List<Position> pos = new ArrayList<>();
		pos.add(new Position(0, 0));
		pos.add(new Position(30, 0));
		pos.add(new Position(30, 30));
		pos.add(new Position(0, 30));
		ice1.getProperties().setPosition(185, 155);
		ice1.getProperties().setBounds(pos);
		ice1.setTTL(Integer.MAX_VALUE);

		Terrain ice2 = myTerrainFactory.getTerrainLibrary().getTerrainByName("Ice");
		ice2.getProperties().setPosition(185, 185);
		ice2.getProperties().setBounds(pos);
		ice2.setTTL(Integer.MAX_VALUE);

		Terrain ice3 = myTerrainFactory.getTerrainLibrary().getTerrainByName("Ice");
		ice3.getProperties().setPosition(185, 185);
		ice3.getProperties().setBounds(pos);
		ice3.setTTL(Integer.MAX_VALUE);

		Terrain ice4 = myTerrainFactory.getTerrainLibrary().getTerrainByName("Ice");
		ice4.getProperties().setPosition(215, 185);
		ice4.getProperties().setBounds(pos);
		ice4.setTTL(Integer.MAX_VALUE);

		return new ArrayList<>(Arrays.asList(new Terrain[] { ice1, ice2, ice3, ice4 }));
	}

	//    private Terrain makeDummyPoisonSpike () {
	//        Terrain poisonSpike = myTerrainFactory.getTerrainLibrary().getTerrainByName("PoisonSpikes");
	//        List<Position> pos = new ArrayList<>();
	//        pos.add(new Position(95, -25));
	//        pos.add(new Position(275, -25));
	//        pos.add(new Position(275, 150));
	//        pos.add(new Position(95, 150));
	//        poisonSpike.getProperties().setPosition(185, 62.5);
	//        poisonSpike.getProperties().setBounds(pos);
	//        poisonSpike.setTTL(Integer.MAX_VALUE);
	//        return poisonSpike;
	//    }

	private Terrain makeDummySpike () {
		Terrain spike = myTerrainFactory.getTerrainLibrary().getTerrainByName("Spikes");
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

	public void updateElements () {
		nextWaveTimer++;
		boolean gameOver = myLives <= 0;
		if (!pause && !gameOver) {
			myTowers.forEach(t -> t.update());
			myTowers.forEach(t -> ((Tower) t).fire());
			myEnemys.forEach(e -> e.update());
			myCollider.resolveEnemyCollisions(myProjectiles, myTerrains);
			Enemy newE = myCurrentLevel.update();
			if (newE != null) {
				myEnemys.add(newE);
			}// tries to spawn new enemies using Waves
			if (myCurrentLevel.getCurrentWave().isFinished()) {
				clearProjectiles();
				pause = true;
				nextWaveTimer = 0;
			}
		}
		else if (myCurrentLevel.getNextWave() != null &&
				myCurrentLevel.getNextWave().getTimeBeforeWave() <= nextWaveTimer) {
			continueWaves();
		}
		myProjectiles.forEach(p -> p.update());
		myTerrains.forEach(t -> t.update());
		updateLives();
	}

	public void updateLives () {
		int livesToSubtract = 0;
		for (int i = 0; i < myEnemys.size(); i++) {
			if (myEnemys.get(i).getProperties().getMovement().isUnitAtLastPosition(myEnemys.get(i))) {
				livesToSubtract++;
				myEnemys.get(i).setElapsedTimeToDeath();
			}
		}
		myCurrentLevel.setMyLives(myCurrentLevel.getStartingLives() - livesToSubtract);
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

	public void addEnemy (Enemy enemy) {
		myEnemys.add(enemy);
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
		// myTowerTypes.set(activeTowerIndex, newProperties);
		//
	}

	private void towerBoundsCheck (int index) {
		if (index < 0 || index > myTowers.size()) {
			throw new IndexOutOfBoundsException();
		}
	}

	private void towerTypeBoundsCheck (int index) {
		if (index < 0 || index > myTowerTypes.size()) {
			throw new IndexOutOfBoundsException();
		}
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

	public List<Branch> getPaths () {
		return myPaths;
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

	@Override
	public void addTower (String name, double x, double y) {
		for(int i = 0; i < myTowerTypes.size(); i++) {
			if(myTowerTypes.get(i).toString().equals(name)) {
				Tower newTower = myTowerTypes.get(i).copyTower(x, y);
				myTowers.add(newTower);
			}
		}
	}

	@Override
	public List<Tower> getTowerTypes () {
		return myTowerTypes;
	}
	
	public List<Affector> getAffectors(){
		return myAffectors;
	}

}