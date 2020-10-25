/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.skill;

import fundamental.stats.Toll;
import etherealtempest.MasterFsmState;
import etherealtempest.characters.Unit.UnitAllegiance;
import fundamental.Associated;
import fundamental.tool.Tool;
import fundamental.tool.Tool.ToolType;
import maps.layout.Coords;
import maps.layout.occupant.TangibleUnit;
import maps.layout.tile.Tile;
import maps.layout.occupant.VenturePeek;

/**
 *
 * @author night
 */
public class Skill extends Associated {
    private String path = "Interface/GUI/skill_icons/empty.png";
    private ToolType type;
    private Toll info;
    private SkillEffect effect;
    
    public Skill(String name, String desc, String path, ToolType type, Toll info, SkillEffect effect) {
        super(name, desc);
        this.path = "Interface/GUI/skill_icons/" + path;
        this.type = type;
        this.info = info; 
        this.effect = effect;
    }
    
    public Skill(boolean exists) {
        super(exists);
    }

    public String getPath() { return path; }
    
    public SkillEffect getEffect() { return effect; }
    public ToolType getType() { return type; }
    
    public Toll getToll() { return info; }
    
    public boolean isAvailableAt(Coords pos, int layer, UnitAllegiance allegiance, Tool tool) { 
        Tile[][] layerTiles = MasterFsmState.getCurrentMap().fullmap[layer];
        for (Integer range : tool.getRange()) {
            for (Coords point : VenturePeek.coordsForTilesOfRange(range, pos, layer)) {
                TangibleUnit occupier = layerTiles[point.getX()][point.getY()].getOccupier();
                if (occupier != null && ((type.isSupportive() && allegiance.alliedWith(occupier.unitStatus)) || (!type.isSupportive() && !allegiance.alliedWith(occupier.unitStatus)))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() { return name; }
}
