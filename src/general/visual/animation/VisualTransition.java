/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual.animation;

import com.jme3.scene.Spatial;
//import general.ui.menu.Menu.TransitionType;

/**
 *
 * @author night
 */
public class VisualTransition {
    private Spatial focus;
    
    private Animation[] appliedTransitions = new Animation[0];
    private Progress transitionProgress = Progress.Fresh;
    //private TransitionType transitionDirection = TransitionType.None;

    private Runnable resetSequence;
    
    public enum Progress {
        Fresh,
        Progressing,
        Finished
    }
    
    //for first parameter: either do Geometry, Panel, or Container
    public VisualTransition(Spatial S, Animation... transitions) { //use Arrays.asList(...) for the 2nd parameter
        focus = S;
        appliedTransitions = transitions;
    }
    
    public VisualTransition(Spatial S) {
        focus = S;
    }
    
    public void update(float tpf) {
        if (transitionProgress == Progress.Progressing) {
            int done = 0;
            for (Animation applied : appliedTransitions) {
                if (applied.getProgress() == Progress.Progressing) {
                    applied.updateAndTrack(tpf, focus);
                } else if (applied.getProgress() == Progress.Finished) {
                    done++;
                }
            }
            if (done == appliedTransitions.length) {
                transitionProgress = Progress.Finished;
                //transitionDirection = TransitionType.None;
                
                System.out.println("Transition Finished");
                if (resetSequence != null) {
                    resetSequence.run();
                }
            }
        }
    }
    
    public void beginTransitions() {
        transitionProgress = Progress.Progressing;
        for (Animation tr : appliedTransitions) {
            tr.setOriginalTargetPosition(focus.getLocalTranslation()); 
            tr.resetTime();
            tr.setProgress(Progress.Progressing);
        }
    }
    
    public void beginTransitions(Animation... transitions) {
        appliedTransitions = transitions;
        beginTransitions();
    }
    
    public Spatial getFocus() { return focus; }
    public Animation[] getTransitions() { return appliedTransitions; }
    
    //public TransitionType getTransitionType() { return transitionDirection; }
    public Progress getTransitionProgress() { return transitionProgress; }
    
    public void setFocus(Spatial f) {
        focus = f;
    }
    
    /*public void setTransitionType(TransitionType TT) {
        transitionDirection = TT;
    }*/
    
    public void setTransitionArray(Animation[] ts) {
        appliedTransitions = ts;
    }
    
    public void setTransitions(Animation... ts) {
        appliedTransitions = ts;
    }
    
    public void setResetProtocol(Runnable RP) { //on finish transitions
        resetSequence = RP;
    }
    
    public void forceEnd() {
        for (Animation transition : appliedTransitions) {
            transition.setLength(transition.getTime());
        }
        
        update(0.01f);
    }
    
    //swaps all transitions' initial and ending values
    public void reverseTransitions() {
        for (Animation transition : appliedTransitions) {
            transition.reverseInitialAndEndVals();
        }
    }
    
}
