/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.environment;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;
import java.util.HashMap;

/**
 *
 * @author night
 */
public class BattleBox {
    private final Vector2f dimensions;
    private final BattleViewInfo viewInfo;
    private final TextureSettings params;
    
    public BattleBox(Vector2f dimensions, BattleViewInfo viewInfo, TextureSettings params) {
        this.dimensions = dimensions;
        this.viewInfo = viewInfo;
        this.params = params;
    }
    
    public Vector2f getDimensions() {
        return dimensions;
    }
    
    public BattleViewInfo getViewInfo() {
        return viewInfo;
    }
    
    public BoxMetadata constructMetadata() {
        return new BoxMetadata(
            dimensions,
            viewInfo.getLeftEdgePositionPercent(),
            viewInfo.getRightEdgePositionPercent(),
            viewInfo.getTopEdgePositionPercent(),
            viewInfo.getBottomEdgePositionPercent()
        );
    }

    public BattleEnvironment generateBattleEnvironment(AssetManager assetManager) {
        Vector3f envCamPos = new Vector3f(
            (viewInfo.getRightEdgePositionPercent() + viewInfo.getLeftEdgePositionPercent()) / 2f, 
            dimensions.y / 2f, 
            viewInfo.getEnvCamPosZ()
        );
        
        System.out.println("envCamPos: " + envCamPos);
        /*
            BattleEnvironment environment = new BattleEnvironment(assetManager, viewInfo.getModelPath(), envCamPos);
            Node model = environment.getTerrainModel();
            params.setTextures(model, assetManager);
        */
        
        return new BattleEnvironment(assetManager, viewInfo.getModelPath(), envCamPos);
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
