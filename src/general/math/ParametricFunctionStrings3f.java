/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import com.google.gson.annotations.Expose;
import general.math.function.ParametricFunction3f;
import general.math.function.ParsedMathFunction;

/**
 *
 * @author night
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
    
    public void initialize() {
        String xFunc = xFunction != null ? xFunction : "0";
        String yFunc = yFunction != null ? yFunction : "0";
        String zFunc = zFunction != null ? zFunction : "0";
        
        func = new ParametricFunction3f(
            new ParsedMathFunction("x", parameter, xFunc),
            new ParsedMathFunction("y", parameter, yFunc),
            new ParsedMathFunction("z", parameter, zFunc)
        );
    }
}
