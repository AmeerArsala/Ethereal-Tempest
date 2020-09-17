/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.forecast;

import battle.Combatant;
import battle.Combatant.BaseStat;
import battle.Combatant.BattleRole;
import battle.Combatant.BattleStat;
import battle.item.Weapon;
import etherealtempest.info.Conveyer;
import fundamental.SupportTool;

/**
 *
 * @author night
 */
public class SupportForecast extends Forecast {
    private final SingularSupportForecast initiatorForecast, receiverForecast;
    
    public SupportForecast(Conveyer data, int fromRange) {
        this(
                new Combatant(data.getUnit(), BattleRole.Initiator),
                new Combatant(data.getOtherUnit(), BattleRole.Receiver),
                data,
                fromRange
        );
    }
    
    public SupportForecast(Combatant supporter, Combatant supported, Conveyer data, int range) {
        super(supporter, supported, data, range, true);
        
        supporter.setHPtoSubtract(data.getUnit().getEquippedFormula().getHPUsage());
        supporter.setTPtoSubtract(data.getUnit().getEquippedFormula().getTPUsage());
        
        SupportTool tool = (SupportTool)supporter.getUnit().getEquippedTool();
        
        initiatorForecast = new SingularSupportForecast(supporter);
        receiverForecast = new SingularSupportForecast(supported, tool.getBuffCalculator().calculate(data)); //receiver gets the bonuses
        
        initiatorForecast.displayedCrit = supporter.getBattleStat(BattleStat.Crit);
        receiverForecast.displayedCrit = 0;
        
        receiverForecast.addRecovery(tool.extraHeals);
        tool.getHealCalculator().calculate(data).forEach((heal) -> {
            receiverForecast.addRecovery(heal);
        });
    }
    
    public SingularSupportForecast getInitiatorForecast() { return initiatorForecast; }
    public SingularSupportForecast getReceiverForecast() { return receiverForecast; }
    
    public SingularSupportForecast getSpecifiedForecast(BattleRole br) {
        if (br == null) { return null; }
        
        if (br == BattleRole.Initiator) {
            return initiatorForecast;
        }
        
        return receiverForecast;
    }

    @Override
    public int calculateDesirabilityToInitiate() {
        int desirability = 0;
        
        if (receiverForecast.hpRecovered > 0) {
            desirability += Math.round(100f * receiverForecast.getCombatant().getBaseStat(BaseStat.maxHP) / receiverForecast.getCombatant().getUnit().currentHP);
        }
        
        if (receiverForecast.tpRecovered > 0) {
            desirability += Math.round(100f * receiverForecast.getCombatant().getBaseStat(BaseStat.maxTP) / receiverForecast.getCombatant().getUnit().currentTP);
        }
        
        Weapon wpn = receiverForecast.getCombatant().getUnit().getEquippedWPN();
        if (receiverForecast.durabilityRecovered > 0 && wpn != null) {
            desirability += Math.round(100f * wpn.getMaxDurability() / wpn.getCurrentDurability());
        }
        
        return desirability;
    }
    
}
