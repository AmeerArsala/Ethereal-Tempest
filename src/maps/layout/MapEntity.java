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
public class MapEntity { //use gson
    //add more to this class later
    private final FSM fsm = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st;
        }
    };
    
    private String name;
    private int MaxHP;
    
    public int currentHP;
    
    public MapEntity(String name, int MaxHP) {
        this.name = name;
        this.MaxHP = MaxHP;
    }
    
    public String getName() { return name; }
    public int getMaxHP() { return MaxHP; }
    
    public FSM getFSM() { return fsm; }
}
