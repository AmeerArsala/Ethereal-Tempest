/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.ai;

import etherealtempest.info.Conveyer;

/**
 *
 * @author night
 */
public class ConditionalBehavior {
    private final boolean declaredAnonymously;
    
    private AICondition condition;
    private AIBehavior behavior;
    
    private boolean cause;
    private Option effect;
        
    ConditionalBehavior(AICondition cond, AIBehavior behave) {
        condition = cond;
        behavior = behave;
        
        declaredAnonymously = true;
    }
    
    ConditionalBehavior(boolean cause, Option effect) {
        this.cause = cause;
        this.effect = effect;
        
        declaredAnonymously = false;
    }
    
    public boolean canExecute(Conveyer data) {
        return declaredAnonymously ? condition.condition(data) : cause;
    }
    
    public Option retrieveAction(Conveyer data) {
        return declaredAnonymously ? behavior.action(data) : effect;
    }
        
    /*public boolean attempt(Conveyer data) {
        if (condition.condition(data)) {
            behavior.execute(data);
            return true;
        }
            
        return false;
    }*/
}
