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
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.LayerComparator;
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
public class Cursor extends Node {
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
    public int pX, pY;
    
    public enum Purpose {
        WeaponAttack,
        SkillAttack,
        EtherAttack,
        EtherSupport,
        Trade,
        None
    }
    
    private final Node pointerParent = new Node();
    private final Node pointerParentParent = new Node();
    private final Node pointer;
    private final Geometry geometry;
    private final Quad quad;
    
    private final float desiredPointerRotY, desiredPointerPosY;
    
    private Material pointerMat;
    
    private float toTraverseX = 0, toTraverseY = 0;
    private float cursorSpeed = 1.5f;
    private boolean translatingX = false, translatingY = false, isBacking = false, isCorrected = true;
    private Purpose purpose = Purpose.None;

    private Vector3f preferredLocation;
    private final RangeDisplay rangeDisplay;
    
    public TangibleUnit receivingEnd, selectedUnit;
    
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
    
    private FSM<CursorState> fsm = new FSM<CursorState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<CursorState> st) {
            if (st.getEnum() != CursorState.AnyoneHovered || (st.getEnum() == CursorState.AnyoneHovered && state.getEnum() == CursorState.CursorDefault)) {
                state = st; //maybe change this if needed
                geometry.getMaterial().setColor("Color", cursorColor.get(state.getEnum()));
                pointerMat.setColor("Color", cursorColor.get(state.getEnum()));
                
                switch (st.getEnum()) {
                    case AnyoneSelectingTarget:
                        selectionDifferenceX = 0;
                        selectionDifferenceY = 0;
                        break;
                    case CursorDefault:
                        rangeDisplay.cancelRange(elv);
                        break;
                    default:
                        break;
                }
            }
            
            if (st.getEnum() == CursorState.Idle) {
                interpreter.clear();
            }
        }
        
        @Override
        public void forceState(FsmState<CursorState> st) {
            if (st.getEnum() == CursorState.Idle) {
                interpreter.clear();
            }
            
            state = st;
            geometry.getMaterial().setColor("Color", cursorColor.get(state.getEnum()));
        }
    };
    
    public Cursor(AssetManager assetManager) {
        quad = new Quad(16f, 16f);
        geometry = new Geometry("Quad", quad);
        rangeDisplay = new RangeDisplay(MasterFsmState.getCurrentMap(), assetManager);
        
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        geometry.setLocalRotation(rotation);
        geometry.setQueueBucket(Bucket.Transparent);
        
        Material pcs = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pcs.setTexture("ColorMap", assetManager.loadTexture("Textures/gui/cursor.png")); //used to be "Models/Sprites/unfinished/Map/tpCursor.png"
        pcs.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        pcs.getAdditionalRenderState().setDepthWrite(false);
        geometry.setMaterial(pcs);
        LayerComparator.setLayer(geometry, 3);
        
        attachChild(geometry);
        
        pointer = (Node)assetManager.loadModel("Models/General/pointer.gltf");
        pointerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pointerMat.setColor("Color", DEFAULT_COLOR);
        pointerMat.getAdditionalRenderState().setWireframe(true);
        pointer.setMaterial(pointerMat);
        pointer.move(0.5f * (1f / 0.85f), 0, -0.13f);
        
        pointerParent.scale(2.75f);
        pointerParent.move(5.2f, 0, 9.411f);
        pointerParentParent.move(0, 45, 0);
        pointerParentParent.scale(0.85f);
        
        desiredPointerRotY = pointerParent.getLocalRotation().getY();
        desiredPointerPosY = pointerParent.getLocalTranslation().y;
        
        Quaternion rot = new Quaternion();
        rot.fromAngles(0, 0, FastMath.PI / -3f);
        pointerParentParent.setLocalRotation(rot);
        
        pointerParentParent.attachChild(pointerParent);
        pointerParent.attachChild(pointer);
        attachChild(pointerParentParent);
        
        fsm.forceState(CursorState.CursorDefault);
    }
    
    public FsmState getState() {
        return fsm.getState();
    }
    
    public void setStateIfAllowed(CursorState cs) { fsm.setNewStateIfAllowed(cs); }
    public void forceState(CursorState cs) { fsm.forceState(cs); }
    
    public int getPosX() { return pX; }
    public int getPosY() { return pY; }
    public int getElevation() { return elv; }
    
    public Coords coords() { return new Coords(pX, pY); }
    
    public Node getPointer() { return pointer; }
    public Geometry getCursorGeometry() { return geometry; }
    public Quad getQuad() { return quad; }
    
    public float getSpeed() { return cursorSpeed; }
    
    public void setSpeed(float speed) { //default is 1
        cursorSpeed = speed;
    }
    
    public void setPosition(int x, int y, int layer) {
        Map map = MasterFsmState.getCurrentMap();
        if (x < map.getXLength(elv) && x >= 0 && y < map.getYLength(elv) && y >= 0) {
            pX = x;
            pY = y;
            elv = layer;
        
            setLocalTranslation(map.fullmap[layer][x][y].getWorldTranslation().x, 0.01f, map.fullmap[layer][x][y].getWorldTranslation().z);
        }
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
    
    public void translateX(int spaces) { //negative spaces for left, positive spaces for right
        if (MasterFsmState.getCurrentMap().getBounds().isWithinXBounds(pX + spaces, elv) && (selectedUnit == null || (selectedUnit != null && selectedUnit.getFSM().getEnumState(UnitState.class) != UnitState.Moving))) {
            setLocalTranslation(getLocalTranslation().x, 1.5f, getLocalTranslation().z);
            translatingX = true;
            toTraverseX += spaces;
            
            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { selectionDifferenceX += spaces; }
            
            //System.out.println("(" + (pX + spaces) + ", " + pY + ")");
        }
    }
    
    public void translateY(int spaces) { //negative spaces for down, positive spaces for up
        if (MasterFsmState.getCurrentMap().getBounds().isWithinYBounds(pY + spaces, elv) && (selectedUnit == null || (selectedUnit != null && selectedUnit.getFSM().getEnumState(UnitState.class) != UnitState.Moving))) {
            setLocalTranslation(getLocalTranslation().x, 1.5f, getLocalTranslation().z);
            translatingY = true;
            toTraverseY += spaces;
            
            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { selectionDifferenceY += spaces; }
            
            //System.out.println("(" + pX + ", " + (pY + spaces) + ")");
        }
    }
    
    private float accumulatedCursorDistanceX = 0, accumulatedCursorDistanceY = 0;
    private int frameCount = 0;
    
    private float getSign(float input) {
        return (input > 0) ? 1 : (input < 0 ? -1 : 0);
    }
    
    public void update(float tpf, MasterFsmState mapFSM) {
        preferredLocation = new Vector3f(MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getWorldTranslation().x, 0.01f, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getWorldTranslation().z);
        
        setLocalTranslation(getLocalTranslation().x, preferredLocation.y, getLocalTranslation().z);
        if ((getLocalTranslation().x != preferredLocation.x || getLocalTranslation().z != preferredLocation.z)) {
            move(((preferredLocation.x - getLocalTranslation().x) / 10f), 0, ((preferredLocation.z - getLocalTranslation().z) / 10f));
            
            isCorrected = ((Math.abs(preferredLocation.x - getLocalTranslation().x) < 2f) && Math.abs(preferredLocation.z - getLocalTranslation().z) < 2f);
            if (isCorrected) { isBacking = false; }
        }
        
        //cursor moving in the X direction
        if (accumulatedCursorDistanceX >= 16) {
            translatingX = false;
            pX += getSign(toTraverseX);
            accumulatedCursorDistanceX = 0;
            toTraverseX = 0;
        } else if (translatingX) {
            move(0, 0, cursorSpeed * 2 * getSign(toTraverseX));
            accumulatedCursorDistanceX += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseX));
        }
        
        //cursor moving in the Y direction
        if (accumulatedCursorDistanceY >= 16) {
            translatingY = false;
            pY += getSign(toTraverseY);
            accumulatedCursorDistanceY = 0;
            toTraverseY = 0;
        } else if (translatingY) {
            move(cursorSpeed * 2 * getSign(toTraverseY), 0, 0);
            accumulatedCursorDistanceY += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseY));
        }
        
        TangibleUnit specified = MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getOccupier();
        
        if (specified != null) {
            updateAIForInteraction(tpf, specified);
        } else if (fsm.getState().getEnum() != CursorState.AnyoneSelected) {
            rangeDisplay.cancelRange(elv);
        }
        
        if (!isBacking || isCorrected) {
            interpreter.update(tpf);
        }
        
        Quaternion pointerRotation = new Quaternion();
        if (selectedUnit != null) {
            float factor = 0.035f * frameCount;
            pointerRotation.fromAngles(0, factor, 0);
        } else {
            pointerRotation.fromAngles(0, desiredPointerRotY, 0);
        }
        
        pointerParent.setLocalRotation(pointerRotation);
        pointerParent.setLocalTranslation(pointerParent.getLocalTranslation().x, desiredPointerPosY + (2 * FastMath.sin(0.035f * frameCount)), pointerParent.getLocalTranslation().z);
        
        geometry.getMaterial().setColor("Color", updateLighting(0.0375f));
        
        if (frameCount == Integer.MAX_VALUE) { frameCount = 0; }
        
        frameCount++;
    }
    
    private ColorRGBA updateLighting(float omega) {
        return cursorColor.get(fsm.getEnumState()).mult(0.075f * FastMath.cos(omega * frameCount) + 0.925f);  
    }
    
    public void updateAIForInteraction(float tpf, TangibleUnit tu) {
        //System.out.println(fsm.getState().getEnum() + ", " + (tu.getFSM().getState().getEnum() == EntityState.SelectingTarget));
        
        if (coords().equals(tu.coords()) && elv == tu.getElevation() && tu.getFSM().getState().getEnum() == UnitState.Active) { //if hovered
            fsm.setNewStateIfAllowed(CursorState.AnyoneHovered);
            
            if (fsm.getState().getEnum() == CursorState.AnyoneHovered || (fsm.getState().getEnum() == CursorState.AnyoneSelected && tu.isSelected)) {
                rangeDisplay.displayRange(tu, fsm.getEnumState(), elv);
            }
        } else if (!tu.isSelected && fsm.getState().getEnum() != CursorState.AnyoneSelected) { //if nobody is selected nor is this unit selected
            fsm.setNewStateIfAllowed(CursorState.CursorDefault); //cancels range too
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
                        rangeDisplay.updateOpacity(fsm.getEnumState());
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
                                selectedUnit.remapPositions(pX - selectionDifferenceX, pY - selectionDifferenceY, elv);
                                return new MasterFsmState(MapFlowState.PreBattle).setConveyer(new Conveyer(selectedUnit).setEnemyUnit(receivingEnd).createCombatants());
                            } else { //selection
                                tu.isSelected = true;
                                selectedUnit = tu;
                                fsm.setNewStateIfAllowed(CursorState.AnyoneSelected);
                            }
                        }
                    }
                }
            }
            
            if (name.equals("deselect")) {
                if (keyPressed) {
                    if (fsm.getState().getEnum() == CursorState.AnyoneSelected) {
                        //fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        fsm.setNewStateIfAllowed(CursorState.CursorDefault);
                        rangeDisplay.updateOpacity(fsm.getEnumState());
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
    
    private int selectionDifferenceX = 0, selectionDifferenceY = 0;
    
    public void resetCursorPositionFromSelection() {
        pX -= selectionDifferenceX;
        pY -= selectionDifferenceY;
        setLocalTranslation(getLocalTranslation().x - (16f * selectionDifferenceY), getLocalTranslation().y, getLocalTranslation().z - (16f * selectionDifferenceX));
        selectionDifferenceX = 0;
        selectionDifferenceY = 0;
    }
    
    public void resetState() { //after battle
        rangeDisplay.cancelRange(elv);
        resetCursorPositionFromSelection();
        selectedUnit.remapPositions(pX, pY, elv);
        selectedUnit.setAnimationState(AnimationState.Idle);
        selectedUnit.setStateIfAllowed(UnitState.Done);
        selectedUnit.isSelected = false;
        selectedUnit = null;
        fsm.forceState(CursorState.CursorDefault);
    }
    
    public void goBackFromMenu() {
        fsm.forceState(CursorState.AnyoneSelected);
        selectedUnit.remapPositions(selectedUnit.prevX, selectedUnit.prevY, elv);
        selectedUnit.setAnimationState(AnimationState.Idle);
        selectedUnit.setStateIfAllowed(UnitState.Active);
        rangeDisplay.displayRange(selectedUnit, fsm.getEnumState(), elv);
    }
    
    public Purpose getPurpose() { return purpose; }
    
    public void setPurpose(Purpose P) {
        purpose = P;
    }
}
