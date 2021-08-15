/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual.sprite;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import general.ui.GeometryPanel;

/**
 *
 * @author night
 */
public class Sprite extends GeometryPanel {
    public static final int FACING_LEFT = -1, FACING_RIGHT = 1;
    
    private final Material mat;
    private String spritesheetPath;
    protected int xFacing = FACING_RIGHT;
    
    public Sprite(Vector2f dimensions, AssetManager assetManager) {
        this(dimensions.x, dimensions.y, assetManager);
    }
    
    public Sprite(float width, float height, AssetManager assetManager) {
        super(width, height, RenderQueue.Bucket.Transparent);
        
        mat = new Material(assetManager, "MatDefs/custom/Spritesheet.j3md");
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        super.setMaterial(mat);
    }
    
    public String getTexturePath() { return spritesheetPath; } //use this if you want to compare whether textures are different
    
    public int getXFacing() { return xFacing; } 
    public boolean isFacingRight() { return xFacing == FACING_RIGHT; }
    public boolean isFacingLeft() { return xFacing == FACING_LEFT; }
    
    @Override
    public Material getMaterial() {
        return mat;
    }
    
    @Override
    public void setMaterial(Material material) {
        setNodeMaterial(material);
    }
    
    @Override
    public Vector2f getPositiveDirection2DVector() {
        return new Vector2f(xFacing, 1);
    }
    
    protected void onMirrorStateChanged(boolean willBeMirrored) {
        xFacing *= -1;
    }
    
    @Override
    public void setMirrored(boolean mirrored) {
        if (mirrored != isMirrored()) {
            onMirrorStateChanged(mirrored);
        }
        
        super.setMirrored(mirrored);
    }
    
    public void setXFacing(int facing) {
        xFacing = facing;
    }
    
    public void setSizeX(float x) {
        mat.setFloat("SizeX", x);
    }
    
    public void setSizeY(float y) {
        mat.setFloat("SizeY", y);
    }
    
    public void setSpritesheetTexture(Texture spritesheet) {
        spritesheet.setMagFilter(MagFilter.Nearest);
        mat.setTexture("ColorMap", spritesheet);
    }
    
    public void setSpritesheetTexture(String path, AssetManager assetManager) {
        setSpritesheetTexture(assetManager.loadTexture(path));
        spritesheetPath = path;
    }
    
    public void setSpritesheetPosition(float pos) {
        mat.setFloat("Position", pos);
    }
}
