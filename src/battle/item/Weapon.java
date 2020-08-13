/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

import battle.skill.Skill;
import battle.talent.Talent;
import java.util.ArrayList;

/**
 *
 * @author night
 */
public class Weapon extends Item {
  protected int Pow, Acc, CRIT; //might, hit rate, weight, crit rate
  protected double durability, currentDurability;
  protected boolean[] Range; //if range is 1-2, RNG[0] and RNG[1] return true
  protected String[] effect; //things the weapon is effective against
  
  private String wpnType = "", wpnAttribute = "";
  private int[] bonus = new int[]{0, 0, 0, 0, 0, 0}; // { STR, ETHER, AGI, DEX, COMP, DEF, RSL }
  private int requiredLevel, worth;
  private String prf, info;

  public Talent wpnTalent;
  public Skill wpnSkill;
  
  public String poweredByElement = "";
  public int extraDamage = 0;
  
  private boolean exists = true;
  
  private enum declarationType {
      regular, withTalent, withSkill, doesntExist
  }
  
  private declarationType declaration;
  
  public Weapon(String name, String description, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, double durability, String[] eff, int[] bonus, int requiredLevel, String prf, int worth) {
    super(name, wt, description);
    wpnType = type;
    wpnAttribute = attr;
    Pow = mt;
    Acc = hit;
    CRIT = crt;
    Range = rng;
    effect = eff;
    this.bonus = bonus;
    this.durability = durability;
    this.requiredLevel = requiredLevel;
    this.prf = prf;
    this.worth = worth;
    currentDurability = durability;
    
    info = 
                "Pow: " + Pow + '\n'
              + "Acc: " + Acc +  '\n'
              + "Crit: " + CRIT + '\n'
              + "Weight: " + Weight + '\n'
              + "Eff. against: " + getEffString() + '\n';
      if (wpnAttribute.length() > 1 && !(wpnAttribute.equals("metal"))) {
          info += "Attribute: " + wpnAttribute + '\n';
      }
    
    declaration = declarationType.regular;
  }
  
  public Weapon(String name, String description, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, double durability, String[] eff, int[] bonus, int requiredLevel, String prf, int worth, Talent wpnTalent) {
    super(name, wt, description);
    wpnType = type;
    wpnAttribute = attr;
    Pow = mt;
    Acc = hit;
    CRIT = crt;
    Range = rng;
    effect = eff;
    this.bonus = bonus;
    this.durability = durability;
    this.requiredLevel = requiredLevel;
    this.prf = prf;
    this.worth = worth;
    this.wpnTalent = wpnTalent;
    currentDurability = durability;
    
    info = 
                "Pow: " + Pow + '\n'
              + "Acc: " + Acc +  '\n'
              + "Crit: " + CRIT + '\n'
              + "Weight: " + Weight + '\n'
              + "Eff. against: " + getEffString() + '\n';
      if (wpnAttribute.length() > 1 && !(wpnAttribute.equals("metal"))) {
          info += "Attribute: " + wpnAttribute + '\n';
      }
    
    declaration = declarationType.withTalent;
  }
  
  public Weapon(String name, String description, String type, String attr, int mt, int hit, int wt, int crt, boolean[] rng, double durability, String[] eff, int[] bonus, int requiredLevel, String prf, int worth, Skill wpnSkill) {
    super(name, wt, description);
    wpnType = type;
    wpnAttribute = attr;
    Pow = mt;
    Acc = hit;
    CRIT = crt;
    Range = rng;
    effect = eff;
    this.bonus = bonus;
    this.durability = durability;
    this.requiredLevel = requiredLevel;
    this.prf = prf;
    this.worth = worth;
    this.wpnSkill = wpnSkill;
    currentDurability = durability;
    
    info = 
                "Pow: " + Pow + '\n'
              + "Acc: " + Acc +  '\n'
              + "Crit: " + CRIT + '\n'
              + "Weight: " + Weight + '\n'
              + "Eff. against: " + getEffString() + '\n';
      if (wpnAttribute.length() > 1 && !(wpnAttribute.equals("metal"))) {
          info += "Attribute: " + wpnAttribute + '\n';
      }
    
    declaration = declarationType.withSkill;
  }

  //only used for empty slots in the inventory
  public Weapon(boolean ex) {
    super(ex);
    exists = false;
    declaration = declarationType.doesntExist;
  }
  
  public Weapon getNewWeaponInstance() {
      if (declaration == declarationType.regular) {
          return new Weapon(name, desc, wpnType, wpnAttribute, Pow, Acc, Weight, CRIT, Range, durability, effect, bonus, requiredLevel, prf, worth);
      }
      if (declaration == declarationType.withSkill) {
          return new Weapon(name, desc, wpnType, wpnAttribute, Pow, Acc, Weight, CRIT, Range, durability, effect, bonus, requiredLevel, prf, worth, wpnSkill);
      }
      if (declaration == declarationType.withTalent) {
          return new Weapon(name, desc, wpnType, wpnAttribute, Pow, Acc, Weight, CRIT, Range, durability, effect, bonus, requiredLevel, prf, worth, wpnTalent);
      }
      return new Weapon(false);
  }
  
  public boolean getExistence() { return exists; }
  
  public String[] effective() { return effect; }
  public int getPow() { return Pow; }
  public int getAcc() { return Acc; }
  public int getWeight() { return Weight; }
  public int getCRIT() { return CRIT; }
  public int getRequiredLevel() { return requiredLevel; }
  public int getWorth() { return worth; }
  public int[] getBonuses() { return bonus; }
  public double getCurrentDurability() { return currentDurability; }
  public double getMaxDurability() { return durability; }
  public String getWpnType() { return wpnType; } //sword, axe, lance, etc
  public String getWpnAttribute() { return wpnAttribute; } //element
  public String getPRF() { return prf; }

  public String getDmgType() {
      if (wpnType.equals("sword") || wpnType.equals("axe") || wpnType.equals("polearm") || wpnType.equals("knife") || wpnType.equals("bow") || wpnType.equals("whip") || wpnType.equals("monster")) {
        return "physical";
      }
      return "ether";
  }

  public boolean[] getRange() { return Range; }
  
  public void used(double amt) {
    if (durability < 99) { currentDurability -= amt; }
  }
  //public boolean equipped() { return isEquipped; }
  
  public static int getWeaponIndex(String wpntp) {
      String[] basic = {"sword", "axe", "polearm", "knife", "bow", "whip", "monster", "pi ether", "gamma ether", "delta ether", "omega ether"}; //PI Ether = holy ether, Delta Ether = Regular ether, encompasses most offensive Spells, Omega Ether = Special or Almighty Spells, Gamma Ether = Dark Spells
      for (int i = 0; i < basic.length; i++) {
          if (wpntp.equals(basic[i])) { return i; }
      }
      return -1;
  }
  
  public void elementalPowerup(String element, int xtradmg) {
      poweredByElement = element;
      extraDamage = xtradmg;
      //maybe do more here
  }
  
  public String getLoreDescription() { return desc; }
  public String getStatDescription() { return info; }
  
  public String getRangeString() {
      String wpnrange = "";
      boolean dashPlaced = false;
      int closest = -1;
      for (int i = 1; i < Range.length; i++) {
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
  
  public String getEffString() {
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
  public String getDescription() {
      return info + desc;
  }
  
  @Override
  public String toString() { return name; }
  
}
