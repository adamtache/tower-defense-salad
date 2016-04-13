package game_engine.game_elements;

/*
* Internal API that will be used to reflect the inclusion of terrain effects in games. 
* Most implementations of the Terrain API will apply some sort of affector to game elements within
* a certain area on the map.
*/
public class Terrain extends Unit{
    
	public Terrain(String name, int numFrames) {
		super(name, numFrames);
	}
	
	@Override
	public void update() {
//		System.out.println(this.getProperties().getVelocity() == null);
//		System.out.println(" testing");
		super.update();
//		System.out.println(this);
	}
	
}