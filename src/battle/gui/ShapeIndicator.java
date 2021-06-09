/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import enginetools.MaterialParamsProtocol;
import etherealtempest.gui.ValueIndicator;
import general.ui.GeometryPanel;
import general.ui.text.Text2D;

/**
 *
 * @author night
 */
public class ShapeIndicator extends ValueIndicator {
    private final GeometryPanel shape; //it's a square, but the shape is on the texture
    private final Material mat;
    
    public ShapeIndicator(Vector2f xyDimensions, MaterialParamsProtocol params, AssetManager assetManager, Text2D text, float basePercent, int max) {
        super(text, basePercent, max);
        shape = new GeometryPanel(xyDimensions.x, xyDimensions.y);
        
        mat = new Material(assetManager, "MatDefs/custom/YFill.j3md");
        params.execute(mat);
        
        shape.setMaterial(mat);
    }
        
    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    protected void updatePercentVisually() {
        mat.setFloat("PercentFilled", percentFull);
    }
    
    public Material getMaterial() {
        return mat;
    }
    
    public GeometryPanel getGeometryPanel() {
        return shape;
    }
}
