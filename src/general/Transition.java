/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Panel;
import general.VisualTransition.Progress;

/**
 *
 * @author night
 */
public abstract class Transition {
    protected Progress trProgress = Progress.Fresh;
    protected Spatial target;
    protected float endVal = -1, penny = 0, maxFrame = 30, scaled = 0, coefficient; //half a second by default
    
    public abstract int getID();
    public abstract void update();
    
    public void update(Spatial focus) {
        if (trProgress == Progress.Progressing) {
            if (target != focus) {
                target = focus;
            }
            update();
            penny++;
            if (endVal > 0) {
                if (penny == maxFrame /*|| coefficient == endVal * maxFrame * scaled*/) {
                    trProgress = Progress.Finished;
                    penny = 0;
                }
            } else {
                if (coefficient % maxFrame == 0) {
                    trProgress = Progress.Finished;
                    penny = 0;
                    //System.out.println("transition finished");
                }
            }
        }
    }
    
    public Transition setTargetSpatial(Spatial tgt) {
        target = tgt;
        return this;
    }
    
    public Transition setLength(float seconds) {
        maxFrame = seconds * 60f;
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
        return maxFrame / 60f;
    }
    
    public float getNextScale() {
        return coefficient * (1f / maxFrame);
    }
    
    public void setMaxFrame(float max) { maxFrame = max; }
    
    public void setProgress(Progress P) {
        trProgress = P;
        if (P == Progress.Progressing) {
            coefficient = maxFrame * scaled;
        }
    }
    
    public Progress getProgress() { return trProgress; }
    public Spatial getTargetSpatial() { return target; }
}
