/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import etherealtempest.info.Conveyer;
import com.jme3.asset.AssetManager;
import etherealtempest.FSM.MapFlowState;
import maps.layout.Map;

/**
 *
 * @author night
 */
public class MasterFsmState extends FsmState {
    private AssetManager local;
    private Conveyer conveyor;
    
    private static Map current;
    //private static Cursor currentCursor;
    
    public MasterFsmState() {
        state = MapFlowState.MapDefault;
    }
    
    public MasterFsmState(MapFlowState e) {
        super(e);
        state = e;
    }
    
    public MasterFsmState setAssetManager(AssetManager AM) {
        local = AM;
        return this;
    }
    
    public MasterFsmState setConveyer(Conveyer C) {
        conveyor = C;
        return this;
    }
    
    public Conveyer getConveyer() { return conveyor; }
    
    public AssetManager getAssetManager() {
        return local;
    }
    
    public MasterFsmState updateState(MapFlowState es) {
        state = es;
        return this;
    }
    
    public static Map getCurrentMap() { return current; }
    //public static Cursor getCurrentCursor() { return currentCursor; }
    
    public static void setCurrentDefaultMap(Map M) { current = M; }
    //public static void setCurrentDefaultCursor(Cursor C) { currentCursor = C; }
    
}
