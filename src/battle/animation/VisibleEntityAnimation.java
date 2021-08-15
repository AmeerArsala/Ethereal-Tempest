/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import battle.animation.config.action.ActionFrame;
import battle.animation.config.action.Changes;
import battle.animation.config.EntityAnimation;
import battle.environment.BoxMetadata;
import battle.participant.visual.BattleSprite;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import general.tools.GameTimer;
import general.procedure.functional.SimpleProcedure;
import general.procedure.ProcedureGroup;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author night
 * @param <R> User Spatial: Node, Sprite, GeometryPanel, etc.
 */
public abstract class VisibleEntityAnimation<R extends Spatial> {
    protected final GameTimer timeCounter = new GameTimer(); //time and frames passed since this animation began
    protected final EntityAnimation info;
    
    protected final EntityRoot<R> entityAnimationRoot;
    protected final EntityRoot<BattleSprite> opponentAnimationRoot;
    
    protected int currentFrameIndex = 0; //this is also localFrame
    
    private float elapsedTime = 0f; //elapsed time to match delays
    private boolean allowVisualUpdate = true;
    
    private final boolean doMirror;
    private final Predicate<BattleSprite> secondEndCondition; //such as waiting for the opponent's "getting hurt" animation to finish
    
    private boolean finished = false;
    
    public VisibleEntityAnimation(EntityAnimation config, RootPackage<R> root, RootPackage<BattleSprite> opponentRoot, Predicate<BattleSprite> otherEndCondition, boolean mirror) {
        info = config;
        doMirror = mirror;
        entityAnimationRoot = new EntityRoot<>(root, false);
        opponentAnimationRoot = new EntityRoot<>(opponentRoot, true);
        secondEndCondition = otherEndCondition;
    }
    
    public EntityAnimation getInfo() { return info; }
    public int getCurrentFrameIndex() { return currentFrameIndex; }
    public boolean doesMirror() { return doMirror; }
    public boolean isFinished() { return finished; }
    
    public BoxMetadata getBattleBoxInfo() {
        return opponentAnimationRoot.root.getBattleBoxInfo();
    }
    
    public final void update(float tpf) {
        if (!finished) {
            if (allowVisualUpdate) { //update once and then set false until end of frame because it updates once per frame of animation
                updateAnimation(tpf);
                updateAction(tpf);
                allowVisualUpdate = false;
            }
                
            if (elapsedTime >= info.getDelayAt(currentFrameIndex)) {
                ++currentFrameIndex;
                elapsedTime = 0;
                allowVisualUpdate = true;
            } else {
                elapsedTime += tpf;
            }
            
            timeCounter.update(tpf);
            finished = currentFrameIndex >= info.getFrames() && secondEndCondition.test(opponentAnimationRoot.root);
        }
    }
    
    //sounds and Changes (anything in regards to extra velocity, angular velocity, local scale, and color)
    public final void updateAction(float tpf) {
        List<ActionFrame> actions = info.getActionFramesAt(currentFrameIndex);
        
        for (ActionFrame action : actions) {
            entityAnimationRoot.addChangesToQueueIfAny(action.getUserChanges(), getBattleBoxInfo(), true);        // fromSelf == true
            opponentAnimationRoot.addChangesToQueueIfAny(action.getOpponentChanges(), getBattleBoxInfo(), false); // fromSelf == false
            
            //String sound = action.getSoundPath();
            //TODO: play the sound
        }
        
        entityAnimationRoot.queue.update(tpf);
        opponentAnimationRoot.queue.update(tpf);
    }
    
    public boolean impactOccured() {
        return info.impactOccursAt(currentFrameIndex);
    }
    
    public void reset() {
        finished = false;
        allowVisualUpdate = true;
        elapsedTime = 0f;
        timeCounter.reset();
        info.reset();
    }
    
    public void begin(Node animationRoot) {
        reset();
        if (!animationRoot.hasChild(entityAnimationRoot.root)) {
            animationRoot.attachChild(entityAnimationRoot.root);
        }
        
        if (doMirror) {
            mirror();
        }
        
        beginAnimation(animationRoot);
    }
    
    protected void onPause() {}
    protected void onResume() {}
    protected void onFinish() {}
    
    protected abstract void updateAnimation(float tpf);
    protected abstract void beginAnimation(Node animationRoot);
    protected abstract void mirror();
    
    @Override
    public String toString() {
        return "VisibleEntityAnimation: " + info.getJsonPath() + "; Object String: " + super.toString();
    }
    
    //                        <S> is the User Spatial
    protected class EntityRoot<S extends Spatial> extends RootPackage<S> {
        private final boolean isOpponent;
        public final ProcedureGroup queue = new ProcedureGroup();
        
        public EntityRoot(RootPackage<S> rootPackage, boolean isOpponent) {
            super(rootPackage.root, rootPackage.positiveDirectionVector, rootPackage.centerPointDefault, rootPackage.dimensionsSupplier);
            this.isOpponent = isOpponent;
        }
    
        public void addChangesToQueueIfAny(Changes changes, BoxMetadata battleBoxInfo, boolean fromSelf) {
            queue.add(new SimpleProcedure() {
                private int framesSince = 0;
                
                @Override
                public boolean update(float tpf) {
                    //System.out.println(fromSelf ? "<USER_CHANGES>" : "<OPPONENT_CHANGES>");
                    return changes.generateChangePack(
                        framesSince++, 
                        root,
                        (isOpponent ? entityAnimationRoot.root : opponentAnimationRoot.root),
                        centerPointDefault
                    ).apply(root, battleBoxInfo, positiveDirectionVector, dimensionsSupplier.get(), fromSelf);
                }
            });
        }
    }
}
