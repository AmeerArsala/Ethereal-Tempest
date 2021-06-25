/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.LayerComparator;
import general.visual.DeserializedModel;

/**
 *
 * @author night
 */
public class TileVisualData { //TODO: Add Shaders to this
    public enum GroundType {
        @SerializedName("Grass") Grass(0, "grass.jpg", 9),
        @SerializedName("Dirt") Dirt(1, "dirt.jpg", 10),
        @SerializedName("Rock") Rock(2, "rock.jpg", 3),
        @SerializedName("Plains") Plains(3, "plains.jpg", -9),
        @SerializedName("Sand") Sand(4, "sand.jpg", 8),
        @SerializedName("Mossy Stone") MossyStone(5, "mossystone.jpg", -11);
        
        private final int index;
        private final String path;
        private final int blendPriority;
        
        private GroundType(int textureArrayIndex, String tex, int textureBlendPriority) {
            index = textureArrayIndex;
            path = "Textures/tiles/" + tex;
            blendPriority = textureBlendPriority;
        }
        
        public int getIndex() {
            return index;
        }
        
        public String getPath() {
            return path;
        }
        
        public int getBlendPriority() {
            return blendPriority;
        }
    }
    
    //DO NOT GSON THESE
    @Expose(deserialize = false) private Node modelTemplateForAssimilation;
    @Expose(deserialize = false) private Node root; //the root node but configured for json
    
    private Float xDisplace, yDisplace, zDisplace; //of the entire node
    
    private String modelPath = null;
    private DistinctOccupantModel[] clones = null; // a group of trees would be one example; if you wanted 4 trees you would make 4 of these
    private GroundType groundType;
    
    private Boolean wangTexture = false; //rotation of texture
    private Boolean deformTexture = false; //messes with texture coordinates
    
    public TileVisualData(String modelPath, DistinctOccupantModel[] clones, GroundType groundType, Float xDisplace, Float yDisplace, Float zDisplace, Boolean wangTexture, Boolean deformTexture) {
        this.modelPath = modelPath;
        this.clones = clones;
        this.groundType = groundType;
        this.xDisplace = xDisplace;
        this.yDisplace = yDisplace;
        this.zDisplace = zDisplace;
        this.wangTexture = wangTexture;
        this.deformTexture = deformTexture;
    }
    
    public Node getRootNode() { return root; }
    
    public GroundType getGroundType() { return groundType; }
    public String getTileTexturePath() { return groundType.path; }
    
    public boolean getIsWangTexture() { return wangTexture != null ? wangTexture : false; }
    public boolean getDeformTexture() { return deformTexture != null ? deformTexture : false; }
    
    private float xDisplace() { return xDisplace != null ? xDisplace : 0; }
    private float yDisplace() { return yDisplace != null ? yDisplace : 0; }
    private float zDisplace() { return zDisplace != null ? zDisplace : 0; }
    
    Vector3f Displacement() { return new Vector3f(xDisplace(), yDisplace(), zDisplace()); }
    void setIsWangTexture(boolean wang) { wangTexture = wang; }
    
    private class DistinctOccupantModel extends DeserializedModel {
        
        public DistinctOccupantModel() { super(); }
        
        public DistinctOccupantModel
        (
            Float x, Float y, Float z,
            Float rotX, Float rotY, Float rotZ,
            Float scaleX, Float scaleY, Float scaleZ,
            Float xAddedRandomness, Float yAddedRandomness, Float zAddedRandomness,
            Float rotXAddedRandomness, Float rotYAddedRandomness, Float rotZAddedRandomness,
            Float scaleXAddedRandomness, Float scaleYAddedRandomness, Float scaleZAddedRandomness
        ) {
            super
            (
                x, y, z, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, 
                xAddedRandomness, yAddedRandomness, zAddedRandomness,
                rotXAddedRandomness, rotYAddedRandomness, rotZAddedRandomness,
                scaleXAddedRandomness, scaleYAddedRandomness, scaleZAddedRandomness
            );
        }
        
        public void resetRootNode() {
            modelRoot = new Node();
        }
        
        public void integrate(Node tileVisualDataRootNode, Node structure) {
            Node model = structure.clone(true);
            
            applyTransformations(model);
            
            modelRoot.attachChild(model);
            tileVisualDataRootNode.attachChild(modelRoot);
        }
    }
    
    public void assimilate(AssetManager assetManager, Node tileRootNode) { // maybe add ambient light?
        lightAssimilate(assetManager);
        finishAssimilation(tileRootNode);
    }
    
    //All the methods below are for breaking up the process into pieces
    public void lightAssimilate(AssetManager assetManager) {
        root = new Node();
        root.addLight(new DirectionalLight());
        root.move(Displacement());
        if (modelPath != null) {
            modelTemplateForAssimilation = (Node)assetManager.loadModel(modelPath);
        }
        
        LayerComparator.setLayer(root, 3);
    }
    
    public void finishAssimilation(Node tileRootNode) {
        if (clones != null) {
            for (DistinctOccupantModel dom : clones) {
                dom.resetRootNode();
                dom.integrate(root, modelTemplateForAssimilation);
            }
            tileRootNode.attachChild(root);
        }
    }
}
