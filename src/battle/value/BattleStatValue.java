/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.value;

import battle.Combatant.BattleStat;

/**
 *
 * @author night
 */
public class BattleStatValue {
    private final BattleStat stat;
    private final int value;
    
    public BattleStatValue(BattleStat statname, int val) {
        super();
        stat = statname;
        value = val;
    }
    
    public BattleStat getStatName() { return stat; }
    public int getValue() { return value; }
}
