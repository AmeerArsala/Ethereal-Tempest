/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import battle.Combatant;
import etherealtempest.info.Conveyer;
import battle.Strike;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class BattleTalentEffect { //onStrike

    public abstract boolean doesTrigger(Conveyer info, Combatant striker, Combatant victim);
    public abstract void applyEffect(Conveyer info, Combatant striker, Combatant victim);
    
    //OVERRIDE THIS WHEN NEEDED
    protected int recalculateDamage(int baseDamage, Conveyer info, Combatant striker, Combatant victim) {
        return baseDamage;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    protected int extraHits(Conveyer info, Combatant striker, Combatant victim) {
        return 0;
    }
    
    public void applyExtraHits(List<Strike> holder, Conveyer info, Combatant striker, Combatant victim) {
        int extra_hits = extraHits(info, striker, victim);
        for (int i = 0; i < extra_hits; i++) {
            holder.add(new Strike(striker, victim, false));
        }
    }
    
    public int modifyDamage(int baseDamage, Conveyer info, Combatant striker, Combatant victim) {
        int dmg = recalculateDamage(baseDamage, info, striker, victim);
        if (dmg > 0) {
            return dmg;
        }
        
        return baseDamage;
    }
}
