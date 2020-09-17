/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.item;

import etherealtempest.MasterFsmState;
import fundamental.DamageTool;
import maps.layout.Coords;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.UnitStatus;
import maps.layout.Tile;
import maps.layout.VenturePeek;

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
  
  public void restoreUses(int val) {
      if (currentDurability + val > durability) {
          currentDurability = durability;
      } else { currentDurability += val; }
  }
  
  public void elementalPowerup(String element, int xtradmg) {
      poweredByElement = element;
      weaponData.extraDamage += xtradmg;
      //do more here
  }
  
  public boolean isAvailableAt(Coords pos, int layer, UnitStatus allegiance) {
      Tile[][] layerTiles = MasterFsmState.getCurrentMap().fullmap[layer];
      for (Integer range : weaponData.getRange()) {
          for (Coords point : VenturePeek.coordsForTilesOfRange(range, pos, layer)) {
              TangibleUnit occupier = layerTiles[point.getX()][point.getY()].getOccupier();
              if (occupier != null && !allegiance.alliedWith(occupier.unitStatus)) {
                  return true;
              }
          }
      }
      
      return false;
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
  
}
