/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

import battle.Conveyer;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import maps.layout.TangibleUnit;

/**
 *
 * @author night
 */
public class Objective {
    private static final int MAX_TURN_LIMIT = 99;
    
    private final String objectiveName;
    private final int turnLimit;
    private final ObjectiveData criteria;
    
    public Objective(String objectiveName) {
        this.objectiveName = objectiveName;
        criteria = deserializeFromJSON();
        
        turnLimit = criteria.getX_NumberOfTurnsPassForDefeat() != null ? criteria.getX_NumberOfTurnsPassForDefeat() : 
                (criteria.getTurnsToPassForVictory() != null ? criteria.getTurnsToPassForVictory() : MAX_TURN_LIMIT);
    }
    
    public Objective(String objectiveName, ObjectiveData criteria) {
        this.objectiveName = objectiveName;
        this.criteria = criteria;
        
        turnLimit = criteria.getX_NumberOfTurnsPassForDefeat() != null ? criteria.getX_NumberOfTurnsPassForDefeat() : 
                (criteria.getTurnsToPassForVictory() != null ? criteria.getTurnsToPassForVictory() : MAX_TURN_LIMIT);
    }
    
    private ObjectiveData deserializeFromJSON() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\MapObjectives\\" + objectiveName + ".json"));
            return gson.fromJson(reader, ObjectiveData.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public String getName() { return objectiveName; }
    public ObjectiveData getCriteria() { return criteria; }
    
    public int turnsRemaining(Conveyer conv) {
        return turnsRemaining(conv.getCurrentTurn());
    }
    
    public int turnsRemaining(int currentTurn) {
        return turnLimit - currentTurn + 1;
    }
    
    /*public boolean isMet(Conveyer conv) { //TODO: TAKE INTO ACCOUNT THE OBJECTIVEDATA AND MAKE A DECISION ABOUT IT
        ArrayList<TangibleUnit> allUnits = conv.getAllUnits();
    }
    
    public boolean hasFailed(Conveyer conv) {
    
    }*/
    
    public static ObjectiveData Marry(ObjectiveData first, ObjectiveData other) {
        return first.marry(other);
    }
    
    public enum Amount {
        Any,
        All
    }
    
    //some presets below
    
    public static ObjectiveData Annex(Amount A) {
        return A == Amount.All ? new ObjectiveData().setAllAnnexableTilesAnnexedForDefeat(true) : new ObjectiveData().setAnnexAnyAnnexableTile(true);
    }
    
    public static ObjectiveData Rout() {
        return new ObjectiveData().setKillAllEnemyUnits(true);
    }
    
    public static ObjectiveData Kill_X_Number_Of_Enemies(Integer amt) {
        return new ObjectiveData().setNumberOfEnemiesToKill(amt);
    }
    
    public static ObjectiveData Escape() {
        return new ObjectiveData().setEscape(true);
    }
    
    public static ObjectiveData SurviveFor_X_Turns(Integer X) {
        return new ObjectiveData().setTurnsToPassForVictory(X);
    }
    
    
}
