/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.jme3.scene.Spatial;
import general.visual.VisualTransition.Progress;

/**
 *
 * @author night
 */
public abstract class Transition {
    protected Progress trProgress = Progress.Fresh;
    protected Spatial target;
    protected float endVal = -1, scaled = 0, counter = 0, maxLength, coefficient; //half a second by default
    
    public abstract int getID();
    public abstract void update(float tpf);
    
    public void update(Spatial focus, float tpf) {
        if (trProgress == Progress.Progressing) {
            if (target != focus) {
                target = focus;
            }
            
            update(tpf);
            
            if (counter >= maxLength) {
                trProgress = Progress.Finished;
                //System.out.println("Transition Finished.");
            }
            
            counter += tpf;
        }
    }
    
    public Transition setTargetSpatial(Spatial tgt) {
        target = tgt;
        return this;
    }
    
    public Transition setLength(float seconds) {
        maxLength = seconds;
        return this;
    }
    
    public Transition setStartingIndexScale(float sc) {
        scaled = sc;
        return this;
    }
    
    public Transition setEndVal(float end) {
        endVal = end;
        return this;
    }
    
    public float getLength() {
        return maxLength;
    }
    
    public float getNextScale() {
        return coefficient * maxLength;
    }
    
    public void setProgress(Progress P) {
        trProgress = P;
        if (P == Progress.Progressing) {
            coefficient = maxLength * scaled;
        }
    }
    
    public Progress getProgress() { return trProgress; }
    public Spatial getTargetSpatial() { return target; }
}
