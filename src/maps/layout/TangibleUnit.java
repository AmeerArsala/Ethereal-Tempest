/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import battle.ability.Ability;
import battle.formation.Formation;
import battle.skill.Skill;
import battle.Unit;
import battle.formula.Formula;
import battle.item.Weapon;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import misc.DirFileExplorer;
import etherealtempest.FSM;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import java.util.List;

/**
 *
 * @author night
 */
public class TangibleUnit extends Unit {
    //forest tileWeight = 15
    //private final int INFANTRY_RESOLVE = 13, CAVALRY_RESOLVE = 11, ARMORED_RESOLVE = 15, MONSTER_RESOLVE = 14, MORPH_RESOLVE = 13, MECHANISM_RESOLVE = 12;
    public int Resolve = 0;
    public int animVar = 0;
    public int currentParryCooldown;
    
    private int saveMaxParryCooldown;
    private int posX, posY, elevation;
    
    int prevX, prevY;
    
    private Skill inUse = null;
    private Formula toUse = null;
    
    public String ustatus = "Healthy";
    
    private Quad q = new Quad(20f, 20f);
    private Geometry geo = new Geometry("Quad", q);
    
    private Path pathway = null;
    
    private final String filepath, fpath2;
    private final String[] silence, load;
    public DirFileExplorer[] dfes;
    
    protected Material defMat;
    
    public boolean hoverSetter = false;
    public boolean isSelected = false;
    public boolean hasStashAccess = false;
    public boolean parryDecider = true;
    
    private static int IDgen = 0;
    
    private final int id;
    
    public enum BattleRole {
        Initiator,
        Receiver
    }
    
    private final FSM fsm = new FSM(){
        @Override
        public void setNewStateIfAllowed(FsmState st) {
            if (state == null || (state.getEnum() != EntityState.Dead && state.getEnum() != EntityState.Done)) {
                state = st;
                accumulatedMovTime = 0;
                movLength = -1;
                prevX = posX;
                prevY = posY;
            } //you can forceState() to forcefully change it
        }
        
    };
    
    public TangibleUnit(Unit X, int posX, int posY) {
        super(X.getName(), X, X.getStats(), X.getGrowthRates(), X.getInventory().getItems(), X.getFormulas(), X.getTalents(), X.getAbilities(), X.getSkills(), X.getFormations(), X.getIsBoss());
        this.posX = posX;
        this.posY = posY;
        
        filepath = "assets\\Models\\Sprites\\map\\" + name + "\\";
        fpath2 = "Models/Sprites/map/" + name + "/";
        
        silence = new String[]{filepath + "idle", filepath + "up", filepath + "left", filepath + "right", filepath + "down"};
        load = new String[]{fpath2 + "idle/", fpath2 + "up/", fpath2 + "left/", fpath2 + "right/", fpath2 + "down/"};
        
        dfes = new DirFileExplorer[]
        {
            new DirFileExplorer(silence[0]), 
            new DirFileExplorer(silence[1]), 
            new DirFileExplorer(silence[2]), 
            new DirFileExplorer(silence[3]),
            new DirFileExplorer(silence[4]) 
        };
        
        fsm.setNewStateIfAllowed(new FsmState(EntityState.Active));
        
        setResolve();
        stabilize();
        
        id = IDgen;
        IDgen++;
    }
    
    public TangibleUnit(Unit X) {
        super(X.getName(), X, X.getStats(), X.getGrowthRates(), X.getInventory().getItems(), X.getFormulas(), X.getTalents(), X.getAbilities(), X.getSkills(), X.getFormations(), X.getIsBoss());
        
        filepath = "assets\\Models\\Sprites\\map\\" + X.getName() + "\\";
        fpath2 = "Models/Sprites/map/" + X.getName() + "/";
       
        silence = new String[]{filepath + "idle", filepath + "up", filepath + "left", filepath + "right", filepath + "down"};
        load = new String[]{fpath2 + "idle/", fpath2 + "up/", fpath2 + "left/", fpath2 + "right/", fpath2 + "down/"};
        
        portraitString = X.portraitString;
        
        dfes = new DirFileExplorer[]
        {
            new DirFileExplorer(silence[0]), 
            new DirFileExplorer(silence[1]), 
            new DirFileExplorer(silence[2]), 
            new DirFileExplorer(silence[3]),
            new DirFileExplorer(silence[4]) 
        };
        
        fsm.setNewStateIfAllowed(new FsmState(EntityState.Active));
        
        setResolve();
        stabilize();
        
        id = IDgen;
        IDgen++;
    }
    
    public TangibleUnit(Unit X, Material defaultMat) {
        super(X.getName(), X, X.getStats(), X.getGrowthRates(), X.getInventory().getItems(), X.getFormulas(), X.getTalents(), X.getAbilities(), X.getSkills(), X.getFormations(), X.getIsBoss());
        
        filepath = "assets\\Models\\Sprites\\map\\" + X.getName() + "\\";
        fpath2 = "Models/Sprites/map/" + X.getName() + "/";
        
        silence = new String[]{filepath + "idle", filepath + "up", filepath + "left", filepath + "right", filepath + "down"};
        load = new String[]{fpath2 + "idle/", fpath2 + "up/", fpath2 + "left/", fpath2 + "right/", fpath2 + "down/"};
        
        portraitString = X.portraitString;
        
        defMat = defaultMat;
        geo.setMaterial(defMat);
        
        dfes = new DirFileExplorer[]
        {
            new DirFileExplorer(silence[0]), 
            new DirFileExplorer(silence[1]), 
            new DirFileExplorer(silence[2]), 
            new DirFileExplorer(silence[3]),
            new DirFileExplorer(silence[4]) 
        };
        
        fsm.setNewStateIfAllowed(new FsmState(EntityState.Active));
        
        setResolve();
        stabilize();
        
        id = IDgen;
        IDgen++;
    }
    
    private void stabilize() {
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        
        geo.setLocalRotation(rotation);
    }
    
    public void setPos(int x, int y, int layer, Map map) {
        posX = x;
        posY = y;
        elevation = layer;
        
        geo.setLocalTranslation(map.fullmap[layer][x][y].getGeometry().getWorldTranslation());
        //geo.setLocalTranslation(geo.getLocalTranslation().x - 4f, map.fullmap[layer][x][y].tile.getWorldTranslation().y + 209f, geo.getLocalTranslation().z - 1f);
        geo.setLocalTranslation(geo.getLocalTranslation().x - 4, map.fullmap[layer][x][y].getHighestPointHeight() + 1, geo.getLocalTranslation().z - 1);
    }
    
    private void rewritePos(Map map, int layer) {
        for (int x = 0; x < map.fullmap[layer].length; x++) {
            for (int y = 0; y < map.fullmap[layer][x].length; y++) {
                float difX = FastMath.abs(map.fullmap[layer][x][y].getWorldTranslation().z - geo.getWorldTranslation().z), 
                      difY = FastMath.abs(map.fullmap[layer][x][y].getWorldTranslation().x - geo.getWorldTranslation().x);
                if (difX == 0 && difY == 0) {
                    posX = x;
                    posY = y;
                }
            }
        }
    }
    
    public Quad getQuad() { return q; }
    public Geometry getGeometry() { return geo; }
    
    public String animationOnFrameUpdate(int x, double f) { //x: 0 = idle, 1 = up, 2 = left, 3 = right, 4 = down
        double coefficient = (dfes[x].getFileCount("png") / 2.0);
        int index = (int) ((-1 * coefficient * Math.cos(f)) + coefficient);
        return load[x] + index + ".png";
    } //DO NOT USE THIS ONE
    
    public String animationOnFrameUpdate(int x, int index) { //x: 0 = idle, 1 = up, 2 = left, 3 = right, 4 = down
        return load[x] + index + ".png";
    }
    
    public void updateTexture(Texture t) {
        geo.getMaterial().setTexture("ColorMap", t);
    }
    
    private void setResolve() {
        if (MovementType().equals("infantry") || MovementType().equals("morph")) {
            Resolve = 13;
        } else if (MovementType().equals("cavalry")) {
            Resolve = 11;
        } else if (MovementType().equals("armored")) {
            Resolve = 15;
        } else if (MovementType().equals("monster")) {
            Resolve = 14;
        } else if (MovementType().equals("mechanism")) {
            Resolve = 12;
        }
    }
    
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getElevation() { return elevation; }
    
    float totalDistanceX = 0, totalDistanceY = 0;
    
    public void moveTo(int stposX, int stposY, int destinationX, int destinationY, int layer, Map map, float distanceperframe, float accumulatedDistance, float prevaccumulatedDistance) { //distanceperframe must be a power of 2 and <= 16
        //System.out.println("posX: " + posX + ", posY: " + posY);
        if (destinationX == posX && destinationY == posY) {
            animVar = 0;
            totalDistanceX = 0;
            totalDistanceY = 0;
        } else {
            int i = (int)(accumulatedDistance / 16f);
            List<Tile> path = pathway.getPath();
            
            if (i > 0) {
                //horizontal
                if (path.get(i).getPosX() - path.get(i - 1).getPosX() < 0) {
                    //left
                    geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, geo.getLocalTranslation().z - distanceperframe);
                    animVar = 2;
                    totalDistanceX -= distanceperframe;
                } else if (path.get(i).getPosX() - path.get(i - 1).getPosX() > 0) {
                    //right
                    geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, geo.getLocalTranslation().z + distanceperframe);
                    animVar = 3;
                    totalDistanceX += distanceperframe;
                } else {
                    //vertical
                    if (path.get(i).getPosY() - path.get(i - 1).getPosY() > 0) {
                        //up
                        geo.setLocalTranslation(geo.getLocalTranslation().x + distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                        animVar = 1;
                        totalDistanceY += distanceperframe;
                    } else if (path.get(i).getPosY() - path.get(i - 1).getPosY() < 0) {
                        //down
                        geo.setLocalTranslation(geo.getLocalTranslation().x - distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                        animVar = 4;
                        totalDistanceY -= distanceperframe;
                    }
                }
                geo.setLocalTranslation(geo.getLocalTranslation().x, map.fullmap[layer][stposX + ((int)(totalDistanceX / 16f))][stposY + ((int)(totalDistanceY / 16f))].getHighestPointHeight() + 1, geo.getLocalTranslation().z);
                return;
            }
            //horizontal
            if (path.get(i).getPosX() - stposX < 0) {
                //left
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, geo.getLocalTranslation().z - distanceperframe);
                animVar = 2;
                totalDistanceX -= distanceperframe;
            } else if (path.get(i).getPosX() - stposX > 0) {
                //right
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, geo.getLocalTranslation().z + distanceperframe);
                animVar = 3;
                totalDistanceX += distanceperframe;
            } else {
                //vertical
                if (path.get(i).getPosY() - stposY > 0) {
                    //up
                    geo.setLocalTranslation(geo.getLocalTranslation().x + distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                    animVar = 1;
                    totalDistanceY += distanceperframe;
                } else if (path.get(i).getPosY() - stposY < 0) {
                    //down
                    geo.setLocalTranslation(geo.getLocalTranslation().x - distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                    animVar = 4;
                    totalDistanceY -= distanceperframe;
                }
            }
            geo.setLocalTranslation(geo.getLocalTranslation().x, map.fullmap[layer][stposX + ((int)(totalDistanceX / 16f))][stposY + ((int)(totalDistanceY / 16f))].getHighestPointHeight() + 1, geo.getLocalTranslation().z);
            //rewritePos(map, layer);
        }
    }
    
    public boolean willParryAgainst(TangibleUnit enemy) {
        return currentParryCooldown == 0 && parryDecider;
    }
    
    public int getEnemyAmount(ArrayList<TangibleUnit> allUnits) {
        int count = 0;
        for (TangibleUnit character : allUnits) {
            if (unitStatus.getValue() != character.unitStatus.getValue() && unitStatus.getValue() + character.unitStatus.getValue() > -1) {
                count++;
            }
        }
        return count;
    }
    
    public void incrementParryCooldown() {
        if (currentParryCooldown > 0) { currentParryCooldown--; } else { currentParryCooldown = saveMaxParryCooldown; }
    }
    
    public void resetParryCooldown(ArrayList<TangibleUnit> allUnits) { 
        currentParryCooldown = maxParryCooldown(allUnits); 
        saveMaxParryCooldown = currentParryCooldown;
    }
    
    public int maxParryCooldown(ArrayList<TangibleUnit> allUnits) { return ((int)(getEnemyAmount(allUnits) / (0.5 * getCOMP()))); }
    
    public void remapPositions(int x, int y, int layer, Map map) {
        setPos(x, y, layer, map);
        map.fullmap[layer][x][y].isOccupied = true;
        map.fullmap[layer][x][y].setOccupier(this);
    }
    
    
    
    public void setStateIfAllowed(FsmState state) {
        fsm.setNewStateIfAllowed(state);
    }
    
    public FSM getFSM() { return fsm; }
    
    float accumulatedTime = 0, accumulatedMovTime = 0, previoustpf;
    int movLength = -1, pstartX = 0, pstartY = 0, frameCount = 0;
    
    
    public void updateAI(float tpf, FSM mapFSM) {
        
            switch (fsm.getState().getEnum()) {
                case Moving:
                {
                    
                    //animate sprite
                    int frameIndex = ((int)(frameCount * 0.1) + dfes[animVar].getFileCount("png")) % dfes[animVar].getFileCount("png");
                    updateTexture(((MasterFsmState)mapFSM.getState()).getAssetManager().loadTexture(animationOnFrameUpdate(animVar, (int)(frameIndex))));
                    getGeometry().getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                    getGeometry().setQueueBucket(RenderQueue.Bucket.Transparent);
                    
                    float dpf = 160 * tpf; //160 distance per second; THE COEFFICIENT MUST BE A MULTIPLE OF 40
                    float accumulatedDistance = (accumulatedMovTime * 160);
                    float previousmovtime = accumulatedMovTime - previoustpf;
                    float prevAccumulatedDistance = (previousmovtime * 160);
                
                    if (movLength < 0) { //is only called at the start
                        //movLength = ((MoveState)fsm.getState()).getMap().generatePath(posX, posY, ((MoveState)fsm.getState()).getCursor().pX, ((MoveState)fsm.getState()).getCursor().pY, ((MoveState)fsm.getState()).getCursor().getElevation()).size();
                        pstartX = posX;
                        pstartY = posY;
                        totalDistanceX = 0;
                        totalDistanceY = 0;
                        pathway = new Path(((MoveState)fsm.getState()).getMap(), pstartX, pstartY, ((MoveState)fsm.getState()).getCursor().pX, ((MoveState)fsm.getState()).getCursor().pY, ((MoveState)fsm.getState()).getCursor().getElevation());
                        movLength = pathway.getPath().size();
                    }   
                
                    if (posX == ((MoveState)fsm.getState()).getCursor().pX && posY == ((MoveState)fsm.getState()).getCursor().pY || ((int)(accumulatedDistance / 16f)) >= movLength) {
                        //open menu
                        if (mapFSM.getState().getEnum() != EntityState.PostActionMenuOpened) {
                            mapFSM.setNewStateIfAllowed(((MasterFsmState)mapFSM.getState()).updateState(EntityState.PostActionMenuOpened)); //switch this if PostActionMenuOpened will need some extra stuff
                        }
                    } else {
                        moveTo(pstartX, pstartY, ((MoveState)fsm.getState()).getCursor().pX, ((MoveState)fsm.getState()).getCursor().pY, ((MoveState)fsm.getState()).getCursor().getElevation(), ((MoveState)fsm.getState()).getMap(), dpf, accumulatedDistance, prevAccumulatedDistance);
                    }   
                
                    accumulatedMovTime += tpf;
                    break;
                }
                
                case Active:
                {
                    //animate sprite
                    int frameIndex = ((int)(frameCount * 0.1) + dfes[animVar].getFileCount("png")) % dfes[animVar].getFileCount("png");
                    updateTexture(((MasterFsmState)mapFSM.getState()).getAssetManager().loadTexture(animationOnFrameUpdate(animVar, (int)(frameIndex))));
                    getGeometry().getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                    getGeometry().setQueueBucket(RenderQueue.Bucket.Transparent);
                    break;
                }
                
                case Done:
                {
                    isSelected = false;
                    fsm.forceState(new FsmState().setEnum(EntityState.Active)); //this is temporary and just for testing purposes
                    mapFSM.setNewStateIfAllowed(((MasterFsmState)mapFSM.getState()).updateState(EntityState.MapDefault));
                    break;
                }
                
                case Dead:
                {
                    break;
                }
                
                case Paused:
                {
                    break;
                }
                
                case SelectingTarget:
                {
                    //animate sprite
                    int frameIndex = ((int)(frameCount * 0.1) + dfes[animVar].getFileCount("png")) % dfes[animVar].getFileCount("png");
                    updateTexture(((MasterFsmState)mapFSM.getState()).getAssetManager().loadTexture(animationOnFrameUpdate(animVar, (int)(frameIndex))));
                    getGeometry().getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                    getGeometry().setQueueBucket(RenderQueue.Bucket.Transparent);
                    break;
                }
                
                case Idle:
                {
                    //animate sprite
                    int frameIndex = ((int)(frameCount * 0.1) + dfes[animVar].getFileCount("png")) % dfes[animVar].getFileCount("png");
                    updateTexture(((MasterFsmState)mapFSM.getState()).getAssetManager().loadTexture(animationOnFrameUpdate(animVar, (int)(frameIndex))));
                    getGeometry().getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                    getGeometry().setQueueBucket(RenderQueue.Bucket.Transparent);
                    break;
                }
                
                default:
                    break;
            }
        
    }
    
    public void update(float tpf, FSM mapFSM) {
        if (frameCount > 1000) {
            frameCount = 0;
        }
        
        updateAI(tpf, mapFSM);
        
        frameCount++;
        previoustpf = tpf;
        accumulatedTime += tpf;
    }
    
    public Skill getToUseSkill() {
        return inUse;
    }
    
    public void setToUseSkill(Skill S) {
        inUse = S;
    }
    
    public Formula getToUseFormula() {
        return toUse;
    }
    
    public void setToUseFormula(Formula F) {
        toUse = F;
    }
    
    public int getSpecifiedATK() {
        if (toUse != null) {
            return getETHER() + toUse.getPow();
        }
        
        return getATK();
    }
    
    public int getSpecifiedAccuracy() {
        if (toUse != null) {
            return toUse.getStatus() ? (toUse.getAcc() + (((getDEX() * 4) + getCOMP()) / 2) + ClassBattleBonus()[0]) : 0;
        }
        return getAccuracy(); 
    } //add commander bonus
    
    public int getSpecifiedCrit() {
        if (toUse != null) {
            return toUse.getStatus() ? (toUse.getCRIT() + (getDEX() / 2) + ClassBattleBonus()[2]) : 0;
        }
        return getCrit();
    }
    
    public int getSpecifiedMobility() { //CHANGE THIS SO IT RESTRICTS MOVEMENT
        return getMOBILITY();
    }
    
    @Override
    public Weapon getEquippedWeapon() { 
        if (toUse != null) {
            return toUse;
        }
        if (getInventory().getItems().get(0) instanceof Weapon) {
            return (Weapon)getInventory().getItems().get(0);
        }
        return new Weapon(false); //if weapon type is equal to empty slot, unit can't attack
    }
    
    public int getID() {
        return id;
    }
    
}


