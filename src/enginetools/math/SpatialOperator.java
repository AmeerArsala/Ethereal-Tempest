/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools.math;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author night
 */
public class SpatialOperator {
    public static final Vector3f ORIGIN_BOTTOM_LEFT = new Vector3f(0, 0, 0);
    public static final Vector3f ORIGIN_TOP_LEFT = new Vector3f(0, 1, 0);
    public static final Vector3f DEFAULT_POSITIVE_DIRECTION = new Vector3f(1, 1, 1);
    
    private final Spatial spatial;
    private final Vector3f dimensions;
    private final Vector3f pointInPercents; //in percent of dimensions; think of SpatialOperator to be "operating" at this point. This is the 'p' value
    private final Vector3f originPointInPercents; //in percent of dimensions
    private final Vector3f positiveDirection;
    
    public SpatialOperator(Spatial spatial, Vector3f dimensions, Vector3f pointInPercents, Vector3f originPointInPercents, Vector3f positiveDirection) {
        this.spatial = spatial;
        this.dimensions = dimensions;
        this.pointInPercents = pointInPercents;
        this.originPointInPercents = originPointInPercents;
        this.positiveDirection = positiveDirection;
    }
    
    public SpatialOperator(Spatial spatial, Vector3f dimensions, Vector3f pointInPercents, Vector3f originPointInPercents) {
        this(spatial, dimensions, pointInPercents, originPointInPercents, DEFAULT_POSITIVE_DIRECTION);
    }
    
    public SpatialOperator(Spatial spatial, Vector3f dimensions, Vector3f pointInPercents) {
        this(spatial, dimensions, pointInPercents, ORIGIN_BOTTOM_LEFT, DEFAULT_POSITIVE_DIRECTION);
    }
    
    public SpatialOperator(Spatial spatial, Vector3f pointInPercents) {
        this(spatial, ((BoundingBox)spatial.getWorldBound()).getExtent(null), pointInPercents, ORIGIN_BOTTOM_LEFT, DEFAULT_POSITIVE_DIRECTION);
    }
    
    public Spatial getSpatial() { return spatial; }
    public Vector3f getDimensions() { return dimensions; }
    public Vector3f getPointInPercents() { return pointInPercents; }
    public Vector3f getOriginPointInPercents() { return originPointInPercents; }
    public Vector3f getPositiveDirection() { return positiveDirection; }
    
    public Vector3f scaledDimensions() {
        return dimensions.mult(spatial.getLocalScale());
    }
    
    public Vector3f bottomLeftOriginInPercents() {
        return originPointInPercents.mult(-1);
    }
    
    public Vector3f relativePointPos(Vector3f pointInPercentages) {
        Vector3f origin = bottomLeftOriginInPercents();
        Vector3f relativePercentagePos = origin.addLocal(pointInPercentages);
        return relativePercentagePos.multLocal(scaledDimensions());
    }
    
    public Vector3f relativePointPos() {
        return relativePointPos(pointInPercents);
    }
    
    public Vector3f calculateLocalPoint(Vector3f pointInPercentages) { //point NOT in percents; this is the F value
        // F = localTranslation + (p * dimensions)
        return spatial.getLocalTranslation().add(relativePointPos(pointInPercentages));
    }
    
    public Vector3f calculateWorldPoint(Vector3f pointInPercentages) { //point NOT in percents; this is the F value
        // F = worldTranslation + (p * dimensions)
        return spatial.getWorldTranslation().add(relativePointPos(pointInPercentages));
    }
    
    public Vector3f calculateLocalPoint() { //point NOT in percents; this is the F value
        return calculateLocalPoint(pointInPercents);
    }
    
    public Vector3f calculateWorldPoint() { //point NOT in percents; this is the F value
        return calculateWorldPoint(pointInPercents);
    }
    
    public Vector3f calculateLocalDeltasTo(SpatialOperator other) {
        return other.calculateLocalPoint().subtractLocal(calculateLocalPoint()).multLocal(positiveDirection);
    }
    
    public Vector3f calculateWorldDeltasTo(SpatialOperator other) {
        return other.calculateWorldPoint().subtractLocal(calculateWorldPoint()).multLocal(positiveDirection);
    }
    
    public Vector3f calculateLocalDeltasTo(Vector3f localPoint) {
        return localPoint.subtract(calculateLocalPoint()).multLocal(positiveDirection);
    }
    
    public Vector3f calculateWorldDeltasTo(Vector3f worldPoint) {
        return worldPoint.subtract(calculateWorldPoint()).multLocal(positiveDirection);
    }
    
    public Vector3f calculateLocalDeltasToNewPoint(Vector3f pointInPercentages) {
        return calculateLocalPoint(pointInPercentages).subtractLocal(calculateLocalPoint()).multLocal(positiveDirection);
    }
    
    public Vector3f calculateWorldDeltasToNewPoint(Vector3f pointInPercentages) {
        return calculateWorldPoint(pointInPercentages).subtractLocal(calculateWorldPoint()).multLocal(positiveDirection);
    }
    
    public float localMagnitude() {
        Vector3f localTranslationSquared = Vector3F.pow(spatial.getLocalTranslation(), 2);
        return FastMath.sqrt(localTranslationSquared.x + localTranslationSquared.y + localTranslationSquared.z);
    }
    
    public float worldMagnitude() {
        Vector3f worldTranslationSquared = Vector3F.pow(spatial.getWorldTranslation(), 2);
        return FastMath.sqrt(worldTranslationSquared.x + worldTranslationSquared.y + worldTranslationSquared.z);
    }
    
    public Vector3f calculateLocalDeltaThetas(Plane rotationPlane, float deltaTheta) {
        Vector3f localTranslation = spatial.getLocalTranslation();
        return rotationPlane.applyPolar(localTranslation, deltaTheta).subtractLocal(localTranslation);
    }
    
    public Vector3f calculateWorldDeltaThetas(Plane rotationPlane, float deltaTheta) {
        Vector3f worldTranslation = spatial.getWorldTranslation();
        return rotationPlane.applyPolar(worldTranslation, deltaTheta).subtractLocal(worldTranslation);
    }
    
    /**
     * Gets the local angle of the Spatial in Tait-Bryan/Euler angles
     * @return the local angle of the Spatial in Tait-Bryan/Euler angles
     */
    public Vector3f getLocalAngle() {
        Quaternion rot = spatial.getLocalRotation();
        float[] angles = rot.toAngles(null);
        
        return new Vector3f(angles[0], angles[1], angles[2]);
    }

    /**
     * Moves the Spatial such that this point's new world translation is the same as the parameter's point
     * In other words, it grabs the entire Spatial and moves it such that the SpatialOperators end up in the same position
     * @param other other SpatialOperator
     * For centering, the field 'pointInPercents' would both have 0.5 on whichever axes this is performed on
     * @return this so calls can be chained
     */
    public SpatialOperator alignTo(SpatialOperator other) {
        spatial.move(spatial.worldToLocal(calculateWorldDeltasTo(other), null));
        return this;
    }
    
    /**
     * Moves the Spatial such that this point's new world translation is the same as the parameter's location
     * In other words, it grabs the entire Spatial and moves it such that the points are lined up
     * @param worldPoint worldTranslation of said point
     * @return this so calls can be chained
     */
    public SpatialOperator alignTo(Vector3f worldPoint) {
        spatial.move(spatial.worldToLocal(calculateWorldDeltasTo(worldPoint), null));
        return this;
    }
    
    /**
     * Moves the Spatial such that this point's new world translation is the same as the parameter's point in percentages
     * In other words, it grabs the entire Spatial and moves it such that the points are lined up; DOES NOT SET THE NEW POINT IN PERCENTS
     * @param pointInPercentages the point in percents to align to
     * @return this so calls can be chained
     */
    public SpatialOperator alignToNewPoint(Vector3f pointInPercentages) {
        spatial.move(spatial.worldToLocal(calculateWorldDeltasToNewPoint(pointInPercentages), null));
        return this;
    }
    
    /**
     * Moves the Spatial such that this point's new local translation is the same as the parameter's point
     * In other words, it grabs the entire Spatial and moves it such that the SpatialOperators end up in the same position
     * @param other other SpatialOperator
     * @return this so calls can be chained
     */
    public SpatialOperator alignToLocally(SpatialOperator other) {
        spatial.move(calculateLocalDeltasTo(other));
        return this;
    }
    
    /**
     * Moves the Spatial such that this point's new local translation is the same as the parameter's point
     * In other words, it grabs the entire Spatial and moves it such that the points are lined up
     * @param localPoint localTranslation of said point
     * @return this so calls can be chained
     */
    public SpatialOperator alignToLocally(Vector3f localPoint) {
        spatial.move(calculateLocalDeltasTo(localPoint));
        return this;
    }
    
    /**
     * Moves the Spatial such that this point's new local translation is the same as the parameter's point
     * In other words, it grabs the entire Spatial and moves it such that the points are lined up; DOES NOT SET THE NEW POINT IN PERCENTS
     * @param pointInPercentages the point in percents to align to
     * @return this so calls can be chained
     */
    public SpatialOperator alignToNewPointLocally(Vector3f pointInPercentages) {
        spatial.move(calculateLocalDeltasToNewPoint(pointInPercentages));
        return this;
    }
    
    /**
     * Rotates the Spatial AROUND the point
     * 
     * @param xAngle
     * @param yAngle
     * @param zAngle 
     * @return this so calls can be chained
     */
    public SpatialOperator rotateSpatial(float xAngle, float yAngle, float zAngle) {
        Vector3f difference = pointInPercents.subtract(originPointInPercents);
        
        alignToNewPointLocally(originPointInPercents);
        spatial.rotate(xAngle, yAngle, zAngle);
        alignToNewPointLocally(pointInPercents.add(difference));
        
        return this;
    }
    
    /**
     * Rotates the Spatial AROUND the point, but calculates it differently 
     * 
     * @param xAngle
     * @param yAngle
     * @param zAngle 
     * @return this so calls can be chained
     */
    public SpatialOperator rotateSpatial2(float xAngle, float yAngle, float zAngle) {
        spatial.rotate(xAngle, yAngle, zAngle);
        
        spatial
            .move(calculateLocalDeltaThetas(Plane.ZY, xAngle))
            .move(calculateLocalDeltaThetas(Plane.XZ, yAngle))
            .move(calculateLocalDeltaThetas(Plane.XY, zAngle));
        
        return this;
    }
    
    /**
     * Rotates the Spatial AROUND the point to the targetAngle
     * @param targetAngle
     * @return the new angle of the Spatial
     */
    public Vector3f rotateSpatialTo(Vector3f targetAngle) {
        Vector3f dthetas = targetAngle.subtract(getLocalAngle());
        rotateSpatial(dthetas.x, dthetas.y, dthetas.z);
        
        return targetAngle;
    }
    
    /**
     * Rotates the Spatial AROUND the point to the targetAngle
     * @param targetX
     * @param targetY
     * @param targetZ
     * @return the new angle of the Spatial
     */
    public Vector3f rotateSpatialTo(float targetX, float targetY, float targetZ) {
        Vector3f targetAngle = new Vector3f(targetX, targetY, targetZ);
        Vector3f dthetas = targetAngle.subtract(getLocalAngle());
        rotateSpatial(dthetas.x, dthetas.y, dthetas.z);
        
        return targetAngle;
    }
    
    /**
     * Rotates the Spatial AROUND the point to the targetAngle, but calculates rotation differently
     * @param targetAngle
     * @return the new angle of the Spatial
     */
    public Vector3f rotateSpatialTo2(Vector3f targetAngle) {
        Vector3f dthetas = targetAngle.subtract(getLocalAngle());
        rotateSpatial2(dthetas.x, dthetas.y, dthetas.z);
        
        return targetAngle;
    }
    
    /**
     * Rotates the Spatial AROUND the point to the targetAngle, but calculates rotation differently
     * @param targetX
     * @param targetY
     * @param targetZ
     * @return the new angle of the Spatial
     */
    public Vector3f rotateSpatialTo2(float targetX, float targetY, float targetZ) {
        Vector3f targetAngle = new Vector3f(targetX, targetY, targetZ);
        Vector3f dthetas = targetAngle.subtract(getLocalAngle());
        rotateSpatial2(dthetas.x, dthetas.y, dthetas.z);
        
        return targetAngle;
    }
}
