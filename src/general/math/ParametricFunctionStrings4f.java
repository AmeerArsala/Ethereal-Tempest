/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import com.google.gson.annotations.Expose;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import general.math.function.ParsedMathFunction;
import general.math.function.RGBAFunction;

/**
 *
 * @author night
 * 
 * RULE: constants MUST be single-character LETTERS in a function String
 */
public class ParametricFunctionStrings4f {
    private char parameter;
    private String rFunction;
    private String gFunction;
    private String bFunction;
    private String aFunction;
    private String colorMatParam;
    
    @Expose(deserialize = false)
    private RGBAFunction func;

    public ParametricFunctionStrings4f(char parameter, String rFunction, String gFunction, String bFunction, String aFunction, String colorMatParam) {
        this.parameter = parameter;
        this.rFunction = rFunction;
        this.gFunction = gFunction;
        this.bFunction = bFunction;
        this.aFunction = aFunction;
        this.colorMatParam = colorMatParam;
    }
    
    public ParametricFunctionStrings4f() {}
    
    public char getParameter() { return parameter; }
    
    public String getRFunctionString() { return rFunction; }
    public String getGFunctionString() { return gFunction; }
    public String getBFunctionString() { return bFunction; }
    public String getAFunctionString() { return aFunction; }
    
    public String getColorMatParam() { return colorMatParam; }
    public RGBAFunction getRGBAFunction() { return func; }
    
    public ColorRGBA outputColor(float input, ConstantColorRGBA... constantColors) {
        ParsedMathFunction r = (ParsedMathFunction)func.getRFunc();
        ParsedMathFunction g = (ParsedMathFunction)func.getGFunc();
        ParsedMathFunction b = (ParsedMathFunction)func.getBFunc();
        ParsedMathFunction a = (ParsedMathFunction)func.getAFunc();
        
        for (int i = 0; i < constantColors.length; ++i) {
            char name = constantColors[i].constantName;
            ColorRGBA color = constantColors[i].constantColorDef;
            
            r.setConstantValue(name, color.r);
            g.setConstantValue(name, color.g);
            b.setConstantValue(name, color.b);
            a.setConstantValue(name, color.a);
        }
        
        //do a little optimization
        float rVal = rFunction != null ? r.output(input) : 1;
        float gVal = gFunction != null ? g.output(input) : 1;
        float bVal = bFunction != null ? b.output(input) : 1;
        float aVal = bFunction != null ? b.output(input) : 1;
        
        return new ColorRGBA(rVal, gVal, bVal, aVal);
    }
    
    //besides the return type and parameters, this method different from the method above because the default value is 0 instead of 1
    public Vector4f outputVector(float input, ConstantVector4f... constantVecs) {
        ParsedMathFunction x = (ParsedMathFunction)func.getXFunc();
        ParsedMathFunction y = (ParsedMathFunction)func.getYFunc();
        ParsedMathFunction z = (ParsedMathFunction)func.getZFunc();
        ParsedMathFunction w = (ParsedMathFunction)func.getWFunc();
        
        for (int i = 0; i < constantVecs.length; ++i) {
            char name = constantVecs[i].constantName;
            Vector4f vec = constantVecs[i].constantVecDef;
            
            x.setConstantValue(name, vec.x);
            y.setConstantValue(name, vec.y);
            z.setConstantValue(name, vec.z);
            w.setConstantValue(name, vec.w);
        }
        
        //do a little optimization
        float xVal = rFunction != null ? x.output(input) : 0;
        float yVal = gFunction != null ? y.output(input) : 0;
        float zVal = bFunction != null ? z.output(input) : 0;
        float wVal = aFunction != null ? w.output(input) : 0;
        
        return new Vector4f(xVal, yVal, zVal, wVal);
    }
    
    public void initialize(char... constantNames) {
        String rFunc = rFunction != null ? rFunction : "1";
        String gFunc = gFunction != null ? gFunction : "1";
        String bFunc = bFunction != null ? bFunction : "1";
        String aFunc = aFunction != null ? aFunction : "1";
        
        func = new RGBAFunction(
            new ParsedMathFunction("r", parameter, rFunc, constantNames),
            new ParsedMathFunction("g", parameter, gFunc, constantNames),
            new ParsedMathFunction("b", parameter, bFunc, constantNames),
            new ParsedMathFunction("a", parameter, aFunc, constantNames)
        );
    }
    
    public static class ConstantColorRGBA {
        public final char constantName;
        public final ColorRGBA constantColorDef;
        
        public ConstantColorRGBA(char constantName, ColorRGBA constantColorDef) {
            this.constantName = constantName;
            this.constantColorDef = constantColorDef;
        }
    }
    
    public static class ConstantVector4f {
        public final char constantName;
        public final Vector4f constantVecDef;
        
        public ConstantVector4f(char constantName, Vector4f constantVecDef) {
            this.constantName = constantName;
            this.constantVecDef = constantVecDef;
        }
    }
}
