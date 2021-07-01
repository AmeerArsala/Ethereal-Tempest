/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author night
 */
public class SpatialOperator {
    private final Spatial spatial;
    private final Vector3f dimensions;
    private final Vector3f pointInPercents; //in percent of dimensions; think of SpatialOperator to be "operating" at this point. This is the 'p' value
    
    public SpatialOperator(Spatial spatial, Vector3f dimensions, Vector3f pointInPercents) {
        this.spatial = spatial;
        this.dimensions = dimensions;
        this.pointInPercents = pointInPercents;
    }
    
    public Spatial getSpatial() { return spatial; }
    public Vector3f getDimensions() { return dimensions; }
    public Vector3f getPointInPercents() { return pointInPercents; }
    
    public Vector3f calculateLocalPoint() { //point NOT in percents; this is the F value
        // F = localTranslation + (p * dimensions)
        return spatial.getLocalTranslation().add(pointInPercents.mult(dimensions));
    }
    
    public Vector3f calculateWorldPoint() { //point NOT in percents; this is the F value
        // F = localTranslation + (p * dimensions)
        return spatial.getWorldTranslation().add(pointInPercents.mult(dimensions));
    }
    
    public Vector3f calculateLocalDeltasTo(SpatialOperator other) {
        return other.calculateLocalPoint().subtractLocal(calculateLocalPoint());
    }
    
    public Vector3f calculateWorldDeltasTo(SpatialOperator other) {
        return other.calculateWorldPoint().subtractLocal(calculateWorldPoint());
    }
    
    public Vector3f calculateLocalDeltasTo(Vector3f localPoint) {
        return localPoint.subtract(calculateLocalPoint());
    }
    
    public Vector3f calculateWorldDeltasTo(Vector3f worldPoint) {
        return worldPoint.subtract(calculateWorldPoint());
    }
    
    public float localMagnitude() {
        Vector3f localTranslationSquared = Vector3F.pow(spatial.getLocalTranslation(), 2);
        return FastMath.sqrt(localTranslationSquared.x + localTranslationSquared.y + localTranslationSquared.z);
    }
    
    public float worldMagnitude() {
        Vector3f worldTranslationSquared = Vector3F.pow(spatial.getWorldTranslation(), 2);
        return FastMath.sqrt(worldTranslationSquared.x + worldTranslationSquared.y + worldTranslationSquared.z);
    }
    
    public Vector3f calculateLocalDeltasAsResultOf(Plane rotationPlane, float deltaTheta) {
        Vector3f localTranslation = spatial.getLocalTranslation();
        return rotationPlane.applyPolar(localTranslation, deltaTheta).subtractLocal(localTranslation);
    }
    
    public Vector3f calculateWorldDeltasAsResultOf(Plane rotationPlane, float deltaTheta) {
        Vector3f worldTranslation = spatial.getWorldTranslation();
        return rotationPlane.applyPolar(worldTranslation, deltaTheta).subtractLocal(worldTranslation);
    }
    
    /**
     * Moves the Spatial such that this point's new location is the same as the parameter's point
     * In other words, it grabs the entire spatial and moves it such that the SpatialOperators end up in the same position
     * @param other other SpatialOperator
     * For centering, the field 'pointInPercents' would both have 0.5 on whichever axes this is performed on
     */
    public void alignTo(SpatialOperator other) {
        spatial.move(spatial.worldToLocal(calculateWorldDeltasTo(other), null));
    }
    
    /**
     * 
     * @param worldPoint worldTranslation of said point
     */
    public void alignTo(Vector3f worldPoint) {
        spatial.move(spatial.worldToLocal(calculateWorldDeltasTo(worldPoint), null));
    }
    
    /**
     * 
     * @param other other SpatialOperator
     */
    public void alignToLocally(SpatialOperator other) {
        spatial.move(calculateLocalDeltasTo(other));
    }
    
    /**
     * 
     * @param localPoint localTranslation of said point
     */
    public void alignToLocally(Vector3f localPoint) {
        spatial.move(calculateLocalDeltasTo(localPoint));
    }
    
    /**
     * Rotates the spatial AROUND the point
     * 
     * @param xAngle
     * @param yAngle
     * @param zAngle 
     */
    public void rotateSpatial(float xAngle, float yAngle, float zAngle) {
        spatial.rotate(xAngle, yAngle, zAngle);
        spatial
            .move(calculateLocalDeltasAsResultOf(Plane.ZY, xAngle))
            .move(calculateLocalDeltasAsResultOf(Plane.XZ, yAngle))
            .move(calculateLocalDeltasAsResultOf(Plane.XY, zAngle));
    }
}
