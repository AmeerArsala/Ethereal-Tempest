/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import etherealtempest.FSM;
import etherealtempest.FsmState;

/**
 *
 * @author night
 */
public class MoveState extends FsmState {
    
    private Map local;
    private Cursor lCursor;
    
    public MoveState() {
        state = FSM.EntityState.Moving;
    }
    
    public MoveState(Map M, Cursor C) {
        state = FSM.EntityState.Moving;
        local = M;
        lCursor = C;
    }
    
    //doing this for the sake of flexibility
    public MoveState setMapAndCursor(Map M, Cursor C) {
        local = M;
        lCursor = C;
        return this;
    } 
    
    public Map getMap() { return local; }
    public Cursor getCursor() { return lCursor; }       
            
}