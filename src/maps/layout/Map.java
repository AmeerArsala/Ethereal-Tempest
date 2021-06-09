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
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import java.util.ArrayList;
import java.util.List;
import etherealtempest.MasterFsmState;
import general.visual.DeserializedParticleEffect;
import java.util.LinkedList;
import maps.layout.tile.TileData;
import maps.layout.tile.TileFoundation;

/**
 *
 * @author night
 */
public class Map {
    public static TextureArray tileTextures;
    public static Texture OverflowBlendMap;
    
    private final int tilesX, tilesY, layers;
    private final Coords[][] layerBounds;
    
    private MapBounds bounds;
    
    private final String terrainName;
    private final Node tileNode = new Node("tile node for tiles and terrain");
    private final Node extraMapStuff = new Node("extra map things");
    
    public Tile[][][] fullmap; // a 3d array of layers, it's a 3d array due to multiple elevations 
    public TileFoundation[][][] movSet; // for movement squares
    
    //basic needs
    private Map(String terrainName, int tilesX, int tilesY, int layers) {
        this.terrainName = terrainName;
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.layers = layers;
        
        layerBounds = new Coords[layers][2]; //2 for (x, y)
    }
    
    //most often used
    public Map(String terrainName, int tilesX, int tilesY, int layers, MapData data, AssetManager assetManager) {
        this(terrainName, tilesX, tilesY, layers);
        
        //default settings/bounds
        for (int l = 0; l < layers; l++) {
            layerBounds[l][0] = new Coords(0, tilesX); //x coordinate bounds [0, tilesX)
            layerBounds[l][1] = new Coords(0, tilesY); //y coordinate bounds [0, tilesY)
        }
        
        setBounds();
        generateTiles(assetManager, data);
    }
    
    //with bounds included in constructor
    public Map(String terrainName, int tilesX, int tilesY, int layers, Coords[] xBoundsForEachLayer, Coords[] yBoundsForEachLayer, MapData data, AssetManager assetManager) {
        this(terrainName, tilesX, tilesY, layers);
        
        for (int l = 0; l < layers; l++) {
            layerBounds[l][0] = xBoundsForEachLayer[l];
            layerBounds[l][1] = yBoundsForEachLayer[l];
        }
        
        setBounds();
        generateTiles(assetManager, data);
    }
    
    public String getName() { return terrainName; }
    
    public int getTilesX() { return tilesX; }
    public int getTilesY() { return tilesY; }
    public int getLayerCount() { return layers; }
    
    public Node getTileNode() {
        return tileNode;
    }
    
    public Node getMiscNode() {
        return extraMapStuff;
    }
    
    private void generateTiles(AssetManager assetManager, MapData info) {
        List<TileData[][]> tileInfo = info.interpret(assetManager);

        //make new instances
        fullmap = new Tile[layers][tilesX][tilesY];
        movSet = new TileFoundation[layers][tilesX][tilesY];
        for (int l = 0; l < layers; l++) {
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    //TileData tile = tileInfo.get(l)[y][x];
                    
                    //for actual tiles
                    fullmap[l][x][y] = new Tile(x, y, l, tileInfo, bounds, assetManager);
                    fullmap[l][x][y].setLocalTranslation(tilesY * y, 50 * l, tilesX * x);
                    tileNode.attachChild(fullmap[l][x][y].getNode());
                    
                    //for move squares
                    movSet[l][x][y] = new TileFoundation(x, y, l, assetManager);
                    movSet[l][x][y].setLocalTranslation(tilesY * y, 50 * l, tilesX * x);
                    extraMapStuff.attachChild(movSet[l][x][y].getGeometry());
                }
            }
        }
    }
    
    public void generateExtra(AssetManager assetManager, MapData info, Node cursorNode) {
        DeserializedParticleEffect[] effects = info.retrieveMapEffects(assetManager);
        if (effects != null) {
            for (DeserializedParticleEffect effect : effects) {
                cursorNode.attachChild(effect.modelRoot);
            }
        }
    }
    
    public Tile getTileAt(int x, int y, int layer) {
        return fullmap[layer][x][y];
    }
    
    public TileFoundation getMovSquareAt(int x, int y, int layer) {
        return movSet[layer][x][y];
    }
    
    public Tile[][] getLayerTiles(int layer) {
        return fullmap[layer];
    }
    
    public TileFoundation[][] getLayerMovSquares(int layer) {
        return movSet[layer];
    }
    
    public MapBounds getBounds() { return bounds; }
    
    private void setBounds() {
        bounds = new MapBounds() {
            @Override
            public Coords[][] getBoundsForAllLayers() { 
                return layerBounds; 
            }
            
            @Override
            public int getXLength(int layer) {
                return layerBounds[layer][0].getY();
            }
            
            @Override
            public int getMinimumX(int layer) {
                return layerBounds[layer][0].getX();
            }
            
            @Override
            public int getYLength(int layer) {
                return layerBounds[layer][1].getY();
            }
            
            @Override
            public int getMinimumY(int layer) {
                return layerBounds[layer][1].getX();
            }
            
            @Override
            public boolean isWithinXBounds(int test, int layer) {
                return test >= getMinimumX(layer) && test < getXLength(layer);
            }
            
            @Override
            public boolean isWithinYBounds(int test, int layer) {
                return test >= getMinimumY(layer) && test < getYLength(layer);
            }
            
            @Override
            public boolean isWithinBounds(Coords test, int layer) {
                return test.getX() >= getMinimumX(layer) && test.getX() < getXLength(layer) 
                       && test.getY() >= getMinimumY(layer) && test.getY() < getYLength(layer);
            }
        };
    }
    
    public int getXLength(int layer) { return bounds.getXLength(layer); }
    public int getMinimumX(int layer) { return bounds.getMinimumX(layer); }
    public int getYLength(int layer) { return bounds.getYLength(layer); }
    public int getMinimumY(int layer) { return bounds.getMinimumY(layer); }
    public boolean isWithinBounds(Coords test, int layer) { return bounds.isWithinBounds(test, layer); }
    
    public LinkedList<Tile> reorderTheseTilesByClosestTo(Coords point, List<Tile> tiles) {
        return reorderTheseTilesByClosestTo(point.getX(), point.getY(), tiles);
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
    
    @Override
    public String toString() {
        String mapstring = "";
        for (int elv = 0; elv < layers; elv++) {
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    mapstring += fullmap[elv][x][y].getName() + " ";
                }
                
                mapstring += "\n";
            }
        }
        
        return terrainName + ": -> " + super.toString() + "\n" + mapstring;
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
    
    private int peekPathSize(int startX, int startY, Tile square) {
        return new Path(this, startX, startY, square.getPosX(), square.getPosY(), square.getElevation(), 100).getPath().size();
    }
    
    public static int peekPathSize(Coords start, Coords end, int layer) {
        return new Path(MasterFsmState.getCurrentMap(), start.getX(), start.getY(), end.getX(), end.getY(), layer, 100).getPath().size();
    }
}