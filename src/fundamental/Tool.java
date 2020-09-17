/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import battle.parse.Catalog;
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
public class Tool {
    protected int CRIT;
    protected List<Integer> ranges = new ArrayList<>();
    protected List<Bonus> passiveBonusesOnEquip = new ArrayList<>(); //applies to both base and battle stats
    protected String attribute = "None";
    protected String type;
    
    protected Talent onEquipTalent = null;
    protected Skill onEquipSkill = null;
    
    public enum ToolType {
        Attack(0),
        SupportSelf(1),
        SupportAlly(2);
        
        private final int value;
        private ToolType(int val) {
            value = val;
        }
        
        public boolean isSupportive() {
            return value != 0;
        }
    }
    
    public Tool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType) { //with neither skill nor talent
        CRIT = crt;
        ranges = toolRanges;
        passiveBonusesOnEquip = bonuses;
        attribute = attr;
        type = toolType;
    }
    
    public Tool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, Talent tOnEquip, Skill sOnEquip) { //with both skill and talent
        CRIT = crt;
        ranges = toolRanges;
        passiveBonusesOnEquip = bonuses;
        attribute = attr;
        type = toolType;
        onEquipTalent = tOnEquip;
        onEquipSkill = sOnEquip;
    }
    
    public Tool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, Talent tOnEquip) { //with just a talent
        CRIT = crt;
        ranges = toolRanges;
        passiveBonusesOnEquip = bonuses;
        attribute = attr;
        type = toolType;
        onEquipTalent = tOnEquip;
    }
    
    public Tool(int crt, List<Integer> toolRanges, List<Bonus> bonuses, String attr, String toolType, Skill sOnEquip) { //with just a skill
        CRIT = crt;
        ranges = toolRanges;
        passiveBonusesOnEquip = bonuses;
        attribute = attr;
        type = toolType;
        onEquipSkill = sOnEquip;
    }
    
    public int getCRIT() { return CRIT; }
    
    public List<Integer> getRange() { return ranges; }
    public List<Bonus> getBonuses() { return passiveBonusesOnEquip; }
    
    public String getAttribute() { return attribute; } //element
    
    public Talent getOnEquipTalent() { return onEquipTalent; }
    public Skill getOnEquipSkill() { return onEquipSkill; }
    
    public String getType() { return type; } //sword, axe, lance, etc
    
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
}
