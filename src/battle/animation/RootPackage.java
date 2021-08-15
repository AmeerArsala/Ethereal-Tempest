/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import general.utils.helpers.EngineUtils;
import java.util.function.Supplier;

/**
 *
 * @author night
 * @param <R> User Spatial: Node, Sprite, GeometryPanel, etc.
 */
public class RootPackage<R extends Spatial> {
    public final R root;
    public final Vector3f positiveDirectionVector;
    public final Vector2f centerPointDefault;
    public final Supplier<Vector3f> dimensionsSupplier;
    
    public RootPackage(R root, Vector3f positiveDirectionVector, Vector2f centerPointDefault, Supplier<Vector3f> dimensionsSupplier) {
        this.root = root;
        this.positiveDirectionVector = positiveDirectionVector;
        this.centerPointDefault = centerPointDefault;
        this.dimensionsSupplier = dimensionsSupplier;
    }
    
    public RootPackage(R root, Vector2f centerPointDefault, Supplier<Vector3f> dimensionsSupplier) {
        this(root, new Vector3f(1, 1, 1), centerPointDefault, dimensionsSupplier);
    }
    
    public RootPackage(R root, Supplier<Vector3f> dimensionsSupplier) {
        this(root, new Vector3f(1, 1, 1), new Vector2f(0.5f, 0.5f), dimensionsSupplier);
    }
    
    public RootPackage(R root, Vector2f centerPointDefault) {
        this(root, new Vector3f(1, 1, 1), centerPointDefault, () -> { return EngineUtils.localDimensions(root); });
    }
    
    public RootPackage(R root) {
        this(root, new Vector3f(1, 1, 1), new Vector2f(0.5f, 0.5f), () -> { return EngineUtils.localDimensions(root); });
    }
    
    public void setPositiveDirection(boolean x, boolean y, boolean z) {
        int $x = x ? 1 : -1;
        int $y = y ? 1 : -1;
        int $z = z ? 1 : -1;
    
        positiveDirectionVector.set($x, $y, $z);
    }
}
