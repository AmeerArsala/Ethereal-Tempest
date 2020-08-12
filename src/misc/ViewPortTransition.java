/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import com.jme3.renderer.Camera;
import general.VisualTransition.Progress;

/**
 *
 * @author night
 */
public abstract class ViewPortTransition {
    private Progress transitionProgress = Progress.Fresh;
    
    protected Camera camera;
    protected float frameLength = 30, counter= 0, endDelay = 15f;
    
    public void update(float tpf) {
        if (transitionProgress == Progress.Progressing) {
            if (counter <= frameLength) {
                updateTransition(tpf);
            }
            if (counter > frameLength && counter > frameLength + endDelay) {
                setProgress(Progress.Finished);
            } else {
                counter++;
            }
        }
    }
    
    public abstract void updateTransition(float tpf);
    
    public ViewPortTransition setLength(float length) { //in seconds
        frameLength = length * 60;
        return this;
    }
    
    public ViewPortTransition setEndDelay(float length) { //in seconds
        endDelay = length * 60;
        return this;
    }
    
    public void setCamera(Camera C) {
        camera = C;
    }

    public void setProgress(Progress P) {
        transitionProgress = P;
    }
    
    public Progress getProgress() { return transitionProgress; }
    
    
}
