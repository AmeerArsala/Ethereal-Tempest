/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.control;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FSM.CursorState;
import etherealtempest.fsm.FsmState;
import maps.layout.Coords;

/**
 *
 * @author night
 */
public abstract class CursorFSM extends FSM<CursorState> {
    public enum Purpose {
        WeaponAttack,
        SkillAttack,
        EtherAttack,
        EtherSupport,
        Trade,
        None
    }
    
    public static final ColorRGBA DEFAULT_COLOR = new ColorRGBA(0.9176f, 0.8509f, 0, 1f);
    public static final ColorRGBA SELECTING_MOVE_SQUARE = new ColorRGBA(1f, 0.6784f, 0.1176f, 1f);
    public static final ColorRGBA SELECTING_ATTACK_TARGET = new ColorRGBA(1f, 0f, 0.549f, 1f);
    public static final ColorRGBA SELECTING_SUPPORT_TARGET = new ColorRGBA(0, 1, 0, 1);
    public static final ColorRGBA MISC_COLOR = new ColorRGBA(1f, 1f, 1f, 1f);
    
    private Purpose purpose = Purpose.None;
    
    public boolean translatingX = false, translatingY = false, isBacking = false, isCorrected = true;
    public final Coords selectionDifferenceXY = new Coords(0, 0); //difference in distance (in terms of Tiles) from when the CursorState is CursorState.AnyoneSelectingTarget compared to before it was that
    public final Coords toTraverseXY = new Coords(0, 0); //marks how far the cursor will be traversing in terms of Tiles
    public final Vector2f accumulatedCursorDistanceXY = new Vector2f(0, 0); //accumulated cursor distance to track how far it is in 3D space to traverse a single Tile.SIDE_LENGTH
    
    public CursorFSM() {
        super();
    }
    
    public CursorFSM(FsmState<CursorState> st, boolean callOnStateSet) {
        super(st, callOnStateSet);
    }
    
    public CursorFSM(CursorState st, boolean callOnStateSet) {
        super(st, callOnStateSet);
    }
    
    public Purpose getPurpose() {
        return purpose;
    }
    
    public boolean canTakeInput() {
        return getEnumState() != CursorState.Idle && (!isBacking || isCorrected);
    }
    
    public void setPurpose(Purpose P) {
        purpose = P;
    }
    
    public void updateIsCorrected(boolean corrected) {
        isCorrected = corrected;
        if (isCorrected) {
            isBacking = false;
        }
    }
    
    public void translate(Coords delta) {
        toTraverseXY.addLocal(delta);
        
        if (getEnumState() == CursorState.AnyoneSelectingTarget) { 
            selectionDifferenceXY.addLocal(delta); 
        }
    }
    
    /**
     * 
     * @return deltaX
     */
    public Coords finishTranslatingX() {
        Coords dx = new Coords(Integer.signum(toTraverseXY.x), 0);
        
        translatingX = false;
        accumulatedCursorDistanceXY.x = 0;
        toTraverseXY.x = 0;
        
        return dx;
    }
    
    /**
     * 
     * @return deltaY
     */
    public Coords finishTranslatingY() {
        Coords dy = new Coords(0, Integer.signum(toTraverseXY.y));
        
        translatingY = false;
        accumulatedCursorDistanceXY.y = 0;
        toTraverseXY.y = 0;
        
        return dy;
    }
}
