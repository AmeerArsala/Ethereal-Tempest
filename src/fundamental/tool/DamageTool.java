/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.tool;

import fundamental.stats.Bonus;
import fundamental.stats.RawBroadBonus;
import java.util.List;

/**
 *
 * @author night
 */
public class DamageTool extends Tool {
    private int Pow, Acc; //might, hit rate, weight, crit rate
    private String[] effect; //things it is effective against
    
    public int extraDamage = 0;
    
    public DamageTool(int pwr, int accuracy, int crt, List<Integer> range, List<Bonus> bonuses, String toolType, String attr, String[] eff) { //with neither skill nor talent
        super(crt, range, bonuses, attr, toolType);
        Pow = pwr;
        Acc = accuracy;
        effect = eff;
    }
    
    public DamageTool(int pwr, int accuracy, int crt, List<Integer> range, List<Bonus> bonuses, String toolType, String attr, String[] eff, RawBroadBonus adv) { //with both skill and talent
        super(crt, range, bonuses, attr, toolType, adv);
        Pow = pwr;
        Acc = accuracy;
        effect = eff;
    }
    
    public DamageTool setExtraDamage(int extra) {
        extraDamage = extra;
        return this;
    }
    
    public DamageTool getNewInstance() {
        return new DamageTool(Pow, Acc, CRIT, ranges, passiveBonusesOnEquip, type, attribute, effect, new RawBroadBonus(onEquipTalent, onEquipSkill, onEquipAbility)).setExtraDamage(extraDamage);
    }
    
    public String[] effective() { return effect; }
    public int getPow() { return Pow; }
    public int getAcc() { return Acc; }
    
    public String getDmgType() {
        if (type.equals("sword") || type.equals("axe") || type.equals("polearm") || type.equals("knife") || type.equals("bow") || type.equals("whip") || type.equals("monster")) {
          return "physical";
        }
        return "ether";
    }
    
    private String getEffString() {
        String full = "";
      
        for (int i = 0; i < effect.length; i++) {
            full += effect[i];
            if (i < effect.length - 1) {
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
