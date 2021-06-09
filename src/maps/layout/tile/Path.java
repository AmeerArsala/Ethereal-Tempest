/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import etherealtempest.MasterFsmState;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapBounds;
import maps.layout.MapCoords;

/**
 *
 * @author night
 */
public class Path {
    private final MapLevel map;
    private final MapBounds mapBounds;
    
    private final MapCoords start, end;
    private final int moveCapacity;
    
    private final List<Tile> path = new ArrayList<>();
    private final LinkedList<MapCoords> pathSequence = new LinkedList<>();
    
    private boolean success = true;
    private int remainingMoves;
    private int optionCheckStartIndex = 0;
    
    public Path(int startPosX, int startPosY, int destX, int destY, int layer, int spaceLimit) {
        this(new MapCoords(new Coords(startPosX, startPosY), layer), new MapCoords(new Coords(destX, destY), layer), spaceLimit, MasterFsmState.getCurrentMap());
    }
    
    public Path(Coords start, Coords end, int layer, int moveCapacity) {
        this(new MapCoords(start, layer), new MapCoords(end, layer), moveCapacity, MasterFsmState.getCurrentMap());
    }
    
    public Path(MapCoords start, MapCoords end, int moveCapacity) {
        this(start, end, moveCapacity, MasterFsmState.getCurrentMap());
    }
    
    public Path(MapCoords start, MapCoords end, int moveCapacity, MapLevel gridmap) {
        this.start = start;
        this.end = end;
        this.moveCapacity = moveCapacity;
        
        map = gridmap;
        mapBounds = map.getBounds();
        
        success = (end.getLayer() == start.getLayer()) && moveCapacity >= end.spacesFrom(start) && !map.getTileAt(end).isOccupied;
        if (success) {
            generatePath();
        }
    }
    
    public List<Tile> getPath() { return path; }
    public List<MapCoords> getSequence() { return pathSequence; }
    
    public int getPathSize() { 
        return path.size(); 
    }
    
    public boolean wasSuccess() { return success; }
    
    public MapCoords getInitialPos() { return start; }
    public MapCoords getFinalPos() { return end; }
    public int getMoveCapacity() { return moveCapacity; }
    
    private boolean isWithinBounds(MapCoords test) {
        return mapBounds.isWithinBounds(test);
    }
    
    boolean sequenceHasCoords(MapCoords cds) {
        return pathSequence.stream().anyMatch((point) -> (point.equals(cds)));
    }
    
    //either was already in sequence, or 1 value difference
    private boolean sequenceAlreadyHadAccessToCoords(MapCoords cds) {
        for (int i = 0; i < pathSequence.size() - 1; ++i) {
            if (pathSequence.get(i).spacesFrom(cds) <= 1) {
                return true;
            }
        }
        
        return false;
    }
    
    private List<MapCoords> optionsAt(MapCoords tile) { //tile must be 1 space away from the latest Coords in pathSequence
        List<MapCoords> options = new ArrayList<>();
        if (!isWithinBounds(tile)) {
            return options;
        }
        
        MapCoords[] cardinalDirections = 
        {
            tile.add(0, 1),  // North
            tile.add(0, -1), // South
            tile.add(1, 0),  // East
            tile.add(-1, 0)  // West
        };
        
        for (MapCoords possibleOption : cardinalDirections) {
            //if option is available
            if (isWithinBounds(possibleOption) && !map.getTileAt(possibleOption).isOccupied && !sequenceAlreadyHadAccessToCoords(possibleOption)) {
                options.add(possibleOption);
            }
        }
        
        return options;
    }
    
    //A tile is a dead end if none of the direct options from that tile will still allow you to reach 'end'
    private boolean tileIsDeadEnd(MapCoords tile) {
        List<MapCoords> options = optionsAt(tile);
        for (MapCoords option : options) {
            if (remainingMoves - 1 >= option.getCoords().nonDiagonalDistanceFrom(end.getCoords())) {
                return false;
            }
        }
        
        return true;
    }
    
    private void generatePath() {
        remainingMoves = moveCapacity;
        pathSequence.add(start);
        
        while (!pathSequence.getLast().equals(end)) {
            MapCoords next = nextTile();
            
            if (next == null) {
                success = false;
                return;
            }
            
            pathSequence.add(next);
            optionCheckStartIndex = 0;
            --remainingMoves;
            
            if (remainingMoves > 0 && end.spacesFrom(pathSequence.getLast()) == 1) {
                pathSequence.add(end);
            }
        }
        
        finishGeneration();
    }
    
    private void finishGeneration() {
        success = !pathSequence.isEmpty() && pathSequence.size() <= moveCapacity + 1 && pathSequence.getLast().equals(end);
        if (success) {
            pathSequence.removeFirst(); //removes 'start'
            buildPath();
        }
    }
    
    private MapCoords nextTile() {
        MapCoords next = attemptNextTile();
        
        //pathSequence.getLast() is a dead end
        //backpedal in this case
        while (next == null) {
            if (pathSequence.size() == 1) {
                return null;
            }
            
            optionCheckStartIndex = pathSequence.getLast().getCoords().getRange() + 1;
            pathSequence.removeLast();
            ++remainingMoves;
            
            next = attemptNextTile();
        }
        
        return next;
    }
    
    private MapCoords nextTileRecursive() {
        MapCoords next = attemptNextTile();
        if (next != null) {
            return next;
        }
        
        //pathSequence.getLast() is a dead end
        //backpedal in this case
        optionCheckStartIndex = pathSequence.getLast().getCoords().getRange() + 1;
        pathSequence.removeLast();
        ++remainingMoves;
        
        return nextTileRecursive();
    }
    
    private MapCoords attemptNextTile() {
        List<MapCoords> options = optionsAt(pathSequence.getLast());
        for (int i = optionCheckStartIndex; i < options.size(); ++i) {
            if (!tileIsDeadEnd(options.get(i))) {
                options.get(i).getCoords().setRange(i); //range will be the index that wasn't a dead end
                return options.get(i);
            }
        }
        
        //pathSequence.getLast() is a dead end
        return null;
    }
    
    private void buildPath() {
        path.clear();
        pathSequence.forEach((coord) -> {
            path.add(map.getTileAt(coord));
        });
    }
    
    public void printPath() {
        pathSequence.forEach((coord) -> {
            System.out.println(coord.toString());
        });
    }
}
