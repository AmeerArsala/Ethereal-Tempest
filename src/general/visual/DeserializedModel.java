/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;

/**
 *
 * @author night
 */
public class DeserializedModel { //this class is for gson stuff
    public Node modelRoot; //the parent of actual thing
        
    //gson this area
    protected Float x = 0f, y = 0f, z = 0f; // relative/local; this would apply to model
    protected Float rotX = 0f, rotY = 0f, rotZ = 0f; // this would apply to modelRoot
    protected Float scaleX = 1f, scaleY = 1f, scaleZ = 1f; // relative/local; this would apply to model
        
    //all below would go from -(input) to (input) EXCLUSIVE NOT INCLUSIVE in terms of the RNG; also gson this area; ROTATION IS ALWAYS IN DEGREES
    protected Float xAddedRandomness = 0f, yAddedRandomness = 0f, zAddedRandomness = 0f; // relative/local; this would apply to model
    protected Float rotXAddedRandomness = 0f, rotYAddedRandomness = 0f, rotZAddedRandomness = 0f; // this would apply to modelRoot
    protected Float scaleXAddedRandomness = 0f, scaleYAddedRandomness = 0f, scaleZAddedRandomness = 0f; // relative/local; this would apply to model
        
    public DeserializedModel() {}
        
    public DeserializedModel
    (
        Float x, Float y, Float z,
        Float rotX, Float rotY, Float rotZ,
        Float scaleX, Float scaleY, Float scaleZ,
        Float xAddedRandomness, Float yAddedRandomness, Float zAddedRandomness,
        Float rotXAddedRandomness, Float rotYAddedRandomness, Float rotZAddedRandomness,
        Float scaleXAddedRandomness, Float scaleYAddedRandomness, Float scaleZAddedRandomness
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.xAddedRandomness = xAddedRandomness;
        this.yAddedRandomness = yAddedRandomness;
        this.zAddedRandomness = zAddedRandomness;
        this.rotXAddedRandomness = rotXAddedRandomness;
        this.rotYAddedRandomness = rotYAddedRandomness;
        this.rotZAddedRandomness = rotZAddedRandomness;
        this.scaleXAddedRandomness = scaleXAddedRandomness;
        this.scaleYAddedRandomness = scaleYAddedRandomness;
        this.scaleZAddedRandomness = scaleZAddedRandomness;
    }
        
    public Node getNode() {
        return modelRoot;
    }
        
    protected float randomize(Float factor) { //EXCLUSIVE NOT INCLUSIVE
        return factor != null ? (float)(( (2 * factor) * Math.random() ) - factor) : 0;
    }
        
    protected float x() { return x != null ? x : 0; }
    protected float y() { return y != null ? y : 0; }
    protected float z() { return z != null ? z : 0; }
    
    protected float rotX() { return rotX != null ? (rotX * (FastMath.PI / 180f)) : 0; } //converts degrees to radians
    protected float rotY() { return rotY != null ? (rotY * (FastMath.PI / 180f)) : 0; } //converts degrees to radians
    protected float rotZ() { return rotZ != null ? (rotZ * (FastMath.PI / 180f)) : 0; } //converts degrees to radians
    
    protected float scaleX() { return scaleX != null ? scaleX / 100f : 1; }
    protected float scaleY() { return scaleY != null ? scaleY / 100f : 1; }
    protected float scaleZ() { return scaleZ != null ? scaleZ / 100f : 1; }
    
    public void applyTransformations(Node model) {
        Float x2 = x() + randomize(xAddedRandomness), y2 = y() + randomize(yAddedRandomness), z2 = z() + randomize(zAddedRandomness);
        model.setLocalTranslation(x2, y2, z2);
            
        Float scaleX2 = scaleX() + randomize(scaleXAddedRandomness / 100f), scaleY2 = scaleY() + randomize(scaleYAddedRandomness / 100f), scaleZ2 = scaleZ() + randomize(scaleZAddedRandomness / 100f);
        model.setLocalScale(scaleX2, scaleY2, scaleZ2);
            
        Float rotX2 = rotX() + randomize(rotXAddedRandomness), rotY2 = rotY() + randomize(rotYAddedRandomness), rotZ2 = rotZ() + randomize(rotZAddedRandomness);
        Quaternion rotation = new Quaternion();
        rotation.fromAngles(rotX2, rotY2, rotZ2);
        modelRoot.setLocalRotation(rotation);
    }
}
