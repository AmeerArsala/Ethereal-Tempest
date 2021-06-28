/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

/**
 *
 * @author night
 */
public class Vector2F {
    public Float x;
    public Float y;
    
    public Vector2F() {}
    
    public Vector2F(Float x, Float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2F(Vector2F copy) {
        x = copy.x;
        y = copy.y;
    }
    
    public Vector2F(Vector2f definedVec) {
        x = definedVec.x;
        y = definedVec.y;
    }
    
    public Vector2f toVector2f() {
        return new Vector2f(x, y);
    }
    
    public Vector2f toVector2f(float defaultValue) {
        return toVector2f(defaultValue, defaultValue);
    }
    
    public Vector2f toVector2f(float defaultX, float defaultY) {
        return new Vector2f(
            x != null ? x : defaultX, 
            y != null ? y : defaultY
        );
    }
    
    public static Vector2f fill(float defaultValue) {
        return new Vector2f(defaultValue, defaultValue);
    }
    
    public static Vector2f random() {
        return new Vector2f(FastMath.nextRandomFloat(), FastMath.nextRandomFloat());
    }
    
    public static Vector2f javaRandom() {
        return new Vector2f((float)Math.random(), (float)Math.random());
    }
    
    public static Vector2f abs(Vector2f vec) {
        return new Vector2f(FastMath.abs(vec.x), FastMath.abs(vec.y));
    } 
    
    public static Vector2f absLocal(Vector2f vec) {
        vec.x = FastMath.abs(vec.x);
        vec.y = FastMath.abs(vec.y);
        
        return vec;
    }
}
