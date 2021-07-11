/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import com.jme3.math.Vector2f;

/**
 *
 * @author night
 */
public class ParametricFunction { //ParametricFunction2f
    public static final short FRESH = 1; //newInstance() will generate a fresh new instance
    public static final short CLONED = 2; //newInstance() will clone this instance onto a new instance
    public static final short SAME = 3;  //newInstance() will return the same object
    
    private final MathFunction x, y;
    private short instanceGenType = CLONED; //cloned by default
    
    public ParametricFunction(MathFunction x, MathFunction y) {
        this.x = x;
        this.y = y;
    }
    
    public ParametricFunction(MathFunction[] xy) {
        x = xy[0];
        y = xy[1];
    }
    
    //copy constructor
    public ParametricFunction(ParametricFunction template) {
        x = template.x;
        y = template.y;
    }
    
    public Float x(float input) {
        return x.output(input);
    }
    
    public Float y(float input) {
        return y.output(input);
    }
    
    public MathFunction getXFunc() {
        return x;
    }
    
    public MathFunction getYFunc() {
        return y;
    }
    
    public Vector2f outputVec2(float input) {
        return new Vector2f(x.output(input), y.output(input));
    }
    
    public ParametricFunction setInstanceGenType(short type) {
        instanceGenType = type;
        return this;
    }
    
    public final ParametricFunction newInstance() {
        switch (instanceGenType) {
            case SAME:
                return this;
            case FRESH:
                return freshInstance();
            case CLONED:
            default:
                return cloneInstance();
        }
    }
    
    //override this if necessary in subclasses
    protected ParametricFunction freshInstance() {
        return new ParametricFunction(x.newInstance(), y.newInstance());
    }
    
    //override this if necessary in subclasses
    protected ParametricFunction cloneInstance() {
        return new ParametricFunction(x, y);
    }
    
    
    public static MathFunction[] splice(ParametricFunction toSplice) {
        if (toSplice instanceof ParametricFunction3f) {
            ParametricFunction3f toSplice3f = (ParametricFunction3f)toSplice;
            return new MathFunction[] { toSplice3f.getRFunc(), toSplice3f.getGFunc(), toSplice3f.getBFunc() };
        } else if (toSplice instanceof ParametricFunction4f) {
            ParametricFunction4f toSplice4f = (ParametricFunction4f)toSplice;
            return new MathFunction[] { toSplice4f.getRFunc(), toSplice4f.getGFunc(), toSplice4f.getBFunc(), toSplice4f.getAFunc() };
        } else {
            return new MathFunction[] { toSplice.x, toSplice.y };
        }
    }
    
    public static MathFunction[] adjoin(MathFunction a, MathFunction[] b) {
        MathFunction[] adjoined = new MathFunction[b.length + 1];
        adjoined[0] = a;
        for (int i = 1; i < adjoined.length; ++i) {
            adjoined[i] = b[i - 1];
        }
        
        return adjoined;
    }
    
    public static MathFunction[] adjoin(MathFunction[] a, MathFunction b) {
        MathFunction[] adjoined = new MathFunction[a.length + 1];
        for (int i = 0; i < a.length; ++i) {
            adjoined[i] = a[i];
        }
        
        adjoined[a.length] = b;
        
        return adjoined;
    }
    
    public static final ParametricFunction ZERO = new ParametricFunction(MathFunction.CONSTANT(0), MathFunction.CONSTANT(0));
    
}
