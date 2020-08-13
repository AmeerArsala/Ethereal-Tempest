/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

/**
 *
 * @author night
 */
public class Item {
    protected String name = "", desc = "";
    private boolean st = true;
    protected int Weight;
    
    public Item(String name, int Weight, String desc) {
        this.name = name;
        this.Weight = Weight;
        this.desc = desc;
    }
    
    public Item(boolean exists) {
        st = exists;
    }

    public String getName() { return name; }
    public String getDescription() { return desc; }
    public int getWeight() { return Weight; }
    
    public boolean getStatus() { return st; }
    
    @Override
    public String toString() { return name; }
    
}
