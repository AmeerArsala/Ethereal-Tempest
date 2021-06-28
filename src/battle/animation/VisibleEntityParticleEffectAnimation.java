/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import battle.animation.config.EntityAnimation;
import battle.participant.visual.BattleParticleEffect;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 *
 * @author night
 */
public class VisibleEntityParticleEffectAnimation extends VisibleEntityAnimation<BattleParticleEffect.ParticleRootNode> {
    private final BattleParticleEffect particleEffect;
    
    public VisibleEntityParticleEffectAnimation(EntityAnimation config, SpriteAnimationParams params) {
        super(
            config,
            new RootPackage<BattleParticleEffect.ParticleRootNode>(initializeEffects(config, params.assetManager)), 
            params.opponentSprite, 
            params.secondEndAnimationCondition, 
            params.mirror
        );
        
        particleEffect = info.getConfig().getPossibleParticleEffect();
        particleEffect.getParticleRootNode().setBattleBoxInfo(getBattleBoxInfo());
    }
    
    private static BattleParticleEffect.ParticleRootNode initializeEffects(EntityAnimation config, AssetManager assetManager) {
        config.initializeParticleEffects(assetManager);
        
        BattleParticleEffect particleEffect = config.getConfig().getPossibleParticleEffect();
        
        particleEffect.onEffectStart((tpf) -> {
            if (particleEffect.useDestroyoflyer()) {
                particleEffect.resetManualControl(assetManager);
                particleEffect.getManualControl().setEnabled(true);
            }
        });
        
        return particleEffect.getParticleRootNode();
    }

    @Override
    protected void updateAnimation(float tpf) {
        particleEffect.getParticleRootNode().updateOrientation(opponentAnimationRoot.root.getXFacing());
        particleEffect.update(tpf);
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
        entityAnimationRoot.root.attachChild(particleEffect.getModelRootNode());
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
