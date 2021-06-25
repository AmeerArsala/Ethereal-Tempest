/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import com.jme3.math.Vector2f;
import java.util.Objects;

/**
 *
 * @author night
 */
public class FloatPair {
    public float a;
    public float b;
    
    public FloatPair(float a, float b) {
        this.a = a;
        this.b = b;
    }
    
    /*public FloatPair(float a) {
        this.a = a;
        b = null;
    }*/
    
    public float getA() { return a; }
    public float getB() { return b; }
    
    public FloatPair add(float num) {
        a += num;
        b += num;
        
        return this;
    }
    
    public FloatPair add(float num1, float num2) {
        a += num1;
        b += num2;
        
        return this;
    }
    
    public FloatPair addNew(float num1, float num2) {
        return new FloatPair(a + num1, b + num2);
    }
    
    public FloatPair addNew(float num) {
        return new FloatPair(a + num, b + num);
    }
    
    public FloatPair mult(float num) {
        a *= num;
        b *= num;
        
        return this;
    }
    
    public FloatPair mult(float num1, float num2) {
        a *= num1;
        b *= num2;
        
        return this;
    }
    
    public FloatPair multNew(float num) {
        return new FloatPair(a * num, b * num);
    }
    
    public FloatPair multNew(float num1, float num2) {
        return new FloatPair(a * num1, b * num2);
    }
    
    public FloatPair newEquivalentInstance() {
        return new FloatPair(a, b);
    }
    
    public void set(float num1, float num2) {
        a = num1;
        b = num2;
    }
    
    public float bound(float num) {
        if (num < a) {
            return a;
        }
        
        if (num > b) {
            return b;
        }
        
        return num;
    }
    
    public float length() {
        return Math.abs(b - a);
    }
    
    public boolean equivalentTo(FloatPair other) {
        return a == other.a && Objects.equals(b, other.b);
    }
    
    @Override
    public String toString() {
        return a + ", " + b;
    }
    
    public String objectToString() {
        return super.toString();
    }
    
    public Vector2f toVector2f() {
        return new Vector2f(a, b);
    }
}
