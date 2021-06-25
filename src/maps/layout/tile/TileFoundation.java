/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maps.layout.tile;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import general.utils.helpers.GeneralUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import maps.layout.MapCoords;

/**
 *
 * @author night
 */
public class TileFoundation {
    public static final float RADIUS_FOR_SQUARE = 8f;
    
    protected final MapCoords coords = new MapCoords();
    protected Geometry tgeometry;
    protected Mesh patchMesh;
    
    protected TileFoundation(int posX, int posY, int layer) {
        coords.setPosition(posX, posY, layer);
    }
    
    public MapCoords getPos() { return coords; }
    
    public Geometry getGeometry() { return tgeometry; }
    public Material getPatchMaterial() { return tgeometry.getMaterial(); }
    public Mesh getTileMesh() { return patchMesh; }
    
    public void setMaterial(Material mat) {
        tgeometry.setMaterial(mat);
    }
    
    public void setLocalTranslation(Vector3f translation) {
        tgeometry.setLocalTranslation(translation); 
    }
    
    public void setLocalTranslation(float x, float y, float z) {
        tgeometry.setLocalTranslation(x, y, z);
    }
    
    protected final Mesh createMesh() {
        Mesh mesh = new Mesh();
        
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textureCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        
        generateTile(vertices, textureCoords, indices);
        
        Vector3f[] verts = vertices.toArray(new Vector3f[vertices.size()]);
        Vector2f[] texCoords = textureCoords.toArray(new Vector2f[textureCoords.size()]);
        
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(verts));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(GeneralUtils.toIntArray(indices)));
        
        mesh.updateBound();
        
        return mesh;
    }
    
    protected void generateTile(List<Vector3f> vertices, List<Vector2f> textureCoords, List<Integer> indices) {
        //frame the tile
        float sideLength = 2f * RADIUS_FOR_SQUARE;
        vertices.add(new Vector3f(0, 0, 0)); //bottom left (0)
        vertices.add(new Vector3f(sideLength, 0, 0)); //top left (1)
        vertices.add(new Vector3f(sideLength, 0, sideLength)); //top right (2)
        vertices.add(new Vector3f(0, 0, sideLength)); //bottom right (3)
        
        indices.addAll(Arrays.asList(1, 0, 3,  3, 2, 1));
        
        for (int n = 0; n < 4; n++) {
            textureCoords.addAll(
                Arrays.asList(
                    new Vector2f(0, 0),
                    new Vector2f(0, 1),
                    new Vector2f(1, 1),
                    new Vector2f(1, 0)
                )
            );
        }
    }
    
}
