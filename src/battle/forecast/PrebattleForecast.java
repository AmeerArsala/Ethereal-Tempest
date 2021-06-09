/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import etherealtempest.info.Conveyer;
import battle.Combatant.*;
import fundamental.skill.Skill;
import maps.layout.occupant.Cursor.Purpose;

/**
 *
 * @author night
 */
public class PrebattleForecast extends Forecast {
    private final SingularForecast initiatorForecast, receiverForecast;
    private final Skill skillBeingUsed, receiverSkillBeingUsed;
    
    private int customRange = -1;
    
    public PrebattleForecast(Combatant initiator, Combatant receiver, Conveyer data) {
        super(initiator, receiver, data, false);
        skillBeingUsed = initiator.getUnit().getToUseSkill();
        receiverSkillBeingUsed = receiver.getUnit().getToUseSkill();
        
        initiatorForecast = new SingularForecast(initiator);
        receiverForecast = new SingularForecast(receiver);
        initializeForecast();
        
        initiator.setDefaultDamage(initiatorForecast.displayedDamage);
        receiver.setDefaultDamage(receiverForecast.displayedDamage);
    }
    
    private PrebattleForecast(Combatant initiator, Combatant receiver, Conveyer data, int customRange) {
        super(initiator, receiver, data, false);
        this.customRange = customRange;
        
        skillBeingUsed = initiator.getUnit().getToUseSkill();
        receiverSkillBeingUsed = receiver.getUnit().getToUseSkill();
        
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
        receiverForecast.canCounterattack = receiverForecast.getCombatant().getUnit().canCounterattackAgainst(customRange > 0 ? customRange : range);
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
            if (receiverSkillBeingUsed != null) {
                receiverForecast.canDouble = false;
                receiverForecast.BPcostPerHit = 1000 / (receiverSkillBeingUsed.getEffect().extraHits() + 1);
            } else {
                receiverForecast.canDouble = true;
                initiatorForecast.canDouble = false;
                receiverForecast.BPcostPerHit = 500;
            }
        }
        
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
        
        if (opponent.getCombatant().getUnit().getStat(BaseStat.currentHP) - participant.displayedDamage <= 0) {
            desirability += 500;
        } else if (opponent.getCombatant().getUnit().getStat(BaseStat.currentHP) - (participant.displayedDamage * participant.getAmountOfHits()) <= 0) {
            desirability += 300;
        }
        
        if (!opponent.canCounterattack) { desirability += 1000; }
        
        return desirability;
    }
    
    public static PrebattleForecast createForecast(Conveyer info, Purpose battlePurpose, int fromRange) { //simulated battle
        Combatant initiator = new Combatant(info, BattleRole.Initiator), receiver = new Combatant(info, BattleRole.Receiver);
        initiator.prebattleInitialization();
        receiver.prebattleInitialization();
        
        return new PrebattleForecast(initiator, receiver, info, fromRange);
    }
    
    public static PrebattleForecast createForecast(Conveyer info, Purpose battlePurpose) { //regular battle; does the same thing as the one below
        return createForecast(info, battlePurpose, -1);
    }
    
    public static PrebattleForecast createBattleForecast(Conveyer info) {
        Combatant initiator = new Combatant(info, BattleRole.Initiator), receiver = new Combatant(info, BattleRole.Receiver);
        initiator.prebattleInitialization();
        receiver.prebattleInitialization();
        
        return new PrebattleForecast(initiator, receiver, info);
    }
}
