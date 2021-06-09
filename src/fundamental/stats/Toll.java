/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.stats;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author night
 */
public class Toll {
    public enum Exchange {
        @SerializedName("HP") HP(BaseStat.CurrentHP),
        @SerializedName("TP") TP(BaseStat.CurrentTP),
        @SerializedName("Durability") Durability();
        
        private BaseStat correlatingStat = null;
        
        private Exchange(BaseStat correlatingStat) {
            this.correlatingStat = correlatingStat;
        }
        
        private Exchange() {}
        
        public BaseStat getCorrelatingStat() {
            return correlatingStat;
        }
    }
    
    private int val;
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
