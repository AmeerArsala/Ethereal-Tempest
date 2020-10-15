/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import maps.layout.tile.Path;
import maps.layout.tile.Tile;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import etherealtempest.MasterFsmState;
import java.util.LinkedList;
import maps.layout.tile.TileData;

/**
 *
 * @author night
 */
public class Map {
    private final int tilesX, tilesY, layers;
    private final Coords[][] layerBounds;
    
    private final String terrainName;
    private final Node tileNode = new Node("tile node for tiles and terrain");
    private final Node extraMapStuff = new Node("extra map things");
    
    public Tile[][][] fullmap; // a 3d array of layers, it's a 3d array due to multiple elevations 
    public Tile[][][] movSet; // for movement squares
    
    public Map(String terrainName, int tilesX, int tilesY, int layers, MapData data, AssetManager assetManager) {
        this.terrainName = terrainName;
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.layers = layers;
        
        layerBounds = new Coords[layers][2]; //2 for (x, y)
        //default settings
        for (int l = 0; l < layers; l++) {
            layerBounds[l][0] = new Coords(0, tilesX); //x coordinate bounds [0, tilesX)
            layerBounds[l][1] = new Coords(0, tilesY); //y coordinate bounds [0, tilesY)
        }
        
        generateTiles(assetManager, data);
    }
    
    public int getTilesX() { return tilesX; }
    public int getTilesY() { return tilesY; }
    public int getLayerCount() { return layers; }
    
    public String getName() { return terrainName; }
    
    public Coords[][] getBoundsForAllLayers() { return layerBounds; }
    
    public Node getMiscNode() {
        return extraMapStuff;
    }
    
    public Node getTileNode() {
        return tileNode;
    }
    
    public int getXLength(int layer) {
        return layerBounds[layer][0].getY();
    }
    
    public int getMinimumX(int layer) {
        return layerBounds[layer][0].getX();
    }
    
    public int getYLength(int layer) {
        return layerBounds[layer][1].getY();
    }
    
    public int getMinimumY(int layer) {
        return layerBounds[layer][1].getX();
    }
    
    public Map setMaximumXTile(int maxX, int layer) { //inclusive
        layerBounds[layer][0].setY(maxX);
        return this;
    }
    
    public Map setMaximumYTile(int maxY, int layer) { //inclusive
        layerBounds[layer][1].setY(maxY);
        return this;
    }
    
    public Map setMinimumXTile(int minX, int layer) { //inclusive
        layerBounds[layer][0].setX(minX);
        return this;
    }
    
    public Map setMinimumYTile(int minY, int layer) { //inclusive
        layerBounds[layer][1].setX(minY);
        return this;
    }
    
    public boolean isWithinXBounds(int test, int layer) {
        return test >= getMinimumX(layer) && test < getXLength(layer);
    }
    
    public boolean isWithinYBounds(int test, int layer) {
        return test >= getMinimumY(layer) && test < getYLength(layer);
    }
    
    public boolean isWithinBounds(Coords test, int layer) {
        return test.getX() >= getMinimumX(layer) && test.getX() < getXLength(layer) 
               && test.getY() >= getMinimumY(layer) && test.getY() < getYLength(layer);
    }
    
    private void generateTiles(AssetManager assetManager, MapData info) {
        List<TileData[][]> tileInfo = info.interpret(assetManager);
        
        //make new instances
        fullmap = new Tile[layers][tilesX][tilesY];
        movSet = new Tile[layers][tilesX][tilesY];
        for (int l = 0; l < layers; l++) {
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    TileData tile = tileInfo.get(l)[y][x];
                    //System.out.println("(" + x + ", " + y + "): " + tile.getVisuals().getGroundType());
                    
                    //for actual tiles
                    fullmap[l][x][y] = new Tile(tile, assetManager, x, y, l);
                    fullmap[l][x][y].setLocalTranslation(tilesY * y, 50 * l, tilesX * x);
                    tileNode.attachChild(fullmap[l][x][y].getNode());
                    
                    //for move squares
                    movSet[l][x][y] = new Tile(x, y, l);
                    movSet[l][x][y].emulateOtherTileAsMoveSquare(assetManager, fullmap[l][x][y]);
                    movSet[l][x][y].setLocalTranslation(tilesY * y, 50 * l, tilesX * x);
                    extraMapStuff.attachChild(movSet[l][x][y].getGeometry());
                }
            }
        }
    }
    
    public String getMapString(int elv) { 
        String[] mapstrings = new String[layers]; // an array of map Strings, it's an array due to multiple elevations
        mapstrings[elv] = "";
        for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    mapstrings[elv] += fullmap[elv][x][y] + " ";
                }
            mapstrings[elv] += "\n";
        }
        
        
        return mapstrings[elv];
    }
    
    public static boolean isWithinSpaces(int spaces, int x1, int y1, int x2, int y2) {
        int diffY = Math.abs(y2 - y1),
            diffX = Math.abs(x2 - x1);
        return ((diffX + diffY) <= spaces);
    }
    
    public static boolean isWithinSpaces(int spaces, Coords p1, Coords p2) {
        int diffX = Math.abs(p2.getX() - p1.getX()),
            diffY = Math.abs(p2.getY() - p1.getY());
        return ((diffX + diffY) <= spaces);
    }
    
    public LinkedList<Tile> reorderTheseTilesByClosestTo(int x, int y, List<Tile> tiles) {
        LinkedList<Tile> reordered = new LinkedList<>();
        
        List<Integer> alreadyAddedIndexes = new ArrayList<>();
        for (int index = 0; index < tiles.size(); index++) {
            if (tiles.get(index).getPosX() == x && tiles.get(index).getPosY() == y) { //if the tileList already has the targeted tile
                reordered.addFirst(tiles.get(index));
                alreadyAddedIndexes.add(index);
            } else {
                //add closest tile
                int shortestIndex = -1;
                for (int t = 0; t < tiles.size(); t++) {
                    if (!alreadyAddedIndexes.contains(t)) {
                        if (shortestIndex == -1 || (peekPathSize(x, y, tiles.get(t)) < peekPathSize(x, y, tiles.get(shortestIndex)))) {
                            shortestIndex = t;
                        }
                    }
                }
                
                reordered.add(tiles.get(shortestIndex));
                alreadyAddedIndexes.add(shortestIndex);
            }
        }
        
        return reordered;
    }
    
    private int peekPathSize(int startX, int startY, Tile square) {
        return new Path(this, startX, startY, square.getPosX(), square.getPosY(), square.getElevation(), 100).getPath().size();
    }
    
    public static int peekPathSize(Coords start, Coords end, int layer) {
        return new Path(MasterFsmState.getCurrentMap(), start.getX(), start.getY(), end.getX(), end.getY(), layer, 100).getPath().size();
    }
}
