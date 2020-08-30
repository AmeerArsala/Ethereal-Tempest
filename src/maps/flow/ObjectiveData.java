/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

import battle.Conveyer;
import etherealtempest.FSM.EntityState;
import etherealtempest.MasterFsmState;
import java.util.ArrayList;
import maps.layout.Map;
import maps.layout.MapEntity;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.UnitStatus;
import maps.layout.Tile;

/**
 *
 * @author night
 */
public class ObjectiveData {
    private boolean killAllBosses = false;
    private boolean killAllEnemyUnits = false;
    private boolean escape = false; //have the Tile class have fields to show that they are escape tiles for player, enemy, ally unit, etc. depending on their allegiance
    private boolean annexAllAnnexableTiles = false;
    private boolean annexAnyAnnexableTile = false;
    private Integer numberOfEnemiesToKill = null;
    private Integer numberOfBossesToKill = null;
    private Integer numberOfUnitsToDie = null; //on any team
    private Integer turnsToPassForVictory = null;
    private String[] namesOfEntitiesToKill = null;
    private String[] namesOfBossesToKill = null;
    private DefeatCondition defeatCondition;
    
    public ObjectiveData() {
        defeatCondition = new DefeatCondition();
    }
    
    public ObjectiveData(
            boolean killAllBosses, boolean killAllEnemyUnits, boolean escape, boolean annexAllAnnexableTiles, boolean annexAnyAnnexableTile,
            Integer numberOfEnemiesToKill, Integer numberOfBossesToKill, Integer numberOfUnitsToDie, Integer turnsToPassForVictory,
            String[] namesOfEntitiesToKill, String[] namesOfBossesToKill, 
            DefeatCondition defeatCondition
            ) {
        this.killAllBosses = killAllBosses;
        this.killAllEnemyUnits = killAllEnemyUnits;
        this.escape = escape;
        this.annexAllAnnexableTiles = annexAllAnnexableTiles;
        this.annexAnyAnnexableTile = annexAnyAnnexableTile;
        this.numberOfEnemiesToKill = numberOfEnemiesToKill;
        this.numberOfBossesToKill = numberOfBossesToKill;
        this.numberOfUnitsToDie = numberOfUnitsToDie;
        this.turnsToPassForVictory = turnsToPassForVictory;
        this.namesOfEntitiesToKill = namesOfEntitiesToKill;
        this.namesOfBossesToKill = namesOfBossesToKill;
        this.defeatCondition = defeatCondition;
    }
    
    //victory conditions
    public boolean getKillAllBosses() { return killAllBosses; }
    public boolean getKillAllEnemyUnits() { return killAllEnemyUnits; }
    public boolean getEscape() { return escape; }
    public boolean getAnnexAllAnnexableTiles() { return annexAllAnnexableTiles; }
    public boolean getAnnexAnyAnnexableTile() { return annexAnyAnnexableTile; }
    public Integer getNumberOfEnemiesToKill() { return numberOfEnemiesToKill; }
    public Integer getNumberOfBossesToKill() { return numberOfBossesToKill; }
    public Integer getNumberOfUnitsToDie() { return numberOfUnitsToDie; }
    public Integer getTurnsToPassForVictory() { return turnsToPassForVictory; }
    public String[] getNamesOfEntitiesToKill() { return namesOfEntitiesToKill; }
    public String[] getNamesOfBossesToKill() { return namesOfBossesToKill; }
    
    //defeat conditions
    public boolean getLeaderDiesForDefeat() { return defeatCondition.leaderDies; }
    public boolean getAnyPlayerCharacterDiesForDefeat() { return defeatCondition.anyPlayerCharacterDies; }
    public boolean getAnyPlayerCommanderDiesForDefeat() { return defeatCondition.anyPlayerCommanderDies; }
    public boolean getAnyAnnexableTileAnnexedForDefeat() { return defeatCondition.anyAnnexableTileAnnexed; }
    public boolean getAllAnnexableTilesAnnexedForDefeat() { return defeatCondition.allAnnexableTilesAnnexed; }
    public Integer getX_NumberOfPlayerCharactersDieForDefeat() { return defeatCondition.X_Number_Of_Player_Characters_Die; }
    public Integer getX_NumberOfAnyCharactersDieForDefeat() { return defeatCondition.X_Number_Of_Any_Characters_Die; }
    public Integer getX_NumberOfAlliesDieForDefeat() { return defeatCondition.X_Number_Of_Allies_Die; }
    public Integer getX_NumberOfEntitiesDieForDefeat() { return defeatCondition.X_Number_Of_Entities_Die; }
    public Integer getX_NumberOfTurnsPassForDefeat() { return defeatCondition.X_Number_Of_Turns_Pass; }
    public String[] getX_CharactersDieForDefeat() { return defeatCondition.X_Characters_Die; }
    public String[] getX_EntitiesDieForDefeat() { return defeatCondition.X_Entities_Die; }
    public String[] getX_EnemiesEscapeForDefeat() { return defeatCondition.X_Enemies_Escape; }
    
    public ObjectiveData setKillAllBosses(boolean O) {
        killAllBosses = O;
        return this;
    }
    
    public ObjectiveData setKillAllEnemyUnits(boolean O) {
        killAllEnemyUnits = O;
        return this;
    }
    
    public ObjectiveData setEscape(boolean O) {
        escape = O;
        return this;
    }
    
    public ObjectiveData setAnnexAllAnnexableTiles(boolean O) {
        annexAllAnnexableTiles = O;
        return this;
    }
    
    public ObjectiveData setAnnexAnyAnnexableTile(boolean O) {
        annexAnyAnnexableTile = O;
        return this;
    }
    
    public ObjectiveData setNumberOfEnemiesToKill(Integer O) {
        numberOfEnemiesToKill = O;
        return this;
    }
    
    public ObjectiveData setNumberOfBossesToKill(Integer O) {
        numberOfBossesToKill = O;
        return this;
    }
    
    public ObjectiveData setNumberOfUnitsToDie(Integer O) {
        numberOfUnitsToDie = O;
        return this;
    }
    
    public ObjectiveData setTurnsToPassForVictory(Integer O) {
        turnsToPassForVictory = O;
        return this;
    }
    
    public ObjectiveData setNamesOfEntitiesToKill(String[] O) {
        namesOfEntitiesToKill = O;
        return this;
    }
    
    public ObjectiveData setNamesOfBossesToKill(String[] O) {
        namesOfBossesToKill = O;
        return this;
    }
    
    public ObjectiveData setLeaderDiesForDefeat(boolean O) {
        defeatCondition.leaderDies = O;
        return this;
    }
    
    public ObjectiveData setAnyPlayerCharacterDiesForDefeat(boolean O) {
        defeatCondition .anyPlayerCharacterDies = O;
        return this;
    }
    
    public ObjectiveData setAnyPlayerCommanderDiesForDefeat(boolean O) {
        defeatCondition.anyPlayerCommanderDies = O;
        return this;
    }
    
    public ObjectiveData setAnyAnnexableTileAnnexedForDefeat(boolean O) {
        defeatCondition.anyAnnexableTileAnnexed = O;
        return this;
    }
    
    public ObjectiveData setAllAnnexableTilesAnnexedForDefeat(boolean O) {
        defeatCondition.allAnnexableTilesAnnexed = O;
        return this;
    }
    
    public ObjectiveData setX_NumberOfPlayerCharactersDieForDefeat(Integer O) {
        defeatCondition.X_Number_Of_Player_Characters_Die = O;
        return this;
    }
    
    public ObjectiveData setX_NumberOfAnyCharactersDieForDefeat(Integer O) {
        defeatCondition.X_Number_Of_Any_Characters_Die = O;
        return this;
    }
    
    public ObjectiveData setX_NumberOfAlliesDieForDefeat(Integer O) {
        defeatCondition.X_Number_Of_Allies_Die = O;
        return this;
    }
    
    public ObjectiveData setX_NumberOfEntitiesDieForDefeat(Integer O) {
        defeatCondition.X_Number_Of_Entities_Die = O;
        return this;
    }
    
    public ObjectiveData setX_NumberOfTurnsPassForDefeat(Integer O) {
        defeatCondition.X_Number_Of_Turns_Pass = O;
        return this;
    }
    
    public ObjectiveData setX_CharactersDieForDefeat(String[] O) {
        defeatCondition.X_Characters_Die = O;
        return this;
    }
    
    public ObjectiveData setX_EntitiesDieForDefeat(String[] O) {
        defeatCondition.X_Entities_Die = O;
        return this;
    }
    
    public ObjectiveData setX_EnemiesEscapeForDefeat(String[] O) {
        defeatCondition.X_Enemies_Escape = O;
        return this;
    }
    
    public boolean killAllBossesMet(Conveyer data) {
        for (TangibleUnit unit : data.getAllUnits()) {
            if (unit.getIsBoss() && !unit.isAlliedWith(UnitStatus.Player) && unit.getFSM().getState().getEnum() != EntityState.Dead) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean killAllEnemyUnitsMet(Conveyer data) {
        for (TangibleUnit unit : data.getAllUnits()) {
            if (!unit.isAlliedWith(UnitStatus.Player) && unit.getFSM().getState().getEnum() != EntityState.Dead) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean escapeMet(Conveyer data) { //remove a unit every time they escape from the map, also the commanders have to escape last
        for (TangibleUnit unit : data.getAllUnits()) {
            if (unit.unitStatus == UnitStatus.Player && unit.getFSM().getState().getEnum() != EntityState.Dead && unit.getIsBoss()) {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean annexAllAnnexableTilesMet() {
        Map stage = MasterFsmState.getCurrentMap();
        for (int layer = 0; layer < stage.fullmap.length; layer++) {
            for (int x = 0; x < stage.fullmap[layer].length; x++) {
                for (int y = 0; y < stage.fullmap[layer][x].length; y++) {
                    if (stage.fullmap[layer][x][y].getTileData().getElegibleAllegiance() == UnitStatus.Player && !stage.fullmap[layer][x][y].hasBeenAnnexed()) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public boolean annexAnyAnnexableTileMet() {
        Map stage = MasterFsmState.getCurrentMap();
        for (int layer = 0; layer < stage.fullmap.length; layer++) {
            for (int x = 0; x < stage.fullmap[layer].length; x++) {
                for (int y = 0; y < stage.fullmap[layer][x].length; y++) {
                    if (stage.fullmap[layer][x][y].hasBeenAnnexedBy(UnitStatus.Player)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public boolean numberOfEnemiesToKillMet(Conveyer data) {
        int killed = 0;
        for (TangibleUnit unit : data.getAllUnits()) {
            if (!unit.isAlliedWith(UnitStatus.Player) && unit.getFSM().getState().getEnum() == EntityState.Dead) {
                killed++;
            }
        }
        
        return killed == numberOfEnemiesToKill || killed == numberOfEnemiesToKill.intValue();
    }
    
    public boolean numberOfBossesToKillMet(Conveyer data) {
        int killed = 0;
        for (TangibleUnit unit : data.getAllUnits()) {
            if (!unit.isAlliedWith(UnitStatus.Player) && unit.getIsBoss() && unit.getFSM().getState().getEnum() == EntityState.Dead) {
                killed++;
            }
        }
        
        return killed == numberOfBossesToKill || killed == numberOfBossesToKill.intValue();
    }
    
    public boolean numberOfUnitsToDieMet(Conveyer data) {
        int killed = 0;
        for (TangibleUnit unit : data.getAllUnits()) {
            if (unit.getFSM().getState().getEnum() == EntityState.Dead) {
                killed++;
            }
        }
        
        return killed == numberOfUnitsToDie || killed == numberOfUnitsToDie.intValue();
    }
    
    public boolean turnsToPassForVictoryMet(Conveyer data) {
        return data.getCurrentTurn() > turnsToPassForVictory || data.getCurrentTurn() > turnsToPassForVictory.intValue();
    }
    
    public boolean namesOfEntitiesToKillMet(Conveyer data) {
        for (TangibleUnit unit : data.getAllUnits()) {
            for (String name : namesOfEntitiesToKill) {
                if (unit.getName().equals(name) && unit.getFSM().getState().getEnum() != EntityState.Dead) {
                    return false;
                }
            }
        }
        
        for (MapEntity entity : data.getMapEntities()) {
            for (String name : namesOfEntitiesToKill) {
                if (entity.getName().equals(name) && entity.getFSM().getState().getEnum() != EntityState.Dead) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean namesOfBossesToKillMet(Conveyer data) {
        for (TangibleUnit unit : data.getAllUnits()) {
            if (unit.getIsBoss() && unit.getFSM().getState().getEnum() != EntityState.Dead) {
                for (String name : namesOfEntitiesToKill) {
                    if (unit.getName().equals(name)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public boolean victoryConditionsMet(Conveyer data) {
        if (killAllBosses && !killAllBossesMet(data)) { return false; }
        if (killAllEnemyUnits && !killAllEnemyUnitsMet(data)) { return false; }
        if (escape && !escapeMet(data)) { return false; }
        if (annexAllAnnexableTiles && !annexAllAnnexableTilesMet()) { return false; }
        if (annexAnyAnnexableTile && !annexAnyAnnexableTileMet()) { return false; }
        if (numberOfEnemiesToKill != null && !numberOfEnemiesToKillMet(data)) { return false; }
        if (numberOfBossesToKill != null && !numberOfBossesToKillMet(data)) { return false; }
        if (numberOfUnitsToDie != null && !numberOfUnitsToDieMet(data)) { return false; }
        if (turnsToPassForVictory != null && !turnsToPassForVictoryMet(data)) { return false; }
        if (namesOfEntitiesToKill != null && !namesOfEntitiesToKillMet(data)) { return false; }
        
        return !(namesOfBossesToKill != null && !namesOfBossesToKillMet(data));
    }
    
    public boolean lossConditionsMet(Conveyer data) {
        return defeatCondition.conditionsMet(data);
    }
    
    private class DefeatCondition {
        private boolean leaderDies = false;
        private boolean anyPlayerCharacterDies = false;
        private boolean anyPlayerCommanderDies = false;
        private boolean anyAnnexableTileAnnexed = false;
        private boolean allAnnexableTilesAnnexed = false;
        private Integer X_Number_Of_Player_Characters_Die = null;
        private Integer X_Number_Of_Any_Characters_Die = null; //no matter the allegiance
        private Integer X_Number_Of_Allies_Die = null;
        private Integer X_Number_Of_Entities_Die = null;
        private Integer X_Number_Of_Turns_Pass = null;
        private String[] X_Characters_Die = null; //can be player characters, enemy characters, ally characters, etc.
        private String[] X_Entities_Die = null;
        private String[] X_Enemies_Escape = null;
        
        public DefeatCondition() {}
        
        public DefeatCondition(
                boolean leaderDies, boolean anyPlayerCharacterDies, boolean anyPlayerCommanderDies, 
                boolean anyAnnexableTileAnnexed, boolean allAnnexableTilesAnnexed,
                Integer X_Number_Of_Player_Characters_Die, Integer X_Number_Of_Any_Characters_Die, Integer X_Number_Of_Allies_Die,
                Integer X_Number_Of_Entities_Die,
                Integer X_Number_Of_Turns_Pass,
                String[] X_Characters_Die, String[] X_Entities_Die,
                String[] X_Enemies_Escape
                ) {
            this.leaderDies = leaderDies;
            this.anyPlayerCharacterDies = anyPlayerCharacterDies;
            this.anyPlayerCommanderDies = anyPlayerCommanderDies;
            this.anyAnnexableTileAnnexed = anyAnnexableTileAnnexed;
            this.allAnnexableTilesAnnexed = allAnnexableTilesAnnexed;
            this.X_Number_Of_Player_Characters_Die = X_Number_Of_Player_Characters_Die;
            this.X_Number_Of_Any_Characters_Die = X_Number_Of_Any_Characters_Die;
            this.X_Number_Of_Allies_Die = X_Number_Of_Allies_Die;
            this.X_Number_Of_Entities_Die = X_Number_Of_Entities_Die;
            this.X_Number_Of_Turns_Pass = X_Number_Of_Turns_Pass;
            this.X_Characters_Die = X_Characters_Die;
            this.X_Entities_Die = X_Entities_Die;
            this.X_Enemies_Escape = X_Enemies_Escape;
        }
        
        public boolean leaderDiesMet(Conveyer data) {
            for (TangibleUnit unit : data.getAllUnits()) {
                if (unit.isAlliedWith(UnitStatus.Player) && unit.isLeader && unit.getFSM().getState().getEnum() == EntityState.Dead) {
                    return true;
                }
            }
            
            return false;
        }
        
        public boolean anyPlayerCharacterDiesMet(Conveyer data) {
            for (TangibleUnit unit : data.getAllUnits()) {
                if (unit.unitStatus == UnitStatus.Player && unit.getFSM().getState().getEnum() == EntityState.Dead) {
                    return true;
                }
            }
        
            return false;
        }
        
        public boolean anyPlayerCommanderDiesMet(Conveyer data) {
            for (TangibleUnit unit : data.getAllUnits()) {
                if (unit.getIsBoss() && unit.unitStatus == UnitStatus.Player && unit.getFSM().getState().getEnum() == EntityState.Dead) {
                    return true;
                }
            }
        
            return false;
        }
        
        public boolean anyAnnexableTileAnnexedMet() {
            Map stage = MasterFsmState.getCurrentMap();
            for (int layer = 0; layer < stage.fullmap.length; layer++) {
                for (int x = 0; x < stage.fullmap[layer].length; x++) {
                    for (int y = 0; y < stage.fullmap[layer][x].length; y++) {
                        if (stage.fullmap[layer][x][y].hasBeenAnnexedByEnemy()) {
                            return true;
                        }
                    }
                }
            }
        
            return false;
        }
        
        public boolean allAnnexableTilesAnnexedMet() {
            Map stage = MasterFsmState.getCurrentMap();
            for (int layer = 0; layer < stage.fullmap.length; layer++) {
                for (int x = 0; x < stage.fullmap[layer].length; x++) {
                    for (int y = 0; y < stage.fullmap[layer][x].length; y++) {
                        if (!stage.fullmap[layer][x][y].hasBeenAnnexedByEnemy()) {
                            return false;
                        }
                    }
                }
            }
        
            return true;
        }
        
        public boolean X_Number_Of_Player_Characters_DieMet(Conveyer data) {
            int dead = 0;
            for (TangibleUnit unit : data.getAllUnits()) {
                if (unit.unitStatus == UnitStatus.Player && unit.getFSM().getState().getEnum() == EntityState.Dead) {
                    dead++;
                }
            }
            
            return dead == X_Number_Of_Player_Characters_Die || dead == X_Number_Of_Player_Characters_Die.intValue();
        }
        
        public boolean X_Number_Of_Any_Characters_DieMet(Conveyer data) {
            int dead = 0;
            for (TangibleUnit unit : data.getAllUnits()) {
                if (unit.getFSM().getState().getEnum() == EntityState.Dead) {
                    dead++;
                }
            }
            
            return dead == X_Number_Of_Any_Characters_Die || dead == X_Number_Of_Any_Characters_Die.intValue();
        }
        
        public boolean X_Number_Of_Allies_DieMet(Conveyer data) {
            int dead = 0;
            for (TangibleUnit unit : data.getAllUnits()) {
                if (unit.unitStatus == UnitStatus.Ally && unit.getFSM().getState().getEnum() == EntityState.Dead) {
                    dead++;
                }
            }
            
            return dead == X_Number_Of_Allies_Die || dead == X_Number_Of_Allies_Die.intValue();
        }
        
        public boolean X_Number_Of_Entities_DieMet(Conveyer data) {
            int dead = 0;
            for (TangibleUnit unit : data.getAllUnits()) {
                if (unit.getFSM().getState().getEnum() == EntityState.Dead) {
                    dead++;
                }
            }
            
            for (MapEntity entity : data.getMapEntities()) {
                if (entity.getFSM().getState().getEnum() == EntityState.Dead) {
                    dead++;
                }
            }
            
            return dead == X_Number_Of_Entities_Die || dead == X_Number_Of_Entities_Die.intValue();
        }
        
        public boolean X_Number_Of_Turns_PassMet(Conveyer data) { return data.getCurrentTurn() > X_Number_Of_Turns_Pass; }
        
        public boolean X_Characters_DieMet(Conveyer data) {
            for (TangibleUnit unit : data.getAllUnits()) {
                for (String name : X_Characters_Die) {
                    if (unit.getName().equals(name) && unit.getFSM().getState().getEnum() != EntityState.Dead) {
                        return false;
                    }
                }
            }
            
            return true;
        }
        
        public boolean X_Entities_DieMet(Conveyer data) {
            for (MapEntity entity : data.getMapEntities()) {
                for (String name : X_Entities_Die) {
                    if (entity.getName().equals(name) && entity.getFSM().getState().getEnum() != EntityState.Dead) {
                        return false;
                    }
                }
            }
            
            for (TangibleUnit unit : data.getAllUnits()) {
                for (String name : X_Characters_Die) {
                    if (unit.getName().equals(name) && unit.getFSM().getState().getEnum() != EntityState.Dead) {
                        return false;
                    }
                }
            }
            
            return true;
        }
        
        public boolean X_Enemies_EscapeMet(Conveyer data) {
            for (TangibleUnit unit : data.getAllUnits()) {
                if (!unit.isAlliedWith(UnitStatus.Player)) {
                    for (String name : X_Enemies_Escape) {
                        if (unit.getName().equals(name)) {
                            return false;
                        } 
                    }
                }
            }
            
            return true;
        }
        
        public boolean conditionsMet(Conveyer data) {
            if (leaderDies && !leaderDiesMet(data)) { return false; }
            if (anyPlayerCharacterDies && !anyPlayerCharacterDiesMet(data)) { return false; }
            if (anyPlayerCommanderDies && !anyPlayerCommanderDiesMet(data)) { return false; }
            if (anyAnnexableTileAnnexed && !anyAnnexableTileAnnexedMet()) { return false; }
            if (allAnnexableTilesAnnexed && !allAnnexableTilesAnnexedMet()) { return false; }
            if (X_Number_Of_Player_Characters_Die != null && !X_Number_Of_Player_Characters_DieMet(data)) { return false; }
            if (X_Number_Of_Any_Characters_Die != null && !X_Number_Of_Any_Characters_DieMet(data)) { return false; }
            if (X_Number_Of_Allies_Die != null && !X_Number_Of_Allies_DieMet(data)) { return false; }
            if (X_Number_Of_Entities_Die != null && !X_Number_Of_Entities_DieMet(data)) { return false; }
            if (X_Number_Of_Turns_Pass != null && !X_Number_Of_Turns_PassMet(data)) { return false; }
            if (X_Characters_Die != null && !X_Characters_DieMet(data)) { return false; }
            if (X_Entities_Die != null && !X_Entities_DieMet(data)) { return false; }
            
            return !(X_Enemies_Escape != null && !X_Enemies_EscapeMet(data));
        }
    }
    
    public ObjectiveData marry(ObjectiveData spouse) {
        //marry victory conditions
        if (!killAllBosses) { killAllBosses = spouse.getKillAllBosses(); }
        if (!killAllEnemyUnits) { killAllEnemyUnits = spouse.getKillAllEnemyUnits(); }
        if (!escape) { escape = spouse.getEscape(); }
        if (!annexAllAnnexableTiles) { annexAllAnnexableTiles = spouse.getAnnexAllAnnexableTiles(); }
        if (!annexAnyAnnexableTile) { annexAnyAnnexableTile = spouse.getAnnexAnyAnnexableTile(); }
        if (numberOfEnemiesToKill == null) { numberOfEnemiesToKill = spouse.getNumberOfEnemiesToKill(); }
        if (numberOfBossesToKill == null) { numberOfBossesToKill = spouse.getNumberOfBossesToKill(); }
        if (numberOfUnitsToDie == null) { numberOfUnitsToDie = spouse.getNumberOfUnitsToDie(); }
        if (turnsToPassForVictory == null) { turnsToPassForVictory = spouse.getTurnsToPassForVictory(); }
        if (namesOfEntitiesToKill == null) { namesOfEntitiesToKill = spouse.getNamesOfEntitiesToKill(); }
        if (namesOfBossesToKill == null) { namesOfBossesToKill = spouse.getNamesOfBossesToKill(); }
        
        //marry defeat conditions
        if (!defeatCondition.leaderDies) { defeatCondition.leaderDies = spouse.getLeaderDiesForDefeat(); }
        if (!defeatCondition.anyPlayerCharacterDies) { defeatCondition.anyPlayerCharacterDies = spouse.getAnyPlayerCharacterDiesForDefeat(); }
        if (!defeatCondition.anyPlayerCommanderDies) { defeatCondition.anyPlayerCommanderDies = spouse.getAnyPlayerCommanderDiesForDefeat(); }
        if (!defeatCondition.anyAnnexableTileAnnexed) { defeatCondition.anyAnnexableTileAnnexed = spouse.getAnyAnnexableTileAnnexedForDefeat(); }
        if (!defeatCondition.allAnnexableTilesAnnexed) { defeatCondition.allAnnexableTilesAnnexed = spouse.getAllAnnexableTilesAnnexedForDefeat(); }
        if (defeatCondition.X_Number_Of_Player_Characters_Die == null) { defeatCondition.X_Number_Of_Player_Characters_Die = spouse.getX_NumberOfPlayerCharactersDieForDefeat(); }
        if (defeatCondition.X_Number_Of_Any_Characters_Die == null) { defeatCondition.X_Number_Of_Any_Characters_Die = spouse.getX_NumberOfAnyCharactersDieForDefeat(); }
        if (defeatCondition.X_Number_Of_Allies_Die == null) { defeatCondition.X_Number_Of_Allies_Die = spouse.getX_NumberOfAlliesDieForDefeat(); }
        if (defeatCondition.X_Number_Of_Entities_Die == null) { defeatCondition.X_Number_Of_Entities_Die = spouse.getX_NumberOfEntitiesDieForDefeat(); }
        if (defeatCondition.X_Number_Of_Turns_Pass == null) { defeatCondition.X_Number_Of_Turns_Pass = spouse.getX_NumberOfTurnsPassForDefeat(); }
        if (defeatCondition.X_Characters_Die == null) { defeatCondition.X_Characters_Die = spouse.getX_CharactersDieForDefeat(); }
        if (defeatCondition.X_Entities_Die == null) { defeatCondition.X_Entities_Die = spouse.getX_EntitiesDieForDefeat(); }
        if (defeatCondition.X_Enemies_Escape == null) { defeatCondition.X_Enemies_Escape = spouse.getX_EnemiesEscapeForDefeat(); }
        
        return this;
    }
    
}
