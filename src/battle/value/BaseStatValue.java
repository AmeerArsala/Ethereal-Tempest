/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.value;

import battle.Combatant.BaseStat;

/**
 *
 * @author night
 */
public class BaseStatValue {
    private final BaseStat stat;
    private final int value;
    
    public BaseStatValue(BaseStat statname, int val) {
        super();
        stat = statname;
        value = val;
    }
    
    public BaseStat getStatName() { return stat; }
    public int getValue() { return value; }
}
