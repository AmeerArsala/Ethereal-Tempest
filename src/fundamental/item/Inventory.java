/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import java.util.List;

/**
 *
 * @author night
 */
public class Inventory {
    static final int DEFAULT_MAX_SPACE = 10;
    
    private final List<Item> items;
    private int maxSpace = 10;
    
    public Inventory(List<Item> items) {
        this.items = items;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public int getSpace(int physique) {
        int difference = physique - combinedInventoryWeight();
        return maxSpace - (difference < 0 ? difference : 0);
    }
    
    public int combinedInventoryWeight() {
        int wt = 0;
        return items.stream().filter((item) -> (item.doesExist())).map((item) -> item.getWeight()).reduce(wt, Integer::sum);
    }
    
    public int getMaxSpace() { return maxSpace; }
    
    public void setMaxSpace(int max) {
        maxSpace = max;
    }
    
    public int getAmountOfItems() {
        int num = 0;
        for (Item item : items) {
            if (item.doesExist()) {
                num++;
            }
        }
        return num;
    }
}
