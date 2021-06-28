/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.participant.visual;

import battle.environment.BoxMetadata;
import general.math.DomainBox;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import enginetools.Vector2F;
import general.math.FloatPair;
import general.math.function.CartesianFunction;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction4f;
import general.math.function.RGBAFunction;
import general.utils.helpers.MathUtils;
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
    
    private final BoxMetadata battleBoxInfo;
    private final boolean usesHitPoint;
    
    private boolean allowDisplacement = true;
    private ColorRGBA color = new ColorRGBA(1, 1, 1, 1);
    private HitPoint hitPoint = null;
    private Hurtbox hurtbox;
    private Vector2f damageNumberLocation;
    
    public BattleSprite(Vector2f dimensions, AssetManager assetManager, BoxMetadata battleBoxInfo, boolean usesHitPoint) {
        super(dimensions, assetManager);
        this.battleBoxInfo = battleBoxInfo;
        this.usesHitPoint = usesHitPoint;
    }
    
    public BattleSprite(float width, float height, AssetManager assetManager, BoxMetadata battleBoxInfo, boolean usesHitPoint) {
        super(width, height, assetManager);
        this.battleBoxInfo = battleBoxInfo;
        this.usesHitPoint = usesHitPoint;
    }
    
    public BoxMetadata getBattleBoxInfo() {
        return battleBoxInfo;
    }
    
    public boolean usesHitPoint() {
        return usesHitPoint;
    }
    
    public boolean allowDisplacementTransformationsFromOpponent() { 
        return allowDisplacement;
    }
    
    public ColorRGBA getColor() {
        return color;
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
    
    public void setHitPointIfAllowed(Vector2f hitPointInPercentage) { //use sprite cell percentage
        if (usesHitPoint) {
            hitPoint = new HitPoint(hitPointInPercentage);
        }
    }
    
    public void setHurtbox(DomainBox boxInPercentages) {
        hurtbox = new Hurtbox(boxInPercentages);
    }
    
    public boolean collidesWith(Vector2f percentagePoint) { //point that is a percentage of the box
        /*
        DomainBox percentages = hurtbox.toBattleBoxPercentages();
        System.out.println("<COLLISION_CHECK>");
        System.out.println("Hitpoint: " + percentagePoint);
        System.out.println("Hurtbox Domain: " + percentages);
        System.out.println("Collision Occurs? " + percentages.pointIsWithinBox(percentagePoint));
        */
        
        return hurtbox.toBattleBoxPercentages().pointIsWithinBox(percentagePoint);
    }
    
    public boolean collidesWith(BattleSprite other) {
        return other.collidesWith(hitPoint.toBattleBoxPercentage());
    }
    
    public Vector2f getPercentagePosition() {
        return getPercentagePosition(getLocalTranslation(), true);
    }
    
    private Vector2f getPercentagePosition(Vector3f localTranslation, boolean tailorToXFacing) { //percentage of battleBox in terms of relative position 
        Vector2f unitVectorPos = new Vector2f();
        
        if (!tailorToXFacing || xFacing == FACING_RIGHT) {
            //left side
            unitVectorPos.x = FastMath.abs(battleBoxInfo.percentDiffFromLeftEdge(localTranslation.x));
        } else { // xFacing == FACING_LEFT
            //right side
            unitVectorPos.x = FastMath.abs(battleBoxInfo.percentDiffFromRightEdge(localTranslation.x));
        }
        
        unitVectorPos.y = FastMath.abs(battleBoxInfo.percentDiffFromBottomEdge(localTranslation.y));
        
        return unitVectorPos;
    }
    
    public Vector2f getPercentageDimensions() { //in terms of battleBox %
        Vector2f a = new HitPoint(new Vector2f(0.0f, 0.0f)).toBattleBoxPercentage();
        Vector2f b = new HitPoint(new Vector2f(1.0f, 1.0f)).toBattleBoxPercentage();
        
        return Vector2F.absLocal(b.subtractLocal(a));
    }
    
    @Override
    public void setColor(String colorMatParam, ColorRGBA color1) {
        color = color1;
        super.setColor(colorMatParam, color);
    }
    
    private class Hurtbox {
        private final DomainBox boxPercentages;
        
        public Hurtbox(DomainBox boxInPercentages) {
            boxPercentages = boxInPercentages;
        }
        
        public DomainBox inPercentages() {
            return boxPercentages.mirrorNew(xFacing != FACING_LEFT, false); //mirror x if not facing left
        }
        
        public DomainBox relativeDomainBox() { //not in percentages
            Vector2f dimensions = getScaledDimensions();
            return inPercentages().multNew(dimensions.x, dimensions.y);
        }
        
        public DomainBox nonRelativeDomainBox() {
            Vector3f localTranslation = getLocalTranslation();
            return relativeDomainBox().addLocal(localTranslation.x, localTranslation.y);
        }
        
        public DomainBox toBattleBoxPercentages() {
            DomainBox relativeBox = relativeDomainBox();
            Vector3f localTranslation = getLocalTranslation();
            
            Vector2f pointA = getPercentagePosition(localTranslation.add(relativeBox.getDomainX().a, relativeBox.getDomainY().a, 0), false);
            Vector2f pointB = getPercentagePosition(localTranslation.add(relativeBox.getDomainX().b, relativeBox.getDomainY().b, 0), false);
            
            return new DomainBox(new FloatPair(pointA.x, pointB.x), new FloatPair(pointA.y, pointB.y));
        }
    }
    
    private class HitPoint {
        private final Vector2f hitPointPercentage;
        
        public HitPoint(Vector2f hitPointInPercentages) {
            hitPointPercentage = hitPointInPercentages;
        }
        
        public Vector2f inPercentages() {
            float xVal = hitPointPercentage.x;
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
        
        public Vector3f nonRelative3DPoint() {
            Vector2f relative = relativePoint();
            return getLocalTranslation().add(relative.x, relative.y, 0);
        }
        
        public Vector2f toBattleBoxPercentage() {
            return getPercentagePosition(nonRelative3DPoint(), false); //false to make using the left side a standard
        }
    }
}
