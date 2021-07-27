/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 *
 * @author night
 * 
 * this class is used for tagging
 */
public abstract class ProgressIndicator extends RangedValue {
    
    public ProgressIndicator(float basePercent, int max) {
        super(basePercent, max);
    }
    
    public abstract Material getMaterial();
    
    public abstract void setTexture(Texture tex);
    public abstract void setUsesAutoGradient(boolean uses);
    public abstract void setOnlyChangeColor(ColorRGBA color);
    public abstract void setBackgroundColor(ColorRGBA color);
    public abstract void setBaseColor(ColorRGBA color);
    public abstract void setColor(ColorRGBA color);
}
