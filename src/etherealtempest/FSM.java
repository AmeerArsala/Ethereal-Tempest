/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

/**
 *
 * @author night
 * @param <T> enum type
 */
public abstract class FSM<T> {
    protected FsmState<T> state;
    
    public abstract void setNewStateIfAllowed(FsmState<T> st);
    
    public void setNewStateIfAllowed(T st) {
        setNewStateIfAllowed(new FsmState<>(st));
    }
    
    public void forceState(FsmState<T> st) {
        state = st;
    }
    
    public void forceState(T st) {
        state = new FsmState<>(st);
    }
    
    public FsmState<T> getState() {
        return state;
    }
    
    public <E> E getEnumState(Class<E> enumClassOfE) {
        return enumClassOfE.cast(state.getEnum());
    }
    
    public T getEnumState() {
        return (T)state.getEnum();
    }
    
    public enum LevelState {
        StartingLevel,
        Preparations,
        DuringLevel,
        Complete;
    }
    
    public enum MapFlowState {
        Idle,
        
        MapDefault,
        SwitchingTurn,
        BeginningOfTurn,
        
        PreBattle,
        DuringBattle,
        PostBattle,
        
        //ActionMenu states
        PostActionMenuOpened,
        
        //StatScreen states
        StatScreenOpened,
        StatScreenSelecting,
        
        GuiClosed
    }
    
    public enum CursorState {
        //Cursor states
        CursorDefault,
        AnyoneHovered,
        AnyoneSelected,
        AnyoneMoving,
        AnyoneSelectingTarget,
        AnyoneTargeted,
        Idle
    }
    
    public enum UnitState {
        //TangibleUnit states
        Idle,
        Moving,
        Active,
        Done,
        Dead,
        SelectingTarget,
        ReceivingEffect //this is for aoe damage to the unit before/after combat, etc.
    }
    
}
