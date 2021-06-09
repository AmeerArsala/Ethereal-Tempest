/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import fundamental.stats.RawBroadBonus;

/**
 *
 * @author night
 * 
 * this is for deserialization
 */
public class BasicItem {
    private String name;
    private String desc;
    private int weight;
    private int worth;
    
    public BasicItem(String name, String desc, int weight, int worth) {
        this.name = name;
        this.desc = desc;
        this.weight = weight;
        this.worth = worth;
    }
    
    public Item construct(RawBroadBonus passive) {
        return new Item(name, desc, weight, worth, passive) {
            @Override
            public String getIconPath() {
                return null;
            }
        };
    }
    
}
