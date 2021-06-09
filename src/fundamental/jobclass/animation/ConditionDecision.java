/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation;

import battle.data.DecisionParams;

/**
 *
 * @author night
 */
public interface ConditionDecision {
    public boolean test(DecisionParams params);
}
