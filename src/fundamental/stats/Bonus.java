/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Bonus {
    public enum BonusType { //how long it lasts
        @SerializedName("Raw") Raw, //always active
        @SerializedName("FullTurn") FullTurn, //like a rally
        @SerializedName("ThroughNextAction") ThroughNextAction
    }
    
    @Expose(deserialize = false)
    private int turnApplied = -1;
    
    private int value;
    private BonusType bonusType;
    private BaseStat baseStatBonus = null;
    private BattleStat battleStatBonus = null;
    
    public Bonus(int value, BonusType bonusType, BaseStat baseStatBonus) {
        this.value = value;
        this.bonusType = bonusType;
        this.baseStatBonus = baseStatBonus;
    }
    
    public Bonus(int value, BonusType bonusType, BattleStat battleStatBonus) {
        this.value = value;
        this.bonusType = bonusType;
        this.battleStatBonus = battleStatBonus;
    }
    
    public int getValue() { return value; }
    public BonusType getType() { return bonusType; }
    public BaseStat getBaseStat() { return baseStatBonus; }
    public BattleStat getBattleStat() { return battleStatBonus; }
    
    public StatType getStatType() {
        return baseStatBonus != null ? StatType.Base : StatType.Battle;
    }
    
    public int getTurnApplied() { return turnApplied; }
    
    public Bonus setTurnApplied(int turn) {
        turnApplied = turn;
        return this;
    }
    
    public StatBundle toStatBundle() {
        if (baseStatBonus != null) { //base
            return new StatBundle(baseStatBonus, value);
        }
        
        //battle
        return new StatBundle(battleStatBonus, value);
    }
    
    public String StatAsString() {
        if (getStatType() == StatType.Base) {
            return baseStatBonus.getName();
        }
        
        return battleStatBonus.getName();
    }
    
    public String StatBonusAsString() {
        if (getStatType() == StatType.Base) {
            return baseStatBonus.getName() + " " + deltaStat(value);
        }
        
        return battleStatBonus.getName() + deltaStat(value);
    }
    
    public String getDeltaValueAsString() {
        return deltaStat(value);
    }
    
    private static String deltaStat(int val) {
        String str = "";
        if (val > 0) { str += "+"; }
        
        return str + val;
    }
    
    public enum StatType {
        @SerializedName("Base") Base(1, BaseStat.class), 
        @SerializedName("Battle") Battle(-1, BattleStat.class);
        
        private BattleStat battleStat = null;
        private BaseStat baseStat = null;
        
        private final int id;
        private final Class<?> declaringClass;
        private StatType(int identifier, Class<?> declClass) {
            id = identifier;
            declaringClass = declClass;
        }
        
        public StatType setBaseStat(BaseStat based) {
            baseStat = based;
            return getMatchingValue(id);
        }
        
        public StatType setBattleStat(BattleStat bt) {
            battleStat = bt;
            return getMatchingValue(id);
        }
        
        public BaseStat getBaseStat() { return baseStat; }
        public BattleStat getBattleStat() { return battleStat; }
        
        public int getID() { return id; }
        
        public Class<?> getTypeDeclaringClass() {
            return declaringClass;
        }
        
        public StatType getMatchingValue(int val) {
            return val == 1 ? Base : (val == -1 ? Battle : null);
        }
        
        public static StatType getStatTypeByTypeDeclaringClass(Class<?> decl) {
            for (StatType stat : StatType.values()) {
                if  (stat.declaringClass == decl) {
                    return stat;
                }
            }
            
            return null;
        }
    }
    
    public Bonus attemptCompileWith(Bonus B) {
        StatType result = bonusesCanBeCompiled(B);
        if (result != null) {
            if (result == StatType.Base) {
                return new Bonus(value + B.value, bonusType, result.baseStat);
            }
            
            if (result == StatType.Battle) {
                return new Bonus(value + B.value, bonusType, result.battleStat);
            }
        }
        
        return null;
    }
    
    private StatType bonusesCanBeCompiled(Bonus B) {
        return 
                bonusType == B.bonusType ? 
                (baseStatBonus != null && B.baseStatBonus != null && baseStatBonus == B.baseStatBonus ? StatType.Base.setBaseStat(baseStatBonus)
                : (battleStatBonus != null && B.battleStatBonus != null && battleStatBonus == B.battleStatBonus ? StatType.Battle.setBattleStat(battleStatBonus)
                : null)
                ) 
                : null;
    }
    
    private boolean canBeCompiledWith(Bonus B) {
        return 
                bonusType == B.bonusType ? 
                (baseStatBonus != null && B.baseStatBonus != null && baseStatBonus == B.baseStatBonus)
                : (battleStatBonus != null && B.battleStatBonus != null && battleStatBonus == B.battleStatBonus);
    }
    
    public static void organizeList(List<Bonus> family) {
        if (family.size() <= 1) { return; } //list must have at least 2 items
        
        List<Bonus> toAdd = new ArrayList<>();
        
        for (int i = 0; i < family.size(); i++) {
            List<Bonus> toCompile = new ArrayList<>();
            
            for (int k = i + 1; k < family.size(); k++) {
                if (family.get(i).canBeCompiledWith(family.get(k))) {
                    toCompile.add(family.get(k));
                }
            }
            
            if (!toCompile.isEmpty()) {
                toCompile.add(family.get(i));
                toAdd.add(compileList(toCompile));
                family.removeAll(toCompile);
                i = 0;
            }
        }
        
        family.addAll(toAdd);
    }
    
    private static Bonus compileList(List<Bonus> toCompile) {
        Bonus bonus = toCompile.get(0);
        
        for (int i = 1; i < toCompile.size(); i++) {
            bonus = bonus.attemptCompileWith(toCompile.get(i));
        }
        
        return bonus;
    }

}
