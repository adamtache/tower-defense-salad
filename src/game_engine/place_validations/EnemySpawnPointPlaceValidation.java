package game_engine.place_validations;

import game_engine.GameEngineInterface;
import game_engine.game_elements.Unit;
import game_engine.properties.Position;

public class EnemySpawnPointPlaceValidation extends PlaceValidation{

    @Override
    public boolean validate (GameEngineInterface gameEngine, Unit unit, double posX, double posY) {
        System.out.println(posX);
        System.out.println(posY);
        if((new Position(posX, posY)).equals(new Position(100, 100))) {
            return true;
        }
        return true;
    }

}