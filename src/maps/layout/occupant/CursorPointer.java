/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import enginetools.MaterialCreator;

/**
 *
 * @author night
 */
public class CursorPointer {
    private final Node pointerParent = new Node();
    private final Node pointerParentParent = new Node();
    private final Node pointer;
    
    private Material pointerMat;
    
    private final float desiredPointerPosY;
    private final float desiredPointerRotY;
    
    public CursorPointer(AssetManager assetManager, MaterialCreator customMaterial, boolean defaultTransformation) {
        pointer = (Node)assetManager.loadModel("Models/General/pointer.gltf");
        pointerMat = customMaterial.createMaterial(assetManager);
        pointer.setMaterial(pointerMat);
        
        if (defaultTransformation) {
            pointer.move(0.5f * (1f / 0.85f), 0, -0.13f);
        
            pointerParent.scale(2.75f);
            pointerParent.move(5.2f, 0, 9.411f);
            pointerParentParent.move(0, 45, 0);
            pointerParentParent.scale(0.85f);
            
            Quaternion rot = new Quaternion();
            rot.fromAngles(0, 0, FastMath.PI / -3f);
            pointerParentParent.setLocalRotation(rot);
        }
        
        desiredPointerRotY = pointerParent.getLocalRotation().getY();
        desiredPointerPosY = pointerParent.getLocalTranslation().y;
        
        pointerParentParent.attachChild(pointerParent);
        pointerParent.attachChild(pointer);
    }
    
    public Material getMaterial() { return pointerMat; }
    
    public Node getMasterNode() { return pointerParentParent; }
    public Node getPointerNode() { return pointer; }
    public Node getPointerParentNode() { return pointerParent; }
    
    public float getDesiredPosY() { return desiredPointerPosY; }
    public float getDesiredRotY() { return desiredPointerRotY; }
    
    public void setMaterial(Material mat) {
        pointerMat = mat;
    }
    
    public void rotateIf(boolean condition, float factor) {
        Quaternion pointerRotation = new Quaternion();
        if (condition) {
            pointerRotation.fromAngles(0, factor, 0);
        } else {
            pointerRotation.fromAngles(0, desiredPointerRotY, 0);
        }
        
        pointerParent.setLocalRotation(pointerRotation);
        pointerParent.setLocalTranslation(pointerParent.getLocalTranslation().x, desiredPointerPosY + (2 * FastMath.sin(factor)), pointerParent.getLocalTranslation().z);
    }
}
