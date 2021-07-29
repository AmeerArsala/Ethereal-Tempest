/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.item.weapon;

import fundamental.stats.BaseStat;
import com.atr.jme.font.util.StringContainer;
import com.google.gson.annotations.SerializedName;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import etherealtempest.fsm.MasterFsmState;
import fundamental.unit.aspect.UnitAllegiance;
import etherealtempest.info.Conveyor;
import fundamental.item.Item;
import fundamental.stats.alteration.Bonus;
import fundamental.stats.alteration.Bonus.BonusType;
import fundamental.RawBroadBonus;
import fundamental.talent.Talent;
import fundamental.talent.TalentConcept;
import fundamental.talent.TalentCondition;
import fundamental.talent.TalentCondition.Occasion;
import fundamental.talent.TalentEffect;
import fundamental.tool.DamageTool;
import fundamental.tool.Tool.ToolType;
import general.ui.menu.BasicMenuOption;
import general.ui.text.TextProperties;
import java.util.Arrays;
import java.util.List;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.occupant.VenturePeek;

/**
 *
 * @author night
 */
public class Weapon extends Item {
    @SerializedName("INFINITE_DURABILITY")
    public static final float INFINITE_DURABILITY = 500.0f;
    
    private DamageTool weaponData;
  
    private int requiredLevel;
    private float durability, currentDurability;
  
    private String prf = "None";
    public String poweredByElement = "";
  
    public Weapon(Item template, DamageTool data, float durability, int requiredLevel, String prf) {
        super(template.getName(), template.getDescription(), template.getWeight(), template.getWorth(), template.getPassiveBonusEffect());
        weaponData = data;
      
        this.durability = durability;
        this.requiredLevel = requiredLevel;
        this.prf = prf;
      
        currentDurability = durability;
    }
  
    public DamageTool getWeaponData() { return weaponData; }
    public int getRequiredLevel() { return requiredLevel; }
    public double getCurrentDurability() { return currentDurability; }
    public double getMaxDurability() { return durability; }
    public String getPRF() { return prf; }
    
    @Override
    protected List<BasicMenuOption<Conveyor>> onSelectOptions(Conveyor data) {
        List<BasicMenuOption<Conveyor>> options = super.onSelectOptions(data);
        
        float rectangleX = 0f;
        float rectangleY = 0f;
        float rectangleWidth = 50f;
        float rectangleHeight = 25f;
        
        if (data.getUnit().getEquippedWeapon() == null || data.getUnit().getEquippedWeapon() != this) {
            //add equip option
            Rectangle rect = new Rectangle(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
            TextProperties equipProperties =
            TextProperties.builder()
                .kerning(3)
                .horizontalAlignment(StringContainer.Align.Center)
                .verticalAlignment(StringContainer.VAlign.Center)
                .wrapMode(StringContainer.WrapMode.Clip)
                .textBox(rect)
                .build();
            
            BasicMenuOption<Conveyor> equipOption = new BasicMenuOption<Conveyor>("Equip", equipProperties, ColorRGBA.White) {
                @Override
                protected void initialize(AssetManager assetManager, Conveyor data) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
                
                @Override
                protected void onSelect() {
                    data.getUnit().equip(Weapon.this);
                    
                    //DO MORE HERE
                }
            };
            
            options.add(0, equipOption); //add it as the first option
        } else { 
            //TODO: add dequip option
        }
        
        return options;
    }
    
    @Override
    public String getIconPath() {
        return "Interface/GUI/icons/item_and_formula/" + weaponData.getType() + ".png";
    }
    
    public void addCurrentDurability(float amt) {
        if (durability != INFINITE_DURABILITY) {
            if (currentDurability + amt > durability) {
                currentDurability = durability;
            } else if (currentDurability + amt < 0.0f) {
                currentDurability = 0.0f;
            } else {
                currentDurability += amt;
            }
        }
    }
  
    public void elementalPowerup(String element, int xtradmg) {
        poweredByElement = element;
        //weaponData.extraDamage += xtradmg; 
        //TODO: do more here
    }
  
    public boolean isAvailableAt(MapCoords pos, UnitAllegiance allegiance) {
        MapLevel currentMap = MasterFsmState.getCurrentMap();
        for (Integer range : weaponData.getRange()) {
            for (MapCoords point : VenturePeek.coordsForTilesOfRange(range, pos)) {
                TangibleUnit occupier = currentMap.getTileAt(point).getOccupier();
                if (occupier != null && !allegiance.alliedWith(occupier.getAllegiance())) {
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
        String fullDescription = weaponData.toString() + statDesc();
      
        if (poweredByElement.length() > 1) { //if it is powered by an element
            fullDescription += "Powered by: " + poweredByElement + '\n';
        }
      
        fullDescription += "Required level: " + requiredLevel + '\n';
      
        if (prf != null && !prf.equalsIgnoreCase("None")) {
            fullDescription += "User: " + prf + '\n';
        }
      
        return fullDescription;
    }
    
    
    public static Weapon Firangi() {
        return WeaponInfo.deserialize("firangi.json", 
            new RawBroadBonus(
                Talent.Bonus(
                    Arrays.asList( //bonuses when equipped
                        new Bonus(1, BonusType.Raw, BaseStat.Ether),
                        new Bonus(5, BonusType.Raw, BaseStat.Comprehension),
                        new Bonus(1, BonusType.Raw, BaseStat.Dexterity),
                        new Bonus(1, BonusType.Raw, BaseStat.Defense),
                        new Bonus(1, BonusType.Raw, BaseStat.Resilience)
                    ), 
                    Occasion.Indifferent
                )
            )
        );
    }
    
    public static Weapon Cutlass() { return WeaponInfo.deserialize("cutlass.json", null); }
    
    public static Weapon Rebel_Pike() {
        return WeaponInfo.deserialize(
            "rebel_pike.json",
            null, //no passive bonus for just having it in your inventory
            new RawBroadBonus( //onEquip bonus
                Talent.createEffect(
                    ToolType.SupportSelf, 
                    Arrays.asList(
                        new TalentConcept(
                            new TalentCondition("If HP <= 50%, ", Occasion.Indifferent) {
                                @Override
                                protected boolean getCondition(Conveyor data) {
                                    return data.getUnit().getCurrentToMaxHPratio() <= 0.5f;
                                }
                            },
                            TalentEffect.Bonuses(
                                Arrays.asList(
                                    new Bonus(5, BonusType.Raw, BaseStat.Strength)
                                )
                            )
                        )
                    )
                )
            )
        );
    }
    
    public static Weapon Copper_Shortsword() { return WeaponInfo.deserialize("copper_shortsword.json", null); }
    
    public static Weapon Steel_Broadsword() { return WeaponInfo.deserialize("steel_broadsword.json", null); }
    
    public static Weapon Svardstav() { return WeaponInfo.deserialize("svardstav.json", null); }
    
    public static Weapon Francisca() { return WeaponInfo.deserialize("francisca.json", null); }
    
    public static Weapon Glaive() { return WeaponInfo.deserialize("glaive.json", null); }
  
}
