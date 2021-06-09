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
public abstract class MathFunction extends AnyFunction<Float> {
    private float shift = 0, coefficient = 1;
    
    protected abstract float f(float x);
    
    @Override
    public Float output(float x) {
        return (coefficient * f(modifyX(x))) + shift;
    }
    
    //OVERRIDE IF NEEDED
    protected float modifyX(float x) {
        return x;
    }
    
    public MathFunction add(float num) {
        shift += num;
        return this;
    }
    
    public MathFunction multiply(float num) {
        coefficient *= num;
        return this;
    }
    
    public float getShift() { return shift; }
    public float getCoefficient() { return coefficient; }
    
    //Be careful with this
    public MathFunction newInstance() {
        return new MathFunction() {
            @Override
            protected float f(float x) {
                return MathFunction.this.f(x);
            }
        };
    }
    
    
    public static final MathFunction CONSTANT(float constant) {
        return new MathFunction() {
            @Override
            protected float f(float x) {
                return constant;
            }
        };
    }
    
}
