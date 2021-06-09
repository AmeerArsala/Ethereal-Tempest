/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import com.jme3.math.ColorRGBA;
import maps.layout.occupant.Cursor;

/**
 *
 * @author night
 * @param <T> enum type
 */
public abstract class FSM<T extends Enum> {
    protected FsmState<T> state;
    
    public FSM() {}
    
    public FSM(FsmState<T> state) {
        this.state = state;
    }
    
    public FSM(T st) {
        state = new FsmState<>(st);
    }
    
    public abstract void setNewStateIfAllowed(FsmState<T> st); //does not set last enum unless it is abstracted as such
    
    public void setNewStateIfAllowed(T st) {
        if (state != null) {
            setNewStateIfAllowed(new FsmState<>(st, state.getEnum()));
        } else {
            setNewStateIfAllowed(new FsmState<>(st));
        }
    }
    
    //does not set last enum
    public void forceState(FsmState<T> st) {
        state = st;
    }
    
    public void forceState(T st) {
        if (state != null) {
            state = new FsmState<>(st, state.getEnum());
        } else {
            state = new FsmState<>(st);
        }
    }
    
    public FsmState<T> getState() {
        return state;
    }
    
    public T getEnumState() {
        return state.getEnum();
    }
    
    public T getLastEnumState() {
        return state.getLastEnum();
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
        CursorDefault(Cursor.DEFAULT_COLOR),
        AnyoneHovered(Cursor.DEFAULT_COLOR),
        AnyoneSelected(Cursor.SELECTING_MOVE_SQUARE),
        AnyoneMoving(Cursor.DEFAULT_COLOR),
        AnyoneSelectingTarget(Cursor.SELECTING_ATTACK_TARGET),
        AnyoneTargeted(Cursor.DEFAULT_COLOR),
        Idle(Cursor.MISC_COLOR);
        
        private final ColorRGBA correspondingColor;
        private CursorState(ColorRGBA color) {
            correspondingColor = color;
        }
        
        public ColorRGBA getCorrespondingColor() {
            return correspondingColor;
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
