/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant;

import maps.layout.tile.RangeDisplay;
import maps.layout.tile.Path;
import maps.layout.tile.Tile;
import com.jme3.math.FastMath;
import etherealtempest.MasterFsmState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.tile.Path;

/**
 *
 * @author night
 */
public class VenturePeek {
    private final MapCoords position;
    private int mobility;
    
    public VenturePeek(MapCoords pos, int mob) {
        position = pos;
        mobility = mob;
    }
    
    public VenturePeek(Coords pos, int layer, int mob) {
        position = new MapCoords(pos, layer);
        mobility = mob;
    }
    
    public VenturePeek setMapCoords(MapCoords point) {
        position.set(point);
        return this;
    }
    
    public VenturePeek setCoords(Coords point) {
        position.setCoords(point);
        return this;
    }
    
    public VenturePeek setLayer(int layer) {
        position.setLayer(layer);
        return this;
    }
    
    public VenturePeek addXY(int x, int y) {
        position.addLocal(x, y);
        return this;
    }
    
    public VenturePeek addXY(Coords coords) {
        position.addLocal(coords);
        return this;
    }
    
    public VenturePeek addMobility(int delta) {
        mobility += delta;
        return this;
    }
    
    public VenturePeek setMobility(int mob) {
        mobility = mob;
        return this;
    }
    
    public boolean willReach(MapCoords coords) {
        if (position.getLayer() != coords.getLayer()) {
            return false;
        }
        
        int layer = coords.getLayer();
        Coords origin = position.getCoords(), destination = coords.getCoords();
        
        if (origin.nonDiagonalDistanceFrom(destination) <= mobility) {
            return RangeDisplay.shouldDisplayTile(origin, destination, layer, mobility);
        }
        
        return false;
    }
    
    public boolean isCloserThan(MapCoords other, MapCoords goal) {
        List<Tile> A = new Path(position, goal, 1000000).getPath();
        List<Tile> B = new Path(other, goal, 1000000).getPath();
        
        return A.size() >= B.size();
    }
    
    public List<MapCoords> smartCoordsForTilesOfRange(int range, MapCoords origin) { //does this with a preference, orders them by which the this instance's position is closest to
        List<MapCoords> positions = new ArrayList<>();
        
        if (position.getLayer() != origin.getLayer()) {
            return positions;
        }
        
        final int layer = position.getLayer();
        final float diff = FastMath.PI / 4f; 
        for (int d = 0; d <= range; d++) {
            HashMap<Integer, Coords> distances = new HashMap<>();
            
            for (float theta = diff; theta < FastMath.TWO_PI; theta += diff) {
                int xsign = (int)Math.signum(FastMath.cos(theta)), ysign = (int)Math.signum(FastMath.sin(theta));
                Coords value = new Coords(xsign * d, ysign * (range - d));
                distances.put(position.getCoords().nonDiagonalDistanceFrom(origin.getCoords().add(value)), value);
            }

            while (distances.keySet().size() > 0) {
                Set<Integer> keyset = distances.keySet();
                Integer lowestKey = null;
                for (int i = 0; i < keyset.size(); i++) {
                    Integer[] keys = distances.keySet().toArray(new Integer[distances.size()]);
                    if (lowestKey == null || keys[i] < lowestKey) {
                        lowestKey = keys[i];
                    }
                }
                
                positions.add(new MapCoords(distances.get(lowestKey), layer));
                distances.remove(lowestKey);
            }
        }
        
        return positions;
    }
    
    private static void addIfNew(List<Coords> coords, Coords coord) {
        if (!coords.contains(coord)) {
            coords.add(coord);
        }
    } 
    
    public static List<Coords> deltaCoordsForTilesOfRange(int range) { // f"(x)
        List<Coords> deltas = new ArrayList<>();
        
        for (int d = 0; d <= range; d++) {
            //Quadrant I (+, +)
            addIfNew(deltas, new Coords(d, range - d));
            //addIfNew(deltas, new Coords(range - d, d));
            
            //Quadrant II (-, +)
            addIfNew(deltas, new Coords(-1 * d, range - d));
            //addIfNew(deltas, new Coords(-1 * (range - d), d));
            
            //Quadrant III (-, -)
            addIfNew(deltas, new Coords(-1 * d, -1 * (range - d)));
            //addIfNew(deltas, new Coords(-1 * (range - d), -1 * d));
            
            //Quadrant IV (+, -)
            addIfNew(deltas, new Coords(d, -1 * (range - d)));
            //addIfNew(deltas, new Coords(range - d, -1 * d));
        }
        
        return deltas;
    }
    
    public static List<MapCoords> coordsForTilesOfRange(int range, MapCoords origin) { //border coords; f'(x)
        List<MapCoords> positions = new ArrayList<>();
        deltaCoordsForTilesOfRange(range).forEach((delta) -> {
            MapCoords next = origin.add(delta);
            if (MasterFsmState.getCurrentMap().isWithinBounds(next)) {
                positions.add(next);
            }
        });
        
        return positions;
    }
    
    public static List<MapCoords> filledCoordsForTilesOfRange(int range, MapCoords origin) { // f(x)
        List<MapCoords> positions = new ArrayList<>();
        positions.add(origin);
        for (int i = 1; i <= range; i++) {
            positions.addAll(coordsForTilesOfRange(i, origin));
        }
        
        return positions;
    }
    
    //converts List<Coords> in specified layer to List<Tile>
    public static List<Tile> toTile(List<Coords> list, int layer) {
        List<Tile> tiles = new ArrayList<>();
        Tile[][] layerTiles = MasterFsmState.getCurrentMap().getLayerTiles(layer);
        list.forEach((cds) -> {
            tiles.add(layerTiles[cds.x()][cds.y()]);
        });
        
        return tiles;
    }
    
    //converts List<MapCoords> to List<Tile>
    public static List<Tile> toTile(List<MapCoords> list) {
        List<Tile> tiles = new ArrayList<>();
        MapLevel map = MasterFsmState.getCurrentMap();
        list.forEach((cds) -> {
            tiles.add(map.getTileAt(cds));
        });
        
        return tiles;
    }

}
