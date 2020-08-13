/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.talent;

import battle.Combatant;
import battle.Conveyer;
import battle.Strike;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class Differentiate {
    protected Conveyer info;
    protected Combatant user, opponent;
    
    public abstract void applyEffect();
    public abstract int recalculateDamage();
    public abstract boolean doesTrigger();
    public abstract int extraHits();
    
    public void inputData(Conveyer data, Combatant user, Combatant opponent) {
        info = data;
        this.user = user;
        this.opponent = opponent;
    }
    
    public void applyExtraHits(List<Strike> holder) {
        for (int i = 0; i < extraHits(); i++) {
            holder.add(new Strike(user, opponent, false));
        } 
    }
    
    public int modifyDamage(int baseDamage) {
        int dmg = recalculateDamage();
        if (dmg > 0) {
            return dmg;
        } else {
            return baseDamage;
        }
    }
}
