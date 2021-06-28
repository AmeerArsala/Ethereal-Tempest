/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import org.mariuszgromada.math.mxparser.Function;

/**
 *
 * @author night
 */
public class StandardParsedMathFunction extends MathFunction {
    private final Function parsedFunc; 
    private final String expressionStr;
    
    private int activeParamIndex;
    private boolean randomizeParamUsed = false;
    
    public StandardParsedMathFunction(Function parsedFunc) { //arguments must be added BEFORE this is instantiated
        this(parsedFunc, 0);
    }
    
    //uses only one changing parameter
    public StandardParsedMathFunction(Function parsedFunc, int activeParamIndex) {
        this.parsedFunc = parsedFunc;
        this.activeParamIndex = activeParamIndex;
        
        expressionStr = parsedFunc.getFunctionExpressionString();
    }
    
    public String getExpressionStr() {
        return expressionStr;
    }
    
    public int getActiveParameterIndex() {
        return activeParamIndex;
    }
    
    public boolean randomizeParameterUsed() {
        return randomizeParamUsed;
    }
    
    public String getActiveParameter() {
        return parsedFunc.getArgument(activeParamIndex).getArgumentName();
    }
    
    public String getParameterName(int index) {
        return parsedFunc.getArgument(index).getArgumentName();
    }
    
    public int getParameterIndex(String paramName) {
        return parsedFunc.getArgumentIndex(paramName);
    }
    
    public double getParameterValue(int index) {
        return parsedFunc.getArgument(index).getArgumentValue();
    }
    
    public double getActiveParameterValue() {
        return parsedFunc.getArgument(activeParamIndex).getArgumentValue();
    }
    
    public void setActiveParameterIndex(int index) {
        activeParamIndex = index;
    }
    
    public void setRandomizeParameterUsed(boolean randomize) {
        randomizeParamUsed = randomize;
    }
    
    public void setActiveParameter(String param) {
        activeParamIndex = getParameterIndex(param);
    }
    
    public void setParameterValue(String param, double value) {
        parsedFunc.setArgumentValue(parsedFunc.getArgumentIndex(param), value);
    }
    
    public void setParameterValue(int index, double value) {
        parsedFunc.setArgumentValue(index, value);
    }
    
    public void setActiveParameterValue(double value) {
        parsedFunc.setArgumentValue(activeParamIndex, value);
    }
    
    private void randomizeWhichParameterUsed() {
        activeParamIndex = (int)(parsedFunc.getArgumentsNumber() * Math.random());
    }

    @Override
    protected float f(float x) {
        if (randomizeParamUsed) {
            randomizeWhichParameterUsed();
        }
        
        parsedFunc.setArgumentValue(activeParamIndex, x);
        return (float)parsedFunc.calculate();
    }
}
