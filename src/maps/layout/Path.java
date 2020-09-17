/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import etherealtempest.MasterFsmState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class Path {
    private final Map map;
    private final int startPosX, startPosY;
    private final int destX, destY, layer;
    //private final int length;
    
    private int length;
    
    private List<Tile> path = new ArrayList<>();
    private List<Coords> pathSequence = new ArrayList<>();
    
    private boolean succeeded;
    
    public Path(Map map, int startPosX, int startPosY, int destX, int destY, int layer, int length) {
        this.map = map;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.destX = destX;
        this.destY = destY;
        this.layer = layer;
        this.length = length;
        
        //length = Math.abs(destX - startPosX) + Math.abs(destY - startPosY);

        succeeded = generate(false);
        
        if (!succeeded) {
            pathSequence = new ArrayList<>();
            succeeded = generate(true);
        }
    }
    
    public Path(Coords start, Coords end, int layer, int length) {
        map = MasterFsmState.getCurrentMap();
        startPosX = start.getX();
        startPosY = start.getY();
        destX = end.getX();
        destY = end.getY();
        
        this.layer = layer;
        this.length = length;
        
        succeeded = generate(false);
        if (!succeeded) {
            pathSequence = new ArrayList<>();
            succeeded = generate(true);
        }
    }
    
    public Path setMaxLength(int len) {
        length = len;
        if (path.size() > length) {
            succeeded = false;
        }
        return this;
    }
    
    public boolean wasSuccess() { return succeeded; }
    
    public List<Tile> getPath() {
        return path;
    }

    private boolean generate(boolean flip) { //first option; tries to find path based on its current info
        if (map.fullmap[layer][destX][destY].isOccupied) {
            return false;
        }
        
        Coords elysium = new Coords(destX, destY);
        
        boolean success;
        success = pathfind(flip);
        for (int i = 0; i < (pathSequence.size() > 0 ? length - 1 : length); i++) {
            if (pathSequence.size() > 0) {
                if (!pathSequence.get(pathSequence.size() - 1).equals(elysium)) {
                    success = pathfind(flip);
                } else { i = length; }
            }
        }
        
        buildPath();
        return success ? (pathSequence.size() > 0 && pathSequence.get(pathSequence.size() - 1).equals(elysium)) : false;
    }
    
    private boolean pathfind(boolean flipPriority) {
        int prevX, prevY;
        if (pathSequence.size() > 0) {
            prevX = pathSequence.get(pathSequence.size() - 1).getX();
            prevY = pathSequence.get(pathSequence.size() - 1).getY();
        } else {
            prevX = startPosX;
            prevY = startPosY;
        }
        
        Coords nextCoords = getNextPathTile(prevX, prevY, flipPriority);
        
        if (nextCoords != null) {
            pathSequence.add(nextCoords);
            return pathSequence.size() <= length;
        }
        
        return false;
    }
    
    private Coords getNextPathTile(int prevX, int prevY, boolean flipPriority) {
        int xDiff = destX - prevX > 0 ? 1 : (destX - prevX < 0 ? -1 : 0);
        int yDiff = destY - prevY > 0 ? 1 : (destY - prevY < 0 ? -1 : 0);
        
        Coords tileCoords = new Coords(prevX, prevY);
        
        if (prevX != destX && map.isWithinBounds(tileCoords, layer) && !flipPriority) {
            tileCoords.setX(prevX + xDiff);
            
            if (sequenceHasCoords(tileCoords) || map.fullmap[layer][tileCoords.getX()][tileCoords.getY()].isOccupied) { //reverts the change if it treads previous ground
                tileCoords.setX(prevX);
                tileCoords.setY(prevY + (yDiff != 0 ? yDiff : 1)); //1 is default newYdiff
                
                if (sequenceHasCoords(tileCoords) || map.fullmap[layer][tileCoords.getX()][tileCoords.getY()].isOccupied) { //inverts the change if it treads previous ground
                    tileCoords.setY(prevY - (yDiff != 0 ? yDiff : 1)); //subtract 1
                }
            }
            return tileCoords;
        }
        if (prevY != destY && map.isWithinBounds(tileCoords, layer)) {
            tileCoords.setY(prevY + yDiff);
            
            if (sequenceHasCoords(tileCoords) || map.fullmap[layer][tileCoords.getX()][tileCoords.getY()].isOccupied) { //reverts the change if it treads previous ground
                tileCoords.setX(prevX + (xDiff != 0 ? xDiff : -1)); //-1 is default newXdiff
                tileCoords.setY(prevY);
                
                if (sequenceHasCoords(tileCoords) || map.fullmap[layer][tileCoords.getX()][tileCoords.getY()].isOccupied) { //inverts the change if it treads previous ground
                    tileCoords.setX(prevX - (xDiff != 0 ? xDiff : -1)); //subtract -1
                }
            }
            return tileCoords;
        }
        
        if (flipPriority && prevX != destX && map.isWithinBounds(tileCoords, layer)) {
            tileCoords.setX(prevX + xDiff);
            
            if (sequenceHasCoords(tileCoords) || map.fullmap[layer][tileCoords.getX()][tileCoords.getY()].isOccupied) { //reverts the change if it treads previous ground
                tileCoords.setX(prevX);
                tileCoords.setY(prevY + (yDiff != 0 ? yDiff : 1)); //1 is default newYdiff
                
                if (sequenceHasCoords(tileCoords) || map.fullmap[layer][tileCoords.getX()][tileCoords.getY()].isOccupied) { //inverts the change if it treads previous ground
                    tileCoords.setY(prevY - (yDiff != 0 ? yDiff : 1)); //subtract 1
                }
            }
            return tileCoords;
        }
        
        return null;
    }
    
    private boolean sequenceHasCoords(Coords cds) {
        return pathSequence.stream().anyMatch((point) -> (point.equals(cds)));
    }
    
    private void buildPath() {
        path = new ArrayList<>();
        pathSequence.forEach((coord) -> {
            path.add(map.fullmap[layer][coord.getX()][coord.getY()]);
            //System.out.print(coord.toString());
        });
        //System.out.println();
        
    }
    
    public void printPath() {
        pathSequence.forEach((coord) -> {
            System.out.println(coord.toString());
        });
    }
    
}
