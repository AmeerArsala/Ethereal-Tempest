/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

/**
 *
 * @author night
 */
public class Toll {
    public enum Exchange {
        HP,
        TP,
        Durability
    }
    
    private int val = 0;
    private Exchange type;
    
    public Toll(Exchange e, int value) {
        val = value;
        type = e;
    }
    
    public int getValue() {
        return val;
    }
    
    public Exchange getType() {
        return type;
    }
}
