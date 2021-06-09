/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import etherealtempest.info.Conveyor;
import com.jme3.asset.AssetManager;
import etherealtempest.FSM.MapFlowState;
import maps.layout.MapLevel;

/**
 *
 * @author night
 */
public class MasterFsmState extends FsmState<MapFlowState> {
    private AssetManager local;
    private Conveyor conveyor;
    
    private static MapLevel current;
    //private static Cursor currentCursor;
    
    public MasterFsmState(MapFlowState e) {
        super(e);
    }
    
    public MasterFsmState setAssetManager(AssetManager AM) {
        local = AM;
        return this;
    }
    
    public MasterFsmState setConveyor(Conveyor C) {
        conveyor = C;
        return this;
    }
    
    public Conveyor getConveyor() { return conveyor; }
    
    public AssetManager getAssetManager() {
        return local;
    }
    
    public MasterFsmState updateState(MapFlowState es) {
        state = es;
        return this;
    }
    
    public static MapLevel getCurrentMap() { return current; }
    //public static Cursor getCurrentCursor() { return currentCursor; }
    
    public static void setCurrentDefaultMap(MapLevel M) { current = M; }
    //public static void setCurrentDefaultCursor(Cursor C) { currentCursor = C; }
    
}
