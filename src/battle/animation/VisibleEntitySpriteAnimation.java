/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import battle.animation.config.AttackSheetConfig;
import battle.animation.config.EntityAnimation;
import battle.participant.visual.BattleSprite;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import general.visual.Sprite;

/**
 *
 * @author night
 */
public class VisibleEntitySpriteAnimation extends VisibleEntityAnimation<BattleSprite> {
    //sprite is NOT bound to this class and only needs to be passed in as a pre-existing Object; otherwise, do whatever you want with the Sprite
    //private final AssetManager assetManager;
    
    public VisibleEntitySpriteAnimation(EntityAnimation config, SpriteAnimationParams params) {
        super(config, params.userSprite, params.opponentSprite, params.secondEndAnimationCondition, params.mirror);
        //assetManager = params.assetManager;
    }
    
    /*private void initialize(AssetManager assetManager) {
        
    }*/

    @Override
    protected void updateAnimation(float tpf) {
        entityAnimationRoot.root.setSpritesheetPosition(info.getActualFrameAt(currentFrameIndex));
    }
    
    @Override
    protected void mirror() {
        entityAnimationRoot.root.mirror();
        entityAnimationRoot.positiveDirection.set(entityAnimationRoot.root.getPositiveDirection3DVector());
    }

    @Override
    protected void beginAnimation(Node animationRoot) {
        System.out.println("BEGIN SPRITE ANIMATION PHASE: " + info.getJsonPath());
        
        BattleSprite sprite = entityAnimationRoot.root;
        
        /*AttackSheetConfig sheetConfig = info.getConfig().getPossibleSpritesheet();
        
        //entityAnimationRoot.root = sprite (it is of type Sprite)
        String existingPath = sprite.getTexturePath();
        
        if (existingPath == null || !existingPath.equals(sheetConfig.getSpritesheetImagePath())) {
            sprite.setSizeX(sheetConfig.getColumns());
            sprite.setSizeY(sheetConfig.getRows());
            sprite.setSpritesheetTexture(sheetConfig.getSpritesheetImagePath(), assetManager);
            
            if (sprite.hasOverlay()) {
                //TODO: set corresponding overlay spritesheet texture path for overlay
            }
        }*/
        
        if (info.getHitPoint() != null) {
            sprite.setHitPointIfAllowed(info.getHitPoint());
            //System.err.println("NOTICE: HITPOINT MAY HAVE BEEN SET -VisibleEntitySpriteAnimation");
        }
    }
    
    public Sprite getSprite() {
        return entityAnimationRoot.root;
    }
}
