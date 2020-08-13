/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.skill;

import battle.Combatant;
import battle.Combatant.BattleStat;
import battle.Conveyer;
import battle.Strike;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class SkillEffect {
    protected int atkpwrBonus = 0, accuracyBonus = 0, evasionBonus = 0, critBonus = 0, critEvasionBonus = 0, speedBonus = 0;
    protected Conveyer conv;
    protected Combatant user, opponent;
    
    public SkillEffect() {
        setBattleStats();
    }
    
    public abstract int extraDamage();
    public abstract int extraHits();
    public abstract void setBattleStats();
    
    public void setData(Conveyer convey, Combatant striker, Combatant victim) {
        conv = convey;
        user = striker;
        opponent = victim;
    }
    
    public void applyExtraHits(List<Strike> holder) {
        for (int i = 0; i < extraHits(); i++) {
            holder.add(new Strike(user, opponent, false));
        } 
    }
    
    public void applyEffectsOnCombat(Combatant C) {
        C.appendToBattleStat(BattleStat.AttackPower, atkpwrBonus);
        C.appendToBattleStat(BattleStat.Accuracy, accuracyBonus);
        C.appendToBattleStat(BattleStat.Evasion, evasionBonus);
        C.appendToBattleStat(BattleStat.Crit, critBonus);
        C.appendToBattleStat(BattleStat.CritEvasion, critEvasionBonus);
        C.appendToBattleStat(BattleStat.AttackSpeed, speedBonus);
    }
    
    public String effectDescription() {
        return
                "Pow: +" + atkpwrBonus + '\n' +
                "Acc: +" + accuracyBonus + '\n' +
                "Eva: +" + evasionBonus + '\n' +
                "Spd: +" + speedBonus + '\n' +
                "Extra DMG: " + extraDamage();
    }
    
}
