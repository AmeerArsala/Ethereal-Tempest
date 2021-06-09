/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import general.math.FloatPair;

/**
 *
 * @author night
 */
public class RandomizedPiecewiseFunction extends PiecewiseFunction { //this will be linear only
    private final FloatPair range; //both a and b must be defined
    
    //if true, the range will be semi-randomized according to a and b of range, which will be the min and max values 
    //in this case, the a and b values of the 'range' field as min and max values would not be inclusive for the randomized range; they would be exclusive
    //in other words if range = FloatPair(-1f, 1f), the randomized range would be (-1f, 1f), not [-1f, 1f]
    private final boolean randomizeRange;
    private FloatPair previousRandomRange = null; //this is for randomizing range
    
    //domain length for each piece is what this output represents
    private final MathFunction partitionLength; //cannot output 'null'
    
    private float previousEndY; //for randomization
    
    public RandomizedPiecewiseFunction(FloatPair domain, FloatPair range, MathFunction partitionLength, float defaultVal, boolean randomizeRange) {
        super(domain, defaultVal);
        this.range = range;
        this.partitionLength = partitionLength;
        this.randomizeRange = randomizeRange;
    }
    
    public RandomizedPiecewiseFunction(FloatPair domain, FloatPair range, MathFunction partitionLength, boolean randomizeRange) {
        super(domain);
        this.range = range;
        this.partitionLength = partitionLength;
        this.randomizeRange = randomizeRange;
    }
    
    public boolean rangeIsRandomized() { return randomizeRange; }
    public FloatPair getRange() { return range; }
    public MathFunction getPartitionLength() { return partitionLength; }
    
    private MathFunction nextLinear(float x1, float y1, float x2, float y2) {
        float m = (y2 - y1) / (x2 - x1); //slope
        
        return new CartesianFunction() {
            @Override
            protected float f(float x) { 
                return (m * (x - x1)) + y1; //point slope form
            }
        };
    }
    
    private MathFunction nextFunction(FloatPair exprDomain, FloatPair exprRange) { //x cannot be null
        float diffY = exprRange.b - exprRange.a;
        
        float x1 = exprDomain.a;
        float y1;
        if (functionParts.isEmpty()) {
            y1 = ((float)(diffY * Math.random())) + exprRange.a;
        } else {
            y1 = previousEndY;
        }
        
        float x2 = exprDomain.b;
        float y2 = ((float)(diffY * Math.random())) + exprRange.a;
        previousEndY = y2;
        
        return nextLinear(x1, y1, x2, y2);
    }
    
    private FloatPair obtainRange() {
        if (!randomizeRange) {
            return range;
        }
        
        float a = (float)(range.a * Math.random());
        float b = (float)(range.b * Math.random());
        
        //if this is not the first time nextFunction will be called in this instance
        if (previousRandomRange != null) {
            //splitting the randomization into phases of a and b theoretically reduces the amount of time it will take for both conditions to be true
            
            //calculate a
            while(a >= previousEndY) { //we want to end up with: a < previousEndY
                a = (float)(range.a * Math.random());
            }
            
            //calculate b
            while (b <= previousEndY) { //we want to end up with: b > previousEndY 
                b = (float)(range.b * Math.random());
            }
        }
        
        previousRandomRange = new FloatPair(a, b);
        return previousRandomRange;
    }
    
    private RandomizedPiecewiseFunction putRandom(FloatPair expressionDomain) {
        functionParts.put(expressionDomain, nextFunction(expressionDomain, obtainRange()));
        
        if (greatestIndividualDomainVal == null || (greatestIndividualDomainVal != null && expressionDomain.b > greatestIndividualDomainVal)) {
            greatestIndividualDomainVal = expressionDomain.b;
        }
        
        if (leastIndividualDomainVal == null || (leastIndividualDomainVal != null && expressionDomain.a < leastIndividualDomainVal)) {
            leastIndividualDomainVal = expressionDomain.a;
        }
        
        return this;
    }
    
    public RandomizedPiecewiseFunction addNextRandom(float x) { //at x, just in case the answer changes due to x
        FloatPair expressionDomain = new FloatPair(greatestIndividualDomainVal, greatestIndividualDomainVal + partitionLength.output(x));
        return putRandom(expressionDomain);
    }
    
    @Override
    protected float f(float x) {
        for (FloatPair partDomain : functionParts.keySet()) {
            if (x <= partDomain.b && x >= partDomain.a) {
                return functionParts.get(partDomain).output(x);
            }
        }
        
        if (domain.b != null && x > domain.b) {
            return defaultValue;
        }
        
        putRandom(new FloatPair(x, x + partitionLength.output(x)));
        return output(x);
    }
    
    @Override
    public MathFunction newInstance() {
        if (defaultValue == null) {
            return new RandomizedPiecewiseFunction(domain, range, partitionLength, randomizeRange);
        }
        
        return new RandomizedPiecewiseFunction(domain, range, partitionLength, defaultValue, randomizeRange);
    }
    
    /**
     *
     * @param rangeOfRange
     * @param partitionLengthMultiplier THIS MUST BE 1 OR ABOVE, CANNOT BE LESS THAN THAT
     * @return
     */
    public static RandomizedPiecewiseFunction superRandom(FloatPair rangeOfRange, float partitionLengthMultiplier) {
        return new RandomizedPiecewiseFunction(ZERO_TO_INFINITY, rangeOfRange, MathFunction.CONSTANT(Float.MIN_VALUE * 2 * partitionLengthMultiplier), true);
    }
    
}