/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import battle.Conveyer;

/**
 *
 * @author night
 */
public abstract class TalentCondition {
    
    public abstract void inputData(Conveyer data); //call this first
    
    public abstract boolean getCondition(); //inputData() must be called FIRST
    
    public boolean checkCondition(Conveyer data) {
        inputData(data);
        return getCondition();
    };
    
}
