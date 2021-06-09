/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;

/**
 *
 * @author night
 */
public class RadialMesh extends Mesh {
    private final float radius, percentage;
    private final int specificity;
    
    private Vector3f[] vertices;
    private Vector2f[] texCoord = new Vector2f[4];
    private int[] indexes;
    
    private ArrayList<Integer> indices = new ArrayList<>();
    
    public RadialMesh(float radius, float percentage, int specificity) {
        this.radius = radius;
        this.percentage = percentage;
        this.specificity = specificity;
        
        vertices = new Vector3f[(int)(percentage * 360 * specificity) + 2];
        vertices[0] = new Vector3f(0, 0, 0); //origin point
        vertices[vertices.length - 1] = new Vector3f(radius * FastMath.cos(0), radius * FastMath.sin(0), 1); //end point
        
        texCoord[0] = new Vector2f(0, 0);
        texCoord[1] = new Vector2f(1, 0);
        texCoord[2] = new Vector2f(0, 1);
        texCoord[3] = new Vector2f(1, 1);
        
        setVertices();
        
        indices.add(0);
        indices.add(vertices.length - 2);
        indices.add(vertices.length - 1);
        
        setIndices();
        
        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        updateBound();
    }
    
    private void setVertices() {
        for (int i = 0; i < vertices.length - 1; i++) { //origin  is (0, 0, 0)
            vertices[i + 1] = new Vector3f(radius * FastMath.cos(i * FastMath.PI / (180f * specificity)), radius * FastMath.sin(i * FastMath.PI / (180f * specificity)), 1);
            
            if (i > 1) {
                //counter clockwise triangle
                indices.add(0);
                indices.add(i - 1);
                indices.add(i);
            }
        }
    }
    
    private void setIndices() {
        indexes = new int[indices.size()];
        
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = indices.get(i);
        }
    }
    
    public float getRadius() {
        return radius;
    }
    
    public Vector3f[] getVertices() {
        return vertices;
    }
    
    public float getPercentage() {
        return percentage;
    }
    
}
