/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

/**
 *
 * @author night
 */
public class ConsumableItem extends Item {
    private int maxUses, currentUses;
    private String iconPath = "Interface/GUI/general_icons/";
    
    public ConsumableItem(String consumableName, String description, int weight, int worth, int maxUses, ItemEffect effect, String iconPath) {
        super(consumableName, description, weight, worth, effect);
        this.maxUses = maxUses;
        this.iconPath += iconPath;
        currentUses = maxUses;
    }
    
    public ConsumableItem(boolean exists) {
        super(exists);
    }
    
    public int getCurrentUses() { return currentUses; }
    public int getMaxUses() { return maxUses; }
    
    public String getPath() { return iconPath; }
    
    public ConsumableItem newItemInstance() {
        return new ConsumableItem(name, desc, Weight, worth, maxUses, useEffect, iconPath.substring(28));
    }
    
}
