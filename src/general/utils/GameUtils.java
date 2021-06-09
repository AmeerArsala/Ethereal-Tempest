/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

import com.jme3.math.ColorRGBA;
import etherealtempest.FSM.UnitState;
import etherealtempest.MasterFsmState;
import fundamental.unit.UnitAllegiance;
import etherealtempest.info.Conveyor;
import java.util.ArrayList;
import java.util.List;
import maps.layout.MapLevel;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.Tile;
import maps.layout.tile.TileOptionData.TileType;

/**
 *
 * @author night
 */
public class GameUtils {
    public static final ColorRGBA HP_COLOR_GREEN = new ColorRGBA(0, 0.76f, 0, 1);
    public static final ColorRGBA TP_COLOR_PINK = new ColorRGBA(0.85f, 0.36f, 0.83f, 1f);
    
    public static List<Tile> getSpecialTiles(TileType type) {
        List<Tile> special = new ArrayList<>();
        MapLevel current = MasterFsmState.getCurrentMap();
        
        for (int l = 0; l < current.getLayerCount(); l++) {
            for (int x = current.getMinimumX(l); x < current.getXLength(l); x++) {
                for (int y = current.getMinimumY(l); y < current.getYLength(l); y++) {
                    if (current.getTileAt(x, y, l).getTileData().getType() == type) {
                        special.add(current.getTileAt(x, y, l));
                    }
                }
            }
        }
        
        return special;
    }
    
    public static List<Tile> getSpecialTiles(TileType type, UnitAllegiance allegiance) {
        List<Tile> special = new ArrayList<>();
        MapLevel current = MasterFsmState.getCurrentMap();
        
        for (int l = 0; l < current.getLayerCount(); l++) {
            for (int x = current.getMinimumX(l); x < current.getXLength(l); x++) {
                for (int y = current.getMinimumY(l); y < current.getYLength(l); y++) {
                    if (current.getTileAt(x, y, l).getTileData().getType() == type && current.getTileAt(x, y, l).getTileData().allegianceIsEligible(allegiance)) {
                        special.add(current.getTileAt(x, y, l));
                    }
                }
            }
        }
        
        return special;
    }
    
    public static List<Tile> getSpecialTiles(TileType type, UnitAllegiance allegiance, boolean alliedWith) {
        List<Tile> special = new ArrayList<>();
        MapLevel current = MasterFsmState.getCurrentMap();
        
        for (int l = 0; l < current.getLayerCount(); l++) {
            for (int x = current.getMinimumX(l); x < current.getXLength(l); x++) {
                for (int y = current.getMinimumY(l); y < current.getYLength(l); y++) {
                    if (current.getTileAt(x, y, l).getTileData().getType() == type && current.getTileAt(x, y, l).getTileData().alliedAllegianceIsEligible(allegiance) == alliedWith) {
                        special.add(current.getTileAt(x, y, l));
                    }
                }
            }
        }
        
        return special;
    }
    
    public static List<TangibleUnit> calculateEnemyUnits(TangibleUnit user, Conveyor info) {
            if (user == null) { return null; }
            
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (!user.isAlliedWith(tu) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> calculateAlliedUnits(TangibleUnit user, Conveyor info) {
            if (user == null) { return null; }
            
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (!user.equals(tu) && user.isAlliedWith(tu) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> calculateAlliedUnits(UnitAllegiance allegiance, Conveyor info) {
            if (allegiance == null) { return null; }
            
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (allegiance.alliedWith(tu.getAllegiance()) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> calculateUnitsOfAllegiance(UnitAllegiance allegiance, Conveyor info) {
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (tu.getAllegiance() == allegiance && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> retrieveCharacters(String[] names, Conveyor data) {
            List<TangibleUnit> characters = new ArrayList<>();
            
            for (String name : names) {
                data.getAllUnits().stream().filter((tu) -> (name.equals(tu.getName()))).forEachOrdered((tu) -> {
                    characters.add(tu);
                });
            }
        
            return characters;
        }
        
        public static List<MapEntity> retrieveEntities(String[] names, Conveyor data) {
            List<MapEntity> entities = new ArrayList<>();
            
            for (String name : names) {
                data.getMapEntities().stream().filter((entity) -> (name.equals(entity.getName()))).forEachOrdered((entity) -> {
                    entities.add(entity);
                });
            }
        
            return entities;
        }
}
