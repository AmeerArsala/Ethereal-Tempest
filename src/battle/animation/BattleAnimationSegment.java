/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import com.jme3.scene.Node;
import com.simsilica.lemur.Command;
import java.util.List;

/**
 *
 * @author night
 */
public class BattleAnimationSegment {
    private final List<VisibleEntityAnimation> entityAnimations; //the last one is the one with the impact
    private final boolean concurrent; //if true, plays all at the same time
    private final boolean isAttack;
    
    private int animationIndex = 0;
    private Runnable onRealImpactOccurred = () -> {};
    
    public BattleAnimationSegment(List<VisibleEntityAnimation> entityAnimations, boolean concurrent, boolean isAttack) {
        this.entityAnimations = entityAnimations;
        this.concurrent = concurrent;
        this.isAttack = isAttack;
        
        for (VisibleEntityAnimation anim : entityAnimations) {
            System.out.println(anim.toString());
        }
    }
    
    public List<VisibleEntityAnimation> getEntityAnimations() {
        return entityAnimations;
    }
    
    public boolean isConcurrent() {
        return concurrent;
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
        VisibleEntityAnimation lastAnimation = entityAnimations.get(entityAnimations.size() - 1);
        
        if (!concurrent) {
            VisibleEntityAnimation entityAnimation = firstUnfinishedAnimation();
            return entityAnimation == null ? false : entityAnimation.impactOccured();
        }
        
        //if it is concurrent
        return lastAnimation.impactOccured(); //real impacts only occur on the last animation on concurrent animations
    }
    
    public void onRealImpactOccurred(Runnable prcdr) {
        onRealImpactOccurred = prcdr;
    }
    
    private void procedure(Command<VisibleEntityAnimation> command) {
        if (concurrent) {
            for (VisibleEntityAnimation entityAnimation : entityAnimations) {
                command.execute(entityAnimation);
            }
        } else {
            command.execute(entityAnimations.get(animationIndex));
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
