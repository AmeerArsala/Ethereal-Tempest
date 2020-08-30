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
import fundamental.Bonus;
import fundamental.DamageTool;
import fundamental.FreelyAssociated;
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

    protected final String name;
    
    private HashMap<BaseStat, Integer> stats = new HashMap<>();
    private HashMap<BaseStat, Integer> personal_growth_rates = new HashMap<>();
    
    private Inventory inventory; //7 items max in inventory
    private List<Formula> formulas; //15 formulas max
    private List<Talent> talents; //6 max
    private List<Ability> abilities; //7 max
    private List<Skill> skills; //5 max
    private List<Formation> formations; //triangle, circle, square, diamond, or none?
    
    protected Formula equippedFormula = null;
    protected Weapon equippedWeapon = null;
    
    private List<Bonus> bonuses = new ArrayList<>();
    
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
    
    public List<Bonus> getBonuses() { return bonuses; }
    
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
        int bonusSum = getEquippedWeapon() != null ? getEquippedWeapon().getTotalBonus(stat) : 0;
        for (Bonus bonus : bonuses) {
            if (bonus.getBaseStat() == stat) {
                bonusSum += bonus.getValue();
            }
        }
        
        return bonusSum;
    }
    
    public int getTotalBonus(BattleStat stat) {
        int bonusSum = getEquippedWeapon() != null ? getEquippedWeapon().getTotalBonus(stat) : 0;
        for (Bonus bonus : bonuses) {
            if (bonus.getBattleStat() == stat) {
                bonusSum += bonus.getValue();
            }
        }
        
        return bonusSum;
    }
    
    //Battle Stats
    public int getATK() {
        if (equippedFormula != null) {
            return getETHER() + equippedFormula.getFormulaData().getPow() + getTotalBonus(BattleStat.AttackPower);
        }
        
        return (equippedWeapon != null ? getSTR() + equippedWeapon.getWeaponData().getPow() : getSTR()) + getTotalBonus(BattleStat.AttackPower);
    }
    
    public int getAccuracy() {
        if (getEquippedWeapon() != null) {
            return getEquippedWeapon().getAcc() + (((getDEX() * 4) + getCOMP()) / 2) + ClassBattleBonus().get(BattleStat.Accuracy) + getTotalBonus(BattleStat.Accuracy);
        }
        return getTotalBonus(BattleStat.Accuracy); 
    } //add commander bonus
    
    public int getEvasion() { return (((getAGI() * 3) + getCOMP()) / 2) + ClassBattleBonus().get(BattleStat.Evasion) + getTotalBonus(BattleStat.Evasion); } //add terrain bonus
    
    public int getCrit() {
        if (getEquippedWeapon() != null) {
            return getEquippedWeapon().getCRIT() + (getDEX() / 2) + ClassBattleBonus().get(BattleStat.Crit) + getTotalBonus(BattleStat.Crit);
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
    
    public Weapon getEquippedWPN() { return equippedWeapon; }
    public Formula getEquippedFormula() { return equippedFormula; }
    
    public Formation getEquippedFormation() { return formations.get(0); }
    
    public DamageTool getEquippedWeapon() { 
        if (equippedFormula != null) {
            return equippedFormula.getFormulaData();
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
        return getEquippedWeapon() != null ? getEquippedWeapon().getRange().contains(range) : false;
    }
    
    @Override
    public String toString() { return name; }
    
    public int getAmountExistingFormulas() {
        int count = 0;
        for (Formula formula : formulas) {
            if (formula.doesExist()) { count++; }
        }
        return count;
    }
    
}
