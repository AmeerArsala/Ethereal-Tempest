/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant;

import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.RangeDisplay;
import etherealtempest.info.Conveyor;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.LayerComparator;
import enginetools.MaterialCreator;
import etherealtempest.FSM;
import etherealtempest.FSM.CursorState;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FSM.UnitState;
import etherealtempest.FsmState;
import etherealtempest.GameProtocols;
import etherealtempest.MasterFsmState;
import etherealtempest.geometry.GeometricBody;
import general.ComplexInputReader;
import general.GameTimer;
import general.procedure.SimpleOrdinalQueue;
import general.procedure.functional.SimpleProcedure;
import maps.layout.occupant.character.Spritesheet.AnimationState;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.OnTile;
import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public class Cursor extends Node implements OnTile {
    public enum Purpose {
        WeaponAttack,
        SkillAttack,
        EtherAttack,
        EtherSupport,
        Trade,
        None
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
    
    public static final ColorRGBA DEFAULT_COLOR = new ColorRGBA(0.9176f, 0.8509f, 0, 1f);
    public static final ColorRGBA SELECTING_MOVE_SQUARE = new ColorRGBA(1f, 0.6784f, 0.1176f, 1f);
    public static final ColorRGBA SELECTING_ATTACK_TARGET = new ColorRGBA(1f, 0f, 0.549f, 1f);
    public static final ColorRGBA SELECTING_SUPPORT_TARGET = new ColorRGBA(0, 1, 0, 1);
    public static final ColorRGBA MISC_COLOR = new ColorRGBA(1, 1, 1, 1);
    
    private static final float BUTTON_HOLD_TIME = 0.25f;
    private static final float DEFAULT_CURSOR_SPEED = 1.5f, HELD_CURSOR_SPEED = 1.25f;
    
    private final GameTimer globals = new GameTimer();
    private final SimpleOrdinalQueue queue = new SimpleOrdinalQueue();
    
    private final CursorPointer pointer;
    private final GeometricBody<Quad> square;
    private final RangeDisplay rangeDisplay;
    
    private final MapCoords pos = new MapCoords();
    private final Coords selectionDifferenceXY = new Coords(0, 0);
    private final Coords toTraverseXY = new Coords(0, 0);
    private final Vector2f accumulatedCursorDistanceXY = new Vector2f(0, 0);
    private final Vector3f preferredLocation = new Vector3f(0, 0, 0);
    
    private float cursorSpeed = 1.5f;
    private boolean translatingX = false, translatingY = false, isBacking = false, isCorrected = true;
    private Purpose purpose = Purpose.None;
    
    public TangibleUnit receivingEnd, selectedUnit;
    
    private ComplexInputReader<Direction> interpreter = 
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
    
    
    private FSM<CursorState> fsm = new FSM<CursorState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<CursorState> st) {
            if (st.getEnum() != CursorState.AnyoneHovered || (st.getEnum() == CursorState.AnyoneHovered && state.getEnum() == CursorState.CursorDefault)) {
                state = st; //maybe change this if needed
                square.getMaterial().setColor("Color", state.getEnum().getCorrespondingColor());
                pointer.getMaterial().setColor("Color", state.getEnum().getCorrespondingColor());
                
                switch (st.getEnum()) {
                    case AnyoneSelectingTarget:
                        selectionDifferenceXY.setCoords(0, 0);
                        break;
                    case CursorDefault:
                        rangeDisplay.cancelRange();
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
            square.getMaterial().setColor("Color", state.getEnum().getCorrespondingColor());
        }
    };
    
    public Cursor(AssetManager assetManager) {
        super("Map Cursor");
        
        Quad quad = new Quad(Tile.LENGTH, Tile.LENGTH);
        Geometry geometry = new Geometry("cursor square", quad);
        rangeDisplay = new RangeDisplay(MasterFsmState.getCurrentMap(), assetManager);
        pointer = 
            new CursorPointer(
                assetManager, 
                new MaterialCreator(
                    MaterialCreator.UNSHADED, 
                    (pointerMat) -> {
                        pointerMat.setColor("Color", DEFAULT_COLOR);
                        pointerMat.getAdditionalRenderState().setWireframe(true);
                    }
                ), 
                true
            );
        
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        geometry.setLocalRotation(rotation);
        geometry.setQueueBucket(Bucket.Transparent);
        
        Material pcs = new Material(assetManager, MaterialCreator.UNSHADED);
        pcs.setTexture("ColorMap", assetManager.loadTexture("Textures/gui/cursor.png"));
        pcs.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        pcs.getAdditionalRenderState().setDepthWrite(false);
        
        square = new GeometricBody<>(geometry, quad, pcs);
        
        LayerComparator.setLayer(square.getGeometry(), 3);
        
        attachChild(square.getGeometry());
        attachChild(pointer.getMasterNode());
        
        fsm.forceState(CursorState.CursorDefault);
    }
    
    public FsmState<CursorState> getState() {
        return fsm.getState();
    }
    
    public void setStateIfAllowed(CursorState cs) { fsm.setNewStateIfAllowed(cs); }
    public void forceState(CursorState cs) { fsm.forceState(cs); }
    
    public MapCoords getPos() { return pos; }
    
    public CursorPointer getPointer() { return pointer; }
    public GeometricBody<Quad> getSquare() { return square; }
    
    public float getSpeed() { return cursorSpeed; }
    public Purpose getPurpose() { return purpose; }
    
    public void setSpeed(float speed) { //default is 1
        cursorSpeed = speed;
    }
    
    public void setPurpose(Purpose P) {
        purpose = P;
    }
    
    public void addToQueue(SimpleProcedure procedure) {
        queue.addToQueue(procedure);
    }
    
    @Override
    public Tile getCurrentTile() {
        return MasterFsmState.getCurrentMap().getTileAt(pos);
    }
    
    @Override
    public Tile getCurrentTile(MapLevel map) {
        return map.getTileAt(pos);
    }
    
    public boolean traversingAllowed() {
        return selectedUnit == null || fsm.getEnumState() != CursorState.AnyoneMoving;
    }
    
    public void setPosition(MapCoords position) {
        MapLevel map = MasterFsmState.getCurrentMap();
        if (map.isWithinBounds(position)) {
            pos.set(position);
            
            Tile tile = map.getTileAt(pos);
            setLocalTranslation(tile.getWorldTranslation().x, 0.01f, tile.getWorldTranslation().z);
        }
    }
    
    public void setPosition(int x, int y, int layer) {
        setPosition(new MapCoords(new Coords(x, y), layer));
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
        Coords delta = new Coords(spaces, 0);
        if (MasterFsmState.getCurrentMap().isWithinBounds(pos.add(delta)) && traversingAllowed()) {
            setLocalTranslation(getLocalTranslation().x, 1.5f, getLocalTranslation().z);
            translatingX = true;
            toTraverseXY.addLocal(delta);
            
            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { 
                selectionDifferenceXY.addLocal(delta);
            }
            
            //System.out.println("(" + (pX + spaces) + ", " + pY + ")");
        }
    }
    
    public void translateY(int spaces) { //negative spaces for down, positive spaces for up
        Coords delta = new Coords(0, spaces);
        if (MasterFsmState.getCurrentMap().isWithinBounds(pos.add(delta)) && traversingAllowed()) {
            setLocalTranslation(getLocalTranslation().x, 1.5f, getLocalTranslation().z);
            translatingY = true;
            toTraverseXY.addLocal(delta);
            
            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { 
                selectionDifferenceXY.addLocal(delta); 
            }
            
            //System.out.println("(" + pX + ", " + (pY + spaces) + ")");
        }
    }
    
    public void update(float tpf) {
        updateCorrection(tpf);
        updateTraversals(tpf);
        updateForInteraction(tpf);
        
        if (!isBacking || isCorrected) {
            interpreter.update(tpf);
        }
        
        updatePointerAndLighting(tpf);
        
        globals.update(tpf);
    }
    
    private void updateCorrection(float tpf) {
        preferredLocation.set(getCurrentTile().getWorldTranslation().x, 0.01f, getCurrentTile().getWorldTranslation().z);
        
        setLocalTranslation(getLocalTranslation().x, preferredLocation.y, getLocalTranslation().z);
        if ((getLocalTranslation().x != preferredLocation.x || getLocalTranslation().z != preferredLocation.z)) {
            move(((preferredLocation.x - getLocalTranslation().x) / 10f), 0, ((preferredLocation.z - getLocalTranslation().z) / 10f));
            
            isCorrected = ((Math.abs(preferredLocation.x - getLocalTranslation().x) < 2f) && Math.abs(preferredLocation.z - getLocalTranslation().z) < 2f);
            if (isCorrected) { 
                isBacking = false; 
            }
        }
    }
    
    private void updateTraversals(float tpf) {
        //cursor moving in the X direction
        if (accumulatedCursorDistanceXY.x >= Tile.LENGTH) {
            translatingX = false;
            pos.addLocal(Integer.signum(toTraverseXY.x), 0);
            accumulatedCursorDistanceXY.x = 0;
            toTraverseXY.x = 0;
        } else if (translatingX) {
            move(0, 0, cursorSpeed * 2 * Math.signum(toTraverseXY.x));
            accumulatedCursorDistanceXY.x += cursorSpeed * 2;
        }
        
        //cursor moving in the Y direction
        if (accumulatedCursorDistanceXY.y >= Tile.LENGTH) {
            translatingY = false;
            pos.addLocal(0, Integer.signum(toTraverseXY.y));
            accumulatedCursorDistanceXY.y = 0;
            toTraverseXY.y = 0;
        } else if (translatingY) {
            move(cursorSpeed * 2 * Math.signum(toTraverseXY.y), 0, 0);
            accumulatedCursorDistanceXY.y += cursorSpeed * 2;
        }
    }
    
    private void updateForInteraction(float tpf) {
        TangibleUnit tu = getCurrentTile().getOccupier();
        
        if (tu != null) {
            if (tu.getFSM().getEnumState() == UnitState.Active) { //if hovered
                fsm.setNewStateIfAllowed(CursorState.AnyoneHovered); //sets it if and only if fsm.getEnumState() == CursorState.CursorDefault
                
                if (fsm.getEnumState() == CursorState.AnyoneHovered || (fsm.getEnumState() == CursorState.AnyoneSelected && tu.isSelected())) {
                    rangeDisplay.displayRange(tu, fsm.getEnumState());
                }
            } else if (!tu.isSelected() && fsm.getEnumState() != CursorState.AnyoneSelected) { //if nobody is selected nor is this unit selected
                fsm.setNewStateIfAllowed(CursorState.CursorDefault); //cancels range too
            }
        } else if (fsm.getEnumState() != CursorState.AnyoneSelected) {
            rangeDisplay.cancelRange();
        }
    }
    
    private void updatePointerAndLighting(float tpf) {
        pointer.rotateIf(selectedUnit != null, 0.035f * globals.getFrame());
        
        float omega = 0.0375f;
        ColorRGBA color = fsm.getEnumState().getCorrespondingColor().mult(0.075f * FastMath.cos(omega * globals.getFrame()) + 0.925f);
        
        square.getMaterial().setColor("Color", color);
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
                    
                    TangibleUnit tu = MasterFsmState.getCurrentMap().getTileAt(pos).getOccupier();
                    
                    //for moving
                    if (tu == null && fsm.getState().getEnum() == CursorState.AnyoneSelected) {
                        if (selectedUnit.getPos().getLayer() == pos.getLayer() && pos.getCoords().nonDiagonalDistanceFrom(selectedUnit.getPos().getCoords()) <= selectedUnit.getMOBILITY()) {
                            fsm.setNewStateIfAllowed(CursorState.AnyoneMoving);
                            selectedUnit.moveTo(pos);
                        }
                    }
                
                    //for selection and targeting
                    if (tu != null && pos.equals(tu.getPos())) {
                        if (!tu.isSelected()) {
                            if (fsm.getState().getEnum() == CursorState.AnyoneSelectingTarget) { //if the unit selected is an enemy being targeted
                                receivingEnd = tu; //modify this later
                                fsm.setNewStateIfAllowed(CursorState.AnyoneTargeted);
                                //start a fight
                                interpreter.clear();
                                selectedUnit.remapPosition(pos.subtract(selectionDifferenceXY));
                                return new MasterFsmState(MapFlowState.PreBattle).setConveyor(new Conveyor(selectedUnit).setEnemyUnit(receivingEnd).createCombatants());
                            } else { //selection
                                tu.select();
                                selectedUnit = tu;
                                fsm.setNewStateIfAllowed(CursorState.AnyoneSelected);
                            }
                        }
                    }
                }
            }
            
            if (name.equals("deselect")) {
                if (keyPressed) {
                    if (fsm.getEnumState() == CursorState.AnyoneSelected) {
                        //fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        fsm.setNewStateIfAllowed(CursorState.CursorDefault);
                        rangeDisplay.updateOpacity(fsm.getEnumState());
                        pos.set(selectedUnit.getPos());
                        selectedUnit.deselect();
                        selectedUnit = null;
                        isBacking = true;
                    } else if (fsm.getEnumState() == CursorState.AnyoneSelectingTarget) {
                        purpose = Purpose.None;
                        selectedUnit.getFSM().setNewStateIfAllowed(UnitState.Active);
                        
                        fsm.forceState(CursorState.Idle);
                        square.getMaterial().setColor("Color", DEFAULT_COLOR);
                        pointer.getMaterial().setColor("Color", DEFAULT_COLOR);
                        
                        setPosition(selectedUnit.getPos());
                        GameProtocols.OpenPostActionMenu();
                    }
                }
            }
        }
        
        return null;
    }
    
    public void resetCursorPositionFromSelection() {
        pos.subtractLocal(selectionDifferenceXY);
        move(selectionDifferenceXY.toVector3fZX().mult(-Tile.LENGTH));
        selectionDifferenceXY.setCoords(0, 0);
    }
    
    public void resetState() { //Happens after a battle, when a unit goes into standby, etc.
        rangeDisplay.cancelRange();
        resetCursorPositionFromSelection();
        selectedUnit.remapPosition(pos);
        selectedUnit.getVisuals().setAnimationState(AnimationState.Idle);
        selectedUnit.getFSM().setNewStateIfAllowed(UnitState.Done);
        selectedUnit.deselect();
        selectedUnit = null;
        fsm.forceState(CursorState.CursorDefault);
    }
    
    public void goBackFromMenu() {
        fsm.forceState(CursorState.AnyoneSelected);
        selectedUnit.remapPosition(selectedUnit.getPreviousPos());
        selectedUnit.getVisuals().setAnimationState(AnimationState.Idle);
        selectedUnit.getFSM().setNewStateIfAllowed(UnitState.Active);
        rangeDisplay.displayRange(selectedUnit, fsm.getEnumState());
    }
}
