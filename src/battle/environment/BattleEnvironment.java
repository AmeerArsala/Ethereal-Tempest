/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.environment;

import com.jme3.asset.AssetManager;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.environment.util.EnvMapUtils;
import com.jme3.light.AmbientLight;
import com.jme3.light.LightProbe;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import etherealtempest.Globals;
import maps.data.MapModels.BattleTerrain;

/**
 *
 * @author night
 */
public class BattleEnvironment {
    private final AssetManager assetManager;
    private final Node scene = new Node("fight scene node"), terrainModel;
    private final LightProbe lightProbe;
    
    
    public BattleEnvironment(AssetManager assetManager, BattleTerrain battleTerrain, Vector3f envCamPos) {
        this.assetManager = assetManager;
        
        terrainModel = battleTerrain.getTerrainModel();
        //terrainModel.setMaterial(new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md"));
        terrainModel.setCullHint(CullHint.Never);
        
        //TangentBinormalGenerator.generate(terrainModel);
        
        scene.attachChild(terrainModel);
        
        EnvironmentCamera envCam = Globals.getStateManager().getState(EnvironmentCamera.class);
        envCam.setPosition(envCamPos);
        lightProbe = battleTerrain.getLightProbe();
        lightProbe.getArea().setRadius(100);
        terrainModel.addLight(lightProbe);
        
        /*
        Spatial skybox = SkyFactory.createSky(assetManager, "Textures/skybox/skybox.png", SkyFactory.EnvMapType.CubeMap);
        skybox.setQueueBucket(RenderQueue.Bucket.Sky);
        
        scene.attachChild(skybox);
        */
        
        //mess with this stuff outside of the constructor probably
        //AmbientLight al = new AmbientLight();
        //scene.addLight(al);
        
        
        SpotLight spotlight = new SpotLight(
            new Vector3f(3f, 30f, 1.5f), //position
            new Vector3f(0, -1, 0), //direction
            250f, //range
            ColorRGBA.White, //color
            FastMath.PI / 3, //inner angle
            80 * FastMath.DEG_TO_RAD //outer angle
        );
        
        scene.addLight(spotlight);
    }
    
    public Node getScene() { 
        return scene; 
    }
    
    public Node getTerrainModel() {
        return terrainModel;
    }
    
    public LightProbe getLightProbe() {
        return lightProbe;
    }
}
