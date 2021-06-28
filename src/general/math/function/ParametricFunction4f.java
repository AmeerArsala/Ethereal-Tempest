/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import com.jme3.math.Vector4f;

/**
 *
 * @author night
 */
public class ParametricFunction4f extends ParametricFunction {
    private final MathFunction b, a; //blue and alpha values in RGBA
    
    public ParametricFunction4f(MathFunction r, MathFunction g, MathFunction b, MathFunction a) {
        super(r, g);
        this.b = b;
        this.a = a;
    }
    
    //copy constructor
    public ParametricFunction4f(ParametricFunction4f rgba) {
        this(rgba.getRFunc(), rgba.getGFunc(), rgba.b, rgba.a);
    }
    
    public ParametricFunction4f(MathFunction[] rgba) {
        super(rgba[0], rgba[1]);
        b = rgba[2];
        a = rgba[3];
    }
    
    public ParametricFunction4f(ParametricFunction3f rgb, MathFunction a) {
        this(adjoin(splice(rgb), a));
    }
    
    public ParametricFunction4f(MathFunction r, ParametricFunction3f gba) {
        this(adjoin(r, splice(gba)));
    }
    
    public ParametricFunction4f(ParametricFunction rg, MathFunction b, MathFunction a) {
        super(rg);
        this.b = b;
        this.a = a;
    }
    
    public ParametricFunction4f(MathFunction r, MathFunction g, ParametricFunction ba) {
        super(r, g);
        MathFunction[] $ba = splice(ba);
        b = $ba[0];
        a = $ba[1];
    }
    
    public ParametricFunction4f(MathFunction r, ParametricFunction gb, MathFunction a) {
        this(r, splice(gb)[0], splice(gb)[1], a);
    }
    
    public Float r(float input) { 
        return x(input); 
    }
    
    public Float g(float input) { 
        return y(input); 
    }
    
    public Float b(float input) {
        return b.output(input);
    }
    
    public Float a(float input) {
        return a.output(input);
    }
    
    public Float w(float input) { //as in xyzw
        return a.output(input);
    }
    
    public MathFunction getRFunc() {
        return getXFunc();
    }
    
    public MathFunction getGFunc() {
        return getYFunc();
    }
    
    public MathFunction getBFunc() {
        return b;
    }
    
    public MathFunction getAFunc() {
        return a;
    }
    
    public MathFunction getZFunc() {
        return b;
    }
    
    public MathFunction getWFunc() {
        return a;
    }
    
    public Vector4f outputVec4(float input) {
        return new Vector4f(x(input), y(input), b.output(input), a.output(input));
    }
    
    
    public static ParametricFunction4f CONSTANT(float r, float g, float b, float a) {
        return new ParametricFunction4f(MathFunction.CONSTANT(r), MathFunction.CONSTANT(g), MathFunction.CONSTANT(b), MathFunction.CONSTANT(a));
    }
    
    public static final ParametricFunction4f ZERO4 = new ParametricFunction4f(MathFunction.CONSTANT(0), MathFunction.CONSTANT(0), MathFunction.CONSTANT(0), MathFunction.CONSTANT(0));
}
