/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import fundamental.tool.DamageTool;

/**
 *
 * @author night
 */
public class SingularForecast extends IndividualForecast {
    public boolean canDouble, canCounterattack;
    public float displayedAccuracy;
    public int displayedDamage, BPcostPerHit = 1000; //1000 for one strike
    
    public final DamageTool equippedTool;
    
    public SingularForecast(Combatant C) {
        super(C);
        equippedTool = ((DamageTool)getTool());
    }
    
    public int getAmountOfHits() {
        return 1000 / BPcostPerHit;
    }

}
