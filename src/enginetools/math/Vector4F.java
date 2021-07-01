/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enginetools.math;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import general.math.FloatOperation;
import general.math.function.ControlledMathFunction.Operation;
import java.util.Random;

/**
 *
 * @author night
 */
public class Vector4F {
    public Float x;
    public Float y;
    public Float z;
    public Float w;
    
    public Vector4F() {}
    
    public Vector4F(Float x, Float y, Float z, Float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4F(Vector4F copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
        w = copy.w;
    }
    
    public Vector4F(Vector4f definedVec) {
        x = definedVec.x;
        y = definedVec.y;
        z = definedVec.z;
        w = definedVec.w;
    }
    
    public Vector4f toVector4f() {
        return new Vector4f(x, y, z, w);
    }
    
    public Vector4f toVector4f(float defaultValue) {
        return toVector4f(defaultValue, defaultValue, defaultValue, defaultValue);
    }
    
    public Vector4f toVector4f(float defaultX, float defaultY, float defaultZ, float defaultW) {
        return new Vector4f(
            x != null ? x : defaultX, 
            y != null ? y : defaultY, 
            z != null ? z : defaultZ,
            w != null ? w : defaultW
        );
    }
    
    public static Vector4f fit(Vector2f vec) {
        return new Vector4f(vec.x, vec.y, 0, 0);
    }
    
    public static Vector4f fit(Vector2f vec, float z) {
        return new Vector4f(vec.x, vec.y, z, 0);
    }
    
    public static Vector4f fit(Vector2f vec, float z, float w) {
        return new Vector4f(vec.x, vec.y, z, w);
    }
    
    public static Vector4f fit(Vector2f xy, Vector2f zw) {
        return new Vector4f(xy.x, xy.y, zw.x, zw.y);
    }
    
    public static Vector4f fit(Vector3f vec) {
        return new Vector4f(vec.x, vec.y, vec.z, 0);
    }
    
    public static Vector4f fit(Vector3f vec, float w) {
        return new Vector4f(vec.x, vec.y, vec.z, w);
    }
    
    public static Vector4f fill(float defaultValue) {
        return new Vector4f(defaultValue, defaultValue, defaultValue, defaultValue);
    }
    
    public static Vector4f random() {
        return new Vector4f(FastMath.nextRandomFloat(), FastMath.nextRandomFloat(), FastMath.nextRandomFloat(), FastMath.nextRandomFloat());
    }
    
    public static Vector4f javaRandom() {
        return new Vector4f((float)Math.random(), (float)Math.random(), (float)Math.random(), (float)Math.random());
    }
    
    public static ColorRGBA randomGaussianColor() {
        Random r = FastMath.rand;
        return new ColorRGBA((float)r.nextGaussian(), (float)r.nextGaussian(), (float)r.nextGaussian(), (float)r.nextGaussian());
    }
    
    public static Vector4f abs(Vector4f vec) {
        return new Vector4f(FastMath.abs(vec.x), FastMath.abs(vec.y), FastMath.abs(vec.z), FastMath.abs(vec.w));
    } 
    
    public static Vector4f absLocal(Vector4f vec) {
        vec.x = FastMath.abs(vec.x);
        vec.y = FastMath.abs(vec.y);
        vec.z = FastMath.abs(vec.z);
        vec.w = FastMath.abs(vec.w);
        
        return vec;
    }
    
    public static Vector4f pow(Vector4f vec, float pwr) {
        return new Vector4f(FastMath.pow(vec.x, pwr), FastMath.pow(vec.y, pwr), FastMath.pow(vec.z, pwr), FastMath.pow(vec.w, pwr));
    }
    
    public static Vector4f powLocal(Vector4f vec, float pwr) {
        vec.x = FastMath.pow(vec.x, pwr);
        vec.y = FastMath.pow(vec.y, pwr);
        vec.z = FastMath.pow(vec.z, pwr);
        vec.w = FastMath.pow(vec.w, pwr);
        
        return vec;
    }
    
    public static Vector4f pow(Vector4f vec, Vector4f pows) {
        return new Vector4f(FastMath.pow(vec.x, pows.x), FastMath.pow(vec.y, pows.y), FastMath.pow(vec.z, pows.z), FastMath.pow(vec.w, pows.w));
    }
    
    public static Vector4f powLocal(Vector4f vec, Vector4f pows) {
        vec.x = FastMath.pow(vec.x, pows.x);
        vec.y = FastMath.pow(vec.y, pows.y);
        vec.z = FastMath.pow(vec.z, pows.z);
        vec.w = FastMath.pow(vec.w, pows.w);
        
        return vec;
    }
    
    public static Vector4f sqrt(Vector4f vec) {
        return new Vector4f(FastMath.sqrt(vec.x), FastMath.sqrt(vec.y), FastMath.sqrt(vec.z), FastMath.sqrt(vec.w));
    }
    
    public static Vector4f sqrtLocal(Vector4f vec) {
        vec.x = FastMath.sqrt(vec.x);
        vec.y = FastMath.sqrt(vec.y);
        vec.z = FastMath.sqrt(vec.z);
        vec.w = FastMath.sqrt(vec.w);
        
        return vec;
    }
    
    public static Vector4f operate(Vector4f vec, FloatOperation operation) {
        return new Vector4f(operation.calculate(vec.x), operation.calculate(vec.y), operation.calculate(vec.z), operation.calculate(vec.w));
    }
    
    public static Vector4f operateLocal(Vector4f vec, FloatOperation operation) {
        vec.x = operation.calculate(vec.x);
        vec.y = operation.calculate(vec.y);
        vec.z = operation.calculate(vec.z);
        vec.w = operation.calculate(vec.w);
        
        return vec;
    }
}
