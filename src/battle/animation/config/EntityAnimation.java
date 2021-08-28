/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import battle.animation.config.action.ActionFrame;
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
import java.util.Objects;

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
    public enum Tag {
        LinksToNextHitPoint, //takes the hitPoint of the next animation
        Attack,
        ChainLink, //links to the next animation but if the next one is also ChainLink, it links to the ones after
        Default;
    }
    
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
    
    @Expose(deserialize = false) private String jsonPath;
    @Expose(deserialize = false) private AnimationSource animSource;
    @Expose(deserialize = false) private PossibleConfig config; //spritesheet config or particle effect animation config, but not both
    @Expose(deserialize = false) private List<IntPair> localFrameConversion;
    @Expose(deserialize = false) private int minLocalFrameForRegisteringImpact; //each impact can only be registered once
    
    private String configPath; //path to PossibleConfig (config)
    private Tag tag; //tag which specifies certain aspects to further specialize the animation
    private Vector2f hitPoint; //focal point at which the hit occurs
    private ColumnDomainDelayMapping[] animation; //which frames to use and their delays
    private Boolean loopAnimationVisually; //whether the animation is looped visually or not
    private Float delayForIndefiniteChanges; //delay for Changes that go on for an indefinite duration
    private ActionFrame impact; //impact sound; this can also be the start of casting ether formulas/spells
    private ActionFrame[] otherActionFrames; //other sounds at specified frames
    private Boolean annulChangesFromOpponent;
    
    public EntityAnimation(String configPath, Tag tag, Vector2f hitPoint, ColumnDomainDelayMapping[] animation, Boolean loopAnimationVisually, Float delayForIndefiniteChanges, ActionFrame impact, ActionFrame[] otherActionFrames, Boolean annulChangesFromOpponent) {
        this.configPath = configPath;
        this.tag = tag;
        this.hitPoint = hitPoint;
        this.animation = animation;
        this.loopAnimationVisually = loopAnimationVisually;
        this.delayForIndefiniteChanges = delayForIndefiniteChanges;
        this.impact = impact;
        this.otherActionFrames = otherActionFrames;
        this.annulChangesFromOpponent = annulChangesFromOpponent;
    }
    
    public void initializeParticleEffects(AssetManager assetManager) {
        if (animSource == AnimationSource.ParticleEffect) {
            config.getPossibleParticleEffect().initialize(assetManager);
        }
    }
    
    private void initializeActionFrames() {
        List<ActionFrame> allActionFrames = getAllActionFrames();
        
        for (ActionFrame actionFrame : allActionFrames) {
            actionFrame.initializeAllChanges();
        }
    }
    
    private void initializeLocalFrameConversion() {
        localFrameConversion = new ArrayList<>();
        for (ColumnDomainDelayMapping seg : animation) {
            localFrameConversion.addAll(seg.generateIndexedList());
        }
    }
    
    private void initializeMiscProperties() { //set default values for property fields
        if (tag == null) {
            tag = Tag.Default;
        }
        
        if (loopAnimationVisually == null) {
            loopAnimationVisually = false;
        }
        
        if (delayForIndefiniteChanges == null) {
            delayForIndefiniteChanges = 0.0f;
        }
        
        if (otherActionFrames == null) {
            otherActionFrames = new ActionFrame[0];
        }
        
        if (annulChangesFromOpponent == null) {
            annulChangesFromOpponent = false;
        }
        
        minLocalFrameForRegisteringImpact = 0;
    }
    
    public String getJsonPath() {
        return jsonPath;
    }
    
    public AnimationSource getAnimationSource() {
        return animSource;
    }
    
    public PossibleConfig getConfig() { 
        return config; 
    }
    
    public Tag getTag() {
        return tag;
    }
    
    public Vector2f getHitPoint() {
        return hitPoint;
    }
    
    public Float getDelayForIndefiniteChanges() {
        return delayForIndefiniteChanges;
    }
    
    public ActionFrame getImpact() {
        return impact;
    }
    
    public boolean annulChangesFromOpponent() {
        return annulChangesFromOpponent;
    }
    
    public int getFrames() {
        return localFrameConversion.size();
    }
    
    //only use this for Spritesheets, because particle effects will just use localFrame
    public int getActualFrameAt(int localFrame) {
        if (animSource == AnimationSource.Spritesheet) {
            IntPair columnAndSegmentFrame = columnAndSegmentFrameAt(localFrame);
            int col = columnAndSegmentFrame.getX();
            int segFrame = columnAndSegmentFrame.getY();
            
            return config.getPossibleSpritesheet().convertToIntPosition(col, segFrame);
        }
        
        return getSegmentFrameAt(localFrame); //returns the possibly changed value of localFrame (for use with ParticleEffect)
    }
    
    /**
     * 
     * @param localFrame 
     * @return an IntPair where the x-value is the column and the y-value is the segmentFrame
     */
    private IntPair columnAndSegmentFrameAt(int localFrame) { // (column, segmentFrame)
        int frames = localFrameConversion.size(); // localFrameConversion.size() == getFrames()
        if (loopAnimationVisually) {
            localFrame %= frames;
        } else if (localFrame >= frames) {
            localFrame = frames - 1;
        }
        
        if (animSource == AnimationSource.Spritesheet) {
            return localFrameConversion.get(localFrame);
        }
        
        return new IntPair().setY(localFrame); // animSource == AnimationSource.ParticleEffect
    }
    
    public int getColumnAt(int localFrame) {
        return columnAndSegmentFrameAt(localFrame).getX();
    }
    
    public int getSegmentFrameAt(int localFrame) {
        return columnAndSegmentFrameAt(localFrame).getY();
    }
    
    public float getDelayAt(int localFrame) {
        if (localFrame >= localFrameConversion.size()) { // localFrameConversion.size() == getFrames()
            return delayForIndefiniteChanges;
        }
        
        int frame = getSegmentFrameAt(localFrame);
        for (ColumnDomainDelayMapping d_delay : animation) {
            if (
                    (frame >= d_delay.getAnimation().getPositionA() && frame <= d_delay.getAnimation().getPositionB()) || 
                    (frame <= d_delay.getAnimation().getPositionA() && frame >= d_delay.getAnimation().getPositionB())
            ) {
                return d_delay.getAnimation().getDelay();
            }
        }
        
        return 0.0f; //no delay if no custom delay was found
    }
    
    public boolean impactOccursAt(int localFrame) {
        //System.out.println(jsonPath + " -> " + "localFrame: " + localFrame + ", localFrameConversion.size(): " + localFrameConversion.size());
        if (impact == null || localFrame >= localFrameConversion.size() || localFrame < minLocalFrameForRegisteringImpact) {
            return false;
        }
        
        IntPair info = localFrameConversion.get(localFrame);
        
        if (Objects.equals(info.getX(), impact.getColumn()) && info.getY() == impact.getFrame()) { //x is column, y is segmentFrame; for particle effects, the first condition wlil be null == null
            ++minLocalFrameForRegisteringImpact;
            return true;
        } else {
            return false; 
        }
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
        if (localFrame >= localFrameConversion.size()) { // localFrameConversion.size() == getFrames()
            return new ArrayList<>();
        }
        
        List<ActionFrame> actions = new ArrayList<>();
        List<ActionFrame> allActionFrames = getAllActionFrames();
        
        IntPair info = localFrameConversion.get(localFrame);
        int col = info.getX();
        int segFrame = info.getY();
        
        for (ActionFrame action : allActionFrames) {
            if (col == action.getColumn() && segFrame == action.getFrame()) {
                actions.add(action);
            }
        }
        
        return actions;
    }
    
    public void reset() {
        minLocalFrameForRegisteringImpact = 0;
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
            entityAnimation.initializeLocalFrameConversion();
            entityAnimation.initializeMiscProperties();
            entityAnimation.jsonPath = jsonPath;
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
            entityAnimation.initializeLocalFrameConversion();
            entityAnimation.initializeMiscProperties();
            entityAnimation.jsonPath = jsonPath;
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
