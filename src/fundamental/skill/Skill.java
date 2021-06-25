/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.skill;

import com.google.gson.Gson;
import fundamental.stats.Toll;
import etherealtempest.fsm.MasterFsmState;
import fundamental.unit.UnitAllegiance;
import fundamental.Attribute;
import fundamental.tool.Tool;
import fundamental.tool.Tool.ToolType;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.occupant.VenturePeek;

/**
 *
 * @author night
 */
public class Skill extends Attribute {
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

    public String getPath() { return path; }
    
    public SkillEffect getEffect() { return effect; }
    public ToolType getType() { return type; }
    
    public Toll getToll() { return info; }
    
    public boolean isAvailableAt(MapCoords pos, UnitAllegiance allegiance, Tool tool) {
        MapLevel currentMap = MasterFsmState.getCurrentMap();
        for (Integer range : tool.getRange()) {
            for (MapCoords point : VenturePeek.coordsForTilesOfRange(range, pos)) {
                TangibleUnit occupier = currentMap.getTileAt(point).getOccupier();
                if (occupier != null && type.isSupportive() == allegiance.alliedWith(occupier.getAllegiance())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() { return name; }
    
    
    public static final Skill Heavy_Swing = deserialization("Heavy Swing.json").constructSkill(null);
    
    private static SkillDeserialization deserialization(String jsonName) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\GameInfo\\EntityPresets\\skills\\" + jsonName));
            
            return gson.fromJson(reader, SkillDeserialization.class); 
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
