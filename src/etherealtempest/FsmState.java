/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import etherealtempest.FSM.EntityState;

/**
 *
 * @author night
 */
public class FsmState {
    
    protected EntityState state;
    
    public FsmState setEnum(EntityState st) {
        state = st;
        return this;
    }
    
    public FsmState() {}
    
    public FsmState(EntityState e) {
        state = e;
    }
    
    public EntityState getEnum() {
        return state;
    }
    
}
