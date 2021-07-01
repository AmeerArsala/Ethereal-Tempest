/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools.math;

import java.io.Serializable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import general.math.FloatOperation;
//import general.math.function.ControlledMathFunction.Operation;

/**
 *
 * @author night
 */
public class Vector2F implements Serializable {
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
    
    public static Vector2f pow(Vector2f vec, float pwr) {
        return new Vector2f(FastMath.pow(vec.x, pwr), FastMath.pow(vec.y, pwr));
    }

    public static Vector2f powLocal(Vector2f vec, float pwr) {
        vec.x = FastMath.pow(vec.x, pwr);
        vec.y = FastMath.pow(vec.y, pwr);
        
        return vec;
    }
    
    public static Vector2f pow(Vector2f vec, Vector2f pows) {
        return new Vector2f(FastMath.pow(vec.x, pows.x), FastMath.pow(vec.y, pows.y));
    }
    
    public static Vector2f powLocal(Vector2f vec, Vector2f pows) {
        vec.x = FastMath.pow(vec.x, pows.x);
        vec.y = FastMath.pow(vec.y, pows.y);
        
        return vec;
    }
    
    public static Vector2f sqrt(Vector2f vec) {
        return new Vector2f(FastMath.sqrt(vec.x), FastMath.sqrt(vec.y));
    }
    
    public static Vector2f sqrtLocal(Vector2f vec) {
        vec.x = FastMath.sqrt(vec.x);
        vec.y = FastMath.sqrt(vec.y);
        
        return vec;
    }
    
    public static Vector2f operate(Vector2f vec, FloatOperation operation) {
        return new Vector2f(operation.calculate(vec.x), operation.calculate(vec.y));
    }
    
    public static Vector2f operateLocal(Vector2f vec, FloatOperation operation) {
        vec.x = operation.calculate(vec.x);
        vec.y = operation.calculate(vec.y);
        
        return vec;
    }
}
