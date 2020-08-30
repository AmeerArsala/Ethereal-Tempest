/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import battle.Conveyer;
import com.jme3.asset.AssetManager;
import maps.layout.Map;
import etherealtempest.FSM.EntityState;
import maps.layout.Cursor;

/**
 *
 * @author night
 */
public class MasterFsmState extends FsmState {
    private AssetManager local;
    private Conveyer conveyor;
    
    private static Map current;
    private static Cursor currentCursor;
    
    public MasterFsmState() {
        state = EntityState.MapDefault;
    }
    
    public MasterFsmState(EntityState e) {
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
    
    public MasterFsmState updateState(EntityState es) {
        state = es;
        return this;
    }
    
    public static Map getCurrentMap() { return current; }
    public static Cursor getCurrentCursor() { return currentCursor; }
    
    public static void setCurrentDefaultMap(Map M) { current = M; }
    public static void setCurrentDefaultCursor(Cursor C) { currentCursor = C; }
    
    
}
