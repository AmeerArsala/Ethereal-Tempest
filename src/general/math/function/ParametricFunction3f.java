/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

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
    
    
    public static MathFunction[] splice(ParametricFunction3f toSplice) {
        MathFunction[] xy = splice((ParametricFunction)toSplice);
        return new MathFunction[] { xy[0], xy[1], toSplice.z };
    }
    
    
    public static final ParametricFunction3f ZERO3 = new ParametricFunction3f(MathFunction.CONSTANT(0), MathFunction.CONSTANT(0), MathFunction.CONSTANT(0));
}
