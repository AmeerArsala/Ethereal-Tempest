/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest;

import etherealtempest.FSM.UnitState;
import etherealtempest.characters.Unit.UnitAllegiance;
import etherealtempest.info.Conveyer;
import java.util.ArrayList;
import java.util.List;
import maps.layout.Map;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.TangibleUnit;
import maps.layout.tile.Tile;
import maps.layout.tile.TileOptionData.TileType;

/**
 *
 * @author night
 */
public class GameUtils {
    
    public static List<Tile> getSpecialTiles(TileType type) {
        List<Tile> special = new ArrayList<>();
        Map current = MasterFsmState.getCurrentMap();
        
        for (int l = 0; l < current.getLayerCount(); l++) {
            for (int x = current.getMinimumX(l); x < current.getXLength(l); x++) {
                for (int y = current.getMinimumY(l); y < current.getYLength(l); y++) {
                    if (current.fullmap[l][x][y].getTileData().getType() == type) {
                        special.add(current.fullmap[l][x][y]);
                    }
                }
            }
        }
        
        return special;
    }
    
    public static List<Tile> getSpecialTiles(TileType type, UnitAllegiance allegiance) {
        List<Tile> special = new ArrayList<>();
        Map current = MasterFsmState.getCurrentMap();
        
        for (int l = 0; l < current.getLayerCount(); l++) {
            for (int x = current.getMinimumX(l); x < current.getXLength(l); x++) {
                for (int y = current.getMinimumY(l); y < current.getYLength(l); y++) {
                    if (current.fullmap[l][x][y].getTileData().getType() == type && current.fullmap[l][x][y].getTileData().allegianceIsEligible(allegiance)) {
                        special.add(current.fullmap[l][x][y]);
                    }
                }
            }
        }
        
        return special;
    }
    
    public static List<Tile> getSpecialTiles(TileType type, UnitAllegiance allegiance, boolean alliedWith) {
        List<Tile> special = new ArrayList<>();
        Map current = MasterFsmState.getCurrentMap();
        
        for (int l = 0; l < current.getLayerCount(); l++) {
            for (int x = current.getMinimumX(l); x < current.getXLength(l); x++) {
                for (int y = current.getMinimumY(l); y < current.getYLength(l); y++) {
                    if (current.fullmap[l][x][y].getTileData().getType() == type && current.fullmap[l][x][y].getTileData().alliedAllegianceIsEligible(allegiance) == alliedWith) {
                        special.add(current.fullmap[l][x][y]);
                    }
                }
            }
        }
        
        return special;
    }
    
    public static List<TangibleUnit> calculateEnemyUnits(TangibleUnit user, Conveyer info) {
            if (user == null) { return null; }
            
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (!user.unitStatus.alliedWith(tu.unitStatus) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> calculateAlliedUnits(TangibleUnit user, Conveyer info) {
            if (user == null) { return null; }
            
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (!user.is(tu) && user.unitStatus.alliedWith(tu.unitStatus) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> calculateAlliedUnits(UnitAllegiance allegiance, Conveyer info) {
            if (allegiance == null) { return null; }
            
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (allegiance.alliedWith(tu.unitStatus) && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> calculateUnitsOfAllegiance(UnitAllegiance allegiance, Conveyer info) {
            List<TangibleUnit> enemies = new ArrayList<>();
            for (TangibleUnit tu : info.getAllUnits()) {
                if (tu.unitStatus == allegiance && tu.getFSM().getState().getEnum() != UnitState.Dead) {
                    enemies.add(tu);
                }
            }
            
            return enemies;
        }
        
        public static List<TangibleUnit> retrieveCharacters(String[] names, Conveyer data) {
            List<TangibleUnit> characters = new ArrayList<>();
            
            for (String name : names) {
                data.getAllUnits().stream().filter((tu) -> (name.equals(tu.getName()))).forEachOrdered((tu) -> {
                    characters.add(tu);
                });
            }
        
            return characters;
        }
        
        public static List<MapEntity> retrieveEntities(String[] names, Conveyer data) {
            List<MapEntity> entities = new ArrayList<>();
            
            for (String name : names) {
                data.getMapEntities().stream().filter((entity) -> (name.equals(entity.getName()))).forEachOrdered((entity) -> {
                    entities.add(entity);
                });
            }
        
            return entities;
        }
}
