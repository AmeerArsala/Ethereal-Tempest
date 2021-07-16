/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.skill;

import battle.data.participant.Combatant;
import etherealtempest.info.Conveyor;
import battle.data.event.Strike;
import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import fundamental.stats.Bonus.StatType;
import fundamental.stats.StatBundle;
import fundamental.talent.Talent;
import fundamental.tool.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public abstract class SkillEffect {
    private final Talent effect;
    private final List<Integer> extraRange;
    private final List<StatBundle> buffs;
    private final int hits;
    private final int extraDamage;
    
    private String extraHitsDesc;
    
    public SkillEffect(List<Integer> extraRange, List<StatBundle> buffs, int hits, int extraDamage, Talent effect) {
        this.extraRange = extraRange;
        this.buffs = buffs;
        this.hits = hits;
        this.extraDamage = extraDamage;
        this.effect = effect;
    }
    
    public SkillEffect(int extra_range, List<StatBundle> buffs, int hits, int extraDamage, Talent effect) {
        this.buffs = buffs;
        this.effect = effect;
        this.hits = hits;
        this.extraDamage = extraDamage;
        
        extraRange = new ArrayList<>();
        for (int i = 1; i <= extra_range; i++) {
            extraRange.add(i);
        }
    }
    
    public SkillEffect setExtraHitsDescription(String extraHitsDesc) { //use for custom descriptions
        this.extraHitsDesc = extraHitsDesc;
        return this;
    }
    
    public Talent getTalent() {
        return effect;
    }
    
    public int getHits() {
        return hits;
    }
    
    public int getExtraDamage() {
        return extraDamage;
    }
    
    //OVERRIDE THIS WHEN NEEDED
    public boolean continueFightingCondition(int BP, Conveyor context, Combatant user, Combatant opponent) {
        return BP > 0;
    }
    
    public List<Integer> getTrueRange(Tool tool) {
        List<Integer> ranges = tool.getRange();
        int max = ranges.get(ranges.size() - 1);
        
        extraRange.forEach((I) -> {
            ranges.add(max + I);
        });
        
        return ranges;
    }
    
    public List<Strike> calculateExtraStrikes(Combatant user, Combatant opponent) {
        List<Strike> extra = new ArrayList<>();
        for (int i = 1; i < hits; ++i) {
            extra.add(Strike.SimpleStrike(user, user, true));
        }
        
        return extra;
    }
    
    public void applyBuffsOnCombat(Combatant C) {
        buffs.forEach((stat) -> {
            if (stat.getStatType() == StatType.Battle) {
                C.appendToBattleStat((BattleStat)stat.getStat(), stat.getValue());
            } else { //base stat
                C.appendToBaseStat((BaseStat)stat.getStat(), stat.getValue());
            }
        });
    }
    
    public String effectDescription() {
        String description = "";
        for (StatBundle stat : buffs) {
            if (stat.getStatType() == StatType.Battle) {
                description += ((BattleStat)stat.getStat()).getName();
            } else { //base stat
                description += ((BaseStat)stat.getStat()).getName();
            }
            
            description += ": " + (stat.getValue() > 0 ? "+" : "") + stat.getValue() + '\n';
        }
        
        if (extraDamage != 0) {
            description += "Extra Damage: " + extraDamage;
        }
        
        String extraHits = "";
        if (extraHitsDesc != null) {
            extraHits = extraHitsDesc;
        } else {
            if (hits > 1) {
                extraHits = hits > 0 ? ("Strikes " + hits + " times in succession.") : "";
            }
        }
        
        return description + extraHits;
    }
    
}
