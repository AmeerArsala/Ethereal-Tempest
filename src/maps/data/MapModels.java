/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.data;
import com.jme3.asset.AssetManager;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.environment.util.EnvMapUtils;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import etherealtempest.Globals;

/**
 *
 * @author night
 */
public class MapModels {
    public enum BattleTerrain {
        ForgottenPillar("Models/BattleTerrains/battletest5.gltf") //Scenes/Battle/battletest5.j3o
        ; 
        
        private Node terrainModel = null;
        private LightProbe lightProbe;
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
        
        public LightProbe getLightProbe() {
            return lightProbe;
        }
        
        public void loadTerrainModel(AssetManager assetManager) {
            terrainModel = (Node)assetManager.loadModel(modelPath);
            
            EnvironmentCamera envCam = Globals.getStateManager().getState(EnvironmentCamera.class);
            
            final long startTime = System.currentTimeMillis();
            lightProbe = LightProbeFactory.makeProbe(envCam, terrainModel, EnvMapUtils.GenerationType.Fast, 
                new JobProgressAdapter<LightProbe>() {
                    @Override
                    public void done(LightProbe t) {
                        System.err.println(toString() + ": LightProbe finished loading in " + (System.currentTimeMillis() - startTime) + "ms");
                    }
                }
            );
            
            terrainModel.updateGeometricState();
        }
    }
    
    /*
    public static final class Common {
        
    }
    */
    
    /* these can be loaded independently, because they can be cloned; Maybe change this later
    public static final class TileStructures {
        public static Node Forest;
    }
    */
}
