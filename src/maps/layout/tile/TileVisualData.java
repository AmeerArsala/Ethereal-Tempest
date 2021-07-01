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
import enginetools.math.Vector3F;
import general.visual.DeserializedModel;
import general.visual.DistinctOccupantModel;

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
    
    @Expose(deserialize = false) private Node modelTemplateForAssimilation;
    @Expose(deserialize = false) private Node root; //the root node of the model; essentially its own modelRoot from DeserializedModel
    
    private Vector3F displacement; //of the entire node (root)
    
    private String modelPath = null;
    private DistinctOccupantModel[] clones = null; // a group of trees would be one example; if you wanted 4 trees you would make 4 of these
    private GroundType groundType;
    
    private Boolean wangTexture = false; //rotation of texture
    private Boolean deformTexture = false; //messes with texture coordinates
    
    public TileVisualData(String modelPath, DistinctOccupantModel[] clones, GroundType groundType, Vector3F displacement, Boolean wangTexture, Boolean deformTexture) {
        this.modelPath = modelPath;
        this.clones = clones;
        this.groundType = groundType;
        this.displacement = displacement;
        this.wangTexture = wangTexture;
        this.deformTexture = deformTexture;
    }
    
    public Node getRootNode() { return root; }
    
    public GroundType getGroundType() { return groundType; }
    public String getTileTexturePath() { return groundType.path; }
    
    public boolean getIsWangTexture() { return wangTexture != null ? wangTexture : false; }
    public boolean getDeformTexture() { return deformTexture != null ? deformTexture : false; }
    
    Vector3f Displacement() { return displacement != null ? displacement.toVector3f(0) : new Vector3f(0, 0, 0); }
    void setIsWangTexture(boolean wang) { wangTexture = wang; }
    
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
                dom.instantiateModelRootNode(); //reset modelRoot (instantiate it again)
                dom.integrate(root, modelTemplateForAssimilation);
            }
            tileRootNode.attachChild(root);
        }
    }
}
