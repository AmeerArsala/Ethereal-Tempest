/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

/**
 *
 * @author night
 */

public class ParticleEffectInfoBeta {
    private int quality = 0;
    private float frameLength = (1f / 48f);
    
    public ParticleEffectInfoBeta quality(int quality) {
        this.quality = quality;
        return this;
    }
    
    public ParticleEffectInfoBeta frameLength(float frameLength) {
        this.frameLength = frameLength;
        return this;
    }
    
    public int getQuality() {
        return quality;
    }
    
    public float getFrameLength() {
        return frameLength;
    }
}
