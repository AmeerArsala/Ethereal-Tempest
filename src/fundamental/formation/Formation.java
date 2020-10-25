/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.formation;

import battle.Combatant.BaseStat;
import etherealtempest.MasterFsmState;
import etherealtempest.characters.Unit.UnitAllegiance;
import etherealtempest.info.Conveyer;
import fundamental.Associated;
import fundamental.stats.Bonus.StatType;
import fundamental.stats.RawBroadBonus;
import fundamental.stats.StatBundle;
import fundamental.tool.Tool.ToolType;
import java.util.HashMap;
import java.util.List;
import maps.layout.Coords;
import maps.layout.occupant.TangibleUnit;
import maps.layout.tile.Tile;
import maps.layout.occupant.VenturePeek;

/**
 *
 * @author night
 */
public class Formation extends Associated {
    private static final HashMap<BaseStat, String> typeMap = new HashMap<>();
    
    static {
        typeMap.put(BaseStat.maxHP, "Aquarius");
        typeMap.put(BaseStat.maxTP, "Pisces");
        typeMap.put(BaseStat.strength, "Leo");
        typeMap.put(BaseStat.ether, "Scorpio");
        typeMap.put(BaseStat.agility, "Aries");
        typeMap.put(BaseStat.comprehension, "Libra");
        typeMap.put(BaseStat.dexterity, "Gemini");
        typeMap.put(BaseStat.defense, "Virgo");
        typeMap.put(BaseStat.resilience, "Capricorn");
        typeMap.put(BaseStat.mobility, "Sagittarius");
        typeMap.put(BaseStat.physique, "Taurus");
        typeMap.put(BaseStat.adrenaline, "Cancer");
    }
    
    private String formationType;
    private int tier;
    private StatBundle statBonus;
    
    private List<FormationTechnique> techniques;
    private List<Integer> ranges;
    
    private ToolType toolType;
    
    public Formation(String name, String desc, int tier, StatBundle statBonus, ToolType toolType, List<Integer> ranges, List<FormationTechnique> techniques) {
        super(name, desc);
        this.tier = tier;
        this.techniques = techniques;
        this.toolType = toolType;
        this.ranges = ranges;
        this.statBonus = statBonus;
        if (statBonus != null && statBonus.getStatType() == StatType.Base) {
            formationType = typeMap.get(statBonus.getWhichBaseStat());
        } else {
            formationType = "Wildcard";
        }
    }
    
    public Formation(boolean ex) {
        super(ex);
    }
    
    public String getFormationType() { return formationType; }
    
    public int getTier() { return tier; }
    
    public StatBundle getPassiveStatBonus() { return statBonus; }
    
    public ToolType getToolType() { return toolType; }
    
    public List<Integer> getRange() { return ranges; }
    
    public List<FormationTechnique> getTechniques() { return techniques; }
    
    public FormationTechnique getMostDesiredTechnique() {
        FormationTechnique highest = techniques.get(0);
        for (FormationTechnique tech : techniques) {
            if (tech.getDesirability() > highest.getDesirability()) {
                highest = tech;
            }
        }
        
        return highest;
    }
    
    public boolean isAvailableAt(Coords pos, int layer, UnitAllegiance allegiance) { 
        Tile[][] layerTiles = MasterFsmState.getCurrentMap().fullmap[layer];
        for (Integer range : ranges) {
            for (Coords point : VenturePeek.coordsForTilesOfRange(range, pos, layer)) {
                TangibleUnit occupier = layerTiles[point.getX()][point.getY()].getOccupier();
                if (occupier != null && ((toolType.isSupportive() && allegiance.alliedWith(occupier.unitStatus)) || (!toolType.isSupportive() && !allegiance.alliedWith(occupier.unitStatus)))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return name + "\n \n" + desc + "\n \n";
    }
    
}

    

