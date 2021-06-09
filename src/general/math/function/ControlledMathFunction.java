/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import com.jme3.math.FastMath;
import general.ArrayManipulator;
import general.math.FloatPair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 * 
 * NOTE: only the first derivative works for special operations; integration has not been implemented yet
 */
public class ControlledMathFunction extends MathFunction {
    private final List<ControlledTerm> terms = new ArrayList<>();
    
    public ControlledMathFunction(List<ControlledTerm> funcTerms) {
        terms.addAll(funcTerms);
    }

    @Override
    protected float f(float x) {
        float sum = 0;
        for (ControlledTerm term : terms) {
            sum += term.compute(x);
        }
        
        return sum;
    }
    
    public List<ControlledTerm> getTerms() {
        return terms;
    }
    
    public ControlledMathFunction derivative() {
        List<ControlledTerm> derivedTerms = new ArrayList<>();
        
        for (ControlledTerm term : terms) {
            derivedTerms.add(term.derivative());
        }
        
        return new ControlledMathFunction(derivedTerms);
    }
    
    @Override
    public String toString() {
        String str = "", concactenator = " + ";
        
        for (ControlledTerm term : terms) {
            str += term.toString() + concactenator;
        }
        
        return str.substring(0, str.length() - concactenator.length());
    }
    
    public enum Operation {
        NOTHING((x) -> { return x; }),
        
        //Basic
        abs(
            (x) -> { return FastMath.abs(x); },
            (x) -> { return FastMath.sign(x); },
            (x) -> { return (x * FastMath.abs(x)) / 2f; }
        ),
        ceil((x) -> { return FastMath.ceil(x); }), //round up
        floor((x) -> { return FastMath.floor(x); }), //round down
        javaRoundUp((x) -> { return Math.round(x); }),
        signOf((x) -> { return FastMath.sign(x); }),
        saturate((x) -> { return FastMath.saturate(x); }), //clamps the given float between 0 and 1
        
        //Trig
        sin(
            (x) -> { return FastMath.sin(x); }, 
            (x) -> { return FastMath.cos(x); }, 
            (x) -> { return -FastMath.cos(x); }
        ),
        cos(
            (x) -> { return FastMath.cos(x); }, 
            (x) -> { return -FastMath.sin(x); }, 
            (x) -> { return FastMath.sin(x); }
        ),
        tan(
            (x) -> { return FastMath.tan(x); }, //regular
            (x) -> { return FastMath.pow((1f / FastMath.cos(x)), 2); }, //derivative
            (x) -> { return -ln(FastMath.abs(FastMath.cos(x))); } //integral
        ),
        
        csc(
            (x) -> { return 1f / FastMath.sin(x); },
            (x) -> { return -(1f / FastMath.sin(x)) * (1f / FastMath.tan(x)); },
            (x) -> { return -ln(FastMath.abs(csc(x) + cot(x))); }
        ),
        sec(
            (x) -> { return 1f / FastMath.cos(x); },
            (x) -> { return sec(x) * FastMath.tan(x); },
            (x) -> { return ln(FastMath.abs(sec(x) + FastMath.tan(x))); }
        ),
        cot(
            (x) -> { return 1f / FastMath.tan(x); },
            (x) -> { return -FastMath.pow(csc(x), 2); },
            (x) -> { return ln(FastMath.abs(FastMath.sin(x))); }
        ),
        arcsin(
            (x) -> { return FastMath.asin(x); },
            (x) -> { return 1f / FastMath.sqrt(1f - FastMath.sqr(x)); },
            (x) -> { return x * FastMath.asin(x) + FastMath.sqrt(1f - FastMath.sqr(x)); }
        ),
        arccos(
            (x) -> { return FastMath.acos(x); },
            (x) -> { return -1f / FastMath.sqrt(1f - FastMath.sqr(x)); },
            (x) -> { return -x * FastMath.asin(x) + FastMath.sqrt(1f - FastMath.sqr(x)); }
        ),
        arctan(
            (x) -> { return FastMath.atan(x); },
            (x) -> { return 1f / (1f + FastMath.sqr(x)); },
            (x) -> { return x * FastMath.atan(x) - (0.5f * ln(1 + FastMath.sqr(x))); }
        ),
        arccsc(
            (x) -> { return 1f / FastMath.asin(1f / x); },
            (x) -> { return -1f / (x * FastMath.sqrt(FastMath.sqr(x) - 1f)); },
            (x) -> { return x * arccsc(x) + ln(FastMath.abs(x + FastMath.sqrt(FastMath.sqr(x) - 1))); }
        ),
        arcsec(
            (x) -> { return 1f / FastMath.acos(1f / x); },
            (x) -> { return 1f / (x * FastMath.sqrt(FastMath.sqr(x) - 1f)); },
            (x) -> { return x * arcsec(x) - ln(FastMath.abs(x + FastMath.sqrt(FastMath.sqr(x) - 1))); }
        ),
        arccot(
            (x) -> { return 1f / FastMath.atan(1f / x); },
            (x) -> { return -1f / (1f + FastMath.sqr(x)); },
            (x) -> { return x * arccot(x) + (0.5f * ln(FastMath.sqr(x) + 1f)); }
        ),
        
        //Hyperbolic Trig
        sinh(
            (x) -> { return (float)Math.sinh(x); },
            (x) -> { return (float)Math.cosh(x); },
            (x) -> { return (float)Math.cosh(x); }
        ),
        cosh(
            (x) -> { return (float)Math.cosh(x); },
            (x) -> { return (float)Math.sinh(x); },
            (x) -> { return (float)Math.sinh(x); }
        ),
        tanh(
            (x) -> { return (float)Math.tanh(x); },
            (x) -> { return (float)(1 - Math.pow(Math.tanh(x), 2)); },
            (x) -> { return ln((float)Math.abs(Math.cosh(x))); }
        ),
        csch(
            (x) -> { return (float)(1f / Math.sinh(x)); },
            (x) -> { return -coth(x) * csch(x); },
            (x) -> { return ln((float)Math.tanh(x / 2f)); }
        ),
        sech(
            (x) -> { return (float)(1f / Math.cosh(x)); },
            (x) -> { return (float)(-Math.tanh(x) * sech(x)); },
            (x) -> { return FastMath.atan((float)Math.sinh(x)); }
        ),
        coth(
            (x) -> { return (float)(1f / Math.tanh(x)); },
            (x) -> { return 1f - FastMath.sqr(coth(x)); },
            (x) -> { return ln((float)Math.abs(Math.sinh(x))); }
        ),
        
        //Logarithmic
        log( //logBase10
            (x) -> { return FastMath.log(x); },
            (x) -> { return 1f / (x * ln(10f)); },
            (x) -> { return (x * FastMath.log(x)) - (x / ln(10f)); }
        ), 
        ln( //natural log
            (x) -> { return FastMath.log(x, (float)Math.E); },
            (x) -> { return 1 / FastMath.abs(x); },
            (x) -> { return (x * ln(x)) - x; } 
        ),
        
        //Random
        random((x) -> { return x * FastMath.nextRandomFloat(); }),  //will return a random number such that it is [0, x)
        randomInt((x) -> { return x * FastMath.nextRandomInt(); }), //will return a random integer such that it is [0, x)
        javaRandom((x) -> { return (float)(x * Math.random()); }),  // Java Math.random() method, will return a random number such that it is [0, x)
        
        //Misc
        nearestPowerOf2((x) -> { return FastMath.nearestPowerOfTwo((int)x); }),
        toDegrees((x) -> { return (float)Math.toDegrees(x); }),
        toRadians((x) -> { return (float)Math.toRadians(x); }),
        toPowerOfItself((x) -> { return FastMath.pow(x, x); }),
        ulp((x) -> { return Math.ulp(x); });
        
        public static float ln(float x) { return FastMath.log(x, (float)Math.E); }
        public static float csc(float x) { return 1f / FastMath.sin(x); }
        public static float sec(float x) { return 1f / FastMath.cos(x); }
        public static float cot(float x) { return 1f / FastMath.tan(x); }
        public static float arccsc(float x) { return 1f / FastMath.asin(1f / x); }
        public static float arcsec(float x) { return 1f / FastMath.acos(1f / x); }
        public static float arccot(float x) { return 1f / FastMath.atan(1f / x); }
        public static float csch(float x) { return (float)(1 / Math.sinh(x)); }
        public static float sech(float x) { return (float)(1 / Math.cosh(x)); }
        public static float coth(float x) { return (float)(1 / Math.tanh(x)); }
        
        private final FloatOperation mathfunc, derivative, integral;
        private Operation(FloatOperation func, FloatOperation deriv, FloatOperation integ) {
            mathfunc = func;
            derivative = deriv;
            integral = integ;
        }
        
        private Operation(FloatOperation func) {
            mathfunc = func;
            derivative = func;
            integral = func;
        }
        
        public float calculate(float x) {
            return mathfunc.calculate(x);
        }
        
        public float calculateDerivative(float x) {
            return derivative.calculate(x);
        }
        
        public float calculateIntegral(float x) {
            return integral.calculate(x);
        }
        
        private interface FloatOperation {
            public float calculate(float x);
        }
    }

    public static class ControlledTerm {
        //Operations on a term go: ((coefficient * x^(powerOfX) * multipliedOperations....run(x)) +...otherTermsInTerm)^exponent
        
        private float coefficient = 0; // number/coefficient of x
        private float powerOfX = 0;    // if 0, then just a whole number
        
        private MultipliedOperation[] multipliedOperations;
        
        private ControlledTerm[] otherTermsInTerm = new ControlledTerm[0]; //if there are other terms, this term is treated as parentheses
        private ControlledTerm exponent = new ControlledTerm(1f, 0f); // exponent of the entire term
        
        public ControlledTerm(float coefficient) {
            this.coefficient = coefficient;
            powerOfX = 0f;
            multipliedOperations = new MultipliedOperation[0];
        }
        
        public ControlledTerm(float coefficient, float powerOfX, MultipliedOperation... multipliedOperations) {
            this.coefficient = coefficient;
            this.powerOfX = powerOfX;
            this.multipliedOperations = multipliedOperations;
        }
        
        public float getCoefficient() { return coefficient; }
        public float getPowerOfX() { return powerOfX; }
        
        public MultipliedOperation[] getMultipliedOperations() { return multipliedOperations; }
        
        public ControlledTerm[] getOtherTermsWithinThisTerm() { return otherTermsInTerm; }
        public ControlledTerm getExponent() { return exponent; }
        
        public float compute(float x) {
            float result = coefficient * FastMath.pow(x, powerOfX) * computeOperations(x);
            
            for (ControlledTerm otherTerm : otherTermsInTerm) {
                result += otherTerm.compute(x);
            }
            
            return FastMath.pow(result, exponent.compute(x));
        }
        
        public float computeOperations(float x) {
            float result = 1f;
            for (MultipliedOperation multOp : multipliedOperations) {
                result *= multOp.run(x);
            }
            
            return result;
        }
        
        public boolean isJustANumber() {
            if (coefficient == 0) {
                return true;
            }
            
            if (!exponent.isJustANumber()) {
                return false;
            }
            
            if (powerOfX != 0) {
                return false;
            }
            
            for (MultipliedOperation multOp : multipliedOperations) {
                if (!multOp.isJustANumber()) {
                    return false;
                }
            }
            
            for (ControlledTerm added : otherTermsInTerm) {
                if (!added.isJustANumber()) {
                    return false;
                }
            }
            
            return true;
        }
        
        public ControlledTerm setTermsAddedInThisTerm(ControlledTerm[] others) {
            otherTermsInTerm = others;
            return this;
        }
        
        public ControlledTerm toThePowerOf(ControlledTerm power) {
            exponent = power;
            return this;
        }
        
        public ControlledTerm toThePowerOf(float power) {
            exponent = new ControlledTerm(power, 0f);
            return this;
        }
        
        public ControlledTerm multiplyExponentBy(float factor) {
            exponent.coefficient *= factor;
            return this;
        }
        
        public ControlledTerm reciprocate() {
            exponent.coefficient *= -1;
            return this;
        }
        
        public ControlledTerm multiplyEntireTermBy(MultipliedOperation multOp) {
            MultipliedOperation[] originalTerm = { new MultipliedOperation(generateEquivalent(), true), multOp };
            
            coefficient = 1f;
            powerOfX = 0f;
            multipliedOperations = originalTerm;
            
            return this;
        }
        
        public ControlledTerm multiplyEntireTermBy(float coeff, float pwrOfX) {
            MultipliedOperation[] originalTerm = { new MultipliedOperation(generateEquivalent(), true) };
            
            coefficient = coeff;
            powerOfX = pwrOfX;
            multipliedOperations = originalTerm;
            
            return this;
        }
        
        public ControlledTerm generateEquivalent() {
            return new ControlledTerm(coefficient, powerOfX, multipliedOperations).setTermsAddedInThisTerm(otherTermsInTerm).toThePowerOf(exponent);
        }
        
        private boolean operationsAreJustNumbers() { //true if they are just numbers or if there are no operations
            for (MultipliedOperation multOp : multipliedOperations) {
                if (!multOp.isJustANumber()) {
                    return false;
                }
            }
            
            return true;
        }
        
        public ControlledTerm derivative() {
            ControlledTerm innerDerivative = innerDerivative();
            
            //everything without exponent
            ControlledTerm inner = new ControlledTerm(coefficient, powerOfX, multipliedOperations).setTermsAddedInThisTerm(otherTermsInTerm);
            
            if (exponent.isJustANumber()) {
                float pow = exponent.compute(0);
                
                if (pow == 0) {
                    return new ControlledTerm(0);
                }
                
                if (pow == 1) {
                    return innerDerivative != null ? innerDerivative : new ControlledTerm(0f);
                }
                
                //power rule
                if (innerDerivative == null) {
                    return new ControlledTerm(pow, 0f, new MultipliedOperation(inner.toThePowerOf(pow - 1), true));
                }
                
                return new ControlledTerm(pow, 0f, new MultipliedOperation(inner.toThePowerOf(pow - 1), true), new MultipliedOperation(innerDerivative, true));
            }
            
            //exponential function rule: [a(x)^b(x)]' = (a(x)^b(x)) * [ln(a(x)) * b(x)]' 
            return new ControlledTerm(
                1f,
                0f,
                new MultipliedOperation(
                    this,
                    true
                ),
                new MultipliedOperation(
                    new ControlledTerm(
                        1f,
                        0f,
                        new MultipliedOperation(
                            exponent.derivative(),
                            true
                        ),
                        new MultipliedOperation(
                            Operation.ln,
                            inner,
                            true
                        )
                    ).setTermsAddedInThisTerm(new ControlledTerm[] {
                        new ControlledTerm(
                            1f,
                            0f,
                            new MultipliedOperation(
                                innerDerivative,
                                true
                            ),
                            new MultipliedOperation(
                                exponent,
                                true
                            ),
                            new MultipliedOperation(
                                Operation.abs,
                                inner,
                                false
                            )
                        )
                    }),
                    true
                )
            );
        }
        
        private ControlledTerm innerDerivative() {
            ControlledTerm innerDerivative;
            
            boolean operationsAreJustNumbers = operationsAreJustNumbers();
            if (coefficient == 0f || (operationsAreJustNumbers && powerOfX == 0)) {
                innerDerivative = new ControlledTerm(0f);
            } else if (operationsAreJustNumbers && powerOfX != 0) {
                float operationsResult = 1;
                for (MultipliedOperation operation : multipliedOperations) {
                    operationsResult *= operation.run(0);
                }
                
                innerDerivative = new ControlledTerm(
                    powerOfX * operationsResult, 
                    powerOfX - 1
                );
            } else { // operationsAreJustNumbers == false
                combineMultipliersThatAreJustNumbers();
                if (powerOfX == 0) {
                    innerDerivative = multiplicationRule(multipliedOperations);
                } else {
                    ArrayManipulator<MultipliedOperation> manipulator = MultipliedOperation.createManipulator(multipliedOperations);
                    innerDerivative = deriveX();
                    innerDerivative.multipliedOperations = multipliedOperations;
                    innerDerivative.otherTermsInTerm = new ControlledTerm[] { 
                        multiplicationRule(multipliedOperations).multiplyEntireTermBy(coefficient, powerOfX)
                    };
                }
            }
            
            List<ControlledTerm> terms = new ArrayList<>();
            for (ControlledTerm added : otherTermsInTerm) {
                ControlledTerm addedDerivative = added.derivative();
                if (!addedDerivative.isJustANumber()) {
                    terms.add(addedDerivative);
                }
            }
            
            ControlledTerm[] addedDterms = new ControlledTerm[terms.size()];
            innerDerivative.otherTermsInTerm = terms.toArray(addedDterms);
            
            if (innerDerivative.isJustANumber()) {
                return null;
            }
            
            return innerDerivative;
        }
        
        public ControlledTerm definiteIntegral(FloatPair domain) {
            return definiteIntegral(domain.a, domain.b);
        }
        
        public ControlledTerm definiteIntegral(float a, float b) {
            return null;
        }
        
        public void combineMultipliersThatAreJustNumbers() {
            List<MultipliedOperation> multOps = new ArrayList<>();
            MultipliedOperation focus = multipliedOperations[0];
            
            for (MultipliedOperation operation : multipliedOperations) {
                if (operation.isJustANumber()) {
                    focus.multiplyBy(operation);
                } else {
                    multOps.add(operation);
                }
            }
            
            multOps.add(focus);
            
            multipliedOperations = multOps.toArray(new MultipliedOperation[multOps.size()]);
        }
        
        private ControlledTerm deriveX() {
            if (powerOfX == 0) {
                return new ControlledTerm(0f, 0f);
            }
            
            //power rule
            return new ControlledTerm(coefficient * powerOfX, powerOfX - 1);
        }
        
        /**
         * 
         * @param operations multipliedOperations
         * @return 
         */
        private static ControlledTerm multiplicationRule(MultipliedOperation[] operations) {
            if (operations.length == 1) { //there must be 2 terms for multiplication rule to work
                return new ControlledTerm(1f, 0f, operations);
            }
            
            ArrayManipulator<MultipliedOperation> manipulator = new ArrayManipulator<>(operations, (size) -> {
                return new MultipliedOperation[size];
            });
            
            MultipliedOperation a = operations[0];
                
            MultipliedOperation[] aPrime_b = manipulator.subArray(0); //clone array with space for a and b
            aPrime_b[0] = a.derivative(); //differentiate the a part
                
            MultipliedOperation[] bPrime = manipulator.subArray(1); //clone array with space for b only
                    
            return new ControlledTerm(
                1f,
                0f,
                aPrime_b
            ).setTermsAddedInThisTerm(
                new ControlledTerm[] {
                    multiplicationRule(bPrime).multiplyEntireTermBy(a)
                }
            );
        }
        
        @Override
        public String toString() {
            if (coefficient == 0) {
                return "0";
            }
            
            if (exponent.coefficient == 0) {
                return "1";
            }
            
            char x = 'x';
            String str = "(";
            
            if (coefficient == -1f) {
                str += "-";
            } else if (coefficient != 1f) {
                str += coefficient;
            }
            
            for (MultipliedOperation multOp : multipliedOperations) {
                str += multOp.toString();
            } 
            
            if (powerOfX == 1f) {
                str += x;
            } else if (powerOfX != 0f) {
                str += (x + "^(" + powerOfX + ")");
            }
            
            for (ControlledTerm term : otherTermsInTerm) {
                str += " + " + term.toString();
            }
            
            str += ")";
            
            if (exponent.exponent.coefficient == 0 || (exponent.coefficient == 1 && exponent.powerOfX == 0 && exponent.multipliedOperations.length == 0 && exponent.otherTermsInTerm.length == 0)) {
                return str;
            }
            
            return str + "^" + exponent.toString();
        }
    }
    
    public static class MultipliedOperation {
        public static final short DIFF_LEVEL_NORMAL = 0;
        public static final short DIFF_LEVEL_DERIVATIVE = 1;
        public static final short DIFF_LEVEL_INTEGRAL = -1;
        
        private short differentiationLevel = DIFF_LEVEL_NORMAL;
        
        private final Operation operation;
        private final ControlledTerm onTerm;
        //private final boolean isMultiply; //if true, will multiply, if false, will divide
            
        public MultipliedOperation(Operation operationType, ControlledTerm paraTerm, boolean isMultiply) {
            operation = operationType;
            onTerm = paraTerm;
            if (isMultiply) { onTerm.reciprocate(); }
        }
        
        public MultipliedOperation(ControlledTerm paraTerm, boolean isMultiply) {
            operation = Operation.NOTHING;
            onTerm = paraTerm;
            if (isMultiply) { onTerm.reciprocate(); }
        }
            
        public Operation getOperation() { return operation; }
        public ControlledTerm getTermGroup() { return onTerm; }
        
        public boolean isJustANumber() {
            return onTerm.isJustANumber();
        }
            
        public float run(float x) {
            switch(differentiationLevel) {
                case DIFF_LEVEL_NORMAL:
                    return operation.calculate(onTerm.compute(x));
                case DIFF_LEVEL_DERIVATIVE:
                    return operation.calculateDerivative(x);
                case DIFF_LEVEL_INTEGRAL:
                    return operation.calculateIntegral(x);
            }
            
            return Float.NaN;
        }
        
        public MultipliedOperation derivative() {
            MultipliedOperation derivative = new MultipliedOperation(operation, onTerm.derivative(), true);
            
            derivative.differentiationLevel = DIFF_LEVEL_DERIVATIVE;
            
            return derivative;
        }
        
        public MultipliedOperation definiteIntegral(float a, float b) {
            MultipliedOperation integral = new MultipliedOperation(operation, onTerm.definiteIntegral(a, b), true);
            
            integral.differentiationLevel = DIFF_LEVEL_INTEGRAL;
            
            return integral;
        }
        
        public void multiplyBy(MultipliedOperation other) {
            onTerm.multiplyEntireTermBy(other);
        }
        
        public static ArrayManipulator<MultipliedOperation> createManipulator(MultipliedOperation[] operations) {
            return new ArrayManipulator<>(operations, (size) -> {
                return new MultipliedOperation[size];
            });
        }
        
        @Override
        public String toString() {
            if (operation == Operation.NOTHING) {
                return onTerm.toString();
            }
            
            return operation.toString() + "(" + onTerm.toString() + ")";
        }
    }
}