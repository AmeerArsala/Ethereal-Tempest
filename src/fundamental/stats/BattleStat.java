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
public enum BattleStat {
    @SerializedName("AttackPower") AttackPower(0, "ATK PWR"),
    @SerializedName("Accuracy") Accuracy(1, "ACC"),
    @SerializedName("Evasion") Evasion(2, "EVA"),
    @SerializedName("Crit") Crit(3, "CRIT"),
    @SerializedName("CritEvasion") CritEvasion(4, "CRIT EVA"),
    @SerializedName("AttackSpeed") AttackSpeed(5, "SPD");
        
    private final int value;
    private final String name;
    private BattleStat(int val, String sname) {
        value = val;
        name = sname;
    }
        
    public String getName() {
        return name;
    }
        
    public int getValue() {
        return value;
    }
}
