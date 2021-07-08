/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.ai;

import etherealtempest.info.Conveyor;
import battle.data.forecast.PrebattleForecast;
import fundamental.ability.Ability;
import battle.data.forecast.SupportForecast;
import etherealtempest.fsm.FSM.UnitState;
import fundamental.formation.Formation;
import fundamental.formula.Formula;
import fundamental.item.Item;
import fundamental.item.weapon.Weapon;
import fundamental.skill.Skill;
import general.utils.helpers.GameUtils;
import etherealtempest.fsm.MasterFsmState;
import fundamental.unit.UnitAllegiance;
import etherealtempest.info.ActionInfo;
import etherealtempest.info.ActionInfo.PostMoveAction;
import fundamental.Attribute;
import fundamental.formation.FormationTechnique;
import fundamental.tool.Tool.ToolType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import maps.data.ObjectiveData;
import maps.layout.occupant.control.CursorFSM.Purpose;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.Tile;
import maps.layout.occupant.VenturePeek;
import maps.layout.tile.TileOptionData.TileType;

/**
 *
 * @author night
 */
public class AI {
    public enum Command { //these would be issued by commanders and would override the default conditional behaviors
        Charge(0),
        Regroup(1),
        Run(2),
        HoldPosition(3);
        
        private final int value;
        private static HashMap map = new HashMap<>();
        private Command(int val) { value = val; }
        
        static {
            for (Command cmd : Command.values()) {
                map.put(cmd.value, cmd);
            }
        }
        
        public int getValue() {
            return value;
        }
        
        public static Command valueOf(int val) {
            return (Command)map.get(val);
        }
        
        public Command equivalentInstance() {
            return (Command)map.get(value);
        }
    }
    
    public enum Condition {
        Always,
        EnemyUnitEntersRangeOnce,
        EnemyUnitEntersRange,
        AlliedUnitEntersRange,
    }
    
    public enum Behavior {
        DefaultMindset, //a default mindset for enemies
        FullThrottle, //charge all in
        Hold, //become a wall
        Defiant, //aloof, doesn't care
        Drugged; //chaos
    }
    
    public static LinkedHashMap<Condition, Behavior> UntilEnemiesInRangeOnce(Behavior onEnemiesInRangeOnce, Behavior otherwise) {
        LinkedHashMap<Condition, Behavior> priorities = new LinkedHashMap<>();
        
        priorities.put(Condition.EnemyUnitEntersRangeOnce, onEnemiesInRangeOnce);
        priorities.put(Condition.Always, otherwise);
        
        return priorities;
    }
    
    private final TangibleUnit reference;
    
    private boolean anEnemyHasOnceEnteredOffensiveRange = false;
    private UnitAllegiance allegiance;
    
    //the meat
    private Command givenCommand = null;
    private LinkedHashMap<Condition, Behavior> behaviorMap = null;
    private List<ConditionalBehavior> mindset = null; //lower index = higher priority; these are assessed in an orderly fashion; based on what matters most to the AI
    
    public static final AllegianceRecognizer ANY = new AllegianceRecognizer() {
        @Override
        public boolean allows(TangibleUnit tu) {
            return true;
        }
    };
    public final AllegianceRecognizer SAME;
    public final AllegianceRecognizer ALLIED;
    public final AllegianceRecognizer ENEMY;
    
    private AI(TangibleUnit ref) {
        reference = ref;
        allegiance = reference.getAllegiance();
        
        SAME = new AllegianceRecognizer() {
            @Override
            public boolean allows(TangibleUnit tu) {
                return !reference.equals(tu) && allegiance == tu.getAllegiance();
            }
        };
        
        ALLIED = new AllegianceRecognizer() {
            @Override
            public boolean allows(TangibleUnit tu) {
                return reference.isAlliedWith(tu);
            }
        };
        
        ENEMY = new AllegianceRecognizer() {
            @Override
            public boolean allows(TangibleUnit tu) {
                return !reference.isAlliedWith(tu);
            }
        };
    }
    
    public AI(TangibleUnit ref, List<ConditionalBehavior> mind) {
        this(ref);
        mindset = mind;
    }
    
    public AI(TangibleUnit ref, LinkedHashMap<Condition, Behavior> processes) {
        this(ref);
        behaviorMap = processes;
    }
    
    public List<ConditionalBehavior> getMindset() { return mindset; }
    
    public UnitAllegiance getAllegiance() { return allegiance; }
    public Command getCommand() { return givenCommand; }
    
    public void issueCommand(Command cmd) {
        givenCommand = cmd;
    }
    
    public void cancelCommand() { givenCommand = null; }
    
    public void setAllegiance(UnitAllegiance us) {
        allegiance = us;
    }
    
    public Option calculateNextCourseOfAction(Conveyor data) {
        if (givenCommand != null) { //command overrides all
            Command cmd = givenCommand.equivalentInstance();
            givenCommand = null;
            return optionMap(cmd, data);
        }
        
        if (behaviorMap != null) {
            updateMindset(data);
        }
        
        for (ConditionalBehavior cbt : mindset) {
            if (cbt.canExecute(data)) {
                return cbt.retrieveAction(data);
            }
        }
        
        return null;
    }
    
    void updateMindset(Conveyor data) {
        mindset = new ArrayList<>();
        behaviorMap.keySet().forEach((cond) -> {
            mindset.add(mindsetMap(cond, behaviorMap.get(cond), data));
        });
    }
    
    private ConditionalBehavior mindsetMap(Condition condition, Behavior behavior, Conveyor data) {
        boolean cause;
        Option effect;
        
        //cause
        switch (condition) {
            case Always:
                cause = true;
                break;
            case EnemyUnitEntersRangeOnce:
                if (anEnemyHasOnceEnteredOffensiveRange) {
                    cause = true;
                    break;
                }
            case EnemyUnitEntersRange:
                cause = reference.anyUnitInOffensiveRange(ANY, data.getAllUnits());
                break;
            case AlliedUnitEntersRange:
                cause = reference.anyUnitInSupportRange(ANY, data.getAllUnits());
                break;
            default:
                cause = false;
                break;
        }
        
        //effect
        switch (behavior) {
            case DefaultMindset:
                if (reference.getAllegiance() == UnitAllegiance.Ally) {
                    effect = ObjectivePriorityAsAlly(data);
                } else { //enemy
                    effect = ObjectivePriorityAsEnemy(data);
                }
                break;
            case FullThrottle:
                effect = ChargeIn(data);
                break;
            case Hold:
                effect = mostFavorableMiscOption(reference.getPos(), data);
                break;
            case Drugged:
                int random = (int)(Math.random() * 2);
                if (random == 0) { //random between all commands
                    int index = (int)(4 * Math.random());
                    effect = optionMap(Command.valueOf(index), data);
                } else { // random == 1; random position in move squares
                    List<MapCoords> squares = reference.movementTiles();
                    MapCoords tile = squares.get((int)(Math.random() * squares.size()));
                    Tile rand = MasterFsmState.getCurrentMap().getTileAt(tile);
                    if (rand.getOccupier() != null) {
                        effect = mostFavorableOffensiveOption(rand.getOccupier(), data);
                    } else {
                        effect = mostFavorableMiscOption(tile, data);
                    }
                }
                break;
            default: //works for Defiant as well
                effect = DefaultBehavior(data);
                break;
        }
        
        return new ConditionalBehavior(cause, effect);
    }
    
    Option optionMap(Command command, Conveyor data) {
        switch(command) {
            case Charge:
                return ChargeIn(data);
            case Regroup:
                return GroupUp(data);
            case Run:
                return RunAway(data);
            case HoldPosition:
                return mostFavorableMiscOption(reference.getPos(), data);
        }
        
        return null;
    }
    
    public List<ConditionalBehavior> DefaultEnemyMindset() {
        return Arrays.asList(
                new ConditionalBehavior( //top priority
                    new AICondition() {
                        private boolean hasPassedOnce = false;
                        
                        @Override
                        public boolean condition(Conveyor data) {
                            if (hasPassedOnce) {
                                return true;
                            }
                            
                            if (data.getUnit().anyUnitInOffensiveRange(ANY, data.getAllUnits())) {
                                hasPassedOnce = true;
                                return true;
                            }
                            
                            return false;
                        }        
                    },
                    new AIBehavior() {
                        @Override
                        public Option action(Conveyor data) {
                            return ObjectivePriorityAsEnemy(data);
                        }
                    }
                )
        );
    }
    
    private Option GroupUp(Conveyor data) {
        int max = 8;
        return mostFavorableMiscOption(surround(data.getUnit().getPos(), max), data); //group up around some target of the same allegiance
    }
    
    private Option RunAway(Conveyor data) {
        ObjectiveData objective = data.getObjective().getCriteria();
        
        //play objective it that is it
        if (allegiance == UnitAllegiance.Ally && objective.getEscape()) {
            List<Tile> escTiles = MapLevel.reorderTheseTilesByClosestTo(
                reference.getPos(),
                GameUtils.getSpecialTiles(TileType.Escape, allegiance)
            );
                
            for (Tile esc : escTiles) {
                if (!esc.isOccupied && reference.movementTiles().contains(esc.getPos())) {
                    return mostFavorableMiscOption(esc.getPos(), data);
                }
            }
                
            return mostFavorableMiscOption(reference.movementTileFurthestOnPathTowards(escTiles.get(0).getPos()).getPos(), data);
        }
        
        if (allegiance == UnitAllegiance.Enemy && objective.getX_EnemiesEscapeForDefeat() != null) {
            List<Tile> escTiles = MapLevel.reorderTheseTilesByClosestTo(
                reference.getPos(),
                GameUtils.getSpecialTiles(TileType.Escape, allegiance)
            );
                
            for (Tile esc : escTiles) {
                if (!esc.isOccupied && reference.movementTiles().contains(esc.getPos())) {
                    return mostFavorableMiscOption(esc.getPos(), data);
                }
            }
                
            return mostFavorableMiscOption(reference.movementTileFurthestOnPathTowards(escTiles.get(0).getPos()).getPos(), data);
        }
        
        //get scariest enemy unit position
        MapCoords scariest = null;
        int highestConcentration = 0;
        for (TangibleUnit unit : data.getAllUnits()) {
            if (!unit.isAlliedWith(reference.getAllegiance()) && unit.getFSM().getEnumState() != UnitState.Dead) {
                int concentration = unit.calculateConcentrationValue(1);
                if (scariest == null || concentration > highestConcentration) {
                    scariest = unit.getPos();
                    highestConcentration = concentration;
                }
            }
        }
        
        MapCoords furthestMoveTileFrom = null;
        for (MapCoords tile : reference.movementTiles()) {
            if (furthestMoveTileFrom == null || (scariest != null && scariest.spacesFrom(tile) > scariest.spacesFrom(furthestMoveTileFrom))) {
                furthestMoveTileFrom = tile;
            }
        }
        
        if (furthestMoveTileFrom == null) {
            return DefaultBehavior(data);
        }
        
        return mostFavorableMiscOption(furthestMoveTileFrom, data);
    }
    
    private Option ChargeIn(Conveyor data) {
        Option highestPriorityOffensiveOption = calculateHighestPriorityOffensiveOption(data, ANY);
        if (highestPriorityOffensiveOption != null) {
            return highestPriorityOffensiveOption;
        }
        
        return mostFavorableMiscOption(
                reference.movementTileFurthestOnPathTowards(
                    calculateHighestPriorityOffensiveOptionNotRegardingDistance(data).targetTile
                ).getPos(),
                data
        );
    }
    
    public Option DefaultBehavior(Conveyor data) {
        Option highestPriorityAssistOption = calculateHighestPriorityAssistOption(data, ANY);        
        if (highestPriorityAssistOption != null) {
            return highestPriorityAssistOption;
        }
                
        return ChargeIn(data);
    }
    
    public MapCoords surround(MapCoords target, int cap) { //returns first available coord
        for (int range = 1; range <= cap; range++) {
            for (MapCoords tile : reference.venture().smartCoordsForTilesOfRange(range, target)) {
                if (!MasterFsmState.getCurrentMap().getTileAt(tile).isOccupied) {
                    return tile;
                }
            }
        }
        
        return null;
    }
    
    public Option ObjectivePriorityAsAlly(Conveyor data) {
        ObjectiveData objective = data.getObjective().getCriteria();
        MapLevel currentMap = MasterFsmState.getCurrentMap();
        
        //adding phase
        List<Tile> toProtect = new ArrayList<>();
        
        if (objective.getAnyAnnexableTileAnnexedForDefeat() || objective.getAllAnnexableTilesAnnexedForDefeat()) { //the ally will rush to protect these tiles
            toProtect.addAll(GameUtils.getSpecialTiles(TileType.Annex, reference.getAllegiance(), false));
        }
        
        if (objective.getX_CharactersDieForDefeat() != null) { //the ally will rush to protect this character and move with them
            GameUtils.retrieveCharacters(objective.getX_CharactersDieForDefeat(), data).forEach((tu) -> {
                toProtect.add(currentMap.getTileAt(tu.getPos()));
            });
        }
        
        if (objective.getX_EntitiesDieForDefeat() != null) { //the ally will rush to protect this structure
            GameUtils.retrieveEntities(objective.getX_EntitiesDieForDefeat(), data).forEach((entity) -> {
                toProtect.add(currentMap.getTileAt(entity.getPos()));
            });
        }
        
        Option protect = protectTheseTiles(toProtect, data);
        if (protect != null) {
            return protect;
        }
        
        return DefaultBehavior(data);
    }
    
    public Option ObjectivePriorityAsEnemy(Conveyor data) { //protect before attack
        ObjectiveData objective = data.getObjective().getCriteria();
        MapLevel currentMap = MasterFsmState.getCurrentMap();
        
        //adding phase: defensive
        List<Tile> toProtect = new ArrayList<>();
        
        if (objective.getAnnexAnyAnnexableTile() || objective.getAnnexAllAnnexableTiles()) { //the enemy will rush to protect these tiles
            toProtect.addAll(GameUtils.getSpecialTiles(TileType.Annex, reference.getAllegiance(), false));
        }
        
        if (objective.getNamesOfBossesToKill() != null) { //the enemy will rush to protect this character and move with them (be a bodyguard)
            GameUtils.retrieveCharacters(objective.getNamesOfBossesToKill(), data).forEach((tu) -> {
                toProtect.add(currentMap.getTileAt(tu.getPos()));
            });
        }
        
        if (objective.getNamesOfEntitiesToKill()!= null) { //the enemy will rush to protect this structure
            GameUtils.retrieveEntities(objective.getNamesOfEntitiesToKill(), data).forEach((entity) -> {
                toProtect.add(currentMap.getTileAt(entity.getPos()));
            });
        }
        
        //protect phase
        Option protect = protectTheseTiles(toProtect, data);
        if (protect != null) {
            return protect;
        }
        
        //adding phase: offensive
        List<Tile> toOffend = new ArrayList<>();
        
        if (objective.getEscape()) {
            return ChargeIn(data);
        }
        
        //attempt annex phase
        if (objective.getAnyAnnexableTileAnnexedForDefeat() || objective.getAllAnnexableTilesAnnexedForDefeat()) { //the enemy will rush to annex these tiles
            return annexWithTheseTiles(GameUtils.getSpecialTiles(TileType.Annex, reference.getAllegiance()), data);
        }
        
        //attempt escapist phase
        if (objective.getX_EnemiesEscapeForDefeat() != null) {
            List<TangibleUnit> escapees = GameUtils.retrieveCharacters(objective.getX_EnemiesEscapeForDefeat(), data);
            for (TangibleUnit escapee : escapees) {
                if (escapee.equals(reference)) {
                    List<Tile> toEscapeIn = new ArrayList<>();
                    toEscapeIn.addAll(GameUtils.getSpecialTiles(TileType.Escape));
                    
                    return escapeWithTheseTiles(toEscapeIn, data);
                }
            }
        }
        
        if (objective.getX_CharactersDieForDefeat() != null) { //the enemy will rush to attack these characters
            GameUtils.retrieveCharacters(objective.getX_CharactersDieForDefeat(), data).forEach((tu) -> {
                toOffend.add(currentMap.getTileAt(tu.getPos()));
            });
        }
        
        if (objective.getX_EntitiesDieForDefeat() != null) { //the enemy will rush to attack these structures
            GameUtils.retrieveEntities(objective.getX_EntitiesDieForDefeat(), data).forEach((entity) -> {
                toOffend.add(currentMap.getTileAt(entity.getPos()));
            });
        }
        
        //attack phase
        Option attack = attackTheseTiles(toOffend, data);
        if (attack != null) {
            return attack;
        }
        
        return DefaultBehavior(data);
    }
    
    public Option escapeWithTheseTiles(List<Tile> toEscapeIn, Conveyor data) {
        //prioritize the closest one
        List<Tile> toEscapeOrdered = MapLevel.reorderTheseTilesByClosestTo(reference.getPos(), toEscapeIn);
        
        for (int i = 0; i < toEscapeOrdered.size(); i++) {
            Tile escapeTile = toEscapeOrdered.get(i);
            MapCoords escape = escapeTile.getPos();
            
            if (reference.canReach(escape) && !escapeTile.isOccupied) {
                return new Option(escape, PostMoveAction.Escape);
            } else {
                //it will assist only if any targeted tile is closer to the goal than previous
                AllegianceRecognizer assistLimiter = new AllegianceRecognizer() {
                    @Override
                    public boolean allows(TangibleUnit target) {
                        for (Integer range : reference.getFullAssistRange()) {
                            List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                            if (ableSquares.stream().anyMatch(
                                    (square) -> (!reference.venture().isCloserThan(square, escape)) )) {
                                return true;
                            }
                        }
                            
                        return false;
                    }
                };
                
                Option highestPriorityAssistOption = calculateHighestPriorityAssistOption(data, assistLimiter);
                if (highestPriorityAssistOption != null) {
                    return highestPriorityAssistOption;
                }
                
                AllegianceRecognizer offensiveLimiter = new AllegianceRecognizer() {
                    @Override
                    public boolean allows(TangibleUnit target) {
                        for (Integer range : reference.getFullOffensiveRange()) {
                            List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                            if (ableSquares.stream().anyMatch(
                                    (square) -> (!reference.venture().isCloserThan(square, escape)) )) {
                                return true;
                            }
                        }
                            
                        return false;
                    }
                };
                
                Option highestPriorityOffensiveOption = calculateHighestPriorityOffensiveOption(data, offensiveLimiter);
                if (highestPriorityOffensiveOption != null) { //enemies in range
                    return highestPriorityOffensiveOption;
                } 
                
                return mostFavorableMiscOption(
                    reference.movementTileFurthestOnPathTowards(escape).getPos(),
                    data
                );
            }
        }
        
        return DefaultBehavior(data);
    }
    
    public Option annexWithTheseTiles(List<Tile> toAnnex, Conveyor data) {
        //prioritize the closest one
        List<Tile> toAnnexOrdered = MapLevel.reorderTheseTilesByClosestTo(reference.getPos(), toAnnex);
        
        for (int i = 0; i < toAnnexOrdered.size(); i++) {
            Tile annexTile = toAnnexOrdered.get(i);
            MapCoords annex = annexTile.getPos();
            
            if (reference.canReach(annex) && !annexTile.isOccupied) {
                return new Option(annex, PostMoveAction.Annex);
            } else {
                //it will assist only if any targeted tile is closer to the goal than previous
                AllegianceRecognizer assistLimiter = new AllegianceRecognizer() {
                    @Override
                    public boolean allows(TangibleUnit target) {
                        for (Integer range : reference.getFullAssistRange()) {
                            List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                            if (ableSquares.stream().anyMatch(
                                    (square) -> (!reference.venture().isCloserThan(square, annex)) )) {
                                return true;
                            }
                        }
                            
                        return false;
                    }
                };
                
                Option highestPriorityAssistOption = calculateHighestPriorityAssistOption(data, assistLimiter);
                if (highestPriorityAssistOption != null) {
                    return highestPriorityAssistOption;
                }
                
                AllegianceRecognizer offensiveLimiter = new AllegianceRecognizer() {
                    @Override
                    public boolean allows(TangibleUnit target) {
                        for (Integer range : reference.getFullOffensiveRange()) {
                            List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                            if (ableSquares.stream().anyMatch(
                                    (square) -> (!reference.venture().isCloserThan(square, annex)) )) {
                                return true;
                            }
                        }
                            
                        return false;
                    }
                };
                
                Option highestPriorityOffensiveOption = calculateHighestPriorityOffensiveOption(data, offensiveLimiter);
                if (highestPriorityOffensiveOption != null) { //enemies in range
                    return highestPriorityOffensiveOption;
                } 
                
                return mostFavorableMiscOption(
                    reference.movementTileFurthestOnPathTowards(annex).getPos(),
                    data
                );
            }
        }
        
        return DefaultBehavior(data);
    }
    
    public Option attackTheseTiles(List<Tile> toOffend, Conveyor data) {
        //prioritize the closest one
        List<Tile> toOffendOrdered = MapLevel.reorderTheseTilesByClosestTo(reference.getPos(), toOffend);
        
        for (Tile offendTile : toOffendOrdered) {
            MapCoords offend = offendTile.getPos();
            
            for (Integer range : reference.getFullOffensiveRange()) {
                if (reference.venture().addMobility(range).willReach(offend)) { //if they can attack
                    if (offendTile.getOccupier() != null) { //the thing it wants to kill is an enemy unit
                        return mostFavorableOffensiveOption(offendTile.getOccupier(), data);
                    }
                    
                    return new Option(offend, PostMoveAction.Attack, offendTile.getStructureOccupier(), range); //meaning it is a destroyable structure
                }
            }
            
            //it will assist only if any target tile is closer to the goal than previous
            AllegianceRecognizer assistLimiter = new AllegianceRecognizer() {
                @Override
                public boolean allows(TangibleUnit target) {
                    for (Integer range : reference.getFullAssistRange()) {
                        List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                        if (ableSquares.stream().anyMatch(
                                    (square) -> (!reference.venture().isCloserThan(square, offend)) )) {
                                return true;
                            }
                        }
                            
                        return false;
                    }
                };
                
                Option highestPriorityAssistOption = calculateHighestPriorityAssistOption(data, assistLimiter);
                if (highestPriorityAssistOption != null) {
                    return highestPriorityAssistOption;
                }
                
                AllegianceRecognizer offensiveLimiter = new AllegianceRecognizer() {
                    @Override
                    public boolean allows(TangibleUnit target) {
                        for (Integer range : reference.getFullOffensiveRange()) {
                            List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                            if (ableSquares.stream().anyMatch(
                                    (square) -> (!reference.venture().isCloserThan(square, offend)) )) {
                                return true;
                            }
                        }
                        
                        return false;
                    }
                };
                
                Option highestPriorityOffensiveOption = calculateHighestPriorityOffensiveOption(data, offensiveLimiter);
                if (highestPriorityOffensiveOption != null) { //enemies in range
                    return highestPriorityOffensiveOption;
                } 
                
                return mostFavorableMiscOption(
                    reference.movementTileFurthestOnPathTowards(offend).getPos(),
                    data
                );
        }
        
        return DefaultBehavior(data);
    }
    
    public Option protectTheseTiles(List<Tile> toProtect, Conveyor data) {
        //prioritize the closest one
        List<Tile> toProtectOrdered = MapLevel.reorderTheseTilesByClosestTo(reference.getPos(), toProtect);
        
        for (int i = 0; i < toProtectOrdered.size(); i++) {
            Tile payload = toProtectOrdered.get(i);
            
            if (reference.canReach(payload.getPos())) { //tile they want to protect is in their range
                if (!payload.isOccupied) {
                    //become a meat shield
                    return new Option(payload.getPos());
                } else {
                    //assist is top priority
                    AllegianceRecognizer assistLimiter = new AllegianceRecognizer() {
                        @Override
                        public boolean allows(TangibleUnit target) {
                            for (Integer range : reference.getFullAssistRange()) {
                                List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                                if (ableSquares.stream().anyMatch((square) -> ((reference.canReach(square) && reference.venture().addMobility(1).setMapCoords(square).willReach(payload.getPos()))))) {
                                    return true;
                                }
                            }
                            
                            return false;
                        }
                    };
                    
                    /**
                     * allied units will be assisted on 2 conditions:
                     * First of all, whether assiting them will take them out of the range where they can become a meatshield for the tile they want to protect
                     * And Secondly, out of those units, who needs it the most
                     */ 
                    Option highestPriorityAssistOption = calculateHighestPriorityAssistOption(data, assistLimiter);
                    if (highestPriorityAssistOption != null) {
                        return highestPriorityAssistOption;
                    }
                    
                    //offense is second priority
                    AllegianceRecognizer offensiveLimiter = new AllegianceRecognizer() {
                        @Override
                        public boolean allows(TangibleUnit target) {
                            for (Integer range : reference.getFullOffensiveRange()) {
                                List<MapCoords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.getPos());
                                if (ableSquares.stream().anyMatch((square) -> ((reference.canReach(square) && reference.venture().addMobility(1).setMapCoords(square).willReach(payload.getPos()))))) {
                                    return true;
                                }
                            }
                            
                            return false;
                        }
                    };
                    
                    /**
                     * enemy units will be targeted on 2 conditions:
                     * First of all, whether initiating combat with them will take them out of the range where they can become a meatshield for the tile they want to protect
                     * And Secondly, out of those units, who is prioritized the most to attack
                     */
                    Option highestPriorityOffensiveOption = calculateHighestPriorityOffensiveOption(data, offensiveLimiter);
                    //List<TangibleUnit> targets = calculateTargetsInOrderOfPriority(data, limiter);
                    
                    if (highestPriorityOffensiveOption != null) { //enemies in range
                        return highestPriorityOffensiveOption;
                    } 
                    
                    //no enemies in range
                    //form a "phalanx" (meat shields adjacent to tile)
                    int max = 1;
                    MapCoords phalanx = surround(payload.getPos(), max);
                    if (phalanx != null) {
                        return mostFavorableMiscOption(reference.closestMovementTileTo(phalanx), data);
                    }
                }
            } else { //tile they want to protect is not in their range ? move towards it
                Option highestPriorityAssistOption = calculateHighestPriorityAssistOption(data, ANY);
                if (highestPriorityAssistOption != null) {
                    return highestPriorityAssistOption;
                }
                
                Option highestPriorityOffensiveOption = calculateHighestPriorityOffensiveOption(data, ANY);
                if (highestPriorityOffensiveOption != null) {
                    return highestPriorityOffensiveOption;
                }
                
                return mostFavorableMiscOption(reference.movementTileFurthestOnPathTowards(payload.getPos()).getPos(), data);
            }
        }
        
        return null;
    }
    
    public Option mostFavorableMiscOption(MapCoords pos, Conveyor data) {
        Ability bestAbility = null;
        FormationTechnique bestFormationTechnique = null;
        Item bestItem = null;
        
        ActionInfo opciones = reference.determineOptions(pos, data);
        
        if (opciones.getAvailableActions().contains(PostMoveAction.Ability)) {
            for (Ability abl : opciones.getUsableAbilities()) {
                if (abl.getType() == ToolType.SupportSelf) {
                    if (bestAbility == null || abl.getDesirability(data) > bestAbility.getDesirability(data)) {
                        bestAbility = abl;
                    }
                }
            }
        }
            
        if (opciones.getAvailableActions().contains(PostMoveAction.Formation) && reference.equippedFormation().getMostDesiredTechnique().getToolType() == ToolType.SupportSelf) {
            bestFormationTechnique = reference.equippedFormation().getMostDesiredTechnique();
        }
            
        if (opciones.getAvailableActions().contains(PostMoveAction.Item)) {
            for (Item itm : opciones.getUsableItems()) {
                if (itm.getItemEffect() != null && itm.getItemEffect().getType() == ToolType.SupportSelf) {
                    if (bestItem == null || itm.getItemEffect().getDesirability() > bestItem.getItemEffect().getDesirability()) {
                        bestItem = itm;
                    }
                }
            }
        }
        
        int highestPriority = 0;
        MapCoords bestCoords = null;
        Attribute bestOption = null;
        Purpose purpose = null;
        PostMoveAction action = null;
        
        if (bestAbility != null && bestAbility.getDesirability(data) > highestPriority) {
            highestPriority = bestAbility.getDesirability(data);
            bestOption = bestAbility;
            purpose = Purpose.None;
            action = PostMoveAction.Ability;
        }
        
        if (bestFormationTechnique != null &&  bestFormationTechnique.getDesirability() > highestPriority) {
            highestPriority = bestFormationTechnique.getDesirability();
            bestOption = bestFormationTechnique;
            purpose = Purpose.None;
            action = PostMoveAction.Formation;
        }
        
        if (bestItem != null && bestItem.getItemEffect().getDesirability() > highestPriority) {
            bestOption = bestItem;
            purpose = Purpose.None;
            action = PostMoveAction.Item;
        }
        
        return new Option(action, purpose, bestOption, bestCoords, 0, null).setPriority(highestPriority);
    }
    
    public Option mostFavorableOffensiveOption(TangibleUnit enemy, Conveyor data) {
        PrebattleForecast bestEtherForecast = null;
        Formula bestFormula = null;
        MapCoords bestFormulaCoords = null;
        
        PrebattleForecast bestWeaponForecast = null;
        Weapon bestWeapon = null;
        MapCoords bestWeaponCoords = null;
        
        PrebattleForecast bestSkillForecast = null;
        Skill bestSkill = null;
        MapCoords bestSkillCoords = null;
        
        Ability bestAbility = null;
        MapCoords bestAbilityCoords = null;
        
        FormationTechnique bestFormationTechnique = null, mostDesirableTechnique = reference.equippedFormation().getMostDesiredTechnique();
        MapCoords bestFormationCoords = null;
        
        Item bestItem = null;
        MapCoords bestItemCoords = null;
        
        for (MapCoords spot : reference.allowedCoordsFromTarget(enemy.getPos(), true)) {
            int range = spot.getCoords().getRange();
            ActionInfo opciones = reference.determineOptions(spot, data);
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Ether)) {
                for (Formula fma : opciones.getUsableFormulas()) {
                    if (!fma.getFormulaPurpose().isSupportive() && fma.getActualFormulaData().getRange().contains(range)) {
                        reference.equip(fma);
                        PrebattleForecast forecast = PrebattleForecast.createSimulatedForecast(new Conveyor(reference).setEnemyUnit(enemy), range);
                        if (bestEtherForecast == null || forecast.calculateDesirabilityToInitiate() > bestEtherForecast.calculateDesirabilityToInitiate()) {
                            bestEtherForecast = forecast;
                            bestFormula = fma;
                            bestFormulaCoords = spot;
                        }
                    }
                }
            }
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Attack)) {
                for (Weapon wpn : opciones.getUsableWeapons()) {
                    if (wpn.getWeaponData().getRange().contains(range)) {
                        reference.equip(wpn);
                        PrebattleForecast forecast = PrebattleForecast.createSimulatedForecast(new Conveyor(reference).setEnemyUnit(enemy), range);
                        if (bestWeaponForecast == null || forecast.calculateDesirabilityToInitiate() > bestWeaponForecast.calculateDesirabilityToInitiate()) {
                            bestWeaponForecast = forecast;
                            bestWeapon = wpn;
                            bestWeaponCoords = spot;
                        }
                    }
                }
            }
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Skill)) {
                for (Skill skl : opciones.getUsableSkills()) {
                    if (skl.getType() == ToolType.Attack && bestWeapon != null && skl.getEffect().getTrueRange(bestWeapon.getWeaponData()).contains(range)) {
                        reference.equip(bestWeapon);
                        reference.setToUseSkill(skl);
                        PrebattleForecast forecast = PrebattleForecast.createSimulatedForecast(new Conveyor(reference).setEnemyUnit(enemy), range);
                        if (bestSkillForecast == null || forecast.calculateDesirabilityToInitiate() > bestSkillForecast.calculateDesirabilityToInitiate()) {
                            bestSkillForecast = forecast;
                            bestSkill = skl;
                            bestSkillCoords = spot;
                        }
                    }
                }
            }
            
            reference.setToUseSkill(null);
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Ability)) {
                for (Ability abl : opciones.getUsableAbilities()) {
                    if (abl.getType() == ToolType.Attack && abl.getRadius().contains(range)) {
                        if (bestAbility == null || abl.getDesirability(data) > bestAbility.getDesirability(data)) {
                            bestAbility = abl;
                            bestAbilityCoords = spot;
                        }
                    }
                }
            }
            
            if (
                    opciones.getAvailableActions().contains(PostMoveAction.Formation) 
                    && mostDesirableTechnique.getToolType() == ToolType.Attack
                    && mostDesirableTechnique.getRanges().contains(range)
               ) 
            {
                bestFormationTechnique = mostDesirableTechnique;
                bestFormationCoords = spot;
            }
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Item)) {
                for (Item itm : opciones.getUsableItems()) {
                    if (itm.getItemEffect() != null && itm.getItemEffect().getType() == ToolType.Attack && itm.getItemEffect().getRadius().contains(range)) {
                        if (bestItem == null || itm.getItemEffect().getDesirability() > bestItem.getItemEffect().getDesirability()) {
                            bestItem = itm;
                            bestItemCoords = spot;
                        }
                    }
                }
            }
        }
        
        int highestPriority = 0;
        MapCoords bestCoords = null;
        Attribute bestOption = null;
        Purpose purpose = null;
        PostMoveAction action = null;
        
        if (bestEtherForecast != null && bestEtherForecast.calculateDesirabilityToInitiate() > highestPriority) {
            highestPriority = bestEtherForecast.calculateDesirabilityToInitiate();
            bestCoords = bestFormulaCoords;
            bestOption = bestFormula;
            purpose = Purpose.EtherAttack;
            action = PostMoveAction.Ether;
            reference.equip(bestFormula);
        }
        
        if (bestWeaponForecast != null && bestWeaponForecast.calculateDesirabilityToInitiate() > highestPriority) {
            highestPriority = bestWeaponForecast.calculateDesirabilityToInitiate();
            bestCoords = bestWeaponCoords;
            bestOption = bestWeapon;
            purpose = Purpose.WeaponAttack;
            action = PostMoveAction.Attack;
            reference.equip(bestWeapon);
        }
        
        if (bestSkillForecast != null && bestSkillForecast.calculateDesirabilityToInitiate() > highestPriority) {
            highestPriority = bestSkillForecast.calculateDesirabilityToInitiate();
            bestCoords = bestSkillCoords;
            bestOption = bestSkill;
            purpose = Purpose.SkillAttack;
            action = PostMoveAction.Skill;
            reference.equip(bestWeapon);
            reference.setToUseSkill(bestSkill);
        }
        
        if (bestAbility != null && bestAbility.getDesirability(data) > highestPriority) {
            highestPriority = bestAbility.getDesirability(data);
            bestCoords = bestAbilityCoords;
            bestOption = bestAbility;
            purpose = Purpose.None;
            action = PostMoveAction.Ability;
        }
        
        if (bestFormationTechnique != null && mostDesirableTechnique.getDesirability() > highestPriority) {
            highestPriority = mostDesirableTechnique.getDesirability();
            bestOption = bestFormationTechnique;
            bestCoords = bestFormationCoords;
            purpose = Purpose.None;
            action = PostMoveAction.Formation;
        }
        
        if (bestItem != null && bestItem.getItemEffect().getDesirability() > highestPriority) {
            bestCoords = bestItemCoords;
            bestOption = bestItem;
            purpose = Purpose.None;
            action = PostMoveAction.Item;
        }
        
        int range = 0;
        
        if (bestCoords != null) {
            range = bestCoords.spacesFrom(enemy.getPos());
        }
        
        return new Option(action, purpose, bestOption, bestCoords, range, enemy).setPriority(highestPriority);
    }
    
    public Option mostFavorableAssistOption(TangibleUnit ally, Conveyor data) {
        SupportForecast bestEtherForecast = null;
        Formula bestFormula = null;
        MapCoords bestFormulaCoords = null;
        
        Ability bestAbility = null;
        MapCoords bestAbilityCoords = null;
        
        FormationTechnique bestFormationTechnique = null, mostDesirableTechnique = reference.equippedFormation().getMostDesiredTechnique();
        MapCoords bestFormationCoords = null;
        
        Item bestItem = null;
        MapCoords bestItemCoords = null;
        
        for (MapCoords spot : reference.allowedCoordsFromTarget(ally.getPos(), false)) { //calculate the most favorable range here too
            int range = spot.getCoords().getRange();
            ActionInfo opciones = reference.determineOptions(spot, data);
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Ether)) {
                for (Formula fma : opciones.getUsableFormulas()) {
                    if (fma.getFormulaPurpose().isSupportive() && fma.getActualFormulaData().getRange().contains(range)) {
                        reference.equip(fma);
                        SupportForecast forecast = new SupportForecast(new Conveyor(reference).setOtherUnit(ally));
                        if (bestEtherForecast == null || forecast.calculateDesirabilityToInitiate() > bestEtherForecast.calculateDesirabilityToInitiate()) {
                            bestEtherForecast = forecast;
                            bestFormula = fma;
                            bestFormulaCoords = spot;
                        }
                    }
                }
            }
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Ability)) {
                for (Ability abl : opciones.getUsableAbilities()) {
                    if (abl.getType() == ToolType.SupportAlly && abl.getRadius().contains(range)) {
                        if (bestAbility == null || abl.getDesirability(data) > bestAbility.getDesirability(data)) {
                            bestAbility = abl;
                            bestAbilityCoords = spot;
                        }
                    }
                }
            }
            
            if (
                    opciones.getAvailableActions().contains(PostMoveAction.Formation) 
                    && mostDesirableTechnique.getToolType() == ToolType.SupportAlly
                    && mostDesirableTechnique.getRanges().contains(range)
               ) 
            {
                bestFormationTechnique = mostDesirableTechnique;
                bestFormationCoords = spot;
            }
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Item)) {
                for (Item itm : opciones.getUsableItems()) {
                    if (itm.getItemEffect() != null && itm.getItemEffect().getType() == ToolType.SupportAlly && itm.getItemEffect().getRadius().contains(range)) {
                        if (bestItem == null || itm.getItemEffect().getDesirability() > bestItem.getItemEffect().getDesirability()) {
                            bestItem = itm;
                            bestItemCoords = spot;
                        }
                    }
                }
            }
        }
        
        int highestPriority = 0;
        MapCoords bestCoords = null;
        Attribute bestOption = null;
        Purpose purpose = null;
        PostMoveAction action = null;
        
        if (bestEtherForecast != null && bestEtherForecast.calculateDesirabilityToInitiate() > highestPriority) {
            highestPriority = bestEtherForecast.calculateDesirabilityToInitiate();
            bestCoords = bestFormulaCoords;
            bestOption = bestFormula;
            purpose = Purpose.EtherAttack;
            action = PostMoveAction.Ether;
            reference.equip(bestFormula);
        }
        
        if (bestAbility != null && bestAbility.getDesirability(data) > highestPriority) {
            highestPriority = bestAbility.getDesirability(data);
            bestCoords = bestAbilityCoords;
            bestOption = bestAbility;
            purpose = Purpose.None;
            action = PostMoveAction.Ability;
        }
        
        if (bestFormationTechnique != null && mostDesirableTechnique.getDesirability() > highestPriority) {
            highestPriority = mostDesirableTechnique.getDesirability();
            bestOption = mostDesirableTechnique;
            bestCoords = bestFormationCoords;
            purpose = Purpose.None;
            action = PostMoveAction.Formation;
        }
        
        if (bestItem != null && bestItem.getItemEffect().getDesirability() > highestPriority) {
            bestCoords = bestItemCoords;
            bestOption = bestItem;
            purpose = Purpose.None;
            action = PostMoveAction.Item;
        }

        int range = 0;
        if (bestCoords != null) {
            range = bestCoords.spacesFrom(ally.getPos());
        }
        
        return new Option(action, purpose, bestOption, bestCoords, range, ally).setPriority(highestPriority);
    }
    
    public Option calculateHighestPriorityAssistOption(Conveyor conv, AllegianceRecognizer condition) {
        List<TangibleUnit> inRangeUnits = reference.UnitsInRange(condition.addExtra(ALLIED), conv.getAllUnits());
        
        Option highestPriorityOption = null;
        for (TangibleUnit tu : inRangeUnits) {
            Option supportive = mostFavorableAssistOption(tu, conv);
            if (highestPriorityOption == null || supportive.priority > highestPriorityOption.priority) {
                highestPriorityOption = supportive;
            }
        }
        
        return highestPriorityOption;
    }
    
    public Option calculateHighestPriorityOffensiveOption(Conveyor conv, AllegianceRecognizer condition) {
        List<TangibleUnit> inRangeUnits = reference.UnitsInRange(condition.addExtra(ENEMY), conv.getAllUnits());

        Option highestPriorityOption = null;
        for (TangibleUnit tu : inRangeUnits) {
            Option offensive = mostFavorableOffensiveOption(tu, conv);
            if (highestPriorityOption == null || offensive.priority > highestPriorityOption.priority) {
                highestPriorityOption = offensive;
            }
        }
        
        return highestPriorityOption;
    }
    
    public Option calculateHighestPriorityOffensiveOptionNotRegardingDistance(Conveyor conv) {
        List<TangibleUnit> inRangeUnits = GameUtils.calculateEnemyUnits(reference, conv);

        Option highestPriorityOption = null;
        for (TangibleUnit tu : inRangeUnits) {
            Option offensive = mostFavorableOffensiveOption(tu, conv);
            if (highestPriorityOption == null || offensive.priority > highestPriorityOption.priority) {
                highestPriorityOption = offensive;
            }
        }
        
        return highestPriorityOption;
    }
}

interface AICondition {
    public boolean condition(Conveyor data);
}
    
interface AIBehavior {
    public Option action(Conveyor data);
}
