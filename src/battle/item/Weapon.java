/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

import fundamental.DamageTool;

/**
 *
 * @author night
 */
public class Weapon extends Item {
  private DamageTool weaponData;
  
  private int requiredLevel;
  private double durability, currentDurability;
  
  private String prf = "None";
  public String poweredByElement = "";
  
  public Weapon(Item template, DamageTool data, double durability, int requiredLevel, String prf) {
      super(template.getName(), template.getDescription(), template.getWeight(), template.getWorth(), template.getExtraSkill(), template.getExtraTalent());
      weaponData = data;
      
      this.durability = durability;
      this.requiredLevel = requiredLevel;
      this.prf = prf;
      
      currentDurability = durability;
  }

  //only used for empty slots in the inventory
  public Weapon(boolean ex) {
    super(ex);
  }
  
  public Weapon getNewWeaponInstance() {
      if (exists) {
          return new Weapon((Item)this, weaponData.getNewInstance(), durability, requiredLevel, prf);
      }
      return new Weapon(false);
  }
  
  public DamageTool getWeaponData() { return weaponData; }
  public int getRequiredLevel() { return requiredLevel; }
  public double getCurrentDurability() { return currentDurability; }
  public double getMaxDurability() { return durability; }
  public String getPRF() { return prf; }
  
  public void used(double amt) {
    if (durability < 99) { currentDurability -= amt; }
  }
  
  public void elementalPowerup(String element, int xtradmg) {
      poweredByElement = element;
      weaponData.extraDamage += xtradmg;
      //maybe do more here
  }
  
  public String getLoreDescription() { return desc; }
  
  @Override
  public String getDescription() {
      return getStatDescription() + desc;
  }
  
  public String getStatDescription() {
      String fullDescription = weaponData.toString();
      
      if (poweredByElement.length() > 1) { //if it is powered by an element
          fullDescription += "Powered by: " + poweredByElement + '\n';
      }
      
      fullDescription += "Required level: " + requiredLevel + '\n';
      
      if (!prf.equalsIgnoreCase("None")) {
          fullDescription += "User: " + prf + '\n';
      }
      
      return fullDescription;
  }
  

  /*public String getStatDescription() { return info; }
  
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
  
  public List<Integer> getRangeInts() { return rangeInts; }
  public boolean getExistence() { return exists; }
  
  public String[] effective() { return effect; }
  public int getPow() { return Pow; }
  public int getAcc() { return Acc; }
  public int getCRIT() { return CRIT; }
  
  public int[] getBonuses() { return bonus; }
  
  public String getWpnType() { return wpnType; } //sword, axe, lance, etc
  public String getWpnAttribute() { return wpnAttribute; } //element
  
  
  public Talent getWeaponTalent() { return wpnTalent; }
  public Skill getWeaponSkill() { return wpnSkill; }

  public String getDmgType() {
      if (wpnType.equals("sword") || wpnType.equals("axe") || wpnType.equals("polearm") || wpnType.equals("knife") || wpnType.equals("bow") || wpnType.equals("whip") || wpnType.equals("monster")) {
        return "physical";
      }
      return "ether";
  }

  public boolean[] getRange() { return Range; }*/
  
}
