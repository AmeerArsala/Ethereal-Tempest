/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.tool;

import fundamental.stats.BaseStat;
import fundamental.stats.BattleStat;
import com.google.gson.annotations.SerializedName;
import fundamental.BattleVisual;
import fundamental.ability.Ability;
import fundamental.item.weapon.WeaponAttribute;
import fundamental.item.weapon.WeaponType;
import fundamental.stats.Bonus;
import fundamental.stats.Bonus.BonusType;
import fundamental.stats.RawBroadBonus;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition.Occasion;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author night
 */
public class Tool {
    public enum ToolType {
        @SerializedName("Purpose_Attack") Attack(0),
        @SerializedName("Purpose_SupportSelf") SupportSelf(1),
        @SerializedName("Purpose_SupportAlly") SupportAlly(2);
        
        private final int value;
        private ToolType(int val) {
            value = val;
        }
        
        public boolean isSupportive() {
            return value != 0;
        }
    }
    
    protected int CRIT;
    protected List<Integer> ranges;
    protected WeaponAttribute attribute;
    protected WeaponType type;
    protected RawBroadBonus onEquipEffect;
    
    protected String effects = "";
    protected BattleVisual animation;
    
    public Tool(int crt, List<Integer> toolRanges, RawBroadBonus onEquipBonus, WeaponAttribute attr, WeaponType toolType) {
        CRIT = crt;
        ranges = toolRanges;
        attribute = attr;
        type = toolType;
        
        if (onEquipBonus != null) {
            onEquipEffect = onEquipBonus;
            effects += "\nEffects: \n" + onEquipBonus.toString();
        }
    }
    
    public int getCRIT() { return CRIT; }
    
    public List<Integer> getRange() { return ranges; }
    
    public WeaponAttribute getAttribute() { return attribute; } //element
    public WeaponType getType() { return type; } //sword, axe, lance, etc
    
    public RawBroadBonus getOnEquipBonusEffect() { return onEquipEffect; }
    
    public BattleVisual getAnimation() { return animation; }
    
    public void setAnimation(BattleVisual anim) {
        animation = anim;
    }
    
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
        return calculateTotalBonus(occasion, (bonus) -> {
            return (bonus.getBaseStat() == stat && (filterBy == null || (bonus.getType() == filterBy) == include));
        });
    }
    
    public int getTotalBonus(BattleStat stat, Occasion occasion, BonusType filterBy, boolean include) {
        return calculateTotalBonus(occasion, (bonus) -> {
            return (bonus.getBattleStat() == stat && (filterBy == null || (bonus.getType() == filterBy) == include));
        });
    }
    
    private int calculateTotalBonus(Occasion occasion, Predicate<Bonus> addCondition) {
        int statBonus = 0;
        
        if (onEquipEffect.getBonusTalent() != null) {
            for (TalentConcept TC : onEquipEffect.getBonusTalent().getFullBody()) {
                if (TC.getTalentCondition().checkCondition(null, occasion)) {
                    List<Bonus> bonuses = TC.getTalentEffect().retrieveBuffs(null);
                    for (Bonus bonus : bonuses) {
                        if (addCondition.test(bonus)) {
                            statBonus += bonus.getValue();
                        }
                    }
                }
            }
        }
        
        return statBonus;
    }
}
