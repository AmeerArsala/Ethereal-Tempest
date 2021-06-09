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
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.RawBroadBonus;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition.Occasion;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Tool {
    protected int CRIT;
    protected List<Integer> ranges = new ArrayList<>();
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
    
    public Tool(int crt, List<Integer> toolRanges, RawBroadBonus adv, String attr, String toolType) {
        CRIT = crt;
        ranges = toolRanges;
        attribute = attr;
        type = toolType;
        
        if (adv != null) {
            onEquipTalent = adv.getBonusTalent();
            onEquipSkill = adv.getBonusSkill();
            onEquipAbility = adv.getBonusAbility();
            effects += "\nEffects: \n" + adv.toString();
        }
    }
    
    public int getCRIT() { return CRIT; }
    
    public List<Integer> getRange() { return ranges; }
    
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
    
    public int getTotalBonus(BaseStat stat, Occasion occasion, BonusType filterBy, boolean include) {
        int statBonus = 0;
        
        if (onEquipTalent != null) {
            for (TalentConcept TC : onEquipTalent.getFullBody()) {
                if (TC.getTalentCondition().checkCondition(null, occasion)) {
                    List<Bonus> bonuses = TC.getTalentEffect().retrieveBuffs(null);
                    for (Bonus bonus : bonuses) {
                        if (bonus.getBaseStat() == stat && (filterBy == null || (bonus.getType() == filterBy) == include)) {
                            statBonus += bonus.getValue();
                        }
                    }
                }
            }
        }
        
        return statBonus;
    }
    
    public int getTotalBonus(BattleStat stat, Occasion occasion, BonusType filterBy, boolean include) {
        int statBonus = 0;
        
        if (onEquipTalent != null) {
            for (TalentConcept TC : onEquipTalent.getFullBody()) {
                if (TC.getTalentCondition().checkCondition(null, occasion)) {
                    List<Bonus> bonuses = TC.getTalentEffect().retrieveBuffs(null);
                    for (Bonus bonus : bonuses) {
                        if (bonus.getBattleStat() == stat && (filterBy == null || (bonus.getType() == filterBy) == include)) {
                            statBonus += bonus.getValue();
                        }
                    }
                }
            }
        }
        
        return statBonus;
    } 
}
