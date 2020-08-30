/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import battle.Combatant;
import battle.Combatant.BattleStat;
import battle.Conveyer;
import battle.ability.Ability;
import battle.formation.Formation;
import battle.skill.Skill;
import battle.Unit;
import battle.formula.Formula;
import battle.item.Weapon;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import etherealtempest.DataStructure;
import java.util.ArrayList;
import misc.DirFileExplorer;
import etherealtempest.FSM;
import etherealtempest.FSM.EntityState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import etherealtempest.Request;
import etherealtempest.RequestDealer;
import etherealtempest.Requestable;
import fundamental.DamageTool;
import general.Spritesheet;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class TangibleUnit extends Unit {
    //forest tileWeight = 15
    //private final int INFANTRY_RESOLVE = 13, CAVALRY_RESOLVE = 11, ARMORED_RESOLVE = 15, MONSTER_RESOLVE = 14, MORPH_RESOLVE = 13, MECHANISM_RESOLVE = 12;
    public int animVar = 0;
    public int currentParryCooldown;
    
    private int saveMaxParryCooldown; //max parry cd
    private int posX, posY, elevation;
    
    int prevX, prevY;
    
    private Skill inUse = null;
    
    public String ustatus = "Healthy"; //status ailments, etc.
    public UnitStatus unitStatus; //allegiance
    
    public enum UnitStatus {
        @SerializedName("Player") Player(0),
        @SerializedName("Ally") Ally(-1),
        @SerializedName("Enemy") Enemy(1),
        @SerializedName("ThirdParty") ThirdParty(2),
        @SerializedName("FourthParty") FourthParty(3),
        @SerializedName("FifthParty") FifthParty(4);
        
        private final int value;
        
        private static HashMap map = new HashMap<>();
        private UnitStatus(int val) {
            value = val;
        }
        
        static {
            for (UnitStatus stat : UnitStatus.values()) {
                map.put(stat.value, stat);
            }
        }

        public static UnitStatus valueOf(int stat) {
            return (UnitStatus) map.get(stat);
        }

        public int getValue() {
            return value;
        }
        
        public ColorRGBA getAssociatedColor() {
            ColorRGBA barColor;
        
            switch (value) {
                case 0: //blue (Player)
                    barColor = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f);
                    break;
                case 1: //red (Enemy)
                    barColor = new ColorRGBA(0.839f, 0, 0, 1f);
                    break;
                case -1: //yellow (Ally)
                    barColor = new ColorRGBA(1f, 0.851f, 0, 1f);
                    break;
                case 2: //green (ThirdParty)
                    barColor = new ColorRGBA(0, 1f, 0, 1f);
                    break;
                case 3: //purple (FourthParty)
                    barColor = new ColorRGBA(0.784f, 0, 1f, 1f);
                    break;
                case 4: //white (FifthParty)
                    barColor = ColorRGBA.White;
                    break;
                default:
                    barColor = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f); //blue
                    break;
            }
        
            return barColor;
        }
    }
    
    private Quad q = new Quad(20f, 20f);
    private Geometry geo = new Geometry("Quad", q);
    
    private Path pathway = null;
    
    private String filepath, fpath2;
    private String[] silence, load;
    private DirFileExplorer[] dfes;
    
    private Spritesheet spritesheetInfo = null;
    private AnimationState animState = AnimationState.Idle;
    
    protected Material defMat;
    
    public boolean hoverSetter = false;
    public boolean isSelected = false;
    public boolean hasStashAccess = false;
    public boolean parryDecider = true;
    public boolean isLeader = false;
    
    private static int IDgen = 0;
    
    private final int id;
    private final RequestDealer requestDealer = new RequestDealer();
    
    private final DeclarationType dType; //temporary
    
    public enum BattleRole {
        Initiator,
        Receiver
    }
    
    enum AnimationState {
        MovingDown(0),
        MovingRight(1),
        MovingLeft(2),
        MovingUp(3),
        Idle(4),
        Idle2(5);
        
        final int row;
        private AnimationState(int r) {
            row = r;
        }
        
        public int getValue() { return row; }
    }
    
    private enum DeclarationType {
        Frames,
        Spritesheet
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

        stabilize();
        
        id = IDgen;
        IDgen++;
        
        dType = DeclarationType.Frames;
    }
    
    public TangibleUnit(Unit X) {
        super(X.getName(), X, X.getStats(), X.getGrowthRates(), X.getInventory().getItems(), X.getFormulas(), X.getTalents(), X.getAbilities(), X.getSkills(), X.getFormations(), X.getIsBoss());
        
        filepath = "assets\\Models\\Sprites\\map\\" + X.getName() + "\\";
        fpath2 = "Models/Sprites/map/" + X.getName() + "/";
       
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
        
        stabilize();
        
        id = IDgen;
        IDgen++;
        
        dType = DeclarationType.Frames;
    }
    
    public TangibleUnit(Unit X, Material defaultMat) {
        super(X.getName(), X, X.getStats(), X.getGrowthRates(), X.getInventory().getItems(), X.getFormulas(), X.getTalents(), X.getAbilities(), X.getSkills(), X.getFormations(), X.getIsBoss());
        
        filepath = "assets\\Models\\Sprites\\map\\" + X.getName() + "\\";
        fpath2 = "Models/Sprites/map/" + X.getName() + "/";
        
        silence = new String[]{filepath + "idle", filepath + "up", filepath + "left", filepath + "right", filepath + "down"};
        load = new String[]{fpath2 + "idle/", fpath2 + "up/", fpath2 + "left/", fpath2 + "right/", fpath2 + "down/"};
        
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
        
        stabilize();
        
        id = IDgen;
        IDgen++;
        
        dType = DeclarationType.Frames;
    }
    
    public TangibleUnit(Unit X, AssetManager assetManager) { //hasExtraIdleAnimation = hasSixthRow
        super(X.getName(), X, X.getStats(), X.getGrowthRates(), X.getInventory().getItems(), X.getFormulas(), X.getTalents(), X.getAbilities(), X.getSkills(), X.getFormations(), X.getIsBoss());
        
        spritesheetInfo = deserializeFromJSON();
        
        q = new Quad(25f, 25f);
        geo = new Geometry("character", q);
        
        defMat = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        defMat.setTexture("ColorMap", assetManager.loadTexture("Models/Sprites/map/" + name + "/" + spritesheetInfo.getSheetName()));
        defMat.setFloat("SizeX", spritesheetInfo.getColumns());
        defMat.setFloat("SizeY", spritesheetInfo.getRows());
        defMat.setFloat("Position", 0f);
        defMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        geo.setMaterial(defMat);
        
        stabilize();
        
        id = IDgen;
        IDgen++;
        
        fsm.setNewStateIfAllowed(new FsmState(EntityState.Active));
        
        dType = DeclarationType.Spritesheet;
    }
    
    public void setStateIfAllowed(FsmState state) {
        fsm.setNewStateIfAllowed(state);
    }
    
    public FSM getFSM() { return fsm; }
    
    private void stabilize() {
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -2f), (FastMath.PI / -2f), 0);
        
        geo.setLocalRotation(rotation);
    }
    
    private void setPos(int x, int y, int layer, Map map) {
        posX = x;
        posY = y;
        elevation = layer;
        
        geo.setLocalTranslation(map.fullmap[layer][x][y].getGeometry().getWorldTranslation());
        geo.setLocalTranslation(geo.getLocalTranslation().x - 6.25f, map.fullmap[layer][x][y].getHighestPointHeight() + 1, geo.getLocalTranslation().z - 3f);
    }
    
    public void remapPositions(int x, int y, int layer, Map map) {
        setPos(x, y, layer, map);
        map.fullmap[layer][x][y].isOccupied = true;
        map.fullmap[layer][x][y].setOccupier(this);
    }
    
    public Quad getQuad() { return q; }
    public Geometry getGeometry() { return geo; }
    
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
    
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getElevation() { return elevation; }
    
    private float totalDistanceX = 0, totalDistanceY = 0;
    
    public void moveTo(int stposX, int stposY, int destinationX, int destinationY, int layer, Map map, float distanceperframe, float accumulatedDistance, float prevaccumulatedDistance) { //distanceperframe must be a power of 2 and <= 16
        //System.out.println("posX: " + posX + ", posY: " + posY);
        if (destinationX == posX && destinationY == posY) {
            animVar = 0;
            animState = AnimationState.Idle2;
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
                    animState = AnimationState.MovingLeft;
                    totalDistanceX -= distanceperframe;
                } else if (path.get(i).getPosX() - path.get(i - 1).getPosX() > 0) {
                    //right
                    geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, geo.getLocalTranslation().z + distanceperframe);
                    animVar = 3;
                    animState = AnimationState.MovingRight;
                    totalDistanceX += distanceperframe;
                } else {
                    //vertical
                    if (path.get(i).getPosY() - path.get(i - 1).getPosY() > 0) {
                        //up
                        geo.setLocalTranslation(geo.getLocalTranslation().x + distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                        animVar = 1;
                        animState = AnimationState.MovingUp;
                        totalDistanceY += distanceperframe;
                    } else if (path.get(i).getPosY() - path.get(i - 1).getPosY() < 0) {
                        //down
                        geo.setLocalTranslation(geo.getLocalTranslation().x - distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                        animVar = 4;
                        animState = AnimationState.MovingDown;
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
                animState = AnimationState.MovingLeft;
                totalDistanceX -= distanceperframe;
            } else if (path.get(i).getPosX() - stposX > 0) {
                //right
                geo.setLocalTranslation(geo.getLocalTranslation().x, geo.getLocalTranslation().y, geo.getLocalTranslation().z + distanceperframe);
                animVar = 3;
                animState = AnimationState.MovingRight;
                totalDistanceX += distanceperframe;
            } else {
                //vertical
                if (path.get(i).getPosY() - stposY > 0) {
                    //up
                    geo.setLocalTranslation(geo.getLocalTranslation().x + distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                    animVar = 1;
                    animState = AnimationState.MovingUp;
                    totalDistanceY += distanceperframe;
                } else if (path.get(i).getPosY() - stposY < 0) {
                    //down
                    geo.setLocalTranslation(geo.getLocalTranslation().x - distanceperframe, geo.getLocalTranslation().y, geo.getLocalTranslation().z);
                    animVar = 4;
                    animState = AnimationState.MovingDown;
                    totalDistanceY -= distanceperframe;
                }
            }
            geo.setLocalTranslation(geo.getLocalTranslation().x, map.fullmap[layer][stposX + ((int)(totalDistanceX / 16f))][stposY + ((int)(totalDistanceY / 16f))].getHighestPointHeight() + 1, geo.getLocalTranslation().z);
            //rewritePos(map, layer);
        }
    }
    
    private float accumulatedTime = 0, accumulatedMovTime = 0, previoustpf;
    private int movLength = -1, pstartX = 0, pstartY = 0, frameCount = 0;
    
    public void updateAI(float tpf, FSM mapFSM) {
        
            switch (fsm.getState().getEnum()) {
                case Moving:
                {
                    
                    updateAnimation(((MasterFsmState)mapFSM.getState()));
                    
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
                    if (isSelected && dType == DeclarationType.Spritesheet && hasExtraIdle()) {
                        animState = AnimationState.Idle2;
                    } else if (animState == AnimationState.Idle2) {
                        animState = AnimationState.Idle;
                    }
                    updateAnimation(((MasterFsmState)mapFSM.getState()));
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
                    updateAnimation(((MasterFsmState)mapFSM.getState()));
                    break;
                }
                
                case Idle:
                {
                    updateAnimation(((MasterFsmState)mapFSM.getState()));
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
    
    public String animationOnFrameUpdate(int x, double f) { //x: 0 = idle, 1 = up, 2 = left, 3 = right, 4 = down
        double coefficient = (dfes[x].getFileCount("png") / 2.0);
        int index = (int) ((-1 * coefficient * Math.cos(f)) + coefficient);
        return load[x] + index + ".png";
    } //DO NOT USE THIS ONE
    public String animationOnFrameUpdate(int x, int index) { //x: 0 = idle, 1 = up, 2 = left, 3 = right, 4 = down
        return load[x] + index + ".png";
    }
    
    void setAnimationState(AnimationState state) {
        animState = state;
    }
    
    boolean hasExtraIdle() {
        return spritesheetInfo != null && spritesheetInfo.getRows() > 5;
    }
    
    private Spritesheet deserializeFromJSON() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Models\\Sprites\\map\\" + name + "\\info.json"));
            return gson.fromJson(reader, Spritesheet.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private void updateAnimation(MasterFsmState mapState) {
        if (dType == DeclarationType.Spritesheet) {
            defMat.setFloat("Position", calculateSpritesheetPosition());
        } else {
            //animate sprite
            int frameIndex = ((int)(frameCount * 0.1) + dfes[animVar].getFileCount("png")) % dfes[animVar].getFileCount("png");
            geo.getMaterial().setTexture("ColorMap", mapState.getAssetManager().loadTexture(animationOnFrameUpdate(animVar, (int)(frameIndex))));
            geo.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
    }
    
    private int calculateSpritesheetPosition() { //value is row
        return ((animState.getValue() * spritesheetInfo.getColumns()) + (((int)(frameCount * 0.1)) % spritesheetInfo.getColumns()));
    }
    
    public Skill getToUseSkill() {
        return inUse;
    }
    
    public void setToUseSkill(Skill S) {
        inUse = S;
    }
    
    public boolean isAlliedWith(TangibleUnit other) {
        return unitStatus == other.unitStatus || (unitStatus == UnitStatus.Player && other.unitStatus == UnitStatus.Ally) || ((unitStatus == UnitStatus.Ally && other.unitStatus == UnitStatus.Player));
    }
    
    public boolean isAlliedWith(UnitStatus allegiance) {
        return unitStatus == allegiance || (unitStatus == UnitStatus.Player && allegiance == UnitStatus.Ally);
    }
    
    public int getMobility() { //CHANGE THIS SO IT RESTRICTS MOVEMENT
        return getMOBILITY();
    }
    
    public RequestDealer getRequestDealer() {
        return requestDealer;
    }
    
    public boolean hasStashAccess() {
        if (hasStashAccess || isLeader) { return true; }
        
        for (int i = 0; i < 2; i++) {
            Tile possibilityX = MasterFsmState.getCurrentMap().fullmap[elevation][posX + ((int)Math.cos(Math.PI * i))][posY];
            Tile possibilityY = MasterFsmState.getCurrentMap().fullmap[elevation][posX][posY + ((int)Math.cos(Math.PI * i))];
            if ((possibilityX.getOccupier() != null && isAlliedWith(possibilityX.getOccupier()) && possibilityX.getOccupier().isLeader) || (possibilityY.getOccupier() != null && isAlliedWith(possibilityY.getOccupier()) && possibilityY.getOccupier().isLeader)) {
                return true;
            }
        }
        
        return false;
    }
    
    public int getID() {
        return id;
    }
    
}


