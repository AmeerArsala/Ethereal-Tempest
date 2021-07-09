/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.simsilica.lemur.LayerComparator;
import enginetools.MaterialParamsProtocol;
import enginetools.math.SpatialOperator;
import general.ui.GeometryPanel;
import general.ui.text.Text2D;

/**
 *
 * @author night
 */
public class ShapeIndicator extends AnchoredIndicator {
    private final GeometryPanel shape; //it's rectangular, but the shape is on the texture
    private final Material mat;
    
    public ShapeIndicator(String name, Vector2f xyDimensions, MaterialParamsProtocol params, AssetManager assetManager, Text2D text2D, float basePercent, int max) {
        super(name, text2D, basePercent, max);
        shape = new GeometryPanel(xyDimensions.x, xyDimensions.y, RenderQueue.Bucket.Gui);
        
        mat = new Material(assetManager, "MatDefs/custom/ProgressFill.j3md");
        mat.getAdditionalRenderState().setDepthWrite(false);
        params.execute(mat);
        
        shape.setMaterial(mat);
        
        //LayerComparator.setLayer(shape, 0);
        //LayerComparator.setLayer(text, 1);
        
        node.attachChild(shape);
        node.attachChild(text);
    }
    
    /*
    @Override
    public void update(float tpf) {
        super.update(tpf);
    }
    */

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
    
    @Override
    public SpatialOperator getAnchor() {
        return shape.getOperator();
    }

    @Override
    public void alignTextToEquilibrium() {
        SpatialOperator shapeAnchor = shape.getOperator(equilibrium.x, equilibrium.y);
        textAnchor.getDimensions().set(text.getTextBounds());
        textAnchor.alignToLocally(shapeAnchor);
    }
}
