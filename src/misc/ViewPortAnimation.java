/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import general.visual.animation.VisualTransition.Progress;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author night
 */
public class ViewPortAnimation {
    private Camera cam;
    private Progress transitionProgress = Progress.Fresh;
    private List<ViewPortTransition> transitions;
    private boolean orderMatters = false;
    
    public ViewPortAnimation(Camera cam) {
        this.cam = cam;
    }
    
    public ViewPortAnimation(Camera cam, List<ViewPortTransition> transitions, boolean orderMatters) {
        this.cam = cam;
        this.transitions = transitions;
        this.orderMatters = orderMatters;
    }
    
    public ViewPortAnimation setOrderMatters(boolean orderDoesMatter) {
        orderMatters = orderDoesMatter;
        return this;
    }
    
    public boolean getOrderMatters() { return orderMatters; }
    
    public void update(float tpf) {
        if (transitionProgress == Progress.Progressing) {
            int fin = 0;
            for (int i = 0; i < transitions.size(); i++) {
                
                if (!orderMatters || (orderMatters && i > 0 && transitions.get(i - 1).getProgress() == Progress.Finished)) {
                    transitions.get(i).update(tpf);
                }
                
                if (transitions.get(i).getProgress() == Progress.Finished) {
                    fin++;
                }
            }
            if (fin == transitions.size()) {
                transitionProgress = Progress.Finished;
            }
        }
    }
    
    public void setTransitionProgress(Progress progress) {
        transitionProgress = progress;
    }
    
    public void beginTransitions() {
        transitionProgress = Progress.Progressing;
        for (ViewPortTransition transition : transitions) {
            transition.setProgress(Progress.Progressing);
            transition.setCamera(cam);
        }
    }
    
    public void beginTransitions(List<ViewPortTransition> appliedTransitions) {
        transitions = appliedTransitions;
        beginTransitions();
    }
    
    public Progress getTransitionProgress() { return transitionProgress; }
    
    
    public static ViewPortTransition horizontalCut() {
        return new ViewPortTransition() {
            @Override
            public void updateTransition(float tpf) {
                camera.setViewPort(0.0f, counter / frameLength, 0.45f, 0.55f);
            }
        }.setEndDelay(0.25f).setLength(0.5f);
    }
    
    public static ViewPortTransition openCut() {
        return new ViewPortTransition() {
            @Override
            public void updateTransition(float tpf) {
                float bottomView = 0.45f - (counter / (FastMath.pow(0.45f, -1) * frameLength)), topView = 0.55f + (counter / (FastMath.pow(0.55f, -1) * frameLength));
                camera.setViewPort(0.0f, 1.0f, bottomView, topView);
            }
        }.setEndDelay(0.25f).setLength(1f);
    }
    
    public static ViewPortAnimation cutOpen(Camera camera) {
        return new ViewPortAnimation(camera, Arrays.asList(horizontalCut(), openCut()), true);
    }
    
    public static ViewPortAnimation poolIn(Camera camera) {
        return new ViewPortAnimation(camera, Arrays.asList(horizontalCut(), openCut()), false);
    }
    
}
