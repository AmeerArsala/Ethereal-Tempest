/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import etherealtempest.info.Conveyer;
import fundamental.stats.Bonus.StatType;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition.Occasion;

/**
 *
 * @author night
 */
public abstract class Forecast {
    protected final Conveyer data;
    protected final int range;
    
    protected Forecast(Combatant offender, Combatant retaliator, Conveyer info, boolean supportive) {
        data = info.setInitiator(offender).setReceiver(retaliator);
        
        range = Math.abs(offender.getUnit().getPosX() - retaliator.getUnit().getPosX()) + Math.abs(offender.getUnit().getPosY() - retaliator.getUnit().getPosY());
        
        applyBonuses(offender, retaliator, supportive);
    }
    
    public int getRange() { return range; }
    
    public abstract int calculateDesirabilityToInitiate();
    
    private void applyBonuses(Combatant initiator, Combatant receiver, boolean isForSupport) {
        initiator.getUnit().getTalents().forEach((T) -> {
            for (TalentConcept X : T.getFullBody()) {
                if (X.getTalentCondition().checkCondition(data, Occasion.BeforeCombat)) {
                    X.getTalentEffect().getBuffsRaw(data).forEach((bonus) -> {
                        if (bonus.getStatType() == StatType.Battle) {
                            initiator.appendToBattleStat(bonus.getWhichBattleStat(), bonus.getValue());
                        } else if (bonus.getStatType() == StatType.Base) {
                            initiator.appendToBaseStat(bonus.getWhichBaseStat(), bonus.getValue());
                        }
                    });
                    
                    if (isForSupport == T.getToolType().isSupportive()) {
                        X.getTalentEffect().enactEffect(data); //enact extra effect
                    }
                }
            }
        });
        
        data.swapUnits();
        
        receiver.getUnit().getTalents().forEach((T) -> {
            for (TalentConcept X : T.getFullBody()) {
                if (X.getTalentCondition().checkCondition(data, Occasion.BeforeCombat)) {
                    X.getTalentEffect().getBuffsRaw(data).forEach((bonus) -> {
                        if (bonus.getStatType() == StatType.Battle) {
                            receiver.appendToBattleStat(bonus.getWhichBattleStat(), bonus.getValue());
                        } else if (bonus.getStatType() == StatType.Base) {
                            receiver.appendToBaseStat(bonus.getWhichBaseStat(), bonus.getValue());
                        }
                    });
                    
                    if (isForSupport == T.getToolType().isSupportive()) {
                        X.getTalentEffect().enactEffect(data); //enact extra effect
                    }
                }
            }
        });
        
        data.swapUnits();
    }
    
}
