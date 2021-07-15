/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import maps.data.MapData;
import maps.layout.tile.Tile;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import general.math.DomainBox;
import general.math.FloatPair;
import java.util.ArrayList;
import java.util.List;
import general.visual.DeserializedParticleEffect;
import java.util.LinkedList;
import maps.layout.tile.move.Path;
import maps.layout.tile.TileData;
import maps.layout.tile.TileFoundation;
import maps.layout.tile.move.MoveSquare;

/**
 *
 * @author night
 */
public class MapLevel {
    public static final float LAYER_Y_DEVIATION = 50f; //maybe rework this later
    
    private final int tilesX, tilesY, layers;
    private final DomainBox[] layerBounds; // bounds go: [a, b)
    
    private MapBounds bounds;
    private MapData info;
    
    private final String terrainName;
    private final Node tileNode = new Node("tile node for tiles and terrain");
    private final Node extraMapStuff = new Node("extra map things");
    
    private Tile[][][] fullmap;      // a 3d array of layers, it's a 3d array due to multiple elevations 
    private MoveSquare[][][] movSet; // for movement squares
    
    //basic needs
    private MapLevel(String terrainName, int tilesX, int tilesY, int layers) {
        this.terrainName = terrainName;
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.layers = layers;
        
        layerBounds = new DomainBox[layers];
    }
    
    //most often used
    public MapLevel(String terrainName, int tilesX, int tilesY, int layers, MapData data, AssetManager assetManager) {
        this(terrainName, tilesX, tilesY, layers);
        info = data;
        
        //default settings/bounds
        for (int l = 0; l < layers; l++) {
            layerBounds[l] = new DomainBox(
                new FloatPair(0, tilesX), //x coordinate bounds [0, tilesX)
                new FloatPair(0, tilesY)  //y coordinate bounds [0, tilesY)
            );
        }
        
        setBounds();
        generateTiles(assetManager);
    }
    
    //with bounds included in constructor
    public MapLevel(String terrainName, int tilesX, int tilesY, int layers, DomainBox[] boundsForEachLayer, MapData data, AssetManager assetManager) {
        this(terrainName, tilesX, tilesY, layers);
        info = data;
        System.arraycopy(boundsForEachLayer, 0, layerBounds, 0, layers); 
        
        setBounds();
        generateTiles(assetManager);
    }
    
    public String getName() { return terrainName; }
    public MapData getMapData() { return info; }
    
    public int getTilesX() { return tilesX; }
    public int getTilesY() { return tilesY; }
    public int getLayerCount() { return layers; }
    
    public Node getTileNode() {
        return tileNode;
    }
    
    public Node getMiscNode() {
        return extraMapStuff;
    }
    
    private void generateTiles(AssetManager assetManager) {
        List<TileData[][]> tileInfo = info.interpret(assetManager);

        //make new instances
        fullmap = new Tile[layers][tilesX][tilesY];
        movSet = new MoveSquare[layers][tilesX][tilesY];
        for (int l = 0; l < layers; l++) {
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    //TileData tile = tileInfo.get(l)[y][x];
                    
                    //for actual tiles
                    fullmap[l][x][y] = new Tile(x, y, l, tileInfo, bounds, assetManager);
                    fullmap[l][x][y].setLocalTranslation(Tile.SIDE_LENGTH * y, LAYER_Y_DEVIATION * l, Tile.SIDE_LENGTH * x);
                    tileNode.attachChild(fullmap[l][x][y].getNode());
                    
                    //for move squares
                    movSet[l][x][y] = new MoveSquare(x, y, l, assetManager);
                    movSet[l][x][y].setLocalTranslation(Tile.SIDE_LENGTH * y, LAYER_Y_DEVIATION * l, Tile.SIDE_LENGTH * x);
                    extraMapStuff.attachChild(movSet[l][x][y].getGeometry());
                }
            }
        }
        
        //tileNode.addLight(new AmbientLight());
        //extraMapStuff.addLight(new AmbientLight());
    }
    
    public void generateWeather(AssetManager assetManager, Node cursorNode) {
        DeserializedParticleEffect[] effects = info.retrieveMapEffects(assetManager);
        if (effects != null) {
            for (DeserializedParticleEffect effect : effects) {
                cursorNode.attachChild(effect.getModelRootNode());
            }
        }
    }
    
    public Tile getTileAt(int x, int y, int layer) {
        return fullmap[layer][x][y];
    }
    
    public Tile getTileAt(MapCoords coords) {
        return coords.getRowXColYfrom(fullmap);
    }
    
    public MoveSquare getMovSquareAt(int x, int y, int layer) {
        return movSet[layer][x][y];
    }
    
    public MoveSquare getMovSquareAt(MapCoords coords) {
        return coords.getRowXColYfrom(movSet);
    }
    
    public Tile[][] getLayerTiles(int layer) {
        return fullmap[layer];
    }
    
    public MoveSquare[][] getLayerMovSquares(int layer) {
        return movSet[layer];
    }
    
    public MapBounds getBounds() { 
        return bounds; 
    }
    
    private void setBounds() {
        bounds = new MapBounds() {
            @Override
            public DomainBox[] getBoundsForAllLayers() { 
                return layerBounds; 
            }
            
            @Override
            public int getXLength(int layer) {
                return (int)layerBounds[layer].getDomainX().b;
            }
            
            @Override
            public int getMinimumX(int layer) {
                return (int)layerBounds[layer].getDomainX().a;
            }
            
            @Override
            public int getYLength(int layer) {
                return (int)layerBounds[layer].getDomainY().b;
            }
            
            @Override
            public int getMinimumY(int layer) {
                return (int)layerBounds[layer].getDomainY().a;
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
            public boolean isWithinBounds(MapCoords test) {
                return test.getCoords().x() >= getMinimumX(test.getLayer()) && test.getCoords().x() < getXLength(test.getLayer()) 
                       &&
                       test.getCoords().y() >= getMinimumY(test.getLayer()) && test.getCoords().y() < getYLength(test.getLayer());
            }
        };
    }
    
    public int getXLength(int layer) { return bounds.getXLength(layer); }
    public int getMinimumX(int layer) { return bounds.getMinimumX(layer); }
    public int getYLength(int layer) { return bounds.getYLength(layer); }
    public int getMinimumY(int layer) { return bounds.getMinimumY(layer); }
    public boolean isWithinBounds(MapCoords test) { return bounds.isWithinBounds(test); }
    
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
    
    public int localPeekPathSize(MapCoords start, MapCoords end) {
        return new Path(start, end, 1000000, this).getPathSize();
    }
    
    public static int peekPathSize(MapCoords start, MapCoords end) {
        return new Path(start, end, 1000000).getPathSize();
    }
    
    public static LinkedList<Tile> reorderTheseTilesByClosestTo(MapCoords point, List<Tile> tiles) {
        LinkedList<Tile> reordered = new LinkedList<>();
        
        List<Integer> alreadyAddedIndexes = new ArrayList<>();
        for (int index = 0; index < tiles.size(); index++) {
            if (tiles.get(index).getPos().equals(point)) { //if the tileList already has the targeted tile
                reordered.addFirst(tiles.get(index));
                alreadyAddedIndexes.add(index);
            } else {
                //add closest tile
                int shortestIndex = -1;
                for (int t = 0; t < tiles.size(); t++) {
                    if (!alreadyAddedIndexes.contains(t)) {
                        if (shortestIndex == -1 || (peekPathSize(tiles.get(t).getPos(), point) < peekPathSize(tiles.get(shortestIndex).getPos(), point))) {
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
}