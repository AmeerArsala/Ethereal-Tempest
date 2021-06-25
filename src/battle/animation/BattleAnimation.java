/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import com.jme3.scene.Node;
import general.procedure.OrdinalQueue;
import java.util.List;

/**
 *
 * @author night
 */
public class BattleAnimation {
    private final Node animationRoot;
    private final List<BattleAnimationSegment> segments;
    
    private int index = 0;
    private boolean paused = true;
    
    //use this constructor if you are already using a pre-existing Node
    public BattleAnimation(Node animationRoot, List<BattleAnimationSegment> segments) {
        this.animationRoot = animationRoot;
        this.segments = segments;
    }
    
    public Node getNode() {
        return animationRoot;
    }
    
    public int getIndex() {
        return index;
    }
    
    public int getSegmentCount() {
        return segments.size();
    }
    
    public BattleAnimationSegment getCurrentSegment() {
        return segments.get(index);
    }
    
    public boolean isFinished() {
        return index >= segments.size() || (index == segments.size() - 1 && segments.get(index).isFinished());
    }
    
    public boolean isPaused() { 
        return paused; 
    }
    
    public void pause() {
        paused = true; 
        segments.get(index).onPause();
    }
    
    public void resume() { 
        paused = false;
        segments.get(index).onResume();
    }
    
    public void startAnimation() {
        reset();
        segments.get(index).onStart(animationRoot);
    }
    
    public void reset() {
        index = 0;
        paused = false;
    }
    
    public void update(float tpf) {
        if (!paused) {
            segments.get(index).update(tpf);
            
            //check if finished
            if (segments.get(index).isFinished()) {
                ++index;
            }
        }
    }
    
    public boolean realImpactOccurred() {
        return segments.get(index).realImpactOccurred();
    }
    
    public boolean isStrikeFinished() {
        return segments.get(index).isAttack() && segments.get(index).isFinished();
    }
    
    //counts the remaining battle segments where 'isAttack' == true, starting from and including the current index
    public int getRemainingAttackBattleSegmentCount() {
        int remaining = 0;
        for (int i = index; i < segments.size(); ++i) {
            if (segments.get(i).isAttack()) {
                ++remaining;
            }
        }
        
        return remaining;
    }
    
    
    public static class Queue extends OrdinalQueue<BattleAnimation> {
        private boolean started = false;
        
        public Queue() {
            super(
                (animation, tpf) -> { animation.update(tpf); },
                (animation) -> { return animation.isFinished(); }
            );
        }
        
        public void startCurrentAnimationIfNotAlready() {
            if (!started) {
                getCurrentTask().startAnimation();
                started = true;
            }
        }
        
        public void setPaused(boolean paused) {
            if (paused) {
                getCurrentTask().pause();
            } else {
                getCurrentTask().resume();
            }
        }
        
        public void resetStarted() { 
            started = false; 
        }
        
        public void resetCurrentAnimation() {
            getCurrentTask().reset();
        }
        
        public boolean realImpactOccurred() {
            BattleAnimation current = getCurrentTask();
            return current != null ? current.realImpactOccurred() : false;
        }
    }
}
