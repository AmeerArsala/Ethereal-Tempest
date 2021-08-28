/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fundamental.jobclass.animation;

import battle.animation.BattleAnimation;
import battle.animation.BattleAnimationSegment;
import battle.animation.SpriteAnimationParams;
import battle.animation.VisibleEntityAnimation;
import battle.animation.config.EntityAnimation;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class NextActionSequence {
    private final List<NextAction> nextActions;
    
    public NextActionSequence(List<NextAction> nextActions) {
        this.nextActions = nextActions;
    }
    
    public List<NextAction> getNextActions() { return nextActions; }
    
    public BattleAnimation generateBattleAnimation(SpriteAnimationParams params, Node animationRoot) {
        return new BattleAnimation(animationRoot, generateBattleAnimationSegments(params));
    }
    
    public List<BattleAnimationSegment> generateBattleAnimationSegments(SpriteAnimationParams params) {
        List<BattleAnimationSegment> battleAnimationSegments = new ArrayList<>();
        
        int i = 0, size = nextActions.size();
        do {
            List<VisibleEntityAnimation> animations = new ArrayList<>();
            
            NextAction nextAction = nextActions.get(i);
            boolean simultaneous = false;
            boolean isAttack = nextAction.isAttack();
            animations.add(nextAction.createAnimation(params));
            
            // A BattleAnimationSegment will start with something considered to be 'sequential'
            // 'sequential' does not mean 'withPrevious'; first entry is loosely treated as 'sequential' no matter what its value of playsSimultaneouslyWithPrevious is
            // The following entries will either be all S
            if (i + 1 < size) {
                simultaneous = nextActions.get(i + 1).playsSimultaneouslyWithPrevious();
            }
            
            // Groups by same boolean value of 'simultaneous'
            // For all the following nextActions (if any) which have the boolean value of playsSimultaneouslyWithPrevious == simultaneous, 
            // they are added to the animations to use in one segment
            groupActions: while (i + 1 < size && nextActions.get(i + 1).playsSimultaneouslyWithPrevious() == simultaneous) {
                if (simultaneous == false && (i + 2 < size && nextActions.get(i + 2).playsSimultaneouslyWithPrevious() == true)) {
                    // If simulataneous == false, it means i + 1 is 'sequential'
                    // We want to avoid having a chain of 'sequentials' be followed by a 'withPrevious' (simultaneous == false)
                    // Same with a chain of 'withPrevious' being followed by a 'sequential' (simultaneous == true), but thankfully that'll be handled automatically by this while condition (the last 'withPrevious' can be added)
                    // If a chain of 'sequentials' IS followed by a 'withPrevious', we don't want to add the last 'sequential'
                    
                    break groupActions;
                }
                
                NextAction followingAction = nextActions.get(i + 1);
                animations.add(followingAction.createAnimation(params));
                isAttack = isAttack || followingAction.isAttack();
                ++i;
            }
            
            battleAnimationSegments.add(new BattleAnimationSegment(animations, simultaneous, isAttack));
            
            ++i;
        } while (i < size);
        
        return battleAnimationSegments;
    }
}
