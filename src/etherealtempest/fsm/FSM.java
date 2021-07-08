/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.fsm;

import com.jme3.math.ColorRGBA;
import maps.layout.occupant.control.CursorFSM;

/**
 *
 * @author night
 * @param <T> enum type
 */
public abstract class FSM<T extends Enum> {
    private FsmState<T> state, lastState = null;
    
    public FSM() {}
    
    public FSM(FsmState<T> state, boolean callOnStateSet) {
        this.state = state;
        
        if (callOnStateSet) {
            onStateSet();
        }
    }
    
    public FSM(T st, boolean callOnStateSet) {
        state = new FsmState<>(st);
        
        if (callOnStateSet) {
            onStateSet();
        }
    }
    
    public abstract boolean stateAllowed(FsmState<T> st);
    public abstract void onStateSet(FsmState<T> currentState, FsmState<T> previousState);
    
    private void onStateSet() {
        onStateSet(state, lastState);
    }
    
    //OVERRIDE IF NEEDED
    protected void onAttemptStateSet(FsmState<T> st) {}
    
    //returns whether the state was set
    public final boolean setNewStateIfAllowed(FsmState<T> st) {
        onAttemptStateSet(st);
        
        boolean stateSet = stateAllowed(st);
        if (stateSet) {
            if (state != null) {
                lastState = state.copy();
            }
        
            state = st;
            onStateSet(state, lastState);
        }
        
        return stateSet;
    }
    
    //returns whether the state was set
    public final boolean setNewStateIfAllowed(T st) {
        return setNewStateIfAllowed(new FsmState<>(st));
    }
    
    public void forceState(FsmState<T> st) {
        onAttemptStateSet(st);
        
        if (state != null) {
            lastState = state.copy();
        }
        
        state = st;
    }
    
    
    public final void forceState(T st) {
        forceState(new FsmState<>(st));
    }
    
    public FsmState<T> getState() {
        return state;
    }
    
    public FsmState<T> getLastState() {
        return lastState;
    }
    
    public T getEnumState() {
        return state.getEnum();
    }
    
    public T getLastEnumState() {
        return lastState.getEnum();
    }
    
    public enum GameState {
        
    }
    
    public enum LevelState {
        StartingLevel,
        Preparations,
        DuringLevel,
        Complete;
    }
    
    public enum MapFlowState { //used for actual map flow, the map opening gui, and gui
        Idle,
        
        MapDefault,
        BeginningOfTurn,
        
        PreBattle,
        DuringBattle,
        PostBattle,
        
        //StatScreen states
        StatScreenOpened,
        StatScreenSelecting,
        
        GuiClosed
    }
    
    public enum CursorState {
        //Cursor states
        CursorDefault(CursorFSM.DEFAULT_COLOR),
        AnyoneHovered(CursorFSM.DEFAULT_COLOR, 0.5f),
        AnyoneSelected(CursorFSM.SELECTING_MOVE_SQUARE, 0.85f),
        AnyoneMoving(CursorFSM.DEFAULT_COLOR),
        AnyoneSelectingTarget(CursorFSM.SELECTING_ATTACK_TARGET),
        AnyoneTargeted(CursorFSM.DEFAULT_COLOR),
        Idle(CursorFSM.MISC_COLOR);
        
        private final ColorRGBA correspondingColor;
        private final float tileOpacity;
        
        private CursorState(ColorRGBA color) {
            correspondingColor = color;
            tileOpacity = 0f;
        }
        
        private CursorState(ColorRGBA color, float opacity) {
            correspondingColor = color;
            tileOpacity = opacity;
        }
        
        public ColorRGBA getCorrespondingColor() {
            return correspondingColor;
        }
        
        public float getCorrespondingTileOpacity() {
            return tileOpacity;
        }
    }
    
    public enum UnitState {
        //TangibleUnit states
        Idle,
        Active,
        Done,
        Dying,
        Dead,
        SelectingTarget
    }
    
    public enum FighterState {
        Fighting,
        ApplyingDeath,
        GainingExp,
        LevelUp, //text pops then transition in
        LevelUpDone, //when you can press select to exit out
        Finished
    }
}
