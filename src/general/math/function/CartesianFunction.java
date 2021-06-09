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
public abstract class CartesianFunction extends MathFunction { //not that accurate but accurate enough for game
    public static final int DEFAULT_ITERATIONS = 30;
    public static final int DEFAULT_INFINITE = 100;
    
    public CartesianFunction() {}
    
    @Override
    public Float output(float x) {
        try {
            return super.output(x);
        }
        catch(Exception e) {
            return null;
        }
    }
    
    @Override
    public CartesianFunction add(float amt) {
        super.add(amt);
        return this;
    }
    
    @Override
    public CartesianFunction multiply(float amt) {
        super.multiply(amt);
        return this;
    }
    
    public CartesianFunction derivative() {
        return derivative(DEFAULT_ITERATIONS);
    }
    
    public CartesianFunction definiteIntegral(float a, float b) {
        return definiteIntegral(a, b, DEFAULT_INFINITE);
    }
    
    public CartesianFunction derivative(int iterations) {
        CartesianFunction original = this;
        return new CartesianFunction() {
            @Override
            protected float f(float x) {
                return 
                        limitAs(
                            0,
                            new CartesianFunction() {
                                @Override
                                protected float f(float h) {
                                    return (original.output(x + h) - original.output(x)) / h;
                                }
                            },
                            iterations
                        );
            }
        };
    }
    
    public CartesianFunction definiteIntegral(float a, float b, int iterations) {
        CartesianFunction original = this;
        return new CartesianFunction() {
            @Override
            protected float f(float x) {
                return 
                        oneSidedLimitAs(
                            iterations, //as n goes to infinity
                            true, //positive infinity
                            new CartesianFunction() {
                                @Override
                                protected float f(float n) {
                                    float delta = (b - a) / n;
                                    
                                    float sum = 0;
                                    for (int k = 1; k <= iterations; k++) {
                                        sum += (original.output(a + (k * delta)) * delta);
                                    }
                                    
                                    return sum;
                                }
                            },
                            1
                        );
            }
        };
    } 
    
    public static Float limitAs(float targetValue, CartesianFunction func, int iterations) {
        Float result = null;
        for (int i = 0; i < iterations; i++) {
            float delta = (float)Math.pow(10, -i);
            
            Float leftSideOutput = func.output(targetValue - delta);
            Float rightSideOutput = func.output(targetValue + delta);
            
            if (leftSideOutput == null || rightSideOutput == null) {
                return null;
            }
            
            result = (leftSideOutput + rightSideOutput) / 2.0f;
        }
        
        return result;
    }
    
    //if fromSide == true, it is coming from the positive side. if it is false, it is coming from the negative side
    public static Float oneSidedLimitAs(float targetValue, boolean fromSide, CartesianFunction func, int iterations) { //this isn't as accurate
        int sign = fromSide ? 1 : -1;
        Float result = null;
        
        for (int i = 0; i < iterations; i++) {
            float delta = (float)Math.pow(10, -i);

            Float sideOutput = func.output(targetValue + (delta * sign));
            
            if (sideOutput == null) {
                return null;
            }
            
            result = sideOutput;
        }
        
        return result;
    }
    
    public static CartesianFunction pointSlopeLine(float y1, float y2, float x1, float x2) {
        float m = (y2 - y1) / (x2 - x1);
        
        return new CartesianFunction() {
            @Override
            protected float f(float x) {
                return m * (x - x1) + y1;
            }
        };
    }
    
}
