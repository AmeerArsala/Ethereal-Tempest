/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import fundamental.stats.RawBroadBonus;

/**
 *
 * @author night
 */
public class Gear extends Attribute {
    //this bonus is on having said item in your inventory or having learned said formula
    protected RawBroadBonus passive;
    
    public Gear(String name, String desc) {
        super(name, desc);
    }
    
    public Gear(String name, String desc, RawBroadBonus passive) {
        super(name, desc);
        this.passive = passive;
    }
    
    public RawBroadBonus getPassiveBonusEffect() {
        return passive;
    }
    
    public void setPassiveBonus(RawBroadBonus bonusEffect) {
        passive = bonusEffect;
    }
}
