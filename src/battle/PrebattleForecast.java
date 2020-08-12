/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import battle.Combatant.*;
import battle.StatValue.StatType;
import battle.skill.Skill;
import battle.talent.PrebattleTalent;
import battle.talent.Talent;
import maps.layout.TangibleUnit.BattleRole;

/**
 *
 * @author night
 */
public class PrebattleForecast {
    private SingularForecast initiatorForecast, receiverForecast;
    private final Conveyer data;
    
    private final int range;
    private final Skill skillBeingUsed;
    
    public PrebattleForecast(Combatant initiator, Combatant receiver, Conveyer data, int range, Skill skillBeingUsed) {
        this.data = data;
        this.range = range;
        this.skillBeingUsed = skillBeingUsed;
        
        applyBonuses(initiator, receiver);
        initiatorForecast = new SingularForecast(initiator);
        receiverForecast = new SingularForecast(receiver);
        initializeForecast();
        
        initiator.setDefaultDamage(initiatorForecast.displayedDamage);
        receiver.setDefaultDamage(receiverForecast.displayedDamage);
    }
    
    private void applyBonuses(Combatant initiator, Combatant receiver) {
        for (Talent X : initiator.getUnit().getTalents()) {
            if (X instanceof PrebattleTalent && ((PrebattleTalent)X).getCondition().checkCondition(data)) {
                for (StatValue bonus : ((PrebattleTalent)X).getEffect().bonuses()) {
                    if (bonus.getStatType() == StatType.Battle) {
                        initiator.appendToBattleStat(bonus.getBattleStatValue().getStatName(), bonus.getBattleStatValue().getValue());
                    } else if (bonus.getStatType() == StatType.Base) {
                        initiator.appendToBaseStat(bonus.getBaseStatValue().getStatName(), bonus.getBaseStatValue().getValue());
                    }
                }
                ((PrebattleTalent)X).getEffect().enactExtraEffect();
            }
        }
        
        for (Talent X : receiver.getUnit().getTalents()) {
            if (X instanceof PrebattleTalent && ((PrebattleTalent)X).getCondition().checkCondition(data)) {
                for (StatValue bonus : ((PrebattleTalent)X).getEffect().bonuses()) {
                    if (bonus.getStatType() == StatType.Battle) {
                        receiver.appendToBattleStat(bonus.getBattleStatValue().getStatName(), bonus.getBattleStatValue().getValue());
                    } else if (bonus.getStatType() == StatType.Base) {
                        receiver.appendToBaseStat(bonus.getBaseStatValue().getStatName(), bonus.getBaseStatValue().getValue());
                    }
                }
                ((PrebattleTalent)X).getEffect().enactExtraEffect();
            }
        }
    }
    
    public int getRange() { return range; }
    
    public SingularForecast getInitiatorForecast() { return initiatorForecast; }
    public SingularForecast getReceiverForecast() { return receiverForecast; }
    
    public SingularForecast getSpecifiedForecast(BattleRole br) {
        if (br == BattleRole.Initiator) {
            return initiatorForecast;
        }
        return receiverForecast;
    }
    
    private void initializeForecast() {
        initiatorForecast.canCounterattack = true;
        receiverForecast.canCounterattack = receiverForecast.getCombatant().getUnit().canCounterattackAgainst(range);
        //if receiver can't counterattack, damage = N/A
        
        if (initiatorForecast.getCombatant().getBattleStat(BattleStat.AttackSpeed) - receiverForecast.getCombatant().getBattleStat(BattleStat.AttackSpeed) >= 3) {
            //initiator doubles receiver
            if (skillBeingUsed != null) {
                initiatorForecast.canDouble = false;
                initiatorForecast.BPcostPerHit = 1000 / (skillBeingUsed.getEffect().extraHits() + 1);
            } else {
                initiatorForecast.canDouble = true;
                initiatorForecast.BPcostPerHit = 500;
            }
            receiverForecast.canDouble = false;
        } else if (receiverForecast.getCombatant().getBattleStat(BattleStat.AttackSpeed) - initiatorForecast.getCombatant().getBattleStat(BattleStat.AttackSpeed) >= 3) {
            //receiver doubles initiator
            receiverForecast.canDouble = true;
            initiatorForecast.canDouble = false;
            receiverForecast.BPcostPerHit = 500;
        }
        
        //stabilization
        /*if (rCanCounterattack == 0.0) {
            rCrit = 0.0;
            rDisplayedAcc = 0.0;
            receiverDouble = 0.0;
            rdisplayedDMG = 0;
        }*/
        
        initiatorForecast.displayedAccuracy = initiatorForecast.getCombatant().getBattleStat(BattleStat.Accuracy) - receiverForecast.getCombatant().getBattleStat(BattleStat.Evasion);
        receiverForecast.displayedAccuracy = receiverForecast.getCombatant().getBattleStat(BattleStat.Accuracy) - initiatorForecast.getCombatant().getBattleStat(BattleStat.Evasion);
        
        if (initiatorForecast.displayedAccuracy > 100) {
            initiatorForecast.displayedAccuracy = 100;
        } else if (initiatorForecast.displayedAccuracy < 0) {
            initiatorForecast.displayedAccuracy = 0;
        }
        
        if (receiverForecast.displayedAccuracy > 100) {
            receiverForecast.displayedAccuracy = 100;
        } else if (receiverForecast.displayedAccuracy < 0) {
            receiverForecast.displayedAccuracy = 0;
        }
        
        initiatorForecast.displayedCrit = initiatorForecast.getCombatant().getBattleStat(BattleStat.Crit) - receiverForecast.getCombatant().getBattleStat(BattleStat.CritEvasion);
        receiverForecast.displayedCrit = receiverForecast.getCombatant().getBattleStat(BattleStat.Crit) - initiatorForecast.getCombatant().getBattleStat(BattleStat.CritEvasion);
        
        if (initiatorForecast.displayedCrit > 100) {
            initiatorForecast.displayedCrit = 100;
        } else if (initiatorForecast.displayedCrit < 0) {
            initiatorForecast.displayedCrit = 0;
        }
        
        if (receiverForecast.displayedCrit > 100) {
            receiverForecast.displayedCrit = 100;
        } else if (receiverForecast.displayedCrit < 0) {
            receiverForecast.displayedCrit = 0;
        }
        
        initiatorForecast.displayedDamage = 
                initiatorForecast.getCombatant().getBattleStat(BattleStat.AttackPower) - 
                (initiatorForecast.getCombatant().getUnit().getEquippedWeapon().getDmgType().equals("ether") ? receiverForecast.getCombatant().getBaseStat(BaseStat.resilience) : receiverForecast.getCombatant().getBaseStat(BaseStat.defense));
        
        initiatorForecast.displayedDamage += initiatorForecast.getCombatant().getExtraDamage();
        
        receiverForecast.displayedDamage = receiverForecast.getCombatant().getBattleStat(BattleStat.AttackPower) 
                    - (receiverForecast.getCombatant().getUnit().getEquippedWeapon().getDmgType().equals("ether") ? initiatorForecast.getCombatant().getBaseStat(BaseStat.resilience) 
                    : initiatorForecast.getCombatant().getBaseStat(BaseStat.defense));
        
        receiverForecast.displayedDamage += receiverForecast.getCombatant().getExtraDamage();
    }
    
}

class SingularForecast {
    private final Combatant combatant;
    
    public boolean canDouble, canCounterattack;
    public float displayedAccuracy, displayedCrit;
    public int displayedDamage, BPcostPerHit = 1000;
    
    public SingularForecast(Combatant C) {
        combatant = C;
    }
    
    public Combatant getCombatant() { return combatant; }
    
    public int getAmountOfHits() {
        return 1000 / BPcostPerHit;
    }

}
