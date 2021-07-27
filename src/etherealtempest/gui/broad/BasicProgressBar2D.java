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
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import enginetools.math.SpatialOperator;
import general.ui.GeometryPanel;
import java.util.function.Supplier;
import maps.data.MapTextures;

/**
 *
 * @author night
 */
public class BasicProgressBar2D extends ProgressIndicator {
    public enum ExpansionType {
        Horizontal(() -> { return MapTextures.GUI.ProgressBar; }),
        Vertical(() -> { return MapTextures.GUI.VerticalProgressBar; }),
        Both(() -> { return null; });
        
        private final Supplier<Texture> defaultTexture;
        private ExpansionType(Supplier<Texture> tex) {
            defaultTexture = tex;
        }
        
        public Texture getDefaultTexture() { return defaultTexture.get(); }
    }
    
    private final GeometryPanel bar;
    private final Material mat;
    
    private ExpansionType expansionType;
    private Vector2f percentEnd = new Vector2f(1.0f, 1.0f);
    
    public BasicProgressBar2D(Vector2f dimensions, Texture texture, AssetManager assetManager) {
        this(dimensions, texture, ExpansionType.Horizontal, ColorRGBA.White, 1f, 100, assetManager);
    }
    
    public BasicProgressBar2D(Vector2f dimensions, Texture texture, ColorRGBA color, AssetManager assetManager) {
        this(dimensions, texture, ExpansionType.Horizontal, color, 1f, 100, assetManager);
    }
    
    public BasicProgressBar2D(Vector2f dimensions, ColorRGBA color, float basePercent, AssetManager assetManager) {
        this(dimensions, null, ExpansionType.Horizontal, color, basePercent, 100, assetManager);
    }
    
    public BasicProgressBar2D(Vector2f dimensions, Texture texture, ColorRGBA color, float basePercent, int max, AssetManager assetManager) {
        this(dimensions, texture, ExpansionType.Horizontal, color, basePercent, max, assetManager);
    }
    
    public BasicProgressBar2D(Vector2f dimensions, Texture texture, ExpansionType type, ColorRGBA color, float basePercent, int max, AssetManager assetManager) {
        super(basePercent, max);
        bar = new GeometryPanel(dimensions);
        expansionType = type;
        
        Vector2f basePercentage = new Vector2f();
        switch(expansionType) {
            case Horizontal:
                basePercentage.set(basePercent, 1.0f);
                break;
            case Vertical:
                basePercentage.set(1.0f, basePercent);
                break;
            case Both:
                basePercentage.set(basePercent, basePercent);
                break;
        }
        
        mat = new Material(assetManager, "MatDefs/custom/ProgressFill2D.j3md");
        
        if (texture == null) {
            if (type != ExpansionType.Both) {
                mat.setTexture("ColorMap", type.getDefaultTexture());
            }
        } else {
            mat.setTexture("ColorMap", texture);
        }
        
        mat.setColor("Color", color);
        mat.setColor("OnlyChangeColor", ColorRGBA.White);
        mat.setColor("BackgroundColor", new ColorRGBA(75f / 255f, 75f / 255f, 75f / 255f, 1f));
        mat.setVector2("PercentFilled", basePercentage);
        mat.setVector2("PercentStart", new Vector2f(0.0f, 0.0f));
        mat.setVector2("PercentEnd", new Vector2f(1.0f, 1.0f));
        mat.setBoolean("UsesGradient", false);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        bar.setMaterial(mat);
    }
    
    public GeometryPanel getGeometryPanel() {
        return bar;
    }
    
    public SpatialOperator getAnchor() {
        return bar.getOperator();
    }
    
    public SpatialOperator getAnchor(float percentX, float percentY) {
        return bar.getOperator(percentX, percentY);
    }
    
    public Quad getQuad() {
        return bar.getQuad();
    }
    
    @Override
    public Material getMaterial() {
        return mat;
    }
    
    @Override
    public void setTexture(Texture tex) {
        mat.setTexture("ColorMap", tex);
    }
    
    @Override
    protected void updateVisuals() {
        Vector2f percentage = new Vector2f();
        switch(expansionType) {
            case Horizontal:
                percentage.set(percentFull, 1.0f);
                break;
            case Vertical:
                percentage.set(1.0f, percentFull);
                break;
            case Both:
                percentage.set(percentFull, percentFull);
                break;
        }
        
        mat.setVector2("PercentFilled", percentage);
    }
    
    public void setTextureRange(Vector2f percentStart, Vector2f percentEnd) {
        this.percentEnd = percentEnd;
        
        mat.setVector2("PercentStart", percentStart);
        mat.setVector2("PercentEnd", percentEnd);
    }
    
    public void setExpansionType(ExpansionType type) {
        expansionType = type;
        updateVisuals();
    }
    
    @Override
    public void setUsesAutoGradient(boolean uses) {
        mat.setBoolean("UsesGradient", uses);
    }
    
    @Override
    public void setOnlyChangeColor(ColorRGBA color) {
        mat.setColor("OnlyChangeColor", color);
    }
    
    @Override
    public void setBackgroundColor(ColorRGBA color) {
        mat.setColor("BackgroundColor", color);
    }
    
    @Override
    public void setBaseColor(ColorRGBA color) {
        mat.setColor("BaseColor", color);
    }
    
    @Override
    public void setColor(ColorRGBA color) {
        mat.setColor("Color", color);
    }
    
    public void setQueueBucket(Bucket queueBucket) {
        bar.getGeometry().setQueueBucket(queueBucket);
    }
}
