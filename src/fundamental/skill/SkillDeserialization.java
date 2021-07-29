/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.skill;

import fundamental.stats.BattleStat;
import fundamental.stats.StatBundle;
import fundamental.stats.StatBundleDeserialization;
import fundamental.stats.alteration.Toll;
import fundamental.talent.Talent;
import fundamental.talent.TalentConcept;
import fundamental.tool.Tool.ToolType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class SkillDeserialization {
    private String name;
    private String desc;
    private String iconPath;
    private ToolType toolType;
    private Toll cost;
    private int[] customExtraRange;
    private int extraRange;
    private int extraDamage;
    private int hits;
    private StatBundleDeserialization[] buffs;
    
    public SkillDeserialization() {}
    
    public SkillDeserialization(String name, String desc, String iconPath, ToolType toolType, Toll cost, int[] customExtraRange, int extraRange, int extraDamage, int hits, StatBundleDeserialization[] buffs) {
        this.name = name;
        this.desc = desc;
        this.iconPath = iconPath;
        this.toolType = toolType;
        this.cost = cost;
        this.customExtraRange = customExtraRange;
        this.extraRange = extraRange;
        this.extraDamage = extraDamage;
        this.hits = hits;
        this.buffs = buffs;
    }
    
    private List<StatBundle> buffsToList() {
        List<StatBundle> bonuses = new ArrayList<>();
        for (StatBundleDeserialization SBD : buffs) {
            bonuses.add(SBD.constructStatBundle());
        }
        
        return bonuses;
    }
    
    private List<Integer> customExtraRangeToList() {
        List<Integer> ranges = new ArrayList<>();
        for (int range : customExtraRange) {
            ranges.add(range);
        }
        
        return ranges;
    }
    
    public Skill constructSkill(Talent effect) {
        SkillEffect skillEffect;
        
        if (customExtraRange != null) {
            skillEffect = new SkillEffect(customExtraRangeToList(), buffsToList(), hits, extraDamage, effect) {};
        } else {
            skillEffect = new SkillEffect(extraRange, buffsToList(), hits, extraDamage, effect) {};
        }
        
        return constructSkillWithEffect(skillEffect);
    }
    
    public Skill constructSkillWithEffect(SkillEffect skillEffect) {
        return new Skill(name, desc, iconPath, toolType, cost, skillEffect);
    }
}
