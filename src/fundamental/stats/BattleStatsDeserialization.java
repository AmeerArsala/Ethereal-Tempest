/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import java.util.HashMap;

/**
 *
 * @author night
 */
public class BattleStatsDeserialization {
    private Integer Accuracy;
    private Integer AttackPower;
    private Integer AttackSpeed;
    private Integer Crit;
    private Integer CritEvasion;
    private Integer Evasion;
    
    public BattleStatsDeserialization(Integer Accuracy, Integer AttackPower, Integer AttackSpeed, Integer Crit, Integer CritEvasion, Integer Evasion) {
        this.Accuracy = Accuracy;
        this.AttackPower = AttackPower;
        this.AttackSpeed = AttackSpeed;
        this.Crit = Crit;
        this.CritEvasion = CritEvasion;
        this.Evasion = Evasion;
    }
    
    /*
        BattleStat[] battleStats = {
            BattleStat.AttackPower,
            BattleStat.Accuracy,
            BattleStat.Evasion,
            BattleStat.Crit,
            BattleStat.CritEvasion,
            BattleStat.AttackSpeed
        };
    
        ^^^ equivalent to BattleStat.values()
    */
    
    public int[] battleStatValueArray(int defaultVal) {
        return new int[] {
            AttackPower != null ? AttackPower : defaultVal,
            Accuracy != null ? Accuracy : defaultVal,
            Evasion != null ? Evasion : defaultVal,
            Crit != null ? Crit : defaultVal,
            CritEvasion != null ? CritEvasion : defaultVal,
            AttackSpeed != null ? AttackSpeed : defaultVal
        };
    }
    
    public HashMap<BattleStat, Integer> createBattleStatMap() {
        return createBattleStatMap(0);
    }
    
    public HashMap<BattleStat, Integer> createBattleStatMap(int defaultVal) {
        HashMap<BattleStat, Integer> statMap = new HashMap<>();
        
        BattleStat[] battleStats = BattleStat.values();
        int[] stats = battleStatValueArray(defaultVal);
        
        for (int i = 0; i < battleStats.length; i++) {
            statMap.put(battleStats[i], stats[i]);
        }
        
        return statMap;
    }
}
