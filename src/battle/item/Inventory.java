/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

import battle.item.Item;
import java.util.List;

/**
 *
 * @author night
 */
public class Inventory {
    private final List<Item> items;
    
    public Inventory(List<Item> items) {
        this.items = items;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public int getSpace(int physique) {
        int amt = items.size();
        for (Item item : items) {
            if (item.getStatus() && physique - item.getWeight() < 0) {
                amt--;
            }
        }
        
        return amt;
    }
    
    public int getAmountOfItems() {
        int num = 0;
        for (Item item : items) {
            if (item.getStatus()) {
                num++;
            }
        }
        return num;
    }
}
