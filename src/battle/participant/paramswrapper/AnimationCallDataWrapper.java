/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant.paramswrapper;

import battle.animation.BattleAnimation;
import battle.animation.SpriteAnimationParams;
import com.jme3.scene.Node;
import fundamental.jobclass.animation.NextActionSequence;

/**
 *
 * @author night
 */
public class AnimationCallDataWrapper {
    public final SpriteAnimationParams params;
    public final Node animationRoot;
    public final NextActionSequence nextSequence;
    public final BattleAnimation animation;

    public AnimationCallDataWrapper(SpriteAnimationParams params, Node animationRoot, NextActionSequence nextSequence, BattleAnimation animation) {
        this.params = params;
        this.animationRoot = animationRoot;
        this.nextSequence = nextSequence;
        this.animation = animation;
    }
}
