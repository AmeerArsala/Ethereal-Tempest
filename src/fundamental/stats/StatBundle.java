/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import fundamental.stats.Bonus.StatType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class StatBundle {
    private final int val;
    private final StatType type;
    
    private BaseStat baseStatName = null;
    private BattleStat battleStatName = null;
    
    public StatBundle(BaseStat name, int value) {
        baseStatName = name;
        val = value;
        type = StatType.Base;
    }
    
    public StatBundle(BattleStat name, int value) {
        battleStatName = name;
        val = value;
        type = StatType.Battle;
    }
    
    public int getValue() { return val; }
    public StatType getStatType() { return type; }
    
    public BaseStat getWhichBaseStat() { return baseStatName; }
    public BattleStat getWhichBattleStat() { return battleStatName; }
    
    public static HashMap<BaseStat, Integer> createBaseStatsFromBundles(List<StatBundle> stats) {
        HashMap<BaseStat, Integer> baseStats = new HashMap<>();
        
        stats.forEach((stat) -> {
            baseStats.put(stat.getWhichBaseStat(), stat.getValue());
        });
        
        return baseStats;
    }
    
    public static HashMap<BattleStat, Integer> createBattleStatsFromBundles(List<StatBundle> stats) {
        HashMap<BattleStat, Integer> battleStats = new HashMap<>();
        
        stats.forEach((stat) -> {
            battleStats.put(stat.getWhichBattleStat(), stat.getValue());
        });
        
        return battleStats;
    }
    
    public static ArrayList<BaseStat> createBaseStats() {
        ArrayList<BaseStat> ret = new ArrayList<>();
        ret.addAll(Arrays.asList(
                BaseStat.level, 
                BaseStat.maxHP,
                BaseStat.maxTP,
                BaseStat.strength, 
                BaseStat.ether, 
                BaseStat.agility, 
                BaseStat.comprehension, 
                BaseStat.dexterity, 
                BaseStat.defense, 
                BaseStat.resilience, 
                BaseStat.mobility,
                BaseStat.physique,
                BaseStat.adrenaline
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
    
}
