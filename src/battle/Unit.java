/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import battle.item.Item;
import battle.item.Weapon;
import battle.ability.Ability;
import battle.formation.Formation;
import battle.formula.Formula;
import battle.item.Inventory;
import battle.skill.Skill;
import battle.talent.PassiveTalent;
import battle.talent.Talent;
import fundamental.StatBundle;
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

    public int currentEXP = 0;
    public int currentHP, currentTP;

    protected String name = "";
    
    private HashMap<BaseStat, Integer> stats = new HashMap<>();
    private HashMap<BaseStat, Integer> personal_growth_rates = new HashMap<>();
    
    private Inventory inventory; //7 items max in inventory
    private List<Formula> formulas; //15 formulas max
    private List<Talent> talents; //6 max
    private List<Ability> abilities; //7 max
    private List<Skill> skills; //5 max
    private List<Formation> formations; //triangle, circle, square, diamond, or none?
    
    private final boolean isBoss;
    
    public Unit(String name, JobClass jc, List<StatBundle> baseStatPackage, List<StatBundle> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.clTier());
        stats = StatBundle.createBaseStatsFromBundles(baseStatPackage);
        personal_growth_rates = StatBundle.createBaseStatsFromBundles(growthRatePackage);
        
        this.name = name;
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
    }
    
    public Unit(String name, JobClass jc, HashMap<BaseStat, Integer> baseStatPackage, List<StatBundle> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.clTier());
        stats = baseStatPackage;
        personal_growth_rates = StatBundle.createBaseStatsFromBundles(growthRatePackage);
        
        this.name = name;
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
    }
    public Unit(String name, JobClass jc, List<StatBundle> baseStatPackage, HashMap<BaseStat, Integer> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.clTier());
        stats = StatBundle.createBaseStatsFromBundles(baseStatPackage);
        personal_growth_rates = growthRatePackage;
        
        this.name = name;
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
    }
    public Unit(String name, JobClass jc, HashMap<BaseStat, Integer> baseStatPackage, HashMap<BaseStat, Integer> growthRatePackage, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), jc.MovementType(), jc.UsableWeapons(), jc.ClassStatBonus(), jc.ClassBattleBonus(), jc.ClassMaxStats(), jc.clTier());
        stats = baseStatPackage;
        personal_growth_rates = growthRatePackage;
        
        this.name = name;
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
    }
    
    
    public String getName() { return name; }
    public boolean getIsBoss() { return isBoss; }
    
    public Inventory getInventory() { return inventory; }
    public List<Formula> getFormulas() { return formulas; }
    public List<Skill> getSkills() { return skills; }
    public List<Talent> getTalents() { return talents; }
    public List<Ability> getAbilities() { return abilities; }
    public List<Formation> getFormations() { return formations; }
    
    public HashMap<BaseStat, Integer> getStats() { return stats; }
    public HashMap<BaseStat, Integer> getGrowthRates() { return personal_growth_rates; }
    
    public void setGrowthRates(HashMap<BaseStat, Integer> rates) { personal_growth_rates = rates; }
    public void setGrowthRate(int amt, BaseStat stat) {
        personal_growth_rates.replace(stat, amt);
    }
    
    public void setStat(BaseStat statname, int amount) {
        stats.replace(statname, amount);
    }
    
    public int simulateTP(int hpextra, int etherextra, int rslextra) {
        return (((hpextra + stats.get(BaseStat.maxHP)) + (((etherextra + stats.get(BaseStat.ether)) + (rslextra + stats.get(BaseStat.resilience))) * 2)) / 2) - calculateTPBody();
    }
    
    private int calculateTPBody() {
        return ((stats.get(BaseStat.maxHP) + ((stats.get(BaseStat.ether) + stats.get(BaseStat.resilience)) * 2)) / 2);
    }
    
    public int getLVL() { return stats.get(BaseStat.level); }
    public int getMaxHP() { return stats.get(BaseStat.maxHP) + ClassStatBonus().get(BaseStat.maxHP); }
    public int getMaxTP() { return stats.get(BaseStat.maxTP) +  calculateTPBody() + ClassStatBonus().get(BaseStat.maxTP); }
    public int getSTR() { return stats.get(BaseStat.strength) + ClassStatBonus().get(BaseStat.strength) + getEquippedWeapon().getBonuses()[0]; }
    public int getETHER() { return stats.get(BaseStat.ether) + ClassStatBonus().get(BaseStat.ether) + getEquippedWeapon().getBonuses()[1]; }
    public int getAGI() { return stats.get(BaseStat.agility) + ClassStatBonus().get(BaseStat.agility) + getEquippedWeapon().getBonuses()[2]; }
    public int getDEX() { return stats.get(BaseStat.dexterity) + ClassStatBonus().get(BaseStat.dexterity) + getEquippedWeapon().getBonuses()[3]; }
    public int getCOMP() { return stats.get(BaseStat.comprehension) + ClassStatBonus().get(BaseStat.comprehension) + getEquippedWeapon().getBonuses()[4]; } //basically luck
    public int getDEF() { return stats.get(BaseStat.defense) + ClassStatBonus().get(BaseStat.defense) + getEquippedWeapon().getBonuses()[5]; }
    public int getRSL() { return stats.get(BaseStat.resilience) + ClassStatBonus().get(BaseStat.resilience) + getEquippedWeapon().getBonuses()[6]; }
    public int getMOBILITY() { return stats.get(BaseStat.mobility) + ClassStatBonus().get(BaseStat.mobility); }
    public int getPHYSIQUE() { return stats.get(BaseStat.physique) + ClassStatBonus().get(BaseStat.physique); }
    public int getADRENALINE() { return stats.get(BaseStat.adrenaline) + ClassStatBonus().get(BaseStat.adrenaline); }
    
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
    
    public int[] getTalentRawBonuses() {
        //     {str, ether, agi, comp, dex, def, rsl, mobility, physique, charisma}
        int[] bn = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int in = 0; in < talents.size(); in++) {
            if (talents.get(in) instanceof PassiveTalent) {
                if (((PassiveTalent)talents.get(in)).getTalentBody().getTalentCondition().getCondition()) {
                    for (int k = 0; k < 10; k++) {
                        if (((PassiveTalent)talents.get(in)).getTalentBody().getTalentEffect().rawBonusStats() != null) {
                            bn[k] += ((PassiveTalent)talents.get(in)).getTalentBody().getTalentEffect().rawBonusStats()[k];
                        }
                    }
                }
            }
        }
        return bn;
    }
    
    public int[] getAllRawBonuses() {
        return new int[] { 
            getEquippedWeapon().getBonuses()[0] + getTalentRawBonuses()[0], //str
            getEquippedWeapon().getBonuses()[1] + getTalentRawBonuses()[1], //ether
            getEquippedWeapon().getBonuses()[2] + getTalentRawBonuses()[2], //agi
            getEquippedWeapon().getBonuses()[4] + getTalentRawBonuses()[3], //comp
            getEquippedWeapon().getBonuses()[3] + getTalentRawBonuses()[4], //dex
            getEquippedWeapon().getBonuses()[5] + getTalentRawBonuses()[5], //def
            getEquippedWeapon().getBonuses()[6] + getTalentRawBonuses()[6], //rsl
            getTalentRawBonuses()[7],   //mobility
            getTalentRawBonuses()[8],   //physique
            getTalentRawBonuses()[9]   //charisma
        };
    }
    
    //Battle Stats
    public int getAccuracy() { return getEquippedWeapon().getStatus() ? (getEquippedWeapon().getAcc() + (((getDEX() * 4) + getCOMP()) / 2) + ClassBattleBonus().get(BattleStat.Accuracy)) : 0; } //add commander bonus
    public int getAvoid() { return ((((getAGI() * 3) + getCOMP()) / 2) + ClassBattleBonus().get(BattleStat.Evasion)); } //add terrain bonus
    public int getAS() { return (getEquippedWeapon().getStatus() ? (getAGI() - ((getEquippedWeapon().getWeight() - getPHYSIQUE() >= 0 ? getEquippedWeapon().getWeight() - getPHYSIQUE() : 0))) : getAGI()) + ClassBattleBonus().get(BattleStat.AttackSpeed); } //FIX LATER
    public int getCrit() { return getEquippedWeapon().getStatus() ? (getEquippedWeapon().getCRIT() + (getDEX() / 2) + ClassBattleBonus().get(BattleStat.Crit)) : 0; }
    public int getCritEvasion() { return getCOMP() + ClassBattleBonus().get(BattleStat.CritEvasion); }
    public int getATK() {
        if (getEquippedWeapon().getStatus()) {
            return ((getEquippedWeapon().getDmgType().equals("ether") ? (getEquippedWeapon().getPow() + getETHER()) : (getEquippedWeapon().getPow() + getSTR()))) + ClassBattleBonus().get(BattleStat.AttackPower);
        }
        return getSTR() + ClassBattleBonus().get(BattleStat.AttackPower);
    }
    
    public Formation getEquippedFormation() { return formations.get(0); }
    
    public Weapon getEquippedWeapon() { 
        if (inventory.getItems().get(0) instanceof Weapon) {
            return (Weapon)inventory.getItems().get(0);
        }
        return new Weapon(false); //if weapon type is equal to empty slot, unit can't attack
    }
    
    private static HashMap<BaseStat, Integer> StatCanvas(int num) {
        HashMap<BaseStat, Integer> canvas = new HashMap<>();
        
        baseStats.forEach((based) -> {
            canvas.put(based, num);
        });
        
        return canvas;
    }
    
    public HashMap<BaseStat, Integer> rollLevelUp() {
        HashMap<BaseStat, Integer> roll = StatCanvas(0);
        
        baseStats.forEach((based) -> {
            for (int t = 0; t < (1 + (personal_growth_rates.get(based)/100)); t++) {
                if (1 + (int)(100 * Math.random()) <= personal_growth_rates.get(based)) { roll.replace(based, roll.get(based) + 1); }
            }
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
    
    public int[] formationBonus(Unit enemy) {
        int[] bn = {0, 0, 0, 0, 0, 0, 0, 0};
        switch (getEquippedFormation().getFormationType()) {
            case "triangle":
                if (enemy.getEquippedFormation().getFormationType().equals("rectangle")) {
                    bn[4] = (int)(getEquippedFormation().formationCoefficient() * (getAGI()));
                    return bn;
                }   if (enemy.getEquippedFormation().getFormationType().equals("circle")) {
                    bn[4] = (int)(-1 * getEquippedFormation().formationCoefficient() * (getAGI()));
                    return bn;
                }   break;
            case "rectangle":
                if (enemy.getEquippedFormation().getFormationType().equals("circle")) {
                    bn[0] = (int)(getEquippedFormation().formationCoefficient() * 100);
                    bn[1] = (int)(getEquippedFormation().formationCoefficient() * 100);
                    return bn;
                }   if (enemy.getEquippedFormation().getFormationType().equals("triangle")) {
                    bn[0] = (int)(getEquippedFormation().formationCoefficient() * -100);
                    bn[1] = (int)(getEquippedFormation().formationCoefficient() * -100);
                    return bn;
                }   break;
            case "circle":
                if (enemy.getEquippedFormation().getFormationType().equals("triangle")) {
                    bn[3] = 3;
                    bn[6] = (int)(getEquippedFormation().formationCoefficient() * getDEF());
                    bn[7] = (int)(getEquippedFormation().formationCoefficient() * getRSL());
                    return bn;
                }   if (enemy.getEquippedFormation().getFormationType().equals("rectangle")) {
                    bn[3] = -3;
                    bn[6] = (int)(-1 * getEquippedFormation().formationCoefficient() * getDEF());
                    bn[7] = (int)(-1 * getEquippedFormation().formationCoefficient() * getRSL());
                    return bn;
                }   break;
            default:
                break;
        }
        
        if (getEquippedFormation().getFormationType().equals("diamond")) { for (int i = 0; i < 4; i++) { bn[i] = 3; } }
        
        return bn;
    }
    
    public boolean canCounterattackAgainst(int range) {
        return getEquippedWeapon().getStatus() ? getEquippedWeapon().getRange()[range] : false;
    }
    
    @Override
    public String toString() { return name; }
    
    public String portraitString = "";
    
    public int getAmountExistingFormulas() {
        int count = 0;
        for (Formula formula : formulas) {
            if (formula.getExistence()) { count++; }
        }
        return count;
    }
    
}
