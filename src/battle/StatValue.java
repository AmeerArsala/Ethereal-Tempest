/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;

/**
 *
 * @author night
 */
public class StatValue {
    public enum StatType {
        Base,
        Battle
    }
    
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

class BattleStatValue {
    private final BattleStat stat;
    private int value;
    
    public BattleStatValue(BattleStat statname, int val) {
        super();
        stat = statname;
        value = val;
    }
    
    public BattleStat getStatName() { return stat; }
    public int getValue() { return value; }
}

class BaseStatValue {
    private final BaseStat stat;
    private int value;
    
    public BaseStatValue(BaseStat statname, int val) {
        super();
        stat = statname;
        value = val;
    }
    
    public BaseStat getStatName() { return stat; }
    public int getValue() { return value; }
}