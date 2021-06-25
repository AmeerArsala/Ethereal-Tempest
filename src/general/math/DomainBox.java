/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import com.jme3.math.Vector2f;

/**
 *
 * @author night
 */
public class DomainBox {
    //use all these FloatPairs in terms of percents of the character's sprite quad (0.0 to 1.0), with the positive x-axis being where the character is facing
    private FloatPair domainX;
    private FloatPair domainY;
    
    public DomainBox(FloatPair domainX, FloatPair domainY) {
        this.domainX = domainX;
        this.domainY = domainY;
    }
    
    public FloatPair getDomainX() { return domainX; }
    public FloatPair getDomainY() { return domainY; }
    
    public FloatPair getDomainX(boolean mirrored) {
        return mirrored ? mirrorDomain(domainX) : domainX;
    }
    
    public FloatPair getDomainY(boolean mirrored) {
        return mirrored ? mirrorDomain(domainY) : domainY;
    }
    
    private FloatPair mirrorDomain(FloatPair domain) {
        return new FloatPair(1.0f - domain.b, 1.0f - domain.a);
    }
    
    public boolean pointIsWithinBox(Vector2f realPoint) {
        return (realPoint.x >= domainX.a && realPoint.x <= domainX.b) && (realPoint.y >= domainY.a && realPoint.y <= domainY.b);
    }
    
    public Vector2f getDimensions() {
        return new Vector2f(domainX.b - domainX.a, domainY.b - domainY.a);
    }
    
    public DomainBox mirrorNew(boolean mirrorX, boolean mirrorY) {
        return new DomainBox(getDomainX(mirrorX), getDomainY(mirrorY));
    }
    
    public DomainBox addLocal(float x, float y) {
        domainX.add(x);
        domainY.add(y);
        
        return this;
    }
    
    public DomainBox addLocal(float num) {
        domainX.add(num);
        domainY.add(num);
        
        return this;
    }
    
    public DomainBox addNew(float num) {
        return new DomainBox(domainX.addNew(num), domainY.addNew(num));
    }
    
    public DomainBox addNew(float x, float y) {
        return new DomainBox(domainX.addNew(x), domainY.addNew(y));
    }
    
    public DomainBox multLocal(float x, float y) {
        domainX.mult(x);
        domainY.mult(y);
        
        return this;
    }
    
    public DomainBox multLocal(float factor) {
        domainX.mult(factor);
        domainY.mult(factor);
        
        return this;
    }
    
    public DomainBox multNew(float factor) {
        return new DomainBox(domainX.multNew(factor), domainY.multNew(factor));
    }
    
    public DomainBox multNew(float x, float y) {
        return new DomainBox(domainX.multNew(x), domainY.multNew(y));
    }
    
    public DomainBox generateEquivalentInstance() {
        return new DomainBox(domainX.newEquivalentInstance(), domainY.newEquivalentInstance());
    }
    
    @Override
    public String toString() {
        return "x: [" + domainX + "], " + "y: [" + domainY + "]";  
    }
}
