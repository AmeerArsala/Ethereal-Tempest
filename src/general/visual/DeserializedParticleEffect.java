/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.destroflyer.jme3.effekseer.model.ParticleEffect;
import com.destroflyer.jme3.effekseer.model.ParticleEffectSettings;
import com.destroflyer.jme3.effekseer.reader.EffekseerReader;
import com.destroflyer.jme3.effekseer.renderer.EffekseerControl;
import com.google.gson.Gson;
import com.jme.effekseer.EffekseerEmitterControl;
import com.jme.effekseer.driver.EffekseerEmissionDriverGeneric;
import com.jme.effekseer.driver.fun.impl.EffekseerGenericDynamicInputSupplier;
import com.jme.effekseer.driver.fun.impl.EffekseerGenericSpawner;
import com.jme.effekseer.driver.fun.impl.EffekseerPointFollowingSpatialShape;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author night
 */
public class DeserializedParticleEffect extends DeserializedModel {
    private static final String destroyoflyerSuffix = ".efkproj";
    private static final String riccardoSuffix = ".efkefc";
    
    //DO NOT GSON THESE
    private EffekseerControl effectControlManual;
    private EffekseerEmitterControl effectControl;
    private EffekseerEmissionDriverGeneric driver;
    private Node particleNode;
    private int counter = 0;
    
    //gson these
    private String effectPath = "";
    private Integer frames = -1;
    private Boolean useDestroyoflyer = false;
    private Boolean useCustomDriver = false;
    private Boolean usePointFollowingSpatialShape = false;
    private Boolean loop = false;
    private Float initialDelay = 0f, minimumDelay = 0f, maximumDelay = 0f; //in miliseconds
    private Integer maxInstances = 1000;
    
    public DeserializedParticleEffect() { super(); }
        
    public DeserializedParticleEffect
    (
        Float x, Float y, Float z,
        Float rotX, Float rotY, Float rotZ,
        Float scaleX, Float scaleY, Float scaleZ,
        Float xAddedRandomness, Float yAddedRandomness, Float zAddedRandomness,
        Float rotXAddedRandomness, Float rotYAddedRandomness, Float rotZAddedRandomness,
        Float scaleXAddedRandomness, Float scaleYAddedRandomness, Float scaleZAddedRandomness,
        String effectPath, Integer frames, Boolean useDestroyoflyer, Boolean useCustomDriver, Boolean usePointFollowingSpatialShape,
        Boolean loop, Float initialDelay, Float minimumDelay, Float maximumDelay, Integer maxInstances
    ) {
        super
        (
            x, y, z, rotX, rotY, rotZ, scaleX, scaleY, scaleZ, 
            xAddedRandomness, yAddedRandomness, zAddedRandomness,
            rotXAddedRandomness, rotYAddedRandomness, rotZAddedRandomness,
            scaleXAddedRandomness, scaleYAddedRandomness, scaleZAddedRandomness
        );
        
        this.effectPath = effectPath;
        this.frames = frames;
        this.useDestroyoflyer = useDestroyoflyer;
        this.useCustomDriver = useCustomDriver;
        this.usePointFollowingSpatialShape = usePointFollowingSpatialShape;
        this.loop = loop;
        this.initialDelay = initialDelay;
        this.minimumDelay = minimumDelay;
        this.maximumDelay = maximumDelay;
        this.maxInstances = maxInstances;
    }
    
    public int getFrames() {
        return frames != null ? frames : -1;
    }
    
    public int getCount() { return counter; }
    public void setCount(int count) { counter = count; }
    public void incrementCount() { counter++; }
    public void decrementCount() { counter--; }
    
    private boolean useDestroyoflyer() { return useDestroyoflyer != null ? useDestroyoflyer : false; }
    private boolean useCustomDriver() { return useCustomDriver != null ? useCustomDriver : false; }
    private boolean usePointFollowingSpatialShape() { return usePointFollowingSpatialShape != null ? usePointFollowingSpatialShape : false; }
    private boolean loop() { return loop != null ? loop : false; }
    private float initialDelay() { return initialDelay != null ? initialDelay / 1000f : 0f; }
    private float minimumDelay() { return minimumDelay != null ? minimumDelay / 1000f : 0f; }
    private float maximumDelay() { return maximumDelay != null ? maximumDelay / 1000f : 0f; }
    //no maxInstances() method
    
    public void initialize(AssetManager assetManager) {
        modelRoot = new Node();
        particleNode = new Node();
        
        if (useDestroyoflyer()) {
            effectPath = "assets/" + effectPath;
            effectPath += destroyoflyerSuffix;
            
            EffekseerReader reader = new EffekseerReader();
            
            String[] effectTreePath = effectPath.split("/");
            String compatiblePath = effectTreePath[0];
            for (int i = 1; i < effectTreePath.length; i++) {
                compatiblePath += "\\" + effectTreePath[i];
            }
            
            ParticleEffect effect = reader.read("assets", compatiblePath);
            effectControlManual = new EffekseerControl(effect, new ParticleEffectSettings(), assetManager);
            particleNode.addControl(effectControlManual);
        } else {
            effectPath += riccardoSuffix;
            effectControl = new EffekseerEmitterControl(assetManager, effectPath);
        
            if (useCustomDriver()) {
                constructDriver();
            }
        
            particleNode.addControl(effectControl);
        }
        
        applyTransformations(particleNode);
        modelRoot.attachChild(particleNode);
    }
    
    private void constructDriver() {
        driver = new EffekseerEmissionDriverGeneric();
        
        if (usePointFollowingSpatialShape()) {
            driver.shape(new EffekseerPointFollowingSpatialShape());
        }
        
        EffekseerGenericSpawner spawner = new EffekseerGenericSpawner().loop(loop()).delay(minimumDelay(), maximumDelay(), initialDelay());
        if (maxInstances != null) {
            spawner.maxInstances(maxInstances);
        }
        
        effectControl.setDriver(driver.spawner(spawner));
    }
    
    protected Node getParticleNode() { //DO NOT ATTACH THIS TO THE ROOT NODE; ATTACH modelRoot INSTEAD. use getNode()
        return particleNode;
    }
    
    public EffekseerControl getManualControl() {
        return effectControlManual;
    }
    
    public EffekseerEmitterControl getControl() {
        return effectControl;
    }
    
    public EffekseerEmissionDriverGeneric getDriver() {
        return driver;
    }
    
    
    public static DeserializedParticleEffect loadEffect(String jsonPath, AssetManager assetManager) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Models\\Effects\\" + jsonPath));
            
            DeserializedParticleEffect effect = gson.fromJson(reader, DeserializedParticleEffect.class);
            effect.initialize(assetManager);
            
            return effect;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}
