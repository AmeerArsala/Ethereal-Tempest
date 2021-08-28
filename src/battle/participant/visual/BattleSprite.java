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
import enginetools.math.Vector2F;
import general.math.FloatPair;
import general.math.function.CartesianFunction;
import general.math.function.MathFunction;
import general.math.function.ParametricFunction4f;
import general.math.function.RGBAFunction;
import general.visual.sprite.ModifiedSprite;
import general.visual.sprite.Sprite;

/**
 *
 * @author night
 */
public class BattleSprite extends ModifiedSprite {
    public static final float DIE_FUNCTION_LENGTH = 1.25f;
    public static final RGBAFunction DIE_FUNCTION = new RGBAFunction(
        new ParametricFunction4f(
            MathFunction.CONSTANT(1f),                                         // R
            CartesianFunction.pointSlopeLine(1f, 0f, 0f, DIE_FUNCTION_LENGTH), // G
            CartesianFunction.pointSlopeLine(1f, 0f, 0f, DIE_FUNCTION_LENGTH), // B
            CartesianFunction.pointSlopeLine(1f, 0f, 0f, DIE_FUNCTION_LENGTH)  // A
        )
    );
    
    private final BoxMetadata battleBoxInfo;
    private final boolean usesHitPoint;
    
    private float zPosDefault;
    
    private boolean allowDisplacement = true;
    private ColorRGBA color = new ColorRGBA(1, 1, 1, 1);
    private Point hitPoint = null;
    private ZoneBox hurtbox;
    private Vector2f damageNumberLocation;     // in percents of Sprite dimensions (FACING_LEFT)
    private Vector2f centerPointDefault;       // in percents of Sprite dimensions (FACING_LEFT)
    private Vector2f posteriorBottomEdgePoint; // in percents of Sprite dimensions (FACING_LEFT)
    
    private boolean textureIsRotating = false;
    private boolean annulChangesFromOpponent = false;
    
    private String pathToCurrentAnimationJson = "";
    
    public BattleSprite(Vector2f dimensions, AssetManager assetManager, BoxMetadata battleBoxInfo, boolean usesHitPoint) {
        super(dimensions, assetManager);
        this.battleBoxInfo = battleBoxInfo;
        this.usesHitPoint = usesHitPoint;
        
        zPosDefault = getLocalTranslation().z;
    }
    
    public BattleSprite(float width, float height, AssetManager assetManager, BoxMetadata battleBoxInfo, boolean usesHitPoint) {
        super(width, height, assetManager);
        this.battleBoxInfo = battleBoxInfo;
        this.usesHitPoint = usesHitPoint;
        
        zPosDefault = getLocalTranslation().z;
    }
    
    public BoxMetadata getBattleBoxInfo() {
        return battleBoxInfo;
    }
    
    public boolean usesHitPoint() {
        return usesHitPoint;
    }
    
    public float getDefaultPosZ() {
        return zPosDefault;
    }
    
    public boolean allowDisplacementTransformationsFromOpponent() { 
        return allowDisplacement;
    }
    
    public boolean annulsChangesFromOpponent() {
        return annulChangesFromOpponent;
    }
    
    public boolean isTextureRotating() {
        return textureIsRotating;
    }
    
    public String getPathToCurrentAnimationJSON() {
        return pathToCurrentAnimationJson;
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
    
    public Vector2f getDamageNumberLocationSpritePercent() {
        return damageNumberLocation;
    }
    
    public Vector2f getDefaultCenterPoint() {
        return centerPointDefault;
    }
    
    public Vector2f getPosteriorBottomEdgePoint() {
        return posteriorBottomEdgePoint;
    }
    
    public void setIsTextureRotating(boolean rotating) {
        textureIsRotating = rotating;
    }
    
    public void setAllowDisplacementTransformationsFromOpponent(boolean allow) {
        allowDisplacement = allow;
    }
    
    public void setAnnulsChangesFromOpponent(boolean annuls) {
        annulChangesFromOpponent = annuls;
    }
    
    public void setPathToCurrentAnimationJSON(String jsonPath) {
        pathToCurrentAnimationJson = jsonPath;
    }
    
    public void setDefaultZPos(float zDefault) {
        zPosDefault = zDefault;
    }
    
    public void revertToDefaultZPos() {
        setLocalTranslation(getLocalTranslation().setZ(zPosDefault));
    }
    
    public void divergeFromDefaultZPos(float deltaZ) {
        setLocalTranslation(getLocalTranslation().setZ(zPosDefault + deltaZ));
    }
    
    public void setDamageNumberLocationSpritePercent(Vector2f dmgNumLoc) {
        damageNumberLocation = dmgNumLoc;
    }
    
    public void setDefaultCenterPoint(Vector2f centerPoint) {
        centerPointDefault = centerPoint;
    }
    
    public void setPosteriorBottomEdgePoint(Vector2f rearBottomEdgePoint) {
        posteriorBottomEdgePoint = rearBottomEdgePoint;
    }
    
    public void setHitPointIfAllowed(Vector2f hitPointInPercentage) { //use sprite cell percentage
        if (usesHitPoint) {
            hitPoint = new Point(hitPointInPercentage);
        }
    }
    
    public void setHurtbox(DomainBox boxInPercentages) {
        hurtbox = new ZoneBox(boxInPercentages);
    }
    
    public boolean collidesWith(Vector2f percentagePoint) { //point that is a percentage of the box
        /*
        DomainBox percentages = hurtbox.toBattleBoxPercentages();
        System.out.println("<COLLISION_CHECK>");
        System.out.println("Hitpoint: " + percentagePoint);
        System.out.println("ZoneBox Domain: " + percentages);
        System.out.println("Collision Occurs? " + percentages.pointIsWithinBox(percentagePoint));
        */
        
        return hurtbox.toBattleBoxPercentages().pointIsWithinBox(percentagePoint);
    }
    
    public boolean collidesWith(BattleSprite other) {
        return other.collidesWith(hitPoint.toBattleBoxPercentage());
    }
    
    public Vector2f getPercentagePosition() {
        /*Vector3f localTranslation;
        if (xFacing == FACING_RIGHT) {
            localTranslation = new Point()
        } else { // xFacing == FACING_LEFT
        
        }*/
        
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
        Vector2f a = new Point(new Vector2f(0.0f, 0.0f)).toBattleBoxPercentage();
        Vector2f b = new Point(new Vector2f(1.0f, 1.0f)).toBattleBoxPercentage();
        
        return Vector2F.absLocal(b.subtractLocal(a));
    }
    
    public Vector2f getDamageNumberLocationPercent() { //in terms of battleBox %
        return new Point(damageNumberLocation).toBattleBoxPercentage();
    }
    
    public Vector3f getDamageNumberLocation() {
        return new Point(damageNumberLocation).nonRelative3DPoint().setZ(0.11f);
    }
    
    /*public Vector3f getDefaultCenterPointLocation() {
        return new Point(Vector2F.salvage(centerPointDefault)).nonRelative3DPoint().setZ(getLocalTranslation().z);
    }*/
    
    @Override
    public void onMirrorStateChanged(boolean willBeMirrored) {
        super.onMirrorStateChanged(willBeMirrored);
        //damageNumberLocation.x = 1.0f - damageNumberLocation.x;
        //centerPointDefault.x = 1.0f - centerPointDefault.x;
    }
    
    @Override
    public void setColor(String colorMatParam, ColorRGBA color1) {
        color = color1;
        super.setColor(colorMatParam, color);
    }
    
    public class ZoneBox {
        private final DomainBox boxPercentages;
        
        public ZoneBox(DomainBox boxInPercentages) {
            boxPercentages = boxInPercentages;
        }
        
        public DomainBox inPercentages() {
            return boxPercentages.mirrorNew(xFacing != FACING_LEFT, false); //mirror x if not facing left, don't mirror y
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
    
    public class Point {
        private final Vector2f hitPointPercentage;
        
        public Point(Vector2f hitPointInPercentages) {
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
            return getPercentagePosition(nonRelative3DPoint(), false); //false to make using the left side a standard which takes percentDiffFromLeftEdge
        }
    }
}
