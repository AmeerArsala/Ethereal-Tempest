/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.tool;

import fundamental.jobclass.JobClass.MobilityType;
import fundamental.item.weapon.WeaponAttribute;
import fundamental.item.weapon.WeaponType;
import fundamental.stats.BaseStat;
import fundamental.RawBroadBonus;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class DamageTool extends Tool {
    private int Pow, Acc; //might, hit rate, weight, crit rate. This is not final because of forging
    private BaseStat damageMeasuredAgainstStat;
    
    private final ArrayList<MobilityType> effect; //things it is effective against
    
    public DamageTool(int pwr, int accuracy, int crt, List<Integer> range, WeaponType toolType, WeaponAttribute attr, BaseStat againstStat, ArrayList<MobilityType> eff, RawBroadBonus onEquip) {
        super(crt, range, onEquip, attr, toolType);
        Pow = pwr;
        Acc = accuracy;
        damageMeasuredAgainstStat = againstStat;
        effect = eff;
    }
    
    public DamageTool getNewInstance() {
        return new DamageTool(Pow, Acc, CRIT, ranges, type, attribute, damageMeasuredAgainstStat, effect, onEquipEffect);
    }
    
    public ArrayList<MobilityType> effective() { return effect; }
    
    public int getPow() { return Pow; }
    public int getAcc() { return Acc; }
    
    public BaseStat getDamageMeasuredAgainstStat() { return damageMeasuredAgainstStat; }
    
    public String getEffString() {
        String full = "";
      
        for (int i = 0; i < effect.size(); i++) {
            full += effect.get(i);
            if (i < effect.size() - 1) {
                full += ", ";
            }
        }
      
        return full;
    }
    
    @Override
    public String toString() {
        return 
                  "Pow: " + Pow + '\n'
                + "Acc: " + Acc +  '\n'
                + "Crit: " + CRIT + '\n'
                + "Range: " + getRangeString() + '\n'
                + "Eff. against: " + getEffString() + '\n'
                + "Attribute: " + attribute + '\n'
                + effects;
    }
}
