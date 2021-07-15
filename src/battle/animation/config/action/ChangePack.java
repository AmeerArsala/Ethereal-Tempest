/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config.action;

import battle.environment.BoxMetadata;
import battle.participant.visual.BattleSprite;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author night
 */
public class ChangePack {
    public final Vector3f velocity;
    public final Vector3f thetaVelocity;
    public final Vector3f localScale;
    public final ColorRGBA color;
    public final String colorMatParam;
    
    public ChangePack(Vector3f velocity, Vector3f thetaVelocity, Vector3f localScale, ColorRGBA color, String colorMatParam) {
        this.velocity = velocity;
        this.thetaVelocity = thetaVelocity;
        this.localScale = localScale;
        this.color = color;
        this.colorMatParam = colorMatParam;
    }
    
    //returns what currentColor should be set to
    //I would just set currentColor but there is a warning that says "the assigned value is never used" and I don't want to mess with that
    public <S extends Spatial> boolean apply(S root, BoxMetadata battleBoxInfo, Vector3f positiveDirection, boolean fromSelf) {
        if (velocity == null && thetaVelocity == null && localScale == null && color == null /*&& colorMatParam == null*/) {
            //System.out.println("Attempting to apply ChangePack Changes...CHANGEPACK_END\n");
            return true;
        }
        
        //System.out.println("Attempting to apply ChangePack Changes..."); //remove later
        
        if (fromSelf || (root instanceof BattleSprite && ((BattleSprite)root).allowDisplacementTransformationsFromOpponent())) {
            if (velocity != null) {
                float boxWidth = battleBoxInfo.horizontalLength(); //the actual width of the box, not the one specified originally
                root.move(velocity.multLocal(boxWidth).multLocal(positiveDirection)); //multiply by positiveDirection for mirroring
                //System.out.println("DeltaPos: " + velocity);
            }
            
            if (thetaVelocity != null) {
                root.rotate(thetaVelocity.x, thetaVelocity.y, thetaVelocity.z);
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
