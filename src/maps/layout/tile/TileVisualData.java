/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.google.gson.annotations.SerializedName;
import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;

/**
 *
 * @author night
 */
public class TileVisualData { //TODO: Add Shaders to this
    public enum GroundType {
        @SerializedName("Grass") Grass("grass2.jpg"),
        @SerializedName("Dirt") Dirt("dirt.jpg"),
        @SerializedName("Rock") Rock("rock.jpg"),
        @SerializedName("Plains") Plains("grass.jpg"),
        @SerializedName("Sand") Sand("sand.jpg"),
        @SerializedName("Mossy Stone") MossyStone("mossystone.jpg");
        
        private final String path;
        private GroundType(String tex) {
            path = "Textures/tiles/" + tex;
        }
        
        public String getPath() {
            return path;
        }
    }
    
    private Node modelTemplateForAssimilation; // DO NOT GSON THIS
    private final Node root = new Node(); //the root node but configured for json; DO NOT GSON THIS
    
    private String modelPath = null;
    private DistinctOccupantModel[] models = null; // a group of trees would be one example
    private GroundType groundType;
    
    public TileVisualData(String modelPath, DistinctOccupantModel[] models, GroundType groundType) {
        this.modelPath = modelPath;
        this.models = models;
        this.groundType = groundType;
    }
    
    public Node getRootNode() { return root; }
    
    public GroundType getGroundType() { return groundType; }
    public String getTileTexturePath() { return groundType.path; }
    
    private class DistinctOccupantModel {
        private final Node modelRoot = new Node(); //the parent of actual thing
        
        //gson this area
        private Float x = 0f, y = 0f, z = 0f; // relative/local; this would apply to model
        private Float rotX = 0f, rotY = 0f, rotZ = 0f; // this would apply to modelRoot
        private Float scaleX = 1f, scaleY = 1f, scaleZ = 1f; // relative/local; this would apply to model
        
        
        //all below would go from -(input) to (input) EXCLUSIVE NOT INCLUSIVE in terms of the RNG; also gson this area
        private Float xAddedRandomness = 0f, yAddedRandomness = 0f, zAddedRandomness = 0f; // relative/local; this would apply to model
        private Float rotXAddedRandomness = 0f, rotYAddedRandomness = 0f, rotZAddedRandomness = 0f; // this would apply to modelRoot
        private Float scaleXAddedRandomness = 0f, scaleYAddedRandomness = 0f, scaleZAddedRandomness = 0f; // relative/local; this would apply to model
        
        public DistinctOccupantModel() {}
        
        public DistinctOccupantModel
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
        
        private float randomize(float factor) { //EXCLUSIVE NOT INCLUSIVE
            return (float)(( (2 * factor) * Math.random() ) - factor);
        }
        
        public void integrate(Node tileVisualDataRootNode, Node structure) {
            Node model = structure.clone(true);
            
            Float x2 = x + randomize(xAddedRandomness), y2 = y + randomize(yAddedRandomness), z2 = z + randomize(zAddedRandomness);
            Float rotX2 = rotX + randomize(rotXAddedRandomness), rotY2 = rotY + randomize(rotYAddedRandomness), rotZ2 = rotZ + randomize(rotZAddedRandomness);
            Float scaleX2 = scaleX + randomize(scaleXAddedRandomness), scaleY2 = scaleY + randomize(scaleYAddedRandomness), scaleZ2 = scaleZ + randomize(scaleZAddedRandomness);
            
            model.setLocalTranslation(x2, y2, z2);
            model.setLocalScale(scaleX2, scaleY2, scaleZ2);
            
            modelRoot.attachChild(model);
            
            Quaternion rotation = new Quaternion();
            rotation.fromAngles(rotX2, rotY2, rotZ2);
            modelRoot.setLocalRotation(rotation);
            
            tileVisualDataRootNode.attachChild(modelRoot);
        }
    }
    
    public void assimilate(AssetManager assetManager, Node tileRootNode) { // maybe add ambient light?
        lightAssimilate(assetManager);
        finishAssimilation(tileRootNode);
    }
    
    //All the methods below are for breaking up the process into pieces
    public void lightAssimilate(AssetManager assetManager) {
        if (modelPath != null) {
            modelTemplateForAssimilation = (Node)assetManager.loadModel(modelPath);
        }
    }
    
    public void finishAssimilation(Node tileRootNode) {
        if (models != null) {
            for (DistinctOccupantModel dom : models) {
                dom.integrate(root, modelTemplateForAssimilation);
            }
            tileRootNode.attachChild(root);
        }
    }
}
