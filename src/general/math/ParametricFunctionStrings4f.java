/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import com.google.gson.annotations.Expose;
import general.math.function.ParametricFunction4f;
import general.math.function.ParsedMathFunction;
import general.math.function.RGBAFunction;

/**
 *
 * @author night
 */
public class ParametricFunctionStrings4f {
    private char parameter;
    private String rFunction;
    private String gFunction;
    private String bFunction;
    private String aFunction;
    private String colorMatParam;
    
    @Expose(deserialize = false)
    private ParametricFunction4f func;
    
    @Expose(deserialize = false)
    private RGBAFunction rgbaFunc;

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
    
    public ParametricFunction4f getFunction() { return func; }
    public RGBAFunction getRGBAFunction() { return rgbaFunc; }
    
    public void initialize() {
        String rFunc = rFunction != null ? rFunction : "1";
        String gFunc = gFunction != null ? gFunction : "1";
        String bFunc = bFunction != null ? bFunction : "1";
        String aFunc = aFunction != null ? aFunction : "1";
        
        func = new ParametricFunction4f(
            new ParsedMathFunction("r", parameter, rFunc),
            new ParsedMathFunction("g", parameter, gFunc),
            new ParsedMathFunction("b", parameter, bFunc),
            new ParsedMathFunction("a", parameter, aFunc)
        );
        
        rgbaFunc = new RGBAFunction(func);
    }
}
