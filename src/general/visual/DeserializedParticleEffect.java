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
import com.google.gson.annotations.Expose;
import com.jme.effekseer.EffekseerEmitterControl;
import com.jme.effekseer.driver.EffekseerEmissionDriverGeneric;
import com.jme.effekseer.driver.fun.impl.EffekseerGenericDynamicInputSupplier;
import com.jme.effekseer.driver.fun.impl.EffekseerGenericSpawner;
import com.jme.effekseer.driver.fun.impl.EffekseerPointFollowingSpatialShape;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import etherealtempest.Globals;
import general.tools.GameTimer;
import general.procedure.functional.UpdateLoop;
import general.visual.animation.VisualTransition.Progress;
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
    

    @Expose(deserialize = false) private EffekseerControl effectControlManual;
    @Expose(deserialize = false) private EffekseerEmitterControl effectControl;
    @Expose(deserialize = false) private EffekseerEmissionDriverGeneric driver;
    @Expose(deserialize = false) private Node particleNode;
    
    @Expose(deserialize = false) private GameTimer counter;
    @Expose(deserialize = false) private Progress effectProgress;
    @Expose(deserialize = false) private UpdateLoop onEffectStart;
    @Expose(deserialize = false) private UpdateLoop onEffectUpdate;
    @Expose(deserialize = false) private UpdateLoop onEffectFinish;
    
    //gson these
    private String effectPath = "";
    private Integer frames = -1;
    private Boolean useDestroyoflyer = false;
    
    //the fields below only matter if useDestroyoflyer == false
    private Boolean useCustomDriver = false;
    private Boolean usePointFollowingSpatialShape = false;
    private Boolean loop = false;
    private Float initialDelay = 0f, minimumDelay = 0f, maximumDelay = 0f; //in miliseconds
    private Integer maxInstances = 1000;
    
    public DeserializedParticleEffect() { super(); }
        
    public DeserializedParticleEffect
    (
        DualVector3F translation, DualVector3F angle, DualVector3F scale,
        String effectPath, Integer frames, Boolean useDestroyoflyer, Boolean useCustomDriver, Boolean usePointFollowingSpatialShape,
        Boolean loop, Float initialDelay, Float minimumDelay, Float maximumDelay, Integer maxInstances
    ) {
        super(translation, angle, scale);
        
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
    
    public boolean useDestroyoflyer() { return useDestroyoflyer != null ? useDestroyoflyer : false; }
    public boolean useCustomDriver() { return useCustomDriver != null ? useCustomDriver : false; }
    public boolean usePointFollowingSpatialShape() { return usePointFollowingSpatialShape != null ? usePointFollowingSpatialShape : false; }
    public boolean loop() { return loop != null ? loop : false; }
    public float initialDelay() { return initialDelay != null ? initialDelay / 1000f : 0f; }
    public float minimumDelay() { return minimumDelay != null ? minimumDelay / 1000f : 0f; }
    public float maximumDelay() { return maximumDelay != null ? maximumDelay / 1000f : 0f; }
    //no maxInstances() method
    
    public void initialize(AssetManager assetManager) {
        instantiateModelRootNode();
        particleNode = new Node();
        
        counter = new GameTimer();
        effectProgress = Progress.Fresh;
        
        onEffectStart = (tpf) -> {};
        onEffectUpdate = (tpf) -> {};
        onEffectFinish = (tpf) -> {};
        
        if (useDestroyoflyer()) {
            effectPath = "assets/" + effectPath;
            effectPath += destroyoflyerSuffix;
            
            resetManualControl(assetManager);
        } else {
            effectPath += riccardoSuffix;
            effectControl = new EffekseerEmitterControl(assetManager, effectPath);
            
            if (useCustomDriver()) {
                constructDriver();
            }
        
            particleNode.addControl(effectControl);
        }
        
        applyTransformations(particleNode);
        attachChildToModelRootNode(particleNode);
    }
    
    public void resetManualControl(AssetManager assetManager) {
        EffekseerReader reader = new EffekseerReader();
        
        String[] effectTreePath = effectPath.split("/");
        String compatiblePath = effectTreePath[0];
        for (int i = 1; i < effectTreePath.length; i++) {
            compatiblePath += "\\" + effectTreePath[i];
        }
        
        ParticleEffect effect = reader.read("assets", compatiblePath);
        effectControlManual = new EffekseerControl(effect, new ParticleEffectSettings(), assetManager);
        particleNode.addControl(effectControlManual);
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
    
    protected Node getParticleNode() { //DO NOT ATTACH THIS TO THE ROOT NODE; ATTACH modelRoot INSTEAD. use getModelRootNode() to get it
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
    
    public GameTimer getCounter() {
        return counter;
    }
    
    public Progress getEffectProgress() {
        return effectProgress;
    }
    
    public void resetEffectProgress() {
        effectProgress = Progress.Fresh;
    }
    
    public void onEffectStart(UpdateLoop onStart) {
        onEffectStart = onStart;
    }
    
    public void onEffectUpdate(UpdateLoop onUpdate) {
        onEffectUpdate = onUpdate;
    }
    
    public void onEffectFinish(UpdateLoop onFinish) {
        onEffectFinish = onFinish;
    }
    
    public void update(float tpf) {
        switch(effectProgress) {
            case Progressing:
                if (useDestroyoflyer()) { // same as manualControl != null
                    effectControlManual.update(tpf);
                }
                
                onEffectUpdate.update(tpf);
                
                counter.update(tpf);
                
                if (counter.getFrame() >= getFrames()) {
                    effectProgress = Progress.Finished;
                }
                
                break;
            case Fresh:
                if (useDestroyoflyer()) { // same as manualControl != null
                    effectControlManual.setEnabled(true);
                }
                
                onEffectStart.update(tpf);
                
                counter.reset();     
                
                effectProgress = Progress.Progressing;
                break;
            case Finished:
                if (useDestroyoflyer()) { // same as manualControl != null
                    effectControlManual.setEnabled(false);
                }
                
                onEffectFinish.update(tpf);
                
                if (loop()) {
                    effectProgress = Progress.Fresh;
                }
                break;
        }
    }
    
    
    public static DeserializedParticleEffect loadEffect(String jsonPath, AssetManager assetManager) {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Effects\\config\\" + jsonPath));
            
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
