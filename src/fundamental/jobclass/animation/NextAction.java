/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation;

import battle.animation.SpriteAnimationParams;
import battle.animation.VisibleEntityAnimation;
import fundamental.BattleVisual;

/**
 *
 * @author night
 * 
 * determines the next X actions
 */
public class NextAction {
    private BattleVisual action;
    private boolean mirror; //here for convenience purposes
    private boolean playsSimultaneouslyWithPrevious; //does it play at the same time as the battle animation/its predacessor or does it wait for its turn?
    private boolean isAttack;
    
    public NextAction(BattleVisual action, boolean mirror, boolean playsSimultaneouslyWithPrevious, boolean isAttack) {
        this.action = action;
        this.mirror = mirror;
        this.playsSimultaneouslyWithPrevious = playsSimultaneouslyWithPrevious;
        this.isAttack = isAttack;
    }
    
    public BattleVisual getAnimation() { return action; }
    public boolean mirrorUserSprite() { return mirror; }
    public boolean playsSimultaneouslyWithPrevious() { return playsSimultaneouslyWithPrevious; }
    public boolean isAttack() { return isAttack; }
    
    public VisibleEntityAnimation createAnimation(SpriteAnimationParams params) {
        params.mirror = mirror;
        return action.getAnimationType().createAnimation(action.getEntityAnimation(), params);
    }
    
    public boolean isEquivalentAnimation(NextAction other) {
        return action.isEquivalentAnimationTo(other.getAnimation());
    }
    
    public boolean isEquivalentAnimation(String path) {
        return action.isEquivalentAnimationTo(path);
    }
}
