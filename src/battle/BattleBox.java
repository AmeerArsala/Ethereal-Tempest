/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import enginetools.ParamSetter;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author night
 */
public class BattleBox {
    private final String modelPath;
    private final Vector2f dimensions;
    private final TextureSettings params;
    
    public BattleBox(String modelPath, Vector2f dimensions, TextureSettings params) {
        this.modelPath = modelPath;
        this.dimensions = dimensions;
        this.params = params;
    }
    
    public String getModelPath() {
        return modelPath;
    }

    public Vector2f getDimensions() {
        return dimensions;
    }
    
    public Node generateModel(AssetManager assetManager) {
        Node model = (Node)assetManager.loadModel(modelPath);
        params.setTextures(model, assetManager);
        return model;
    }
    
    public static class TextureSettings {
        private final HashMap<String, String> childToTextureMap;
        private final String matTextureParam, matPath;
        
        public TextureSettings(HashMap<String, String> childToTextureMap, String matTextureParam, String matPath) {
            this.childToTextureMap = childToTextureMap;
            this.matTextureParam = matTextureParam;
            this.matPath = matPath;
        }
        
        public void setTextures(Node model, AssetManager assetManager) {
            for (String child : childToTextureMap.keySet()) {
                Material mat = new Material(assetManager, matPath);
                mat.setTexture(matTextureParam, assetManager.loadTexture(childToTextureMap.get(child)));
                
                model.getChild(child).setMaterial(mat);
            }
        }
    }
}
