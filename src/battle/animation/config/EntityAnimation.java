/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import battle.animation.SpriteAnimationParams;
import battle.animation.VisibleEntityAnimation;
import battle.animation.VisibleEntityParticleEffectAnimation;
import battle.animation.VisibleEntitySpriteAnimation;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import general.math.IntPair;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 * 
 * Terminology: 
 * local frame ALWAYS starts at 0 and is the index of the frame of the actual animation
 * segment frame is the vertical index starting from the top of the spritesheet (if there is one); otherwise it is literally frame of an index of the field 'animation'
 * actual frame is the actual frame from the animation
 */
public class EntityAnimation {
    public enum AnimationSource { 
        @SerializedName("Spritesheet") Spritesheet((EntityAnimationCreator<VisibleEntitySpriteAnimation>)(config, params) -> {
            return new VisibleEntitySpriteAnimation(config, params);
        }), 
        @SerializedName("Particle Effect") ParticleEffect((EntityAnimationCreator<VisibleEntityParticleEffectAnimation>)(config, params) -> {
            return new VisibleEntityParticleEffectAnimation(config, params);
        });
        
        private interface EntityAnimationCreator<A extends VisibleEntityAnimation> {
            public A create(EntityAnimation config, SpriteAnimationParams params);
        }
        
        private final EntityAnimationCreator creator;
        private AnimationSource(EntityAnimationCreator creator) {
            this.creator = creator;
        }
        
        public VisibleEntityAnimation createAnimation(EntityAnimation config, SpriteAnimationParams params) {
            return creator.create(config, params);
        }
    }
    
    @Expose(deserialize = false) private AnimationSource animSource;
    @Expose(deserialize = false) private PossibleConfig config; //spritesheet config or particle effect animation config, but not both
    
    private String configPath; //path to PossibleConfig (config)
    private Vector2f hitPoint; //hitbox
    private ColumnDomainDelayMapping[] animation; //which frames to use and their delays
    private ActionFrame impact; //impact sound; this can also be the start of casting ether formulas/spells
    private ActionFrame[] otherActionFrames; //other sounds at specified frames
    
    public EntityAnimation(String configPath, Vector2f hitPoint, ColumnDomainDelayMapping[] animation, ActionFrame impact, ActionFrame[] otherActionFrames) {
        this.configPath = configPath;
        this.hitPoint = hitPoint;
        this.animation = animation;
        this.impact = impact;
        this.otherActionFrames = otherActionFrames;
    }
    
    public void initializeParticleEffects(AssetManager assetManager) {
        if (animSource == AnimationSource.ParticleEffect) {
            config.getPossibleParticleEffect().getEffect().initialize(assetManager);
        }
    }
    
    private void initializeActionFrames() {
        List<ActionFrame> allActionFrames = getAllActionFrames();
        
        for (ActionFrame actionFrame : allActionFrames) {
            actionFrame.initializeAllChanges();
        }
    }
    
    public AnimationSource getAnimationSource() {
        return animSource;
    }
    
    public PossibleConfig getConfig() { 
        return config; 
    }
    
    public Vector2f getHitPoint() {
        return hitPoint;
    }
    
    public int getFrames() {
        int sum = 0;
        for (ColumnDomainDelayMapping segment : animation) {
            sum += segment.getAnimation().length();
        }
        
        return sum;
    }
    
    public int getActualFrameAt(int localFrame) {
        IntPair columnAndSegmentFrame = columnAndSegmentFrameAt(localFrame);
        if (animSource == AnimationSource.Spritesheet) {
            return config.getPossibleSpritesheet().convertToIntPosition(columnAndSegmentFrame.getX(), columnAndSegmentFrame.getY());
        }
        
        return -1;
    }
    
    private IntPair columnAndSegmentFrameAt(int localFrame) { // (column, segmentFrame)
        for (ColumnDomainDelayMapping segment : animation) {
            if (localFrame < segment.getAnimation().length()) {
                return new IntPair(segment.getColumn(), segment.getAnimation().getPositionA() + localFrame);
            }
            
            localFrame -= segment.getAnimation().length();
        }
        
        return null;
    }
    
    public int getColumnAt(int localFrame) {
        return columnAndSegmentFrameAt(localFrame).getX();
    }
    
    public int getSegmentFrameAt(int localFrame) {
        return columnAndSegmentFrameAt(localFrame).getY();
    }
    
    protected ColumnDomainDelayMapping segmentAt(int localFrame) {
        for (ColumnDomainDelayMapping segment : animation) {
            if (localFrame < segment.getAnimation().length()) {
                return segment;
            }
            
            localFrame -= segment.getAnimation().length();
        }
        
        return null;
    }
    
    public float getDelayAt(int localFrame) {
        int frame = getSegmentFrameAt(localFrame);
        for (ColumnDomainDelayMapping d_delay : animation) {
            if (
                    (frame >= d_delay.getAnimation().getPositionA() && frame <= d_delay.getAnimation().getPositionB()) || 
                    (frame <= d_delay.getAnimation().getPositionA() && frame >= d_delay.getAnimation().getPositionB())
               ) {
                return d_delay.getAnimation().getDelay();
            }
        }
        
        return 0f; //no delay if no custom delay was found
    }
    
    public boolean impactOccursAt(int localFrame) {
        if (impact == null) {
            return false;
        }
        
        IntPair info = columnAndSegmentFrameAt(localFrame);
        return info.getX() == impact.getColumn() && info.getY() == impact.getFrame();
    }
    
    public List<ActionFrame> getAllActionFrames() { //typically not in order because of impact
        List<ActionFrame> actionFrames = new ArrayList<>();
        
        actionFrames.addAll(Arrays.asList(otherActionFrames));
        
        if (impact != null) {
            actionFrames.add(impact);
        }
        
        return actionFrames;
    }
    
    public List<ActionFrame> getActionFramesAt(int localFrame) {
        List<ActionFrame> allActionFrames = getAllActionFrames();
        List<ActionFrame> actions = new ArrayList<>();
        
        IntPair info = columnAndSegmentFrameAt(localFrame);
        
        for (ActionFrame action : allActionFrames) {
            if (info.getX() == action.getColumn() && info.getY() == action.getFrame()) {
                actions.add(action);
            }
        }
        
        return actions;
    }
    
    public static EntityAnimation deserializeForSpritesheet(String jsonPath) { //example would be: "Battle\\Freeblade\\offense\\sword\\phase_animations\\attack.json"
        try {
            String spritePath = "Sprites\\" + jsonPath;
            
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\" + spritePath));
            
            EntityAnimation entityAnimation = gson.fromJson(reader, EntityAnimation.class);
            
            String folderRoot = spritePath.substring(0, spritePath.lastIndexOf("\\")); // phase_animations or skill_animations folder
            String fileRoot = folderRoot.substring(0, folderRoot.lastIndexOf("\\")) + "\\"; //root folder + \\
            
            entityAnimation.config = PossibleConfig.deserialize(entityAnimation.configPath);
            entityAnimation.config.getPossibleSpritesheet().setFileRoot(fileRoot);
            entityAnimation.initializeActionFrames();
            entityAnimation.animSource = AnimationSource.Spritesheet;
            
            return entityAnimation;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static EntityAnimation deserializeForParticleEffect(String jsonPath) { //example would be: "Battle\\Formulas\\AnemoSchism.json"
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Effects\\config\\" + jsonPath));
            
            EntityAnimation entityAnimation = gson.fromJson(reader, EntityAnimation.class);
            entityAnimation.config = PossibleConfig.deserialize(entityAnimation.configPath);
            entityAnimation.initializeActionFrames();
            entityAnimation.animSource = AnimationSource.ParticleEffect;
            
            return entityAnimation;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static EntityAnimation deserializeAuto(String jsonPath, AnimationSource animationType) {
        if (animationType == AnimationSource.Spritesheet) {
            return deserializeForSpritesheet(jsonPath);
        }
        
        if (animationType == AnimationSource.ParticleEffect) {
            return deserializeForParticleEffect(jsonPath);
        }
        
        return null;
    }
}
