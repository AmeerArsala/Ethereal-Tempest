/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import com.jme3.math.FastMath;
import general.GeneralUtils;
import general.NodeTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class Path {
    private final Map map;
    private final int startPosX, startPosY;
    private final int destX, destY, layer;
    private final int length;
    
    private List<Tile> path = new ArrayList<>();
    private List<Coords> pathSequence = new ArrayList<>();
    private NodeTree<Coords> blacklist = new NodeTree<>();
    
    public Path(Map map, int startPosX, int startPosY, int destX, int destY, int layer) {
        this.map = map;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.destX = destX;
        this.destY = destY;
        this.layer = layer;
        
        length = Math.abs(destX - startPosX) + Math.abs(destY - startPosY);

        boolean success;
        success = generate(false);
        System.out.println("success1: " + success);
        
        if (!success) {
            
            blacklist.put(GeneralUtils.cloneList(pathSequence));
            path = new ArrayList<>();
            pathSequence = new ArrayList<>();
            boolean successflip;
            successflip = generate(true);
            System.out.println("succes2: " + successflip);
            
            /*if (!successflip) {
                blacklist.put(GeneralUtils.cloneList(pathSequence));
                generateUnbiased();
            }*/
        }
    }
    
    public List<Tile> getPath() {
        return path;
    }
    
    private void generateUnbiased() { //will take into account the blacklist + all possibilities to reach goal
        pathSequence = new ArrayList<>();
        
        while (pathSequence.size() < length) {
            findUnbiased();
        }
        
        if (pathSequence.get(pathSequence.size() - 1).getX() != destX || pathSequence.get(pathSequence.size() - 1).getY() != destY) {
            System.out.println("no");
            blacklist.put(GeneralUtils.cloneList(pathSequence));
            generateUnbiased();
            return;
        }
        
        buildPath();
    }
    
    private void findUnbiased() {
        int prevX, prevY;
        if (pathSequence.size() > 0) {
            prevX = pathSequence.get(pathSequence.size() - 1).getX();
            prevY = pathSequence.get(pathSequence.size() - 1).getY();
        } else {
            prevX = startPosX;
            prevY = startPosY;
        }
        
        //"possible" options
        ArrayList<Coords> possibleOptions = new ArrayList<>();
        possibleOptions.add(new Coords(prevX + 1, prevY)); // right
        possibleOptions.add(new Coords(prevX, prevY + 1)); // up
        possibleOptions.add(new Coords(prevX - 1, prevY)); // left
        possibleOptions.add(new Coords(prevX, prevY - 1)); // down
        
        List<Coords> toRemove = new ArrayList<>();
        
        //remove options that aren't plausible
        possibleOptions.stream().filter((option) -> (option.getX() < 0 || option.getY() < 0 || option.getX() >= map.getTilesX() || option.getY() >= map.getTilesY())).forEachOrdered((option) -> {
            toRemove.add(option);
        });
        
        //remove options that consist of a previous tile
        if (pathSequence.size() > 0) {
            pathSequence.forEach((already) -> {
                possibleOptions.stream().filter((option) -> (already.equals(option))).forEachOrdered((option) -> {
                    if (!toRemove.contains(option)) {
                        toRemove.add(option);
                    }
                });
            });
        }
        
        //remove blacklisted options
        possibleOptions.stream().filter((option) -> (!toRemove.contains(option) && blacklist.getAllChildren(GeneralUtils.addItem(pathSequence, option)).size() > 0 && ((float)FastMath.log((blacklist.getAllChildren(GeneralUtils.addItem(pathSequence, option)).size()), 3)) == ((int)FastMath.log((blacklist.getAllChildren(GeneralUtils.addItem(pathSequence, option)).size()), 3)))).forEachOrdered((option) -> {
            toRemove.add(option);
        });
        
        toRemove.forEach((die) -> {
            possibleOptions.remove(die);
        });
        
        //pathSequence.add(possibleOptions.get(FastMath.rand.nextInt(possibleOptions.size())));
        for (int i = 0; i < possibleOptions.size(); i++) {
            pathSequence.add(possibleOptions.get(i));
            if (blacklist.getValueByOrigins(pathSequence) != null) {
                pathSequence.remove(possibleOptions.get(i));
            }
            i = possibleOptions.size();
        }
    }

    private boolean generate(boolean flip) { //first option; tries to find path based on its current info
        boolean success = false;
        while (path.size() < length) {
            success = pathfind(flip);
        }
        
        return success;
    }
    
    private boolean pathfind(boolean flipPriority) {
        int prevX, prevY;
        if (path.size() > 0) {
            prevX = path.get(path.size() - 1).getPosX();
            prevY = path.get(path.size() - 1).getPosY();
        } else {
            prevX = startPosX;
            prevY = startPosY;
        }
        
        Coords nextCoords = getNextPathTile(prevX, prevY, flipPriority);
        
        if (nextCoords != null) {
            path.add(map.fullmap[layer][nextCoords.getX()][nextCoords.getY()]);
            pathSequence.add(nextCoords);
            return true;
        }
        
        return false;
    }
    
    private Coords getNextPathTile(int prevX, int prevY, boolean flipPriority) {
        int xDiff = destX - prevX > 0 ? 1 : (destX - prevX < 0 ? -1 : 0);
        int yDiff = destY - prevY > 0 ? 1 : (destY - prevY < 0 ? -1 : 0);
        
        Coords tileCoords = new Coords(prevX, prevY);
        boolean obstacle = false;
        
        if (prevX != destX && !flipPriority) {
            if (!map.fullmap[layer][prevX + xDiff][prevY].isOccupied) {
                tileCoords.setX(prevX + xDiff);
                return tileCoords;
            } else {
                obstacle = true;
            }
        } 
        if (prevY != destY) {
            if (!map.fullmap[layer][prevX][prevY + yDiff].isOccupied) {
                tileCoords.setY(prevY + yDiff);
                return tileCoords;
            } else {
                obstacle = true;
            }
        }
        
        if (obstacle) {
        
        }
        
        return null;
    }
    
    private void buildPath() {
        path = new ArrayList<>();
        pathSequence.forEach((coord) -> {
            path.add(map.fullmap[layer][coord.getX()][coord.getY()]);
        });
    }
    
    private class Coords {
        private int xcoord, ycoord;
        
        public Coords(int x, int y) {
            xcoord = x;
            ycoord = y;
        }
        
        public int getX() { return xcoord; }
        public int getY() { return ycoord; }
        
        public void setX(int x) { xcoord = x; }
        public void setY(int y) { ycoord = y; }
        
        public void setCoords(int x, int y) {
            xcoord = x;
            ycoord = y;
        }
        
        public void setCoords(Coords otro) {
            xcoord = otro.getX();
            ycoord = otro.getY();
        }
        
        public boolean equals(Coords other) {
            return xcoord == other.xcoord && ycoord == other.ycoord;
        }
    }
    
}
