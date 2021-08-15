/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config.action;

import battle.environment.BoxMetadata;
import battle.participant.visual.BattleSprite;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import general.visual.sprite.Sprite;

/**
 *
 * @author night
 */
public class ChangePack {
    public final Vector2f centerPoint;
    
    public final Vector3f velocity;
    public final Vector3f thetaVelocity;
    public final Vector3f localScale;
    public final ColorRGBA color;
    public final String colorMatParam;
    
    public ChangePack(Vector2f centerPoint, Vector3f velocity, Vector3f thetaVelocity, Vector3f localScale, ColorRGBA color, String colorMatParam) {
        this.centerPoint = centerPoint;
        this.velocity = velocity;
        this.thetaVelocity = thetaVelocity;
        this.localScale = localScale;
        this.color = color;
        this.colorMatParam = colorMatParam;
    }

    public <S extends Spatial> boolean apply(S root, BoxMetadata battleBoxInfo, Vector3f positiveDirection, Vector3f dimensions, boolean fromSelf) {
        if (velocity == null && thetaVelocity == null && localScale == null && color == null /*&& colorMatParam == null*/) {
            //System.out.println("Attempting to apply ChangePack Changes...CHANGEPACK_END\n");
            
            if (root instanceof BattleSprite && ((BattleSprite)root).isTextureRotating()) {
                BattleSprite sprite = ((BattleSprite)root);
                Material mat = sprite.getMaterial();
                mat.setFloat("Angle", 0f);
                mat.setBoolean("RotationUsesTime", false);
                mat.setBoolean("IsRotating", true);
                sprite.setIsTextureRotating(false);
            }
            
            return true;
        }
        
        //System.out.println("Attempting to apply ChangePack Changes..."); //remove later
        
        if (fromSelf || (root instanceof BattleSprite && ((BattleSprite)root).allowDisplacementTransformationsFromOpponent())) {
            if (velocity != null) {
                float boxWidth = battleBoxInfo.horizontalLength(); //the actual width of the box, not the one specified originally
                root.move(velocity.multLocal(boxWidth).multLocal(positiveDirection)); //multiply by positiveDirection for mirroring
                System.out.println("DeltaPos: " + velocity);
            }
            
            if (thetaVelocity != null) {
                if (root instanceof BattleSprite) {
                    Vector2f centrifuge = new Vector2f(centerPoint);
                    if (positiveDirection.x != Sprite.FACING_LEFT) {
                        centrifuge.x = 1.0f - centrifuge.x;
                    }
                    
                    BattleSprite sprite = ((BattleSprite)root);
                    Material mat = sprite.getMaterial();
                    mat.setVector2("Pivot", centrifuge);
                    mat.setFloat("RotationSpeed", thetaVelocity.z);
                    mat.setBoolean("RotationUsesTime", true);
                    mat.setBoolean("IsRotating", true);
                    
                    root.rotate(thetaVelocity.x, thetaVelocity.y, 0);
                    sprite.setIsTextureRotating(true);
                } else {
                    root.rotate(thetaVelocity.x, thetaVelocity.y, thetaVelocity.z);
                }
                
                System.out.println("DeltaThetas: " + thetaVelocity);
            }
            
            if (localScale != null) {
                root.setLocalScale(localScale);
            }
        }
                    
        if (root instanceof BattleSprite && color != null) {
           ((BattleSprite)root).setColor(colorMatParam, color);
        }
        
        //System.out.println("CHANGEPACK_CONTINUE\n");
        return false;
    }
}
