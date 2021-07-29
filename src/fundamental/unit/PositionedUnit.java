/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

import fundamental.unit.aspect.UnitAllegiance;
import etherealtempest.fsm.FSM.UnitState;
import etherealtempest.fsm.MasterFsmState;
import etherealtempest.ai.AllegianceRecognizer;
import etherealtempest.info.ActionInfo;
import etherealtempest.info.Conveyor;
import fundamental.ability.Ability;
import fundamental.formation.FormationTechnique;
import fundamental.formula.Formula;
import fundamental.item.Item;
import fundamental.item.weapon.Weapon;
import fundamental.skill.Skill;
import fundamental.stats.alteration.Bonus;
import fundamental.stats.StatBundle;
import fundamental.talent.Talent;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition.Occasion;
import java.util.ArrayList;
import java.util.List;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.OnTile;
import maps.layout.occupant.VenturePeek;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.move.Path;
import maps.layout.tile.move.RangeDisplay;
import maps.layout.tile.Tile;
import maps.layout.tile.TileStatisticalData;

/**
 *
 * @author night
 * 
 * This class is for methods relative to the unit's position
 */
public class PositionedUnit extends CharacterizedUnit implements OnTile {
    public static final int DEFAULT_TRADE_DISTANCE = 1; //adjacent
    
    public static class Params {
        public boolean hasStashAccess;
        public boolean isLeader;
        public boolean isBoss;
    
        public Params() {
            hasStashAccess = false;
            isLeader = false;
            isBoss = false;
        }
        
        public Params(boolean hasStashAccess, boolean isLeader, boolean isBoss) {
            this.hasStashAccess = hasStashAccess;
            this.isLeader = isLeader;
            this.isBoss = isBoss;
        }
        
        public Params hasStashAccess(boolean hasStashAccess) {
            this.hasStashAccess = hasStashAccess;
            return this;
        }
        
        public Params isLeader(boolean isLeader) {
            this.isLeader = isLeader;
            return this;
        }
        
        public Params isBoss(boolean isBoss) {
            this.isBoss = isBoss;
            return this;
        }
    }
    
    protected final Params params;
    
    protected final MapCoords pos = new MapCoords();
    protected final MapCoords previousPos = new MapCoords();
    
    protected int tradeDistance = DEFAULT_TRADE_DISTANCE; //required distance to initiate a trade
    
    private Skill inUse = null;
    private final List<String> namesOfUnitsToTalkTo = new ArrayList<>();
    
    public PositionedUnit(Unit X, CharacterizedUnit.Info info, Params startingParams, UnitAllegiance startingAllegiance) {
        super(X, info);
        params = startingParams;
        allegiance = startingAllegiance;
    }

    public Params getParams() { 
        return params; 
    }
    
    public MapCoords getPos() { 
        return pos; 
    }
    
    public MapCoords getPreviousPos() { 
        return previousPos;
    }
    
    public int getTradeDistance() {
        return tradeDistance;
    }
    
    public Skill getToUseSkill() { 
        return inUse; 
    }
    
    public List<String> getTalkRecipients() {
        return namesOfUnitsToTalkTo;
    }
    
    public void setTradeDistance(int dist) {
        tradeDistance = dist;
    }
    
    public void setToUseSkill(Skill S) { 
        inUse = S; 
    }
    
    public VenturePeek venture() {
        return new VenturePeek(pos, getMOBILITY());
    }
    
    @Override
    public Tile getCurrentTile() {
        return MasterFsmState.getCurrentMap().getTileAt(pos);
    }
    
    @Override
    public Tile getCurrentTile(MapLevel map) {
        return map.getTileAt(pos);
    }
    
    @Override
    public int getMOBILITY() { //TODO: CHANGE THIS SO IT RESTRICTS/ADDS TO MOVEMENT ON TILES
        return super.getMOBILITY();
    }
    
    public ActionInfo determineOptions(Conveyor conv) {
        return determineOptions(conv.getCursor().getPos(), conv);
    }
    
    public ActionInfo determineOptions(MapCoords atPosition, Conveyor conv) {
        List<Item> usableItems = inventory.getItems();
        List<Weapon> usableWeapons = inventory.getUsableWeapons(atPosition, allegiance);
        List<Formula> usableFormulas = formulaManager.getAvailableFormulas(atPosition, allegiance, currentHP, currentTP);
        
        List<Skill> usableSkills = new ArrayList<>();
        for (Skill S : getSkills()) {
            if (S.isAvailableAt(atPosition, allegiance, getEquippedTool())) {
                usableSkills.add(S);
            }
        }
        
        List<Ability> usableAbilities = new ArrayList<>();
        for (Ability A : getAbilities()) {
            if (A.canBeUsed(conv)) {
                usableAbilities.add(A);
            }
        }
        
        List<FormationTechnique> usableFormationTechniques = new ArrayList<>();
        if (equippedFormation() != null) {
            usableFormationTechniques.addAll(equippedFormation().techniquesAvailableAt(pos, allegiance));
        }
        
        return new ActionInfo(
            usableWeapons, usableFormulas, usableItems, usableSkills, usableAbilities, usableFormationTechniques, 
            tradePartnersAt(atPosition), talkPartnersAt(atPosition));
    }
    
    public boolean canReach(MapCoords point) {
        return venture().willReach(point);
    }
    
    public boolean hasStashAccess() {
        if (params.hasStashAccess || params.isLeader) { return true; }
        List<Tile> adjacentTiles = VenturePeek.toTile(VenturePeek.coordsForTilesOfRange(1, pos));
        
        for (Tile adjacent : adjacentTiles) {
            TangibleUnit adjacentUnit = adjacent.getOccupier();
            if (adjacentUnit != null && allegiance.alliedWith(adjacentUnit.allegiance) && adjacentUnit.params.isLeader) {
                return true;
            }
        }
        
        
        return false;
    }
    
    public List<TangibleUnit> tradePartnersAt(MapCoords atPosition) {
        List<TangibleUnit> partners = new ArrayList<>();

        for (MapCoords position : VenturePeek.coordsForTilesOfRange(tradeDistance, atPosition)) {
            Tile tile = MasterFsmState.getCurrentMap().getTileAt(position);
            if (tile.isOccupied && allegiance == tile.getOccupier().allegiance && (inventory.getItems().size() > 0 || tile.getOccupier().getInventory().getItems().size() > 0)) {
                partners.add(tile.getOccupier());
            }
        }
        
        return partners;
    }
    
    public List<TangibleUnit> talkPartnersAt(MapCoords atPosition) {
        int TALK_DISTANCE = 1;
        
        List<TangibleUnit> partners = new ArrayList<>();

        for (MapCoords tilePos : VenturePeek.coordsForTilesOfRange(TALK_DISTANCE, atPosition)) {
            Tile tile = MasterFsmState.getCurrentMap().getTileAt(tilePos);
            if (tile.isOccupied && namesOfUnitsToTalkTo != null && namesOfUnitsToTalkTo.contains(tile.getOccupier().getName())) {
                partners.add(tile.getOccupier());
            }
        }
        
        return partners;
    }
    
    //this is for the Coords you can attack or assist the target from; for example, you would be able to attack someone with a 1 ranged weapon from a single space away
    public List<MapCoords> allowedCoordsFromTarget(MapCoords target, boolean offensive) {
        List<MapCoords> allowed = new ArrayList<>();
        List<Integer> rangesFrom = offensive ? getFullOffensiveRange() : getFullAssistRange();
        rangesFrom.forEach((range) -> {
            for (MapCoords point : VenturePeek.coordsForTilesOfRange(range, target)) {
                if (canReach(point) && !allowed.contains(point)) {
                    point.getCoords().setRange(range);
                    allowed.add(point);
                }
            }
        });
        
        return allowed;
    }
    
    public List<MapCoords> movementTiles() {
        List<MapCoords> actual = new ArrayList<>();
        List<MapCoords> possible = VenturePeek.filledCoordsForTilesOfRange(getMOBILITY(), pos);
        possible.stream().filter((possibleCoord) -> (RangeDisplay.shouldDisplayTile(pos, possibleCoord, getMOBILITY()))).forEachOrdered((possibleCoord) -> {
            actual.add(possibleCoord);
        });
        
        return actual;
    }
    
    public MapCoords closestMovementTileTo(MapCoords destination) { //doesn't actually filter in path
        List<MapCoords> moveTiles = movementTiles();
        MapCoords closest = moveTiles.get(0);
        for (MapCoords tile : moveTiles) {
            if (tile.getLayer() != destination.getLayer()) {
                continue;
            }
            
            if (destination.spacesFrom(tile) < destination.spacesFrom(closest)) {
                closest = tile;
            }
        }
        
        return closest;
    }
    
    public Tile movementTileFurthestOnPathTowards(MapCoords destination) {
        Path path = new Path(pos, destination, getMOBILITY());
        List<Tile> tiles = path.getPath();
        List<MapCoords> moveTiles = movementTiles();
        for (int i = tiles.size() - 1; i >= 0; i--) {
            if (moveTiles.contains(tiles.get(i).getPos())) {
                return tiles.get(i);
            }
        }
        
        return null;
    }
    
    public List<TangibleUnit> UnitsInRange(AllegianceRecognizer allegianceType, List<TangibleUnit> allUnits) {
        List<TangibleUnit> inRange = new ArrayList<>();
        
        for (TangibleUnit tu : allUnits) {
            if (allegianceType.passesTest(tu)) {
                if (venture().willReach(tu.getPos()) && tu.getFSM().getEnumState() != UnitState.Dead) {
                    inRange.add(tu);
                } else {
                    for (Integer range : getFullRange()) {
                        if (venture().addMobility(range).willReach(tu.getPos()) && tu.getFSM().getEnumState() != UnitState.Dead) {
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
            if (!allegiance.alliedWith(tu.allegiance) && allegianceType.passesTest(tu)) {
                if (venture().willReach(tu.pos) && tu.getFSM().getEnumState() != UnitState.Dead) {
                    return true;
                } else {
                    for (Integer range : getFullOffensiveRange()) {
                        if (venture().addMobility(range).willReach(tu.pos) && tu.getFSM().getEnumState() != UnitState.Dead) {
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
            if (allegiance.alliedWith(tu.allegiance) && allegianceType.passesTest(tu)) {
                if (venture().willReach(tu.pos) && tu.getFSM().getEnumState() != UnitState.Dead) {
                    return true;
                } else {
                    for (Integer range : getFullAssistRange()) {
                        if (venture().addMobility(range).willReach(tu.pos) && tu.getFSM().getEnumState() != UnitState.Dead) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public List<Talent> getTalents() {
        List<Talent> all = super.getTalents();
        
        if (inUse != null) {
            all.add(inUse.getEffect().getTalent());
        }
        
        TileStatisticalData TSD = getCurrentTile().getTileData().getEffects();
        all.add(TSD.convertRawBonuses(pos));
        
        return all;
    }
    
    public List<Bonus> bonusesAvailable(Conveyor conv, Occasion occasion) {
        List<Bonus> buffs = new ArrayList<>();
        List<Talent> availableTalents = getTalents();

        for (int i = 0; i < availableTalents.size(); ++i) {
            for (TalentConcept concept : availableTalents.get(i).getFullBody()) {
                List<Bonus> talentBuffs = concept.getTalentEffect().retrieveBuffs(conv);
                if (talentBuffs != null && !talentBuffs.isEmpty() && concept.getTalentCondition().checkCondition(conv, occasion)) {
                    buffs.addAll(talentBuffs);
                }
            }
        }
        
        return buffs;
    }
    
    public List<StatBundle> rawBuffsAvailable(Conveyor context, Occasion occasion) {
        List<StatBundle> buffs = new ArrayList<>();
        List<Talent> availableTalents = getTalents();
        
        for (int i = 0; i < availableTalents.size(); ++i) {
            for (TalentConcept concept : availableTalents.get(i).getFullBody()) {
                List<StatBundle> rawBuffs = concept.getTalentEffect().getBuffsRaw(context);
                if (rawBuffs != null && !rawBuffs.isEmpty() && concept.getTalentCondition().checkCondition(context, occasion)) {
                    buffs.addAll(rawBuffs);
                }
            }
        }
        
        return buffs;
    }
}
