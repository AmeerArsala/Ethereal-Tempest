/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.occupant.control;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import enginetools.MaterialCreator;
import enginetools.math.SpatialOperator;
import etherealtempest.mesh.TrapezoidalMesh;
import maps.layout.tile.Tile;

/**
 *
 * @author night
 */
public class CursorPointer {
    private static final float FACING_CAMERA = FastMath.PI / -3f;
    
    private final Node pointerParent = new Node();
    private final Geometry pointer;
    
    private Material pointerMat;
    
    private final SpatialOperator pointerOperator;
    
    public CursorPointer(AssetManager assetManager, MaterialCreator customMaterial) {
        pointer = new Geometry(
            "cursorPointerGeometry",
            new TrapezoidalMesh(
                1.0f,                                   //length
                new Vector3f(2.5f/7f, 5f/7f, 0),        //right edge
                new Vector3f(0f, 4f/7f, 4f/15f),        //focus front
                new Vector3f(1.5f/7f, 3f/7f, 0)         //right extra edge point
            )
        );
        
        pointerMat = customMaterial.createMaterial(assetManager);
        pointer.setMaterial(pointerMat);
        
        float scale = (9.0f / 16f) * Tile.SIDE_LENGTH;
        pointer.setLocalScale(scale);
        
        pointerOperator = new SpatialOperator(pointer, new Vector3f(0.5f, 0.5f, 0));
        
        pointerParent.setLocalRotation(new Quaternion().fromAngles(0, 0, FACING_CAMERA));
        pointerParent.attachChild(pointer);
        
        setAngle(0, -FastMath.HALF_PI, 0);
    }
    
    public Material getMaterial() { return pointerMat; }
    
    public Node getNode() { return pointerParent; }
    public Geometry getPointer() { return pointer; }
    
    public SpatialOperator getOperator() {
        return pointerOperator;
    }
    
    public void setMaterial(Material mat) {
        pointerMat = mat;
    }
    
    public final void setAngle(float xAngle, float yAngle, float zAngle) {
        pointer.setLocalRotation(new Quaternion().fromAngles(0, 0, 0));
        pointer.setLocalTranslation(0, 0, 0);
        
        float translationAngleZ = FastMath.HALF_PI + zAngle;
        Vector3f deltaXYZ = new Vector3f(
            FastMath.cos(translationAngleZ) * Tile.SIDE_LENGTH * 1.25f,
            FastMath.sin(translationAngleZ) * Tile.SIDE_LENGTH * 1.25f,
            Tile.RADIUS_FOR_SQUARE
        );
        
        pointerOperator.rotateSpatialTo(xAngle, yAngle, zAngle);
        pointer.move(deltaXYZ);
    }
    
    public void rotateIf(boolean condition, float factor) {
        float theta = -FastMath.HALF_PI;
        if (condition) {
            theta += factor;
        }
        
        setAngle(0, theta, 0);

        pointerParent.setLocalTranslation(pointerParent.getLocalTranslation().x, (2 * FastMath.sin(factor)), pointerParent.getLocalTranslation().z);
    }
}
