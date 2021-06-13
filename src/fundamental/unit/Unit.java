/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.unit;

import fundamental.jobclass.JobClass;
import etherealtempest.info.Conveyor;
import fundamental.Entity;
import fundamental.stats.Toll;
import fundamental.item.Item;
import fundamental.item.weapon.Weapon;
import fundamental.ability.Ability;
import fundamental.ability.AbilityManager;
import fundamental.formation.Formation;
import fundamental.formation.FormationManager;
import fundamental.formula.Formula;
import fundamental.formula.FormulaManager;
import fundamental.item.Inventory;
import fundamental.skill.Skill;
import fundamental.skill.SkillManager;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.talent.Talent;
import fundamental.stats.BonusHolder;
import fundamental.tool.DamageTool;
import fundamental.stats.StatBundle;
import fundamental.talent.TalentManager;
import fundamental.tool.Tool;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class Unit extends Entity {
    public static final int MAX_LEVEL = 99;
    
    //DISCLAIMER: maxTP (the BaseStat enum) does not actually refer to the Unit's maximum tp stat, but rather an accumulation of the amount of points it has grown through growth rate increases, the rest of the stat is calculated by other stats
    public static final HashMap<BaseStat, Integer> DEFAULT_ENEMY_GROWTH_RATES() { return StatBundle.BaseStatCanvas(100); }
    public static final List<StatBundle<BaseStat>> DEFAULT_ENEMY_GROWTH_RATES_LIST() { return StatBundle.uniformBaseStats(100); }
    
    protected JobClass jobclass;
    protected UnitStatus status = UnitStatus.HEALTHY; //status ailments, etc.
    protected UnitAllegiance allegiance;
    
    public int currentEXP = 0;
    protected int currentHP, currentTP;
    
    private final HashMap<BaseStat, Integer> stats, personalGrowthRates;
    
    protected final Inventory inventory; 
    protected final FormulaManager formulaManager;
    protected final TalentManager talentManager;
    protected final SkillManager skillManager;
    protected final AbilityManager abilityManager;
    protected final FormationManager formationManager;
    
    protected Formula equippedFormula = null;
    protected Weapon equippedWeapon = null;

    protected final BonusHolder tempBonuses = new BonusHolder(); //NOT RAW BONUSES
    
    public Unit(String name, JobClass jc, List<StatBundle<BaseStat>> baseStatPackage, List<StatBundle<BaseStat>> growthRatePackage, List<Item> items, List<Formula> formulas, List<Talent> talents, List<Skill> skills, List<Ability> abilities, List<Formation> formations) {
        super(name);
        jobclass = jc;
        stats = StatBundle.createBaseStatsFromBundles(baseStatPackage);
        personalGrowthRates = StatBundle.createBaseStatsFromBundles(growthRatePackage);
        
        inventory = new Inventory(items); // 10 items max in inventory
        formulaManager = new FormulaManager(formulas, 15); // 15 formulas max
        talentManager = new TalentManager(talents); // 6 max
        skillManager = new SkillManager(skills, 5); // 5 max
        abilityManager = new AbilityManager(abilities, 7); // 7 max
        formationManager = new FormationManager(formations);
        
        finishInitialization();
    }
    
    //copies fields
    public Unit(String name, JobClass jobclass, HashMap<BaseStat, Integer> stats, HashMap<BaseStat, Integer> personalGrowthRates, Inventory inventory, FormulaManager formulaManager, TalentManager talentManager, SkillManager skillManager, AbilityManager abilityManager, FormationManager formationManager) {
        super(name);
        this.jobclass = jobclass;
        this.stats = stats;
        this.personalGrowthRates = personalGrowthRates;
        this.inventory = inventory;
        this.formulaManager = formulaManager;
        this.talentManager = talentManager;
        this.skillManager = skillManager;
        this.abilityManager = abilityManager;
        this.formationManager = formationManager;
        
        finishInitialization();
    }
    
    private void finishInitialization() {
        currentHP = stats.get(BaseStat.MaxHP) + jobclass.getBaseStatBonuses().get(BaseStat.MaxHP);
        currentTP = ((stats.get(BaseStat.MaxHP) + ((stats.get(BaseStat.Ether) + stats.get(BaseStat.Resilience)) * 2)) / 2) + jobclass.getBaseStatBonuses().get(BaseStat.MaxTP); //MAXTP = (MAXHP + ((ETHER + RSL) * 2)) / 2
        
        autoEquip();
    }
    
    public JobClass getJobClass() { return jobclass; }
    public UnitStatus getStatus() { return status; }
    public UnitAllegiance getAllegiance() { return allegiance; }
    
    public boolean isAlliedWith(UnitAllegiance other) { return allegiance.alliedWith(other); }
    public boolean isAlliedWith(Unit otherUnit) { return allegiance.alliedWith(otherUnit.allegiance); }
    
    public void setJobClass(JobClass jc) {
        jobclass = jc;
    }
    
    public void setStatus(UnitStatus unitStatus) {
        status = unitStatus;
    }
    
    public void setAllegiance(UnitAllegiance loyalty) {
        allegiance = loyalty;
    }
    
    //Items, Formulas, and Formations extend FreelyAssociated
    public Inventory getInventory() { return inventory; }
    public FormulaManager getFormulaManager() { return formulaManager; }
    public FormationManager getFormationManager() { return formationManager; }
    public List<Formation> getFormations() { return formationManager.getAll(); }
    public Formation equippedFormation() { return formationManager.getEquippedFormation(); };
    
    //Talents, Skills, and Abilities extend Associated
    public TalentManager getTalentManager() { return talentManager; }
    public SkillManager getSkillManager() { return skillManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public List<Talent> getIndividualTalents() { return talentManager.getEquipped(); }
    public List<Skill> getIndividualSkills() { return skillManager.getEquipped(); }
    public List<Ability> getIndividualAbilities() { return abilityManager.getEquipped(); }
    
    public List<Talent> getTalents() { 
        List<Talent> talents = talentManager.getTalents(getEquippedTool(), formationManager.getEquippedFormation());
        
        talents.addAll(inventory.getPassiveTalents());
        talents.addAll(formulaManager.getPassiveTalents());

        return talents;
    }
    
    public List<Skill> getSkills() { 
        List<Skill> skills = skillManager.getSkills(getEquippedTool(), formationManager.getEquippedFormation()); 
        
        skills.addAll(inventory.getPassiveSkills());
        skills.addAll(formulaManager.getPassiveSkills());
        
        return skills;
    }
    
    public List<Ability> getAbilities() { 
        List<Ability> abilities = abilityManager.getAbilities(getEquippedTool(), formationManager.getEquippedFormation()); 
        
        abilities.addAll(inventory.getPassiveAbilities());
        abilities.addAll(formulaManager.getPassiveAbilities());
        
        return abilities;
    }
    
    public Weapon getEquippedWPN() { return equippedWeapon; }
    public Formula getEquippedFormula() { return equippedFormula; }
    
    public Tool getEquippedTool() { 
        if (equippedFormula != null) {
            return equippedFormula.getActualFormulaData();
        }
        
        if (equippedWeapon != null) {
            return equippedWeapon.getWeaponData();
        }
        
        if (inventory.getItems().get(0) instanceof Weapon) {
            return ((Weapon)inventory.getFirstItem()).getWeaponData();
        }
        
        return null;
    }
    
    public void equip(Weapon W) {
        equippedWeapon = W;
        equippedFormula = null;
    }
    
    public void equip(Formula F) {
        equippedFormula = F;
        equippedWeapon = null;
    }
    
    public final void autoEquip() {
        if (!inventory.getItems().isEmpty() && inventory.getItems().get(0) instanceof Weapon) {
            equip((Weapon)inventory.getItems().get(0));
        }
    }
    
    public HashMap<BaseStat, Integer> getRawStats() { return stats; }
    public HashMap<BaseStat, Integer> getGrowthRates() { return personalGrowthRates; }
    
    public BonusHolder getTempBonuses() { return tempBonuses; }
    
    public void setGrowthRate(BaseStat stat, int growth) {
        personalGrowthRates.replace(stat, growth);
    }
    
    public void setRawStat(BaseStat statname, int amount) {
        switch (statname) {
            case CurrentHP:
                currentHP = amount;
                break;
            case CurrentTP:
                currentTP = amount;
                break;
            default:
                stats.replace(statname, amount);
                break;
        }
    }
    
    public int getRawStat(BaseStat statname) {
        if (statname == BaseStat.CurrentHP) { return currentHP; }
        
        return statname == BaseStat.CurrentTP ? currentTP : stats.get(statname); 
    }
    
    public int getBaseStat(BaseStat statName) {
        switch (statName) {
            case CurrentHP:
                return currentHP;
            case CurrentTP:
                return currentTP;
            case Level:
                return stats.get(BaseStat.Level);
            case MaxTP:
                return stats.get(BaseStat.MaxTP) +  calculateTPBody() + getTotalBaseStatBonus(BaseStat.MaxTP);
            case Mobility:
                return getMOBILITY();
            case Adrenaline:
                return getADRENALINE();
            default:
                return stats.get(statName) + getTotalBaseStatBonus(statName);
        }
    }
    
    public float getCurrentToMaxHPratio() {
        return ((float)currentHP) / getMaxHP();
    }
    
    public float getCurrentToMaxTPratio() {
        return ((float)currentTP) / getMaxTP();
    }
    
    public int simulateTP(int hpextra, int etherextra, int rslextra) {
        return (((hpextra + stats.get(BaseStat.MaxHP)) + (((etherextra + stats.get(BaseStat.Ether)) + (rslextra + stats.get(BaseStat.Resilience))) * 2)) / 2) - calculateTPBody();
    }
    
    private int calculateTPBody() {
        return ((stats.get(BaseStat.MaxHP) + ((stats.get(BaseStat.Ether) + stats.get(BaseStat.Resilience)) * 2)) / 2);
    }
    
    //Base Stats
    //TODO: maybe change the purpose of AGI, COMP, DEX, PHYSIQUE, and ADRENALINE
    public int getLVL() { return stats.get(BaseStat.Level); }
    public int getMaxHP() { return stats.get(BaseStat.MaxHP) + getTotalBaseStatBonus(BaseStat.MaxHP); }
    public int getMaxTP() { return stats.get(BaseStat.MaxTP) +  calculateTPBody() + getTotalBaseStatBonus(BaseStat.MaxTP); }
    public int getSTR() { return stats.get(BaseStat.Strength) + getTotalBaseStatBonus(BaseStat.Strength); }
    public int getETHER() { return stats.get(BaseStat.Ether) + getTotalBaseStatBonus(BaseStat.Ether); }
    public int getAGI() { return stats.get(BaseStat.Agility) + getTotalBaseStatBonus(BaseStat.Agility); }
    public int getDEX() { return stats.get(BaseStat.Dexterity) + getTotalBaseStatBonus(BaseStat.Dexterity); }
    public int getCOMP() { return stats.get(BaseStat.Comprehension) + getTotalBaseStatBonus(BaseStat.Comprehension); } //basically luck
    public int getDEF() { return stats.get(BaseStat.Defense) + getTotalBaseStatBonus(BaseStat.Defense); }
    public int getRSL() { return stats.get(BaseStat.Resilience) + getTotalBaseStatBonus(BaseStat.Resilience); }
    public int getMOBILITY() { return stats.get(BaseStat.Mobility) + getTotalBaseStatBonus(BaseStat.Mobility); }
    public int getPHYSIQUE() { return stats.get(BaseStat.Physique) + getTotalBaseStatBonus(BaseStat.Physique); }
    public int getADRENALINE() { return stats.get(BaseStat.Adrenaline) + getTotalBaseStatBonus(BaseStat.Adrenaline); }
    
    //Battle Stats
    //TODO: maybe change the purpose of AttackSpeed
    public int getATK() {
        int totalBonus = getTotalBattleStatBonus(BattleStat.AttackPower);
        
        if (equippedFormula != null && equippedFormula.getOffensiveFormulaData() != null) {
            return getETHER() + equippedFormula.getOffensiveFormulaData().getPow() + totalBonus;
        }
        
        return (equippedWeapon != null ? getSTR() + equippedWeapon.getWeaponData().getPow() : getSTR()) + totalBonus;
    }
    
    public int getAccuracy() { //TODO: add commander bonus
        int totalBonus = getTotalBattleStatBonus(BattleStat.Accuracy);
        if (getEquippedTool() != null) {
            return ((DamageTool)getEquippedTool()).getAcc() + (((getDEX() * 4) + getCOMP()) / 2) + totalBonus;
        }
        
        return totalBonus; 
    }
    
    public int getEvasion() {
        return (((getAGI() * 3) + getCOMP()) / 2) + getTotalBattleStatBonus(BattleStat.Evasion);
    } 
    
    public int getCrit() {
        int totalBonus = getTotalBattleStatBonus(BattleStat.Crit);
        if (getEquippedTool() != null) {
            return getEquippedTool().getCRIT() + (getDEX() / 2) + totalBonus;
        }
        
        return totalBonus;
    }
    
    public int getCritEvasion() { 
        return getCOMP() + getTotalBattleStatBonus(BattleStat.CritEvasion); 
    }
    
    public int getAS() {  //FIX LATER
        return getAGI() + getTotalBattleStatBonus(BattleStat.AttackSpeed);
    }
    
    public int getTotalBaseStatBonus(BaseStat stat) {
        return jobclass.getBaseStatBonuses().get(stat) + tempBonuses.getTotal(stat);
    }
    
    public int getTotalBattleStatBonus(BattleStat stat) {
        return jobclass.getBattleStatBonuses().get(stat) + tempBonuses.getTotal(stat);
    }
    
    public void addToll(Toll type) {
        int value = type.getValue();
        switch (type.getType()) {
            case HP:
                if (currentHP + value > stats.get(BaseStat.MaxHP)) {
                    currentHP = stats.get(BaseStat.MaxHP);
                } else { currentHP += value; }
                break;
            case TP:
                if (currentTP + value > stats.get(BaseStat.MaxTP)) {
                    currentTP = stats.get(BaseStat.MaxTP);
                } else { currentTP += value; }
                break;
            case Durability: //only on the equipped weapon
                if (equippedWeapon != null) {
                    equippedWeapon.addCurrentDurability(value);
                }
                break;
        }
    }
    
    public HashMap<BaseStat, Integer> rollLevelUp() {
        HashMap<BaseStat, Integer> roll = StatBundle.BaseStatCanvas(0); //create an empty one filled with 0's
        
        for (BaseStat based : BaseStat.values()) {
            int growth = personalGrowthRates.get(based) / 100;
            int remainderGrowth = personalGrowthRates.get(based) - (growth * 100);
            if ((int)(100 * Math.random()) <= remainderGrowth) {
                growth++;
            }
            
            roll.replace(based, roll.get(based) + growth);
        }
        
        roll.replace(BaseStat.MaxTP, roll.get(BaseStat.MaxTP) + simulateTP(roll.get(BaseStat.MaxHP), roll.get(BaseStat.Ether), roll.get(BaseStat.Resilience)));
        
        return roll;
    }
    
    public void levelUp() {
        HashMap<BaseStat, Integer> levelRoll = rollLevelUp();
        for (BaseStat based : BaseStat.values()) {
            stats.replace(based, stats.get(based) + levelRoll.get(based));
        }
    }
    
    public void levelUp(HashMap<BaseStat, Integer> additions) { //manual level up
        for (BaseStat based : BaseStat.values()) {
            stats.replace(based, stats.get(based) + additions.get(based));
        }
    }
    
    /**
     *
     * @return whether it leveled up or not
     */
    public boolean attemptLevelUp() {
        if (currentEXP < 100) {
            return false;
        }
        
        levelUp();
        currentEXP -= 100;
        return true;
    }
    
    /**
     *
     * @param additions stat additions
     * @return whether it leveled up
     */
    public boolean attemptLevelUp(HashMap<BaseStat, Integer> additions) { //manual level up
        if (currentEXP < 100) {
            return false;
        }
        
        levelUp(additions);
        currentEXP -= 100;
        return true;
    }
    
    public boolean canCounterattackAgainst(int range) {
        return getEquippedTool() != null ? getEquippedTool().getRange().contains(range) : false;
    }
    
    public List<Integer> getFullRange() {
        List<Integer> fullRange = getFullOffensiveRange();
        
        for (Integer range : getFullAssistRange()) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        return fullRange;
    }
    
    public List<Integer> getFullOffensiveRange() {
        List<Integer> fullRange = inventory.getFullWeaponRange();
        
        for (Integer range : formulaManager.getPartialFormulaRange(false)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : formationManager.getPartialFormationRange(false)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : skillManager.getPartialSkillRange(getEquippedTool(), formationManager.getEquippedFormation(), false)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        return fullRange;
    }
    
    public List<Integer> getFullAssistRange() {
        List<Integer> fullRange = formulaManager.getPartialFormulaRange(true);
        
        for (Integer range : formationManager.getPartialFormationRange(true)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : skillManager.getPartialSkillRange(getEquippedTool(), formationManager.getEquippedFormation(), true)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        return fullRange;
    }
    
    public boolean anyAbilityAllowed(Conveyor conv) {
        return abilityManager.anyAbilityAllowed(getEquippedTool(), formationManager.getEquippedFormation(), conv);
    }
    
    @Override
    public String toString() { return name; }
}