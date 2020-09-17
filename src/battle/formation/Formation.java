/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.formation;

import etherealtempest.MasterFsmState;
import etherealtempest.info.Conveyer;
import fundamental.Associated;
import fundamental.Tool;
import fundamental.Tool.ToolType;
import java.util.List;
import maps.layout.Coords;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.UnitStatus;
import maps.layout.Tile;
import maps.layout.VenturePeek;

/**
 *
 * @author night
 */
public class Formation extends Associated {
    private String imagePath = "Interface/GUI/formation_infographics/";
    private String formationType = "";
    
    public boolean isElite;
    
    private int stars;
    private List<Integer> ranges;
    private FormationTechnique[] techniques;
    
    private ToolType toolType;
    
    public Formation(String name, String desc, String type, boolean elite, int stars, String imageName, ToolType toolType, List<Integer> ranges, FormationTechnique[] techniques) {
        super(name, desc);
        this.stars = stars;
        this.techniques = techniques;
        this.toolType = toolType;
        this.ranges = ranges;
        imagePath += imageName;
        formationType = type;
        isElite = elite;
    }
    
    public Formation(boolean ex) {
        super(ex);
    }
    
    public String getFormationType() { return formationType; }
    public String getPath() { return imagePath; }
    
    public List<Integer> getRange() { return ranges; }
    
    public int getStars() { return stars; }
    public FormationTechnique[] getTechniques() { return techniques; }
    
    public FormationTechnique getMostDesiredTechnique(Conveyer data) {
        FormationTechnique highest = techniques[0];
        for (FormationTechnique tech : techniques) {
            if (tech.calculateDesirability(data) > highest.calculateDesirability(data)) {
                highest = tech;
            }
        }
        
        return highest;
    }
    
    public ToolType getToolType() { return toolType; }
    
    public double formationCoefficient() {
        if (isElite) {
            if (formationType.equals("diamond")) {
                return (5.0/30.0);
            } else { return 0.15; }
        }
        return 0.1;
    }
    
    public boolean isAvailableAt(Coords pos, int layer, UnitStatus allegiance) { 
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
    public String toString() { return name; }
    
}

    

