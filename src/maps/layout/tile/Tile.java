/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import fundamental.stats.Bonus;
import general.GeneralUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.Coords;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.TangibleUnit;
import maps.layout.occupant.TangibleUnit.UnitStatus;
import maps.layout.tile.TileOptionData.TileType;

/**
 *
 * @author night
 */
public class Tile {
    static final String MOVEMENT = "Textures/tiles/movsquare.png";
    static final String ATTACK = "Textures/tiles/atksquare.png";
    
    private Node node = null;
    
    private final float radiusForSquare = 8f;
    private final int pX, pY, elevation;
    
    private float[] heights = null;
    
    private TileData info = null;
    private boolean gottenAnnexed = false;
    
    private Geometry tgeometry;
    private Mesh patchMesh;
    
    private TangibleUnit occupier = null;
    private MapEntity structure = null;
    
    public boolean isOccupied = false;
    
    public Tile(int posx, int posy, int layer) { //for movement square
        pX = posx;
        pY = posy;
        elevation = layer;
    }
    
    public Tile(Material mat, int posx, int posy, int layer) {
        this(posx, posy, layer);
        node = new Node();
        initializeGeometry(mat);
    }
    
    public Tile(TileData info, AssetManager assetManager, int posx, int posy, int layer) {
        this(posx, posy, layer);
        this.info = info;
        
        node = new Node();
        info.getVisuals().finishAssimilation(node);
        
        //System.out.println(info.getVisuals().getGroundType());
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture(info.getVisuals().getTileTexturePath()));
        initializeGeometry(mat);
    }
    
    public String getName() { 
        return info != null ? info.getTileName() : "Ground"; 
    }
    
    public int getPosX() { return pX; }
    public int getPosY() { return pY; }
    public int getElevation() { return elevation; }
    
    public Coords coords() { return new Coords(pX, pY); }
    
    public TileData getTileData() { return info; }
    public TangibleUnit getOccupier() { return occupier; }
    public MapEntity getStructureOccupier() { return structure; }
    
    public Node getNode() { return node; }
    public Geometry getGeometry() { return tgeometry; }
    public Material getPatchMaterial() { return tgeometry.getMaterial(); }
    public Mesh getTileMesh() { return patchMesh; }
    
    public Vector3f getWorldTranslation() { return node != null ? node.getWorldTranslation() : tgeometry.getWorldTranslation(); }
    public Vector3f getLocalTranslation() { return node != null ? node.getLocalTranslation() : tgeometry.getLocalTranslation(); }
    
    public void setLocalTranslation(Vector3f translation) {
        if (node != null) {
            node.setLocalTranslation(translation);
            return;
        }
        
        tgeometry.setLocalTranslation(translation); 
    }
    
    public void setLocalTranslation(float x, float y, float z) {
        if (node != null) {
            node.setLocalTranslation(x, y, z);
            return;
        }
        
        tgeometry.setLocalTranslation(x, y, z);
    }
    
    public void setMaterial(Material mat) {
        tgeometry.setMaterial(mat);
    }
    
    public void setTileData(TileData TD) {
        info = TD;
    }
    
    private void initializeGeometry(Material mat) {
        patchMesh = createMesh();
        tgeometry = new Geometry("tile: (" + pX + ", " + pY + ")", patchMesh);
        tgeometry.setMaterial(mat);
        node.attachChild(tgeometry);
    }
    
    public void emulateOtherTileAsMoveSquare(AssetManager assetManager, Tile other) {
        patchMesh = other.getTileMesh().deepClone();
        tgeometry = new Geometry("tile: (" + pX + ", " + pY + ")", patchMesh);
        
        Material movsquare = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        movsquare.setTexture("ColorMap", assetManager.loadTexture("Textures/tiles/movsquare.png"));
        movsquare.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        movsquare.setColor("Color", new ColorRGBA(1, 1, 1, 0)); //we don't want to see this by itself
        
        tgeometry.setMaterial(movsquare);
        tgeometry.setQueueBucket(Bucket.Opaque);
    }
    
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
    
    public boolean hasBeenAnnexed() {
        if (info != null) {
            return info.getFunctionData().getFunctionType() == TileType.Annex ? gottenAnnexed : true;
        }
        
        return true;
    }
    
    public boolean hasBeenAnnexedBy(UnitStatus allegiance) {
        return hasBeenAnnexed() && info.getFunctionData().allegianceIsEligible(allegiance);
    }
    
    public boolean hasBeenAnnexedByEnemy() {
        return hasBeenAnnexed() && !info.getFunctionData().allegianceIsEligible(UnitStatus.Player) && !info.getFunctionData().allegianceIsEligible(UnitStatus.Ally); 
    }
    
    //TODO: move this to TileOptionData
    public void annex() {
        if (info.getFunctionData().getFunctionType() == TileType.Annex) {
            //do some visual stuff
            gottenAnnexed = true;
        }
    }
    
    public void setOccupier(TangibleUnit u) { 
        occupier = u;
        isOccupied = true;
    }
    
    public void setStructureOccupier(MapEntity ME) {
        structure = ME;
        isOccupied = true;
    }
    
    public void resetOccupier() {
        occupier = null;
        structure = null;
        isOccupied = false;
    }
}
