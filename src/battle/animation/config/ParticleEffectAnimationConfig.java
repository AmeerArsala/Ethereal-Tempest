/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import general.visual.DeserializedParticleEffect;

/**
 *
 * @author night
 */
public class ParticleEffectAnimationConfig {
    private DeserializedParticleEffect particleEffect;
    private boolean doesLoop;
    
    public ParticleEffectAnimationConfig(DeserializedParticleEffect particleEffect, boolean doesLoop) {
        this.particleEffect = particleEffect;
        this.doesLoop = doesLoop;
    }
    
    public DeserializedParticleEffect getEffect() { return particleEffect; }
    public boolean doesLoop() { return doesLoop; }
}
