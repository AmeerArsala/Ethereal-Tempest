/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant;

import maps.layout.tile.RangeDisplay;
import etherealtempest.info.Conveyer;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import etherealtempest.FSM;
import etherealtempest.FSM.CursorState;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FSM.UnitState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import general.ComplexInputReader;
import general.Spritesheet.AnimationState;
import java.util.HashMap;
import maps.layout.Coords;
import maps.layout.Map;

/**
 *
 * @author night
 */
public class Cursor {
    static final HashMap<CursorState, ColorRGBA> cursorColor = new HashMap<>();
    
    static final ColorRGBA DEFAULT_COLOR = new ColorRGBA(0.9176f, 0.8509f, 0, 1f);
    static final ColorRGBA SELECTING_MOVE_SQUARE = new ColorRGBA(1f, 0.6784f, 0.1176f, 1f);
    static final ColorRGBA SELECTING_ATTACK_TARGET = new ColorRGBA(1f, 0f, 0.549f, 1f);
    static final ColorRGBA SELECTING_SUPPORT_TARGET = new ColorRGBA(0, 1, 0, 1);
    static final ColorRGBA MISC_COLOR = new ColorRGBA(1, 1, 1, 1);
    
    static {
        for (CursorState state : CursorState.values()) {
            switch (state) {
                case AnyoneSelected:
                    cursorColor.put(state, SELECTING_MOVE_SQUARE);
                    break;
                case AnyoneSelectingTarget:
                    cursorColor.put(state, SELECTING_ATTACK_TARGET);
                    break;
                default:
                    cursorColor.put(state, DEFAULT_COLOR);
                    break;
            }
        }
    }
    
    private int elv = 0;
    
    public final Geometry geometry;
    private final Quad quad;
    
    public int pX, pY;
    
    private float toTraverseX = 0, toTraverseY = 0;
    private float cursorSpeed = 1.5f;
    private boolean translatingX = false, translatingY = false, isBacking = false, isCorrected = true;
    private Purpose purpose = Purpose.None;

    private Vector3f preferredLocation;
    private final RangeDisplay rangeDisplay;
    
    private static final float BUTTON_HOLD_TIME = 0.25f;
    private static final float DEFAULT_CURSOR_SPEED = 1.5f, HELD_CURSOR_SPEED = 1.25f;
    
    protected ComplexInputReader<Direction> interpreter = 
            new ComplexInputReader<Direction>(true, BUTTON_HOLD_TIME) {
                @Override
                public void protocolProcedure(Direction inputHeld) {
                    if (fsm.getState().getEnum() != CursorState.Idle && getHeldTime(inputHeld) > BUTTON_HOLD_TIME) {
                        tryTranslate(inputHeld, 1, amountOfKeysPressed() == 1 || !keyIsHeld(inputHeld.getConflicter()));
                    }
                }
            }
            .link(Direction.Up, "move up")
            .link(Direction.Down, "move down")
            .link(Direction.Left, "move left")
            .link(Direction.Right, "move right");
    
    public TangibleUnit receivingEnd, selectedUnit;
    
    public Cursor(AssetManager assetManager) {
        quad = new Quad(16f, 16f);
        geometry = new Geometry("Quad", quad);
        rangeDisplay = new RangeDisplay(MasterFsmState.getCurrentMap(), assetManager);
        
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        geometry.setLocalRotation(rotation);
        geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        Material pcs = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pcs.setTexture("ColorMap", assetManager.loadTexture("Textures/gui/cursor.png")); //used to be "Models/Sprites/unfinished/Map/tpCursor.png"
        pcs.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geometry.setMaterial(pcs);
        
        fsm.forceState(CursorState.CursorDefault);
    }
    
    public enum Direction {
        Up,
        Down,
        Left,
        Right;
        
        private Direction conflicter;
        
        private void setConflicter(Direction conflict) {
            conflicter = conflict;
        }
        
        static {
            Up.setConflicter(Down);
            Down.setConflicter(Up);
            Left.setConflicter(Right);
            Right.setConflicter(Left);
        }
        
        public Direction getConflicter() {
            return conflicter;
        }
    }
    
    public enum Purpose {
        WeaponAttack,
        SkillAttack,
        EtherAttack,
        EtherSupport,
        Trade,
        None
    }
    
    private FSM<CursorState> fsm = new FSM<CursorState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<CursorState> st) {
            if (st.getEnum() != CursorState.AnyoneHovered || (st.getEnum() == CursorState.AnyoneHovered && state.getEnum() == CursorState.CursorDefault)) {
                state = st; //maybe change this if needed
                geometry.getMaterial().setColor("Color", cursorColor.get(state.getEnum()));
                
                if (st.getEnum() == CursorState.AnyoneSelectingTarget) {
                    selectionDifferenceX = 0;
                    selectionDifferenceY = 0;
                }
            } 
        }
        
        @Override
        public void forceState(FsmState<CursorState> st) {
            state = st;
            geometry.getMaterial().setColor("Color", cursorColor.get(state.getEnum()));
        }
    };
    
    public void setPosition(int x, int y, int layer) {
        Map map = MasterFsmState.getCurrentMap();
        if (x < map.getXLength(elv) && x >= 0 && y < map.getYLength(elv) && y >= 0) {
            pX = x;
            pY = y;
            elv = layer;
        
            geometry.setLocalTranslation(map.fullmap[layer][x][y].getWorldTranslation().x, 0.01f, map.fullmap[layer][x][y].getWorldTranslation().z);
        }
    }
    
    public int getPosX() { return pX; }
    public int getPosY() { return pY; }
    public int getElevation() { return elv; }
    
    public Coords coords() { return new Coords(pX, pY); }
    
    public int selectionDifferenceX = 0, selectionDifferenceY = 0;
    
    public void resetCursorPositionFromSelection() {
        pX -= selectionDifferenceX;
        pY -= selectionDifferenceY;
        geometry.setLocalTranslation(geometry.getLocalTranslation().x - (16f * selectionDifferenceY), geometry.getLocalTranslation().y, geometry.getLocalTranslation().z - (16f * selectionDifferenceX));
        selectionDifferenceX = 0;
        selectionDifferenceY = 0;
    }
    
    public void translate(Direction dir, int spaces) {
        switch (dir) {
            case Up:
                translateY(spaces);
                break;
            case Down:
                translateY(spaces * -1);
                break;
            case Left:
                translateX(spaces * -1);
                break;
            case Right:
                translateX(spaces);
                break;
        }
    }
    
    public void translateX(int spaces) { //negative spaces for left, positive spaces for right
        if (pX + spaces >= MasterFsmState.getCurrentMap().getMinimumX(elv) && pX + spaces < MasterFsmState.getCurrentMap().getXLength(elv)) {
            geometry.setLocalTranslation(geometry.getLocalTranslation().x, 1.5f, geometry.getLocalTranslation().z);
            translatingX = true;
            toTraverseX += spaces;
            
            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { selectionDifferenceX += spaces; }
        }
    }
    
    public void translateY(int spaces) { //negative spaces for down, positive spaces for up
        if (pY + spaces >= MasterFsmState.getCurrentMap().getMinimumY(elv) && pY + spaces < MasterFsmState.getCurrentMap().getYLength(elv)) {
            geometry.setLocalTranslation(geometry.getLocalTranslation().x, 1.5f, geometry.getLocalTranslation().z);
            translatingY = true;
            toTraverseY += spaces;
            
            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { selectionDifferenceY += spaces; }
        }
    }
    
    public void updateAI(float tpf) {
        switch (fsm.getState().getEnum()) {
            case CursorDefault: 
            {
                rangeDisplay.tileOpacity = 0;
                break;
            }
            case AnyoneHovered: 
            {
                rangeDisplay.tileOpacity = 0.5f;
                break;
            }
            case AnyoneMoving: 
            {
                rangeDisplay.tileOpacity = 0;
                break;
            }
            case AnyoneSelected:
            {
                rangeDisplay.tileOpacity = 0.85f;
                break;
            }
            case AnyoneSelectingTarget: 
            {
                rangeDisplay.tileOpacity = 0;
                break;
            }
            case AnyoneTargeted: 
            {
                rangeDisplay.tileOpacity = 0;
                break;
            }
            default:
                break;
        }
    }
    
    private float accumulatedCursorDistanceX = 0, accumulatedCursorDistanceY = 0;
    private int frameCount = 0;
    
    private float getSign(float input) {
        return (input > 0) ? 1 : (input < 0 ? -1 : 0);
    }
    
    public void update(float tpf, MasterFsmState mapFSM) {
        preferredLocation = new Vector3f(MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getWorldTranslation().x, 0.01f, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getWorldTranslation().z);
        geometry.setLocalTranslation(geometry.getLocalTranslation().x, preferredLocation.y, geometry.getLocalTranslation().z);
        if ((geometry.getLocalTranslation().x != preferredLocation.x || geometry.getLocalTranslation().z != preferredLocation.z)) {
            geometry.move(((preferredLocation.x - geometry.getLocalTranslation().x) / 10f), 0, ((preferredLocation.z - geometry.getLocalTranslation().z) / 10f));
            
            isCorrected = ((Math.abs(preferredLocation.x - geometry.getLocalTranslation().x) < 2f) && Math.abs(preferredLocation.z - geometry.getLocalTranslation().z) < 2f);
            if (isCorrected) { isBacking = false; }
        }
        
        //cursor moving in the X direction
        if (accumulatedCursorDistanceX >= 16) {
            translatingX = false;
            pX += getSign(toTraverseX);
            accumulatedCursorDistanceX = 0;
            toTraverseX = 0;
        } else if (translatingX) {
            geometry.move(0, 0, cursorSpeed * 2 * getSign(toTraverseX));
            accumulatedCursorDistanceX += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseX));
        }
        
        //cursor moving in the Y direction
        if (accumulatedCursorDistanceY >= 16) {
            translatingY = false;
            pY += getSign(toTraverseY);
            accumulatedCursorDistanceY = 0;
            toTraverseY = 0;
        } else if (translatingY) {
            geometry.move(cursorSpeed * 2 * getSign(toTraverseY), 0, 0);
            accumulatedCursorDistanceY += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseY));
        }
        
        TangibleUnit specified = MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getOccupier();
        
        if (specified != null) {
            updateAIForInteraction(tpf, specified);
            updateAI(tpf);
        } else if (fsm.getState().getEnum() != CursorState.AnyoneSelected) {
            rangeDisplay.cancelRange(elv);
        }
        
        if (!isBacking || isCorrected) {
            interpreter.update(tpf);
        }
        
        geometry.getMaterial().setColor("Color", updateLighting(0.0375f));
        
        if (frameCount > 1000) { frameCount = 0; }
        
        frameCount++;
    }
    
    private ColorRGBA updateLighting(float omega) { return cursorColor.get(fsm.getEnumState()).mult(0.075f * FastMath.cos(omega * frameCount) + 0.925f);  }
    
    public void updateAIForInteraction(float tpf, TangibleUnit tu) {
        //System.out.println(fsm.getState().getEnum() + ", " + (tu.getFSM().getState().getEnum() == EntityState.SelectingTarget));
        
        if (pX == tu.getPosX() && pY == tu.getPosY() && tu.getFSM().getState().getEnum() == UnitState.Active) {
            if (fsm.getState().getEnum() != CursorState.AnyoneHovered) {
                fsm.setNewStateIfAllowed(CursorState.AnyoneHovered);
                tu.hoverSetter = true;
            }
            if (fsm.getState().getEnum() != CursorState.AnyoneSelectingTarget) {
                if (fsm.getState().getEnum() != CursorState.AnyoneSelected || (fsm.getState().getEnum() == CursorState.AnyoneSelected && tu.isSelected)) {
                    rangeDisplay.displayRange(tu, elv);
                }
            } else if (!tu.isSelected) {
                tu.hoverSetter = false;
            }
        } else if (!tu.isSelected && fsm.getState().getEnum() != CursorState.AnyoneSelected) { //if nobody is selected
            if (fsm.getState().getEnum() != CursorState.AnyoneHovered) { //if nobody is selected or hovered
                rangeDisplay.cancelRange(elv);
            } else { //if nobody is selected but someone is hovered yet the cursor isnt there
                if (tu.hoverSetter) {
                    fsm.setNewStateIfAllowed(CursorState.CursorDefault);
                    tu.hoverSetter = false;
                }
            }
        }
        
        /*if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget && tu.getFSM().getState().getEnum() == EntityState.SelectingTarget) {
            //put something here later
        }*/
    }
    
    public MasterFsmState resolveInput(String name, float tpf, boolean keyPressed) {
        if (!interpreter.anyKeyPressed() && keyPressed) {
            cursorSpeed = DEFAULT_CURSOR_SPEED;
        }
        
        interpreter.obtainInput(name, tpf, keyPressed);
        
        if (fsm.getState().getEnum() != CursorState.Idle && (!isBacking || isCorrected)) {
            if (name.equals("move up") && keyPressed) {
                translateY(1);
            } 
            if (name.equals("move down") && keyPressed) {
                translateY(-1);
            }  
            if (name.equals("move left") && keyPressed) {
                translateX(-1);
            } 
            if (name.equals("move right") && keyPressed) {
                translateX(1);
            }
            
            if (name.equals("select")) {
                if (keyPressed) {
                    if (fsm.getState().getEnum() == CursorState.AnyoneSelected) {
                        rangeDisplay.tileOpacity = 0.85f;
                    }
                
                    TangibleUnit tu = MasterFsmState.getCurrentMap().fullmap[getElevation()][pX][pY].getOccupier();
                    
                    //for moving
                    if (tu == null && fsm.getState().getEnum() == CursorState.AnyoneSelected) {
                        if (Map.isWithinSpaces(selectedUnit.getMobility(), selectedUnit.getPosX(), selectedUnit.getPosY(), pX, pY)) {
                            fsm.setNewStateIfAllowed(CursorState.AnyoneMoving);
                            selectedUnit.getFSM().setNewStateIfAllowed(new MoveState().setMapAndCursor(MasterFsmState.getCurrentMap(), this));
                        }
                    }
                
                    //for selection and targeting
                    if (tu != null && pX == tu.getPosX() && pY == tu.getPosY()) {
                        if (!tu.isSelected) {
                            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { //if the unit selected is an enemy being targeted
                                receivingEnd = tu; //modify this later
                                fsm.setNewStateIfAllowed(CursorState.AnyoneTargeted);
                                //start a battle
                                interpreter.clear();
                                return new MasterFsmState(MapFlowState.PreBattle).setConveyer(new Conveyer(selectedUnit).setEnemyUnit(receivingEnd));
                            } else { //selection
                                tu.isSelected = true;
                                selectedUnit = tu;
                                fsm.setNewStateIfAllowed(CursorState.AnyoneSelected);
                            }
                        }
                    }
                }
            } else {
                if (selectedUnit == null) {
                    if (fsm.getState().getEnum() == CursorState.AnyoneHovered) {
                        rangeDisplay.tileOpacity = 0.5f;
                    } else { rangeDisplay.tileOpacity = 0f; }
                } else {
                    rangeDisplay.tileOpacity = 0.85f;
                }
            }
            if (name.equals("deselect")) {
                if (keyPressed) {
                    if (fsm.getState().getEnum() == CursorState.AnyoneSelected) {
                        //fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        fsm.setNewStateIfAllowed(CursorState.CursorDefault);
                        rangeDisplay.tileOpacity = 0.5f;
                        pX = selectedUnit.getPosX();
                        pY = selectedUnit.getPosY();
                        selectedUnit.isSelected = false;
                        selectedUnit = null;
                        isBacking = true;
                    }
                }
            }
        }
        
        return null;
    }
    
    public FsmState getState() {
        return fsm.getState();
    }
    
    public void setStateIfAllowed(CursorState cs) {
        fsm.setNewStateIfAllowed(cs);
    }
    
    public void forceState(CursorState cs) {
        fsm.forceState(cs);
    }
    
    public void resetState(Map M) {
        M.fullmap[elv][selectedUnit.getPosX()][selectedUnit.getPosY()].resetOccupier();
        resetCursorPositionFromSelection();
        selectedUnit.remapPositions(pX, pY, elv, M);
        selectedUnit.setAnimationState(AnimationState.Idle);
        rangeDisplay.cancelRange(elv);
        M.fullmap[elv][selectedUnit.getPosX()][selectedUnit.getPosY()].setOccupier(selectedUnit);
        selectedUnit.isSelected = false;
        selectedUnit.setStateIfAllowed(UnitState.Done);
        selectedUnit = null;
        fsm.forceState(CursorState.CursorDefault);
    }
    
    public void goBackFromMenu() {
        fsm.forceState(CursorState.AnyoneSelected);
        selectedUnit.remapPositions(selectedUnit.prevX, selectedUnit.prevY, elv, MasterFsmState.getCurrentMap());
        selectedUnit.setAnimationState(AnimationState.Idle);
        selectedUnit.setStateIfAllowed(UnitState.Active);
        rangeDisplay.tileOpacity = 0.85f;
        rangeDisplay.displayRange(selectedUnit, elv);
    }
    
    public void tryTranslate(Direction dir, int spaces, boolean overrideCondition) {
        switch (dir) {
            case Up:
                if (!translatingY || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateY(spaces); 
                }
                break;
            case Down:
                if (!translatingY || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateY(spaces * -1);
                }
                break;
            case Left:
                if (!translatingX || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateX(spaces * -1);
                }
                break;
            case Right:
                if (!translatingX || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateX(spaces);
                }
                break;
        }
    }
    
    public void tryContinueX() {
        if (interpreter.keyIsHeld(Direction.Left)) {
            translateX(-1);
        } else if (interpreter.keyIsHeld(Direction.Right)) {
            translateX(1);
        }
    }
    
    public void tryContinueY() {
        if (interpreter.keyIsHeld(Direction.Up)) {
            translateY(1);
        } else if (interpreter.keyIsHeld(Direction.Down)) {
            translateY(-1);
        } 
    }
    
    public void updatePosition(Map map) {
        int closestX = 0, closestY = 0;
        for (int x = 0; x < map.getXLength(elv); x++) {
            for (int y = 0; y < map.getYLength(elv); y++) {
                if (FastMath.abs(map.fullmap[elv][x][y].getWorldTranslation().x) + FastMath.abs(map.fullmap[elv][x][y].getWorldTranslation().y) < FastMath.abs(map.fullmap[elv][closestX][closestY].getWorldTranslation().x) + FastMath.abs(map.fullmap[elv][closestX][closestY].getWorldTranslation().y)) {
                    closestX = x;
                    closestY = y;
                }
            }
        }
        pX = closestX;
        pY = closestY;
    }
    
    public float getSpeed() { return cursorSpeed; } 
    
    public void setSpeed(float speed) { //default is 1
        cursorSpeed = speed;
    }
    
    public Purpose getPurpose() { return purpose; }
    
    public void setPurpose(Purpose P) {
        purpose = P;
    }
}
