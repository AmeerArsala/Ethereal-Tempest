/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.shader.VarType;
import com.simsilica.lemur.LayerComparator;
import fundamental.unit.UnitAllegiance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.data.MapTextures;
import maps.layout.MapBounds;
import maps.layout.occupant.MapEntity;
import maps.layout.occupant.character.TangibleUnit;
import maps.layout.tile.TileOptionData.TileType;
import maps.layout.tile.TileVisualData.GroundType;

/**
 *
 * @author night
 */
public class Tile extends TileFoundation {
    public static final float LENGTH = RADIUS_FOR_SQUARE * 2f;
    
    private final Node node;
    
    private TileData info = null;
    private boolean gottenAnnexed = false; //move this stuff to TileData
    
    private TangibleUnit occupier = null;
    private MapEntity structure = null;
    
    public boolean isOccupied = false;
    
    public Tile(int posX, int posY, int layer, List<TileData[][]> data, MapBounds bounds, AssetManager assetManager) { //for actual tile
        super(posX, posY, layer);
        node = new Node();
        info = data.get(layer)[posY][posX];
        info.getVisuals().finishAssimilation(node);
        initializeTexture(data, bounds, assetManager);
    }
    
    public String getName() { 
        return info != null ? info.getTileName() : "Ground"; 
    }
    
    public TileData getTileData() { return info; }
    public TangibleUnit getOccupier() { return occupier; }
    public MapEntity getStructureOccupier() { return structure; }
    
    public Node getNode() { return node; }
    public Vector3f getWorldTranslation() { return node.getWorldTranslation(); }
    public Vector3f getLocalTranslation() { return node.getLocalTranslation(); }
    
    @Override
    public void setLocalTranslation(Vector3f translation) {
        node.setLocalTranslation(translation); 
    }
    
    @Override
    public void setLocalTranslation(float x, float y, float z) {
        node.setLocalTranslation(x, y, z);
    }
    
    public void setTileData(TileData TD) {
        info = TD;
    }
    
    private void initializeTexture(List<TileData[][]> data, MapBounds bounds, AssetManager assetManager) {
        Material mat = new Material(assetManager, "MatDefs/custom/TileBlend.j3md");
        mat.setTextureParam("TileTexArray", VarType.TextureArray, MapTextures.Tiles.TileTextures);
        
        GroundType groundType = info.getVisuals().getGroundType();
        int index = groundType.getIndex();
        
        mat.setInt("CurrentIndex", index);
        mat.setInt("CurrentPriority", groundType.getBlendPriority());
        mat.setTexture("BlendMap", MapTextures.Tiles.OverflowBlendMap);
        //mat.setFloat("BlendAmplitude", 0.15f);
        
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        boolean hasTop = coords.getY() + 1 < bounds.getYLength(coords.getLayer());
        boolean hasBottom = coords.getY() - 1 >= bounds.getMinimumY(coords.getLayer());
        boolean hasRight = coords.getX() + 1 < bounds.getXLength(coords.getLayer());
        boolean hasLeft = coords.getX() - 1 >= bounds.getMinimumX(coords.getLayer());
        
        boolean allSame = true;
        if (hasTop) {
            GroundType topGroundType = coords.add(0, 1).getRowYColXfrom(data).getVisuals().getGroundType();
            int topIndex = topGroundType.getIndex();
            mat.setInt("TopIndex", topIndex);
            mat.setInt("TopPriority", topGroundType.getBlendPriority());
            
            if (topIndex != index) {
                allSame = false;
            }
        }
        
        if (hasBottom) {
            GroundType bottomGroundType = coords.add(0, -1).getRowYColXfrom(data).getVisuals().getGroundType();
            int bottomIndex = bottomGroundType.getIndex();
            mat.setInt("BottomIndex", bottomIndex);
            mat.setInt("BottomPriority", bottomGroundType.getBlendPriority());
            
            if (bottomIndex != index) {
                allSame = false;
            }
        }
        
        if (hasLeft) {
            GroundType leftGroundType = coords.add(-1, 0).getRowYColXfrom(data).getVisuals().getGroundType();
            int leftIndex = leftGroundType.getIndex();
            mat.setInt("LeftIndex", leftIndex);
            mat.setInt("LeftPriority", leftGroundType.getBlendPriority());
            
            if (leftIndex != index) {
                allSame = false;
            }
        }
        
        if (hasRight) {
            GroundType rightGroundType = coords.add(1, 0).getRowYColXfrom(data).getVisuals().getGroundType();
            int rightIndex = rightGroundType.getIndex();
            mat.setInt("RightIndex", rightIndex);
            mat.setInt("RightPriority", rightGroundType.getBlendPriority());
            
            if (rightIndex != index) {
                allSame = false;
            }
        }
        
        if (info.getVisuals().getIsWangTexture()) {
            info.getVisuals().setIsWangTexture(allSame);
        }
        
        patchMesh = createMesh();
        tgeometry = new Geometry("tile: " + coords.toString(), patchMesh);
        tgeometry.setMaterial(mat);
        
        node.attachChild(tgeometry);
        LayerComparator.setLayer(tgeometry, 1);
    }
    
    @Override
    protected void generateTile(List<Vector3f> vertices, List<Vector2f> textureCoords, List<Integer> indices) {
        //frame the tile
        vertices.add(new Vector3f(0, 0, 0));           //bottom left (0)
        vertices.add(new Vector3f(LENGTH, 0, 0));      //top left (1)
        vertices.add(new Vector3f(LENGTH, 0, LENGTH)); //top right (2)
        vertices.add(new Vector3f(0, 0, LENGTH));      //bottom right (3)
        
        indices.addAll(Arrays.asList(1, 0, 3,  3, 2, 1));
        
        for (int n = 0; n < 4; n++) {
            List<Vector2f> texCoords = new ArrayList<>();
            texCoords.addAll(
                Arrays.asList(
                    new Vector2f(0, 0),
                    new Vector2f(0, 1),
                    new Vector2f(1, 1),
                    new Vector2f(1, 0)
                )
            );
            
            if (info != null && info.getVisuals().getDeformTexture()) {
                for (int i = 0; i < 4; i++)  {
                    int randomIndex = (int)(Math.random() * texCoords.size());
                    textureCoords.add(texCoords.get(randomIndex));
                    texCoords.remove(randomIndex);
                }
            } else if (info != null && info.getVisuals().getIsWangTexture()) {
                int revolutions = (int)(4 * Math.random()) + 1; //counter clockwise 90 degree revolutions
                int revIndex = 4 - revolutions;
                for (int c = 0; c < revolutions; c++) {
                    textureCoords.add(texCoords.get(revIndex));
                    texCoords.remove(revIndex);
                }
                
                for (int i = 0; i < texCoords.size(); i++) { textureCoords.add(texCoords.get(i)); }
            } else {
                for (int i = 0; i < 4; i++) { textureCoords.add(texCoords.get(i)); }
            }
        }
    }
    
    public boolean hasBeenAnnexed() {
        if (info != null) {
            return info.getFunctionData().getFunctionType() == TileType.Annex ? gottenAnnexed : true;
        }
        
        return true;
    }
    
    public boolean hasBeenAnnexedBy(UnitAllegiance allegiance) {
        return hasBeenAnnexed() && info.getFunctionData().allegianceIsEligible(allegiance);
    }
    
    public boolean hasBeenAnnexedByEnemy() {
        return hasBeenAnnexed() && !info.getFunctionData().allegianceIsEligible(UnitAllegiance.Player) && !info.getFunctionData().allegianceIsEligible(UnitAllegiance.Ally); 
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
