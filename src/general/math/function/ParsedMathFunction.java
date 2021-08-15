/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import general.math.FloatVariable;
import java.util.ArrayList;
import java.util.List;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

/**
 *
 * @author night
 */
public class ParsedMathFunction extends MathFunction {
    private final String functionName;
    private final char parameter;
    private final String expressionStr;
    private String decipheredExpression;
    
    private final Function parsedFunction;
    private final Argument input;
    private final FloatVariable[] constants;
    
    public ParsedMathFunction(String functionName, char parameter, String expressionStr, FloatVariable... constants) {
        this.functionName = functionName;
        this.parameter = parameter;
        this.expressionStr = expressionStr;
        this.constants = constants;
        
        updateDecipheredExpression();
        parsedFunction = new Function(functionName + "(" + parameter + ")=" + decipheredExpression);
        input = new Argument(parameter + "=" + 0); //set to 0 for the time being
    }
    
    public ParsedMathFunction(String functionName, char parameter, String expressionStr, char[] constantNames) {
        this(functionName, parameter, expressionStr, forgeConstants(constantNames));
    }
    
    public static FloatVariable[] forgeConstants(char[] names) {
        FloatVariable[] constants = new FloatVariable[names.length];
        for (int i = 0; i < constants.length; i++) {
            constants[i] = new FloatVariable(names[i]); //set to 0 for the time being
        }
        
        return constants;
    }
    
    public String getFuncString() {
        return functionName + "(" + parameter + ") = " + decipheredExpression;
    }

    public char getParameter() {
        return parameter;
    }

    public String getExpressionStr() {
        return expressionStr;
    }
    
    public int getConstantsLength() {
        return constants.length;
    }
    
    public char getConstantChar(int index) {
        return constants[index].getChar();
    }
    
    public float getConstantValue(int index) {
        return constants[index].getVal();
    }
    
    public float getConstantValue(char name) {
        return constants[getConstantIndex(name)].getVal();
    }
    
    public int getConstantIndex(char name) {
        for (int i = 0; i < constants.length; i++) {
            if (constants[i].getChar() == name) {
                return i;
            } 
        }
        
        return -1;
    }
    
    public void setConstantValue(int index, float val) {
        constants[index].setVal(val);
        
        updateDecipheredExpression();
        parsedFunction.setFunction(getFuncString());
    }
    
    public void setConstantValue(char name, float val) {
        constants[getConstantIndex(name)].setVal(val);
        
        updateDecipheredExpression();
        parsedFunction.setFunction(getFuncString());
    }
    
    public final void updateDecipheredExpression() {
        decipheredExpression = expressionStr;
        for (FloatVariable constant : constants) {
            decipheredExpression = constant.decipher(decipheredExpression);
        }
    }
    
    public String callOf(float inputVal) {
        return functionName + "(" + inputVal + ")";
    }

    @Override
    protected float f(float x) {
        input.setArgumentValue(x);
        Expression outcome = new Expression(functionName + "(" + parameter + ")", input);
        outcome.addDefinitions(parsedFunction);
        
        float result = (float)outcome.calculate();
        //System.out.println("Expression: " + parsedFunction.getFunctionExpressionString() + " -> " + result);
        return result;
    }
    
}
