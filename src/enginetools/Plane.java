/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Axis;
import java.util.function.BiFunction;

/**
 *
 * @author night
 */
public enum Plane {
    XY(Axis.Z, (vec, theta) -> { // x is the "x-axis" and y is the "y-axis"
        float radius = FastMath.sqrt(FastMath.sqr(vec.x) + FastMath.sqr(vec.y));
        
        return new Vector3f(FastMath.cos(theta), FastMath.sin(theta), 0).multLocal(radius);
    }),
    XZ(Axis.Y, (vec, theta) -> { // x is the "x-axis" and z is the "y-axis"
        float radius = FastMath.sqrt(FastMath.sqr(vec.x) + FastMath.sqr(vec.z));
        
        return new Vector3f(FastMath.cos(theta), 0, FastMath.sin(theta)).multLocal(radius);
    }),
    ZY(Axis.X, (vec, theta) -> { // z is the "x-axis" and y is the "y-axis"
        float radius = FastMath.sqrt(FastMath.sqr(vec.y) + FastMath.sqr(vec.z));
        
        return new Vector3f(0, FastMath.sin(theta), FastMath.cos(theta)).multLocal(radius);
    });
    
    private final Axis correspondingAxis;
    private final BiFunction<Vector3f, Float, Vector3f> procedure;
    private Plane(Axis correspondingAxis, BiFunction<Vector3f, Float, Vector3f> procedure) {
        this.correspondingAxis = correspondingAxis;
        this.procedure = procedure;
    }
    
    public Vector3f applyPolar(Vector3f vec, float theta) {
        return procedure.apply(vec, theta);
    }
    
    public Axis getCorrespondingAxis() {
        return correspondingAxis;
    }
}
