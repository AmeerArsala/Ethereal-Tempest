/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import fundamental.ability.Ability;
import fundamental.skill.Skill;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.Bonus.StatType;
import fundamental.talent.Talent;
import fundamental.talent.TalentCondition;
import fundamental.talent.TalentCondition.Occasion;
import java.util.Arrays;

/**
 *
 * @author night
 */
public class RawBroadBonus {
    private Skill acquiredSkill = null;
    private Talent acquiredTalent = null;
    private Ability acquiredAbility = null;
    
    public RawBroadBonus() {}
    
    public RawBroadBonus(Talent acquiredTalent, Skill acquiredSkill, Ability acquiredAbility) {
        this.acquiredTalent = acquiredTalent;
        this.acquiredSkill = acquiredSkill;
        this.acquiredAbility = acquiredAbility;
    }
    
    public RawBroadBonus(StatBundle statBonus, Skill acquiredSkill, Ability acquiredAbility) {
        this(statBonus);
        this.acquiredSkill = acquiredSkill;
        this.acquiredAbility = acquiredAbility;
    }
    
    public RawBroadBonus(StatBundle statBonus) {
        if (statBonus.getStatType() == StatType.Base) {
            acquiredTalent = Talent.Bonus(Arrays.asList(new Bonus(statBonus.getValue(), BonusType.Raw, statBonus.getWhichBaseStat())), Occasion.Indifferent);
        } else if (statBonus.getStatType() == StatType.Battle) {
            acquiredTalent = Talent.Bonus(Arrays.asList(new Bonus(statBonus.getValue(), BonusType.Raw, statBonus.getWhichBattleStat())), Occasion.Indifferent);
        }
    }
    
    public RawBroadBonus(Talent acquiredTalent) {
        this.acquiredTalent = acquiredTalent;
    }
    
    public RawBroadBonus(Skill acquiredSkill) {
        this.acquiredSkill = acquiredSkill;
    }
    
    public RawBroadBonus(Ability acquiredAbility) {
        this.acquiredAbility = acquiredAbility;
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
    
    public Skill getBonusSkill() { return acquiredSkill; }
    public Talent getBonusTalent() { return acquiredTalent; }
    public Ability getBonusAbility() { return acquiredAbility; }
    
    @Override
    public String toString() {
        String desc = "";
        
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
