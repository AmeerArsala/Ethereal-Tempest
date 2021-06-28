/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import com.jme3.math.Vector3f;
import java.awt.Color;

/**
 *
 * @author night
 */
public class ParametricFunction3f extends ParametricFunction {
    private final MathFunction z;
    
    public ParametricFunction3f(MathFunction xFunc, MathFunction yFunc, MathFunction zFunc) {
        super(xFunc, yFunc);
        z = zFunc;
    }
    
    //copy constructor
    public ParametricFunction3f(ParametricFunction3f xyz) {
        this(xyz.getXFunc(), xyz.getYFunc(), xyz.z);
    } 
    
    public ParametricFunction3f(MathFunction[] xyz) {
        super(xyz[0], xyz[1]);
        z = xyz[2];
    }
    
    public ParametricFunction3f(ParametricFunction xyFuncs, MathFunction zFunc) {
        super(xyFuncs);
        z = zFunc;
    }
    
    public ParametricFunction3f(MathFunction xFunc, ParametricFunction templateYZ) {
        this(xFunc, splice(templateYZ)[0], splice(templateYZ)[1]);
    }
    
    public Float z(float input) {
        return z.output(input);
    }
    
    public Float r(float input) {
        return x(input);
    }
    
    public Float g(float input) {
        return y(input);
    }
    
    public Float b(float input) {
        return z.output(input);
    }
    
    public MathFunction getZFunc() {
        return z;
    }
    
    public MathFunction getRFunc() {
        return getXFunc();
    }
    
    public MathFunction getGFunc() {
        return getYFunc();
    }
    
    public MathFunction getBFunc() {
        return z;
    }
    
    public Vector3f outputVec3(float input) {
        return new Vector3f(x(input), y(input), z.output(input));
    }
    
    public Color outputRGB(float input) {
        return new Color(x(input), y(input), z.output(input));
    }
    
    
    public static MathFunction[] splice(ParametricFunction3f toSplice) {
        MathFunction[] xy = splice((ParametricFunction)toSplice);
        return new MathFunction[] { xy[0], xy[1], toSplice.z };
    }
    
    
    public static final ParametricFunction3f ZERO3 = new ParametricFunction3f(MathFunction.CONSTANT(0), MathFunction.CONSTANT(0), MathFunction.CONSTANT(0));
}
