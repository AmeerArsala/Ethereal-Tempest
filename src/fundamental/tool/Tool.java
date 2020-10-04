/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.tool;

import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import fundamental.ability.Ability;
import fundamental.skill.Skill;
import fundamental.talent.Talent;
import fundamental.stats.Bonus;
import fundamental.stats.RawBroadBonus;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Tool {
    protected int CRIT;
    protected List<Integer> ranges = new ArrayList<>();
    protected List<Bonus> passiveBonusesOnEquip = new ArrayList<>(); //applies to both base and battle stats
    protected String attribute = "None";
    protected String type;
    
    protected Talent onEquipTalent = null;
    protected Skill onEquipSkill = null;
    protected Ability onEquipAbility = null;
    
    protected String effects = "";
    
    public enum ToolType {
        Attack(0),
        SupportSelf(1),
        SupportAlly(2);
        
        private final int value;
        private ToolType(int val) {
            value = val;
        }
        
        public boolean isSupportive() {
            return value != 0;
        }
    }
    
    public Tool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType) { //with neither skill nor talent nor ability
        CRIT = crt;
        ranges = toolRanges;
        passiveBonusesOnEquip = bonuses;
        attribute = attr;
        type = toolType;
    }
    
    public Tool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, RawBroadBonus adv) { //does NOT take into account stat bonus on RawBroadBonus
        CRIT = crt;
        ranges = toolRanges;
        passiveBonusesOnEquip = bonuses;
        attribute = attr;
        type = toolType;
        
        onEquipTalent = adv.getBonusTalent();
        onEquipSkill = adv.getBonusSkill();
        onEquipAbility = adv.getBonusAbility();
        
        effects += "\nEffects: \n" + adv.toString();
    }
    
    public int getCRIT() { return CRIT; }
    
    public List<Integer> getRange() { return ranges; }
    public List<Bonus> getBonuses() { return passiveBonusesOnEquip; }
    
    public String getAttribute() { return attribute; } //element
    
    public Talent getOnEquipTalent() { return onEquipTalent; }
    public Skill getOnEquipSkill() { return onEquipSkill; }
    public Ability getOnEquipAbility() { return onEquipAbility; }
    
    public String getType() { return type; } //sword, axe, lance, etc
    
    public String getRangeString() {
        String rngstr = "" + ranges.get(0);
        boolean dashPlaced = false;
        for (int i = 1; i < ranges.size(); i++) {
            if (ranges.get(i) - ranges.get(i - 1) == 1) {
                if (!dashPlaced) {
                    rngstr += "-";
                    dashPlaced = true;
                }
                
                if (i + 1 == ranges.size() || ranges.get(i + 1) - ranges.get(i) > 1) { //if next item no longer continues the 1 difference trend
                    rngstr += ranges.get(i);
                    dashPlaced = false;
                }
            } else if (ranges.get(i) - ranges.get(i - 1) > 1) {
                rngstr += ", " + ranges.get(i);
            }
        }
        
        return rngstr;
    }
    
    public int getTotalBonus(BaseStat stat) {
        int statBonus = 0;
        for (Bonus bonus : passiveBonusesOnEquip) {
            if (bonus.getBaseStat() == stat) {
                statBonus += bonus.getValue();
            }
        }
        
        return statBonus;
    }
    
    public int getTotalBonus(BattleStat stat) {
        int statBonus = 0;
        for (Bonus bonus : passiveBonusesOnEquip) {
            if (bonus.getBattleStat() == stat) {
                statBonus += bonus.getValue();
            }
        }
        
        return statBonus;
    }
}
