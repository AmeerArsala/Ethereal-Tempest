/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import battle.Battle;
import battle.Catalog;
import battle.Conveyer;
import com.jme3.asset.AssetManager;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;
import etherealtempest.FSM;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;

/**
 *
 * @author night
 */
public class Cursor {
    private int elv = 0;
    
    public Geometry geometry;
    public Quad quad;
    public int pX, pY;
    
    private float toTraverseX = 0, toTraverseY = 0;
    private float cursorSpeed = 1.0f, accumulatedTPF = 0;
    private boolean translatingX = false, translatingY = false;
    private Purpose purpose = Purpose.None;
    
    private Vector3f preferredLocation;
    
    protected Direction holding[] = new Direction[4];
    
    public TangibleUnit receivingEnd, selectedUnit;
    
    public Cursor(Quad quad) {
        this.quad = quad;
        geometry = new Geometry("Quad", this.quad);
    }
    
    public enum Direction {
        Up,
        Down,
        Left,
        Right
    }
    
    public enum Purpose {
        WeaponAttack,
        SkillAttack,
        EtherAttack,
        EtherSupport,
        Trade,
        None
    }
    
    public void init() {
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        geometry.setLocalRotation(rotation);
        geometry.setQueueBucket(Bucket.Transparent);
        
        fsm.forceState(new FsmState(EntityState.CursorDefault));
    }
    
    public void setPosition(int x, int y, int layer, Map map) {
        if (x < map.getTilesX() && x >= 0 && y < map.getTilesY() && y >= 0) {
            pX = x;
            pY = y;
            elv = layer;
        
            geometry.setLocalTranslation(map.fullmap[layer][x][y].tile.getWorldTranslation().x - 5f, map.fullmap[layer][x][y].getHighestPointHeight() + 1.5f, map.fullmap[layer][x][y].tile.getWorldTranslation().z - 3f);
        }
        
        //geometry.setLocalTranslation(map.fullmap[layer][x][y].tile.getWorldTranslation());
        //geometry.setLocalTranslation(geometry.getLocalTranslation().x - 5f, map.fullmap[layer][x][y].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z - 3f);
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
    }
    
    public void translateX(int spaces) { //negative spaces for left, positive spaces for right
        if (pX + spaces >= 0 && pX + spaces < MasterFsmState.getCurrentMap().getTilesX()) {
            geometry.setLocalTranslation(geometry.getLocalTranslation().x, MasterFsmState.getCurrentMap().fullmap[elv][pX + spaces][pY].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z);
            translatingX = true;
            toTraverseX += spaces;
            
            //geometry.setLocalTranslation(geometry.getLocalTranslation().x, MasterFsmState.getCurrentMap().fullmap[elv][pX + spaces][pY].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z + (16f * spaces));
            //pX += spaces;
        }
    }
    
    public void translateY(int spaces) { //negative spaces for down, positive spaces for up
        if (pY + spaces >= 0 && pY + spaces < MasterFsmState.getCurrentMap().getTilesY()) {
            geometry.setLocalTranslation(geometry.getLocalTranslation().x, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY + spaces].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z);
            translatingY = true;
            toTraverseY += spaces;
            
            //geometry.setLocalTranslation(geometry.getLocalTranslation().x, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY + spaces].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z);
            //geometry.setLocalTranslation(geometry.getLocalTranslation().x + (16f * spaces), MasterFsmState.getCurrentMap().fullmap[elv][pX][pY + spaces].getHighestPointHeight() + 1.5f, geometry.getLocalTranslation().z);
            //pY += spaces;
        }
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
    
    public void updateAI(float tpf, TangibleUnit tu, MasterFsmState mapFSM) {
        switch (fsm.getState().getEnum()) {
            case CursorDefault: 
            {
                tileOpacity = 0;
                break;
            }
            case AnyoneHovered: 
            {
                tileOpacity = 0.5f;
                break;
            }
            case AnyoneMoving: 
            {
                tileOpacity = 0;
                break;
            }
            case AnyoneSelected:
            {
                tileOpacity = 0.85f;
                break;
            }
            case AnyoneSelectingTarget: 
            {
                tileOpacity = 0;
                break;
            }
            case AnyoneTargeted: 
            {
                tileOpacity = 0;
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
    
    public void update(float tpf, TangibleUnit specified, MasterFsmState mapFSM) {
        //System.out.println("accumulatedCursorDistanceX = " + accumulatedCursorDistanceX + ", accumulatedCursorDistanceY = " + accumulatedCursorDistanceY + ", toTraverseX = " + toTraverseX + ", toTraverseY = " + toTraverseY);
        preferredLocation = new Vector3f(MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].tile.getWorldTranslation().x - 5f, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].getHighestPointHeight() + 1.5f, MasterFsmState.getCurrentMap().fullmap[elv][pX][pY].tile.getWorldTranslation().z - 3f);
        geometry.setLocalTranslation(geometry.getLocalTranslation().x, preferredLocation.y, geometry.getLocalTranslation().z);
        if (geometry.getLocalTranslation().x != preferredLocation.x || geometry.getLocalTranslation().z != preferredLocation.z) {
            geometry.setLocalTranslation(geometry.getLocalTranslation().x + ((preferredLocation.x - geometry.getLocalTranslation().x) / 10f), preferredLocation.y, geometry.getLocalTranslation().z + ((preferredLocation.z - geometry.getLocalTranslation().z) / 10f));
        }
        
        if (directionHeld()) {
            accumulatedTPF += tpf;
        } else {
            accumulatedTPF = 0;
            holding = new Direction[4];
        }
        
        if (accumulatedCursorDistanceX >= 16) {
            translatingX = false;
            pX += getSign(toTraverseX);
            accumulatedCursorDistanceX = 0;
            toTraverseX = 0;
        } else if (translatingX) {
            geometry.move(0, 0, cursorSpeed * 2 * getSign(toTraverseX));
            accumulatedCursorDistanceX += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseX));
            /*if (accumulatedCursorDistanceX < 16 + 1 - FastMath.abs(getSign(toTraverseX))) {
                geometry.move(0, 0, cursorSpeed * 2 * getSign(toTraverseX));
                accumulatedCursorDistanceX += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseX));
            } else if (accumulatedCursorDistanceX > 16) {
                toTraverseX = getSign(toTraverseX) * accumulatedCursorDistanceX;
                accumulatedCursorDistanceX = 0;
            }*/
        }
        if (accumulatedCursorDistanceY >= 16) {
            translatingY = false;
            pY += getSign(toTraverseY);
            accumulatedCursorDistanceY = 0;
            toTraverseY = 0;
        } else if (translatingY) {
            geometry.move(cursorSpeed * 2 * getSign(toTraverseY), 0, 0);
            accumulatedCursorDistanceY += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseY));
            /*if (accumulatedCursorDistanceY < 16 + 1 - FastMath.abs(toTraverseY)) {
                geometry.move(cursorSpeed * 2 * getSign(toTraverseY), 0, 0);
                accumulatedCursorDistanceY += cursorSpeed * 2 * FastMath.abs(getSign(toTraverseY));
            } else if (accumulatedCursorDistanceY > 16 + 1 - FastMath.abs(toTraverseY)) {
                toTraverseY = getSign(toTraverseY) * accumulatedCursorDistanceY;
                accumulatedCursorDistanceY = 0;
            }*/
        }
        
        if (directionHeld() && accumulatedTPF >= 0.625f) {
            if (!translatingX) {
                cursorSpeed = 0.75f;
                tryContinueX();
            }
            if (!translatingY) {
                cursorSpeed = 0.75f;
                tryContinueY();
            }
        }
        
        updateAIForInteraction(tpf, specified, mapFSM);
        
        updateAI(tpf, specified, mapFSM);
    }
    
    public float tileOpacity = 0;
    
    public void updateAIForInteraction(float tpf, TangibleUnit tu, MasterFsmState mapFSM) {
        //System.out.println(fsm.getState().getEnum());
        
        if (pX == tu.getPosX() && pY == tu.getPosY() && tu.getFSM().getState().getEnum() == EntityState.Active) {
            if (fsm.getState().getEnum() != EntityState.AnyoneHovered) {
                fsm.setNewStateIfAllowed(new FsmState(EntityState.AnyoneHovered));
                tu.hoverSetter = true;
            }
            if (fsm.getState().getEnum() != EntityState.AnyoneSelectingTarget) {
                if (fsm.getState().getEnum() != EntityState.AnyoneSelected || (fsm.getState().getEnum() == EntityState.AnyoneSelected && tu.isSelected)) {
                    displayRange(tu, MasterFsmState.getCurrentMap(), elv, mapFSM.getAssetManager());
                }
            } else if (!tu.isSelected) {
                if (tu.hoverSetter) { tu.hoverSetter = false; }
            }
            //if (pCursor.getState().getEnum() == EntityState.AnyoneSelected) { tileOpacity = 0.85f; }
        } else if (!tu.isSelected && fsm.getState().getEnum() != EntityState.AnyoneSelected) {
            if (fsm.getState().getEnum() != EntityState.AnyoneHovered) { //if nobody is selected or hovered
                cancelRange(0, MasterFsmState.getCurrentMap());
            } else { //if nobody is selected but someone is hovered yet the cursor isnt there
                if (tu.hoverSetter) {
                    AssetManager AM = mapFSM.getAssetManager();
                    mapFSM = new MasterFsmState().setAssetManager(AM);
                    fsm.setNewStateIfAllowed(new FsmState(EntityState.CursorDefault));
                    tu.hoverSetter = false;
                }
            }
        }
        
        if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget && tu.getFSM().getState().getEnum() == EntityState.SelectingTarget) {
            //put something here later
        }
        
        if (fsm.getState().getEnum() == EntityState.AnyoneTargeted && tu.getFSM().getState().getEnum() == EntityState.SelectingTarget && tu != receivingEnd) {
            //revamp this whole battle thing later
            
            /*Battle.start(new Conveyer(selectedUnit).setEnemyUnit(receivingEnd).setMap(MasterFsmState.getCurrentMap()), 1);
            mapFSM.updateState(EntityState.PostBattle);*/
            if (mapFSM.getEnum() == EntityState.PostActionMenuOpened) {
                mapFSM.updateState(EntityState.PreBattle).setConveyer(new Conveyer(selectedUnit).setEnemyUnit(receivingEnd).setMap(MasterFsmState.getCurrentMap()));
            }
        }
    }
    
    public void displayRange(TangibleUnit tu, Map mp, int layer, AssetManager assetManager) {
        for (int x = 0; x < mp.fullmap[layer].length; x++) {
            for (int y = 0; y < mp.fullmap[layer][0].length; y++) {
                if (Map.isWithinSpaces(tu.getMOBILITY(), tu.getPosX(), tu.getPosY(), mp.fullmap[layer][x][y].getPosX(), mp.fullmap[layer][x][y].getPosY())) {
                    //reveal at specified opacity in blue tile
                    Texture tx = assetManager.loadTexture("Textures/tiles/movsquare.png");
                    mp.movSet[layer][x][y].changeTexture(tx);
                    mp.movSet[layer][x][y].tile.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, tileOpacity));
                    //mp.movSet[layer][x][y].tile.getMaterial().setColor("Color", new ColorRGBA(0.122f, 0.247f, 0.804f, tileOpacity));
                } else {
                    for (int i = 0; i < Catalog.furthestTrue(tu.getEquippedWeapon().getRange()); i++) {
                        
                        if (Map.isWithinSpaces(tu.getMOBILITY() + i + 1, tu.getPosX(), tu.getPosY(), x, y)) {
                            //reveal red tile
                            Texture tx = assetManager.loadTexture("Textures/tiles/atksquare.png");
                            mp.movSet[layer][x][y].changeTexture(tx);
                            mp.movSet[layer][x][y].tile.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, tileOpacity));
                            //mp.movSet[layer][x][y].tile.getMaterial().setColor("Color", new ColorRGBA(0.902f, 0.176f, 0.082f, tileOpacity)); //red
                        } else {
                            //dissipate
                            Texture tx = assetManager.loadTexture("Textures/tiles/movsquare.png");
                            mp.movSet[layer][x][y].changeTexture(tx);
                            mp.movSet[layer][x][y].tile.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 0));
                            //mp.movSet[layer][x][y].tile.getMaterial().setColor("Color", new ColorRGBA(0.122f, 0.247f, 0.804f, 0));
                        }
                    }
                }
            }
        }
    }
    
    public void cancelRange(int layer, Map mp) {
        for (int x2 = 0; x2 < mp.fullmap[layer].length; x2++) {
            for (int y2 = 0; y2 < mp.fullmap[layer][0].length; y2++) {               
                if (mp.movSet[layer][x2][y2].tile.getMaterial().getAdditionalRenderState().getBlendMode() != RenderState.BlendMode.Alpha) {
                    mp.movSet[layer][x2][y2].tile.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                }

                mp.movSet[layer][x2][y2].tile.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, 0));
            }
        }
    }
    
    public void resolveInput(String name, float tpf, boolean keyPressed) {
        //System.out.println(accumulatedTPF);
        if (!directionHeld() && keyPressed) {
            cursorSpeed = 1.0f;
        }
        
        /*if (!keyPressed) {
            if (!directionHeld()) {
                accumulatedTPF = 0;
            } else {}
        }*/
        
        if (fsm.getState().getEnum() != EntityState.Idle) {
            if (name.equals("move up")) {
                if (keyPressed) {
                    //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                    if (holding[1] == Direction.Down) {
                        holding[1] = null;
                        setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        accumulatedCursorDistanceY = 0;
                        toTraverseY = 0;
                    }
                    translateY(1);
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget) { selectionDifferenceY += 1; }
                    holding[0] = Direction.Up;
                    //accumulatedTPF += tpf;
                } else {
                    /*if (translatingY && directionHeld()) {
                        //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        toTraverseY = -1 * accumulatedCursorDistanceY;
                    } else { toTraverseY = 0; }*/
                    holding[0] = null;
                }
            } else if (name.equals("move down")) {
                if (keyPressed) {
                    //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                    if (holding[0] == Direction.Up) {
                        holding[0] = null;
                        setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        accumulatedCursorDistanceY = 0;
                        toTraverseY = 0;
                    }
                    translateY(-1);
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget) { selectionDifferenceY -= 1; }
                    holding[1] = Direction.Down;
                    //accumulatedTPF += tpf;
                } else {
                    /*if (translatingY && directionHeld()) {
                        //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        toTraverseY = accumulatedCursorDistanceY;
                    } else { toTraverseY = 0; }*/
                    
                    holding[1] = null;
                }
            }  
            
            if (name.equals("move left")) {
                if (keyPressed) {
                    //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                    if (holding[3] == Direction.Right) {
                        holding[3] = null;
                        setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        accumulatedCursorDistanceX = 0;
                        toTraverseX = 0;
                    }
                    translateX(-1);
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget) { selectionDifferenceX -= 1; }
                    holding[2] = Direction.Left;
                    //accumulatedTPF += tpf;
                } else {
                    /*if (translatingX && directionHeld()) {
                        //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        toTraverseX = accumulatedCursorDistanceX;
                    } else { toTraverseX = 0; }*/
                    holding[2] = null;
                }
            } else if (name.equals("move right")) {
                if (keyPressed) {
                    //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                    if (holding[2] == Direction.Left) {
                        holding[2] = null;
                        setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        accumulatedCursorDistanceX = 0;
                        toTraverseX = 0;
                    }
                    translateX(1);
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelectingTarget) { selectionDifferenceX += 1; }
                    holding[3] = Direction.Right;
                    //accumulatedTPF += tpf;
                } else {
                    /*if (translatingX && directionHeld()) {
                        //setPosition(pX, pY, elv, MasterFsmState.getCurrentMap());
                        toTraverseX = -1 * accumulatedCursorDistanceX;
                    } else { toTraverseX = 0; }*/
                    holding[3] = null;
                }
            } 
            
            if (name.equals("select")) {
                if (keyPressed) {
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelected) {
                        tileOpacity = 0.85f;
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
                        tileOpacity = 0.5f;
                    } else { tileOpacity = 0f; }
                } else {
                    tileOpacity = 0.85f;
                }
            }
            if (name.equals("deselect")) {
                if (keyPressed) {
                    if (fsm.getState().getEnum() == EntityState.AnyoneSelected) {
                        //fsm.setNewStateIfAllowed(new MasterFsmState().setAssetManager(assetManager));
                        selectedUnit.isSelected = false;
                        selectedUnit = null;
                        setStateIfAllowed(new FsmState(EntityState.CursorDefault));
                        tileOpacity = 0.5f;
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
        selectedUnit.animVar = 0;
        cancelRange(elv, M);
        M.fullmap[elv][selectedUnit.getPosX()][selectedUnit.getPosY()].setOccupier(selectedUnit);
        selectedUnit.isSelected = false;
        selectedUnit.setStateIfAllowed(new FsmState(EntityState.Done));
        selectedUnit = null;
        fsm.forceState(new FsmState(EntityState.CursorDefault));
    }
    
    public void setSpeed(float speed) { //default is 1
        cursorSpeed = speed;
    } 
    
    public void tryContinueX() {
        if (holding[2] == Direction.Left) {
            translateX(-1);
        } else if (holding[3] == Direction.Right) {
            translateX(1);
        }
    }
    
    public void tryContinueY() {
        if (holding[0] == Direction.Up) {
            translateY(1);
        } else if (holding[1] == Direction.Down) {
            translateY(-1);
        }
    }
    
    public boolean directionHeld() {
        for (int i = 0; i < holding.length; i++) {
            if (holding[i] != null) {
                return true;
            }
        }
        return false;
    } 
    
    public void updatePosition(Map map) {
        int closestX = 0, closestY = 0;
        for (int x = 0; x < map.getTilesX(); x++) {
            for (int y = 0; y < map.getTilesY(); y++) {
                if (FastMath.abs(map.fullmap[elv][x][y].tile.getWorldTranslation().x) + FastMath.abs(map.fullmap[elv][x][y].tile.getWorldTranslation().y) < FastMath.abs(map.fullmap[elv][closestX][closestY].tile.getWorldTranslation().x) + FastMath.abs(map.fullmap[elv][closestX][closestY].tile.getWorldTranslation().y)) {
                    closestX = x;
                    closestY = y;
                }
            }
        }
        pX = closestX;
        pY = closestY;
    }
    
    public Purpose getPurpose() { return purpose; }
    
    public void setPurpose(Purpose P) {
        purpose = P;
    }
}
