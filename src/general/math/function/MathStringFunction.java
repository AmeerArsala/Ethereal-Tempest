/** 
 * Parentheses
 * Exponents
 * Multiplication
 * Division
 * Addition
 * Subtraction
 * . (PEMDAS)
 */
package general.math.function;

import general.math.IntPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ameer Arsala
 * 
 * rules: do not put two operators next to each other, besides parentheses; don't do "5--x" or "3**x" or "8//x" or "1++x" 
 * 
 */
public class MathStringFunction extends MathFunction { // will use float values
    private final String originalFunctionString;
    private final char independentVar; // x name; if it is just called x, it would be 'x'
    private final List<Term> terms;

    public MathStringFunction(char independentVariable, String functionStr) { // example parameters: 'x', "5x + 4"
        originalFunctionString = "" + functionStr;
        independentVar = independentVariable;
        terms = parseTerms(new ScopeDepthTracker(functionStr.replaceAll("- ", "+ -") + " ", 0, 0)); //converts for use with negative addition instead of subtraction
    }
    
    private MathStringFunction(char independentVariable, String functionStr, List<Term> functionTerms) {
        originalFunctionString = "" + functionStr;
        independentVar = independentVariable;
        terms = functionTerms;
    }

    public char getVariableCharacter() { return independentVar; }

    private List<Term> parseTerms(ScopeDepthTracker functionDepth) {
        List<Term> parsed = new ArrayList<>();
        String funcStr = functionDepth.getLiteral();
        int termsToGo = 1, previousParentheticalDepth = -1;
        int startTracing = -1;
        for (int i = 0; i < funcStr.length(); ++i) {
            if (funcStr.charAt(i) == '+') {
                ++termsToGo;
            } else if (termsToGo > 0) {
                if (functionDepth.parentheticalDepths[i] != previousParentheticalDepth) {
                    if (functionDepth.parentheticalDepths[i] >= 0) {
                        startTracing = i;
                    } else {
                        parsed.add(new Term(functionDepth.substring(startTracing, i)));
                        startTracing = -1;
                        --termsToGo;
                    }
                }

                if (funcStr.charAt(i) == '(') { //skip forward
                    i = functionDepth.getParenthesesMatcher().getYByX(i);
                }
            }

            previousParentheticalDepth = functionDepth.parentheticalDepths[i];
        }

        return parsed;
    }
    
    
    @Override
    protected float f(float x) {
        float sum = 0;
        for (Term term : terms) {
            sum += term.runOperations(x);
        }

        return sum;
    }

    @Override
    public String toString() {
        return originalFunctionString;
    }

    private class Term {
        private final ScopeDepthTracker termDepth;
        private final List<Operand> operands = new ArrayList<>();
        
        //these ArrayLists collect the indexes of the String of the multipliers, exponents, etc.
        //term-modifying operations (multiplication, division, exponentiation)
        //these are all in order; indexes for the operands list
        private final ArrayList<Integer> exponentOperationPointers = new ArrayList<>();

        private final ArrayList<Integer> multiplicationOperationPointers = new ArrayList<>();
        private final ArrayList<Boolean> divisionOperationDecider = new ArrayList<>(); //if at the index, it is division, then true


        private void addOn(Character lastSign, int a, int b) {
            if (lastSign == null) {
                multiplicationOperationPointers.add(operands.size());
                divisionOperationDecider.add(false);
            } else { //a and b are for substring values
                switch (lastSign) {
                    case '^':
                        exponentOperationPointers.add(operands.size());
                        break;
                    default:
                        multiplicationOperationPointers.add(operands.size());
                        divisionOperationDecider.add(lastSign == '/');
                        break;
                }
            }

            operands.add(new Operand(termDepth.substring(a, b)));
        }

        public Term(ScopeDepthTracker termDepth) {
            this.termDepth = termDepth;

            String termLiteral = "" + termDepth.getLiteral();
            int currentNumberRecordingStartIndex = -1, currentNumberRecordingEndIndex = -1;
            Character lastSign = null;
            for (int i = 0; i < termLiteral.length(); ++i) {
                if (termLiteral.charAt(i) == '(') {
                    if (currentNumberRecordingStartIndex != -1) {
                        addOn('*', currentNumberRecordingStartIndex, currentNumberRecordingEndIndex + 1);
                        currentNumberRecordingStartIndex = -1;
                        currentNumberRecordingEndIndex = -1;
                    }

                    int closingIndex = termDepth.getParenthesesMatcher().getYByX(i);
                    if (i - 1 >= 0 && termDepth.isValidCharacter(termLiteral.charAt(i - 1))) {
                        addOn('*', i, closingIndex + 1);
                    } else {
                        addOn(lastSign, i, closingIndex + 1);
                    }
                    i = closingIndex;
                } else if (termLiteral.charAt(i) == independentVar) {
                    if (currentNumberRecordingStartIndex != -1) {
                        addOn('*', currentNumberRecordingStartIndex, currentNumberRecordingEndIndex + 1); //the number
                        currentNumberRecordingStartIndex = -1;
                        currentNumberRecordingEndIndex = -1;
                        addOn('*', i, i + 1); //the variable
                    } else {
                        addOn(lastSign, i, i + 1); //the variable
                    }
                } else if (termDepth.isValidCharacter(termLiteral.charAt(i))) { //it is a digit or decimal point
                    if (currentNumberRecordingStartIndex == -1) {
                        currentNumberRecordingStartIndex = i;
                        currentNumberRecordingEndIndex = i;
                    } else {
                        ++currentNumberRecordingEndIndex;
                    }
                } else if (termLiteral.charAt(i) != ' ') { //a sign like multiplication, division, or power
                    if (currentNumberRecordingStartIndex != -1) {
                        addOn(lastSign, currentNumberRecordingStartIndex, currentNumberRecordingEndIndex + 1);
                        currentNumberRecordingStartIndex = -1;
                        currentNumberRecordingEndIndex = -1;
                    }

                    lastSign = termLiteral.charAt(i);
                }
            }

            if (currentNumberRecordingStartIndex != -1) {
                addOn(lastSign, currentNumberRecordingStartIndex, currentNumberRecordingEndIndex + 1);
            }
        }

        public List<Operand> getOperands() { return operands; }
        public ScopeDepthTracker getDepthReading() { return termDepth; }

        public float runOperations(float xvalue) { // xvalue is the input
            List<ArrayList<Integer>> specificOperandPointers = Arrays.asList(exponentOperationPointers, multiplicationOperationPointers);
            List<Operation> operations = Arrays.asList(Operation.EXPONENT, Operation.MULTIPLICATION);

            float[] values = new float[operands.size()];
            boolean[] indexAvailability = new boolean[operands.size()];

            for (int i = 0; i < values.length; ++i) {
                values[i] = operands.get(i).runInnerOperations(xvalue);
                indexAvailability[i] = true;
            }

            float output = 1;
            for (int o = 0; o < specificOperandPointers.size(); ++o) {
                List<Float> vals = new ArrayList<>();

                for (int i = 0; i < specificOperandPointers.get(o).size(); ++i) {
                    int index = specificOperandPointers.get(o).get(i);
                    if (indexAvailability[index]) {
                        if (o == 1 && divisionOperationDecider.get(i)) {
                            vals.add(1f / values[index]);
                        } else {
                            vals.add(values[index]);
                        }
                        indexAvailability[index] = false;
                    }
                }

                output *= operations.get(o).operateAll(vals);
            }

            return output;
        }
    }

    //in this case, an operand can be (...), a known number, or a variable, which is denoted by singleValue
    private class Operand { //a Term is always an Operand, but an Operand is not always a Term
        private ScopeDepthTracker operandDepth;

        private OperandAttribute singleValue; //can be a variable or known number, but null if parentheses
        private List<Term> innerTerms; //if the operand is parentheses, it will have inner terms

        public Operand(ScopeDepthTracker operandDepth) {
            this.operandDepth = operandDepth;

            String operandLiteral = operandDepth.getLiteral();
            if (operandDepth.isSurroundedByParentheses()) { //parentheses
                innerTerms = parseTerms(operandDepth.substring(1, operandLiteral.length() - 1)); //parse terms with removed parentheses
                singleValue = null; //denotes parentheses
            } else {
                innerTerms = null;

                if (operandLiteral.equals(Character.toString(independentVar))) { // operand is a variable
                    singleValue = new OperandAttribute();
                } else { // operand is a known number
                    if (operandLiteral.equals("-")) {
                        singleValue = new OperandAttribute(-1);
                    } else {
                        singleValue = new OperandAttribute(Integer.parseUnsignedInt(operandLiteral));
                    }
                }
            }
        }

        public ScopeDepthTracker getDepthReading() { return operandDepth; }

        public OperandAttribute getSolitaryValue() { return singleValue; }
        public List<Term> getInnerTerms() { return innerTerms; }

        public boolean isParentheses() { return innerTerms != null; }

        public float runInnerOperations(float xvalue) { //xvalue is the input
            if (innerTerms == null) {
                return singleValue.isVariable() ? xvalue : singleValue.getValue();
            }

            float termSum = 0;
            for (Term innerTerm : innerTerms) {
                termSum += innerTerm.runOperations(xvalue);
            }

            return termSum;
        }
    }

    private class OperandAttribute {
        private float value;
        private final boolean isVariable;

        public OperandAttribute(float val) { // specified number
            value = val;
            isVariable = false;
        }

        public OperandAttribute() { // variable
            isVariable = true;
        }

        public float getValue() { return value; }
        public boolean isVariable() { return isVariable; }
    }

    private class ScopeDepthTracker { //depth for every char in a String
        private static final char OPEN_PARENTHESIS = '(';
        private static final char CLOSE_PARENTHESIS = ')';
        private static final char EXPONENT = '^';
        private static final char SPACE = ' ';

        /**
         * parentheticalDepth is the number of pairs of parentheses an operand or term is within
         * The term/operand '5x' in "5x + 4" has a parentheticalDepth of 0
         * The operand/inner term '3x' in "5(3x + 1) - 8" has a parentheticalDepth of 1
         * The operand 'x^2' in "3(5 + (2x * x^2) + x)" has a parentheticalDepth of 2
         * ----------------------------------------------------------------------------------
         * exponentialDepth is the nth exponential operation on an operand or term
         * The exponential term/operand '(x + 5)' in "5x^3^(x + 5)" has an exponentialDepth of 2
         * The exponential term/operand '3' in "x^3" has an exponentialDepth of 1
         * The term/operand 'x' in "x - 9" has an exponentialDepth of 0
         * ----------------------------------------------------------------------------------
         * For both depths, if it is an invalid character, it will return -1
         */
        public final int[] parentheticalDepths;
        public final int[] exponentialDepths;

        private int baselineParentheticalDepth, baselineExponentialDepth;

        private final IntMatcher parentheses;
        private final String str;

        public ScopeDepthTracker(String str, int baseParentheticalDepth, int baseExponentialDepth) {
            this.str = str;
            parentheses = new IntMatcher();
            parentheticalDepths = new int[str.length()];
            exponentialDepths = new int[str.length()];

            baselineParentheticalDepth = Integer.MAX_VALUE;
            baselineExponentialDepth = Integer.MAX_VALUE;

            int subtractExponentOnSpaceOfAddedParentheticalDepth = -1; // -1 means it won't do it, because depth is never < 0

            int addedParentheticalDepth = 0;
            int addedExponentialDepth = 0;
            for (int i = 0; i < parentheticalDepths.length; ++i) {
                switch (str.charAt(i)) {
                    case OPEN_PARENTHESIS:
                        ++addedParentheticalDepth;
                        parentheses.addX(i);

                        parentheticalDepths[i] = baseParentheticalDepth + addedParentheticalDepth;
                        exponentialDepths[i] = baseExponentialDepth + addedExponentialDepth;
                        break;
                    case CLOSE_PARENTHESIS:
                        parentheticalDepths[i] = baseParentheticalDepth + addedParentheticalDepth;
                        exponentialDepths[i] = baseExponentialDepth + addedExponentialDepth;

                        --addedParentheticalDepth;
                        parentheses.addY(i);
                        break;
                    case EXPONENT:
                        ++addedExponentialDepth;
                        subtractExponentOnSpaceOfAddedParentheticalDepth = addedParentheticalDepth;

                        parentheticalDepths[i] = baseParentheticalDepth + addedParentheticalDepth;
                        exponentialDepths[i] = baseExponentialDepth + addedExponentialDepth;
                        break;
                    case SPACE:
                        if (addedParentheticalDepth == subtractExponentOnSpaceOfAddedParentheticalDepth) {
                            --addedExponentialDepth;
                        }
                    default:
                        if (isValidCharacter(str.charAt(i))) { //if it's a digit, variable, decimal point, or negative sign
                            parentheticalDepths[i] = baseParentheticalDepth + addedParentheticalDepth;
                            exponentialDepths[i] = baseExponentialDepth + addedExponentialDepth;
                        } else {
                            parentheticalDepths[i] = -1;
                            exponentialDepths[i] = -1;
                        }
                        break;
                }

                if (baseParentheticalDepth + addedParentheticalDepth < baselineParentheticalDepth) {
                    baselineParentheticalDepth = baseParentheticalDepth + addedParentheticalDepth;
                }

                if (baseExponentialDepth + addedExponentialDepth < baselineExponentialDepth) {
                    baselineExponentialDepth = baseExponentialDepth + addedExponentialDepth;
                }
            }
        }

        private ScopeDepthTracker(String str, IntMatcher parentheses, int[] parenthesesDepths, int[] exponentsDepths, int baselineParenthesesDepth, int baselineExponentsDepth) {
            this.str = str;
            this.parentheses = parentheses;
            parentheticalDepths = parenthesesDepths;
            exponentialDepths = exponentsDepths;
            baselineParentheticalDepth = baselineParenthesesDepth;
            baselineExponentialDepth = baselineExponentsDepth;
        }

        //valid if it is a digit, variable, or decimal point
        public final boolean isValidCharacter(char ch) {
            return
                    ch == independentVar || ch == '.' || ch == '-' //variable or decimal point or negative sign
                            || ch == '0' || ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6' || ch == '7' || ch == '8' || ch == '9'; //digits
        }

        public boolean isSurroundedByParentheses() {
            return parentheses.containsXY(0, str.length() - 1);
        }

        public IntMatcher getParenthesesMatcher() { return parentheses; }

        public IntPair getPair(int i) {
            return parentheses.get(i);
        }

        public int getBaselineParentheticalDepth() { return baselineParentheticalDepth; }
        public int getBaselineExponentialDepth() { return baselineExponentialDepth; }

        public String getLiteral() {
            return str;
        }

        public ScopeDepthTracker substring(int start, int end) { // [start, end)
            int[] parenthesesDepths = new int[end - start];
            int[] exponentsDepths = new int[end - start];

            int baselineParenthesesDepth = Integer.MAX_VALUE;
            int baselineExponentsDepth = Integer.MAX_VALUE;

            for (int i = start; i < end; ++i) {
                parenthesesDepths[i - start] = parentheticalDepths[i];
                exponentsDepths[i - start] = exponentialDepths[i];

                if (parentheticalDepths[i] != -1 && parentheticalDepths[i] < baselineParenthesesDepth) {
                    baselineParenthesesDepth = parentheticalDepths[i];
                }

                if (exponentialDepths[i] != -1 && exponentialDepths[i] < baselineExponentsDepth) {
                    baselineExponentsDepth = exponentialDepths[i];
                }
            }

            return new ScopeDepthTracker(str.substring(start, end), parentheses.confine(start, end), parenthesesDepths, exponentsDepths, baselineParenthesesDepth, baselineExponentsDepth);
        }
    }
}

abstract class Operation {
    static final Operation EXPONENT = new Operation() { //exponent
        @Override
        public float operate(float a, float b) { return (float)Math.pow(a, b); }
    };
    
    static final Operation MULTIPLICATION = new Operation() { //multiplication
        @Override
        public float operate(float a, float b) { return a * b; }
    };
    
    static final Operation DIVISION = new Operation() { //division
        @Override
        public float operate(float a, float b) { return a / b; }
    };
    
    static final Operation ADDITION = new Operation() { //addition
        @Override
        public float operate(float a, float b) { return a + b; }
    };
    
    static final Operation SUBTRACTION = new Operation() { //subtraction
        @Override
        public float operate(float a, float b) { return a - b; }
    };
    
    public abstract float operate(float a, float b);
    
    public float operateAll(List<Float> operands) {
        float outcome = operands.get(0);
        for (int i = 1; i < operands.size(); ++i) {
            outcome = operate(outcome, operands.get(i));
        }
        
        return outcome;
    }
}