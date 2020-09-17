/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import etherealtempest.info.Conveyer;
import battle.skill.Skill;
import battle.talent.Talent;
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
    
    public SupportTool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, Talent tOnEquip, Skill sOnEquip, Calculation<Conveyer, Toll> healCalculator) { //with both skill and talent
        super(crt, toolRanges, bonuses, attr, toolType, tOnEquip, sOnEquip);
        this.healCalculator = healCalculator;
    }
    
    public SupportTool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, Talent tOnEquip, Calculation<Conveyer, Toll> healCalculator) { //with just a talent
        super(crt, toolRanges, bonuses, attr, toolType, tOnEquip);
        this.healCalculator = healCalculator;
    }
    
    public SupportTool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, Skill sOnEquip, Calculation<Conveyer, Toll> healCalculator) { //with just a skill
        super(crt, toolRanges, bonuses, attr, toolType, sOnEquip);
        this.healCalculator = healCalculator;
    }
    
    public SupportTool setBuffs(Calculation<Conveyer, Bonus> buffCalculator) {
        this.buffCalculator = buffCalculator;
        return this;
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
                + "Attribute: " + attribute + '\n';
    }
    
}
