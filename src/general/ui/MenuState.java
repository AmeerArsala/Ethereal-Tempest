/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import battle.Conveyer;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;

/**
 *
 * @author night
 */
public class MenuState extends FsmState {
    private Conveyer info;
    
    public MenuState(EntityState es) {
        super(es);
    }
    
    public void updateEnum(EntityState es) {
        state = es;
    }
    
    public MenuState setConveyer(Conveyer convey) {
        info = convey;
        return this;
    }
    
    public Conveyer getConveyer() { return info; }
    
    public void setConvey(Conveyer convey) {
        info = convey;
    }
}
