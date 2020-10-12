/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import battle.Combatant;
import etherealtempest.MasterFsmState;
import static etherealtempest.info.Catalog.effAgainstNothing;
import fundamental.stats.Bonus;
import fundamental.tool.DamageTool;
import java.util.Arrays;
import maps.layout.Coords;
import maps.layout.occupant.TangibleUnit;
import maps.layout.occupant.TangibleUnit.UnitStatus;
import maps.layout.tile.Tile;
import maps.layout.occupant.VenturePeek;

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
    
    
    
    public static Weapon Firangi() {
        return new Weapon(
              new Item("Firangi", //weapon name
                      "An extremely rare offensive sword of unknown origin. Many are found lying on the ground or at the bottom of a pond.",  //description
                      6, //weight 
                      5000 //worth
              ), 
              new DamageTool(
                      8,  //pow
                      95, //acc
                      15, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                              new Bonus(1, Combatant.BaseStat.ether),
                              new Bonus(5, Combatant.BaseStat.comprehension),
                              new Bonus(1, Combatant.BaseStat.dexterity),
                              new Bonus(1, Combatant.BaseStat.defense),
                              new Bonus(1, Combatant.BaseStat.resilience)
                      ),
                      "sword", //type
                      "metal", //attribute
                      new String[]{"cavalry", "mechanism"} //what it is effective against
              ),
              45.0, //durability
              0, //required level
              "Morva" //prf
        );
    }
    
    public static Weapon Cutlass() {
        return new Weapon(
              new Item("Cutlass", //weapon name
                      "An amateur sword designed for close-quarters fighting that is cheap to make. Favored in naval battles.",  //description
                      5, //weight 
                      500 //worth
              ), 
              new DamageTool(
                      7,  //pow
                      90, //acc
                      0, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "sword", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              40.0, //durability
              0, //required level
              "None" //prf
        );
    }
    
    public static Weapon Rebel_Pike() {
        return new Weapon(
              new Item("Rebel Pike", //weapon name
                      "A pike that becomes stronger when the user's HP is less than or equal to half.",  //description
                      9, //weight 
                      1000 //worth
              ), 
              new DamageTool(
                      11,  //pow
                      75, //acc
                      5, //crit 
                      Arrays.asList(1), //range
                      Arrays.asList( //bonuses when equipped
                      ),
                      "polearm", //type
                      "metal", //attribute
                      effAgainstNothing //what it is effective against
              ),
              35.0, //durability
              5, //required level
              "None" //prf
        );
    }
  
}
