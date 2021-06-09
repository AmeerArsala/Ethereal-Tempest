/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.destroflyer.jme3.effekseer.model.ParticleEffect;

/**
 *
 * @author night
 */

public class ParticleEffectBeta {
    private String directory;
    private String fileName;
    private ParticleEffect particleEffect;
    private ParticleEffectInfoBeta info;
    
    public ParticleEffectBeta directory(String directory) {
        this.directory = directory;
        return this;
    }
    
    public ParticleEffectBeta fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    
    public ParticleEffectBeta particleEffect(ParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
        return this;
    }
    
    public ParticleEffectBeta info(ParticleEffectInfoBeta info) {
        this.info = info;
        return this;
    }
    
    public String getDirectory() {
        return directory;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }
    
    public ParticleEffectInfoBeta getInfo() {
        return info;
    }
}
