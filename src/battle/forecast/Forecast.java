/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import battle.Combatant.BattleRole;
import battle.talent.PrebattleTalent;
import battle.talent.Talent;
import etherealtempest.info.Conveyer;
import fundamental.Bonus.StatType;

/**
 *
 * @author night
 */
public abstract class Forecast {
    protected final Conveyer data;
    protected final int range;
    
    protected Forecast(Combatant offender, Combatant retaliator, Conveyer info, int fromRange, boolean supportive) {
        data = info;
        range = fromRange;
        
        applyBonuses(offender, retaliator, supportive);
    }
    
    public int getRange() { return range; }
    
    public abstract int calculateDesirabilityToInitiate();
    
    private void applyBonuses(Combatant initiator, Combatant receiver, boolean isForSupport) {
        for (Talent X : initiator.getUnit().getTalents()) {
            if (X instanceof PrebattleTalent && ((PrebattleTalent)X).getCondition().checkCondition(data)) {
                ((PrebattleTalent)X).getEffect().bonuses().forEach((bonus) -> {
                    if (bonus.getStatType() == StatType.Battle) {
                        initiator.appendToBattleStat(bonus.getBattleStatValue().getStatName(), bonus.getBattleStatValue().getValue());
                    } else if (bonus.getStatType() == StatType.Base) {
                        initiator.appendToBaseStat(bonus.getBaseStatValue().getStatName(), bonus.getBaseStatValue().getValue());
                    }
                });
                if (isForSupport == ((PrebattleTalent)X).getToolType().isSupportive()) {
                    ((PrebattleTalent)X).getEffect().enactExtraEffect();
                }
            }
        }
        
        for (Talent X : receiver.getUnit().getTalents()) {
            if (X instanceof PrebattleTalent && ((PrebattleTalent)X).getCondition().checkCondition(data)) {
                ((PrebattleTalent)X).getEffect().bonuses().forEach((bonus) -> {
                    if (bonus.getStatType() == StatType.Battle) {
                        receiver.appendToBattleStat(bonus.getBattleStatValue().getStatName(), bonus.getBattleStatValue().getValue());
                    } else if (bonus.getStatType() == StatType.Base) {
                        receiver.appendToBaseStat(bonus.getBaseStatValue().getStatName(), bonus.getBaseStatValue().getValue());
                    }
                });
                if (isForSupport == ((PrebattleTalent)X).getToolType().isSupportive()) {
                    ((PrebattleTalent)X).getEffect().enactExtraEffect();
                }
            }
        }
    }
    
}
