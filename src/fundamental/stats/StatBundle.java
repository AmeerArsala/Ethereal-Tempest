/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import fundamental.stats.Bonus.StatType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 * @param <E> either BaseStat or BattleStat
 */
public class StatBundle<E extends java.lang.Enum> {
    private final E statName;
    private final int val;
    private final StatType type;
    
    public StatBundle(E statName, int val, StatType type) {
        this.statName = statName;
        this.val = val;
        this.type = type;
    }
    
    public StatBundle(E statName, int val) {
        this.statName = statName;
        this.val = val;

        type = StatType.getStatTypeByTypeDeclaringClass(statName.getDeclaringClass());
    }
        
    public int getValue() { return val; }
    public StatType getStatType() { return type; }
    
    public E getStat() { return statName; }
    
    public Bonus toRawBonus() {
        if (type == StatType.Base) {
            return new Bonus(val, Bonus.BonusType.Raw, (BaseStat)statName);
        }
        
        if (type == StatType.Battle) {
            return new Bonus(val, Bonus.BonusType.Raw, (BattleStat)statName);
        }
        
        return null;
    }
    
    public static HashMap<BaseStat, Integer> createBaseStatsFromBundles(List<StatBundle<BaseStat>> stats) {
        HashMap<BaseStat, Integer> baseStats = new HashMap<>();
        
        stats.forEach((stat) -> {
            baseStats.put(stat.getStat(), stat.getValue());
        });
        
        return baseStats;
    }
    
    public static HashMap<BattleStat, Integer> createBattleStatsFromBundles(List<StatBundle<BattleStat>> stats) {
        HashMap<BattleStat, Integer> battleStats = new HashMap<>();
        
        stats.forEach((stat) -> {
            battleStats.put(stat.getStat(), stat.getValue());
        });
        
        return battleStats;
    }
    
    public static ArrayList<BaseStat> createBaseStats() {
        ArrayList<BaseStat> ret = new ArrayList<>();
        ret.addAll(Arrays.asList(
                BaseStat.Level, 
                BaseStat.MaxHP,
                BaseStat.MaxTP,
                BaseStat.Strength, 
                BaseStat.Ether, 
                BaseStat.Agility, 
                BaseStat.Comprehension, 
                BaseStat.Dexterity, 
                BaseStat.Defense, 
                BaseStat.Resilience, 
                BaseStat.Mobility,
                BaseStat.Physique,
                BaseStat.Adrenaline
            ));
        
        return ret;
    }
    
    public static ArrayList<BattleStat> createBattleStats() {
        ArrayList<BattleStat> ret = new ArrayList<>();
        ret.addAll(Arrays.asList(
                BattleStat.Accuracy,
                BattleStat.AttackPower,
                BattleStat.AttackSpeed,
                BattleStat.Crit,
                BattleStat.CritEvasion,
                BattleStat.Evasion
        ));
        
        return ret;
    }
    
    public static List<StatBundle<BaseStat>> uniformBaseStats(int uniform) {
        return Arrays.asList(
            new StatBundle<>(BaseStat.Level, uniform),
            new StatBundle<>(BaseStat.MaxHP, uniform),
            new StatBundle<>(BaseStat.MaxTP, uniform),
            new StatBundle<>(BaseStat.Strength, uniform),
            new StatBundle<>(BaseStat.Ether, uniform),
            new StatBundle<>(BaseStat.Agility, uniform),
            new StatBundle<>(BaseStat.Comprehension, uniform),
            new StatBundle<>(BaseStat.Dexterity, uniform),
            new StatBundle<>(BaseStat.Defense, uniform),
            new StatBundle<>(BaseStat.Resilience, uniform),
            new StatBundle<>(BaseStat.Mobility, uniform),
            new StatBundle<>(BaseStat.Physique, uniform),
            new StatBundle<>(BaseStat.Adrenaline, uniform)
        );
    }
    
    public static List<StatBundle<BattleStat>> uniformBattleStats(int uniform) {
        return Arrays.asList(
            new StatBundle<>(BattleStat.Accuracy, uniform),
            new StatBundle<>(BattleStat.AttackPower, uniform),
            new StatBundle<>(BattleStat.AttackSpeed, uniform),
            new StatBundle<>(BattleStat.Crit, uniform),
            new StatBundle<>(BattleStat.CritEvasion, uniform),
            new StatBundle<>(BattleStat.Evasion, uniform)
        );
    }
    
    public static HashMap<BaseStat, Integer> BaseStatCanvas(int num) {
        HashMap<BaseStat, Integer> canvas = new HashMap<>();
        
        BaseStat[] stats = BaseStat.values();
        
        for (BaseStat stat : stats) {
            canvas.put(stat, num);
        }
        
        return canvas;
    }
    
    public static HashMap<BattleStat, Integer> BattleStatCanvas(int num) {
        HashMap<BattleStat, Integer> canvas = new HashMap<>();
        
        BattleStat[] stats = BattleStat.values();
        
        for (BattleStat stat : stats) {
            canvas.put(stat, num);
        }
        
        return canvas;
    }
    
}
