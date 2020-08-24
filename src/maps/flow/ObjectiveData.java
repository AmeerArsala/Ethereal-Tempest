/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.flow;

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
    public String[] getX_BossesEscapeForDefeat() { return defeatCondition.X_Bosses_Escape; }
    public String[] getX_EnemiesEscapeForDefeat() { return defeatCondition.X_Enemies_Escape; }
    
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
        if (defeatCondition.X_Bosses_Escape == null) { defeatCondition.X_Bosses_Escape = spouse.getX_BossesEscapeForDefeat(); }
        if (defeatCondition.X_Enemies_Escape == null) { defeatCondition.X_Enemies_Escape = spouse.getX_EnemiesEscapeForDefeat(); }
        
        return this;
    }  
    
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
    
    public ObjectiveData setX_BossesEscapeForDefeat(String[] O) {
        defeatCondition.X_Bosses_Escape = O;
        return this;
    }
    
    public ObjectiveData setX_EnemiesEscapeForDefeat(String[] O) {
        defeatCondition.X_Enemies_Escape = O;
        return this;
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
        private String[] X_Bosses_Escape = null;
        private String[] X_Enemies_Escape = null;
        
        public DefeatCondition() {}
        
        public DefeatCondition(
                boolean leaderDies, boolean anyPlayerCharacterDies, boolean anyPlayerCommanderDies, 
                boolean anyAnnexableTileAnnexed, boolean allAnnexableTilesAnnexed,
                Integer X_Number_Of_Player_Characters_Die, Integer X_Number_Of_Any_Characters_Die, Integer X_Number_Of_Allies_Die,
                Integer X_Number_Of_Entities_Die,
                Integer X_Number_Of_Turns_Pass,
                String[] X_Characters_Die, String[] X_Entities_Die,
                String[] X_Bosses_Escape, String[] X_Enemies_Escape
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
            this.X_Bosses_Escape = X_Bosses_Escape;
            this.X_Enemies_Escape = X_Enemies_Escape;
        }
        
    }
    
    
}
