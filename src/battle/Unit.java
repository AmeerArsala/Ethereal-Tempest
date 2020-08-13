/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.item.Item;
import battle.item.Weapon;
import battle.ability.Ability;
import battle.formation.Formation;
import battle.formula.Formula;
import battle.item.Inventory;
import battle.skill.Skill;
import battle.talent.PassiveTalent;
import battle.talent.Talent;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class Unit extends JobClass {
    public static final int[] DEFAULT_ENEMY_GROWTH_RATES = new int[] {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};

    public int currentEXP = 0;
    public int currentHP, currentTP;
    
    public UnitStatus unitStatus;
    
    public enum UnitStatus {
        Player(0),
        Ally(-1),
        Enemy(1),
        ThirdParty(2),
        FourthParty(3),
        FifthParty(4);
        
        private final int value;
        
        private static HashMap map = new HashMap<>();
        private UnitStatus(int val) {
            value = val;
        }
        
        static {
            for (UnitStatus stat : UnitStatus.values()) {
                map.put(stat.value, stat);
            }
        }

        public static UnitStatus valueOf(int stat) {
            return (UnitStatus) map.get(stat);
        }

        public int getValue() {
            return value;
        }
    }
    //public int battleStatus; //initiated combat = 1, got initiated against = -1, not in combat = 0
    
    
    protected final int MAX_EXP = 100;
    protected final int MAX_LEVEL = 99;
    protected String name = "";
    
    private int LVL, MAXHP, STR, ETHER, AGI, DEX, COMP, DEF, RSL, MOBILITY, PHYSIQUE, CHARISMA, MAXTP;
    private int[] stats = {LVL, MAXHP, STR, ETHER, AGI, DEX, COMP, DEF, RSL, MOBILITY, PHYSIQUE, CHARISMA}; //length 12
    private int[] personal_growth_rates; //length 12
    
    private Inventory inventory; //7 items max in inventory
    private List<Formula> formulas; //15 formulas max
    private List<Talent> talents; //6 max
    private List<Ability> abilities; //7 max
    private List<Skill> skills; //5 max
    private List<Formation> formations; //triangle, circle, square, diamond, or none?
    
    private final boolean isBoss;
    
    public Unit(String name, JobClass jc, int[] baseStats, int[] growthRates, List<Item> inventory, List<Formula> formulas, List<Talent> talents, List<Ability> abilities, List<Skill> skills, List<Formation> formations, boolean isBoss) {
        super(jc.clName(), jc.ClassStatBonus(), jc.MovementType(), jc.UsableWeapons(), jc.ClassBattleBonus(), jc.clTier(), jc.ClassMaxStats());
        stats = baseStats;
        personal_growth_rates = growthRates;
        
        this.name = name;
        this.inventory = new Inventory(inventory);
        this.formulas = formulas;
        this.talents = talents;
        this.abilities = abilities;
        this.skills = skills;
        this.formations = formations;
        this.isBoss = isBoss;
        currentHP = stats[1];
        currentTP = (stats[1] + ((stats[3] + stats[8]) * 2)) / 2; //MAXTP = (MAXHP + ((ETHER + RSL) * 2)) / 2
        
        customSkillAnimations = jc.getCustomSkillAnimations();
       //setCustomSkillAnimations(jc.getCustomSkillAnimations());
    }
    
    public String getName() { return name; }
    public boolean getIsBoss() { return isBoss; }
    
    public Inventory getInventory() { return inventory; }
    public List<Formula> getFormulas() { return formulas; }
    public List<Skill> getSkills() { return skills; }
    public List<Talent> getTalents() { return talents; }
    public List<Ability> getAbilities() { return abilities; }
    public List<Formation> getFormations() { return formations; }
    
    public int[] getStats() { return stats; }
    public int[] getGrowthRates() { return personal_growth_rates; }
    
    public void setGrowthRates(int[] rates) { personal_growth_rates = rates; }
    public void setGrowthRate(int amt, statName stat) {
        personal_growth_rates[stat.getValue()] = amt;
    }
    
    public enum statName {
        level(0),
        maxHP(1),
        strength(2),
        ether(3),
        agility(4),
        comprehension(6),
        dexterity(5),
        defense(7),
        resilience(8),
        mobility(9),
        physique(10),
        charisma(11);
        
        private final int value;
        private static HashMap map = new HashMap<>();
        private statName(int val) {
            value = val;
        }
        
        static {
            for (statName stat : statName.values()) {
                map.put(stat.value, stat);
            }
        }

        public static statName valueOf(int stat) {
            return (statName) map.get(stat);
        }

        public int getValue() {
            return value;
        }
    }
    
    public void setStats(statName statname, int amount) {
        stats[statname.getValue()] = amount;
    }
    
    int simulateTP(int hpextra, int etherextra, int rslextra) {
        return (hpextra + ((hpextra + rslextra)* 2)) / 2;
    }
    
    public int getStatValue(statName nameofstat) {
        return getRawBaseStats()[nameofstat.getValue()];
    }
    
    public int[] getHP() {
        //         {currentHP, MAXHP}
        int[] hp = {currentHP, stats[1] + ClassStatBonus()[0]};
        return hp;
    }
    
    public int[] getTP() {
        int[] tp = {currentTP, (stats[1] + ((stats[3] + stats[8]) * 2)) / 2};
        return tp;
    }
    public int getLVL() { return stats[0]; }
    public int getSTR() { return stats[2] + ClassStatBonus()[1] + getEquippedWeapon().getBonuses()[0]; }
    public int getETHER() { return stats[3] + ClassStatBonus()[2] + getEquippedWeapon().getBonuses()[1]; } //basically mag
    public int getAGI() { return stats[4] + ClassStatBonus()[3] + getEquippedWeapon().getBonuses()[2]; }
    public int getDEX() { return stats[5] + ClassStatBonus()[4] + getEquippedWeapon().getBonuses()[3]; }
    public int getCOMP() { return stats[6] + ClassStatBonus()[5] + getEquippedWeapon().getBonuses()[4]; } //basically luck
    public int getDEF() { return stats[7] + ClassStatBonus()[6] + getEquippedWeapon().getBonuses()[5]; }
    public int getRSL() { return stats[8] + ClassStatBonus()[7] + getEquippedWeapon().getBonuses()[6]; }
    public int getMOBILITY() { return stats[9] + ClassStatBonus()[8]; } //basically mov
    public int getPHYSIQUE() { return stats[10] + ClassStatBonus()[9]; } //basically con
    public int getCHARISMA() { return stats[11] + ClassStatBonus()[10]; }
    
    public int[] getAllBaseStats() { //except hp
        return new int[] {getLVL(), getSTR(), getETHER(), getAGI(), getCOMP(), getDEX(), getDEF(), getRSL(), getMOBILITY(), getPHYSIQUE(), getCHARISMA()};
    }
    
    public int[] getRawBaseStats() {
        return new int[] {
            getLVL(), 
            stats[2] + ClassStatBonus()[1], //str
            stats[3] + ClassStatBonus()[2], //ether
            stats[4] +  + ClassStatBonus()[3], //agi
            stats[6] + ClassStatBonus()[5], //comp
            stats[5] + ClassStatBonus()[4], //dex
            stats[7] + ClassStatBonus()[6], //def
            stats[8] + ClassStatBonus()[7], //rsl
            stats[9] + ClassStatBonus()[8], //mobility
            stats[10] + ClassStatBonus()[9], //physique
            stats[11] + ClassStatBonus()[10] //charisma
        };
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
    public int getAccuracy() { return getEquippedWeapon().getStatus() ? (getEquippedWeapon().getAcc() + (((getDEX() * 4) + getCOMP()) / 2) + ClassBattleBonus()[0]) : 0; } //add commander bonus
    public int getAvoid() { return ((((getAGI() * 3) + getCOMP()) / 2) + ClassBattleBonus()[1]); } //add terrain bonuus
    public int getAS() { return getEquippedWeapon().getStatus() ? (getAGI() - ((getEquippedWeapon().getWeight() - getPHYSIQUE() >= 0 ? getEquippedWeapon().getWeight() - getPHYSIQUE() : 0))): getAGI(); }
    public int getCrit() { return getEquippedWeapon().getStatus() ? (getEquippedWeapon().getCRIT() + (DEX / 2) + ClassBattleBonus()[2]) : 0; }
    public int getATK() {
        if (getEquippedWeapon().getStatus()) {
            return ((getEquippedWeapon().getDmgType().equals("ether") ? (getEquippedWeapon().getPow() + getETHER()) : (getEquippedWeapon().getPow() + getSTR())));
        }
        return getSTR();
    }
    
    public Formation getEquippedFormation() { return formations.get(0); }
    
    public Weapon getEquippedWeapon() { 
        if (inventory.getItems().get(0) instanceof Weapon) {
            return (Weapon)inventory.getItems().get(0);
        }
        return new Weapon(false); //if weapon type is equal to empty slot, unit can't attack
    }
    
    public int[] rollLevelUp() {
        int[] roll = new int[stats.length];
        for (int k : roll) { k = 0; }
        for (int i = 0; i < stats.length; i++) {
            for (int t = 0; t < (1 + (personal_growth_rates[i]/100)); t++) {
                if (1 + (int)(100 * Math.random()) <= personal_growth_rates[i]) { roll[i]++; }
            }
        }
        return roll;
    }
    
    public void levelUp() {
        for (int i = 0; i < stats.length; i++) {
            stats[i] += rollLevelUp()[i];
        }
    }
    
    public void levelUp(int[] additions) {
        for (int i = 0; i < stats.length; i++) {
            stats[i] += additions[i];
        }
    }
    
    public void levelUp(int[] additions, int index) {
        stats[index] = additions[index];
    }
    
    public boolean isWeaponUsable(String wp) { return UsableWeapons()[Weapon.getWeaponIndex(wp)]; }
    
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
