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
    
    public HashMap<BattleStat, Integer> createBattleStatMap() {
        int statCount = 6;
        HashMap<BattleStat, Integer> statMap = new HashMap<>();
        
        BattleStat[] battleStats = {
            BattleStat.Accuracy,
            BattleStat.AttackPower,
            BattleStat.AttackSpeed,
            BattleStat.Crit,
            BattleStat.CritEvasion,
            BattleStat.Evasion
        };
        
        int[] stats = {
            Accuracy != null ? Accuracy : 0,
            AttackPower != null ? AttackPower : 0,
            AttackSpeed != null ? AttackSpeed : 0,
            Crit != null ? Crit : 0,
            CritEvasion != null ? CritEvasion : 0,
            Evasion != null ? Evasion : 0
        };
        
        for (int i = 0; i < statCount; i++) {
            statMap.put(battleStats[i], stats[i]);
        }
        
        return statMap;
    }
}
