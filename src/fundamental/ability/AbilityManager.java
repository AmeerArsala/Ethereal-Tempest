/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.ability;

import etherealtempest.info.Conveyor;
import fundamental.AttributeManager;
import fundamental.formation.Formation;
import fundamental.tool.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class AbilityManager extends AttributeManager<Ability> {
    public AbilityManager(List<Ability> equippedAbilities, int maxEquippedAbilities) {
        super(equippedAbilities, maxEquippedAbilities);
    }
    
    public AbilityManager(List<Ability> equippedAbilities, List<Ability> unequippedAbilities, int maxEquippedAbilities) {
        super(equippedAbilities, unequippedAbilities, maxEquippedAbilities);
    }
    
    public AbilityManager(int maxEquippedAbilities) {
        super(maxEquippedAbilities);
    }
    
    public void setMaxEquippedAbilities(int maxEquippedAbilities) {
        maxEquippedCapacity = maxEquippedAbilities;
    }
    
    public List<Ability> getAbilities(Tool tool, Formation equippedFormation) {
        List<Ability> full = new ArrayList<>();
        full.addAll(equipped);
        
        if (tool != null && tool.getOnEquipBonusEffect() != null && tool.getOnEquipBonusEffect().getBonusAbility() != null) {
            full.add(tool.getOnEquipBonusEffect().getBonusAbility());
        }
        
        if (
            equippedFormation != null &&
            equippedFormation.getPassiveBonusEffect() != null &&
            equippedFormation.getPassiveBonusEffect().getBonusAbility() != null
        ) 
        {
            full.add(equippedFormation.getPassiveBonusEffect().getBonusAbility());
        }
        
        return full;
    }
    
    public boolean anyAbilityAllowed(Tool tool, Formation equippedFormation, Conveyor conv) {
        return getAbilities(tool, equippedFormation).stream().anyMatch((ability) -> (ability.canBeUsed(conv)));
    }
}
