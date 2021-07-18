/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.google.gson.annotations.Expose;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import enginetools.math.Vector3F;

/**
 *
 * @author night
 */
public class DeserializedModel {
    //public static final float SCALE_DIVIDEND = 100f;
    
    @Expose(deserialize = false)
    private Node modelRoot; //the parent of actual thing
        
    //gson this area
    //added randomness would go from -(input) to (input) EXCLUSIVE NOT INCLUSIVE in terms of the RNG; also gson this area; ROTATION IS ALWAYS IN DEGREES
    private DualVector3F translation = new DualVector3F( // relative/local; this would apply to model
        new Vector3F(0f, 0f, 0f), //xyz
        new Vector3F(0f, 0f, 0f)  //added randomness
    );
    
    private DualVector3F angle = new DualVector3F( // this would apply to modelRoot
        new Vector3F(0f, 0f, 0f), //xyz
        new Vector3F(0f, 0f, 0f)  //added randomness
    );
    
    private DualVector3F scale = new DualVector3F( // relative/local; this would apply to model
        new Vector3F(1f, 1f, 1f), //xyz
        new Vector3F(0f, 0f, 0f)  //added randomness
    );
        
    public DeserializedModel() {}
        
    public DeserializedModel(DualVector3F translation, DualVector3F angle, DualVector3F scale) {
        this.translation = translation;
        this.angle = angle;
        this.scale = scale;
    }
    
    protected DeserializedModel instantiateTransformations() {
        translation = new DualVector3F();
        angle = new DualVector3F();
        scale = new DualVector3F();
        
        return this;
    }
        
    public Node getModelRootNode() {
        return modelRoot;
    }
    
    public final void instantiateModelRootNode() {
        createModelRootNode(modelRoot);
    }
    
    protected void createModelRootNode(Node root) {
        modelRoot = new Node("DeserializedModel: modelRoot");
    }
    
    public void attachChildToModelRootNode(Spatial child) {
        modelRoot.attachChild(child);
    }
    
    //EXCLUSIVE NOT INCLUSIVE
    public static float randomize(float factor) {
        return (float)(( (2 * factor) * Math.random() ) - factor);
    }
    
    //EXCLUSIVE NOT INCLUSIVE
    public static Vector3f randomize(Vector3f addedRandomness) {
        return new Vector3f(randomize(addedRandomness.x), randomize(addedRandomness.y), randomize(addedRandomness.z));
    }
        
    protected Vector3f translation() { return translation != null ? translation.vec(0) : Vector3F.fill(0); }
    protected Vector3f translationAddedRandomness() { return translation != null ? translation.addedRandomness(0) : Vector3F.fill(0); }
    
    protected Vector3f angle() { return angle != null ? angle.vec(0).multLocal(FastMath.PI / 180f) : Vector3F.fill(0); } //converts degrees to radians
    protected Vector3f angleAddedRandomness() { return angle != null ? angle.addedRandomness(0).multLocal(FastMath.PI / 180f) : Vector3F.fill(0); } //converts degrees to radians
    
    protected Vector3f scale() { return scale != null ? scale.vec(1) : Vector3F.fill(1); }
    protected Vector3f scaleAddedRandomness() { return scale != null ? scale.addedRandomness(0) : Vector3F.fill(0); }
    
    public Vector3f getLocalAngleOfModelRootNode() {
        Quaternion rot = modelRoot.getLocalRotation();
        float[] angles = rot.toAngles(null);
        
        return new Vector3f(angles[0], angles[1], angles[2]);
    }
    
    /**
     * 
     * @param model the CHILD of modelRoot
     */
    public void applyTransformations(Node model) {
        Vector3f translationRandomDeltas = randomize(translationAddedRandomness());
        Vector3f translation2 = translation().addLocal(translationRandomDeltas);
        model.setLocalTranslation(translation2);
        
        Vector3f scaleRandomDeltas = randomize(scaleAddedRandomness());
        Vector3f scale2 = scale().addLocal(scaleRandomDeltas);
        model.setLocalScale(scale2);
        
        Vector3f angleRandomDeltas = randomize(angleAddedRandomness());
        Vector3f angle2 = angle().addLocal(angleRandomDeltas);
        
        Quaternion rotation = new Quaternion();
        rotation.fromAngles(angle2.x, angle2.y, angle2.z);
        modelRoot.setLocalRotation(rotation);
    }
    
    public static class DualVector3F {
        public Vector3F vec;
        public Vector3F addedRandomness;
        
        public DualVector3F(Vector3F vec, Vector3F addedRandomness) {
            this.vec = vec;
            this.addedRandomness = addedRandomness;
        }
        
        public DualVector3F() {}
        
        
        public Vector3f vec(float defaultVal) {
            return vec != null ? vec.toVector3f(defaultVal) : Vector3F.fill(defaultVal);
        }
        
        public Vector3f addedRandomness(float defaultVal) {
            return addedRandomness != null ? addedRandomness.toVector3f(defaultVal) : Vector3F.fill(defaultVal);
        }
    }
}
