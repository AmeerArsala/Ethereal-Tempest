/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import general.math.FloatPair;
import java.util.LinkedHashMap;

/**
 *
 * @author night
 */
public class PiecewiseFunction extends MathFunction {
    //public static final FloatPair ZERO_TO_INFINITY = new FloatPair(0f, Float.POSITIVE_INFINITY);
    
    //defaultValue is the defaultValue put in place of the point that is not in the specified domain of the function
    protected final Float defaultValue; //null or 0
    protected final FloatPair domain; //inclusive
    protected final LinkedHashMap<FloatPair, MathFunction> functionParts = new LinkedHashMap<>();
    
    protected boolean allowRepetition = true; //this is in case it is outside the domain
    protected Float greatestIndividualDomainVal = null, leastIndividualDomainVal = null; //latest domain, earliest domain
    
    public PiecewiseFunction(FloatPair domain, float defaultVal) {
        this.domain = domain;
        defaultValue = defaultVal;
    }
    
    public PiecewiseFunction(FloatPair domain) {
        this.domain = domain;
        defaultValue = null;
    }
    
    public PiecewiseFunction(float defaultVal) {
        domain = null;
        defaultValue = defaultVal;
    }
    
    public PiecewiseFunction() {
        domain = null;
        defaultValue = null;
    }
    
    public PiecewiseFunction putExpression(FloatPair expressionDomain, MathFunction func) {
        functionParts.put(expressionDomain, func);
        
        if (greatestIndividualDomainVal == null || (greatestIndividualDomainVal != null && expressionDomain.b > greatestIndividualDomainVal)) {
            greatestIndividualDomainVal = expressionDomain.b;
        }
        
        if (leastIndividualDomainVal == null || (leastIndividualDomainVal != null && expressionDomain.a < leastIndividualDomainVal)) {
            leastIndividualDomainVal = expressionDomain.a;
        }
        
        return this;
    }
    
    //adds an expression to the right
    public PiecewiseFunction addExpression(MathFunction func, Float length) {
        FloatPair expressionDomain;
        if (length != Float.POSITIVE_INFINITY) {
            expressionDomain = new FloatPair(greatestIndividualDomainVal, greatestIndividualDomainVal + length);
        } else {
            expressionDomain = new FloatPair(greatestIndividualDomainVal, Float.POSITIVE_INFINITY);
        }
        
        return putExpression(expressionDomain, func);
    }
    
    public FloatPair getDomain() { return domain; }
    public Float getDefaultValue() { return defaultValue; }
    
    public boolean isRepetitionAllowed() { return allowRepetition; }
    
    public void setRepetitionAllowed(boolean allowed) {
        allowRepetition = allowed;
    }

    @Override
    protected float f(float x) {
        for (FloatPair partDomain : functionParts.keySet()) {
            if (x <= partDomain.b && x >= partDomain.a) {
                return functionParts.get(partDomain).output(x);
            }
        }
        
        return defaultValue;
    }
    
    @Override
    protected float modifyX(float x) {
        if (domain.b == Float.POSITIVE_INFINITY) { return x; }
        
        if (allowRepetition) {
            float next = x;
            
            while (x < domain.a) {
                next += (domain.b - domain.a);
            }
            
            while (x > domain.b) {
                next -= (domain.b - domain.a);
            }
            
            return next;
        }
        
        return x;
    }
}
