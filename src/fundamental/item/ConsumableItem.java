/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import fundamental.stats.RawBroadBonus;
import fundamental.stats.Toll.Exchange;

/**
 *
 * @author night
 */
public class ConsumableItem extends Item {
    private int maxUses, currentUses;
    private String iconPath;

    public ConsumableItem(String consumableName, String description, String iconPath, int weight, int worth, int maxUses, RawBroadBonus passive, ItemEffect effect) {
        super(consumableName, description, weight, worth, passive, effect);
        this.maxUses = maxUses;
        this.iconPath = iconPath;
        
        currentUses = maxUses;
    }
    
    public int getCurrentUses() { return currentUses; }
    public int getMaxUses() { return maxUses; }
    
    @Override
    public String getIconPath() { return iconPath; }
    
    @Override
    public String getDescription() {
        return super.getDescription() + "\n" + statDesc();
    }
    
    public static ConsumableItem Apple() {
        return new ConsumableItem("Apple", "Restores 10 health", "Interface/GUI/icons/item_and_formula/apple.png", 1, 15, 3, null, ItemEffect.Restore(Exchange.HP, 10));
    }
    
}
