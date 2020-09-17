/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.ai;

import battle.Combatant.BaseStat;
import etherealtempest.info.Conveyer;
import battle.forecast.PrebattleForecast;
import battle.ability.Ability;
import battle.forecast.SupportForecast;
import battle.formation.Formation;
import battle.formula.Formula;
import battle.item.Item;
import battle.item.Weapon;
import battle.skill.Skill;
import etherealtempest.FSM.EntityState;
import etherealtempest.GameUtils;
import etherealtempest.MasterFsmState;
import etherealtempest.info.ActionInfo;
import etherealtempest.info.ActionInfo.PostMoveAction;
import fundamental.Associated;
import fundamental.Tool.ToolType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import maps.flow.ObjectiveData;
import maps.layout.Coords;
import maps.layout.Cursor.Purpose;
import maps.layout.Map;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.UnitStatus;
import maps.layout.Tile;
import maps.layout.TileData.TileType;
import maps.layout.VenturePeek;

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
    
    private final TangibleUnit reference;
    
    private UnitStatus allegiance;
    
    //the meat
    private Command givenCommand = null;
    private HashMap<Condition, Behavior> behaviorMap = null;
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
        allegiance = reference.unitStatus;
        
        SAME = new AllegianceRecognizer() {
            @Override
            public boolean allows(TangibleUnit tu) {
                return reference.getID() != tu.getID() && allegiance == tu.unitStatus;
            }
        };
        
        ALLIED = new AllegianceRecognizer() {
            @Override
            public boolean allows(TangibleUnit tu) {
                return reference.unitStatus.alliedWith(tu.unitStatus);
            }
        };
        
        ENEMY = new AllegianceRecognizer() {
            @Override
            public boolean allows(TangibleUnit tu) {
                return !reference.unitStatus.alliedWith(tu.unitStatus);
            }
        };
    }
    
    public AI(TangibleUnit ref, List<ConditionalBehavior> mind) {
        this(ref);
        mindset = mind;
    }
    
    public AI(TangibleUnit ref, HashMap<Condition, Behavior> processes) {
        this(ref);
        
        behaviorMap = processes;
    }
    
    public List<ConditionalBehavior> getMindset() { return mindset; }
    
    public UnitStatus getAllegiance() { return allegiance; }
    public Command getCommand() { return givenCommand; }
    
    public void issueCommand(Command cmd) {
        givenCommand = cmd;
    }
    
    public void cancelCommand() { givenCommand = null; }
    
    public void setAllegiance(UnitStatus us) {
        allegiance = us;
    }
    
    public Option calculateNextCourseOfAction(Conveyer data) {
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
    
    void updateMindset(Conveyer data) {
        mindset = new ArrayList<>();
        behaviorMap.keySet().forEach((cond) -> {
            mindset.add(mindsetMap(cond, behaviorMap.get(cond), data));
        });
    }
    
    private ConditionalBehavior mindsetMap(Condition condition, Behavior behavior, Conveyer data) {
        boolean cause;
        Option effect;
        
        //cause
        switch (condition) {
            case Always:
                cause = true;
                break;
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
                if (reference.unitStatus == UnitStatus.Ally) {
                    effect = ObjectivePriorityAsAlly(data);
                } else { //enemy
                    effect = ObjectivePriorityAsEnemy(data);
                }
                break;
            case FullThrottle:
                effect = ChargeIn(data);
                break;
            case Hold:
                effect = mostFavorableMiscOption(reference.coords(), data);
                break;
            case Drugged:
                int random = (int)(Math.random() * 2);
                if (random == 0) { //random between all commands
                    int index = (int)(4 * Math.random());
                    effect = optionMap(Command.valueOf(index), data);
                } else { // random == 1; random position in move squares
                    List<Coords> squares = reference.movementTiles();
                    Coords tile = squares.get((int)(Math.random() * squares.size()));
                    Tile rand = MasterFsmState.getCurrentMap().fullmap[reference.getElevation()][tile.getX()][tile.getY()];
                    if (rand.getOccupier() != null) {
                        effect = mostFavorableOffensiveOption(rand.getOccupier(), data);
                    } else {
                        effect = mostFavorableMiscOption(tile, data);
                    }
                }
                break;
            default:
                effect = DefaultBehavior(data);
                break;
        }
        
        return new ConditionalBehavior(cause, effect);
    }
    
    Option optionMap(Command command, Conveyer data) {
        switch(command) {
            case Charge:
                return ChargeIn(data);
            case Regroup:
                return GroupUp(data);
            case Run:
                return RunAway(data);
            case HoldPosition:
                return mostFavorableMiscOption(reference.coords(), data);
        }
        
        return null;
    }
    
    public List<ConditionalBehavior> DefaultEnemyMindset() {
        return Arrays.asList(
                new ConditionalBehavior( //top priority
                    new AICondition() {
                        private boolean hasPassedOnce = false;
                        
                        @Override
                        public boolean condition(Conveyer data) {
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
                        public Option action(Conveyer data) {
                            return ObjectivePriorityAsEnemy(data);
                        }
                    }
                )
        );
    }
    
    private Option GroupUp(Conveyer data) {
        return mostFavorableMiscOption(surround(data.getUnit().coords(), 8, reference.getElevation()), data); //group up around some target of the same allegiance
    }
    
    private Option RunAway(Conveyer data) {
        ObjectiveData objective = data.getObjective().getCriteria();
        
        //play objective it that is it
        if (allegiance == UnitStatus.Ally && objective.getEscape()) {
            List<Tile> escTiles = MasterFsmState.getCurrentMap().reorderTheseTilesByClosestTo(
                    reference.getPosX(), 
                    reference.getPosY(), 
                    GameUtils.getSpecialTiles(TileType.Escape, allegiance)
            );
                
            for (Tile esc : escTiles) {
                if (!esc.isOccupied && reference.movementTiles().contains(esc.coords())) {
                    return mostFavorableMiscOption(esc.coords(), data);
                }
            }
                
            return mostFavorableMiscOption(reference.movementTileFurthestOnPathTowards(escTiles.get(0).coords()).coords(), data);
        }
        
        if (allegiance == UnitStatus.Enemy && objective.getX_EnemiesEscapeForDefeat() != null) {
            List<Tile> escTiles = MasterFsmState.getCurrentMap().reorderTheseTilesByClosestTo(
                    reference.getPosX(), 
                    reference.getPosY(), 
                    GameUtils.getSpecialTiles(TileType.Escape, allegiance)
            );
                
            for (Tile esc : escTiles) {
                if (!esc.isOccupied && reference.movementTiles().contains(esc.coords())) {
                    return mostFavorableMiscOption(esc.coords(), data);
                }
            }
                
            return mostFavorableMiscOption(reference.movementTileFurthestOnPathTowards(escTiles.get(0).coords()).coords(), data);
        }
        
        //get scariest enemy unit position
        Coords scariest = null;
        int highestConcentration = 0;
        for (TangibleUnit unit : data.getAllUnits()) {
            if (!unit.isAlliedWith(reference.unitStatus) && unit.getFSM().getState().getEnum() != EntityState.Dead) {
                int concentration = unit.calculateConcentrationValue(1);
                if (scariest == null || concentration > highestConcentration) {
                    scariest = unit.coords();
                    highestConcentration = concentration;
                }
            }
        }
        
        Coords furthestMoveTileFrom = null;
        for (Coords tile : reference.movementTiles()) {
            if (furthestMoveTileFrom == null || (scariest != null && scariest.difference(tile) > scariest.difference(furthestMoveTileFrom))) {
                furthestMoveTileFrom = tile;
            }
        }
        
        if (furthestMoveTileFrom == null) {
            return DefaultBehavior(data);
        }
        
        return mostFavorableMiscOption(furthestMoveTileFrom, data);
    }
    
    private Option ChargeIn(Conveyer data) {
        Option highestPriorityOffensiveOption = calculateHighestPriorityOffensiveOption(data, ANY);
        if (highestPriorityOffensiveOption != null) {
            return highestPriorityOffensiveOption;
        }
        
        return mostFavorableMiscOption(
                reference.movementTileFurthestOnPathTowards(
                    calculateHighestPriorityOffensiveOptionNotRegardingDistance(data).targetTile
                ).coords(),
                data
        );
    }
    
    public Option DefaultBehavior(Conveyer data) {
        Option highestPriorityAssistOption = calculateHighestPriorityAssistOption(data, ANY);        
        if (highestPriorityAssistOption != null) {
            return highestPriorityAssistOption;
        }
                
        return ChargeIn(data);
    }
    
    public Coords surround(Coords target, int cap, int layer) { //returns first available coord
        Tile[][] layerTiles = MasterFsmState.getCurrentMap().fullmap[layer];
        
        for (int range = 1; range <= cap; range++) {
            for (Coords tile : reference.venture().smartCoordsForTilesOfRange(range, target, layer)) {
                if (!layerTiles[tile.getX()][tile.getY()].isOccupied) {
                    return tile;
                }
            }
        }
        
        return null;
    }
    
    public Option ObjectivePriorityAsAlly(Conveyer data) {
        ObjectiveData objective = data.getObjective().getCriteria();
        Map currentMap = MasterFsmState.getCurrentMap();
        
        //adding phase
        List<Tile> toProtect = new ArrayList<>();
        
        if (objective.getAnyAnnexableTileAnnexedForDefeat() || objective.getAllAnnexableTilesAnnexedForDefeat()) { //the ally will rush to protect these tiles
            toProtect.addAll(GameUtils.getSpecialTiles(TileType.Annex, reference.unitStatus, false));
        }
        
        if (objective.getX_CharactersDieForDefeat() != null) { //the ally will rush to protect this character and move with them
            GameUtils.retrieveCharacters(objective.getX_CharactersDieForDefeat(), data).forEach((tu) -> {
                toProtect.add(currentMap.fullmap[tu.getElevation()][tu.getPosX()][tu.getPosY()]);
            });
        }
        
        if (objective.getX_EntitiesDieForDefeat() != null) { //the ally will rush to protect this structure
            GameUtils.retrieveEntities(objective.getX_EntitiesDieForDefeat(), data).forEach((entity) -> {
                toProtect.add(currentMap.fullmap[entity.getElevation()][entity.getPosX()][entity.getPosY()]);
            });
        }
        
        Option protect = protectTheseTiles(toProtect, data);
        if (protect != null) {
            return protect;
        }
        
        return DefaultBehavior(data);
    }
    
    public Option ObjectivePriorityAsEnemy(Conveyer data) { //protect before attack
        ObjectiveData objective = data.getObjective().getCriteria();
        Map currentMap = MasterFsmState.getCurrentMap();
        
        //adding phase: defensive
        List<Tile> toProtect = new ArrayList<>();
        
        if (objective.getAnnexAnyAnnexableTile() || objective.getAnnexAllAnnexableTiles()) { //the enemy will rush to protect these tiles
            toProtect.addAll(GameUtils.getSpecialTiles(TileType.Annex, reference.unitStatus, false));
        }
        
        if (objective.getNamesOfBossesToKill() != null) { //the enemy will rush to protect this character and move with them (be a bodyguard)
            GameUtils.retrieveCharacters(objective.getNamesOfBossesToKill(), data).forEach((tu) -> {
                toProtect.add(currentMap.fullmap[tu.getElevation()][tu.getPosX()][tu.getPosY()]);
            });
        }
        
        if (objective.getNamesOfEntitiesToKill()!= null) { //the enemy will rush to protect this structure
            GameUtils.retrieveEntities(objective.getNamesOfEntitiesToKill(), data).forEach((entity) -> {
                toProtect.add(currentMap.fullmap[entity.getElevation()][entity.getPosX()][entity.getPosY()]);
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
            return annexWithTheseTiles(GameUtils.getSpecialTiles(TileType.Annex, reference.unitStatus), data);
        }
        
        //attempt escapist phase
        if (objective.getX_EnemiesEscapeForDefeat() != null) {
            List<TangibleUnit> escapees = GameUtils.retrieveCharacters(objective.getX_EnemiesEscapeForDefeat(), data);
            for (TangibleUnit escapee : escapees) {
                if (escapee.getID() == reference.getID()) {
                    List<Tile> toEscapeIn = new ArrayList<>();
                    toEscapeIn.addAll(GameUtils.getSpecialTiles(TileType.Escape));
                    
                    return escapeWithTheseTiles(toEscapeIn, data);
                }
            }
        }
        
        if (objective.getX_CharactersDieForDefeat() != null) { //the enemy will rush to attack these characters
            GameUtils.retrieveCharacters(objective.getX_CharactersDieForDefeat(), data).forEach((tu) -> {
                toOffend.add(currentMap.fullmap[tu.getElevation()][tu.getPosX()][tu.getPosY()]);
            });
        }
        
        if (objective.getX_EntitiesDieForDefeat() != null) { //the enemy will rush to attack these structures
            GameUtils.retrieveEntities(objective.getX_EntitiesDieForDefeat(), data).forEach((entity) -> {
                toOffend.add(currentMap.fullmap[entity.getElevation()][entity.getPosX()][entity.getPosY()]);
            });
        }
        
        //attack phase
        Option attack = attackTheseTiles(toOffend, data);
        if (attack != null) {
            return attack;
        }
        
        return DefaultBehavior(data);
    }
    
    public Option escapeWithTheseTiles(List<Tile> toEscapeIn, Conveyer data) {
        //prioritize the closest one
        List<Tile> toEscapeOrdered = MasterFsmState.getCurrentMap().reorderTheseTilesByClosestTo(reference.getPosX(), reference.getPosY(), toEscapeIn);
        
        for (int i = 0; i < toEscapeOrdered.size(); i++) {
            Tile escapeTile = toEscapeOrdered.get(i);
            Coords escape = escapeTile.coords();
            
            if (reference.canReach(escape) && !escapeTile.isOccupied) {
                return new Option(escape, PostMoveAction.Escape);
            } else {
                //it will assist only if any targeted tile is closer to the goal than previous
                AllegianceRecognizer assistLimiter = new AllegianceRecognizer() {
                    @Override
                    public boolean allows(TangibleUnit target) {
                        for (Integer range : reference.getFullAssistRange()) {
                            List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
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
                            List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
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
                    reference.movementTileFurthestOnPathTowards(escape).coords(),
                    data
                );
            }
        }
        
        return DefaultBehavior(data);
    }
    
    public Option annexWithTheseTiles(List<Tile> toAnnex, Conveyer data) {
        //prioritize the closest one
        List<Tile> toAnnexOrdered = MasterFsmState.getCurrentMap().reorderTheseTilesByClosestTo(reference.getPosX(), reference.getPosY(), toAnnex);
        
        for (int i = 0; i < toAnnexOrdered.size(); i++) {
            Tile annexTile = toAnnexOrdered.get(i);
            Coords annex = annexTile.coords();
            
            if (reference.canReach(annex) && !annexTile.isOccupied) {
                return new Option(annex, PostMoveAction.Annex);
            } else {
                //it will assist only if any targeted tile is closer to the goal than previous
                AllegianceRecognizer assistLimiter = new AllegianceRecognizer() {
                    @Override
                    public boolean allows(TangibleUnit target) {
                        for (Integer range : reference.getFullAssistRange()) {
                            List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
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
                            List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
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
                    reference.movementTileFurthestOnPathTowards(annex).coords(),
                    data
                );
            }
        }
        
        return DefaultBehavior(data);
    }
    
    public Option attackTheseTiles(List<Tile> toOffend, Conveyer data) {
        //prioritize the closest one
        List<Tile> toOffendOrdered = MasterFsmState.getCurrentMap().reorderTheseTilesByClosestTo(reference.getPosX(), reference.getPosY(), toOffend);
        
        for (Tile offendTile : toOffendOrdered) {
            Coords offend = offendTile.coords();
            
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
                        List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
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
                            List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
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
                    reference.movementTileFurthestOnPathTowards(offend).coords(),
                    data
                );
        }
        
        return DefaultBehavior(data);
    }
    
    public Option protectTheseTiles(List<Tile> toProtect, Conveyer data) {
        //prioritize the closest one
        List<Tile> toProtectOrdered = MasterFsmState.getCurrentMap().reorderTheseTilesByClosestTo(reference.getPosX(), reference.getPosY(), toProtect);
        
        for (int i = 0; i < toProtectOrdered.size(); i++) {
            Tile payload = toProtectOrdered.get(i);
            
            if (reference.canReach(payload.getPosX(), payload.getPosY())) { //tile they want to protect is in their range
                if (!payload.isOccupied) {
                    //become a meat shield
                    return new Option(new Coords(payload.getPosX(), payload.getPosY()));
                } else {
                    //assist is top priority
                    AllegianceRecognizer assistLimiter = new AllegianceRecognizer() {
                        @Override
                        public boolean allows(TangibleUnit target) {
                            for (Integer range : reference.getFullAssistRange()) {
                                List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
                                if (ableSquares.stream().anyMatch((square) -> ( (reference.canReach(square.getX(), square.getY()) && reference.venture().addMobility(1).setCoords(square).willReach(payload.coords()) ) ))) {
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
                                List<Coords> ableSquares = VenturePeek.coordsForTilesOfRange(range, target.coords(), target.getElevation());
                                if (ableSquares.stream().anyMatch((square) -> ( (reference.canReach(square.getX(), square.getY()) && reference.venture().addMobility(1).setCoords(square).willReach(payload.coords()) ) ))) {
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
                    Coords phalanx = surround(payload.coords(), 1, reference.getElevation());
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
                
                return mostFavorableMiscOption(reference.movementTileFurthestOnPathTowards(payload.coords()).coords(), data);
            }
        }
        
        return null;
    }
    
    public Option mostFavorableMiscOption(Coords pos, Conveyer data) {
        Ability bestAbility = null;
        Formation bestFormation = null;
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
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Formation)) {
                for (Formation frmn : opciones.getUsableFormations()) {
                    if (frmn.getToolType() == ToolType.SupportSelf) {
                        if (bestFormation == null || frmn.getMostDesiredTechnique(data).calculateDesirability(data) > bestFormation.getMostDesiredTechnique(data).calculateDesirability(data)) {
                            bestFormation = frmn;
                        }
                    }
                }
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
        Coords bestCoords = null;
        Associated bestOption = null;
        Purpose purpose = null;
        PostMoveAction action = null;
        
        if (bestAbility != null && bestAbility.getDesirability(data) > highestPriority) {
            highestPriority = bestAbility.getDesirability(data);
            bestOption = bestAbility;
            purpose = Purpose.None;
            action = PostMoveAction.Ability;
        }
        
        if (bestFormation != null && bestFormation.getMostDesiredTechnique(data).calculateDesirability(data) > highestPriority) {
            highestPriority = bestFormation.getMostDesiredTechnique(data).calculateDesirability(data);
            bestOption = bestFormation;
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
    
    public Option mostFavorableOffensiveOption(TangibleUnit enemy, Conveyer data) {
        PrebattleForecast bestEtherForecast = null;
        Formula bestFormula = null;
        Coords bestFormulaCoords = null;
        
        PrebattleForecast bestWeaponForecast = null;
        Weapon bestWeapon = null;
        Coords bestWeaponCoords = null;
        
        PrebattleForecast bestSkillForecast = null;
        Skill bestSkill = null;
        Coords bestSkillCoords = null;
        
        Ability bestAbility = null;
        Coords bestAbilityCoords = null;
        
        Formation bestFormation = null;
        Coords bestFormationCoords = null;
        
        Item bestItem = null;
        Coords bestItemCoords = null;
        
        for (Coords spot : reference.allowedCoordsFromTarget(enemy.coords(), true)) {
            int range = spot.getRange();
            ActionInfo opciones = reference.determineOptions(spot, data);
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Ether)) {
                for (Formula fma : opciones.getUsableFormulas()) {
                    if (!fma.getFormulaPurpose().isSupportive() && fma.getActualFormulaData().getRange().contains(range)) {
                        reference.equip(fma);
                        PrebattleForecast forecast = PrebattleForecast.createForecast(new Conveyer(reference).setEnemyUnit(enemy), Purpose.EtherAttack, range);
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
                        PrebattleForecast forecast = PrebattleForecast.createForecast(new Conveyer(reference).setEnemyUnit(enemy), Purpose.WeaponAttack, range);
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
                        PrebattleForecast forecast = PrebattleForecast.createForecast(new Conveyer(reference).setEnemyUnit(enemy), Purpose.SkillAttack, range);
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
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Formation)) {
                for (Formation frmn : opciones.getUsableFormations()) {
                    if (frmn.getToolType() == ToolType.Attack && frmn.getRange().contains(range)) {
                        if (bestFormation == null || frmn.getMostDesiredTechnique(data).calculateDesirability(data) > bestFormation.getMostDesiredTechnique(data).calculateDesirability(data)) {
                            bestFormation = frmn;
                            bestFormationCoords = spot;
                        }
                    }
                }
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
        Coords bestCoords = null;
        Associated bestOption = null;
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
        
        if (bestFormation != null && bestFormation.getMostDesiredTechnique(data).calculateDesirability(data) > highestPriority) {
            highestPriority = bestFormation.getMostDesiredTechnique(data).calculateDesirability(data);
            bestCoords = bestFormationCoords;
            bestOption = bestFormation;
            purpose = Purpose.None;
            action = PostMoveAction.Formation;
        }
        
        if (bestItem != null && bestItem.getItemEffect().getDesirability() > highestPriority) {
            bestCoords = bestItemCoords;
            bestOption = bestItem;
            purpose = Purpose.None;
            action = PostMoveAction.Item;
        }
        
        int range = Math.abs(bestCoords.getX() - enemy.getPosX()) + Math.abs(bestCoords.getY() - enemy.getPosY());
        
        return new Option(action, purpose, bestOption, bestCoords, range, enemy).setPriority(highestPriority);
    }
    
    public Option mostFavorableAssistOption(TangibleUnit ally, Conveyer data) {
        SupportForecast bestEtherForecast = null;
        Formula bestFormula = null;
        Coords bestFormulaCoords = null;
        
        Ability bestAbility = null;
        Coords bestAbilityCoords = null;
        
        Formation bestFormation = null;
        Coords bestFormationCoords = null;
        
        Item bestItem = null;
        Coords bestItemCoords = null;
        
        for (Coords spot : reference.allowedCoordsFromTarget(ally.coords(), false)) { //calculate the most favorable range here too
            int range = spot.getRange();
            ActionInfo opciones = reference.determineOptions(spot, data);
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Ether)) {
                for (Formula fma : opciones.getUsableFormulas()) {
                    if (fma.getFormulaPurpose().isSupportive() && fma.getActualFormulaData().getRange().contains(range)) {
                        reference.equip(fma);
                        SupportForecast forecast = new SupportForecast(new Conveyer(reference).setOtherUnit(ally), range);
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
            
            if (opciones.getAvailableActions().contains(PostMoveAction.Formation)) {
                for (Formation frmn : opciones.getUsableFormations()) {
                    if (frmn.getToolType() == ToolType.SupportAlly && frmn.getRange().contains(range)) {
                        if (bestFormation == null || frmn.getMostDesiredTechnique(data).calculateDesirability(data) > bestFormation.getMostDesiredTechnique(data).calculateDesirability(data)) {
                            bestFormation = frmn;
                            bestFormationCoords = spot;
                        }
                    }
                }
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
        Coords bestCoords = null;
        Associated bestOption = null;
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
        
        if (bestFormation != null && bestFormation.getMostDesiredTechnique(data).calculateDesirability(data) > highestPriority) {
            highestPriority = bestFormation.getMostDesiredTechnique(data).calculateDesirability(data);
            bestCoords = bestFormationCoords;
            bestOption = bestFormation;
            purpose = Purpose.None;
            action = PostMoveAction.Formation;
        }
        
        if (bestItem != null && bestItem.getItemEffect().getDesirability() > highestPriority) {
            bestCoords = bestItemCoords;
            bestOption = bestItem;
            purpose = Purpose.None;
            action = PostMoveAction.Item;
        }
        
        int range = Math.abs(bestCoords.getX() - ally.getPosX()) + Math.abs(bestCoords.getY() - ally.getPosY());
        
        return new Option(action, purpose, bestOption, bestCoords, range, ally).setPriority(highestPriority);
    }
    
    /*public int favorabilityToAssist(TangibleUnit ally) {
        return (int)(100 - (100 * ally.currentHP / ((float)ally.getStat(BaseStat.maxHP))));
    }*/
    
    public Option calculateHighestPriorityAssistOption(Conveyer conv, AllegianceRecognizer condition) {
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
    
    public Option calculateHighestPriorityOffensiveOption(Conveyer conv, AllegianceRecognizer condition) {
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
    
    public Option calculateHighestPriorityOffensiveOptionNotRegardingDistance(Conveyer conv) {
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
    public boolean condition(Conveyer data);
}
    
interface AIBehavior {
    public Option action(Conveyer data);
}
