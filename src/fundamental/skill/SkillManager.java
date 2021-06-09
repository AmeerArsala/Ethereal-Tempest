/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.skill;

import fundamental.AttributeManager;
import fundamental.formation.Formation;
import fundamental.tool.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class SkillManager extends AttributeManager<Skill> {
    
    public SkillManager(List<Skill> skillsEquipped, int maximumEquippedSkills) {
        super(skillsEquipped, maximumEquippedSkills);
    }
    
    public SkillManager(List<Skill> skillsEquipped, List<Skill> skillsUnequipped, int maximumEquippedSkills) {
        super(skillsEquipped, skillsUnequipped, maximumEquippedSkills);
    }
    
    public SkillManager(int maximumEquippedSkills) {
        super(maximumEquippedSkills);
    }
    
    public void setMaxEquippedSkills(int maxEquippedSkills) {
        maxEquippedCapacity = maxEquippedSkills;
    }
    
    public List<Skill> getSkills(Tool tool, Formation equippedFormation) {
        List<Skill> full = new ArrayList<>();
        full.addAll(equipped);
        
        if (tool != null && tool.getOnEquipBonusEffect() != null && tool.getOnEquipBonusEffect().getBonusSkill() != null) {
            full.add(tool.getOnEquipBonusEffect().getBonusSkill());
        }
        
        if (
            equippedFormation != null &&
            equippedFormation.getPassiveBonusEffect() != null &&
            equippedFormation.getPassiveBonusEffect().getBonusSkill() != null
        ) 
        {
            full.add(equippedFormation.getPassiveBonusEffect().getBonusSkill());
        }
        
        return full;
    }
    
    public List<Integer> getPartialSkillRange(Tool tool, Formation equippedFormation, boolean supportive) {
        List<Integer> fullRange = new ArrayList<>();
        
        getSkills(tool, equippedFormation).forEach((skill) -> {
            if (skill.getType().isSupportive() == supportive) {
                //for each Integer 'range' in skill.getEffect().getTrueRange(tool), if fullRange does not contain a value equivalent to this Integer, add it to fullRange
                skill.getEffect().getTrueRange(tool).stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                    fullRange.add(range);
                });
            }
        });
        
        return fullRange;
    }
    
    public List<Integer> getFullSkillRange(Tool tool, Formation equippedFormation) {
        List<Integer> fullRange = new ArrayList<>();
        
        getSkills(tool, equippedFormation).forEach((skill) -> {
            //for each Integer 'range' in skill.getEffect().getTrueRange(tool), if fullRange does not contain a value equivalent to this Integer, add it to fullRange
            skill.getEffect().getTrueRange(tool).stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                fullRange.add(range);
            });
        });
        
        return fullRange;
    }
}