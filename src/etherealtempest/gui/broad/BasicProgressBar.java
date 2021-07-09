/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Axis;
import general.ui.GeometryPanel;
import maps.data.MapTextures;

/**
 *
 * @author night
 */
public class BasicProgressBar extends RangedValue {
    private final GeometryPanel bar;
    private final Material mat;
    
    public BasicProgressBar(Vector2f dimensions, Texture texture, AssetManager assetManager) {
        this(dimensions, texture, false, ColorRGBA.White, 1f, 100, assetManager);
    }
    
    public BasicProgressBar(Vector2f dimensions, Texture texture, boolean usesYAxis, AssetManager assetManager) {
        this(dimensions, texture, usesYAxis, ColorRGBA.White, 1f, 100, assetManager);
    }
    
    public BasicProgressBar(Vector2f dimensions, Texture texture, ColorRGBA color, AssetManager assetManager) {
        this(dimensions, texture, false, color, 1f, 100, assetManager);
    }
    
    public BasicProgressBar(Vector2f dimensions, Texture texture, boolean usesYAxis, ColorRGBA color, AssetManager assetManager) {
        this(dimensions, texture, usesYAxis, color, 1f, 100, assetManager);
    }
    
    public BasicProgressBar(Vector2f dimensions, boolean usesYAxis, ColorRGBA color, float basePercent, AssetManager assetManager) {
        this(dimensions, usesYAxis ? MapTextures.GUI.VerticalProgressBar : MapTextures.GUI.ProgressBar, usesYAxis, color, basePercent, 100, assetManager);
    }
    
    public BasicProgressBar(Vector2f dimensions, ColorRGBA color, float basePercent, AssetManager assetManager) {
        this(dimensions, false, color, basePercent, assetManager);
    }
    
    public BasicProgressBar(Vector2f dimensions, Texture texture, ColorRGBA color, float basePercent, int max, AssetManager assetManager) {
        this(dimensions, texture, false, color, basePercent, max, assetManager);
    }
    
    public BasicProgressBar(Vector2f dimensions, Texture texture, boolean usesYAxis, ColorRGBA color, float basePercent, int max, AssetManager assetManager) {
        super(basePercent, max);
        bar = new GeometryPanel(dimensions.x, dimensions.y);
        
        mat = new Material(assetManager, "MatDefs/custom/ProgressFill.j3md");
        mat.setTexture("ColorMap", texture);
        mat.setColor("Color", color);
        mat.setColor("OnlyChangeColor", ColorRGBA.White);
        mat.setColor("BackgroundColor", new ColorRGBA(75f / 255f, 75f / 255f, 75f / 255f, 1f));
        mat.setFloat("PercentFilled", basePercent);
        mat.setBoolean("UsesYAxis", usesYAxis);
        mat.setBoolean("UsesGradient", false);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        bar.setMaterial(mat);
    }
    
    public Node getBarNode() {
        return bar;
    }
    
    public Quad getQuad() {
        return bar.getQuad();
    }
    
    public Material getMaterial() {
        return mat;
    }
    
    public void setTexture(Texture tex) {
        mat.setTexture("ColorMap", tex);
    }
    
    @Override
    protected void updateVisuals() {
        mat.setFloat("PercentFilled", percentFull);
    }
    
    public void setTextureRange(float percentStart, float percentEnd) {
        mat.setFloat("PercentStart", percentStart);
        mat.setFloat("PercentEnd", percentEnd);
    }
    
    public void setAxis(Axis axis) {
        if (axis != Axis.Z) {
            boolean usesY = axis == Axis.Y;
            mat.setBoolean("UsesYAxis", usesY);
        }
    }
    
    public void setUsesAutoGradient(boolean uses) {
        mat.setBoolean("UsesGradient", uses);
    }
    
    public void setOnlyChangeColor(ColorRGBA color) {
        mat.setColor("OnlyChangeColor", color);
    }
    
    public void setBackgroundColor(ColorRGBA color) {
        mat.setColor("BackgroundColor", color);
    }
    
    public void setBaseColor(ColorRGBA color) {
        mat.setColor("BaseColor", color);
    }
    
    public void setColor(ColorRGBA color) {
        mat.setColor("Color", color);
    }
}
