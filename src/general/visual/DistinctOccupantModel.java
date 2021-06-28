/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.jme3.scene.Node;

/**
 *
 * @author night
 */
public class DistinctOccupantModel extends DeserializedModel {
        
    public DistinctOccupantModel() { super(); }
        
    public DistinctOccupantModel(DualVector3F translation, DualVector3F angle, DualVector3F scale) {
        super(translation, angle, scale);
    }
        
    public void integrate(Node visualDataRootNode, Node structure) {
        Node model = structure.clone(true);
        Node modelRoot = getModelRootNode();
            
        applyTransformations(model);
        
        modelRoot.attachChild(model);
        visualDataRootNode.attachChild(modelRoot);
    }
}
