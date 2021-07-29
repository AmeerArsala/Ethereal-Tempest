/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.tool;

import etherealtempest.info.Conveyor;
import fundamental.Calculation;
import fundamental.item.weapon.WeaponAttribute;
import fundamental.item.weapon.WeaponType;
import fundamental.RawBroadBonus;
import fundamental.stats.alteration.Toll;
import java.util.List;

/**
 *
 * @author night
 */
public class SupportTool extends Tool {
    private final Calculation<Conveyor, Toll> healCalculator;
    
    public Toll extraHeals = null;
    
    public SupportTool(int crt, List<Integer> toolRanges, WeaponAttribute attr, WeaponType toolType, RawBroadBonus adv, Calculation<Conveyor, Toll> healCalculator) {
        super(crt, toolRanges, adv, attr, toolType);
        this.healCalculator = healCalculator;
    }
    
    public Calculation<Conveyor, Toll> getHealCalculator() {
        return healCalculator;
    }
    
    public SupportTool setExtraHeals(Toll heals) {
        extraHeals = heals;
        return this;
    }
    
    @Override
    public String toString() {
        String stats = healCalculator.description() + '\n';
        
        return     
                   stats + '\n'
                +  "Crit: " + CRIT + '\n'
                + "Range: " + getRangeString() + '\n'
                + "Attribute: " + attribute + '\n'
                + effects;
    }
    
}
