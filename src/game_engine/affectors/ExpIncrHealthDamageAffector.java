package game_engine.affectors;


import java.util.List;
import game_engine.IPlayerEngineInterface;
import game_engine.functions.Function;
import game_engine.properties.Bounds;

public class ExpIncrHealthDamageAffector extends HealthDamageAffector{

    public ExpIncrHealthDamageAffector(List<Function> functions){
        super(functions);
    }

}