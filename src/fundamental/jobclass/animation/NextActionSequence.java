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
        
        int i = 0;
        do {
            List<VisibleEntityAnimation> animations = new ArrayList<>();
            
            NextAction nextAction = nextActions.get(i);
            boolean concurrent = nextAction.isConcurrent();
            boolean isAttack = nextAction.isAttack();
            animations.add(nextAction.createAnimation(params));
            
            //groups by same concurrency boolean value
            while (i + 1 < nextActions.size() && nextActions.get(i + 1).isConcurrent() == concurrent) {
                animations.add(nextActions.get(i + 1).createAnimation(params));
                isAttack = isAttack || nextActions.get(i + 1).isAttack();
                ++i;
            }
            
            battleAnimationSegments.add(new BattleAnimationSegment(animations, concurrent, isAttack));
            
            ++i;
        } while (i < nextActions.size());
        
        return battleAnimationSegments;
    }
}
