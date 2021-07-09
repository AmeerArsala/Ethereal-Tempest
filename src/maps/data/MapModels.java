/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.data;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 *
 * @author night
 */
public class MapModels {
    public enum BattleTerrain {
        ForgottenPillar("Models/BattleTerrains/battletest5.gltf") //Scenes/Battle/battletest5.j3o
        ; 
        
        private Node terrainModel = null;
        private final String modelPath;
        
        private BattleTerrain(String modelPath) {
            this.modelPath = modelPath;
        }
        
        public Node getTerrainModel() {
            return terrainModel;
        }
        
        public String getModelPath() {
            return modelPath;
        }
        
        public void loadTerrainModel(AssetManager assetManager) {
            terrainModel = (Node)assetManager.loadModel(modelPath);
        }
    }
    
    /* these can be loaded independently, because they can be cloned; Maybe change this later
    public static final class TileStructures {
        public static Node Forest;
    }
    */
}
