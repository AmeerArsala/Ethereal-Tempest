/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.forecast;

import battle.data.participant.BattleRole;
import battle.data.participant.Combatant;
import etherealtempest.info.Conveyor;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.stats.Bonus.StatType;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition.Occasion;

/**
 *
 * @author night
 * @param <S> either SingularForecast or SingularSupportForecast
 */
public abstract class Forecast<S extends IndividualForecast> {
    protected final Conveyor data;
    protected final int range;
    protected final boolean usesCustomRange;
    
    protected S initiatorForecast, receiverForecast;
    
    protected interface IndividualForecastCreation<F extends IndividualForecast> {
        public F create(int range);
    }
    
    protected Forecast(IndividualForecastCreation<S> initiatorCreation, IndividualForecastCreation<S> receiverCreation, Conveyor info, boolean supportive) {
        // same as: |offender.x - retaliator.x| + |offender.y - retaliator.y|
        range = info.getInitiator().getUnit().getPos().getCoords().nonDiagonalDistanceFrom(info.getReceiver().getUnit().getPos().getCoords()); 
        usesCustomRange = false;
        
        initiatorForecast = initiatorCreation.create(range);
        receiverForecast = receiverCreation.create(range);
        
        data = info.setInitiator(initiatorForecast.getCombatant()).setReceiver(receiverForecast.getCombatant());
        
        applyBonuses(supportive);
        
        initiatorForecast.initialize();
        receiverForecast.initialize();
        
        applyTalentEffectsToInfluenceForecasts(initiatorForecast, receiverForecast);
        applyTalentEffectsToInfluenceForecasts(receiverForecast, initiatorForecast);
    }
    
    //custom range
    protected Forecast(S offenderForecast, S retaliatorForecast, Conveyor info, boolean supportive, int customRange) {
        range = customRange;
        usesCustomRange = true;
        
        initiatorForecast = offenderForecast;
        receiverForecast = retaliatorForecast;
        
        data = info.setInitiator(initiatorForecast.getCombatant()).setReceiver(receiverForecast.getCombatant());
        
        applyBonuses(supportive);
        
        initiatorForecast.initialize();
        receiverForecast.initialize();
        
        applyTalentEffectsToInfluenceForecasts(initiatorForecast, receiverForecast);
        applyTalentEffectsToInfluenceForecasts(receiverForecast, initiatorForecast);
    }
    
    public int getRange() { return range; }
    public boolean usesCustomRange() { return usesCustomRange; }
    
    public Conveyor getContext() { return data; }
    
    public S getInitiatorForecast() { return initiatorForecast; }
    public S getReceiverForecast() { return receiverForecast; }
    
    public S getSpecifiedForecast(BattleRole br) {
        if (br == BattleRole.Initiator) {
            return initiatorForecast;
        }
        
        return receiverForecast;
    }
    
    public abstract int calculateDesirabilityToInitiate();
    
    private void applyBonuses(boolean isForSupport) {
        applyTalentBonusesToCombatant(data.getInitiator(), isForSupport);
        data.swapUnits();
        applyTalentBonusesToCombatant(data.getReceiver(), isForSupport);
        data.swapUnits();
    }
    
    private void applyTalentBonusesToCombatant(Combatant participant, boolean combatIsSupport) {
        participant.getUnit().getTalents().forEach((T) -> {
            for (TalentConcept X : T.getFullBody()) {
                if (X.getTalentCondition().checkCondition(data, Occasion.BeforeCombat)) {
                    X.getTalentEffect().getBuffsRaw(data).forEach((bonus) -> {
                        if (bonus.getStatType() == StatType.Battle) {
                            participant.appendToBattleStat((BattleStat)bonus.getStat(), bonus.getValue());
                        } else if (bonus.getStatType() == StatType.Base) {
                            participant.appendToBaseStat((BaseStat)bonus.getStat(), bonus.getValue());
                        }
                    });
                    
                    //we don't want a sword to be used aggressively in a support round, but we do want it to be used aggressively in a fight
                    if (combatIsSupport == T.getToolType().isSupportive()) {
                        X.getTalentEffect().enactEffect(data); //enact extra effect, like switching battle roles or blazing wind
                    }
                }
            }
        });
    }
    
    private void applyTalentEffectsToInfluenceForecasts(S participant, S target) {
        participant.getCombatant().getUnit().getTalents().forEach((T) -> {
            for (TalentConcept X : T.getFullBody()) {
                if (X.getTalentCondition().checkCondition(data, Occasion.BeforeCombat)) {
                    X.getTalentEffect().influenceForecasts(participant, target, data);
                }
            }
        });
    }
    
}
