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
public class ShapeIndicator extends ValueIndicator {
    private final GeometryPanel shape; //it's rectangular, but the shape is on the texture
    private final Material mat;
    private final Vector2f equilibrium = new Vector2f(0, 0);
    
    public ShapeIndicator(String name, Vector2f xyDimensions, MaterialParamsProtocol params, AssetManager assetManager, Text2D text2D, float basePercent, int max) {
        super(name, text2D, basePercent, max);
        shape = new GeometryPanel(xyDimensions.x, xyDimensions.y, RenderQueue.Bucket.Gui);
        
        mat = new Material(assetManager, "MatDefs/custom/YFill.j3md");
        mat.getAdditionalRenderState().setDepthWrite(false);
        params.execute(mat);
        
        shape.setMaterial(mat);

        LayerComparator.setLayer(shape, 1);
        LayerComparator.setLayer(text, 5);
        
        node.attachChild(shape);
        node.attachChild(text);
        
        text.move(0, -text.getTextHeight(), 0);
    }
        
    @Override
    public void update(float tpf) {
        super.update(tpf);
    }

    @Override
    protected void updatePercentVisually() {
        mat.setFloat("PercentFilled", percentFull);
        //alignTextTo(equilibrium.x, equilibrium.y);
    }
    
    public Material getMaterial() {
        return mat;
    }
    
    public GeometryPanel getGeometryPanel() {
        return shape;
    }
    
    public SpatialOperator getShapeAnchor() {
        return shape.getOperator();
    }
    
    public void alignTextTo(float percentX, float percentY) {
        equilibrium.set(percentX, percentY);
        
        SpatialOperator shapeAnchor = shape.getOperator(percentX, percentY);
        textAnchor.getDimensions().set(text.getTextBounds());
        textAnchor.alignTo(shapeAnchor);
    }
}
