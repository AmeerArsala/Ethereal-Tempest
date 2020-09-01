/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import battle.Conveyer;
import com.jme3.asset.AssetManager;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.util.List;
import etherealtempest.FSM;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import general.ComplexInputReader;
import maps.layout.TangibleUnit.AnimationState;
import maps.layout.TangibleUnit.UnitStatus;

/**
 *
 * @author night
 */
public class Cursor {
    private int elv = 0;
    
    public final Geometry geometry;
    private final Quad quad;
    
    public int pX, pY;
    
    private int selectionDisplacementX = 0, selectionDisplacementY = 0;
    
    private float toTraverseX = 0, toTraverseY = 0;
    private float cursorSpeed = 1.5f;
    private boolean translatingX = false, translatingY = false;
    private Purpose purpose = Purpose.None;
    
    private Vector3f preferredLocation;
    private final RangeDisplay rangeDisplay;
    
    private final float BUTTON_HOLD_TIME = 0.25f;
    private final float DEFAULT_CURSOR_SPEED = 1.5f, HELD_CURSOR_SPEED = 1.25f;
    
    protected ComplexInputReader interpreter = 
            new ComplexInputReader(true, BUTTON_HOLD_TIME) {
                @Override
                public void protocolProcedure(Object inputHeld) {
                    Direction dir = (Direction)inputHeld;
                    if (getHeldTime(dir) > BUTTON_HOLD_TIME) {
                        tryTranslate(dir, 1, amountOfKeysPressed() == 1 || !keyIsHeld(dir.getConflicter()));
                    }
                }
            }
            .link(Direction.Up, "move up")
            .link(Direction.Down, "move down")
            .link(Direction.Left, "move left")
            .link(Direction.Right, "move right");
    
    public TangibleUnit receivingEnd, selectedUnit;
    
    public Cursor(Quad quad) {
        this.quad = quad;
        geometry = new Geometry("Quad", this.quad);
        rangeDisplay = new RangeDisplay(MasterFsmState.getCurrentMap());
        
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        geometry.setLocalRotation(rotation);
        geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        
        fsm.forceState(new FsmState(EntityState.CursorDefault));
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
    
    private FSM fsm = new FSM() {
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            if (st.getEnum() != EntityState.AnyoneHovered || (st.getEnum() == EntityState.AnyoneHovered && state.getEnum() == EntityState.CursorDefault)) {
                state = st; //maybe change this if needed
                if (st.getEnum() == EntityState.AnyoneSelectingTarget) {
                    selectionDifferenceX = 0;
                    selectionDifferenceY = 0;
                }
            } 
        }
    };
    
    public void setPosition(int x, int y, int layer, Map map) {
        if (x < map.getXLength(elv) && x >= 0 && y < map.getYLength(elv) && y >= 0) {
            pX = x;
            pY = y;
            elv = layer;
        
            geometry.setLocalTranslation(map.fullmap[layer][x][y].getWorldTranslation().x - 5f, map.fullmap[layer][x][y].getHighestPointHeight() + 1.5f, map.fullmap[layer][x][y].getWorldTranslation().z - 3f);
        }
    }
    
    public int getPosX() { return pX; }
    public int getPosY() { return pY; }
    public int getElevation() { return elv; }
    
    public int selectionDifferenceX = 0, selectionDifferenceY = 0;
    
    public void resetCursorPositionFromSelection() {
        pX -= selectionDifferenceX;
        pY -= selectionDifferenceY;
        geometry.setLocalTranslation(geometry.getLocalTranslation().x - (16f * selectionDifferenceY), geometry.getLocalTranslation().y, geometry.getLocalTranslation().z - (16f * selectionDifferenceX));
        selectionDifferenceX = 0;
        selectionDifferenceY = 0;
        selectionDisplacementX = 0;
        selectionDisplacementY = 0;
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
            geometry.setLocalTranslation(geometry.getLocalTranslation().x, MasterFsmState.getCurrentMap().fullmap[elv][pX + spaces][pY].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z);
            translatingX = true;
            toTraverseX += spaces;
            
            if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget) { selectionDifferenceX += spaces; }
            if (fsm.getState().getEnum() == EntityState.AnyoneSelected) { selectionDisplacementX += spaces; }
        }
    }
    
    public void translateY(int spaces) { //negative spaces for down, positive spaces for up
        if (pY + spaces >= MasterFsmState.getCurrentMap().getMinimumY(elv) && pY + spaces < MasterFsmState.getCurrentMap().getYLength(elv)) {
            geometry.setLocalTranslation(geometry.getLocalTranslation().x, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY + spaces].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z);
            translatingY = true;
            toTraverseY += spaces;
            
            if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget) { selectionDifferenceY += spaces; }
            if (fsm.getState().getEnum() == EntityState.AnyoneSelected) { selectionDisplacementY += spaces; }
        }
    }
    
    public void updateAI(float tpf, TangibleUnit tu, MasterFsmState mapFSM) {
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
    
    float getSign(float input) {
        return (input > 0) ? 1 : (input < 0 ? -1 : 0);
    }
    
    public void update(float tpf, MasterFsmState mapFSM) {
        //System.out.println("Held Keys: " + interpreter.heldPointers());
        
        preferredLocation = new Vector3f(MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getWorldTranslation().x - 5f, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getHighestPointHeight() + 1.5f, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getWorldTranslation().z - 3f);
        geometry.setLocalTranslation(geometry.getLocalTranslation().x, preferredLocation.y, geometry.getLocalTranslation().z);
        if ((geometry.getLocalTranslation().x != preferredLocation.x || geometry.getLocalTranslation().z != preferredLocation.z)) {
            geometry.move(((preferredLocation.x - geometry.getLocalTranslation().x) / 15f), 0, ((preferredLocation.z - geometry.getLocalTranslation().z) / 15f));
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
            updateAIForInteraction(tpf, specified, mapFSM);
            updateAI(tpf, specified, mapFSM);
        } else if (fsm.getState().getEnum() != EntityState.AnyoneSelected) {
            rangeDisplay.cancelRange(elv);
        }
        
        interpreter.update(tpf);
    }
    
    public void updateAIForInteraction(float tpf, TangibleUnit tu, MasterFsmState mapFSM) {
        //System.out.println(fsm.getState().getEnum());
        
        if (pX == tu.getPosX() && pY == tu.getPosY() && tu.getFSM().getState().getEnum() == EntityState.Active) {
            if (fsm.getState().getEnum() != EntityState.AnyoneHovered) {
                fsm.setNewStateIfAllowed(new FsmState(EntityState.AnyoneHovered));
                tu.hoverSetter = true;
            }
            if (fsm.getState().getEnum() != EntityState.AnyoneSelectingTarget) {
                if (fsm.getState().getEnum() != EntityState.AnyoneSelected || (fsm.getState().getEnum() == EntityState.AnyoneSelected && tu.isSelected)) {
                    rangeDisplay.displayRange(tu, elv, mapFSM.getAssetManager());
                }
            } else if (!tu.isSelected) {
                tu.hoverSetter = false;
            }
        } else if (!tu.isSelected && fsm.getState().getEnum() != EntityState.AnyoneSelected) { //if nobody is selected
            if (fsm.getState().getEnum() != EntityState.AnyoneHovered) { //if nobody is selected or hovered
                rangeDisplay.cancelRange(elv);
            } else { //if nobody is selected but someone is hovered yet the cursor isnt there
                if (tu.hoverSetter) {
                    AssetManager AM = mapFSM.getAssetManager();
                    mapFSM = new MasterFsmState().setAssetManager(AM);
                    fsm.setNewStateIfAllowed(new FsmState(EntityState.CursorDefault));
                    tu.hoverSetter = false;
                }
            }
        }
        
        /*if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget && tu.getFSM().getState().getEnum() == EntityState.SelectingTarget) {
            //put something here later
        }*/
        
        if (fsm.getState().getEnum() == EntityState.AnyoneTargeted && tu.getFSM().getState().getEnum() == EntityState.SelectingTarget && tu != receivingEnd) {
            //start a battle
            if (mapFSM.getEnum() == EntityState.PostActionMenuOpened) {
                mapFSM.updateState(EntityState.PreBattle).setConveyer(new Conveyer(selectedUnit).setEnemyUnit(receivingEnd).setMap(MasterFsmState.getCurrentMap()));
            }
        }
    }
    
    public void resolveInput(String name, float tpf, boolean keyPressed) {
        if (!interpreter.anyKeyPressed() && keyPressed) {
            cursorSpeed = DEFAULT_CURSOR_SPEED;
        }
        
        interpreter.obtainInput(name, tpf, keyPressed);
        
        if (fsm.getState().getEnum() != EntityState.Idle) {
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
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelected) {
                        rangeDisplay.tileOpacity = 0.85f;
                    }
                
                    TangibleUnit tu = MasterFsmState.getCurrentMap().fullmap[getElevation()][pX][pY].getOccupier();
                    
                    //for moving
                    if (tu == null && fsm.getState().getEnum() == EntityState.AnyoneSelected) {
                        if (Map.isWithinSpaces(selectedUnit.getMOBILITY(), selectedUnit.getPosX(), selectedUnit.getPosY(), pX, pY)) {
                            fsm.setNewStateIfAllowed(new FsmState(EntityState.AnyoneMoving));
                            selectedUnit.setStateIfAllowed(new MoveState().setMapAndCursor(MasterFsmState.getCurrentMap(), this));
                        }
                    }
                
                    //for selection and targeting
                    if (tu != null && pX == tu.getPosX() && pY == tu.getPosY()) {
                        if (!tu.isSelected) {
                            if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget) { //if the unit selected is an enemy being targeted
                                receivingEnd = tu; //modify this later
                                fsm.setNewStateIfAllowed(new FsmState(EntityState.AnyoneTargeted));
                            } else { //selection
                                tu.isSelected = true;
                                selectedUnit = tu;
                                fsm.setNewStateIfAllowed(new FsmState(EntityState.AnyoneSelected));
                            }
                        }
                    }
                }
            } else {
                if (selectedUnit == null) {
                    if (fsm.getState().getEnum() == EntityState.AnyoneHovered) {
                        rangeDisplay.tileOpacity = 0.5f;
                    } else { rangeDisplay.tileOpacity = 0f; }
                } else {
                    rangeDisplay.tileOpacity = 0.85f;
                }
            }
            if (name.equals("deselect")) {
                if (keyPressed) {
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelected) {
                        //fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        selectedUnit.isSelected = false;
                        selectedUnit = null;
                        setStateIfAllowed(new FsmState(EntityState.CursorDefault));
                        rangeDisplay.tileOpacity = 0.5f;
                        pX -= selectionDisplacementX;
                        pY -= selectionDisplacementY;
                        selectionDisplacementX = 0;
                        selectionDisplacementY = 0;
                    } else if (fsm.getState().getEnum() == EntityState.PostActionMenuOpened) {}
                }
            }
        }
    }
    
    public FsmState getState() {
        return fsm.getState();
    }
    
    public void setStateIfAllowed(FsmState newState) {
        fsm.setNewStateIfAllowed(newState);
    }
    
    public void forceState(FsmState newState) {
        fsm.forceState(newState);
    }
    
    public void resetState(Map M) {
        M.fullmap[elv][selectedUnit.getPosX()][selectedUnit.getPosY()].resetOccupier();
        resetCursorPositionFromSelection();
        selectedUnit.remapPositions(pX, pY, elv, M);
        selectedUnit.animVar = 0; //remove later
        selectedUnit.setAnimationState(AnimationState.Idle);
        rangeDisplay.cancelRange(elv);
        M.fullmap[elv][selectedUnit.getPosX()][selectedUnit.getPosY()].setOccupier(selectedUnit);
        selectedUnit.isSelected = false;
        selectedUnit.setStateIfAllowed(new FsmState(EntityState.Done));
        selectedUnit = null;
        fsm.forceState(new FsmState(EntityState.CursorDefault));
    }
    
    public void goBackFromMenu(AssetManager AM) {
        forceState(new FsmState(EntityState.AnyoneSelected));
        selectedUnit.remapPositions(selectedUnit.prevX, selectedUnit.prevY, elv, MasterFsmState.getCurrentMap());
        selectedUnit.animVar = 0; //idle animation
        selectedUnit.setAnimationState(AnimationState.Idle);
        selectedUnit.setStateIfAllowed(new FsmState(EntityState.Active));
        rangeDisplay.tileOpacity = 0.85f;
        rangeDisplay.displayRange(selectedUnit, elv, AM);
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
