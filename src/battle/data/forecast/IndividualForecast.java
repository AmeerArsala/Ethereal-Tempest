/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.forecast;

import battle.data.participant.BattleRole;
import battle.data.participant.Combatant;
import fundamental.jobclass.animation.ActionDecider;
import fundamental.tool.Tool;

/**
 *
 * @author night
 */
public abstract class IndividualForecast {
    protected final Combatant combatant;
    
    protected float displayedCrit;
    
    public IndividualForecast(Combatant C) {
        combatant = C;
    }
    
    public Combatant getCombatant() { return combatant; }
    
    public float getDisplayedCrit() { return displayedCrit; }
    
    public void forceDisplayedCrit(float crit) {
        displayedCrit = crit;
    }
    
    public final Tool getEquippedTool() {
        return combatant.getUnit().getEquippedTool();
    }
    
    public ActionDecider getActionDecider() {
        return combatant.getUnit().getJobClass().getBattleAnimationConfigurations().get(getEquippedTool().getType());
    }
    
    public boolean isInitiator() {
        return combatant.battle_role == BattleRole.Initiator;
    }
    
    protected abstract void initialize(); //initialize forecast
}
