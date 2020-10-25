/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.skill;

import battle.Combatant;
import etherealtempest.info.Conveyer;
import battle.Strike;
import fundamental.stats.Bonus.StatType;
import fundamental.stats.StatBundle;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.tool.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class SkillEffect {
    protected Conveyer conv;
    protected Combatant user, opponent;
    
    protected final TalentConcept effect;
    
    private final List<Integer> extraRange;
    private final List<StatBundle> buffs;
    
    private String extraHitsDesc = "";
    
    public SkillEffect(List<Integer> extraRange, List<StatBundle> buffs) {
        this.extraRange = extraRange;
        this.buffs = buffs;
        
        effect = null;
    }
    
    public SkillEffect(int extra_range, List<StatBundle> buffs) {
        this.buffs = buffs;
        extraRange = new ArrayList<>();
        for (int i = 1; i <= extra_range; i++) {
            extraRange.add(i);
        }
        
        effect = null;
    }
    
    public SkillEffect(List<Integer> extraRange, List<StatBundle> buffs, TalentConcept effect) {
        this.extraRange = extraRange;
        this.buffs = buffs;
        this.effect = effect;
    }
    
    public SkillEffect(int extra_range, List<StatBundle> buffs, TalentConcept effect) {
        this.buffs = buffs;
        this.effect = effect;
        
        extraRange = new ArrayList<>();
        for (int i = 1; i <= extra_range; i++) {
            extraRange.add(i);
        }
    }
    
    public SkillEffect setExtraHitsDescription(String extraHitsDesc) {
        this.extraHitsDesc = extraHitsDesc;
        return this;
    }
    
    public TalentConcept getTalentConcept() {
        return effect;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public int extraHits() {
        return 0;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public int extraDamage() {
        return 0;
    }
    
    public List<Integer> getTrueRange(Tool tool) {
        List<Integer> ranges = tool.getRange();
        int max = ranges.get(ranges.size() - 1);
        
        extraRange.forEach((I) -> {
            ranges.add(max + I);
        });
        
        return ranges;
    }
    
    public void setData(Conveyer convey, Combatant striker, Combatant victim) {
        conv = convey;
        user = striker;
        opponent = victim;
    }
    
    public void applyExtraHits(List<Strike> holder) {
        for (int i = 0; i < extraHits(); i++) {
            holder.add(new Strike(user, opponent, false));
        } 
    }
    
    public void applyEffectsOnCombat(Combatant C) {
        buffs.forEach((stat) -> {
            if (stat.getStatType() == StatType.Battle) {
                C.appendToBattleStat(stat.getWhichBattleStat(), stat.getValue());
            } else { //base stat
                C.appendToBaseStat(stat.getWhichBaseStat(), stat.getValue());
            }
        });
    }
    
    public String effectDescription() {
        String description = "";
        for (StatBundle stat : buffs) {
            if (stat.getStatType() == StatType.Battle) {
                description += stat.getWhichBattleStat().getName();
            } else { //base stat
                description += stat.getWhichBaseStat().getName();
            }
            
            description += ": " + (stat.getValue() > 0 ? "+" : "") + stat.getValue() + '\n';
        }
        
        int extradmg = extraDamage();
        if (extradmg != 0) {
            description += "Extra Damage: " + extradmg;
        }
        
        String extraHits = "";
        if (extraHitsDesc.length() > 0) {
            extraHits = extraHitsDesc;
        } else {
            int extra = extraHits();
            if (extra > 0) {
                extraHits = extra > 0 ? ("Strikes " + (extra + 1) + " times in succession.") : "";
            }
        }
        
        return description + extraHits;
    }
    
}
