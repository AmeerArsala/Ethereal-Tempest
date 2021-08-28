/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import battle.animation.config.EntityAnimation;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author night
 */
public class BattleAnimationSegment {
    private final List<VisibleEntityAnimation> entityAnimations; //the last one is typically the one with the impact
    private final boolean simultaneous; //if true, plays all at the same time
    private final boolean isAttack;
    
    private int animationIndex = 0;
    private Runnable onRealImpactOccurred = () -> {};
    
    public BattleAnimationSegment(List<VisibleEntityAnimation> entityAnimations, boolean simultaneous, boolean isAttack) {
        this.entityAnimations = entityAnimations;
        this.simultaneous = simultaneous;
        this.isAttack = isAttack;
        
        for (int i = 0, len = entityAnimations.size(); i < len; ++i) {
            VisibleEntityAnimation anim = entityAnimations.get(i);
            EntityAnimation data = anim.getInfo();
            switch (data.getTag()) {
                case LinksToNextHitPoint:
                    Vector2f hitPoint = data.getHitPoint();
                    if (i + 1 < len && hitPoint != null) {
                        hitPoint.set(entityAnimations.get(i + 1).getInfo().getHitPoint());
                    }
                    break;
                case Attack:
                    //TODO: this
                    break;
                case ChainLink:
                    //TODO: this; see description on the comment next to the original declaration of the enum
                    break;
                case Default:
                    //TODO: this
                    break;
            }
            
            //System.out.println(anim.toString());
        }
    }
    
    public List<VisibleEntityAnimation> getEntityAnimations() {
        return entityAnimations;
    }
    
    public boolean isSimultaneous() {
        return simultaneous;
    }
    
    public boolean isAttack() {
        return isAttack;
    }
    
    public boolean isFinished() {
        return firstUnfinishedAnimation() == null;
    }
    
    public VisibleEntityAnimation firstUnfinishedAnimation() {
        for (VisibleEntityAnimation VEA : entityAnimations) {
            if (!VEA.isFinished()) {
                return VEA;
            }
        }
        
        return null;
    }
    
    public boolean realImpactOccurred() {
        if (!simultaneous) {
            VisibleEntityAnimation entityAnimation = firstUnfinishedAnimation();
            return entityAnimation == null ? false : entityAnimation.impactOccured();
        } else {
            for (VisibleEntityAnimation VEA : entityAnimations) {
                if (!VEA.isFinished() && VEA.impactOccured()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public void onRealImpactOccurred(Runnable prcdr) {
        onRealImpactOccurred = prcdr;
    }
    
    private void procedure(Consumer<VisibleEntityAnimation> command) {
        if (simultaneous) {
            for (VisibleEntityAnimation entityAnimation : entityAnimations) {
                command.accept(entityAnimation);
            }
        } else {
            command.accept(entityAnimations.get(animationIndex));
        }
    }
    
    public void onStart(Node animationRoot) {
        animationIndex = 0;
        procedure((entityAnimation) -> {
            entityAnimation.begin(animationRoot);
        });
    }
    
    public void onPause() {
        procedure((entityAnimation) -> {
            entityAnimation.onPause();
        });
    }
    
    public void onResume() {
        procedure((entityAnimation) -> {
            entityAnimation.onResume();
        });
    }
    
    public void update(float tpf) {
        if (!isFinished()) {
            procedure((entityAnimation) -> {
                entityAnimation.update(tpf);
                
                if (realImpactOccurred()) {
                    System.err.println("onRealImpactOccurred (impact occurs here)");
                    onRealImpactOccurred.run();
                }
                
                listenForFinish(entityAnimation);
            });
        }
    }
    
    private void listenForFinish(VisibleEntityAnimation entityAnimation) {
        if (entityAnimation.isFinished()) {
            entityAnimation.onFinish();
            ++animationIndex;
        }
    }
}
