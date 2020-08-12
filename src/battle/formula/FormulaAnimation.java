/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.formula;

/**
 *
 * @author night
 */
public class FormulaAnimation {
    private String filepath;
    private String soundpath;
    private String impactSoundpath;
    private int frames;
    private int impactFrame;
    
    public FormulaAnimation(String filepath, String soundpath, String impactSoundpath, int frames, int impactFrame) {
        this.filepath = filepath;
        this.soundpath = soundpath;
        this.impactSoundpath = impactSoundpath;
        this.frames = frames;
        this.impactFrame = impactFrame;
    }
    
    public String getFilePath() {
        return filepath;
    }
    
    public String getSoundpath() {
        return soundpath;
    }
    
    public String getImpactSoundpath() {
        return impactSoundpath;
    }
    
    public int getFrames() {
        return frames;
    }
    
    public int getImpactFrame() {
        return impactFrame;
    }
}

