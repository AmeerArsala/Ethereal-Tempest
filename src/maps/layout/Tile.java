/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 *
 * @author night
 */
public class Tile {
    private final int pX, pY, elevation;
    private int tileWeight;
    public int[] tileBonuses;
    private String name;
    private Quad tquad = new Quad(40f, 40f);
    private Geometry tgeometry = new Geometry("Quad", tquad);
    
    public TerrainPatch tile;
    public boolean isMovSpace = false, isOccupied = false;
    
    private TangibleUnit occupier;
    
    public double rotX, rotY, rotZ;
    
    public Tile(String tileName, int posx, int posy, int elevation, int[] tileBonuses, int t_weight, AssetManager assetManager) {
        this.elevation = elevation;
        this.tileBonuses = tileBonuses;
        pX = posx;
        pY = posy;
        name = tileName;
        tileWeight = t_weight;
        
        //Material m = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
    }
    
    public Tile(int posx, int posy, int elevation, AssetManager assetManager) { //movement space
        this.elevation = elevation;
        pX = posx;
        pY = posy;
        isMovSpace = true;
    }
    
    public Quad getQuad() { return tquad; }
    public Geometry getGeometry() { return tgeometry; }
    
    public void setGeometry(Material Ma) {
        tgeometry.setMaterial(Ma);
    }
    
    public TangibleUnit getOccupier() { return occupier; }
    public void setOccupier(TangibleUnit u) { occupier = u; }
    
    public void resetOccupier() {
        occupier = null;
        isOccupied = false;
    }
    
    public float getHighestPointHeight() {
        float highest = 0;
        for (int x = 0; x <= 16; x++) {
            for (int z = 0; z <= 16; z++) {
                if (tile.getHeightmapHeight(x, z) > highest) {
                    highest = tile.getHeightmapHeight(x, z);
                }
            }
        }
        return highest;
    }
    
    public void changeTexture(Texture tex) {
        tile.getMaterial().setTexture("LightMap", tex);
        
        //tile.getMaterial().setTexture("Tex1", tex);
        
        /*if (tile.getMaterial().getAdditionalRenderState().getBlendMode() != RenderState.BlendMode.Alpha) {
            tile.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        }*/
    }
    
    public String getName() { return name; }
    public int getPosX() { return pX; }
    public int getPosY() { return pY; }
    public int getElevation() { return elevation; }
    public int getTileWeight() { return tileWeight; } 
    /* each unit will have a hidden Resolve stat
     * there movement reduction would be MOBILITY - (tileWeight - Resolve)
     * yes, this means if the unit has a higher Resolve than the tileWeight, they will move further
     * but if the unit has a lower Resolve than tileWeight, they won't move as far. This will be used to make forests penalize movement like regular FE
     * the Resolve stat will have baselines for infantry, cavalry, armored, etc.
     * the Resolve stat will NOT be factored in at all for units that are on flying mounts, they can traverse any terrain
     * but that also means they (fliers) don't get bonuses at all, even to mobility
     * like Biorhythms, Resolve won't be constant between some chapters
     * some units will get a bonus or penalty to the stat during them due to a story event
     * standard tileWeight = 10
     */
}
