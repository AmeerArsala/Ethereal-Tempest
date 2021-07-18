/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.character;

import battle.data.participant.BattleRole;
import battle.participant.visual.BattleSprite;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import maps.layout.tile.Tile;
import etherealtempest.info.Conveyor;
import fundamental.unit.Unit;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.util.ArrayList;
import etherealtempest.fsm.FSM;
import etherealtempest.fsm.FSM.UnitState;
import etherealtempest.fsm.FsmState;
import etherealtempest.fsm.MasterFsmState;
import etherealtempest.ai.AI;
import etherealtempest.ai.AI.Behavior;
import etherealtempest.ai.AI.Condition;
import etherealtempest.ai.ConditionalBehavior;
import fundamental.unit.UnitAllegiance;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.Bonus.StatType;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.unit.CharacterUnitInfo;
import fundamental.unit.PositionedUnit;
import general.tools.GameTimer;
import maps.layout.occupant.character.Spritesheet.AnimationState;
import general.visual.DeserializedParticleEffect;
import java.util.LinkedHashMap;
import java.util.List;
import maps.flow.MapFlow;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import etherealtempest.GameProtocols;
import fundamental.unit.PositionedUnitParams;
import maps.layout.occupant.VenturePeek;
import maps.layout.tile.TileFoundation;

/**
 *
 * @author night
 */
public class TangibleUnit extends PositionedUnit {
    private final UnitVisuals visuals;
    
    private boolean isSelected = false;
    private AI auto;
    
    private final FSM<UnitState> fsm = new FSM<UnitState>() {
        @Override
        public boolean stateAllowed(FsmState<UnitState> st) {
            FsmState<UnitState> currentState = getState();
            return currentState == null || (currentState.getEnum() != UnitState.Dead && currentState.getEnum() != UnitState.Done);
        }
        
        @Override
        public void onStateSet(FsmState<UnitState> currentState, FsmState<UnitState> previousState) {
            switch (currentState.getEnum()) {
                case Active:
                    //TODO: CHANGE LATER FOR IT TO BE ON LESS THAN FULL HP OR TP (remove the commented conditions in the if statements)
                    if (/* currentHP < getMaxHP() && */ !visuals.node.hasChild(visuals.getHPNode())) {
                        visuals.node.attachChild(visuals.getHPNode());
                    }
                    
                    if (/* currentTP < getMaxTP() && */ !visuals.node.hasChild(visuals.getTPNode())) {
                        visuals.node.attachChild(visuals.getTPNode());
                    }
                    break;
                case Done: //onActionCommitted
                    tempBonuses.updateOnActionCommitted(); //removes bonuses that are through next action
                        
                    isSelected = false;
                    forceState(UnitState.Active); //this is temporary and just for testing purposes
                    break;
                case Dying:
                    visuals.detachBars();
                    die();
                    break;
                case Dead:
                    getCurrentTile().resetOccupier();
                    visuals.node.removeFromParent();
                    break;
                default:
                    break;
            }
        }
    };
    
    public TangibleUnit(Unit X, CharacterUnitInfo info, PositionedUnitParams startingParams, UnitAllegiance startingAllegiance, AssetManager assetManager) {
        super(X, info, startingParams, startingAllegiance);
        
        //create visuals
        visuals = new UnitVisuals(name, X.getJobClass().getName(), allegiance.getAssociatedColor(), assetManager);
        
        //set state
        fsm.setNewStateIfAllowed(new FsmState<>(UnitState.Active));
        
        //update sprite
        visuals.updateSprite();
    }
    
    public TangibleUnit(Unit X, CharacterUnitInfo info, PositionedUnitParams startingParams, UnitAllegiance startingAllegiance, MapCoords startingPos, AssetManager assetManager) {
        this(X, info, startingParams, startingAllegiance, assetManager);
        pos.set(startingPos);
    }
    
    public FSM<UnitState> getFSM() { 
        return fsm; 
    }
    
    public UnitVisuals getVisuals() { 
        return visuals; 
    }
    
    public Node getNode() { 
        return visuals.node; 
    }
    
    public AI getAI() { 
        return auto; 
    }
    
    public boolean isSelected() { 
        return isSelected; 
    }
    
    public void setAI(AI mind) {
        auto = mind;
    }
    
    public void setAI(List<ConditionalBehavior> brain) { //create your own with new parameters
        auto = new AI(this, brain);
    }
    
    public void setAI(LinkedHashMap<Condition, Behavior> processes) { //create your own by using presets
        auto = new AI(this, processes);
    }
    
    public void select() {
        isSelected = true;
        visuals.setIdealIdle();
    }
    
    public void deselect() {
        isSelected = false;
        visuals.setAnimationState(AnimationState.Idle);
    }
    
    public void remapPosition(MapCoords coords, MapLevel map) {
        if (coords.equals(pos)) {
            return;
        }
        
        map.getTileAt(pos).resetOccupier();
        
        pos.set(coords);
        
        Tile destination = map.getTileAt(coords);
        
        visuals.node.setLocalTranslation(destination.getGeometry().getWorldTranslation().add(-1f, 1, -5.5f));
        destination.setOccupier(this);
    }
    
    public void remapPosition(MapCoords coords) { 
        remapPosition(coords, MasterFsmState.getCurrentMap()); 
    }
    
    @Override
    public void setAllegiance(UnitAllegiance loyalty) {
        allegiance = loyalty;
        visuals.setBaseOutlineColor(allegiance.getAssociatedColor());
    }
    
    public void update(float tpf, Camera cam, boolean shouldUpdate) {
        if (shouldUpdate) {
            updateAI(tpf);
        }
        
        visuals.updateHP(getCurrentToMaxHPratio());
        visuals.updateTP(getCurrentToMaxTPratio());
        visuals.update(tpf, cam);
    }
    
    public void updateAI(float tpf) {
        switch (fsm.getEnumState()) {
            case Active:
            case Done:
            case SelectingTarget:
                visuals.updateSprite();
                break;
            case Idle:
                //visuals.updateSprite();
                break;
            default: 
                break;
        }
    }
    
    public void moveWith(Movement moveSeq) {
        previousPos.set(pos);
        visuals.addToQueue((tpf) -> {
            visuals.updateSprite();
            
            if (moveSeq.isFinished()) {
                //set to idle if there is a special idle
                if (visuals.hasExtraIdle()) {
                    visuals.setAnimationState(AnimationState.Idle2);
                }
                
                //set final position
                remapPosition(moveSeq.getFinalPos());
                
                //open post action menu
                GameProtocols.OpenPostActionMenu();
                
                fsm.setNewStateIfAllowed(UnitState.Active);
                return true;
            }
            
            moveSeq.update(tpf);
            return false;
        });
        
        fsm.setNewStateIfAllowed(UnitState.Idle);
    }
    
    public void moveWith(MapCoords[] tilePath) {
        moveWith(visuals.birthMovement(tilePath, pos.deepDuplicate()));
    }
    
    public void moveTo(MapCoords destination) {
        moveWith(visuals.createMovement(pos, destination, getMOBILITY()));
    }
    
    public void die() {
        GameTimer counter = new GameTimer();
        visuals.addToQueue((tpf) -> {
            //update color
            ColorRGBA color = BattleSprite.DIE_FUNCTION.rgba(counter.getTime());
            visuals.getSpriteBody().getMaterial().setColor("Color", color);
            visuals.getOutlineBody().getMaterial().setColor("Color", color);
            
            //update time
            counter.update(tpf);
            
            if (counter.getTime() >= BattleSprite.DIE_FUNCTION_LENGTH) {
                fsm.setNewStateIfAllowed(UnitState.Dead);
                return true;
            }
            
            return false;
        });
    }
    
    private static final List<TangibleUnit> exclude = new ArrayList<>();
    
    //concentration of same allegiance
    private int concentrationValue(int leniency) { //leniency just means range
        int val = 0;
        exclude.add(this);
        List<Tile> adjacents = VenturePeek.toTile(VenturePeek.coordsForTilesOfRange(1, pos));
        for (Tile adjacent : adjacents) {
            if (adjacent.getOccupier() != null && allegiance.alliedWith(adjacent.getOccupier().allegiance) && !exclude.contains(adjacent.getOccupier())) {
                val += 1 + adjacent.getOccupier().concentrationValue(leniency);
            }
        }
        
        return val;
    }
    
    public int calculateConcentrationValue(int leniency) { //leniency just means range
        exclude.clear();
        return concentrationValue(leniency);
    }
    
    @Override
    public int getTotalBaseStatBonus(BaseStat stat) {
        return getTotalBaseStatBonus(stat, Occasion.Indifferent, new Conveyor(this), null, true);
    }
    
    @Override
    public int getTotalBattleStatBonus(BattleStat stat) {
        return getTotalBattleStatBonus(stat, Occasion.Indifferent, new Conveyor(this), null, true);
    }
    
    //classBonuses + tempBonuses + talentBonuses
    //the "include" parameter means filter by inclusion (opposite is filter by exclusion)
    public int getTotalBaseStatBonus(BaseStat stat, Occasion occasion, Conveyor context, BonusType filterBy, boolean include) {
        int total = super.getTotalBaseStatBonus(stat);
        context.setUnit(this);
        
        //bonuses from talents
        for (Bonus B : bonusesAvailable(context, occasion)) {
            if (B.getStatType() == StatType.Base && B.getBaseStat() == stat && (filterBy == null || ((filterBy == B.getType()) == include))) {
                total += B.getValue();
            }
        }
        
        return total;
    }
    
    //classBonuses + tempBonuses + talentBonuses
    //the "include" parameter means filter by inclusion (opposite is filter by exclusion)
    public int getTotalBattleStatBonus(BattleStat stat, Occasion occasion, Conveyor context, BonusType filterBy, boolean include) { //call this version of the method whenever possible
        int total = super.getTotalBattleStatBonus(stat);
        context.setUnit(this);
        
        //bonuses from talents
        for (Bonus B : bonusesAvailable(context, occasion)) {
            if (B.getStatType() == StatType.Battle && B.getBattleStat() == stat && (filterBy == null || ((filterBy == B.getType()) == include))) { //call this version of the method whenever possible
                total += B.getValue();
            }
        }
        
        return total;
    }
    
    //tempBonuses + talentBonuses
    //the "include" parameter means filter by inclusion (opposite is filter by exclusion)
    public int getTotalBonus(BaseStat stat, Occasion occasion, Conveyor context, BonusType filterBy, boolean include) { //call this version of the method whenever possible
        return getTotalBaseStatBonus(stat, occasion, context, filterBy, include) - jobclass.getBaseStatBonuses().get(stat);
    }
    
    //tempBonuses + talentBonuses
    //the "include" parameter means filter by inclusion (opposite is filter by exclusion)
    public int getTotalBonus(BattleStat stat, Occasion occasion, Conveyor context, BonusType filterBy, boolean include) { //call this version of the method whenever possible
        return getTotalBattleStatBonus(stat, occasion, context, filterBy, include) - jobclass.getBattleStatBonuses().get(stat);
    }
    
    public void onOccasion(Conveyor conv, Occasion occasion) { //TODO: add more than just buff and debuff for effects
        if (!fsm.getEnumState().allowsOnOccasion()) {
            return;
        }
        
        AssetManager assetManager = conv.getAssetManager();
        
        BattleRole role = conv.battleRoleFor(this);
        
        //remove bonuses from the previous turn
        tempBonuses.update(conv.getCurrentTurn());
        
        List<Bonus> availableBonuses = bonusesAvailable(conv, occasion);
        if (!availableBonuses.isEmpty()) {
            List<Bonus> buffQueue = new ArrayList<>(), debuffQueue = new ArrayList<>();
            for (Bonus bonus : availableBonuses) {
                if (bonus.getType() != BonusType.Raw) { //only add the non-raw ones, because the raw ones are equivalent to "bonuses during combat"
                    if (bonus.getValue() >= 0) {
                        buffQueue.add(bonus);
                    } else {
                        debuffQueue.add(bonus);
                    }
                    
                    tempBonuses.addBonus(bonus.setTurnApplied(conv.getCurrentTurn())); //adds the stat modifier regardless
                    System.out.println("Bonus added: " + bonus.StatBonusAsString());
                }
            }
            
            if (!buffQueue.isEmpty()) {
                visuals.addToEffectQueue(DeserializedParticleEffect.loadEffect(MapFlow.EFFECT_BUFF, assetManager));
            }
            
            if (!debuffQueue.isEmpty()) {
                visuals.addToEffectQueue(DeserializedParticleEffect.loadEffect(MapFlow.EFFECT_DEBUFF, assetManager));
            }
            
            //queue receiving of effect
            fsm.setNewStateIfAllowed(UnitState.Idle);
            visuals.addToQueue((tpf) -> {
                if (visuals.getEffectQueueSize() > 0) {
                    visuals.updateEffects(tpf);
                    visuals.updateSprite();
                    return false;
                }
                
                //TODO: once MapFlowState is past whatever state it was on, it will set all units afflicted back to normal
                fsm.setNewStateIfAllowed(fsm.getLastEnumState() != UnitState.Idle ? fsm.getLastEnumState() : UnitState.Active); 
                return true;
            });
        }
    }
}