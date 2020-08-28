/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

import battle.Conveyer;
import battle.Unit;

/**
 *
 * @author night
 */
public abstract class ItemEffect {
    public int restoredHPValue, restoredTPValue;
    
    public abstract int HPrestoration();
    public abstract int TPrestoration();
    
    public abstract int[] tempBonusStats(Conveyer C); // buffs to {str, ether, agi, comp, dex, def, rsl, mobility, physique, charisma}
    public abstract int[] permanentBonusStats(Unit U); // {str, ether, agi, comp, dex, def, rsl, mobility, physique, charisma}
    
    public abstract void enactEffect(Conveyer C); //this is also for learning a skill or talent or formula or ability
    
    
    public ItemEffect setHPrestoration(int restore) {
        restoredHPValue = restore;
        return this;
    }
    
    public ItemEffect setTPrestoration(int restore) {
        restoredTPValue = restore;
        return this;
    }
    
}
