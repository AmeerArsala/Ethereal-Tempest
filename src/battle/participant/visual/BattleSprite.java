/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant.visual;

import general.math.DomainBox;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import general.math.function.CartesianFunction;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction4f;
import general.math.function.RGBAFunction;
import general.visual.ModifiedSprite;
import general.visual.Sprite;

/**
 *
 * @author night
 */
public class BattleSprite extends ModifiedSprite {
    public static final float DIE_FUNCTION_LENGTH = 1.5f;
    public static final RGBAFunction DIE_FUNCTION = new RGBAFunction(
        new ParametricFunction4f(
            MathFunction.CONSTANT(1f),                                         // R
            CartesianFunction.pointSlopeLine(1f, 0f, 0f, DIE_FUNCTION_LENGTH), // G; 1.5 seconds
            CartesianFunction.pointSlopeLine(1f, 0f, 0f, DIE_FUNCTION_LENGTH), // B; 1.5 seconds
            CartesianFunction.pointSlopeLine(1f, 0f, 0f, DIE_FUNCTION_LENGTH)  // A; 1.5 seconds
        )
    );
    
    private boolean allowDisplacement = true;
    private final boolean usesHitPoint;
    private HitPoint hitPoint = null;
    private Hurtbox hurtbox;
    private Vector2f damageNumberLocation;
    
    public BattleSprite(Vector2f dimensions, AssetManager assetManager, boolean usesHitPoint) {
        super(dimensions, assetManager);
        this.usesHitPoint = usesHitPoint;
    }
    
    public BattleSprite(float width, float height, AssetManager assetManager, boolean usesHitPoint) {
        super(width, height, assetManager);
        this.usesHitPoint = usesHitPoint;
    }
    
    public boolean allowDisplacementTransformationsFromOpponent() { 
        return allowDisplacement;
    }
    
    public boolean usesHitPoint() {
        return usesHitPoint;
    }
    
    public Vector2f getHitPoint() {
        return hitPoint.inPercentages();
    }
    
    public DomainBox getHurtbox() {
        return hurtbox.inPercentages();
    }
    
    public Vector2f getDamageNumberLocation() {
        return damageNumberLocation;
    }
    
    public void setDamageNumberLocation(Vector2f dmgNumLoc) {
        damageNumberLocation = dmgNumLoc;
    }
    
    public void setAllowDisplacementTransformationsFromOpponent(boolean allow) {
        allowDisplacement = allow;
    }
    
    public void setHitPointIfAllowed(Vector2f hitPointInPercentage) {
        if (usesHitPoint) {
            hitPoint = new HitPoint(hitPointInPercentage);
        }
    }
    
    public void setHurtbox(DomainBox boxInPercentages) {
        hurtbox = new Hurtbox(boxInPercentages);
    }
    
    public boolean collidesWith(Vector2f nonRelativeHitPoint) { //non relative hit point
        return hurtbox.nonRelativeDomainBox().pointIsWithinBox(nonRelativeHitPoint);
    }
    
    public boolean collidesWith(BattleSprite other) {
        return other.collidesWith(hitPoint.nonRelativePoint());
    }
    
    public Vector2f getPercentagePosition(Vector2f battleBoxDimensions) { //percentage of battleBox in terms of relative position 
        Vector3f localTranslation = getLocalTranslation();
        Vector2f unitVectorPos = new Vector2f(localTranslation.x / battleBoxDimensions.x, localTranslation.y / battleBoxDimensions.y);
        
        if (isMirrored()) {
            return new Vector2f(1.0f - unitVectorPos.x, 1.0f - unitVectorPos.y);
        }
        
        return unitVectorPos;
    }
    
    private class Hurtbox {
        private final DomainBox boxPercentages;
        
        public Hurtbox(DomainBox boxInPercentages) {
            boxPercentages = boxInPercentages;
        }
        
        public DomainBox inPercentages() {
            return boxPercentages.mirrorNew(xFacing != Sprite.FACING_LEFT, false); //mirror x if not facing left
        }
        
        public DomainBox relativeDomainBox() { //not in percentages
            Vector2f dimensions = getScaledDimensions();
            return inPercentages().multNew(dimensions.x, dimensions.y);
        }
        
        public DomainBox nonRelativeDomainBox() {
            Vector3f localTranslation = getLocalTranslation();
            return relativeDomainBox().addLocal(localTranslation.x, localTranslation.y);
        }
    }
    
    private class HitPoint {
        private final Vector2f hitPointPercentage;
        
        public HitPoint(Vector2f hitPointInPercentages) {
            hitPointPercentage = hitPointInPercentages;
        }
        
        public Vector2f inPercentages() {
            float xVal =  hitPointPercentage.x;
            if (xFacing != Sprite.FACING_LEFT) {
                xVal = 1.0f - xVal; //mirror X if not facing left
            }
            
            return new Vector2f(xVal, hitPointPercentage.y);
        }
        
        public Vector2f relativePoint() {
            return getScaledDimensions().multLocal(inPercentages());
        }
        
        public Vector2f nonRelativePoint() {
            Vector3f localTranslation = getLocalTranslation();
            return relativePoint().addLocal(localTranslation.x, localTranslation.y);
        }
    }
}
