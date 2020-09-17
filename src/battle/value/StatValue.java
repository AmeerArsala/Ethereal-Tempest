/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.value;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import fundamental.Bonus.StatType;

/**
 *
 * @author night
 */
public class StatValue {
    private StatType type;
    private BattleStatValue battleStatValue;
    private BaseStatValue baseStatValue;
    
    public StatValue(BattleStatValue BSV) {
        type = StatType.Battle;
        battleStatValue = BSV;
    }
    
    public StatValue(BaseStatValue BSV) {
        type = StatType.Base;
        baseStatValue = BSV;
    }
    
    public BaseStatValue getBaseStatValue() {
        return baseStatValue;
    }
    
    public BattleStatValue getBattleStatValue() {
        return battleStatValue;
    }
    
    public StatType getStatType() {
        return type;
    }
    
    public int getValue() {
        if (type == StatType.Battle) {
            return battleStatValue.getValue();
        } else if (type == StatType.Base) {
            return baseStatValue.getValue();
        }
        return -1;
    }
    
}