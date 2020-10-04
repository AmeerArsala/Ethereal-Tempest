/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import maps.layout.tile.RangeDisplay;
import maps.layout.tile.Path;
import maps.layout.tile.Tile;
import com.jme3.math.FastMath;
import etherealtempest.MasterFsmState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author night
 */
public class VenturePeek {
    private final Coords position;
    private final int elevation;
    
    private int mobility;
    
    public VenturePeek(int posX, int posY, int elv, int mob) {
        position = new Coords(posX, posY);
        elevation = elv;
        mobility = mob;
    }
    
    public VenturePeek addX(int x) {
        position.addX(x);
        return this;
    }
    
    public VenturePeek addY(int y) {
        position.addX(y);
        return this;
    }
    
    public VenturePeek setCoords(Coords point) {
        position.setCoords(point);
        return this;
    }
    
    public VenturePeek addMobility(int delta) {
        mobility += delta;
        return this;
    }
    
    public boolean willReach(int x, int y) {
        Map mp = MasterFsmState.getCurrentMap();
        
        boolean withinSpaces = false;
        for (int layer = 0; layer < mp.getLayerCount(); layer++) {
            if (Map.isWithinSpaces(mobility, position.getX(), position.getY(), mp.fullmap[layer][x][y].getPosX(), mp.fullmap[layer][x][y].getPosY())) {
                withinSpaces = true;
                layer = mp.getLayerCount();
            }
        }
        
        return withinSpaces && RangeDisplay.shouldDisplayTile(position.getX(), position.getY(), x, y, elevation, mobility, mp);
    }
    
    public boolean willReach(Coords cds) {
        return willReach(cds.getX(), cds.getY());
    }
    
    public boolean isCloserThan(Coords other, Coords goal) {
        List<Tile> A = new Path(position, goal, elevation, 100).getPath();
        List<Tile> B = new Path(other, goal, elevation, 100).getPath();
        
        return A.size() >= B.size();
    }
    
    private static void addIfNew(Coords coord, List<Coords> coords) {
        if (!coords.contains(coord)) {
            coords.add(coord);
        }
    } 
    
    public static List<Coords> deltaCoordsForTilesOfRange(int range) { // f"(x)
        List<Coords> deltas = new ArrayList<>();
        
        for (int d = 0; d <= range; d++) {
            //Quadrant I (+, +)
            addIfNew(new Coords(d, range - d), deltas);
            //addIfNew(new Coords(range - d, d), deltas);
            
            //Quadrant II (-, +)
            addIfNew(new Coords(-1 * d, range - d), deltas);
            //addIfNew(new Coords(-1 * (range - d), d), deltas);
            
            //Quadrant III (-, -)
            addIfNew(new Coords(-1 * d, -1 * (range - d)), deltas);
            //addIfNew(new Coords(-1 * (range - d), -1 * d), deltas);
            
            //Quadrant IV (+, -)
            addIfNew(new Coords(d, -1 * (range - d)), deltas);
            //addIfNew(new Coords(range - d, -1 * d), deltas);
        }
        
        return deltas;
    }
    
    public static List<Coords> coordsForTilesOfRange(int range, Coords origin, int layer) { //border coords; f'(x)
        List<Coords> positions = new ArrayList<>();
        deltaCoordsForTilesOfRange(range).forEach((delta) -> {
            if (MasterFsmState.getCurrentMap().isWithinBounds(origin.combine(delta), layer)) {
                positions.add(origin.combine(delta));
            }
        });
        
        return positions;
    }
    
    public static List<Coords> filledCoordsForTilesOfRange(int range, Coords origin, int layer) { // f(x)
        List<Coords> positions = new ArrayList<>();
        positions.add(origin);
        for (int i = 1; i <= range; i++) {
            positions.addAll(coordsForTilesOfRange(i, origin, layer));
        }
        
        return positions;
    }
    
    public static List<Tile> toTile(List<Coords> list, int layer) {
        List<Tile> tiles = new ArrayList<>();
        Tile[][] layerTiles = MasterFsmState.getCurrentMap().fullmap[layer];
        list.forEach((cds) -> {
            tiles.add(layerTiles[cds.getX()][cds.getY()]);
        });
        
        return tiles;
    }
    
    public List<Coords> smartCoordsForTilesOfRange(int range, Coords origin, int layer) { //does this with a preference, orders them by which the this instance's position is closest to
        List<Coords> positions = new ArrayList<>();
        
        for (int d = 0; d <= range; d++) {
            HashMap<Integer, Coords> distances = new HashMap<>();
            
            final float diff = FastMath.HALF_PI / 2f;
            for (float theta = diff; theta < FastMath.TWO_PI; theta += diff) {
                int xsign = (int)Math.signum(FastMath.cos(theta)), ysign = (int)Math.signum(FastMath.sin(theta));
                Coords value = new Coords(xsign * d, ysign * (range - d));
                distances.put(position.difference(origin.combine(value)), value);
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
                
                positions.add(distances.get(lowestKey));
                distances.remove(lowestKey);
            }
        }
        
        return positions;
    }

}
