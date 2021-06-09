/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental;

import battle.animation.SpriteAnimationParams;
import battle.animation.VisibleEntityAnimation;
import battle.animation.config.EntityAnimation;
import battle.animation.config.EntityAnimation.AnimationSource;
import com.google.gson.annotations.Expose;

/**
 *
 * @author night
 */
public class BattleVisual {
    @Expose(deserialize = false) 
    private EntityAnimation entityAnimation;
    
    private String animationJsonPath;
    private AnimationSource type;
    
    public BattleVisual(String animationJsonPath, AnimationSource type) {
        this.animationJsonPath = animationJsonPath;
        this.type = type;
    }
    
    public EntityAnimation getEntityAnimation() { return entityAnimation; }
    public AnimationSource getAnimationType() { return type; }
    public String getJSONPath() { return animationJsonPath; }
    
    public void deserializeIfNotAlready() {
        if (entityAnimation == null) {
            entityAnimation = EntityAnimation.deserializeAuto(animationJsonPath, type);
        }
    }
    
    public void addFolderRoot(String rootPath) { //for example, if your animationJsonPath was "phase_animations\\idle.json", rootPath would be the root that leads to it, thus giving it its entire context
        animationJsonPath = rootPath + animationJsonPath;
    }
    
    public boolean isEquivalentAnimationTo(BattleVisual other) {
        return animationJsonPath.equals(other.animationJsonPath);
    }
    
    public boolean isEquivalentAnimationTo(String path) {
        return animationJsonPath.equals(path);
    }
}
