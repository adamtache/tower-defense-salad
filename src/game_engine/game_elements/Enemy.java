package game_engine.game_elements;

import game_engine.properties.Damage;

public class Enemy extends LiveableUnit{
	
	public Enemy(String name){
		super(name);
		setID(getWorkspace().getIDFactory().createID(this));
	}
	
	/*
	* the Enemy fires a projectile at a target, which subsequently applies
	* an Affector on it. 
	*
	*/
	public void fire(){
		
	}
	
	/*
	 * updates the Enemy's health when an Affector is applied to it. 
	 */
	public void takeDamage(double damage){
		Damage myDamage = getProperties().getDamage();
		myDamage.setDamage(myDamage.getDamage() - damage);
	}

	public String toFile() {
		return "Enemy " + getID();
	}
	
	public void update(){
		super.update(this);
	}

	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
}