/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formula;

import fundamental.AttributeManager;
import fundamental.ability.Ability;
import fundamental.skill.Skill;
import fundamental.talent.Talent;
import fundamental.tool.Tool;
import fundamental.unit.aspect.UnitAllegiance;
import java.util.ArrayList;
import java.util.List;
import maps.layout.MapCoords;

/**
 *
 * @author night
 */
public class FormulaManager extends AttributeManager<Formula> {
    
    public FormulaManager(List<Formula> equippedFormulas, int maxEquippedFormulas) {
        super(equippedFormulas, maxEquippedFormulas);
    }
    
    public FormulaManager(List<Formula> equippedFormulas, List<Formula> unequippedFormulas, int maxEquippedFormulas) {
        super(equippedFormulas, unequippedFormulas, maxEquippedFormulas);
    }
    
    public FormulaManager(int maxEquippedFormulas) {
        super(maxEquippedFormulas);
    }
    
    public void setMaxEquippedFormulas(int maxEquippedFormulas) {
        maxEquippedCapacity = maxEquippedFormulas;
    }
    
    public int getNumOfEquippedFormulas() {
        return equipped.size();
    }
    
    public List<Integer> getPartialFormulaRange(boolean supportive) {
        List<Integer> fullRange = new ArrayList<>();
        
        addPartialFormulaRanges(fullRange, supportive);
        
        return fullRange;
    }
    
    public List<Integer> getFullFormulaRange() {
        List<Integer> fullRange = new ArrayList<>();
        
        addFullFormulaRanges(fullRange);
        
        return fullRange;
    }
    
    public List<Formula> getAvailableFormulas(MapCoords atPosition, UnitAllegiance allegiance, int currentHP, int currentTP) {
        List<Formula> usableFormulas = new ArrayList<>();
        
        for (Formula F : equipped) {
            if (F.isAvailableAt(atPosition, allegiance, currentHP, currentTP)) {
                usableFormulas.add(F);
            }
        }
        
        return usableFormulas;
    }
    
    public void addPartialFormulaRanges(List<Integer> fullRange, boolean supportive) {
        equipped.forEach((formula) -> {
            if (formula.getFormulaPurpose().isSupportive() == supportive) {
                formula.getActualFormulaData().getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                    fullRange.add(range);
                });
            }
        });
    }
    
    public void addFullFormulaRanges(List<Integer> fullRange) {
        equipped.forEach((formula) -> {
            formula.getActualFormulaData().getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                fullRange.add(range);
            });
        });
    }
    
    public boolean hasSupportingFormulas() {
        return hasSelfSupportingFormulas() || hasAllySupportingFormulas();
    }
    
    public boolean hasSelfSupportingFormulas() {
        return equipped.stream().anyMatch((formula) -> (formula.getFormulaPurpose() == Tool.ToolType.SupportSelf));
    }
    
    public boolean hasAllySupportingFormulas() {
        return equipped.stream().anyMatch((formula) -> (formula.getFormulaPurpose() == Tool.ToolType.SupportAlly));
    }
    
    public boolean hasAttackingFormulas() {
        return equipped.stream().anyMatch((formula) -> (formula.getFormulaPurpose() == Tool.ToolType.Attack));
    }
    
    public List<Talent> getPassiveTalents() {
        List<Talent> passives = new ArrayList<>();
        
        for (Formula I : equipped) {
            if (I.getPassiveBonusEffect() != null && I.getPassiveBonusEffect().getBonusTalent() != null) {
                passives.add(I.getPassiveBonusEffect().getBonusTalent());
            }
        }
        
        return passives;
    }
    
    public List<Skill> getPassiveSkills() {
        List<Skill> passives = new ArrayList<>();
        
        for (Formula I : equipped) {
            if (I.getPassiveBonusEffect() != null && I.getPassiveBonusEffect().getBonusSkill() != null) {
                passives.add(I.getPassiveBonusEffect().getBonusSkill());
            }
        }
        
        return passives;
    }
    
    public List<Ability> getPassiveAbilities() {
        List<Ability> passives = new ArrayList<>();
        
        for (Formula I : equipped) {
            if (I.getPassiveBonusEffect() != null && I.getPassiveBonusEffect().getBonusAbility() != null) {
                passives.add(I.getPassiveBonusEffect().getBonusAbility());
            }
        }
        
        return passives;
    }
    
}
