/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleRole;
import battle.Combatant.BattleStat;
import com.destroflyer.jme3.effekseer.renderer.EffekseerControl;
import maps.layout.tile.RangeDisplay;
import maps.layout.tile.Path;
import maps.layout.tile.Tile;
import etherealtempest.info.Conveyer;
import etherealtempest.characters.Unit;
import fundamental.ability.Ability;
import fundamental.formation.Formation;
import fundamental.skill.Skill;
import fundamental.formula.Formula;
import fundamental.item.Item;
import fundamental.item.Weapon;
import com.google.gson.Gson;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.LayerComparator;
import com.simsilica.lemur.ProgressBar;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import java.util.ArrayList;
import etherealtempest.FSM;
import etherealtempest.FSM.MapFlowState;
import etherealtempest.FSM.UnitState;
import etherealtempest.FsmState;
import etherealtempest.MasterFsmState;
import etherealtempest.ai.AI;
import etherealtempest.ai.AI.Behavior;
import etherealtempest.ai.AI.Condition;
import etherealtempest.info.Request;
import etherealtempest.info.RequestDealer;
import etherealtempest.ai.AllegianceRecognizer;
import etherealtempest.ai.ConditionalBehavior;
import general.Spritesheet;
import etherealtempest.info.ActionInfo;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.Bonus.StatType;
import fundamental.talent.Talent;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.tool.Tool;
import general.Spritesheet.AnimationState;
import general.visual.DeserializedParticleEffect;
import general.visual.VisualTransition.Progress;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import maps.flow.MapFlow;
import maps.layout.Coords;
import maps.layout.occupant.Cursor.Purpose;
import maps.layout.Map;
import maps.layout.tile.TileStatisticalData;

/**
 *
 * @author night
 */
public class TangibleUnit extends Unit {
    private static final int DEFAULT_TRADE_DISTANCE = 1; //adjacent
    
    private int posX, posY, elevation;
    int prevX, prevY;
    
    private int tradeDistance = DEFAULT_TRADE_DISTANCE;
    
    private AI auto = null;
    private Skill inUse = null;
    private List<String> namesOfUnitsToTalkTo = null;
    
    public boolean isSelected = false;
    public boolean hasStashAccess = false;
    public boolean isLeader = false;
    
    private final Node node = new Node();
    
    private final Material defMat;
    private final Quad q;
    private final Geometry geo;
    
    private final Material outlineMat;
    private final Quad outlineQuad;
    private final Geometry outline;
    
    private Path pathway = null;
    
    private final Node hpNode = new Node(), tpNode = new Node();
    private final ProgressBar hpBar, tpBar;
    
    private final LinkedList<DeserializedParticleEffect> effectQueue = new LinkedList<>(); //these strings are the paths to the JSON
    private Progress effectProgress = Progress.Fresh;
    private UnitState lastState;
    
    private final Spritesheet spritesheetInfo;
    private AnimationState animState = AnimationState.Idle;
    
    private int commitsToAttack = 0;
    private int commitsToEther = 0;
    private int commitsToSkill = 0;
    private int commitsToOther = 0;
    
    private static int IDgen = 0;
    
    private final int id;
    private final RequestDealer requestDealer = new RequestDealer(); //remove later
    
    private final FSM<UnitState> fsm = new FSM<UnitState>() {
        @Override
        public void setNewStateIfAllowed(FsmState<UnitState> st) {
            if (state == null || (state.getEnum() != UnitState.Dead && state.getEnum() != UnitState.Done)) { //you can forceState() to forcefully change it
                if (state != null) {
                    lastState = state.getEnum();
                }
                
                if (st.getEnum() == UnitState.Active) { //CHANGE LATER
                    if (!node.hasChild(hpNode)) {
                        node.attachChild(hpNode);
                    }
                    
                    if (!node.hasChild(tpNode)) {
                        node.attachChild(tpNode);
                    }
                }
                
                state = st;
                accumulatedMovTime = 0;
                movLength = -1;
                prevX = posX;
                prevY = posY;
            } 
        }
    };
    
    public TangibleUnit(Unit X, AssetManager assetManager) { //hasExtraIdleAnimation = hasSixthRow
        super(X.getName(), X, X.getStats(), X.getGrowthRates(), X.getInventory().getItems(), X.getFormulas(), X.getTalents(), X.getAbilities(), X.getSkills(), X.getFormations(), X.getIsBoss());
        
        //deserialize spritesheets
        spritesheetInfo = deserializeFromJSON();
        
        //create rotation
        Quaternion rotation = new Quaternion();
        rotation.fromAngles((FastMath.PI / -3f), (FastMath.PI / -2f), 0);
        
        //create quad and geometry
        q = new Quad(25f, 25f);
        geo = new Geometry("character", q);
        
        //create sprite texture
        Texture tex = assetManager.loadTexture("Models/Sprites/map/" + name + "/" + spritesheetInfo.getSheetName());
        tex.setMagFilter(MagFilter.Nearest);
        
        //initialize material
        defMat = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        defMat.setTexture("ColorMap", tex);
        defMat.setFloat("SizeX", spritesheetInfo.getMaxColumnCount());
        defMat.setFloat("SizeY", spritesheetInfo.getRowCount());
        defMat.setFloat("Position", 0f);
        defMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        defMat.getAdditionalRenderState().setDepthWrite(false);
        geo.setQueueBucket(Bucket.Transparent);
        geo.setMaterial(defMat);
        geo.setLocalRotation(rotation);
        
        //create outline quad and geometry
        outlineQuad = new Quad(25f, 25f);
        outline = new Geometry("outline", outlineQuad);
        
        //create outline texture
        Texture outlineTexture = assetManager.loadTexture("Models/Sprites/map/" + name + "/" + spritesheetInfo.getOutlineSheet());
        outlineTexture.setMagFilter(MagFilter.Nearest);
        
        //initialize outline material
        outlineMat = new Material(assetManager, "MatDefs/Spritesheet.j3md");
        outlineMat.setTexture("ColorMap", outlineTexture);
        outlineMat.setFloat("SizeX", spritesheetInfo.getMaxColumnCount());
        outlineMat.setFloat("SizeY", spritesheetInfo.getRowCount());
        outlineMat.setFloat("Position", 0f);
        outlineMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        outlineMat.getAdditionalRenderState().setDepthWrite(false);
        outline.setQueueBucket(Bucket.Transparent);
        outline.setMaterial(outlineMat);
        outline.setLocalRotation(rotation);
        outline.move(0, -0.1f, 0);
        
        //create bars
        hpBar = new ProgressBar();
        tpBar = new ProgressBar();
        
        ((QuadBackgroundComponent)hpBar.getValueIndicator().getBackground()).setColor(new ColorRGBA(0, 0.76f, 0, 1));
        ((QuadBackgroundComponent)tpBar.getValueIndicator().getBackground()).setColor(new ColorRGBA(0.85f, 0.36f, 0.83f, 1f));
        hpBar.getValueIndicator().setAlpha(1f);
        tpBar.getValueIndicator().setAlpha(1f);
        
        ((TbtQuadBackgroundComponent)hpBar.getBackground()).setColor(ColorRGBA.Black);
        ((TbtQuadBackgroundComponent)tpBar.getBackground()).setColor(ColorRGBA.Black);
        
        hpBar.setInsets(new Insets3f(0.5f, 0.5f, 0.5f, 0.5f));
        tpBar.setInsets(new Insets3f(0.5f, 0.5f, 0.5f, 0.5f));
        
        Node hpMidNode = new Node(), tpMidNode = new Node();
        
        hpMidNode.attachChild(hpBar);
        tpMidNode.attachChild(tpBar);
        
        hpMidNode.scale(0.3f);
        tpMidNode.scale(0.3f);
        
        hpMidNode.move(0, 8f, 8f);
        tpMidNode.move(10f, 10, 20f);
        
        tpMidNode.setLocalRotation(rotation);
        
        Quaternion hpRotation = new Quaternion();
        hpRotation.fromAngles((FastMath.PI / -2f), 0, FastMath.PI / 4f); //FastMath.PI / 2f
        hpMidNode.setLocalRotation(hpRotation);
        
        hpNode.attachChild(hpMidNode);
        tpNode.attachChild(tpMidNode);
        
        //set layers
        LayerComparator.setLayer(geo, 5);
        LayerComparator.setLayer(outline, 4);
        LayerComparator.setLayer(hpNode, 6);
        LayerComparator.setLayer(tpNode, 6);
        
        //attach to node
        node.attachChild(geo);
        node.attachChild(outline);
        
        //set state
        fsm.setNewStateIfAllowed(new FsmState(UnitState.Active));
        
        //create ID
        id = IDgen;
        IDgen++;
    }
    
    public FSM getFSM() { return fsm; }
    
    public void setStateIfAllowed(UnitState state) {
        fsm.setNewStateIfAllowed(state);
    }
    
    public boolean isAlliedWith(UnitAllegiance other) { return unitStatus.alliedWith(other); }
    
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public int getElevation() { return elevation; }
    
    public Coords coords() {
        return new Coords(posX, posY);
    }
    
    public VenturePeek venture() {
        return new VenturePeek(posX, posY, elevation, getMobility());
    }
    
    private void setPos(int x, int y, int layer, Map map) {
        posX = x;
        posY = y;
        elevation = layer;
        
        node.setLocalTranslation(map.fullmap[layer][x][y].getGeometry().getWorldTranslation().add(-1f, 1, -5.5f));
    }
    
    public void remapPositions(int x, int y, int layer, Map map) {
        map.fullmap[elevation][posX][posY].resetOccupier();
        setPos(x, y, layer, map);
        map.fullmap[layer][x][y].setOccupier(this);
    }
    
    public void remapPositions(Coords cds, int layer, Map map) { remapPositions(cds.getX(), cds.getY(), layer, map); }
    public void remapPositions(int x, int y, int layer) { remapPositions(x, y, layer, MasterFsmState.getCurrentMap()); }
    public void remapPositions(Coords cds, int layer) { remapPositions(cds.getX(), cds.getY(), layer); }
    
    public Quad getQuad() { return q; }
    public Quad getOutlineQuad() { return outlineQuad; } 
    public Geometry getGeometry() { return geo; }
    
    public Node getNode() { return node; }
    
    public void update(float tpf, FSM mapFSM, Camera cam) {
        if (frameCount > 1000) {
            frameCount = 0;
        }
        
        updateAI(tpf, mapFSM);
        
        hpBar.setProgressPercent(((double)getStat(BaseStat.currentHP)) / getMaxHP());
        tpBar.setProgressPercent(((double)getStat(BaseStat.currentTP)) / getMaxTP());
        
        rotateNodeWithCamera(hpNode, cam);
        rotateNodeWithCamera(tpNode, cam);
        
        frameCount++;
        //accumulatedTime += tpf;
    }
    
    private void rotateNodeWithCamera(Node point, Camera cam) {
        final float constant = 0.2f; //0.25078946f
        
        Quaternion rot = new Quaternion();
        float A = point.getWorldTranslation().x - cam.getLocation().x;
        float B = point.getWorldTranslation().z - cam.getLocation().z + 15;
        float deltaTheta = FastMath.atan(B / A) / -2.5f;
        
        if (FastMath.abs(deltaTheta) > constant) {
            float sign = deltaTheta / (FastMath.abs(deltaTheta));
            deltaTheta = constant * sign;
        }
      
        rot.fromAngles(0, deltaTheta, 0);
        point.setLocalRotation(rot);
    }
    
    private float accumulatedMovTime = 0;
    private int movLength = -1, pstartX = 0, pstartY = 0, frameCount = 0;
    
    public void updateAI(float tpf, FSM mapFSM) {
        switch (fsm.getEnumState()) {
            case Moving:
            {            
                defMat.setFloat("Position", calculateSpritesheetPosition());
                updateOutline();
                    
                float dpf = 160 * tpf; //160 distance per second; THE COEFFICIENT MUST BE A MULTIPLE OF 40
                float accumulatedDistance = (accumulatedMovTime * 160);
                //float previousmovtime = accumulatedMovTime - previoustpf;
                //float prevAccumulatedDistance = (previousmovtime * 160);
                
                if (movLength < 0) { //is only called at the start
                    pstartX = posX;
                    pstartY = posY;
                    //totalDistanceX = 0;
                    //totalDistanceY = 0;
                    pathway = new Path(MasterFsmState.getCurrentMap(), pstartX, pstartY, ((MoveState)fsm.getState()).getCursor().pX, ((MoveState)fsm.getState()).getCursor().pY, ((MoveState)fsm.getState()).getCursor().getElevation(), getMobility());
                    movLength = pathway.getPath().size();
                }   
                
                if (posX == ((MoveState)fsm.getState()).getCursor().pX && posY == ((MoveState)fsm.getState()).getCursor().pY || ((int)(accumulatedDistance / 16f)) >= movLength) {
                    //open menu
                    if (mapFSM.getState().getEnum() != MapFlowState.PostActionMenuOpened) {
                        mapFSM.setNewStateIfAllowed(((MasterFsmState)mapFSM.getState()).updateState(MapFlowState.PostActionMenuOpened)); //switch this if PostActionMenuOpened will need some extra stuff
                    }
                } else {
                    moveTo(((MoveState)fsm.getState()).getCursor().pX, ((MoveState)fsm.getState()).getCursor().pY, ((MoveState)fsm.getState()).getCursor().getElevation(), dpf, accumulatedDistance);
                }   
                
                accumulatedMovTime += tpf;
                break;
            }
                
            case Active:
            {   
                if (isSelected && hasExtraIdle()) {
                    animState = AnimationState.Idle2;
                } else if (animState == AnimationState.Idle2) {
                    animState = AnimationState.Idle;
                }
                
                defMat.setFloat("Position", calculateSpritesheetPosition());
                updateOutline();
                break;
            }
                
            case Done:
            {
                isSelected = false;
                fsm.forceState(new FsmState().setEnum(UnitState.Active)); //this is temporary and just for testing purposes
                mapFSM.setNewStateIfAllowed(((MasterFsmState)mapFSM.getState()).updateState(MapFlowState.MapDefault));
                break;
            }
   
            case Idle:
            {
                defMat.setFloat("Position", calculateSpritesheetPosition());
                updateOutline();
                break;
            }
            
            case SelectingTarget:
            {
                defMat.setFloat("Position", calculateSpritesheetPosition());
                updateOutline();
                break;
            }
            
            case ReceivingEffect:
            {
                if (!effectQueue.isEmpty()) {
                    DeserializedParticleEffect particleEffect = effectQueue.getFirst();
                    EffekseerControl manualControl = particleEffect.getManualControl();
                    switch(effectProgress) {
                        case Progressing:
                            if (manualControl != null) {
                                manualControl.update(tpf);
                            }
                            
                            particleEffect.incrementCount();
                            
                            if (particleEffect.getCount() >= particleEffect.getFrames()) {
                                effectProgress = Progress.Finished;
                            }
                            break;
                        case Fresh:
                            Quaternion rotation = new Quaternion();
                            rotation.fromAngles((FastMath.PI / -3f), (FastMath.PI / -2f), 0);
                            particleEffect.modelRoot.setLocalRotation(rotation);
                            
                            if (manualControl != null) {
                                manualControl.setEnabled(true);
                            }
                            
                            particleEffect.setCount(0);
                            
                            node.attachChild(particleEffect.modelRoot);
                            effectProgress = Progress.Progressing;
                            break;
                        case Finished:
                            if (manualControl != null) {
                                manualControl.setEnabled(false);
                            }
                            
                            node.detachChild(particleEffect.modelRoot);
                            effectQueue.removeFirst();
                            effectProgress = Progress.Fresh;
                            break;
                    }
                } else {
                    fsm.setNewStateIfAllowed(lastState); //TODO: once MapFlowState is past whatever state it was on, it will set all units afflicted back to normal
                }
                break;
            }
                
            default:
                break;
        }
    }
    
    public void moveTo(int destinationX, int destinationY, int layer, float distanceperframe, float accumulatedDistance) { //distanceperframe must be a power of 2 and <= 16
        if (destinationX == posX && destinationY == posY) {
            animState = hasExtraIdle() ? AnimationState.Idle2 : AnimationState.Idle;
        } else {
            int i = (int)(accumulatedDistance / 16f);
            List<Tile> path = pathway.getPath();
            
            int lastX = i > 0 ? path.get(i - 1).getPosX() : pstartX;
            int lastY = i > 0 ? path.get(i - 1).getPosY() : pstartY;
            
            //these will always be 1, 0, or -1
            int deltaX = path.get(i).getPosX() - lastX;
            int deltaY = path.get(i).getPosY() - lastY;
            
            animState = AnimationState.directionalValueOf(deltaX, deltaY);
            node.move(deltaY * distanceperframe, 0, deltaX * distanceperframe);
        }
    }
    
    public void setAnimationState(AnimationState state) {
        animState = state;
    }
    
    boolean hasExtraIdle() {
        return spritesheetInfo.hasAnimation(AnimationState.Idle2);
    }
    
    private Spritesheet deserializeFromJSON() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Models\\Sprites\\map\\" + name + "\\info.json"));
            return gson.fromJson(reader, Spritesheet.class).setAnimations();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private int calculateSpritesheetPosition() { //value is row
        return (spritesheetInfo.startingPositions.get(animState) + (((int)(frameCount * 0.1)) % spritesheetInfo.getColumnCount(animState)));
    }
    
    private void updateOutline() {
        float outlineGradient = 0.075f * FastMath.cos(0.08f * frameCount) + 0.925f;
        outlineMat.setFloat("Position", calculateSpritesheetPosition());
        outlineMat.setColor("Color", unitStatus.getAssociatedColor().mult(outlineGradient));
    }
    
    public boolean canReach(int x, int y) {
        Map mp = MasterFsmState.getCurrentMap();
        
        boolean withinSpaces = false;
        for (int layer = 0; layer < mp.getLayerCount(); layer++) {
            if (Map.isWithinSpaces(getMobility(), posX, posY, mp.fullmap[layer][x][y].getPosX(), mp.fullmap[layer][x][y].getPosY())) {
                withinSpaces = true;
                layer = mp.getLayerCount();
            }
        }
        
        return withinSpaces && RangeDisplay.shouldDisplayTile(this, x, y, elevation, mp);
    }
    
    public boolean canReach(Coords point) {
        return canReach(point.getX(), point.getY());
    }
    
    public boolean hasStashAccess() {
        if (hasStashAccess || isLeader) { return true; }
        
        for (int i = 0; i < 2; i++) {
            Tile possibilityX = MasterFsmState.getCurrentMap().fullmap[elevation][posX + ((int)Math.cos(Math.PI * i))][posY];
            Tile possibilityY = MasterFsmState.getCurrentMap().fullmap[elevation][posX][posY + ((int)Math.cos(Math.PI * i))];
            if ((possibilityX.getOccupier() != null && unitStatus.alliedWith(possibilityX.getOccupier().unitStatus) && possibilityX.getOccupier().isLeader) || (possibilityY.getOccupier() != null && unitStatus.alliedWith(possibilityY.getOccupier().unitStatus) && possibilityY.getOccupier().isLeader)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void incrementOption(Purpose p) {
        switch (p) {
            case EtherAttack:
            case EtherSupport:
                commitsToEther++;
                break;
            case WeaponAttack:
                commitsToAttack++;
                break;
            case SkillAttack:
                commitsToSkill++;
                break;
            default:
                commitsToOther++;
                break;
        }
    }
    
    public void setUnitsToTalkTo(List<String> talks) {
        namesOfUnitsToTalkTo = talks;
    }
    
    public List<String> getTalkRecipients() {
        return namesOfUnitsToTalkTo;
    }
    
    public void setTradeDistance(int dist) {
        tradeDistance = dist;
    }
    
    public int getTradeDistance() {
        return tradeDistance;
    }
    
    public List<TangibleUnit> tradePartnersAt(Coords atPosition) {
        List<TangibleUnit> partners = new ArrayList<>();
        
        Tile[][] mapLayer = MasterFsmState.getCurrentMap().fullmap[elevation];
        for (Coords pos : VenturePeek.coordsForTilesOfRange(tradeDistance, atPosition, elevation)) {
            TangibleUnit occupier = mapLayer[pos.getX()][pos.getY()].getOccupier();
            if (occupier != null && unitStatus == occupier.unitStatus && (getInventory().getItems().size() > 0 || occupier.getInventory().getItems().size() > 0)) {
                partners.add(occupier);
            }
        }
        
        return partners;
    }
    
    public List<TangibleUnit> talkPartnersAt(Coords position) {
        List<TangibleUnit> partners = new ArrayList<>();
        
        Tile[][] mapLayer = MasterFsmState.getCurrentMap().fullmap[elevation];
        for (Coords pos : VenturePeek.coordsForTilesOfRange(tradeDistance, position, elevation)) {
            TangibleUnit occupier = mapLayer[pos.getX()][pos.getY()].getOccupier();
            if (occupier != null && namesOfUnitsToTalkTo != null && namesOfUnitsToTalkTo.contains(occupier.getName())) {
                partners.add(occupier);
            }
        }
        
        return partners;
    }
    
    public ActionInfo determineOptions(Conveyer conv) {
        return determineOptions(conv.getCursor().coords(), conv);
    }
    
    public ActionInfo determineOptions(Coords atPosition, Conveyer conv) {
        List<Weapon> usableWeapons = new ArrayList<>();
        List<Item> usableItems = new ArrayList<>();
        for (Item I : getInventory().getItems()) {
            if (I instanceof Weapon && ((Weapon)I).isAvailableAt(atPosition, elevation, unitStatus)) {
                usableWeapons.add((Weapon)I);
            }
            
            if (I.getItemEffect() != null && I.getItemEffect().canBeUsed(conv)) {
                usableItems.add(I);
            }
        }
        
        List<Formula> usableFormulas = new ArrayList<>();
        for (Formula F : getFormulas()) {
            if (F.isAvailableAt(atPosition, elevation, unitStatus, currentHP, currentTP)) {
                usableFormulas.add(F);
            }
        }
        
        List<Skill> usableSkills = new ArrayList<>();
        for (Skill S : getSkills()) {
            if (S.isAvailableAt(atPosition, elevation, unitStatus, getEquippedTool())) {
                usableSkills.add(S);
            }
        }
        
        List<Ability> usableAbilities = new ArrayList<>();
        for (Ability A : getAbilities()) {
            if (A.canBeUsed(conv)) {
                usableAbilities.add(A);
            }
        }
        
        List<Formation> usableFormations = new ArrayList<>();
        for (Formation Form : getFormations()) {
            if (Form.isAvailableAt(atPosition, elevation, unitStatus)) {
                usableFormations.add(Form);
            }
        }
        
        int highestCommitted = Math.max(Math.max(Math.max(commitsToAttack, commitsToEther), commitsToSkill), commitsToOther);
        Coords startingPosition;
        
        if (highestCommitted == 0) {
            startingPosition = ActionInfo.STARTING_POSITION;
        } else if (highestCommitted == commitsToAttack) {
            startingPosition = ActionInfo.ATTACK_POSITION;
        } else if (highestCommitted == commitsToEther) {
            startingPosition = ActionInfo.ETHER_POSITION;
        } else if (highestCommitted == commitsToSkill) {
            startingPosition = ActionInfo.SKILL_POSITION;
        } else { //commitsToOther
            startingPosition = ActionInfo.STARTING_POSITION;
        }
        
        return new ActionInfo(
                        usableWeapons, usableFormulas, usableItems, usableSkills, usableAbilities, usableFormations, 
                        tradePartnersAt(atPosition), talkPartnersAt(atPosition)).setStartingPosition(startingPosition);
    }
    
    public List<TangibleUnit> UnitsInRange(AllegianceRecognizer allegianceType, List<TangibleUnit> allUnits) {
        List<TangibleUnit> inRange = new ArrayList<>();
        
        for (TangibleUnit tu : allUnits) {
            if (allegianceType.passesTest(tu)) {
                if (venture().willReach(tu.coords()) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    inRange.add(tu);
                } else {
                    for (Integer range : getFullRange()) {
                        if (venture().addMobility(range).willReach(tu.coords()) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                            inRange.add(tu);
                        }
                    }
                }
            }
        }
        
        return inRange;
    }
    
    public boolean anyUnitInOffensiveRange(AllegianceRecognizer allegianceType, List<TangibleUnit> allUnits) { //cannot be allied
        for (TangibleUnit tu : allUnits) {
            if (!unitStatus.alliedWith(tu.unitStatus) && allegianceType.passesTest(tu)) {
                if (venture().willReach(tu.coords()) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    return true;
                } else {
                    for (Integer range : getFullOffensiveRange()) {
                        if (venture().addMobility(range).willReach(tu.coords()) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public boolean anyUnitInSupportRange(AllegianceRecognizer allegianceType, List<TangibleUnit> allUnits) { //must be allied
        for (TangibleUnit tu : allUnits) {
            if (unitStatus.alliedWith(tu.unitStatus) && allegianceType.passesTest(tu)) {
                if (venture().willReach(tu.coords()) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    return true;
                } else {
                    for (Integer range : getFullAssistRange()) {
                        if (venture().addMobility(range).willReach(tu.coords()) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public List<Coords> allowedCoordsFromTarget(Coords target, boolean offensive) { //this is for the Coords you can attack or assist the target from
        List<Coords> allowed = new ArrayList<>();
        List<Integer> rangesFrom = offensive ? getFullOffensiveRange() : getFullAssistRange();
        rangesFrom.forEach((range) -> {
            for (Coords point : VenturePeek.coordsForTilesOfRange(range, target, elevation)) {
                if (canReach(point) && !allowed.contains(point)) {
                    point.setRange(range);
                    allowed.add(point);
                }
            }
        });
        
        return allowed;
    }
    
    public List<Coords> movementTiles() {
        List<Coords> actual = new ArrayList<>();
        List<Coords> possible = VenturePeek.filledCoordsForTilesOfRange(getMobility(), coords(), elevation);
        possible.stream().filter((possibleCoord) -> (RangeDisplay.shouldDisplayTile(coords(), possibleCoord, elevation, getMobility()))).forEachOrdered((possibleCoord) -> {
            actual.add(possibleCoord);
        });
        
        return actual;
    }
    
    public Tile movementTileFurthestOnPathTowards(Coords destination) {
        Path path = new Path(coords(), destination, elevation, getMobility());
        List<Tile> tiles = path.getPath();
        for (int i = tiles.size() - 1; i >= 0; i--) {
            if (movementTiles().contains(tiles.get(i).coords())) {
                return tiles.get(i);
            }
        }
        
        return null;
    }
    
    public Coords closestMovementTileTo(Coords destination) { //doesn't actually filter in path
        Coords closest = null;
        for (Coords tile : movementTiles()) {
            if (closest == null || destination.difference(tile) < destination.difference(closest)) {
                closest = tile;
            }
        }
        
        return closest;
    }
    
    private static List<TangibleUnit> exclude;
    
    //concentration of same allegiance
    private int concentrationValue(int leniency) { //leniency just means range
        int val = 0;
        exclude.add(this);
        List<Tile> adjacents = VenturePeek.toTile(VenturePeek.coordsForTilesOfRange(1, coords(), elevation), elevation);
        for (Tile adjacent : adjacents) {
            if (adjacent.getOccupier() != null && unitStatus.alliedWith(adjacent.getOccupier().unitStatus) && !exclude.contains(adjacent.getOccupier())) {
                val += 1 + adjacent.getOccupier().concentrationValue(leniency);
            }
        }
        
        return val;
    }
    
    public int calculateConcentrationValue(int leniency) { //leniency just means range
        exclude = new ArrayList<>();
        return concentrationValue(leniency);
    }
    
    public Skill getToUseSkill() { return inUse; }
    public void setToUseSkill(Skill S) { inUse = S; }
    
    public int getMobility() { //TODO: CHANGE THIS SO IT RESTRICTS/ADDS TO MOVEMENT ON TILES
        return getMOBILITY();
    }
    
    @Override
    public List<Talent> getTalents() {
        List<Talent> all = super.getTalents();
        
        Tile onTile = MasterFsmState.getCurrentMap().fullmap[elevation][posX][posY];
        TileStatisticalData TSD = onTile.getTileData().getEffects();
        all.add(TSD.convertRawBonuses(posX, posY, elevation));
        
        return all;
    }
    
    @Override
    public int getTotalBonus(BaseStat stat) {
        return getTotalBonus(stat, Occasion.Indifferent, new Conveyer(this), null, true);
    }
    
    @Override
    public int getTotalBonus(BaseStat stat, BonusType filterBy, boolean include) {
        return getTotalBonus(stat, Occasion.Indifferent, new Conveyer(this), filterBy, include);
    }
    
    @Override
    public int getTotalBonus(BattleStat stat) {
        return getTotalBonus(stat, Occasion.Indifferent, new Conveyer(this), null, true);
    }
    
    @Override
    public int getTotalBonus(BattleStat stat, BonusType filterBy, boolean include) {
        return getTotalBonus(stat, Occasion.Indifferent, new Conveyer(this), filterBy, include);
    }
    
    //the "include" parameter means filter by inclusion (opposite is filter by exclusion)
    public int getTotalBonus(BaseStat stat, Occasion occasion, Conveyer conv, BonusType filterBy, boolean include) { //call this version of the method whenever possible
        int total = super.getTotalBonus(stat, filterBy, include);
        
        for (Bonus B : BonusesAvailable(conv, occasion)) {
            if (B.getStatType() == StatType.Base && B.getBaseStat() == stat && (filterBy == null || ((filterBy == B.getType()) == include))) {
                total += B.getValue();
            }
        }
        
        Tool equipped = getEquippedTool();
        if (equipped != null) {
            total += equipped.getTotalBonus(stat, occasion, filterBy, include);
        }
        
        return total;
    }
    
    //the "include" parameter means filter by inclusion (opposite is filter by exclusion)
    public int getTotalBonus(BattleStat stat, Occasion occasion, Conveyer conv, BonusType filterBy, boolean include) { //call this version of the method whenever possible
        int total = super.getTotalBonus(stat, filterBy, include);
        
        for (Bonus B : BonusesAvailable(conv, occasion)) {
            if (B.getStatType() == StatType.Battle && B.getBattleStat() == stat && (filterBy == null || ((filterBy == B.getType()) == include))) {
                total += B.getValue();
            }
        }
        
        Tool equipped = getEquippedTool();
        if (equipped != null) {
            total += equipped.getTotalBonus(stat, occasion, filterBy, include);
        }
        
        return total;
    }
    
    public List<Bonus> BonusesAvailable(Conveyer conv, Occasion occasion) {
        List<Bonus> buffs = new ArrayList<>();
        
        for (Talent T : getTalents()) {
            T.getFullBody().forEach((TC) -> {
                List<Bonus> talentBuffs = TC.getTalentEffect().retrieveBuffs(conv);
                if (talentBuffs != null && !talentBuffs.isEmpty() && TC.getTalentCondition().checkCondition(conv, occasion)) {
                    buffs.addAll(talentBuffs);
                }
            });
        }
        
        return buffs;
    }
    
    public void subtractHP(int amt) {
        currentHP -= amt;
        
        if (currentHP < 0) { currentHP = 0; }
    }
    
    public void subtractTP(int amt) {
        currentTP -= amt;
        
        if (currentTP < 0) { currentTP = 0; }
    }
    
    public void subtractDurability(double amt) {
        for (Item item : getInventory().getItems()) {
            if (item instanceof Weapon) {
                ((Weapon)item).used(amt);
                return;
            }
        }
    }
    
    public AI getAI() { return auto; }
    
    public void setAI(AI mind) {
        auto = mind;
    }
    
    public void setAI(List<ConditionalBehavior> brain) { //create your own with new parameters
        auto = new AI(this, brain);
    }
    
    public void setAI(LinkedHashMap<Condition, Behavior> processes) { //create your own by using presets
        auto = new AI(this, processes);
    }
    
    public RequestDealer getRequestDealer() {
        return requestDealer;
    }
    
    public boolean is(TangibleUnit other) {
        return id == other.id || equals(other);
    }
    
    public int getID() { return id; }
    
    public void onOccasion(Conveyer conv, Occasion occasion) { //TODO: add more than just buff and debuff for effects
        AssetManager assetManager = conv.getAssetManager();
        
        BattleRole role = conv.battleRoleFor(this);
        
        List<Bonus> availableBonuses = BonusesAvailable(conv, occasion);
        if (!availableBonuses.isEmpty()) {
            List<Bonus> buffQueue = new ArrayList<>(), debuffQueue = new ArrayList<>();
            for (Bonus bonus : availableBonuses) {
                if (bonus.getType() != BonusType.Raw) { //only add the non-raw ones, because the raw ones are equivalent to "bonuses during combat"
                    if (bonus.getValue() >= 0) {
                        buffQueue.add(bonus);
                    } else {
                        debuffQueue.add(bonus);
                    }
                
                    addBonus(bonus); //adds the stat modifier regardless
                }
            }
            
            if (!buffQueue.isEmpty()) {
                effectQueue.add(DeserializedParticleEffect.loadEffect(MapFlow.EFFECT_BUFF, assetManager));
            }
            
            if (!debuffQueue.isEmpty()) {
                effectQueue.add(DeserializedParticleEffect.loadEffect(MapFlow.EFFECT_DEBUFF, assetManager));
            }
            
            fsm.setNewStateIfAllowed(UnitState.ReceivingEffect);
        }
    }
    
}