/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.data.forecast;

import battle.data.participant.Combatant;
import battle.data.participant.BattleRole;
import fundamental.item.weapon.Weapon;
import etherealtempest.info.Conveyor;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.tool.SupportTool;

/**
 *
 * @author night
 */
public class SupportForecast extends Forecast<SingularSupportForecast> {
    public SupportForecast(Conveyor data) {
        this(
                new Combatant(data.getUnit(), BattleRole.Initiator),
                new Combatant(data.getOtherUnit(), BattleRole.Receiver),
                data
        );
    }
    
    public SupportForecast(Combatant supporter, Combatant supported, Conveyor data) {
        super
        (
            (RANGE) -> { return new SingularSupportForecast(supporter, supported, RANGE); }, 
            (RANGE) -> { return new SingularSupportForecast(supported, supporter, RANGE); }, //receiver gets the bonuses 
            data,
            true
        );
        
        SupportTool tool = (SupportTool)supporter.getUnit().getEquippedTool();
        
        initiatorForecast.displayedCrit = supporter.getBattleStat(BattleStat.Crit);
        receiverForecast.displayedCrit = 0;
        
        receiverForecast.addRecovery(tool.extraHeals);
        tool.getHealCalculator().calculate(data).forEach((heal) -> {
            receiverForecast.addRecovery(heal);
        });
    }

    @Override
    public int calculateDesirabilityToInitiate() {
        int desirability = 0;
        
        if (receiverForecast.hpRecovered > 0) {
            desirability += Math.round(100f * receiverForecast.getCombatant().getBaseStat(BaseStat.MaxHP) / receiverForecast.getCombatant().getUnit().getBaseStat(BaseStat.CurrentHP));
        }
        
        if (receiverForecast.tpRecovered > 0) {
            desirability += Math.round(100f * receiverForecast.getCombatant().getBaseStat(BaseStat.MaxTP) / receiverForecast.getCombatant().getUnit().getBaseStat(BaseStat.CurrentTP));
        }
        
        Weapon wpn = receiverForecast.getCombatant().getUnit().getEquippedWeapon();
        if (receiverForecast.durabilityRecovered > 0 && wpn != null) {
            desirability += Math.round(100f * wpn.getMaxDurability() / wpn.getCurrentDurability());
        }
        
        return desirability;
    }
    
}
