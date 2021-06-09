/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author night
 * @param <R> User Spatial: Node, Sprite, GeometryPanel, etc.
 */
public class RootPackage<R extends Spatial> {
    public final R root;
    public final Vector3f positiveDirectionVector;
    
    public RootPackage(R root, Vector3f positiveDirectionVector) {
        this.root = root;
        this.positiveDirectionVector = positiveDirectionVector;
    }
    
    public RootPackage(R root) {
        this(root, new Vector3f(1, 1, 1));
    }
}
