/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.control;

import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.move.RangeDisplay;
import etherealtempest.info.Conveyor;
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
import enginetools.MaterialCreator;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FSM.CursorState;
import etherealtempest.fsm.FSM.MapFlowState;
import etherealtempest.fsm.FSM.UnitState;
import etherealtempest.fsm.FsmState;
import etherealtempest.GameProtocols;
import etherealtempest.fsm.MasterFsmState;
import etherealtempest.geometry.GeometricBody;
import general.tools.input.ComplexInputReader;
import general.tools.GameTimer;
import general.procedure.SimpleOrdinalQueue;
import general.procedure.functional.SimpleProcedure;
import general.utils.wrapper.Duo;
import maps.data.MapTextures;
import maps.layout.occupant.character.Spritesheet.AnimationState;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.OnTile;
import maps.layout.occupant.character.Movement;
import maps.layout.occupant.control.CursorFSM.Purpose;
import maps.layout.tile.Tile;
import maps.layout.tile.move.MoveArrowTrain;

/**
 *
 * @author night
 */
public class Cursor extends Node implements OnTile {
    private static final float BUTTON_HOLD_TIME = 0.25f;
    private static final float DEFAULT_CURSOR_SPEED = 1.5f, HELD_CURSOR_SPEED = 1.25f;
    
    private final GameTimer timer = new GameTimer();
    private final SimpleOrdinalQueue queue = new SimpleOrdinalQueue();
    
    private final CursorPointer pointer;
    private final GeometricBody<Quad> square;
    private final RangeDisplay rangeDisplay;
    private final MoveArrowTrain arrowTrain;
    
    private final MapCoords pos = new MapCoords();
    private final Vector3f preferredLocation = new Vector3f(0, 0.01f, 0);
    
    private float cursorSpeed = DEFAULT_CURSOR_SPEED;
    
    public TangibleUnit receivingEnd, selectedUnit;
    
    private final ComplexInputReader<Direction> interpreter = 
        new ComplexInputReader<Direction>(true, BUTTON_HOLD_TIME) {
            @Override
            public void protocolProcedure(Direction inputHeld) {
                if (fsm.getEnumState() != CursorState.Idle && getHeldTime(inputHeld) > BUTTON_HOLD_TIME) {
                    tryTranslate(inputHeld, 1, amountOfKeysPressed() == 1 || !keyIsHeld(inputHeld.getConflicter()));
                }
            }
        }.link(
            new Duo<>(Direction.Up, "move up"),
            new Duo<>(Direction.Down, "move down"),
            new Duo<>(Direction.Left, "move left"),
            new Duo<>(Direction.Right, "move right")
        );
    
    private CursorFSM fsm = new CursorFSM() {
        @Override
        protected void onAttemptStateSet(FsmState<CursorState> st) {
            if (st.getEnum() == CursorState.Idle) {
                interpreter.clear();
            }
        }
        
        @Override
        public boolean stateAllowed(FsmState<CursorState> st) {
            FsmState<CursorState> currentState = getState();
            return st.getEnum() != CursorState.AnyoneHovered || (st.getEnum() == CursorState.AnyoneHovered && currentState.getEnum() == CursorState.CursorDefault);
        }

        @Override
        public void onStateSet(FsmState<CursorState> currentState, FsmState<CursorState> previousState) {
            square.getMaterial().setColor("Color", currentState.getEnum().getCorrespondingColor());
            pointer.getMaterial().setColor("Color", currentState.getEnum().getCorrespondingColor());
            
            if (currentState.getEnum() == CursorState.AnyoneSelected) {
                if (previousState.getEnum() == CursorState.AnyoneHovered) {
                    arrowTrain.clear();
                }
                
                arrowTrain.setVisibility(true);
            } else {
                arrowTrain.setVisibility(false);
            }
            
            switch (currentState.getEnum()) {
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
        
        @Override
        public void forceState(FsmState<CursorState> st) {
            super.forceState(st);
            square.getMaterial().setColor("Color", st.getEnum().getCorrespondingColor());
            if (st.getEnum() == CursorState.AnyoneSelected) {
                arrowTrain.setVisibility(true);
            } else {
                arrowTrain.setVisibility(false);
            }
        }
    };
    
    public Cursor(AssetManager assetManager) {
        super("Map Cursor");
        
        Quad quad = new Quad(Tile.SIDE_LENGTH, Tile.SIDE_LENGTH);
        Geometry geometry = new Geometry("cursor square", quad);
        arrowTrain = new MoveArrowTrain(assetManager);
        rangeDisplay = new RangeDisplay(MasterFsmState.getCurrentMap(), assetManager);
        pointer = new CursorPointer(
            assetManager, 
            new MaterialCreator(
                MaterialCreator.UNSHADED,
                (pointerMat) -> {
                    pointerMat.setColor("Color", CursorFSM.DEFAULT_COLOR);
                    pointerMat.getAdditionalRenderState().setWireframe(true);
                }
            ), 
            true
        );
        
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        geometry.setLocalRotation(rotation);
        geometry.setQueueBucket(Bucket.Transparent);
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        mat.setTexture("ColorMap", MapTextures.Tiles.Cursor);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        square = new GeometricBody<>(geometry, quad, mat);
        
        LayerComparator.setLayer(square.getGeometry(), 3);
        
        attachChild(square.getGeometry());
        attachChild(pointer.getMasterNode());
        
        fsm.forceState(CursorState.CursorDefault);
    }
    
    public FSM<CursorState> getFSM() { return fsm; }
    public MapCoords getPos() { return pos; }
    
    public CursorPointer getPointer() { return pointer; }
    public GeometricBody<Quad> getSquare() { return square; }
    public MoveArrowTrain getMoveArrowTrain() { return arrowTrain; }
    
    public float getSpeed() { return cursorSpeed; }
    public Purpose getPurpose() { return fsm.getPurpose(); }
    
    public void setSpeed(float speed) { //default is 1
        cursorSpeed = speed;
    }
    
    public void setPurpose(Purpose P) {
        fsm.setPurpose(P);
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
        return selectedUnit == null || (fsm.getEnumState() != CursorState.AnyoneMoving && fsm.getEnumState() != CursorState.Idle);
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
                if (!fsm.translatingY || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateY(spaces); 
                }
                break;
            case Down:
                if (!fsm.translatingY || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateY(spaces * -1);
                }
                break;
            case Left:
                if (!fsm.translatingX || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateX(spaces * -1);
                }
                break;
            case Right:
                if (!fsm.translatingX || overrideCondition) {
                    cursorSpeed = HELD_CURSOR_SPEED;
                    translateX(spaces);
                }
                break;
        }
    }
    
    public void translateX(int spaces) { //negative spaces for left, positive spaces for right
        Coords delta = new Coords(spaces, 0);
        if (MasterFsmState.getCurrentMap().isWithinBounds(pos.add(delta)) && traversingAllowed()) {
            setLocalTranslation(getLocalTranslation().x, 0.01f, getLocalTranslation().z);
            fsm.translatingX = true;
            fsm.translate(delta);
        }
    }
    
    public void translateY(int spaces) { //negative spaces for down, positive spaces for up
        Coords delta = new Coords(0, spaces);
        if (MasterFsmState.getCurrentMap().isWithinBounds(pos.add(delta)) && traversingAllowed()) {
            setLocalTranslation(getLocalTranslation().x, 0.01f, getLocalTranslation().z);
            fsm.translatingY = true;
            fsm.translate(delta);
        }
    }
    
    public void update(float tpf) {
        updateCorrection(tpf);
        updateTraversals(tpf);
        updateForInteraction(tpf);
        
        if (fsm.canTakeInput()) {
            interpreter.update(tpf);
        }
        
        updatePointerAndLighting(tpf);
        
        timer.update(tpf);
    }
    
    private void updateCorrection(float tpf) {
        Vector3f tileTranslation = getCurrentTile().getWorldTranslation();
        preferredLocation.set(tileTranslation.x, 0.01f, tileTranslation.z);
        
        setLocalTranslation(getLocalTranslation().x, preferredLocation.y, getLocalTranslation().z);
        Vector3f localTranslation = getLocalTranslation();
        if ((localTranslation.x != preferredLocation.x || localTranslation.z != preferredLocation.z)) {
            move(((preferredLocation.x - localTranslation.x) / 10f), 0, ((preferredLocation.z - localTranslation.z) / 10f));
            
            float diffX = Math.abs(preferredLocation.x - getLocalTranslation().x);
            float diffZ = Math.abs(preferredLocation.z - getLocalTranslation().z);
            fsm.updateIsCorrected(diffX < 2f && diffZ < 2f);
        }
    }
    
    private void changePos(Coords delta) {
        pos.addLocal(delta);
        if (fsm.getEnumState() == CursorState.AnyoneSelected && rangeDisplay.isDisplayedMoveSquare(pos)) {
            arrowTrain.append(pos);
        }
    }
    
    private void updateTraversals(float tpf) {
        //cursor moving in the X direction
        if (fsm.accumulatedCursorDistanceXY.x >= Tile.SIDE_LENGTH) {
            changePos(fsm.finishTranslatingX());
        } else if (fsm.translatingX) {
            move(0, 0, cursorSpeed * 2 * Math.signum(fsm.toTraverseXY.x));
            fsm.accumulatedCursorDistanceXY.x += cursorSpeed * 2;
        }
        
        //cursor moving in the Y direction
        if (fsm.accumulatedCursorDistanceXY.y >= Tile.SIDE_LENGTH) {
            changePos(fsm.finishTranslatingY());
        } else if (fsm.translatingY) {
            move(cursorSpeed * 2 * Math.signum(fsm.toTraverseXY.y), 0, 0);
            fsm.accumulatedCursorDistanceXY.y += cursorSpeed * 2;
        }
        
        arrowTrain.tick();
    }
    
    private void updateForInteraction(float tpf) {
        TangibleUnit tu = getCurrentTile().getOccupier();
        
        if (tu != null) {
            if (tu.getFSM().getEnumState() == UnitState.Active) { //if hovered
                fsm.setNewStateIfAllowed(CursorState.AnyoneHovered); //sets it if and only if fsm.getEnumState() == CursorState.CursorDefault
                
                if (fsm.getEnumState() == CursorState.AnyoneHovered || (fsm.getEnumState() == CursorState.AnyoneSelected && tu.isSelected())) {
                    rangeDisplay.displayRange(tu, fsm.getEnumState().getCorrespondingTileOpacity());
                }
            } else if (!tu.isSelected() && fsm.getEnumState() != CursorState.AnyoneSelected) { //if nobody is selected nor is this unit selected
                fsm.setNewStateIfAllowed(CursorState.CursorDefault); //cancels range too
            }
        } else if (fsm.getEnumState() != CursorState.AnyoneSelected) {
            rangeDisplay.cancelRange();
        }
    }
    
    private void updatePointerAndLighting(float tpf) {
        pointer.rotateIf(selectedUnit != null, 0.035f * timer.getFrame());
        
        float omega = 0.0375f;
        ColorRGBA color = fsm.getEnumState().getCorrespondingColor().mult(0.075f * FastMath.cos(omega * timer.getFrame()) + 0.925f);
        
        square.getMaterial().setColor("Color", color);
    }
    
    public MasterFsmState resolveInput(String name, float tpf, boolean keyPressed) {
        if (!interpreter.anyKeyPressed() && keyPressed) {
            cursorSpeed = DEFAULT_CURSOR_SPEED;
        }
        
        interpreter.obtainInput(name, tpf, keyPressed);
        
        if (fsm.canTakeInput()) {
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
                if (fsm.getEnumState() == CursorState.AnyoneMoving || fsm.getEnumState() == CursorState.AnyoneSelected) {
                    Movement.keyToIncreaseSpeedPressed = keyPressed;
                }
                
                if (keyPressed && !fsm.translatingX && !fsm.translatingY) {
                    TangibleUnit tu = getCurrentTile().getOccupier();
                    
                    //for moving
                    if (fsm.getEnumState() == CursorState.AnyoneSelected) {
                        if (selectedUnit.getPos().getLayer() == pos.getLayer() && pos.getCoords().nonDiagonalDistanceFrom(selectedUnit.getPos().getCoords()) <= selectedUnit.getMOBILITY()) {
                            fsm.setNewStateIfAllowed(CursorState.AnyoneMoving);
                            //selectedUnit.moveTo(pos);
                            selectedUnit.moveWith(arrowTrain.asArrayWithoutRoot());
                            
                            if (tu != null) {
                                rangeDisplay.cancelRange();
                            }
                        }
                    }
                
                    //for selection and targeting
                    if (tu != null && pos.equals(tu.getPos())) {
                        if (!tu.isSelected()) {
                            if (fsm.getEnumState() == CursorState.AnyoneSelectingTarget) { //if the unit selected is an enemy being targeted
                                receivingEnd = tu; //modify this later
                                fsm.setNewStateIfAllowed(CursorState.AnyoneTargeted);
                                //start a fight
                                interpreter.clear();
                                selectedUnit.remapPosition(pos.subtract(fsm.selectionDifferenceXY));
                                return new MasterFsmState(MapFlowState.PreBattle).setConveyor(new Conveyor(selectedUnit).setEnemyUnit(receivingEnd).createCombatants());
                            } else { //selection
                                tu.select();
                                selectedUnit = tu;
                                arrowTrain.setCapacity(selectedUnit.getMOBILITY());
                                arrowTrain.setRoot(pos);
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
                        pos.set(selectedUnit.getPos());
                        selectedUnit.deselect();
                        selectedUnit = null;
                        fsm.isBacking = true;
                    } else if (fsm.getEnumState() == CursorState.AnyoneSelectingTarget) {
                        fsm.setPurpose(Purpose.None);
                        selectedUnit.getFSM().setNewStateIfAllowed(UnitState.Active);
                        
                        fsm.forceState(CursorState.Idle);
                        square.getMaterial().setColor("Color", CursorFSM.DEFAULT_COLOR);
                        pointer.getMaterial().setColor("Color", CursorFSM.DEFAULT_COLOR);
                        
                        setPosition(selectedUnit.getPos());
                        GameProtocols.OpenPostActionMenu();
                    }
                }
            }
        }
        
        return null;
    }
    
    public void resetCursorPositionFromSelection() {
        pos.subtractLocal(fsm.selectionDifferenceXY);
        move(fsm.selectionDifferenceXY.toVector3fZX().mult(-Tile.SIDE_LENGTH));
        fsm.selectionDifferenceXY.setCoords(0, 0);
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
        selectedUnit.getVisuals().setIdealIdle();
        selectedUnit.getFSM().setNewStateIfAllowed(UnitState.Active);
        rangeDisplay.displayRange(selectedUnit, fsm.getEnumState().getCorrespondingTileOpacity());
    }
}
