/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.talent;

import fundamental.AttributeManager;
import fundamental.formation.Formation;
import fundamental.tool.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class TalentManager extends AttributeManager<Talent> {
    public static final int MAX_EQUIPPED_TALENTS = 6;

    public TalentManager(List<Talent> equippedTalents) {
        super(equippedTalents, MAX_EQUIPPED_TALENTS);
    }
    
    public TalentManager(List<Talent> equippedTalents, List<Talent> unequippedTalents, int maxEquippedTalents) {
        super(equippedTalents, unequippedTalents, MAX_EQUIPPED_TALENTS);
    }
    
    public TalentManager() {
        super(MAX_EQUIPPED_TALENTS);
    }
    
    public List<Talent> getTalents(Tool tool, Formation equippedFormation) {
        List<Talent> full = new ArrayList<>();
        full.addAll(equipped);
        
        if (tool != null && tool.getOnEquipBonusEffect() != null && tool.getOnEquipBonusEffect().getBonusTalent() != null) {
            full.add(tool.getOnEquipBonusEffect().getBonusTalent());
        }
        
        if (
            equippedFormation != null &&
            equippedFormation.getPassiveBonusEffect() != null &&
            equippedFormation.getPassiveBonusEffect().getBonusTalent() != null
        ) 
        {
            full.add(equippedFormation.getPassiveBonusEffect().getBonusTalent());
        }
        
        return full;
    }
}
