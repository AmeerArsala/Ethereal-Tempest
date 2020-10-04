/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.tool;

import etherealtempest.info.Conveyer;
import fundamental.stats.Bonus;
import fundamental.Calculation;
import fundamental.stats.RawBroadBonus;
import fundamental.stats.Toll;
import java.util.List;

/**
 *
 * @author night
 */
public class SupportTool extends Tool {
    private final Calculation<Conveyer, Toll> healCalculator;
    private Calculation<Conveyer, Bonus> buffCalculator = null;
    
    public Toll extraHeals = null;
    
    public SupportTool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, Calculation<Conveyer, Toll> healCalculator) { //with neither skill nor talent
        super(crt, toolRanges, bonuses, attr, toolType);
        this.healCalculator = healCalculator;
    }
    
    public SupportTool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, RawBroadBonus adv, Calculation<Conveyer, Toll> healCalculator) { //with both skill and talent
        super(crt, toolRanges, bonuses, attr, toolType, adv);
        this.healCalculator = healCalculator;
    }
    
    public Calculation<Conveyer, Toll> getHealCalculator() {
        return healCalculator;
    }
    
    public Calculation<Conveyer, Bonus> getBuffCalculator() {
        return buffCalculator;
    }
    
    public SupportTool setExtraHeals(Toll heals) {
        extraHeals = heals;
        return this;
    }
    
    @Override
    public String toString() {
        String stats = healCalculator.description() + '\n';
        
        if (buffCalculator != null) {
            stats += buffCalculator.description() + '\n';
        }
        
        return     
                   stats + '\n'
                +  "Crit: " + CRIT + '\n'
                + "Range: " + getRangeString() + '\n'
                + "Attribute: " + attribute + '\n'
                + effects;
    }
    
}
