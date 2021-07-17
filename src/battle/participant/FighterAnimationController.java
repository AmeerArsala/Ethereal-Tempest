/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant;

import battle.animation.BattleAnimation;
import battle.animation.SpriteAnimationParams;
import battle.data.CombatFlowData;
import battle.participant.visual.BattleSprite;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import fundamental.jobclass.animation.ActionDecider;
import fundamental.jobclass.animation.NextActionSequence;
import general.procedure.functional.UpdateLoop;
import java.util.function.Predicate;

/**
 *
 * @author night
 */
public class FighterAnimationController {
    private final AssetManager assetManager;
    
    private final BattleSprite sprite;
    private final ActionDecider animationDecider;
    private final BattleAnimation.Queue currentAnimationQueue = new BattleAnimation.Queue();
    
    public FighterAnimationController(BattleSprite sprite, ActionDecider animationDecider, AssetManager assetManager) {
        this.sprite = sprite;
        this.animationDecider = animationDecider;
        this.assetManager = assetManager;
    }
    
    public BattleSprite getSprite() { return sprite; }
    public BattleAnimation.Queue getCurrentAnimationQueue() { return currentAnimationQueue; }
    public BattleAnimation getCurrentAnimation() { return currentAnimationQueue.getCurrentTask(); }
    
    public void setPaused(boolean paused) {
        currentAnimationQueue.setPaused(paused);
    }
    
    public void resetCurrentAnimation() {
        currentAnimationQueue.resetCurrentAnimation();
    }
    
    private void callAnimation(ActionDecider.Procedure next, AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        SpriteAnimationParams params = new SpriteAnimationParams(sprite, opponent, assetManager, animParams.secondEndCondition);
        Node animationRoot = sprite.getParent();
        NextActionSequence nextSequence = next.run(decisionData);
        
        currentAnimationQueue.addToQueue(
            nextSequence.generateBattleAnimation(params, animationRoot), 
            animParams.onUpdate,
            () -> { //onStart
                
            },
            () -> { //onFinish
                currentAnimationQueue.resetStarted();
            }
        );
    }
    
    private void callAttributeAnimation(ActionDecider.AttributeAnimation next, UpdateLoop onDashUpdate, AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        if (next.usesDash()) { //uses default dash
            nextDashAnimation(onDashUpdate, opponent, decisionData);
        }
        
        callAnimation(next.getOnCall(), animParams, opponent, decisionData); //specify movement in the json file
    }
    
    public void nextSkillAttackAnimation(String name, UpdateLoop onDashUpdate, AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        callAttributeAnimation(animationDecider.getOnSkillAttackCalled(name), onDashUpdate, animParams, opponent, decisionData);
    }
    
    public void nextBattleTalentAttackAnimation(String name, UpdateLoop onDashUpdate, AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        callAttributeAnimation(animationDecider.getOnBattleTalentAttackCalled(name), onDashUpdate, animParams, opponent, decisionData);
    }
    
    public void nextAttackAnimation(UpdateLoop onDashUpdate, AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        if (sprite.usesHitPoint()) { //uses dash in that case
            nextDashAnimation(onDashUpdate, opponent, decisionData);
        }
        
        callAnimation(animationDecider.getOnAttackCalled(), animParams, opponent, decisionData);
    }
    
    public void nextDashAnimation(UpdateLoop onUpdate, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        //System.out.println("collision so dash isnt needed? " + sprite.collidesWith(fromOpponent.getSprite()));
        if (animationDecider.getOnDashCalled() != null && !sprite.collidesWith(opponent)) {
            callAnimation(
                animationDecider.getOnDashCalled(), 
                new AnimationParams(
                    onUpdate,
                    (enemySprite) -> {
                        return sprite.collidesWith(enemySprite);
                    }
                ),
                opponent,
                decisionData
            );
        }
    }
    
    public void nextReceiveImpactAnimation(AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) { 
        if (decisionData.getStrikeReel().getCurrentStrike().didHit()) { //assumes this unit is the victim for this strike
            callAnimation(animationDecider.getOnGotHitCalled(), animParams, opponent, decisionData);
        } else {
            callAnimation(animationDecider.getOnDodgeCalled(), animParams, opponent, decisionData);
        }
    }
    
    public void nextIdleAnimation(AnimationParams animParams, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        callAnimation(animationDecider.getOnIdleCalled(), animParams, opponent, decisionData);
    }
    
    public void update(float tpf) {
        currentAnimationQueue.update(tpf); //this MUST be after the onStrikeFinished
    }
    
    
    public static class AnimationParams {
        public static final UpdateLoop IDLE_UPDATE = (tpf) -> {};
        public static final Predicate<BattleSprite> END_WITH_LAST_FRAME = (enemySprite) -> { return true; };  
        
        public final UpdateLoop onUpdate;
        public final Predicate<BattleSprite> secondEndCondition;
        
        public AnimationParams(UpdateLoop onUpdate, Predicate<BattleSprite> secondEndCondition) {
            this.onUpdate = onUpdate;
            this.secondEndCondition = secondEndCondition;
        }
        
        public AnimationParams(UpdateLoop onUpdate) {
            this.onUpdate = onUpdate;
            secondEndCondition = END_WITH_LAST_FRAME;
        }
        
        public AnimationParams(Predicate<BattleSprite> secondEndCondition) {
            onUpdate = IDLE_UPDATE;
            this.secondEndCondition = secondEndCondition;
        }
        
        public AnimationParams() {
            onUpdate = IDLE_UPDATE;
            secondEndCondition = END_WITH_LAST_FRAME;
        }
    }
}
