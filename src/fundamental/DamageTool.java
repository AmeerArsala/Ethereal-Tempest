/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import battle.Catalog;
import battle.Combatant.BaseStat;
import battle.Combatant.BattleStat;
import battle.skill.Skill;
import battle.talent.Talent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class DamageTool {
    private int Pow, Acc, CRIT; //might, hit rate, weight, crit rate
    private String[] effect; //things it is effective against
    private String attribute = "None", type;
    private List<Integer> ranges = new ArrayList<>();
    private List<Bonus> passiveBonusesOnEquip = new ArrayList<>(); //applies to both base and battle stats
    private Talent onEquipTalent = null;
    private Skill onEquipSkill = null;
    
    public int extraDamage = 0, reducedDamage = 0;
    
    public DamageTool(int pwr, int accuracy, int crt, List<Integer> range, List<Bonus> bonuses, String toolType, String attr, String[] eff) { //with neither skill nor talent
        Pow = pwr;
        Acc = accuracy;
        CRIT = crt;
        ranges = range;
        passiveBonusesOnEquip = bonuses;
        type = toolType;
        attribute = attr;
        effect = eff;
    }
    
    public DamageTool(int pwr, int accuracy, int crt, List<Integer> range, List<Bonus> bonuses, String toolType, String attr, String[] eff, Talent toolTalent, Skill toolSkill) { //with both skill and talent
        Pow = pwr;
        Acc = accuracy;
        CRIT = crt;
        ranges = range;
        passiveBonusesOnEquip = bonuses;
        type = toolType;
        attribute = attr;
        effect = eff;
        onEquipTalent = toolTalent;
        onEquipSkill = toolSkill;
    }
    
    public DamageTool(int pwr, int accuracy, int crt, List<Integer> range, List<Bonus> bonuses, String toolType, String attr, String[] eff, Talent toolTalent) { //with only talent
        Pow = pwr;
        Acc = accuracy;
        CRIT = crt;
        ranges = range;
        passiveBonusesOnEquip = bonuses;
        type = toolType;
        attribute = attr;
        effect = eff;
        onEquipTalent = toolTalent;
    }
    
    public DamageTool(int pwr, int accuracy, int crt, List<Integer> range, List<Bonus> bonuses, String toolType, String attr, String[] eff, Skill toolSkill) { //with only skill
        Pow = pwr;
        Acc = accuracy;
        CRIT = crt;
        ranges = range;
        passiveBonusesOnEquip = bonuses;
        type = toolType;
        attribute = attr;
        effect = eff;
        onEquipSkill = toolSkill;
    }
    
    public DamageTool getNewInstance() {
        return new DamageTool(Pow, Acc, CRIT, ranges, passiveBonusesOnEquip, type, attribute, effect, onEquipTalent, onEquipSkill);
    }
    
    public String[] effective() { return effect; }
    public int getPow() { return Pow; }
    public int getAcc() { return Acc; }
    public int getCRIT() { return CRIT; }
  
    public List<Integer> getRange() { return ranges; }
    public List<Bonus> getBonuses() { return passiveBonusesOnEquip; }
    
    public String getType() { return type; } //sword, axe, lance, etc
    public String getAttribute() { return attribute; } //element
    
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
    
    public String getRangeString() {
      boolean[] Range = Catalog.rangeCreator(ranges);
      String wpnrange = "";
      boolean dashPlaced = false;
      int closest = -1;
      for (int i = 0; i < Range.length; i++) {
          if (Range[i]) {
              if (closest == -1) {
                  closest = i;
              }
              if (i > closest) {
                  if (!dashPlaced && Range[i - 1]) {
                      wpnrange += "-";
                      dashPlaced = true;
                  }
              } else {
                  wpnrange += i;
              }
          } else if (dashPlaced && Range[i - 1]) {
              wpnrange += (i - 1);
          }
      }
      
      return wpnrange;
  }
    
    public int getTotalBonus(BaseStat stat) {
        int statBonus = 0;
        for (Bonus bonus : passiveBonusesOnEquip) {
            if (bonus.getBaseStat() == stat) {
                statBonus += bonus.getValue();
            }
        }
        
        return statBonus;
    }
    
    public int getTotalBonus(BattleStat stat) {
        int statBonus = 0;
        for (Bonus bonus : passiveBonusesOnEquip) {
            if (bonus.getBattleStat() == stat) {
                statBonus += bonus.getValue();
            }
        }
        
        return statBonus;
    }
    
    @Override
    public String toString() {
        return 
                  "Pow: " + Pow + '\n'
                + "Acc: " + Acc +  '\n'
                + "Crit: " + CRIT + '\n'
                + "Range: " + getRangeString() + '\n'
                + "Eff. against: " + getEffString() + '\n'
                + "Attribute: " + attribute + '\n';
    }
}
