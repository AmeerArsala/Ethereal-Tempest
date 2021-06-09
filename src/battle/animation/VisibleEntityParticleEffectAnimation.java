/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import battle.animation.config.EntityAnimation;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import general.visual.DeserializedParticleEffect;
import general.visual.animation.VisualTransition.Progress;

/**
 *
 * @author night
 */
public class VisibleEntityParticleEffectAnimation extends VisibleEntityAnimation<Node> {
    private DeserializedParticleEffect particleEffect;
    private boolean doesLoop;
    
    public VisibleEntityParticleEffectAnimation(EntityAnimation config, SpriteAnimationParams params) {
        super(config, new RootPackage<Node>(new Node()), params.opponentSprite, params.secondEndAnimationCondition, params.mirror);
        initializeEffects(params.assetManager);
    }
    
    private void initializeEffects(AssetManager assetManager) {
        info.initializeParticleEffects(assetManager);
        
        particleEffect = info.getConfig().getPossibleParticleEffect().getEffect();
        doesLoop = info.getConfig().getPossibleParticleEffect().doesLoop();
        
        particleEffect.onEffectStart((tpf) -> {
            if (particleEffect.useDestroyoflyer()) {
                particleEffect.resetManualControl(assetManager);
                particleEffect.getManualControl().setEnabled(true);
            }
        });
    }
    
    public boolean doesLoop() {
        return doesLoop;
    }

    @Override
    protected void updateAnimation(float tpf) {
        if (doesLoop || particleEffect.getEffectProgress() != Progress.Finished) {
            particleEffect.update(tpf);
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        particleEffect.resetEffectProgress();
    }
    
    @Override
    protected void mirror() {
    
    }

    @Override
    protected void beginAnimation(Node animationRoot) {
        entityAnimationRoot.root.attachChild(particleEffect.getNode());
    }
    
    @Override
    protected void onPause() {
        if (particleEffect.useDestroyoflyer()) {
            particleEffect.getManualControl().setEnabled(false);
        }
    }
    
    @Override
    protected void onResume() {
        if (particleEffect.useDestroyoflyer()) {
            particleEffect.getManualControl().setEnabled(true);
        }
    }
    
    @Override
    protected void onFinish() {
        entityAnimationRoot.root.removeFromParent();
    }
}
