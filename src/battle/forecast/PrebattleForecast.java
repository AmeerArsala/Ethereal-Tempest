/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import etherealtempest.info.Conveyer;
import battle.Combatant.*;
import fundamental.Toll.Exchange;
import battle.skill.Skill;
import fundamental.DamageTool;
import maps.layout.Cursor.Purpose;

/**
 *
 * @author night
 */
public class PrebattleForecast extends Forecast {
    private final SingularForecast initiatorForecast, receiverForecast;
    private final Skill skillBeingUsed;
    
    public PrebattleForecast(Combatant initiator, Combatant receiver, Conveyer data, int range, Skill skillBeingUsed) {
        super(initiator, receiver, data, range, false);
        this.skillBeingUsed = skillBeingUsed;
        
        initiatorForecast = new SingularForecast(initiator);
        receiverForecast = new SingularForecast(receiver);
        initializeForecast();
        
        initiator.setDefaultDamage(initiatorForecast.displayedDamage);
        receiver.setDefaultDamage(receiverForecast.displayedDamage);
    }
    
    public SingularForecast getInitiatorForecast() { return initiatorForecast; }
    public SingularForecast getReceiverForecast() { return receiverForecast; }
    
    public SingularForecast getSpecifiedForecast(BattleRole br) {
        if (br == BattleRole.Initiator) {
            return initiatorForecast;
        }
        return receiverForecast;
    }
    
    protected final void initializeForecast() {
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
                (initiatorForecast.equippedTool.getDmgType().equals("ether") ? receiverForecast.getCombatant().getBaseStat(BaseStat.resilience) : receiverForecast.getCombatant().getBaseStat(BaseStat.defense));
        
        initiatorForecast.displayedDamage += initiatorForecast.getCombatant().getExtraDamage();
        
        receiverForecast.displayedDamage = receiverForecast.getCombatant().getBattleStat(BattleStat.AttackPower) 
                    - (receiverForecast.equippedTool.getDmgType().equals("ether") ? initiatorForecast.getCombatant().getBaseStat(BaseStat.resilience) 
                    : initiatorForecast.getCombatant().getBaseStat(BaseStat.defense));
        
        receiverForecast.displayedDamage += receiverForecast.getCombatant().getExtraDamage();
    }
    
    @Override
    public int calculateDesirabilityToInitiate() { //desirability to be initiator
        SingularForecast participant = initiatorForecast;
        SingularForecast opponent = receiverForecast;
        
        int desirability = 
                ((participant.displayedDamage * 15) * participant.getAmountOfHits()) //damage and hits
                + (int)(participant.displayedAccuracy + participant.displayedCrit); //accuracy and crit
        
        desirability += (30 - opponent.displayedDamage >= 0 ? 30 - opponent.displayedDamage : 0);
        desirability += 10 - (opponent.displayedAccuracy / 10);
        desirability += 5 - (opponent.displayedCrit / 20);
        
        if (opponent.getCombatant().getUnit().currentHP - participant.displayedDamage <= 0) {
            desirability += 500;
        } else if (opponent.getCombatant().getUnit().currentHP - (participant.displayedDamage * participant.getAmountOfHits()) <= 0) {
            desirability += 300;
        }
        
        if (!opponent.canCounterattack) { desirability += 1000; }
        
        return desirability;
    }
    
    public static PrebattleForecast createForecast(Conveyer info, Purpose battlePurpose, int attackRange) {
        Combatant initTemp = new Combatant(info, BattleRole.Initiator), receiverTemp = new Combatant(info, BattleRole.Receiver);
        initTemp.setExtraDamage(((DamageTool)initTemp.getUnit().getEquippedTool()).extraDamage);
        receiverTemp.setExtraDamage(((DamageTool)receiverTemp.getUnit().getEquippedTool()).extraDamage);
        if (battlePurpose == Purpose.SkillAttack) {
            int subtractHP = 0, subtractTP = 0;
            if (info.getUnit().getToUseSkill().getToll().getType() == Exchange.HP) {
                subtractHP = info.getUnit().getToUseSkill().getToll().getValue();
            } else if (info.getUnit().getToUseSkill().getToll().getType() == Exchange.TP) {
                subtractTP = info.getUnit().getToUseSkill().getToll().getValue();
            }
            
            initTemp = new Combatant(info, BattleRole.Initiator);
            initTemp.setHPtoSubtract(subtractHP);
            initTemp.setTPtoSubtract(subtractTP);
            initTemp.getUnit().getToUseSkill().getEffect().applyEffectsOnCombat(initTemp);
            initTemp.setExtraDamage(initTemp.getUnit().getToUseSkill().getEffect().extraDamage() + ((DamageTool)initTemp.getUnit().getEquippedTool()).extraDamage);
        } else if (battlePurpose == Purpose.EtherAttack) {
            initTemp.setHPtoSubtract(info.getUnit().getEquippedFormula().getHPUsage());
            initTemp.setTPtoSubtract(info.getUnit().getEquippedFormula().getTPUsage());
        }
        
        return
                new PrebattleForecast(
                    initTemp,
                    receiverTemp,
                    info,
                    attackRange,
                    initTemp.getUnit().getToUseSkill()
                );
    }
}
