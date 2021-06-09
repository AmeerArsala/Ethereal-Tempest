/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import com.simsilica.lemur.Command;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 * @param <A> attribute such as Skill, Ability, Talent, etc.
 */
public class AttributeManager<A> {
    protected final List<A> equipped = new ArrayList<>();
    protected final List<A> unequipped = new ArrayList<>();
    
    protected int maxEquippedCapacity;
    
    public AttributeManager(List<A> equippedAs, int maxEquipAllowed) {
        equipped.addAll(equippedAs);
        maxEquippedCapacity = maxEquipAllowed;
    }
    
    public AttributeManager(List<A> equippedAs, List<A> unequippedAs, int maxEquipAllowed) {
        equipped.addAll(equippedAs);
        unequipped.addAll(unequippedAs);
        maxEquippedCapacity = maxEquipAllowed;
    }
    
    public AttributeManager(int maxEquipAllowed) {
        maxEquippedCapacity = maxEquipAllowed;
    }
    
    public List<A> getEquipped() { return equipped; }
    public List<A> getUnequipped() { return unequipped; }
    
    public int getMaxNumberEquipped() { return maxEquippedCapacity; }
    
    public List<A> getAll() {
        List<A> all = new ArrayList<>();
        all.addAll(equipped);
        all.addAll(unequipped);
        
        return all;
    }
    
    /**
     *
     * @param attr the 'A' to be learned
     * @return If adding this 'A' to be equipped will exceed maxEquippedCapacity, it returns a Command that unequips the 'A' in a parameter (the player can choose).
     *         Otherwise, it returns null
     */
    public Command<A> learn(A attr) {
        equipped.add(attr);
        if (equipped.size() > maxEquippedCapacity) {
            return (a) -> {
                unequip(a);
            };
        }
        
        return null;
    }
    
    /**
     *
     * @param attr an 'A' from the unequipped List
     */
    public void equip(A attr) {
        unequipped.remove(attr);
        equipped.add(attr);
    }
    
    /**
     *
     * @param index the index of an 'A' in the unequipped List
     */
    public void equip(int index) {
        equipped.add(unequipped.get(index));
        unequipped.remove(index);
    }
    
    /**
     *
     * @param attr an 'A' from the equipped List
     */
    public void unequip(A attr) {
        equipped.remove(attr);
        unequipped.add(attr);
    }
    
    /**
     *
     * @param index the index of an 'A' in the equipped List
     */
    public void unequip(int index) {
        unequipped.add(equipped.get(index));
        equipped.remove(index);
    }
}
