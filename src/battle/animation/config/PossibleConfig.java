/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import battle.participant.visual.BattleParticleEffect;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author night
 * 
 * ONLY DESERIALIZE ONE OF THEM, NOT BOTH
 * 
 */
public class PossibleConfig {
    private AttackSheetConfig spritesheet; //this will stay null if this animation is not from a spritesheet
    
    //if this is not null, it will already be configured
    private BattleParticleEffect particleEffect; //this will stay null if this animation is not from a particle effect
    
    public PossibleConfig(AttackSheetConfig spritesheet, BattleParticleEffect particleEffect) {
        this.spritesheet = spritesheet;
        this.particleEffect = particleEffect;
    }
    
    public PossibleConfig(AttackSheetConfig spritesheet) {
        this.spritesheet = spritesheet;
    }
    
    public PossibleConfig(BattleParticleEffect particleEffect) {
        this.particleEffect = particleEffect;
    }
    
    public AttackSheetConfig getPossibleSpritesheet() {
        return spritesheet;
    }
    
    public BattleParticleEffect getPossibleParticleEffect() {
        return particleEffect;
    }
    
    public static PossibleConfig deserialize(String jsonPath) { //example would be: "Sprites\\Battle\\Freeblade\\offense\\sword\\config.json"
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\" + jsonPath));
            
            return gson.fromJson(reader, PossibleConfig.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
