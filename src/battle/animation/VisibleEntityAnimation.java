/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import battle.animation.config.ActionFrame;
import battle.animation.config.Changes;
import battle.animation.config.EntityAnimation;
import battle.participant.visual.BattleSprite;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import etherealtempest.Globals;
import general.procedure.SimpleProcedure;
import general.procedure.SimpleQueue;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author night
 * @param <R> User Spatial: Node, Sprite, GeometryPanel, etc.
 */
public abstract class VisibleEntityAnimation<R extends Spatial> {
    protected final Globals timeCounter = new Globals();
    protected final EntityAnimation info;
    
    protected final EntityRoot<R> entityAnimationRoot;
    protected final EntityRoot<BattleSprite> opponentAnimationRoot;
    
    protected int currentFrameIndex = 0; //this is also localFrame
    
    private final int frames; //total frame count
    private final boolean doMirror;
    private final Predicate<BattleSprite> secondEndCondition;
    
    private boolean finished = false;
    
    public VisibleEntityAnimation(EntityAnimation config, RootPackage<R> root, RootPackage<BattleSprite> opponentRoot, Predicate<BattleSprite> otherEndCondition, boolean mirror) {
        info = config;
        doMirror = mirror;
        entityAnimationRoot = new EntityRoot<>(root);
        opponentAnimationRoot = new EntityRoot<>(opponentRoot);
        secondEndCondition = otherEndCondition;
        frames = info.getFrames();
    }
    
    public EntityAnimation getInfo() { return info; }
    public int getCurrentFrameIndex() { return currentFrameIndex; }
    public boolean doesMirror() { return doMirror; }
    public boolean isFinished() { return finished; }
    
    private float elapsedTime = 0f;
    
    public final void update(float tpf) {
        if (!finished) {
            if (currentFrameIndex >= frames || elapsedTime >= info.getDelayAt(currentFrameIndex)) {
                updateAnimation(tpf);
                updateAction(tpf);
                ++currentFrameIndex;
                elapsedTime = 0;
                finished = currentFrameIndex == frames && secondEndCondition.test(opponentAnimationRoot.root);
            }
            
            elapsedTime += tpf;
            timeCounter.update(tpf);
        }
    }
    
    //sounds and Changes (anything in regards to extra velocity, color, and angular velocity
    public final void updateAction(float tpf) {
        List<ActionFrame> actions = info.getActionFramesAt(currentFrameIndex);
        
        for (ActionFrame action : actions) {
            entityAnimationRoot.addChangesToQueueIfAny(action.getUserChanges(), true); //fromSelf == true
            opponentAnimationRoot.addChangesToQueueIfAny(action.getOpponentChanges(), false); //fromSelf == false
            
            String sound = action.getSoundPath();
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
        elapsedTime = 0f;
        timeCounter.reset();
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
}

// <S> is the User Spatial
class EntityRoot<S extends Spatial> {
    public final S root;
    public final SimpleQueue queue = new SimpleQueue();
    public final Vector3f positiveDirection;
    
    private ColorRGBA currentColor = null;
        
    public EntityRoot(RootPackage<S> rootPackage) {
        root = rootPackage.root;
        positiveDirection = rootPackage.positiveDirectionVector;
    }
    
    public void setPositiveDirection(int x, int y, int z) { //all parameters are either 1 or -1
        positiveDirection.set(x, y, z);
    }
    
    public void setPositiveDirection(boolean x, boolean y, boolean z) {
        int $x = x ? 1 : -1;
        int $y = y ? 1 : -1;
        int $z = z ? 1 : -1;
        
        setPositiveDirection($x, $y, $z);
    }
        
    private boolean updateChanges(Vector3f velocity, Vector3f angularVelocity, Vector3f localScale, ColorRGBA color, String colorMatParam, boolean fromSelf) {
        boolean rootIsBattleSprite = root instanceof BattleSprite;
        
        if (velocity == null && angularVelocity == null && localScale == null && color == null) {
            if (rootIsBattleSprite && currentColor != null) {
                //reset the color
                ColorRGBA reset = new ColorRGBA(1f / currentColor.r, 1f / currentColor.g, 1f / currentColor.b, 1f / currentColor.a);
                ((BattleSprite)root).setColor(colorMatParam, reset);
                currentColor = ColorRGBA.White;
            }
                
            return true;
        }
        
        if (fromSelf || (rootIsBattleSprite && ((BattleSprite)root).allowDisplacementTransformationsFromOpponent())) {
            if (velocity != null) {
                root.move(velocity.multLocal(positiveDirection)); //multiply by positiveDirection for mirroring
            }
        
            if (angularVelocity != null) {
                root.rotate(angularVelocity.x, angularVelocity.y, angularVelocity.z);
            }
        
            if (localScale != null) {
                root.setLocalScale(localScale);
            }
        }
                    
        if (rootIsBattleSprite && color != null) {
           ((BattleSprite)root).setColor(colorMatParam, color);
            currentColor = color;
        }
    
        return false;
    }
    
    public void addChangesToQueueIfAny(Changes changes, boolean fromSelf) {
        queue.addToQueue(new SimpleProcedure() {
            private int framesSince = 0;
                
            @Override
            public boolean update(float tpf) {
                Vector3f velocity = changes.getVelocity(framesSince);
                Vector3f angularVelocity = changes.getVelocity(framesSince);
                Vector3f localScale = changes.getLocalScale(framesSince);
                ColorRGBA color = changes.getColor(framesSince);
                String colorMatParam = changes.getColorMatParam(framesSince);
                
                boolean done = updateChanges(velocity, angularVelocity, localScale, color, colorMatParam, fromSelf);
                    
                ++framesSince;
                return done;
            }
        });
    }
    
    public void addChangesToQueueIfAny(ActionFrame action, boolean isUser, boolean fromSelf) {
        Changes changes = isUser ? action.getUserChanges() : action.getOpponentChanges();
        addChangesToQueueIfAny(changes, fromSelf);
    }
}
