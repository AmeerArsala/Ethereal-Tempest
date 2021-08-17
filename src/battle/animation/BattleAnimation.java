/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import general.procedure.OrdinalQueue;
import java.util.List;

/**
 *
 * @author night
 */
public class BattleAnimation {
    private final Node animationRoot;
    private final List<BattleAnimationSegment> segments; //do not make a get method of this
    
    private int index = 0;
    private boolean paused = true;
    
    private Runnable onStrikeFinished = () -> {};
    
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
            BattleAnimationSegment segment = segments.get(index);
            segment.update(tpf);
            
            //Check if the segment is finished. If so, increment segment index. If it was an attack, call onStrikeFinished
            if (segment.isFinished()) {
                ++index;
                if (segment.isAttack()) {
                    System.err.println("onStrikeFinished");
                    onStrikeFinished.run();
                }
            }
        }
    }
    
    public void onStrikeFinished(Runnable procedure) {
        onStrikeFinished = procedure;
    }
    
    public void onRealImpactOccurred(Runnable procedure) {
        for (BattleAnimationSegment segment : segments) {
            segment.onRealImpactOccurred(procedure);
        }
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
    
    public boolean isAttack() {
        for (int i = 0, len = segments.size(); i < len; ++i) {
            if (segments.get(i).isAttack()) {
                return true;
            }
        }
        
        return false;
    }
    
    //gets hitpoint of the very first animation in the very first segment
    public Vector2f getVeryFirstHitPoint() {
        return segments.get(0).getEntityAnimations().get(0).getInfo().getHitPoint();
    }
    
    
    public static class Queue extends OrdinalQueue<BattleAnimation> {
        private boolean started = false;
        private Runnable onStrikeFinished = () -> {};
        private Runnable onRealImpactOccurred = () -> {};
        
        public Queue() {
            super(
                (animation, tpf) -> { animation.update(tpf); },
                (animation) -> { return animation.isFinished(); }
            );
        }
        
        public void onStrikeFinished(Runnable userProcedure, Runnable opponentProcedure) {
            onStrikeFinished = () -> {
                userProcedure.run();
                opponentProcedure.run();
            };
        }
        
        public void onRealImpactOccurred(Runnable userProcedure, Runnable opponentProcedure) {
            onRealImpactOccurred = () -> {
                userProcedure.run();
                opponentProcedure.run();
            };
        }
        
        public void startCurrentAnimationIfNotAlready() {
            if (!started) {
                BattleAnimation animation = getCurrentTask();
                animation.onStrikeFinished(onStrikeFinished);
                animation.onRealImpactOccurred(onRealImpactOccurred);
                
                animation.startAnimation();
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
    }
}
