/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.characters;

import etherealtempest.info.Conveyer;
import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import fundamental.stats.Toll;
import fundamental.item.Item;
import fundamental.item.Weapon;
import fundamental.ability.Ability;
import fundamental.formation.Formation;
import fundamental.formula.Formula;
import fundamental.item.Inventory;
import fundamental.skill.Skill;
import fundamental.talent.Talent;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.StatType;
import fundamental.tool.DamageTool;
import fundamental.stats.StatBundle;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition;
import fundamental.tool.Tool;
import fundamental.tool.Tool.ToolType;
import general.GeneralUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class Unit extends JobClass {
    public static final int MAX_LEVEL = 99;
    
    //DISCLAIMER: maxTP (the BaseStat enum) does not actually refer to the Unit's maximum tp stat, but rather an accumulation of the amount of points it has grown through growth rate increases, the rest of the stat is calculated by other stats
    public static final ArrayList<BaseStat> baseStats = StatBundle.createBaseStats();
    public static final HashMap<BaseStat, Integer> DEFAULT_ENEMY_GROWTH_RATES = StatCanvas(100);

    public Unit(boolean isBoss, String jobname, List<String> mobilityTypes, List<String> wieldableWeaponTypes, HashMap<BaseStat, Integer> bonusStats, HashMap<BattleStat, Integer> battleBonus, HashMap<BaseStat, Integer> maxStats, String desc, int tier) {
        super(jobname, mobilityTypes, wieldableWeaponTypes, bonusStats, battleBonus, maxStats, desc, tier);
        this.isBoss = isBoss;
    }
    
    public int currentEXP = 0;
    protected int currentHP, currentTP;
    
    private HashMap<BaseStat, Integer> stats = new HashMap<>();
    private HashMap<BaseStat, Integer> personal_growth_rates = new HashMap<>();
    
    private Inventory inventory; //7 items max in inventory
    private List<Formula> formulas; //15 formulas max
    private List<Talent> talents; //6 max
    private List<Skill> skills; //5 max
    private List<Ability> abilities; //7 max
    private List<Formation> formations; //triangle, circle, square, diamond, or none?
    
    protected Formula equippedFormula = null;
    protected Weapon equippedWeapon = null;
    
    private List<Bonus> bonuses = new ArrayList<>();
    
    private final boolean isBoss;
    
    public Unit(String name, JobClass jc, List<StatBundle> baseStatPackage, List<StatBundle> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), name, jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.getClassDescription(), jc.clTier());
        stats = StatBundle.createBaseStatsFromBundles(baseStatPackage);
        personal_growth_rates = StatBundle.createBaseStatsFromBundles(growthRatePackage);
        
        this.inventory = new Inventory(inventory);
        this.formulas = formulas;
        this.talents = talents;
        this.abilities = abilities;
        this.skills = skills;
        this.formations = formations;
        this.isBoss = isBoss;
        
        currentHP = stats.get(BaseStat.maxHP) + ClassStatBonus().get(BaseStat.maxHP);
        currentTP = ((stats.get(BaseStat.maxHP) + ((stats.get(BaseStat.ether) + stats.get(BaseStat.resilience)) * 2)) / 2) + ClassStatBonus().get(BaseStat.maxTP); //MAXTP = (MAXHP + ((ETHER + RSL) * 2)) / 2
        
        customSkillAnimations = jc.getCustomSkillAnimations();
        autoEquip();
    }
    
    public Unit(String name, JobClass jc, HashMap<BaseStat, Integer> baseStatPackage, List<StatBundle> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), name, jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.getClassDescription(), jc.clTier());
        stats = baseStatPackage;
        personal_growth_rates = StatBundle.createBaseStatsFromBundles(growthRatePackage);
        
        this.inventory = new Inventory(inventory);
        this.formulas = formulas;
        this.talents = talents;
        this.abilities = abilities;
        this.skills = skills;
        this.formations = formations;
        this.isBoss = isBoss;
        
        currentHP = stats.get(BaseStat.maxHP) + ClassStatBonus().get(BaseStat.maxHP);
        currentTP = ((stats.get(BaseStat.maxHP) + ((stats.get(BaseStat.ether) + stats.get(BaseStat.resilience)) * 2)) / 2) + ClassStatBonus().get(BaseStat.maxTP); //MAXTP = (MAXHP + ((ETHER + RSL) * 2)) / 2
        
        customSkillAnimations = jc.getCustomSkillAnimations();
        autoEquip();
    }
    public Unit(String name, JobClass jc, List<StatBundle> baseStatPackage, HashMap<BaseStat, Integer> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), name, jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.getClassDescription(), jc.clTier());
        stats = StatBundle.createBaseStatsFromBundles(baseStatPackage);
        personal_growth_rates = growthRatePackage;
        
        this.inventory = new Inventory(inventory);
        this.formulas = formulas;
        this.talents = talents;
        this.abilities = abilities;
        this.skills = skills;
        this.formations = formations;
        this.isBoss = isBoss;
        
        currentHP = stats.get(BaseStat.maxHP) + ClassStatBonus().get(BaseStat.maxHP);
        currentTP = ((stats.get(BaseStat.maxHP) + ((stats.get(BaseStat.ether) + stats.get(BaseStat.resilience)) * 2)) / 2) + ClassStatBonus().get(BaseStat.maxTP); //MAXTP = (MAXHP + ((ETHER + RSL) * 2)) / 2
        
        customSkillAnimations = jc.getCustomSkillAnimations();
        autoEquip();
    }
    public Unit(String name, JobClass jc, HashMap<BaseStat, Integer> baseStatPackage, HashMap<BaseStat, Integer> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), name, jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.getClassDescription(), jc.clTier());
        stats = baseStatPackage;
        personal_growth_rates = growthRatePackage;
        
        this.inventory = new Inventory(inventory);
        this.formulas = formulas;
        this.talents = talents;
        this.abilities = abilities;
        this.skills = skills;
        this.formations = formations;
        this.isBoss = isBoss;
        
        currentHP = stats.get(BaseStat.maxHP) + ClassStatBonus().get(BaseStat.maxHP);
        currentTP = ((stats.get(BaseStat.maxHP) + ((stats.get(BaseStat.ether) + stats.get(BaseStat.resilience)) * 2)) / 2) + ClassStatBonus().get(BaseStat.maxTP); //MAXTP = (MAXHP + ((ETHER + RSL) * 2)) / 2
        
        customSkillAnimations = jc.getCustomSkillAnimations();
        autoEquip();
    }
    
    public boolean getIsBoss() { return isBoss; }
    
    public Inventory getInventory() { return inventory; }
    public List<Formula> getFormulas() { return formulas; }
    public List<Formation> getFormations() { return formations; }
    public List<Talent> getIndividualTalents() { return talents; }
    
    public List<Talent> getTalents() {
        List<Talent> full = new ArrayList<>();
        full.addAll(talents);
        
        Formation forma = equippedFormation();
        Tool tool = getEquippedTool();
        
        if (forma != null && forma.getBonusEffects() != null && forma.getBonusEffects().getBonusTalent() != null) {
            full.add(forma.getBonusEffects().getBonusTalent());
        }
        
        if (tool != null && tool.getOnEquipSkill() != null) {
            full.add(tool.getOnEquipTalent());
        }
        
        return full;
    }
    
    public List<Skill> getSkills() {
        List<Skill> full = new ArrayList<>();
        full.addAll(skills);
        
        Formation forma = equippedFormation();
        Tool tool = getEquippedTool();
        
        if (forma != null && forma.getBonusEffects() != null && forma.getBonusEffects().getBonusSkill() != null) {
            full.add(forma.getBonusEffects().getBonusSkill());
        }
        
        if (tool != null && tool.getOnEquipSkill() != null) {
            full.add(tool.getOnEquipSkill());
        }
        
        return full;
    }
    
    public List<Ability> getAbilities() {
        List<Ability> full = new ArrayList<>();
        full.addAll(abilities);
        
        Formation forma = equippedFormation();
        Tool tool = getEquippedTool();
        
        if (forma != null && forma.getBonusEffects() != null && forma.getBonusEffects().getBonusAbility() != null) {
            full.add(forma.getBonusEffects().getBonusAbility());
        }
        
        if (tool != null && tool.getOnEquipSkill() != null) {
            full.add(tool.getOnEquipAbility());
        }
        
        return full;
    }
    
    public HashMap<BaseStat, Integer> getStats() { return stats; }
    public HashMap<BaseStat, Integer> getGrowthRates() { return personal_growth_rates; }
    
    public List<Bonus> getBonuses() { return bonuses; }
    
    public void addBonus(Bonus B) {
        bonuses.add(B);
        Bonus.organizeList(bonuses);
    }
    
    public void setGrowthRates(HashMap<BaseStat, Integer> rates) { personal_growth_rates = rates; }
    
    public void setGrowthRate(int amt, BaseStat stat) {
        personal_growth_rates.replace(stat, amt);
    }
    
    public void setStat(BaseStat statname, int amount) {
        switch (statname) {
            case currentHP:
                currentHP = amount;
                break;
            case currentTP:
                currentTP = amount;
                break;
            default:
                stats.replace(statname, amount);
                break;
        }
    }
    
    public Integer getStat(BaseStat statname) {
        if (statname == BaseStat.currentHP) { return currentHP; }
        
        return statname == BaseStat.currentTP ? currentTP : stats.get(statname); 
    }
    
    public int simulateTP(int hpextra, int etherextra, int rslextra) {
        return (((hpextra + stats.get(BaseStat.maxHP)) + (((etherextra + stats.get(BaseStat.ether)) + (rslextra + stats.get(BaseStat.resilience))) * 2)) / 2) - calculateTPBody();
    }
    
    private int calculateTPBody() {
        return ((stats.get(BaseStat.maxHP) + ((stats.get(BaseStat.ether) + stats.get(BaseStat.resilience)) * 2)) / 2);
    }
    
    public int getLVL() { return stats.get(BaseStat.level); }
    public int getMaxHP() { return stats.get(BaseStat.maxHP) + ClassStatBonus().get(BaseStat.maxHP) + getTotalBonus(BaseStat.maxHP); }
    public int getMaxTP() { return stats.get(BaseStat.maxTP) +  calculateTPBody() + ClassStatBonus().get(BaseStat.maxTP) + getTotalBonus(BaseStat.maxTP); }
    public int getSTR() { return stats.get(BaseStat.strength) + ClassStatBonus().get(BaseStat.strength) + getTotalBonus(BaseStat.strength); }
    public int getETHER() { return stats.get(BaseStat.ether) + ClassStatBonus().get(BaseStat.ether) + getTotalBonus(BaseStat.ether); }
    public int getAGI() { return stats.get(BaseStat.agility) + ClassStatBonus().get(BaseStat.agility) + getTotalBonus(BaseStat.agility); }
    public int getDEX() { return stats.get(BaseStat.dexterity) + ClassStatBonus().get(BaseStat.dexterity) + getTotalBonus(BaseStat.dexterity); }
    public int getCOMP() { return stats.get(BaseStat.comprehension) + ClassStatBonus().get(BaseStat.comprehension) + getTotalBonus(BaseStat.comprehension); } //basically luck
    public int getDEF() { return stats.get(BaseStat.defense) + ClassStatBonus().get(BaseStat.defense) + getTotalBonus(BaseStat.defense); }
    public int getRSL() { return stats.get(BaseStat.resilience) + ClassStatBonus().get(BaseStat.resilience) + getTotalBonus(BaseStat.resilience); }
    public int getMOBILITY() { return stats.get(BaseStat.mobility) + ClassStatBonus().get(BaseStat.mobility) + getTotalBonus(BaseStat.mobility); }
    public int getPHYSIQUE() { return stats.get(BaseStat.physique) + ClassStatBonus().get(BaseStat.physique) + getTotalBonus(BaseStat.physique); }
    public int getADRENALINE() { return stats.get(BaseStat.adrenaline) + ClassStatBonus().get(BaseStat.adrenaline) + getTotalBonus(BaseStat.adrenaline); }
    
    public int[] getAllBaseStats() { //except hp
        return new int[] {getLVL(), getSTR(), getETHER(), getAGI(), getCOMP(), getDEX(), getDEF(), getRSL(), getMOBILITY(), getPHYSIQUE(), getADRENALINE()};
    }
    
    public List<StatBundle> getRawBaseStats() { //except hp and tp
        return Arrays.asList(
            new StatBundle(BaseStat.level, stats.get(BaseStat.level)), //level
            new StatBundle(BaseStat.strength, stats.get(BaseStat.strength) + ClassStatBonus().get(BaseStat.strength)), //str
            new StatBundle(BaseStat.ether, stats.get(BaseStat.ether) + ClassStatBonus().get(BaseStat.ether)), //ether
            new StatBundle(BaseStat.agility, stats.get(BaseStat.agility) +  + ClassStatBonus().get(BaseStat.agility)), //agi
            new StatBundle(BaseStat.comprehension, stats.get(BaseStat.comprehension) + ClassStatBonus().get(BaseStat.comprehension)), //comp
            new StatBundle(BaseStat.dexterity, stats.get(BaseStat.dexterity) + ClassStatBonus().get(BaseStat.dexterity)), //dex
            new StatBundle(BaseStat.defense, stats.get(BaseStat.defense) + ClassStatBonus().get(BaseStat.defense)), //def
            new StatBundle(BaseStat.resilience, stats.get(BaseStat.resilience) + ClassStatBonus().get(BaseStat.resilience)), //rsl
            new StatBundle(BaseStat.mobility, stats.get(BaseStat.mobility) + ClassStatBonus().get(BaseStat.mobility)), //mobility
            new StatBundle(BaseStat.physique, stats.get(BaseStat.physique) + ClassStatBonus().get(BaseStat.physique)), //physique
            new StatBundle(BaseStat.adrenaline, stats.get(BaseStat.adrenaline) + ClassStatBonus().get(BaseStat.adrenaline)) //adrenaline
        );
    }
    
    public int[] getAllRawBonuses() {
        return new int[] {
            getTotalBonus(BaseStat.strength),      //str
            getTotalBonus(BaseStat.ether),         //ether
            getTotalBonus(BaseStat.agility),       //agi
            getTotalBonus(BaseStat.comprehension), //comp
            getTotalBonus(BaseStat.dexterity),     //dex
            getTotalBonus(BaseStat.defense),       //def
            getTotalBonus(BaseStat.resilience),    //rsl
            getTotalBonus(BaseStat.mobility),      //mobility
            getTotalBonus(BaseStat.physique),      //physique
            getTotalBonus(BaseStat.adrenaline)     //adrenaline
        };
    }
    
    public int getTotalBonus(BaseStat stat) { //for talent bonuses, on the enactEffect(), it will add to the Unit's bonus list
        int bonusSum = getEquippedTool() != null ? getEquippedTool().getTotalBonus(stat) : 0;
        for (Bonus bonus : bonuses) {
            if (bonus.getBaseStat() == stat) {
                bonusSum += bonus.getValue();
            }
        }
        
        Formation equipped = equippedFormation();
        if (
                equipped != null 
                && equipped.getBonusEffects().getStatBonus() != null 
                && equipped.getBonusEffects().getStatBonus().getStatType() == StatType.Base 
                && equipped.getBonusEffects().getStatBonus().getWhichBaseStat()  == stat
            ) 
        {
            return bonusSum + equipped.getBonusEffects().getStatBonus().getValue();
        }
        
        return bonusSum;
    }
    
    public int getTotalBonus(BattleStat stat) {
        int bonusSum = getEquippedTool() != null ? getEquippedTool().getTotalBonus(stat) : 0;
        for (Bonus bonus : bonuses) {
            if (bonus.getBattleStat() == stat) {
                bonusSum += bonus.getValue();
            }
        }
        
        Formation equipped = equippedFormation();
        if (
                equipped != null 
                && equipped.getBonusEffects().getStatBonus() != null 
                && equipped.getBonusEffects().getStatBonus().getStatType() == StatType.Battle
                && equipped.getBonusEffects().getStatBonus().getWhichBattleStat()  == stat
            ) 
        {
            return bonusSum + equipped.getBonusEffects().getStatBonus().getValue();
        }
        
        return bonusSum;
    }
    
    //Battle Stats
    public int getATK() {
        if (equippedFormula != null && equippedFormula.getOffensiveFormulaData() != null) {
            return getETHER() + equippedFormula.getOffensiveFormulaData().getPow() + getTotalBonus(BattleStat.AttackPower);
        }
        
        return (equippedWeapon != null ? getSTR() + equippedWeapon.getWeaponData().getPow() : getSTR()) + getTotalBonus(BattleStat.AttackPower);
    }
    
    public int getAccuracy() {
        if (getEquippedTool() != null) {
            return ((DamageTool)getEquippedTool()).getAcc() + (((getDEX() * 4) + getCOMP()) / 2) + ClassBattleBonus().get(BattleStat.Accuracy) + getTotalBonus(BattleStat.Accuracy);
        }
        return getTotalBonus(BattleStat.Accuracy); 
    } //add commander bonus
    
    public int getEvasion() { return (((getAGI() * 3) + getCOMP()) / 2) + ClassBattleBonus().get(BattleStat.Evasion) + getTotalBonus(BattleStat.Evasion); } //add terrain bonus
    
    public int getCrit() {
        if (getEquippedTool() != null) {
            return getEquippedTool().getCRIT() + (getDEX() / 2) + ClassBattleBonus().get(BattleStat.Crit) + getTotalBonus(BattleStat.Crit);
        }
        return 0;
    }
    
    public int getCritEvasion() { return getCOMP() + ClassBattleBonus().get(BattleStat.CritEvasion) + getTotalBonus(BattleStat.CritEvasion); }
    
    public int getAS() { return getAGI() + ClassBattleBonus().get(BattleStat.AttackSpeed) + getTotalBonus(BattleStat.AttackSpeed); } //FIX LATER
    
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
    
    public void restore(Toll type) {
        int value = type.getValue();
        switch (type.getType()) {
            case HP:
                if (currentHP + value > stats.get(BaseStat.maxHP)) {
                    currentHP = stats.get(BaseStat.maxHP);
                } else { currentHP += value; }
                break;
            case TP:
                if (currentTP + value > stats.get(BaseStat.maxTP)) {
                    currentTP = stats.get(BaseStat.maxTP);
                } else { currentTP += value; }
                break;
            case Durability: //only on the equipped weapon
                if (equippedWeapon != null) {
                    equippedWeapon.restoreUses(value);
                }
                break;
        }
    }
    
    public Formation equippedFormation() {
        return !formations.isEmpty() ? formations.get(0) : null;
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
        if (getInventory().getItems().get(0) instanceof Weapon) {
            return ((Weapon)getInventory().getItems().get(0)).getWeaponData();
        }
        return null;
    }
    
    public static HashMap<BaseStat, Integer> StatCanvas(int num) {
        HashMap<BaseStat, Integer> canvas = new HashMap<>();
        
        baseStats.forEach((based) -> {
            canvas.put(based, num);
        });
        
        return canvas;
    }
    
    public HashMap<BaseStat, Integer> rollLevelUp() {
        HashMap<BaseStat, Integer> roll = StatCanvas(0);
        
        baseStats.forEach((based) -> {
            int growth = personal_growth_rates.get(based) / 100;
            if (1 + (int)(100 * Math.random()) <= personal_growth_rates.get(based) % 100) {
                growth++;
            }
            
            roll.replace(based, roll.get(based) + growth);
        });
        
        roll.replace(BaseStat.maxTP, roll.get(BaseStat.maxTP) + simulateTP(roll.get(BaseStat.maxHP), roll.get(BaseStat.ether), roll.get(BaseStat.resilience)));
        
        return roll;
    }
    
    public void levelUp() {
        HashMap<BaseStat, Integer> levelRoll = rollLevelUp();
        baseStats.forEach((based) -> {
            stats.replace(based, stats.get(based) + levelRoll.get(based));
        });
    }
    
    public void levelUp(HashMap<BaseStat, Integer> additions) {
        baseStats.forEach((based) -> {
            stats.replace(based, stats.get(based) + additions.get(based));
        });
    }
    
    public boolean isWeaponUsable(String wp) { return UsableWeapons().contains(wp); } //wp is weapon type
    
    public boolean canCounterattackAgainst(int range) {
        return getEquippedTool() != null ? getEquippedTool().getRange().contains(range) : false;
    }
    
    public int getAmountExistingFormulas() {
        int count = 0;
        for (Formula formula : formulas) {
            if (formula.doesExist()) { count++; }
        }
        return count;
    }
    
    public int highestRange() {
        int max = 0;
        for (Item item : inventory.getItems()) {
            if (item instanceof Weapon) {
                int range = GeneralUtils.highestInt(((Weapon)item).getWeaponData().getRange());
                if (range > max) {
                    max = range;
                }
            }
        }
        
        for (Formula formula : formulas) {
            int range = GeneralUtils.highestInt(formula.getActualFormulaData().getRange());
            if (range > max) {
                max = range;
            }
        }
        
        return max;
    }
    
    public List<Integer> getFullRange() {
        List<Integer> fullRange = new ArrayList<>();
        
        for (Integer range : getFullOffensiveRange()) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : getFullSkillRange()) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        formulas.forEach((formula) -> {
            formula.getActualFormulaData().getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                fullRange.add(range);
            });
        });
        
        formations.forEach((formation) -> {
            formation.getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                fullRange.add(range);
            });
        });
        
        return fullRange;
    }
    
    public List<Integer> getFullOffensiveRange() {
        List<Integer> fullRange = new ArrayList<>();
        
        inventory.getItems().stream().filter((item) -> (item instanceof Weapon)).forEachOrdered((item) -> {
            ((Weapon)item).getWeaponData().getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                fullRange.add(range);
            });
        });
        
        for (Integer range : getPartialFormulaRange(false)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : getPartialFormationRange(false)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : getPartialSkillRange(false)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        return fullRange;
    }
    
    public List<Integer> getFullAssistRange() {
        List<Integer> fullRange = new ArrayList<>();
        
        for (Integer range : getPartialFormulaRange(true)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : getPartialFormationRange(true)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        for (Integer range : getPartialSkillRange(true)) {
            if (!fullRange.contains(range)) {
                fullRange.add(range);
            }
        }
        
        return fullRange;
    }
    
    public List<Integer> getPartialFormulaRange(boolean supportive) {
        List<Integer> fullRange = new ArrayList<>();
        
        formulas.forEach((formula) -> {
            if (formula.getFormulaPurpose().isSupportive() == supportive) {
                formula.getActualFormulaData().getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                    fullRange.add(range);
                });
            }
        });
        
        return fullRange;
    }
    
    public List<Integer> getPartialFormationRange(boolean supportive) {
        List<Integer> fullRange = new ArrayList<>();
        
        formations.forEach((formation) -> {
            if (formation.getToolType().isSupportive() == supportive) {
                formation.getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                    fullRange.add(range);
                });
            }
        });
        
        return fullRange;
    }
    
    public List<Integer> getPartialSkillRange(boolean supportive) {
        List<Integer> fullRange = new ArrayList<>();
        
        skills.forEach((skill) -> {
            if (skill.getType().isSupportive() == supportive) {
                skill.getEffect().getTrueRange(getEquippedTool()).stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                    fullRange.add(range);
                });
            }
        });
        
        return fullRange;
    }
    
    public List<Integer> getFullWeaponRange() {
        List<Integer> fullRange = new ArrayList<>();
        
        inventory.getItems().stream().filter((item) -> (item instanceof Weapon)).forEachOrdered((item) -> {
            ((Weapon)item).getWeaponData().getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                fullRange.add(range);
            });
        });
        
        return fullRange;
    }
    
    public List<Integer> getFullSkillRange() {
        List<Integer> fullRange = new ArrayList<>();
        
        skills.forEach((skill) -> {
            for (Integer range : skill.getEffect().getTrueRange(getEquippedTool())) {
                if (!fullRange.contains(range)) {
                    fullRange.add(range);
                }
            }
        });
        
        return fullRange;
    }
    
    public boolean hasSupportingFormulas() {
        return hasSelfSupportingFormulas() || hasAllySupportingFormulas();
    }
    
    public boolean hasSelfSupportingFormulas() {
        return formulas.stream().anyMatch((spell) -> (spell.getFormulaPurpose() == ToolType.SupportSelf));
    }
    
    public boolean hasAllySupportingFormulas() {
        return formulas.stream().anyMatch((spell) -> (spell.getFormulaPurpose() == ToolType.SupportAlly));
    }
    
    public boolean hasAttackingFormulas() {
        return formulas.stream().anyMatch((spell) -> (spell.getFormulaPurpose() == ToolType.Attack));
    }
    
    public boolean anyAbilityAllowed(Conveyer conv) {
        return abilities.stream().anyMatch((ability) -> (ability.canBeUsed(conv)));
    }
    
    @Override
    public String toString() { return name; }
}
