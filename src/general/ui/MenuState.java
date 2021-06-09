/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import etherealtempest.FSM.MapFlowState;
import etherealtempest.info.Conveyer;
import etherealtempest.FsmState;

/**
 *
 * @author night
 */
public class MenuState extends FsmState<MapFlowState> {
    private Conveyer info;
    
    public MenuState(MapFlowState es) {
        super(es);
    }
    
    public void updateEnum(MapFlowState es) {
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
