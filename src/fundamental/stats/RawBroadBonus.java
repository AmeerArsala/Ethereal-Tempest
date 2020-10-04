/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import fundamental.ability.Ability;
import fundamental.skill.Skill;
import fundamental.stats.Bonus.StatType;
import fundamental.talent.Talent;

/**
 *
 * @author night
 */
public class RawBroadBonus {
    private StatBundle statBonus = null;
    private Skill acquiredSkill = null;
    private Talent acquiredTalent = null;
    private Ability acquiredAbility = null;
    
    public RawBroadBonus() {}
    
    public RawBroadBonus(StatBundle statBonus) {
        this.statBonus = statBonus;
    }
    
    public RawBroadBonus(Talent acquiredTalent, Skill acquiredSkill, Ability acquiredAbility) {
        this.acquiredTalent = acquiredTalent;
        this.acquiredSkill = acquiredSkill;
        this.acquiredAbility = acquiredAbility;
    }
    
    public RawBroadBonus(StatBundle statBonus, Talent acquiredTalent, Skill acquiredSkill, Ability acquiredAbility) {
        this.statBonus = statBonus;
        this.acquiredTalent = acquiredTalent;
        this.acquiredSkill = acquiredSkill;
        this.acquiredAbility = acquiredAbility;
    }
    
    public RawBroadBonus setStatBonus(StatBundle statBonus) {
        this.statBonus = statBonus;
        return this;
    }
    
    public RawBroadBonus setBonusSkill(Skill acquiredSkill) {
        this.acquiredSkill = acquiredSkill;
        return this;
    }
    
    public RawBroadBonus setBonusTalent(Talent acquiredTalent) {
        this.acquiredTalent = acquiredTalent;
        return this;
    }
    
    public RawBroadBonus setBonusAbility(Ability acquiredAbility) {
        this.acquiredAbility = acquiredAbility;
        return this;
    }
    
    public StatBundle getStatBonus() { return statBonus; }
    public Skill getBonusSkill() { return acquiredSkill; }
    public Talent getBonusTalent() { return acquiredTalent; }
    public Ability getBonusAbility() { return acquiredAbility; }
    
    @Override
    public String toString() {
        String desc = "";
        
        if (statBonus != null) {
            if (statBonus.getStatType() == StatType.Base) {
                desc += statBonus.getWhichBaseStat().getName() + " +" + statBonus.getValue() + "\n";
            } else if (statBonus.getStatType() == StatType.Battle) {
                desc += statBonus.getWhichBattleStat().getName() + " +" + statBonus.getValue() + "\n";
            }
        }
        
        if (acquiredSkill != null) {
            desc += "Grants the Skill '" + acquiredSkill.getName() + "'\n";
        }
        
        if (acquiredAbility != null) {
            desc += "Grants the Ability '" + acquiredAbility.getName() + "'\n";
        }
        
        if (acquiredTalent != null) {
            desc += acquiredTalent.getDescription();
        }
        
        return desc;
    }
}
