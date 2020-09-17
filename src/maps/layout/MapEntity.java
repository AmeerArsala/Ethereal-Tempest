/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import etherealtempest.FSM;
import etherealtempest.FsmState;
import fundamental.Entity;

/**
 *
 * @author night
 */
public class MapEntity extends Entity { //use gson
    //add more to this class later
    private final FSM fsm = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            state = st;
        }
    };

    private int posX;
    private int posY;
    private int elevation;
    private int MaxHP;
    
    public int currentHP;
    
    public MapEntity(String name, int posX, int posY, int elevation, int MaxHP) {
        super(name);
        this.posX = posX;
        this.posY = posY;
        this.elevation = elevation;
        this.MaxHP = MaxHP;
    }
    
    public void damage() { //by default, breakable structures can have 3 HP; no matter the stats of the enemy, they can only do 1 damage to it normally
        if (MaxHP > 0) {
            currentHP--;
        }
    }
    
    public void damage(int val) { //...unless they have a Talent that gives them extra damage towards structures
        if (MaxHP > 0) {
            currentHP -= val;
        }
    }
    
    public boolean isDamageable() { //MaxHP being <= means it is an unbreakable structure
        return MaxHP > 0;
    }
    
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getElevation() { return elevation; }
    
    public int getMaxHP() { return MaxHP; }
    
    public FSM getFSM() { return fsm; }
}
