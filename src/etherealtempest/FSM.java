/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

/**
 *
 * @author night
 */
public abstract class FSM {
    
    protected FsmState state;
    
    public abstract void setNewStateIfAllowed(FsmState st);
    
    public FsmState getState() {
        return state;
    }
    
    public void forceState(FsmState st) {
        state = st;
    }
    
    public enum EntityState {
        //Universal
        Idle,
        Paused,
        
        //Main.java states (Game states)
        TitleScreen,
        InMap,
        DialogueScene,
        WorldMap,
        Town,
        
        //TangibleUnit states
        Moving,
        Active,
        Done,
        Dead,
        SelectingTarget,
        
        //Shared
        GuiClosed,
        
        //Map states (not the class Map)
        MapDefault,
        PreBattle,
        DuringBattle,
        PostBattle,
        
        //ActionMenu states
        PostActionMenuOpened,
        
        //StatScreen states
        StatScreenOpened,
        StatScreenSelecting,
        
        //Cursor states
        CursorDefault,
        AnyoneHovered,
        AnyoneSelected,
        AnyoneMoving,
        AnyoneSelectingTarget,
        AnyoneTargeted
        
    }
    
    /*public static <T> T fsmStateMethodReturn(FsmState fsmst) {
        System.out.println("startreflection");
        T result = null;
        try {
            Method get = fsmst.getClass().getMethod("getMap", null);
            result = (T)get.invoke(fsmst);
            System.out.println("works!");
        }
        catch (NoSuchMethodException e) {} catch (IllegalArgumentException ex) {
            Logger.getLogger(FSM.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("doesn't work");
        } catch (IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(FSM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }*/
    
}
