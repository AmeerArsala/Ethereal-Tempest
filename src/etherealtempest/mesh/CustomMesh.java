/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.mesh;

import com.jme3.math.Vector2f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 *
 * @author night
 */
public abstract class CustomMesh extends Mesh {
    protected Vector2f[] vertices;
    protected Vector2f[] texCoord = new Vector2f[4];
    protected int[] indices;
    
    public final void create() {
        generate();
        
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        updateBound();
    }
    
    protected abstract void generate();
    
    public Vector2f[] getVertices() {
        return vertices;
    }
}
