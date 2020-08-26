/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.BufferUtils;
import general.GeneralUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class Tile {
    private final float radiusForSquare = 8f;
    private final int pX, pY, elevation;
    
    //private float rotX, rotY, rotZ;
    
    private float[] heights = null;
    
    private TileData info;
    
    private String name;
    private Geometry tgeometry;
    private Material patchMaterial;
    private Mesh patchMesh;
    private TangibleUnit occupier;
    
    //public TerrainPatch tile;
    public boolean isOccupied = false;
    
    /*public Tile(String tileName, int posx, int posy, int elevation, int[] tileBonuses, int t_weight) {
        this.elevation = elevation;
        this.tileBonuses = tileBonuses;
        pX = posx;
        pY = posy;
        name = tileName;
        tileWeight = t_weight;
    }*/
    
    public Tile(String tileName, int posx, int posy, int elevation) {
        this.elevation = elevation;
        pX = posx;
        pY = posy;
        name = tileName;
    }
    
    public Tile(int posx, int posy, int elevation) { //movement space
        this.elevation = elevation;
        pX = posx;
        pY = posy;
    }
    
    public Geometry getGeometry() { return tgeometry; }
    
    public Vector3f getWorldTranslation() {
        //tile.getWorldTranslation()
        return tgeometry.getWorldTranslation();
    }
    
    public void setLocalTranslation(Vector3f translation) { tgeometry.setLocalTranslation(translation); }
    public void setLocalTranslation(float x, float y, float z) { tgeometry.setLocalTranslation(x, y, z); }
    
    public void initializeGeometry(Material mat, float maxHeight, int percentRandomHeights, int smoothness, boolean isSmooth, Spread spreadType) {
        //patchMesh = tile.getMesh().clone();
        this.isSmooth = isSmooth;
        patchMesh = createMesh(maxHeight, percentRandomHeights, smoothness, spreadType);
        patchMaterial = mat;
        tgeometry = new Geometry("tile: (" + pX + ", " + pY + ")", patchMesh);
        tgeometry.setMaterial(patchMaterial);
        //tgeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
    }
    
    public void initializeGeometry(Material mat) {
        patchMesh = createMesh();
        patchMaterial = mat;
        tgeometry = new Geometry("tile: (" + pX + ", " + pY + ")", patchMesh);
        tgeometry.setMaterial(patchMaterial);
    }
    
    public void emulateOtherTile(Material mat, Tile other) {
        patchMesh = other.getTileMesh().deepClone();
        patchMaterial = mat;
        tgeometry = new Geometry("tile: (" + pX + ", " + pY + ")", patchMesh);
        tgeometry.setMaterial(patchMaterial);
        tgeometry.setQueueBucket(RenderQueue.Bucket.Transparent);
    }
    
    public enum Spread {
        All,
        Random,
        Centered,
        Sinusoidal,
        Randomize
    }
    
    private boolean isSmooth;
    private final float defDeltaHeight = 0.005f;
    
    private Mesh createMesh() {
        Mesh mesh = new Mesh();
        
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textureCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        generateTile(vertices, textureCoords, indices);
        
        Vector3f[] verts = vertices.toArray(new Vector3f[vertices.size()]);
        Vector2f[] texCoords = textureCoords.toArray(new Vector2f[textureCoords.size()]);
        
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(verts));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(GeneralUtils.toIntArray(indices)));
        
        mesh.updateBound();
        
        return mesh;
    }
    
    private Mesh createMesh(float maxHeight, int percentRandomHeights, int smoothness, Spread spreadType) { //default smoothness = 1
        if (percentRandomHeights > 100) { percentRandomHeights = 100; }
        
        Mesh mesh = new Mesh();
        
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textureCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        generateTile(vertices, textureCoords, indices, maxHeight, percentRandomHeights, smoothness, spreadType);
        
        Vector3f[] verts = vertices.toArray(new Vector3f[vertices.size()]);
        Vector2f[] texCoords = textureCoords.toArray(new Vector2f[textureCoords.size()]);
        
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(verts));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(GeneralUtils.toIntArray(indices)));
        
        mesh.updateBound();
        
        return mesh;
    }
    
    private void generateTile(List<Vector3f> vertices, List<Vector2f> textureCoords, List<Integer> indices) {
        //frame the tile
        vertices.add(new Vector3f(0, 0, 0)); //bottom left (0)
        vertices.add(new Vector3f(2f * radiusForSquare, 0, 0)); //top left (1)
        vertices.add(new Vector3f(2f * radiusForSquare, 0, 2f * radiusForSquare)); //top right (2)
        vertices.add(new Vector3f(0, 0, 2f * radiusForSquare)); //bottom right (3)
        
        indices.addAll(Arrays.asList(1, 0, 3,  3, 2, 1));
        
        for (int n = 0; n < 4; n++) {
            Vector2f textureCoord1 = new Vector2f(0, 0);
            Vector2f textureCoord2 = new Vector2f(1, 0);
            Vector2f textureCoord3 = new Vector2f(0, 1);
            Vector2f textureCoord4 = new Vector2f(1, 1);
            
            textureCoords.add(textureCoord1);
            textureCoords.add(textureCoord3);
            textureCoords.add(textureCoord4);
            textureCoords.add(textureCoord2);
        }
    }
    
    private void generateTile(List<Vector3f> vertices, List<Vector2f> textureCoords, List<Integer> indices, float maxHeight, int percentRandomHeights, int smoothness, Spread spreadOfRandomness) {
        final int vertexAmount = (int)(25 * Math.pow(4, smoothness));
        
        //predetermine heights
        switch (spreadOfRandomness) {
            case All: //all cells will have a chance to have their heights randomized
                allSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
            case Random: //random cells will have a chance to have their heights randomized 
                randomSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
            case Centered: //only centered cells will have a chance to have their heights randomized
                centeredSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
            case Randomize: //random choice of the 3 above options
                randomizeSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
            default: //default will be Randomize
                randomizeSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
        }
        
        //frame the tile
        vertices.add(new Vector3f(0, 0, 0)); //bottom left (0)
        vertices.add(new Vector3f(2f * radiusForSquare, 0, 0)); //top left (1)
        vertices.add(new Vector3f(2f * radiusForSquare, 0, 2f * radiusForSquare)); //top right (2)
        vertices.add(new Vector3f(0, 0, 2f * radiusForSquare)); //bottom right (3)
        
        indices.addAll(Arrays.asList(2, 1, 3,  3, 1, 0));
        
        for (int n = 0; n < 4; n++) {
            Vector2f textureCoord1 = new Vector2f(0, 0);
            Vector2f textureCoord2 = new Vector2f(1, 0);
            Vector2f textureCoord3 = new Vector2f(0, 1);
            Vector2f textureCoord4 = new Vector2f(1, 1);
            
            textureCoords.add(textureCoord1);
            textureCoords.add(textureCoord2);
            textureCoords.add(textureCoord3);
            textureCoords.add(textureCoord4);
        }
        
        List<Vector2f> vertexCells = new ArrayList<>();
        
        float sideLength = (float)Math.sqrt(vertexAmount);
        float delta = (2f * radiusForSquare) / sideLength;
        int maxY = (int)((vertexAmount - 1) / sideLength);

        for (int i = 0; i < vertexAmount; i++) {
            // Texture coordinates
            Vector2f textureCoord1 = new Vector2f(0, 0);
            Vector2f textureCoord2 = new Vector2f(1, 0);
            Vector2f textureCoord3 = new Vector2f(0, 1);
            Vector2f textureCoord4 = new Vector2f(1, 1);
            
            float x = (int)(i % sideLength), y = (int)(i / sideLength);
            
            vertices.add(new Vector3f(y * delta, heights[i], x * delta)); //(y, height, x); x in z axis
            vertexCells.add(new Vector2f(x, y));
            
            textureCoords.add(textureCoord1);
            textureCoords.add(textureCoord2);
            textureCoords.add(textureCoord3);
            textureCoords.add(textureCoord4);
            
            /*if (i % 2 == 0 && y > 0) { //bottom paneling
                int index1 = (int)(i + 4 - sideLength); //bottom left
                int index2 = (int)(i + 5 - sideLength); //bottom right
                int index3 = i + 4; //top left
                int index4 = i + 5; //top right
                
                indices.addAll(Arrays.asList(index4, index3, index1,  index1, index2, index4));
            }*/
        }
        
        for (int i = 0; i < vertexAmount; i++) {
            if (i % 2 != 0) { //on every odd index
                List<Integer> options = calculateOptions(vertexCells, i, (int)sideLength, maxY);
                int closestIndexByHeight = GeneralUtils.closestIndex(heights[i], GeneralUtils.createFloatArrayFromElements(options, heights));
                float diffX = vertexCells.get(i).x - vertexCells.get(closestIndexByHeight).x, diffY = vertexCells.get(i).y - vertexCells.get(closestIndexByHeight).y;
                indices.add(i + 4);
                if (diffX < 0) { //it's on the left
                    if (diffY != 0) { //it's on the top or bottom left
                        if (diffY > 0) { //top left
                            indices.addAll(Arrays.asList(closestIndexByHeight + 4, i + 3));
                        } else { //bottom left
                            indices.addAll(Arrays.asList(i + 3, closestIndexByHeight + 4));
                        }
                    } else { //it's purely left
                        if (vertexCells.get(i).y >= maxY) { //top
                            indices.addAll(Arrays.asList(i + 3, (int)(i + 4 - sideLength)));
                        } else { //bottom or mid
                            indices.addAll(Arrays.asList((int)(i + 4 + sideLength), i + 3));
                        }
                    }
                } else if (diffX > 0) { //it's on the right
                    if (diffY != 0) { //it's on the top or bottom right
                        if (diffY > 0) { //top right
                            indices.addAll(Arrays.asList(i + 5, closestIndexByHeight + 4));
                        } else { //bottom right
                            indices.addAll(Arrays.asList(closestIndexByHeight + 4, i + 5));
                        }
                    } else { //it's purely right
                        if (vertexCells.get(i).y >= maxY) { //top
                            indices.addAll(Arrays.asList((int)(i + 4 - sideLength), i + 5));
                        } else { //bottom or mid
                            indices.addAll(Arrays.asList(i + 5, (int)(i + 4 - sideLength)));
                        }
                    }
                } else { //it's purely up or down
                    if (diffY > 0) { //it's purely up
                        if (vertexCells.get(i).x >= maxY) { //try left; max X = maxY
                            indices.addAll(Arrays.asList((int)(i + 4 + sideLength), i + 3));
                        } else { //try right
                            indices.addAll(Arrays.asList(i + 5, (int)(i + 4 + sideLength)));
                        }
                    } else { //it's purely down
                        if (vertexCells.get(i).x >= maxY) { //try right; max X = maxY
                            indices.addAll(Arrays.asList((int)(i + 4 - sideLength), (int)(i + 5 - sideLength)));
                        } else { //try left
                            indices.addAll(Arrays.asList((int)(i + 4 - sideLength), (int)(i + 3 - sideLength)));
                        }
                    }
                }
            }
        }
    }
    
    private List<Integer> calculateOptions(List<Vector2f> cells, int currentIndex, int sideLength, int maxValue) { //maxValue accounts for max X and max Y
        List<Integer> options = new ArrayList<>();
        Vector2f current = cells.get(currentIndex);
        if (current.x > 0) {
            options.add(currentIndex - 1); //left
            if (current.y < maxValue) {
                options.add(currentIndex + sideLength); //up
                options.add(currentIndex + sideLength - 1); //up left
            }
            if (current.y > 0) {
                options.add(currentIndex - sideLength); //down
                options.add(currentIndex - sideLength - 1); //down left

            }
        }
        
        if (current.x < maxValue) {
            options.add(currentIndex + 1); //right
            if (current.y < maxValue) {
                options.add(currentIndex + sideLength); //up
                options.add(currentIndex + sideLength + 1); //up right
            }
            if (current.y > 0) {
                options.add(currentIndex - sideLength); //down
                options.add(currentIndex - sideLength + 1); //down right
            }
        }
        
        return options;
    }
    
    private void allSpreadRandom(float maxHeight, int percentRandomHeights, final int vertexAmount) {
        heights = new float[vertexAmount];
        
        for (int i = 0; i < vertexAmount; i++) {
            int chance = (int)(Math.random() * percentRandomHeights) + 1;
            heights[i] = chance <= percentRandomHeights ? (float)(Math.random() * maxHeight) + 1 : 0;
            if (isSmooth && i > 0) {
                float deltaHeight;
                if (heights[i] > heights[i - 1]) {
                    deltaHeight = defDeltaHeight;
                } else if (heights[i] < heights[i - 1]) {
                    deltaHeight = defDeltaHeight * -1;
                } else {
                    deltaHeight = 0;
                }
                heights[i] = deltaHeight;
            }
        }
    }
    
    private void randomSpreadRandom(float maxHeight, int percentRandomHeights, final int vertexAmount) {
        heights = new float[vertexAmount];
        
        for (int i = 0; i < vertexAmount; i++) {
            int randomPercent = (int)(Math.random() * 100) + 1;
            int randomChance = (int)(Math.random() * randomPercent) + 1;
            
            if (randomChance <= randomPercent) {
                int chance = (int)(Math.random() * percentRandomHeights) + 1;
                heights[i] = chance <= percentRandomHeights ? (float)(Math.random() * maxHeight) + 1 : 0;
            } else { heights[i] = 0f; }
            
            if (isSmooth && i > 0) {
                float deltaHeight;
                if (heights[i] > heights[i - 1]) {
                    deltaHeight = defDeltaHeight;
                } else if (heights[i] < heights[i - 1]) {
                    deltaHeight = defDeltaHeight * -1;
                } else {
                    deltaHeight = 0;
                }
                heights[i] = deltaHeight;
            }
        }
    }
    
    private void centeredSpreadRandom(float maxHeight, int percentRandomHeights, final int vertexAmount) {
        heights = new float[vertexAmount];
        
        for (int i = 0; i < vertexAmount; i++) {
            float sideLength = (float)Math.sqrt(vertexAmount);
            int midpoint = (int)((sideLength / 2f) + (FastMath.pow(sideLength, 2) / 2f));
            
            int x = (int)(i % sideLength), y = (int)(i / sideLength);
            int midpointX = (int)(midpoint % sideLength), midpointY = (int)(midpoint / sideLength);
            
            if (FastMath.abs(midpointX - x) + FastMath.abs(midpointY - y) <= 5) {
                int chance = (int)(Math.random() * percentRandomHeights) + 1;
                heights[i] = chance <= percentRandomHeights ? (float)(Math.random() * maxHeight) + 1 : 0;
            } else {
                heights[i] = 0;
            }
            
            if (isSmooth && i > 0) {
                float deltaHeight;
                if (heights[i] > heights[i - 1]) {
                    deltaHeight = defDeltaHeight;
                } else if (heights[i] < heights[i - 1]) {
                    deltaHeight = defDeltaHeight * -1;
                } else {
                    deltaHeight = 0;
                }
                heights[i] = deltaHeight;
            }
        }
    }
    
    private void randomizeSpreadRandom(float maxHeight, int percentRandomHeights, final int vertexAmount) {
        int rand = (int)(Math.random() * 3);
        
        switch (rand) {
            case 0:
                allSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
            case 1:
                randomSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
            case 2:
                centeredSpreadRandom(maxHeight, percentRandomHeights, vertexAmount);
                break;
            default:
                break;
        }
    }
    
    public void setMaterial(Material mat) {
        patchMaterial = mat;
        tgeometry.setMaterial(patchMaterial);
        //tile.setMaterial(patchMaterial);
    }
    
    public Material getPatchMaterial() { return patchMaterial; }
    public Mesh getTileMesh() { return patchMesh; }
    
    public TangibleUnit getOccupier() { return occupier; }
    public void setOccupier(TangibleUnit u) { occupier = u; }
    
    public void resetOccupier() {
        occupier = null;
        isOccupied = false;
    }
    
    public float getHighestPointHeight() {
        float highest = 0;
        
        if (heights == null) { return 0; }
        
        for (int i = 0; i < heights.length; i++) {
            if (heights[i] > highest) {
                highest = heights[i];
            }
        }
        
        /*for (int x = 0; x <= 16; x++) {
            for (int z = 0; z <= 16; z++) {
                if (tile.getHeightmapHeight(x, z) > highest) {
                    highest = tile.getHeightmapHeight(x, z);
                }
            }
        }*/
        return highest;
    }
    
    public String getName() { return name; }
    public int getPosX() { return pX; }
    public int getPosY() { return pY; }
    public int getElevation() { return elevation; }
}
