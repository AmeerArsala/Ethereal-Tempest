/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author night
 */
public class Vector3F {
    public Float x;
    public Float y;
    public Float z;
    
    public Vector3F() {}
    
    public Vector3F(Float x, Float y, Float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3F(Vector3F copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }
    
    public Vector3F(Vector3f definedVec) {
        x = definedVec.x;
        y = definedVec.y;
        z = definedVec.z;
    }
    
    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }
    
    public Vector3f toVector3f(float defaultValue) {
        return toVector3f(defaultValue, defaultValue, defaultValue);
    }
    
    public Vector3f toVector3f(float defaultX, float defaultY, float defaultZ) {
        return new Vector3f(
            x != null ? x : defaultX, 
            y != null ? y : defaultY, 
            z != null ? z : defaultZ
        );
    }
    
    public static Vector3f fit(Vector2f vec) {
        return new Vector3f(vec.x, vec.y, 0f);
    }
    
    public static Vector3f fit(Vector2f vec, float z) {
        return new Vector3f(vec.x, vec.y, z);
    }
    
    public static Vector3f fill(float defaultValue) {
        return new Vector3f(defaultValue, defaultValue, defaultValue);
    }
    
    public static Vector3f random() {
        return new Vector3f(FastMath.nextRandomFloat(), FastMath.nextRandomFloat(), FastMath.nextRandomFloat());
    }
    
    public static Vector3f javaRandom() {
        return new Vector3f((float)Math.random(), (float)Math.random(), (float)Math.random());
    }
    
    public static Vector3f abs(Vector3f vec) {
        return new Vector3f(FastMath.abs(vec.x), FastMath.abs(vec.y), FastMath.abs(vec.z));
    } 
    
    public static Vector3f absLocal(Vector3f vec) {
        vec.x = FastMath.abs(vec.x);
        vec.y = FastMath.abs(vec.y);
        vec.z = FastMath.abs(vec.z);
        
        return vec;
    }
}
