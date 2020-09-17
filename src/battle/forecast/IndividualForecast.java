/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import fundamental.Tool;

/**
 *
 * @author night
 */
public class IndividualForecast {
    protected final Combatant combatant;
    
    public float displayedCrit;
    
    public IndividualForecast(Combatant C) {
        combatant = C;
    }
    
    public Combatant getCombatant() { return combatant; }
    
    protected final Tool getTool() {
        return combatant.getUnit().getEquippedTool();
    }
}
