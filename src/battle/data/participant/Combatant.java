/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.participant;

import com.jme3.math.FastMath;
import etherealtempest.info.Conveyor;
import fundamental.skill.Skill;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.stats.alteration.Toll.Exchange;
import general.math.FloatPair;
import java.util.HashMap;
import java.util.Objects;
import maps.layout.occupant.character.TangibleUnit;

/**
 *
 * @author night
 */
public class Combatant {
    public enum AttackType {
        Weapon,
        Formula
    }
    
    public static final int MAX_EXP_VALUE = 100;
    
    public BattleRole battle_role;
    
    private final TangibleUnit tu;
    private final AttackType attackType;
    private final HashMap<BaseStat, Integer> combatBaseStats = new HashMap<>(); //raw base stats
    private final HashMap<BattleStat, Integer> combatBattleStats = new HashMap<>();
    private final CombatantStatistics statistics = new CombatantStatistics();
    
    private boolean isUsingSkill;
    private int defaultDamage;
    
    public Combatant(Conveyor info, BattleRole role) {
        battle_role = role;
        
        if (role == BattleRole.Initiator) {
            tu = info.getUnit();
        } else {
            tu = info.getEnemyUnit();
        }
        
        if (tu.getEquippedFormula() != null) {
            attackType = AttackType.Formula;
        } else {
            attackType = AttackType.Weapon;
        }
        
        initializeCombatant();
    }
    
    public Combatant(TangibleUnit unit, BattleRole role) {
        battle_role = role;
        tu = unit;
        
        if (tu.getEquippedFormula() != null) {
            attackType = AttackType.Formula;
        } else {
            attackType = AttackType.Weapon;
        }
        
        initializeCombatant();
    }
    
    private void initializeCombatant() {
        combatBaseStats.put(BaseStat.Level, tu.getLVL());
        combatBaseStats.put(BaseStat.MaxHP, tu.getMaxHP());
        combatBaseStats.put(BaseStat.CurrentHP, tu.getBaseStat(BaseStat.CurrentHP));
        combatBaseStats.put(BaseStat.CurrentTP, tu.getBaseStat(BaseStat.CurrentTP));
        combatBaseStats.put(BaseStat.MaxTP, tu.getMaxTP());
        combatBaseStats.put(BaseStat.Strength, tu.getSTR());
        combatBaseStats.put(BaseStat.Ether, tu.getETHER());
        combatBaseStats.put(BaseStat.Agility, tu.getAGI());
        combatBaseStats.put(BaseStat.Comprehension, tu.getCOMP());
        combatBaseStats.put(BaseStat.Dexterity, tu.getDEX());
        combatBaseStats.put(BaseStat.Defense, tu.getDEF());
        combatBaseStats.put(BaseStat.Resilience, tu.getRSL());
        
        combatBaseStats.put(BaseStat.Mobility, tu.getMOBILITY());
        combatBaseStats.put(BaseStat.Physique, tu.getPHYSIQUE());
        
        combatBaseStats.put(BaseStat.Adrenaline, tu.getADRENALINE());
        
        combatBattleStats.put(BattleStat.AttackPower, tu.getATK());
        combatBattleStats.put(BattleStat.Evasion, tu.getEvasion());
        combatBattleStats.put(BattleStat.Crit, tu.getCrit());
        combatBattleStats.put(BattleStat.CritEvasion, tu.getCritEvasion());
        combatBattleStats.put(BattleStat.AttackSpeed, tu.getAS());
        combatBattleStats.put(BattleStat.Accuracy, tu.getAccuracy());
        
        applySkillBuffsIfAny();
    }
    
    private void applySkillBuffsIfAny() {
        Skill skillBeingUsed = tu.getToUseSkill();
        if (skillBeingUsed != null) {
            skillBeingUsed.getEffect().applyBuffsOnCombat(this);
            
            isUsingSkill = true;
        } else {
            isUsingSkill = false;
        }
    }
    
    public void applySkillTollIfAny() {
        if (isUsingSkill) {
            Skill skillInUse = tu.getToUseSkill();
            Exchange costType = skillInUse.getToll().getType();
            if (costType != Exchange.Durability) { // costType == Exchange.HP || costType == Exchange.TP
                appendToBaseStat(costType.getCorrelatingStat(), -skillInUse.getToll().getValue());
            } else {
                //if this skill is being used with a cost of durability, this assumes that the equipped tool is a weapon
                tu.getEquippedWeapon().addCurrentDurability(-skillInUse.getToll().getValue());
            }
        }
    }
    
    public TangibleUnit getUnit() { return tu; }
    public CombatantStatistics getStatistics() { return statistics; }
    public AttackType getAttackType() { return attackType; } 
    public boolean isUsingSkill() { return isUsingSkill; }
    public int getDefaultDamage() { return defaultDamage; } //full damage on hit including extra damage
    
    public void setDefaultDamage(int dmg) { 
        defaultDamage = dmg; 
    }

    public int getBaseStat(BaseStat stat) {
        return combatBaseStats.get(stat);
    }
    
    public int getBattleStat(BattleStat stat) {
        return combatBattleStats.get(stat);
    }
    
    public float getCurrentToMaxHPRatio() {
        return ((float)combatBaseStats.get(BaseStat.CurrentHP)) / combatBaseStats.get(BaseStat.MaxHP);
    }
    
    public float getCurrentToMaxTPRatio() {
        return ((float)combatBaseStats.get(BaseStat.CurrentTP)) / combatBaseStats.get(BaseStat.MaxTP);
    }
    
    public void appendToBaseStat(BaseStat stat, int value) {
        int nextVal = combatBaseStats.get(stat) + value;
        if (stat == BaseStat.CurrentHP) {
            int maxHP = combatBaseStats.get(BaseStat.MaxHP);
            if (nextVal > maxHP) {
                nextVal = maxHP;
            }
        } else if (stat == BaseStat.CurrentTP) {
            int maxTP = combatBaseStats.get(BaseStat.MaxTP);
            if (nextVal > maxTP) {
                nextVal = maxTP;
            }
        }
        
        if (nextVal < 0) {
            nextVal = 0;
        }
        
        combatBaseStats.replace(stat, nextVal);
    }
    
    public void appendToBattleStat(BattleStat stat, int value) {
        int nextVal = combatBattleStats.get(stat) + value;
        if (nextVal < 0) {
            nextVal = 0;
        }
        
        combatBattleStats.replace(stat, nextVal);
    }
    
    public void applyAllStatsToUnit() {
        tu.setRawBaseStat(BaseStat.Level, combatBaseStats.get(BaseStat.Level));
        
        tu.setRawBaseStat(BaseStat.CurrentHP, combatBaseStats.get(BaseStat.CurrentHP));
        tu.setRawBaseStat(BaseStat.CurrentTP, combatBaseStats.get(BaseStat.CurrentTP));
        
        statistics.apply(tu.getUnitStatisticsModifier());
    }
    
    public float secondsToDrain(BaseStat statToDrain, BaseStat defensiveStat, int dmg, boolean isCrit) {
        FloatPair domain = new FloatPair(0.2f, 1.25f);
        
        float multiplier = 0.5f; //base is 0.5f
        float damage = dmg + 1f;
        float defense = combatBaseStats.get(defensiveStat);
        float attack = damage + defense; // damage = attack - defense
        
        if (isCrit) {
            multiplier /= 2f;
        }
        
        float seconds = multiplier * (combatBaseStats.get(statToDrain) / damage) * (defense / attack);
        
        return domain.bound(seconds);
    }
    
    public void calculateEXP(Combatant opponent) {
        int levelDifference = tu.getLVL() - opponent.tu.getLVL();
        int baseDamageValue = 10; // base damage exp
        int baseKillValue = 40;   // base kill exp (for killing a foe)
        int bountyCoefficient = tu.getParams().isBoss ? 2 : 1;
        FloatPair expDomain = new FloatPair(1, 199 - tu.currentEXP); // no double level ups
        
        float overlevelPreventionCoefficient = 1f;
        if (levelDifference > 0) {
            overlevelPreventionCoefficient = FastMath.pow(levelDifference, -1);
        }
        
        if (opponent.getBaseStat(BaseStat.CurrentHP) <= 0) { //opponent is dead
            float underdogKillCoefficient; //other units can easily catch up in levels if they kill an enemy, but it is extremely hard to overlevel
            if (levelDifference <= 0) { //opponent is a higher level
                underdogKillCoefficient = 1 + (levelDifference * -1);
            } else { // > 0
                underdogKillCoefficient = overlevelPreventionCoefficient;
            }
            
            statistics.expGained = (int)expDomain.bound(baseKillValue * bountyCoefficient * underdogKillCoefficient);
        } else {
            statistics.expGained = (int)expDomain.bound((statistics.damageDone > 0 ? baseDamageValue : 1) * bountyCoefficient * overlevelPreventionCoefficient);
        }
    }
    
    /**
     *
     * @return the new current exp value
     */
    public int addExpGained() {
        tu.currentEXP += statistics.expGained;
        return tu.currentEXP;
    }
    
    /**
     *
     * @return whether the unit is eligible to level up or not
     */
    public boolean subtractEXPifLevelUp() {
        if (tu.currentEXP < CombatantStatistics.MAX_EXP_VALUE) {
            return false;
        }
        
        tu.currentEXP -= CombatantStatistics.MAX_EXP_VALUE;
        return true;
    }
    
    @Override
    public String toString() {
        return 
                  tu + "\n"
                + "Max HP: " + combatBaseStats.get(BaseStat.MaxHP) + "\n"
                + "Max TP: " + combatBaseStats.get(BaseStat.MaxTP) + "\n"
                + "Current HP: " + combatBaseStats.get(BaseStat.CurrentTP) + "\n"
                + "Current TP: " + combatBaseStats.get(BaseStat.CurrentTP) + "\n"
                + "STR: " + combatBaseStats.get(BaseStat.Strength) + "\n"
                + "ETHER: " + combatBaseStats.get(BaseStat.Ether) + "\n"
                + "AGI: " + combatBaseStats.get(BaseStat.Agility) + "\n"
                + "COMP: " + combatBaseStats.get(BaseStat.Comprehension) + "\n"
                + "DEX: " + combatBaseStats.get(BaseStat.Dexterity) + "\n"
                + "DEF: " + combatBaseStats.get(BaseStat.Defense) + "\n"
                + "RSL: " + combatBaseStats.get(BaseStat.Resilience) + "\n"
                + "MOBILITY: " + combatBaseStats.get(BaseStat.Mobility) + "\n"
                + "PHYSIQUE: " + combatBaseStats.get(BaseStat.Physique) + "\n"
                + "Initial Adrenaline: " + combatBaseStats.get(BaseStat.Adrenaline) + "\n"
                + "ATK PWR: " + combatBattleStats.get(BattleStat.AttackPower) + "\n"
                + "ACC: " + combatBattleStats.get(BattleStat.Accuracy) + "\n"
                + "EVA: " + combatBattleStats.get(BattleStat.Evasion) + "\n"
                + "CRIT: " + combatBattleStats.get(BattleStat.Crit) + "\n"
                + "CRIT EVA: " + combatBattleStats.get(BattleStat.CritEvasion) + "\n"
                + "AS: " + combatBattleStats.get(BattleStat.AttackSpeed) + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.battle_role);
        hash = 37 * hash + Objects.hashCode(this.tu);
        hash = 37 * hash + Objects.hashCode(this.attackType);
        hash = 37 * hash + Objects.hashCode(this.combatBaseStats);
        hash = 37 * hash + Objects.hashCode(this.combatBattleStats);
        hash = 37 * hash + Objects.hashCode(this.statistics);
        hash = 37 * hash + (this.isUsingSkill ? 1 : 0);
        hash = 37 * hash + this.defaultDamage;
        return hash;
    }
    
}