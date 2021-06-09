/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

/**
 *
 * @author night
 * @param <N> any number type
 */
public abstract class AnyFunction<N extends Number> {
    public abstract N output(float x);
    
    
    public static final AnyFunction<Float> RandomNumberFunction() {
        return new AnyFunction<Float>() {
            @Override
            public Float output(float x) { //x is the amplitude
                return (float)(x * Math.random());
            }
        };
    }
    
    public static final AnyFunction<Float> RandomNumber(float amp) {
        return new AnyFunction<Float>() {
            @Override
            public Float output(float x) { //x is the amplitude
                return (float)(amp * Math.random());
            }
        };
    }
}
