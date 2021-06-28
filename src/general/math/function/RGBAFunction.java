/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math.function;

import com.jme3.math.ColorRGBA;
import java.awt.Color;

/**
 *
 * @author night
 */
public class RGBAFunction extends ParametricFunction4f {
    //private ParametricFunction4f rgbaFunction;
    
    public RGBAFunction(MathFunction r, MathFunction g, MathFunction b, MathFunction a) {
        super(r, g, b, a);
    }
    
    public RGBAFunction(MathFunction[] rgba) {
        super(rgba);
    }
    
    public RGBAFunction(ParametricFunction3f rgb, MathFunction a) {
        super(rgb, a);
    }
    
    public RGBAFunction(MathFunction r, ParametricFunction3f gba) {
        super(r, gba);
    }
    
    public RGBAFunction(ParametricFunction rg, MathFunction b, MathFunction a) {
        super(rg, b, a);
    }
    
    public RGBAFunction(MathFunction r, MathFunction g, ParametricFunction ba) {
        super(r, g, ba);
    }
    
    public RGBAFunction(MathFunction r, ParametricFunction gb, MathFunction a) {
        super(r, gb, a);
    }
    
    public RGBAFunction(ParametricFunction4f rgbaFunction) {
        super(rgbaFunction);
    }
    
    public RGBAFunction(ColorRGBA color) {
        super(ParametricFunction4f.CONSTANT(color.r, color.g, color.b, color.a));
    }
    
    public ColorRGBA rgba(float time) {
        return new ColorRGBA(r(time), g(time), b(time), a(time));
    }
    
    public ColorRGBA rgba(float time, float alphaMultiplier) {
        return new ColorRGBA(r(time), g(time), b(time), a(time) * alphaMultiplier);
    }
    
    public Color outputRGB(float input) {
        return new Color(x(input), y(input), b(input)); //x is the same as r, y is the same as g, just requires less calls
    }
}
