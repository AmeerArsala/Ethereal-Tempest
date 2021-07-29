/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import fundamental.stats.alteration.Bonus;
import fundamental.ability.Ability;
import fundamental.skill.Skill;
import fundamental.stats.StatBundle;
import fundamental.stats.alteration.Bonus.BonusType;
import fundamental.stats.alteration.Bonus.StatType;
import fundamental.talent.Talent;
import fundamental.talent.TalentCondition.Occasion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        this(Arrays.asList(statBonus));
    }
    
    public RawBroadBonus(List<StatBundle> statBonuses) {
        List<Bonus> bonuses = new ArrayList<>(statBonuses.size());
        statBonuses.forEach((bundle) -> {
            bonuses.add(bundle.toRawBonus());
        });
        
        acquiredTalent = Talent.Bonus(bonuses, Occasion.Indifferent);
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
            desc += "Grants the Skill '" + acquiredSkill.getName() + "\n";
        }
        
        if (acquiredAbility != null) {
            desc += "Grants the Ability '" + acquiredAbility.getName() + "\n";
        }
        
        if (acquiredTalent != null) {
            desc += acquiredTalent.getDescription();
        }
        
        return desc;
    }
}
