/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;

/**
 *
 * @author night
 */
public class GeometryUIElement extends GeometryPanel {
    private Material mat;
    private Padding padding;
    private Vector3f paddingDiff = new Vector3f(0f, 0f, 0f);
    
    public GeometryUIElement(float width, float height, Material mat, Padding padding) {
        super(width, height, Bucket.Gui);
        this.mat = mat;
        this.padding = padding;
        initialize();
    }
    
    private void initialize() {
        setMaterial(mat);
        applyPadding();
    }
    
    private void applyPadding() {
        move(paddingDiff.multLocal(-1f)); // undo previous padding
        paddingDiff = new Vector3f(padding.left - padding.right, padding.bottom - padding.top, 0f); // create new vector
        move(paddingDiff); // apply new vector
    }
    
    @Override
    public Material getMaterial() { return mat; }
    
    public Padding getPadding() { return padding; }
    
    @Override
    public void setMaterial(Material material) {
        mat = material;
        super.setMaterial(mat);
    }
    
    public void setPadding(Padding padding) {
        this.padding = padding;
        applyPadding();
    }
    
    @Override
    public void setLocalTranslation(Vector3f localTranslation) {
        super.setLocalTranslation(localTranslation.addLocal(paddingDiff));
    }
    
    @Override
    public void setLocalTranslation(float x, float y, float z) {
        super.setLocalTranslation(paddingDiff.add(x, y, z));
    }
}
