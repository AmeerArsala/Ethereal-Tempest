/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile.move;

import etherealtempest.fsm.MasterFsmState;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import maps.layout.Coords;
import maps.layout.MapLevel;
import maps.layout.MapCoords;
import maps.layout.tile.Tile;


/**
 *
 * @author night
 * 
 * Uses the old path algorithm
 * Not really used but has some uses
 * 
 */
public class OldPath {
    private final MapLevel map;
    
    private final MapCoords startPos, destPos;
    private final int spaceLimit; //the limit of spaces it can be; the mobility a unit has. This will be the maximum length of the pathway
    
    private final List<Tile> path = new ArrayList<>();
    private final List<MapCoords> pathSequence = new ArrayList<>();
    
    private boolean succeeded;
    
    public OldPath (MapCoords startPos, MapCoords destPos, int spaceLimit) {
        this(MasterFsmState.getCurrentMap(), startPos, destPos, spaceLimit);
    }
    
    public OldPath(MapLevel map, MapCoords startPos, MapCoords destPos, int spaceLimit) {
        this.map = map;
        this.startPos = startPos;
        this.destPos = destPos;
        this.spaceLimit = spaceLimit;
        
        //length = Math.abs(destX - startPosX) + Math.abs(destY - startPosY);

        succeeded = generate(false);
        
        if (!succeeded) {
            pathSequence.clear();
            succeeded = generate(true);
        }
    }
    
    public List<Tile> getPath() { return path; }
    public boolean wasSuccess() { return succeeded; }
    
    public MapCoords getStartPos() { return startPos; }
    public MapCoords getDestPos() { return destPos; }
    
    public int getPathSize() {
        return path.size();
    }
    
    List<MapCoords> getSequence() { return pathSequence; }
    
    boolean sequenceHasCoords(MapCoords cds) {
        return pathSequence.stream().anyMatch((point) -> (point.equals(cds)));
    }

    private boolean generate(boolean flip) { //first option; tries to find path based on its current info
        if (startPos.getLayer() != destPos.getLayer() || map.getTileAt(destPos).isOccupied) {
            return false;
        }
        
        Coords elysium = destPos.getCoords();
        
        boolean success;
        success = pathfind(flip);
        for (int i = 0; i < (pathSequence.size() > 0 ? spaceLimit - 1 : spaceLimit); i++) {
            if (pathSequence.size() > 0) {
                if (!pathSequence.get(pathSequence.size() - 1).equals(elysium)) {
                    success = pathfind(flip);
                } else { i = spaceLimit; }
            }
        }
        
        if (success) {
            buildPath();
            return (pathSequence.size() > 0 && pathSequence.get(pathSequence.size() - 1).equals(elysium));
        }
        
        return false;
    }
    
    private boolean pathfind(boolean flipPriority) {
        Coords prevPos;
        if (pathSequence.size() > 0) {
            prevPos = pathSequence.get(pathSequence.size() - 1).getCoords();
        } else {
            prevPos = startPos.getCoords();
        }
        
        MapCoords nextCoords = getNextPathTile(prevPos, flipPriority);
        
        if (nextCoords != null) {
            pathSequence.add(nextCoords);
            return pathSequence.size() <= spaceLimit;
        }
        
        return false;
    }
    
    private MapCoords getNextPathTile(Coords prevPos, boolean flipPriority) {
        Coords diff = prevPos.signsOf();
        
        MapCoords tileCoords = new MapCoords(prevPos, destPos.getLayer());
        
        if (prevPos.x != destPos.getCoords().x && map.isWithinBounds(tileCoords) && !flipPriority) {
            tileCoords.getCoords().x = prevPos.x + diff.x; 
            
            if (sequenceHasCoords(tileCoords) || map.getTileAt(tileCoords).isOccupied) { //reverts the change if it treads previous ground
                tileCoords.getCoords().setCoords(prevPos.x, prevPos.y + (diff.y != 0 ? diff.y : 1)); //1 is default newYdiff
                
                if (sequenceHasCoords(tileCoords) || map.getTileAt(tileCoords).isOccupied) { //inverts the change if it treads previous ground
                    tileCoords.getCoords().y = prevPos.y - (diff.y != 0 ? diff.y : 1); //subtract 1
                }
            }
            return tileCoords;
        }
        
        if (prevPos.y != destPos.getCoords().y && map.isWithinBounds(tileCoords)) {
            tileCoords.getCoords().y = prevPos.y + diff.y;
            
            if (sequenceHasCoords(tileCoords) || map.getTileAt(tileCoords).isOccupied) { //reverts the change if it treads previous ground
                tileCoords.getCoords().setCoords(prevPos.x + (diff.x != 0 ? diff.x : -1), prevPos.y); //-1 is default newXdiff
                
                if (sequenceHasCoords(tileCoords) || map.getTileAt(tileCoords).isOccupied) { //inverts the change if it treads previous ground
                    tileCoords.getCoords().x = prevPos.x - (diff.x != 0 ? diff.x : -1); //subtract -1
                }
            }
            return tileCoords;
        }
        
        if (flipPriority && prevPos.x != destPos.getCoords().x && map.isWithinBounds(tileCoords)) {
            tileCoords.getCoords().x = prevPos.x + diff.x; 
            
            if (sequenceHasCoords(tileCoords) || map.getTileAt(tileCoords).isOccupied) { //reverts the change if it treads previous ground
                tileCoords.getCoords().setCoords(prevPos.x, prevPos.y + (diff.y != 0 ? diff.y : 1)); //1 is default newYdiff
                
                if (sequenceHasCoords(tileCoords) || map.getTileAt(tileCoords).isOccupied) { //inverts the change if it treads previous ground
                    tileCoords.getCoords().y = prevPos.y - (diff.y != 0 ? diff.y : 1); //subtract 1
                }
            }
            return tileCoords;
        }
        
        return null;
    }
    
    private void buildPath() {
        path.clear();
        pathSequence.forEach((coord) -> {
            path.add(map.getTileAt(coord));
            //System.out.print(coord.toString());
        });
        //System.out.println();
        
    }
    
    public void printPath(PrintStream printStream) { //you can pass in stuff like System.out and System.err
        pathSequence.forEach((coord) -> {
            printStream.println(coord.toString());
        });
    }
}
