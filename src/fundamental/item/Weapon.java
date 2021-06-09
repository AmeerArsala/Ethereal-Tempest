/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item;

import battle.Combatant.BaseStat;
import etherealtempest.MasterFsmState;
import etherealtempest.characters.Unit.UnitAllegiance;
import static etherealtempest.info.Catalog.effAgainstNothing;
import fundamental.stats.Bonus;
import fundamental.stats.RawBroadBonus;
import fundamental.talent.Talent;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.tool.DamageTool;
import java.util.Arrays;
import maps.layout.Coords;
import maps.layout.occupant.TangibleUnit;
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
    
    public Weapon(WeaponInfo info, DamageTool data) {
        this(info.getTemplate(), data, info.getDurability(), info.getRequiredLevel(), info.getPRF());
    }

    //only used for empty slots in the inventory
    public Weapon(boolean ex) {
      super(ex);
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
  
    public boolean isAvailableAt(Coords pos, int layer, UnitAllegiance allegiance) {
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
            WeaponInfo.deserialize("firangi.json"), 
            new DamageTool(
                8,  //pow
                95, //acc
                15, //crit 
                Arrays.asList(1), //range
                "sword", //type
                "metal", //attribute
                new String[]{"cavalry", "mechanism"}, //what it is effective against
                new RawBroadBonus(
                    Talent.Bonus(
                        Arrays.asList( //bonuses when equipped
                            new Bonus(1, BaseStat.ether),
                            new Bonus(5, BaseStat.comprehension),
                            new Bonus(1, BaseStat.dexterity),
                            new Bonus(1, BaseStat.defense),
                            new Bonus(1, BaseStat.resilience)
                        ), 
                        Occasion.Indifferent
                    )
                )
            )
        );
    }
    
    public static Weapon Cutlass() {
        return new Weapon(
            WeaponInfo.deserialize("cutlass.json"), 
            new DamageTool(
                7,  //pow
                90, //acc
                0, //crit 
                Arrays.asList(1), //range
                "sword", //type
                "metal", //attribute
                effAgainstNothing, //what it is effective against
                null
            )
        );
    }
    
    public static Weapon Rebel_Pike() {
        return new Weapon(
            WeaponInfo.deserialize("rebel_pike.json"), 
            new DamageTool(
                11,  //pow
                75, //acc
                5, //crit 
                Arrays.asList(1), //range
                "polearm", //type
                "metal", //attribute
                effAgainstNothing,
                null
            )
        );
    }
    
    public static Weapon Copper_Shortsword() {
        return new Weapon(
            WeaponInfo.deserialize("copper_shortsword.json"), 
            new DamageTool(
                5,  //pow
                100, //acc
                0, //crit 
                Arrays.asList(1), //range
                "sword", //type
                "metal", //attribute
                effAgainstNothing,
                null
            )
        );
    }
    
    public static Weapon Steel_Broadsword() {
        return new Weapon(
            WeaponInfo.deserialize("steel_broadsword.json"), 
            new DamageTool(
                9,  //pow
                85, //acc
                3, //crit 
                Arrays.asList(1), //range
                "sword", //type
                "metal", //attribute
                effAgainstNothing,
                null
            )
        );
    }
    
    public static Weapon Svardstav() {
        return new Weapon(
            WeaponInfo.deserialize("svardstav.json"), 
            new DamageTool(
                8,  //pow
                75, //acc
                0, //crit 
                Arrays.asList(1), //range
                "sword", //type
                "metal", //attribute
                new String[]{"cavalry"},
                null
            )
        );
    }
    
    public static Weapon Francisca() {
        return new Weapon(
            WeaponInfo.deserialize("francisca.json"), 
            new DamageTool(
                9,  //pow
                70, //acc
                0, //crit 
                Arrays.asList(1), //range
                "axe", //type
                "metal", //attribute
                effAgainstNothing,
                null
            )
        );
    }
    
    public static Weapon Glaive() {
        return new Weapon(
            WeaponInfo.deserialize("glaive.json"), 
            new DamageTool(
                12,  //pow
                80, //acc
                5, //crit 
                Arrays.asList(1), //range
                "polearm", //type
                "metal", //attribute
                effAgainstNothing,
                null
            )
        );
    }
  
}
