/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import fundamental.stats.Bonus.StatType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class StatBundleDeserialization {
    private int val;
    private BaseStat baseStatName;
    private BattleStat battleStatName;
    
    public StatBundleDeserialization() {}
    
    public StatBundleDeserialization(int val, BaseStat baseStatName, BattleStat battleStatName) {
        this.val = val;
        this.baseStatName = baseStatName;
        this.battleStatName = battleStatName;
    }
    
    public StatBundle constructStatBundle() {
        if (baseStatName != null) { //base stat
            return new StatBundle(baseStatName, val, StatType.Base);
        }
        
        if (battleStatName != null) {
            return new StatBundle(battleStatName, val, StatType.Battle);
        }
        
        return null;
    }
    
    public static List<StatBundle<BaseStat>> constructBaseStatBundleGroup(StatBundleDeserialization[] SBDs) {
        List<StatBundle<BaseStat>> statBundleGroup = new ArrayList<>();
        
        for (StatBundleDeserialization statBundle : SBDs) {
            statBundleGroup.add(statBundle.constructStatBundle());
        }
        
        return statBundleGroup;
    }
    
    public static List<StatBundle<BattleStat>> constructBattleStatBundleGroup(StatBundleDeserialization[] SBDs) {
        List<StatBundle<BattleStat>> statBundleGroup = new ArrayList<>();
        
        for (StatBundleDeserialization statBundle : SBDs) {
            statBundleGroup.add(statBundle.constructStatBundle());
        }
        
        return statBundleGroup;
    }
}
