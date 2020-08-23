/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

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
    
    public Path(Map map, int startPosX, int startPosY, int destX, int destY, int layer) {
        this.map = map;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.destX = destX;
        this.destY = destY;
        this.layer = layer;
        
        //length = Math.abs(destX - startPosX) + Math.abs(destY - startPosY);

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
        
        boolean success;
        success = pathfind(flip);
        while (!pathSequence.get(pathSequence.size() - 1).equals(new Coords(destX, destY))) {
            success = pathfind(flip);
        }
        
        buildPath();
        return success;
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
            return true;
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
    
}
