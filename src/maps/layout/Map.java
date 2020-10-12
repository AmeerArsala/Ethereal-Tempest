/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import maps.layout.tile.Path;
import maps.layout.tile.Tile;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
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
    
    //public TerrainQuad[] mapscene;
    //public TerrainQuad[] mov;
    
    //protected Integer i, n = 0;
    //private ArrayList<ArrayList<TerrainPatch>> realTiles, mobilityTiles;
    
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
    
    /*public Map(int tilesX, int tilesY, int layers, AssetManager assetManager, TerrainQuad[] mapscene, TerrainQuad[] mov, String terrainName) {
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.layers = layers;
        this.terrainName = terrainName;
        this.mapscene = mapscene;
        this.mov = mov;
        
        layerBounds = new Coords[layers][2]; //2 for (x, y)
        //default settings
        for (int l = 0; l < layers; l++) {
            layerBounds[l][0] = new Coords(0, tilesX); //x coordinate bounds [0, tilesX)
            layerBounds[l][1] = new Coords(0, tilesY); //y coordinate bounds [0, tilesY)
        }
        
        realTiles = new ArrayList<>();
        mobilityTiles = new ArrayList<>();
        for (int x = 0; x < layers; x++) {
            realTiles.add(new ArrayList<TerrainPatch>());
            mapscene[x].getAllTerrainPatches(realTiles.get(x));
            
            mobilityTiles.add(new ArrayList<TerrainPatch>());
            mov[x].getAllTerrainPatches(mobilityTiles.get(x));
            mov[x].setLocalTranslation(mov[x].getLocalTranslation().x, mov[x].getLocalTranslation().y - 499.5f, mov[x].getLocalTranslation().z);
        }
        
        createTiles(assetManager);
    }
    
    public Map(int layers, AssetManager assetManager, TerrainQuad[] mapscene, TerrainQuad[] mov) {
        this.layers = layers;
        this.mapscene = mapscene;
        this.mov = mov;
        
        tilesX = (int)FastMath.sqrt(FastMath.pow(4, getLayersOfChildren(mapscene[0])));
        tilesY = tilesX;
        //for (int i = 0; i < layers; i++) { setTilePositions(mapscene[i].getChildren(), mov[i].getChildren()); }
        realTiles = new ArrayList<>();
        mobilityTiles = new ArrayList<>();
        for (int x = 0; x < layers; x++) {
            realTiles.add(new ArrayList<TerrainPatch>());
            mapscene[x].getAllTerrainPatches(realTiles.get(x));
            
            mobilityTiles.add(new ArrayList<TerrainPatch>());
            mov[x].getAllTerrainPatches(mobilityTiles.get(x));
        }
        
        createTiles(assetManager);
    }*/
    
    public int getTilesX() { return tilesX; }
    public int getTilesY() { return tilesY; }
    public int getLayerCount() { return layers; }
    
    public String getName() { return terrainName; }
    
    public Coords[][] getBoundsForAllLayers() { return layerBounds; }
    
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
    
    public Node getMiscNode() {
        return extraMapStuff;
    }
    
    public Node getTileNode() {
        return tileNode;
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
                    Material movsquare = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); //material
                    movsquare.setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/movsquare.png")); //texture
                    movsquare.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); //alpha
                    movsquare.setColor("Color", new ColorRGBA(1, 1, 1, 0)); //we don't want to see this by itself
                    movSet[l][x][y].emulateOtherTile(movsquare, fullmap[l][x][y]);
                    movSet[l][x][y].setLocalTranslation(tilesY * y, 50 * l, tilesX * x);
                    extraMapStuff.attachChild(movSet[l][x][y].getGeometry());
                }
            }
        }
    }
    
    /*private void createTiles(AssetManager assetManager) {
        int[] bonuses = {0}; //TODO: change this later
        fullmap = new Tile[layers][tilesX][tilesY];
        movSet = fullmap;
        for (int l = 0; l < layers; l++) {
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    //printCoords(l);
                    fullmap[l][x][y] = new Tile("Ground", x, y, l, bonuses, 10);
                    //fullmap[l][x][y].tile = realTiles.get(l).get(n);
                    movSet[l][x][y] = new Tile(x, y, l);
                    //movSet[l][x][y].tile = mobilityTiles.get(l).get(n);
                }
            }
        }
        
        for (int l = 0; l < layers; l++) {
            n = realTiles.get(l).size() - 1;
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    int[] crd = Sequence.nextCoord(((TerrainQuad)masterQuadrant(realTiles.get(l).get(n))).getQuadrant());
                    //System.out.println("(" + crd[0] + ", " + crd[1] + "), Quadrant " + ((TerrainQuad)masterQuadrant(realTiles.get(l).get(n))).getQuadrant());
                    
                    /*
                    fullmap[l][crd[0]][crd[1]].tile = realTiles.get(l).get(n);
                    fullmap[l][crd[0]][crd[1]].tile.lockMesh();
                    
                    movSet[l][crd[0]][crd[1]].tile = mobilityTiles.get(l).get(n);
                    movSet[l][crd[0]][crd[1]].tile.setQueueBucket(RenderQueue.Bucket.Transparent);
                    movSet[l][crd[0]][crd[1]].tile.lockMesh();
                    
                    Material m2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    m2.setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/movsquare.png"));
                    m2.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    m2.setColor("Color", new ColorRGBA(1, 1, 1, 0));
                    movSet[l][crd[0]][crd[1]].initializeGeometry(m2); //, movSet[l].length, movSet[l][x].length
                    movSet[l][crd[0]][crd[1]].tile.setMaterial(m2.clone());
                    extraMapStuff.attachChild(movSet[l][crd[0]][crd[1]].getGeometry());
                    movSet[l][crd[0]][crd[1]].getGeometry().setLocalTranslation(extraMapStuff.worldToLocal(movSet[l][crd[0]][crd[1]].tile.getWorldTranslation(), null));
                    
                    n--;
                }
            }
        }
        
        /*for (int l = 0; l < layers; l++) {
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    try { System.out.println("layer = " + l + ", (" + x + ", " + y + "): " + fullmap[l][x][y].tile); }
                    catch (NullPointerException exc) { System.out.println("layer = " + l + ", (" + x + ", " + y + "): null"); }
                }
            }
        }
        
    }
    
    private void printCoords(int l) {
        try {
                        System.out.println( "(" +
                                  realTiles.get(l).get(n).getWorldTranslation().x + ", " 
                                  + realTiles.get(l).get(n).getWorldTranslation().y
                                  + ", " + realTiles.get(l).get(n).getWorldTranslation().z + ") "
                                  + "Quadrant: " + ((TerrainQuad)masterQuadrant(realTiles.get(l).get(n))).getQuadrant());
                        
        }
        catch (NullPointerException j) { System.out.println("null"); }
    }
    
    private Node masterQuadrant(TerrainPatch tp) {
        Spatial par = tp;
        Node prnt = new Node();
        for (int c = 0; c < (FastMath.log((tilesX * tilesY), 4) - 1); c++) {
            prnt = par.getParent();
            par = (Spatial)prnt;
        }
        return prnt;
    }
    
    private int getLayersOfChildren(Node ch) {
        try {
            if (ch.getChild(0) instanceof Node) {
                return 1 + getLayersOfChildren((Node)ch.getChild(0));
            }
        }
        catch (Exception e) { return 1; }
        
        return 1;
    }*/
    
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
