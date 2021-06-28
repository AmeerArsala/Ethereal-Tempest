/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import com.google.gson.annotations.Expose;
import com.jme3.math.Vector3f;
import general.math.function.ParametricFunction3f;
import general.math.function.ParsedMathFunction;

/**
 *
 * @author night
 * 
 * RULE: constants MUST be single-character LETTERS in a function String
 */
public class ParametricFunctionStrings3f {
    private char parameter;
    private String xFunction;
    private String yFunction;
    private String zFunction;
    
    @Expose(deserialize = false)
    private ParametricFunction3f func;

    public ParametricFunctionStrings3f(char parameter, String xFunction, String yFunction, String zFunction) {
        this.parameter = parameter;
        this.xFunction = xFunction;
        this.yFunction = yFunction;
        this.zFunction = zFunction;
    }
    
    public ParametricFunctionStrings3f() {}
    
    public char getParameter() { return parameter; }
    
    public String getXFunctionString() { return xFunction; }
    public String getYFunctionString() { return yFunction; }
    public String getZFunctionString() { return zFunction; }
    
    public ParametricFunction3f getFunction() { return func; }
    
    public Vector3f outputVector(float input, ConstantVector3f... constantVecs) {
        ParsedMathFunction x = (ParsedMathFunction)func.getXFunc();
        ParsedMathFunction y = (ParsedMathFunction)func.getYFunc();
        ParsedMathFunction z = (ParsedMathFunction)func.getZFunc();
        
        for (int i = 0; i < constantVecs.length; ++i) {
            char name = constantVecs[i].constantName;
            Vector3f vec = constantVecs[i].constantVecDef;
            
            x.setConstantValue(name, vec.x);
            y.setConstantValue(name, vec.y);
            z.setConstantValue(name, vec.z);
        }
        
        //do a little optimization
        float xVal = xFunction != null ? x.output(input) : 0;
        float yVal = yFunction != null ? y.output(input) : 0;
        float zVal = zFunction != null ? z.output(input) : 0;
        
        return new Vector3f(xVal, yVal, zVal);
    }
    
    public void initialize(char... constantNames) {
        String xFunc = xFunction != null ? xFunction : "0";
        String yFunc = yFunction != null ? yFunction : "0";
        String zFunc = zFunction != null ? zFunction : "0";
        
        func = new ParametricFunction3f(
            new ParsedMathFunction("x", parameter, xFunc, constantNames),
            new ParsedMathFunction("y", parameter, yFunc, constantNames),
            new ParsedMathFunction("z", parameter, zFunc, constantNames)
        );
    }
    
    public static class ConstantVector3f {
        public final char constantName;
        public final Vector3f constantVecDef;
        
        public ConstantVector3f(char constantName, Vector3f constantVecDef) {
            this.constantName = constantName;
            this.constantVecDef = constantVecDef;
        }
    }
}
