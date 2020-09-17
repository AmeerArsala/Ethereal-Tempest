/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.skill;

import fundamental.Toll;
import etherealtempest.MasterFsmState;
import fundamental.Associated;
import fundamental.Tool;
import fundamental.Tool.ToolType;
import maps.layout.Coords;
import maps.layout.TangibleUnit;
import maps.layout.TangibleUnit.UnitStatus;
import maps.layout.Tile;
import maps.layout.VenturePeek;

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
        this.path = path;
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
    
    public Skill getNewSkillInstance() {
        return new Skill(name, desc, path, type, info, effect);
    }
    
    public Toll getToll() { return info; }
    
    public boolean isAvailableAt(Coords pos, int layer, UnitStatus allegiance, Tool tool) { 
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
