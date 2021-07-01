/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import enginetools.math.SpatialOperator;

/**
 *
 * @author night
 */
public class GeometryPanel extends Node {
    protected final float width, height;
    private final Quad quad;
    private final Geometry geometry;
    private final SpatialOperator anchor;
    
    private boolean isMirrored = false;
    
    public GeometryPanel(float width, float height) {
        this(width, height, Bucket.Transparent);
    }
    
    public GeometryPanel(float width, float height, Bucket queueBucket) {
        this.width = width;
        this.height = height;
        
        quad = new Quad(width, height);
        geometry = new Geometry("quad panel", quad);
        geometry.setQueueBucket(queueBucket);
        
        attachChild(geometry);
        
        anchor = new SpatialOperator(this, getScaledDimensions3D(), new Vector3f());
    }
    
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    
    public final Vector2f getUnscaledDimensions() {
        return new Vector2f(width, height);
    }
    
    public final Vector2f getScaledDimensions() { //scaled dimensions
        Vector3f localScale = getLocalScale();
        return new Vector2f(width * localScale.x, height * localScale.y);
    }
    
    public final Vector3f getUnscaledDimensions3D() {
        return new Vector3f(width, height, 0);
    }
    
    public final Vector3f getScaledDimensions3D() {
        return getLocalScale().mult(new Vector3f(width, height, 1));
    }
    
    public Geometry getGeometry() { 
        return geometry; 
    }
    
    public Quad getQuad() { 
        return quad; 
    }
    
    public SpatialOperator getOperator() {
        anchor.getDimensions().set(getScaledDimensions3D()); //update dimensions
        return anchor;
    }
    
    public SpatialOperator getOperator(float percentX, float percentY) {
        anchor.getDimensions().set(getScaledDimensions3D()); //update dimensions
        anchor.getPointInPercents().set(percentX, percentY, 0);
        
        return anchor;
    }
    
    public boolean isMirrored() { 
        return isMirrored; 
    }
    
    public Material getMaterial() {
        return geometry.getMaterial();
    }
    
    @Override
    public void setMaterial(Material mat) {
        geometry.setMaterial(mat);
    }
    
    protected void setNodeMaterial(Material mat) {
        super.setMaterial(mat);
    }
    
    public void setMirrored(boolean mirrored) {
        isMirrored = mirrored;
        if (mirrored) {
            quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
            { //mirrored; clockwise / x values inverted (0 goes to 1 and 1 goes to 0)
                1, 0,
                0, 0,
                0, 1,
                1, 1
            });
        } else { //not mirrored; counter clockwise / x values not inverted
            quad.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]
            {
                0, 0,
                1, 0,
                1, 1,
                0, 1
            });
        }
    }
    
    public void mirror() {
        setMirrored(!isMirrored);
    }
    
    public Vector3f getPositiveDirection3DVector() {
        Vector2f vec = getPositiveDirection2DVector();
        return new Vector3f(vec.x, vec.y, 1);
    }
    
    public Vector2f getPositiveDirection2DVector() {
        return new Vector2f(isMirrored ? -1 : 1, 1);
    }
    
    public Vector2f vectorInPositiveDirection(Vector2f percentDimensions, boolean scaleDimensions) {
        Vector2f dims = scaleDimensions ? getScaledDimensions() : getUnscaledDimensions();
        return dims.multLocal(percentDimensions).multLocal(getPositiveDirection2DVector());
    }
    
    public Vector3f getLocalAngle() {
        Quaternion rot = getLocalRotation();
        float[] angles = rot.toAngles(null);
        
        return new Vector3f(angles[0], angles[1], angles[2]);
    }
    
    
}
