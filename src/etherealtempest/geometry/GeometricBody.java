/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.geometry;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

/**
 *
 * @author night
 * @param <M> Quad, custom mesh, etc.
 */
public class GeometricBody<M extends Mesh> {
    private final Geometry geometry;
    private final M mesh;
    private Material material;
    
    public GeometricBody(Geometry geometry, M mesh, Material mat) {
        this.geometry = geometry;
        this.mesh = mesh;
        
        material = mat;
        geometry.setMaterial(material);
    }
    
    public Geometry getGeometry() {
        return geometry;
    }
    
    public M getMesh() {
        return mesh;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material mat) {
        material = mat;
        geometry.setMaterial(material);
    }
}
