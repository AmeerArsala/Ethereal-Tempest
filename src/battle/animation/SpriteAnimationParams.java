/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import battle.participant.visual.BattleSprite;
import com.jme3.asset.AssetManager;
import java.util.function.Predicate;

/**
 *
 * @author night
 */
public class SpriteAnimationParams {
    public final RootPackage<BattleSprite> userSprite, opponentSprite;
    public final AssetManager assetManager;
    public final Predicate<BattleSprite> secondEndAnimationCondition;
    public boolean mirror = false; //NextAction sets this
    
    public SpriteAnimationParams(BattleSprite userSprite, BattleSprite opponentSprite, AssetManager assetManager, Predicate<BattleSprite> secondEndAnimationCondition) {
        this.userSprite = new RootPackage<>(userSprite, userSprite.getPositiveDirection3DVector());
        this.opponentSprite = new RootPackage<>(opponentSprite, opponentSprite.getPositiveDirection3DVector());
        this.assetManager = assetManager;
        this.secondEndAnimationCondition = secondEndAnimationCondition;
    }
}
