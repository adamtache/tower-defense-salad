package auth_environment.view;

import java.util.ResourceBundle;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by BrianLin on 4/6/16.
 * 
 * Team member responsible: Brian
 * 
 * First (interactive) screen displayed to the Developer. Asks for Game name. 
 */

public class Welcome {
	
	private static final String DIMENSIONS_PACKAGE = "auth_environment/properties/dimensions";
	private ResourceBundle myDimensionsBundle = ResourceBundle.getBundle(DIMENSIONS_PACKAGE);
	
	private static final String NAMES_PACKAGE = "auth_environment/properties/names";
	private ResourceBundle myNamesBundle = ResourceBundle.getBundle(NAMES_PACKAGE);
	
	private static final String URLS_PACKAGE = "auth_environment/properties/urls";
	private ResourceBundle myURLSBundle = ResourceBundle.getBundle(URLS_PACKAGE);
	
	private NodeFactory myNodeFactory = new NodeFactory(); 
	private BorderPane myRoot = new BorderPane(); 
	
	public Welcome() {
		
	}
	
	private VBox buildCenter() {
		VBox center = 
	}
	
	public Node getRoot() {
		return this.myRoot;
	}

}
