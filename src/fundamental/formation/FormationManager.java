/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formation;

import fundamental.AttributeManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class FormationManager extends AttributeManager<Formation> {
    
    public FormationManager(List<Formation> formations) {
        super
        (
            formations.isEmpty() ? new ArrayList<>() : Arrays.asList(formations.get(0)), 
            formations.isEmpty() ? new ArrayList<>() : subList(formations, 1, formations.size()), 
            1
        );
    }
    
    public FormationManager(Formation equippedFormation, List<Formation> unequippedFormations) {
        super(Arrays.asList(equippedFormation), unequippedFormations, 1);
    }
    
    public FormationManager(Formation equippedFormation) {
        super(Arrays.asList(equippedFormation), 1);
    }
    
    private static List<Formation> subList(List<Formation> list, int start, int end) {
        List<Formation> sub = new ArrayList<>();
        
        for (int i = start; i < end; ++i) {
            sub.add(list.get(i));
        }
        
        return sub;
    }
    
    public Formation getEquippedFormation() {
        return !equipped.isEmpty() ? equipped.get(0) : null;
    }
    
    public List<Integer> getPartialFormationRange(boolean supportive) {
        Formation equippedFormation = getEquippedFormation();
        if (equippedFormation != null) {
            return equippedFormation.getPartialRange(supportive);
        }
        
        return new ArrayList<>();
    }
    
    public List<Integer> getFullFormationRange() {
        Formation equippedFormation = getEquippedFormation();
        if (equippedFormation != null) {
            return equippedFormation.getFullRange();
        }
        
        return new ArrayList<>();
    }
    
    /**
     *
     * @param forma a Formation from the unequipped List
     */
    @Override
    public void equip(Formation forma) {
        unequipped.remove(forma);
        
        if (!equipped.isEmpty()) {
            unequip(0);
        }
        
        equipped.add(forma);
    }
    
    /**
     *
     * @param index the index of a Formation in the unequipped List
     */
    @Override
    public void equip(int index) {
        if (!equipped.isEmpty()) {
            unequip(0);
        }
        
        equipped.add(unequipped.get(index));
        unequipped.remove(index);
    }
}
