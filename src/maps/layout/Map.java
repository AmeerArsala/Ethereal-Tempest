/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;
import java.util.ArrayList;
import java.util.List;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.TerrainQuad;

/**
 *
 * @author night
 */
public class Map {
    private final int tilesX, tilesY, layers;
    
    public Tile[][][] fullmap; // a 3d array of layers, it's a 3d array due to multiple elevations 
    public Tile[][][] movSet; // for movement squares
    
    private String terrainName = "";
    
    public TerrainQuad[] mapscene;
    public TerrainQuad[] mov;
    
    protected Integer i, n = 0;
    private ArrayList<ArrayList<TerrainPatch>> realTiles, mobilityTiles;
    
    public Map(int tilesX, int tilesY, int layers, AssetManager assetManager, TerrainQuad[] mapscene, TerrainQuad[] mov, String terrainName) {
        this.tilesX = tilesX;
        this.tilesY = tilesY;
        this.layers = layers;
        this.terrainName = terrainName;
        this.mapscene = mapscene;
        this.mov = mov;
        
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
    }
    
    public int getTilesX() { return tilesX; }
    public int getTilesY() { return tilesY; }
    public int getLayerCount() { return layers; }
    
    private void createTiles(AssetManager assetManager) {
        int[] bonuses = {0};
        fullmap = new Tile[layers][tilesX][tilesY];
        movSet = fullmap;
        for (int l = 0; l < layers; l++) {
            for (int x = 0; x < tilesX; x++) {
                for (int y = 0; y < tilesY; y++) {
                    printCoords(l);
                    fullmap[l][x][y] = new Tile("Blank", x, y, l, bonuses, 10, assetManager);
                    //fullmap[l][x][y].tile = realTiles.get(l).get(n);
                    movSet[l][x][y] = new Tile(x, y, l, assetManager);
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
                    
                    fullmap[l][crd[0]][crd[1]].tile = realTiles.get(l).get(n);
                    
                    Material m2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    movSet[l][crd[0]][crd[1]].tile = mobilityTiles.get(l).get(n);
                    movSet[l][crd[0]][crd[1]].tile.setMaterial(m2);
                    movSet[l][crd[0]][crd[1]].tile.setQueueBucket(RenderQueue.Bucket.Transparent);
                    
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
        }*/
        
    }
    
    private void printCoords(int l) {
        /*try {
                        System.out.println( "(" +
                                  realTiles.get(l).get(n).getWorldTranslation().x + ", " 
                                  + realTiles.get(l).get(n).getWorldTranslation().y
                                  + ", " + realTiles.get(l).get(n).getWorldTranslation().z + ") "
                                  + "Quadrant: " + ((TerrainQuad)masterQuadrant(realTiles.get(l).get(n))).getQuadrant());
                        
        }
        catch (NullPointerException j) { System.out.println("null"); }*/ 
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
    
    public ArrayList<Tile> generatePath(int startX, int startY, int endX, int endY, int layer) {
        ArrayList<Tile> path = new ArrayList<>();
        int ablespaces = Math.abs(endY - startY) + Math.abs(endX - startX);
        pathsquare(path, layer, startX, startY, ablespaces, endX, endY);
        return path;
    }
    
    private void pathsquare(ArrayList<Tile> path, int layer, int currentX, int currentY, int ablespaces, int destX, int destY) {
        if (ablespaces == 0) {
            return;
        }
        
        int addX = 0, addY = 0;
        if (currentX - destX < 0) { addX = 1; } else if (currentX - destX > 0) { addX = -1; }
        if (currentY - destY < 0) { addY = 1; } else if (currentY - destY > 0) { addY = -1; }
        
        //start with horizontal, then vertical
        if (currentX != destX && !fullmap[layer][currentX + addX][currentY].isOccupied) {
            path.add(fullmap[layer][currentX + addX][currentY]);
            pathsquare(path, layer, currentX + addX, currentY, ablespaces - 1, destX, destY);
        } else if (currentY != destY && !fullmap[layer][currentX][currentY + addY].isOccupied) {
            path.add(fullmap[layer][currentX][currentY + addY]);
            pathsquare(path, layer, currentX, currentY + addY, ablespaces - 1, destX, destY);
        }
    }
    
    //instead of directly moving to a square, a unit will only move horizontally and vertically via this algorithm that calls itself multiple times
    //until ablespaces = 0
    private void pathmaker(ArrayList<Tile> path, int startX, int endX, int startY, int endY, int layer) {
        int ablespaces = Math.abs(endY - startY) + Math.abs(endX - startX);
        int used = ablespaces;
        for (int x = startX; x <= endX; x++) {
            if (!fullmap[layer][x][startY].isOccupied) {
                path.add(fullmap[layer][x][startY]);
                used--;
            }
        }
    }
    

}

class Sequence {
    public static int[] xsequence = new int[4], ysequence = new int[4];
    
    private static int incXa = 0, incXb = 0, incYa = 0, 
            incYb = 0, incXasq3 = 0, incXbsq3 = 0, incYasq23 = 0, incYbsq23 = 0, incXasq2 = 0, incXbsq2 = 0;
    private static int incXa2 = 0, incXb2 = 0, incYa2 = 0, incYb2 = 0, incXa2sq3 = 0, incXb2sq3 = 0,
            incXa2sq2 = 0, incXb2sq2 = 0, incYa2sq23 = 0, incYb2sq23 = 0;
    private static int saveIncXa = 0, saveIncXb = 0, saveIncYa = 0, saveIncYb = 0, 
            saveIncXasq3 = 0, saveIncXbsq3 = 0, saveIncYasq23 = 0, saveIncYbsq23 = 0, saveIncXasq2 = 0, saveIncXbsq2 = 0;
    private static int incXq4a = 0, incXq4b = 0, incXq4asq2 = 0, incXq4bsq2 = 0, incXq4asq3 = 0, incXq4bsq3 = 0,
            incYq4a = 0, incYq4b = 0, incYq4asq23 = 0, incYq4bsq23 = 0;
    
    private static double gen = 0, gen2 = 0, gensave = 0, genq4 = 0;
    
    private static int amountCalled = 0;
    
    public static int[] nextCoord(int quadrant) {
        if ((amountCalled + 4) % 4 == 0) {
            switch (quadrant) {
            case 2:
                generateSavedX();
                generateY2();
                break;
            case 3:
                generateX2();
                generateSavedY();
                break;
            case 4:
                generateXQ4();
                generateYQ4();
                break;
            default:
                generateX();
                generateY();
                break;
            }
        }
        
        int[] cds = {xsequence[(amountCalled + 4) % 4], ysequence[(amountCalled + 4) % 4]};
        amountCalled++;
        return cds;
    }
    
    private static int getSubquadrant(int amt) {
        //int range = (amt + 32) % 32;
        int ra = amt, st = 1;
        boolean done = false;
        while(!done) {
            if (ra >= 4) {
                ra -= 4;
                st++;
            } else { done = true; }
        }
        
        return st;
    }
    
    //generate a new list every time it is called
    private static void generateX() {
        
        int subquadrant = getSubquadrant((int)gen * 2);
        //if (quadrant == 4) { subquadrant = getSubquadrant((int)gen); }
        
        switch (subquadrant) {
            case 2:
                if (((int)gen) % 2 == 0) {
                    //System.out.println("Triggered generateX(); incXa = " + incXa);
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXasq2);
                    }
                    incXasq2 += 2;
                } else {
                    //System.out.println("ElseTriggered generateX(); incXb = " + incXb);
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXbsq2);
                    }
                    incXbsq2 += 2;
                }
                break;
            case 3:
                if (((int)gen) % 2 == 0) {
                    //System.out.println("Triggered generateX(); incXa = " + incXa);
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXasq3);
                    }
                    incXasq3 += 2;
                } else {
                    //System.out.println("ElseTriggered generateX(); incXb = " + incXb);
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXbsq3);
                    }
                    incXbsq3 += 2;
                }
                break;
            default:
                //subquadrants 1 and 4
                if (((int)gen) % 2 == 0) {
                    //System.out.println("Triggered generateX(); incXa = " + incXa);
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXa);
                    }
                    incXa += 2;
                    saveIncXa = incXa;
                    incXq4a = incXa;
                    incXasq2 = incXa;
                } else {
                    //System.out.println("ElseTriggered generateX(); incXb = " + incXb);
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXb);
                    }
                    incXb += 2;
                    saveIncXb = incXb;
                    incXq4b = incXb;
                    incXbsq2 = incXb;
                }
                break;
        }
        gen += 0.5;
    }
    
    private static void generateY() {
        /*if (quadrant == 1 && (incYa == 8 && incYb == 8) && (incXa == 8 && incXb == 8)) {
            return;
        } else if (quadrant == 4 && incYa == 16 && incYb == 16) { return; }*/
        double ygen = (gen - 0.5);
        int subquadrant = getSubquadrant((int)(ygen * 2));
        //if (quadrant == 4) { subquadrant = getSubquadrant((int)ygen); }
        //boolean repeated = (gen == (double)((int)gen));
        
        switch (subquadrant) {
            case 2:
            case 3:
                if (incYasq23 <= incYbsq23) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYasq23);
                    }
                    incYasq23 += 2;
                    saveIncYasq23 = incYasq23;
                    //incYq4asq23 = incYasq23;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYbsq23);
                    }
                    incYbsq23 += 2;
                    saveIncYbsq23 = incYbsq23;
                    //incYq4bsq23 = incYbsq23;
                }
                break;
            default:
                if (incYa <= incYb) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYa);
                    }
                    incYa += 2;
                    saveIncYa = incYa;
                    incYq4a = incYa;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYb);
                    }
                    incYb += 2;
                    saveIncYb = incYb;
                    incYq4b = incYb;
                }
                break;
        }
        
    }
    
    private static void generateSavedX() {
        /*if ((saveIncXa == 16 && saveIncXb == 16) && (incYa2 == 8 && incYb2 == 8)) {
            return;
        }*/
        
        if (gensave == 0.0) {
            saveIncXasq3 = saveIncXa;
            saveIncXbsq3 = saveIncXb;
            saveIncXasq2 = saveIncXa;
            saveIncXbsq2 = saveIncXb;
        }
        
        int subquadrant = getSubquadrant((int)gensave * 2);
        
        switch (subquadrant) {
            case 2:
                if (((int)gensave) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, saveIncXasq2);
                    }
                    saveIncXasq2 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, saveIncXbsq2);
                    }
                    saveIncXbsq2 += 2;
                }
                break;
            case 3:
                if (((int)gensave) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, saveIncXasq3);
                    }
                    saveIncXasq3 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, saveIncXbsq3);
                    }
                    saveIncXbsq3 += 2;
                }
                break;
            default:
                if (((int)gensave) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, saveIncXa);
                    }
                    saveIncXa += 2;
                    saveIncXasq2 = saveIncXa;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, saveIncXb);
                    }
                    saveIncXb += 2;
                    saveIncXbsq2 = saveIncXb;
                }
                break;
        }
        
        gensave += 0.5;
    }
    
    private static void generateY2() {
        /*if ((incYa2 == 8 && incYb2 == 8) && (saveIncXa == 16 && saveIncXb == 16)) {
            return;
        }*/
        double ygen = (gensave - 0.5);
        int subquadrant = getSubquadrant((int)(ygen * 2));
        //boolean repeated = (gensave == (double)((int)gensave));
        
        switch (subquadrant) {
            case 2:
            case 3:
                if (incYa2sq23 <= incYb2sq23) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYa2sq23);
                    }
                    incYa2sq23 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYb2sq23);
                    }
                    incYb2sq23 += 2;
                }
                break;
            default:
                if (incYa2 <= incYb2) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYa2);
                    }
                    incYa2 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYb2);
                    }
                    incYb2 += 2;
                }
                break;
        }
        
    }
    
    private static void generateX2() {
        
        if (gen2 == 0.0) {
            saveIncYasq23 = saveIncYa;
            saveIncYbsq23 = saveIncYb;
        }
        
        int subquadrant = getSubquadrant((int)gen2 * 2);
        
        switch (subquadrant) {
            case 2:
                if (((int)gen2) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXa2sq2);
                    }
                    incXa2sq2 += 2;
                    //incXa = incXa2sq2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXb2sq2);
                    }
                    incXb2sq2 += 2;
                    //incXb = incXb2sq2;
                }
                break;
            case 3:
                if (((int)gen2) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXa2sq3);
                    }
                    incXa2sq3 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXb2sq3);
                    }
                    incXb2sq3 += 2;
                }
                break;
            
            default:
                if (((int)gen2) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXa2);
                    }
                    incXa2 += 2;
                    incXa2sq2 = incXa2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXb2);
                    }
                    incXb2 += 2;
                    incXb2sq2 = incXb2;
                } 
                break;
        }
        
        gen2 += 0.5;
        
    }
    
    private static void generateSavedY() {
        double ygen = (gen2 - 0.5);
        int subquadrant = getSubquadrant((int)(ygen * 2));     
        //boolean repeated = (gen2 == (double)((int)gen2));
        
        switch (subquadrant) {
            case 2:
            case 3:
                if (saveIncYasq23 <= saveIncYbsq23) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, saveIncYasq23);
                    }
                    saveIncYasq23 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, saveIncYbsq23);
                    }
                    saveIncYbsq23 += 2;
                }
                break;
            default:
                if (saveIncYa <= saveIncYb) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, saveIncYa);
                    }
                    saveIncYa += 2;
                    incYa = saveIncYa;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, saveIncYb);
                    }
                    saveIncYb += 2;
                    incYb = saveIncYb;
                }
                break;
        }
    }
    
    private static void generateXQ4() {
        if (genq4 == 0.0) {
            incXq4asq2 = incXq4a;
            incXq4bsq2 = incXq4b;
            incXq4asq3 = incXq4a;
            incXq4bsq3 = incXq4b;
            
            incYq4asq23 = incYq4a;
            incYq4bsq23 = incYq4b;
        }
        
        int subquadrant = getSubquadrant((int)(genq4 * 2));
        
        switch (subquadrant) {
            case 2:
                if (((int)genq4) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXq4asq2);
                    }
                    incXq4asq2 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXq4bsq2);
                    }
                    incXq4bsq2 += 2;
                }
                break;
            case 3:
                if (((int)genq4) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXq4asq3);
                    }
                    incXq4asq3 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXq4bsq3);
                    }
                    incXq4bsq3 += 2;
                }
                break;
            default:
                if (((int)genq4) % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXq4a);
                    }
                    incXq4a += 2;
                    incXq4asq2 = incXq4a;
                } else {
                    for (int i = 0; i < 4; i++) {
                        xsequence[i] = defXeq(i, incXq4b);
                    }
                    incXq4b += 2;
                    incXq4bsq2 = incXq4b;
                }
                break;
        }
        
        genq4 += 0.5;
    }
    
    private static void generateYQ4() {
        double ygen = (genq4 - 0.5);
        int subquadrant = getSubquadrant((int)(ygen * 2));
        
        switch (subquadrant) {
            case 2:
            case 3:
                if (incYq4asq23 <= incYq4bsq23) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYq4asq23);
                    }
                    incYq4asq23 += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYq4bsq23);
                    }
                    incYq4bsq23 += 2;
                }
                break;
            default:
                if (incYq4a <= incYq4b) {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYq4a);
                    }
                    incYq4a += 2;
                } else {
                    for (int i = 0; i < 4; i++) {
                        ysequence[i] = defYeq(i, incYq4b);
                    }
                    incYq4b += 2;
                }
                break;
        }
        
    }

    private static int defXeq(int index, int inc) {
        return (int)((-0.5 * FastMath.cos(FastMath.PI * index)) + 0.5 + inc); //((0.5 * inc) * FastMath.sin(FastMath.PI / 2)) + (0.5 * inc) 
    }
    
    private static int defYeq(int index, int inc) { return (int)((1.1 * FastMath.sin(0.63f * index)) + inc); }

    
    
}
