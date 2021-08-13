/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant;

import battle.animation.BattleAnimation;
import battle.animation.SpriteAnimationParams;
import battle.data.CombatFlowData;
import battle.participant.paramswrapper.AnimationArgsWrapper;
import battle.participant.paramswrapper.AnimationCallDataWrapper;
import battle.participant.paramswrapper.DashAnimationArgsWrapper;
import battle.participant.visual.BattleSprite;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
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
    
    private AnimationCallDataWrapper createAnimation(ActionDecider.Procedure next, AnimationArgsWrapper args) {
        SpriteAnimationParams params = new SpriteAnimationParams(sprite, args.opponent, assetManager, args.animParams.secondEndCondition);
        Node animationRoot = sprite.getParent();
        NextActionSequence nextSequence = next.run(args.decisionData);
        BattleAnimation animation = nextSequence.generateBattleAnimation(params, animationRoot);
        
        return new AnimationCallDataWrapper(params, animationRoot, nextSequence, animation);
    }
    
    private void callAnimation(AnimationCallDataWrapper data, AnimationArgsWrapper args) {
        currentAnimationQueue.addToQueue(
            data.animation, 
            args.animParams.onUpdate,
            () -> { //onStart
                //TODO: do something here
            },
            () -> { //onFinish
                currentAnimationQueue.resetStarted();
            }
        );
    }
    
    private void callAnimation(ActionDecider.Procedure next, AnimationArgsWrapper args) {
        callAnimation(createAnimation(next, args), args);
    }
    
    private void callAttributeAnimation(ActionDecider.AttributeAnimation next, DashAnimationArgsWrapper args) {
        if (next.usesDash()) { //uses default dash
            nextDashAnimation(args.onDashUpdate, args.opponent, args.decisionData);
        }
        
        callAnimation(next.getOnCall(), args); //specify movement in the json file
    }
    
    public void nextSkillAttackAnimation(String name, DashAnimationArgsWrapper args) {
        callAttributeAnimation(animationDecider.getOnSkillAttackCalled(name), args);
    }
    
    public void nextBattleTalentAttackAnimation(String name, DashAnimationArgsWrapper args) {
        callAttributeAnimation(animationDecider.getOnBattleTalentAttackCalled(name), args);
    }
    
    public void nextAttackAnimation(DashAnimationArgsWrapper args) {
        if (sprite.usesHitPoint()) { //uses dash in that case
            nextDashAnimation(args.onDashUpdate, args.opponent, args.decisionData);
        }
        
        callAnimation(animationDecider.getOnAttackCalled(), args);
    }
    
    public void nextDashAnimation(UpdateLoop onUpdate, BattleSprite opponent, CombatFlowData.Representative decisionData) {
        //System.out.println("collision so dash isnt needed? " + sprite.collidesWith(fromOpponent.getSprite()));
        if (animationDecider.getOnDashCalled() != null) {
            AnimationArgsWrapper args = new AnimationArgsWrapper(
                new AnimationParams(
                    onUpdate,
                    (foeSprite) -> {
                        return sprite.collidesWith(foeSprite);
                    }
                ),
                opponent,
                decisionData
            );
            
            AnimationCallDataWrapper callData = createAnimation(animationDecider.getOnDashCalled(), args);
            
            Vector2f hitPointInSpritePercents = callData.animation.getVeryFirstHitPoint();
            if (!opponent.collidesWith(sprite.new Point(hitPointInSpritePercents).toBattleBoxPercentage())) {
                callAnimation(callData, args);
            }
        }
    }
    
    public void nextReceiveImpactAnimation(AnimationArgsWrapper args) { 
        if (args.decisionData.getStrikeReel().getCurrentStrike().didHit()) { //assumes this unit is the victim for this strike
            callAnimation(animationDecider.getOnGotHitCalled(), args);
        } else {
            callAnimation(animationDecider.getOnDodgeCalled(), args);
        }
    }
    
    public void nextIdleAnimation(AnimationArgsWrapper args) {
        callAnimation(animationDecider.getOnIdleCalled(), args);
    }
    
    public void update(float tpf) {
        currentAnimationQueue.update(tpf); //this MUST be after the onStrikeFinished
    }
    
    
    public static class AnimationParams {
        public static final UpdateLoop IDLE_UPDATE = (tpf) -> {};
        public static final Predicate<BattleSprite> END_WITH_LAST_FRAME = (foeSprite) -> { return true; };  
        
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
