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
public class BasicItem {
    private String name;
    private String desc;
    private int Weight;
    private int worth;
    
    public BasicItem(String name, String desc, int Weight, int worth) {
        this.name = name;
        this.desc = desc;
        this.Weight = Weight;
        this.worth = worth;
    }
    
    public Item construct() {
        return new Item(name, desc, Weight, worth);
    }
    
}
