/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import battle.Conveyer;
import com.jme3.math.Vector2f;
import fundamental.Bonus;
import java.util.List;
import maps.layout.Coords;
import maps.layout.TangibleUnit.UnitStatus;

/**
 *
 * @author night
 */
public abstract class TalentEffect {
    
    public abstract void inputData(Conveyer data); //call this first
    
    public abstract Coords userTranslation(); // {x, y}
    public abstract Coords enemyTranslation(); // {x, y}
   
    public abstract Vector2f enemyAOEDMG(); // {damage, range}
    
    public abstract void enactEffect(); //actually do the effect
    public abstract void onPhaseBegin(UnitStatus phase);
    
    public abstract List<Bonus> Buffs();
    
    public Coords retrieveUserTranslation(Conveyer data) {
        inputData(data);
        return userTranslation();
    }
    
    public Coords retrieveEnemyTranslation(Conveyer data) {
        inputData(data);
        return enemyTranslation();
    }
    
    public Vector2f retrieveEnemyAOEDMG(Conveyer data) {
        inputData(data);
        return enemyAOEDMG();
    }
    
    public void doOnPhaseBegin(UnitStatus phase, Conveyer data) {
        inputData(data);
        onPhaseBegin(phase);
    }
    
    public void doEffect(Conveyer data) {
        inputData(data);
        enactEffect();
    }
    
}
