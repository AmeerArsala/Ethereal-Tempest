/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Function;

/**
 *
 * @author night
 */
public class ParsedMathFunction extends MathFunction {
    private final char parameter;
    private final String expressionStr;
    
    private final Function parsedFunction; 
    private final Argument input;
    
    public ParsedMathFunction(String functionName, char parameter, String expressionStr) {
        this.parameter = parameter;
        this.expressionStr = expressionStr;
        
        parsedFunction = new Function(functionName + "(" + parameter + ") = " + expressionStr);
        input = new Argument(parameter + " = " + 0);
    }

    public char getParameter() {
        return parameter;
    }

    public String getExpressionStr() {
        return expressionStr;
    }

    @Override
    protected float f(float x) {
        input.setArgumentValue(x);
        return (float)parsedFunction.calculate(input);
    }
    
}
