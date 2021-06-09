/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import com.jme3.math.ColorRGBA;

/**
 *
 * @author night
 */
public class RGBAFunction {
    private ParametricFunction4f rgbaFunction;
    
    public RGBAFunction(ParametricFunction4f rgbaFunction) {
        this.rgbaFunction = rgbaFunction;
    }
    
    public RGBAFunction(ColorRGBA color) {
        rgbaFunction = ParametricFunction4f.CONSTANT(color.r, color.g, color.b, color.a);
    }
    
    public ParametricFunction4f getParametricFunction() { return rgbaFunction; }
    
    public void setParametricFunction(ParametricFunction4f rgbaFunc) {
        rgbaFunction = rgbaFunc;
    }
    
    public void setColor(ColorRGBA color) {
        rgbaFunction = ParametricFunction4f.CONSTANT(color.r, color.g, color.b, color.a);
    }
    
    public ColorRGBA rgba(float time) {
        return new ColorRGBA(rgbaFunction.r(time), rgbaFunction.g(time), rgbaFunction.b(time), rgbaFunction.a(time));
    }
    
    public ColorRGBA rgba(float time, float alphaMultiplier) {
        return new ColorRGBA(rgbaFunction.r(time), rgbaFunction.g(time), rgbaFunction.b(time), rgbaFunction.a(time) * alphaMultiplier);
    }
}
