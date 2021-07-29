/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import fundamental.ability.Ability;
import fundamental.item.weapon.Weapon;
import fundamental.skill.Skill;
import fundamental.talent.Talent;
import fundamental.unit.aspect.UnitAllegiance;
import java.util.ArrayList;
import java.util.List;
import maps.layout.Coords;
import maps.layout.MapCoords;

/**
 *
 * @author night
 */
public class Inventory {
    public static final int DEFAULT_MAX_SPACE = 10;
    
    private final List<Item> items;
    private int maxSpace = DEFAULT_MAX_SPACE;
    
    public Inventory(List<Item> items) {
        this.items = items;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public Item getFirstItem() {
        return items.get(0);
    }
    
    public int getSpace(int physique) {
        int difference = physique - combinedInventoryWeight();
        return maxSpace - (difference < 0 ? difference : 0);
    }
    
    public int combinedInventoryWeight() {
        int wt = 0;
        for (Item item : items) {
            wt += item.getWeight();
        }
        
        return wt;
    }
    
    public int getMaxSpace() { return maxSpace; }
    
    public void setMaxSpace(int max) {
        maxSpace = max;
    }
    
    public int getNumberOfItems() {
        return items.size();
    }
    
    public List<Talent> getPassiveTalents() {
        List<Talent> passives = new ArrayList<>();
        
        for (Item I : items) {
            if (I.getPassiveBonusEffect() != null && I.getPassiveBonusEffect().getBonusTalent() != null) {
                passives.add(I.getPassiveBonusEffect().getBonusTalent());
            }
        }
        
        return passives;
    }
    
    public List<Skill> getPassiveSkills() {
        List<Skill> passives = new ArrayList<>();
        
        for (Item I : items) {
            if (I.getPassiveBonusEffect() != null && I.getPassiveBonusEffect().getBonusSkill() != null) {
                passives.add(I.getPassiveBonusEffect().getBonusSkill());
            }
        }
        
        return passives;
    }
    
    public List<Ability> getPassiveAbilities() {
        List<Ability> passives = new ArrayList<>();
        
        for (Item I : items) {
            if (I.getPassiveBonusEffect() != null && I.getPassiveBonusEffect().getBonusAbility() != null) {
                passives.add(I.getPassiveBonusEffect().getBonusAbility());
            }
        }
        
        return passives;
    }
    
    public List<Weapon> getUsableWeapons(MapCoords atPosition, UnitAllegiance allegiance) {
        List<Weapon> usableWeapons = new ArrayList<>();
        
        for (Item I : items) {
            if (I instanceof Weapon && ((Weapon)I).isAvailableAt(atPosition, allegiance)) {
                usableWeapons.add((Weapon)I);
            }
        }
        
        return usableWeapons;
    }
    
    public List<Integer> getFullWeaponRange() {
        List<Integer> fullRange = new ArrayList<>();
        
        items.stream().filter((item) -> (item instanceof Weapon)).forEachOrdered((item) -> {
            ((Weapon)item).getWeaponData().getRange().stream().filter((range) -> (!fullRange.contains(range))).forEachOrdered((range) -> {
                fullRange.add(range);
            });
        });
        
        return fullRange;
    }
}
