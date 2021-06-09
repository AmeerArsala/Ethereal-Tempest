/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui;

import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.QuadBackgroundComponent;

/**
 *
 * @author night
 */
public class Icon extends Node {
    private final Panel iconDisplay;
    private final Padding padding;
    
    public Icon(float width, float height, Texture texture, Padding padding) {
        this.padding = padding;
        
        iconDisplay = new Panel(width, height);
        ((QuadBackgroundComponent)iconDisplay.getBackground()).setTexture(texture);
        
        attachChild(iconDisplay);
        applyPadding();
    }
    
    private void applyPadding() {
        iconDisplay.move(padding.right - padding.left, padding.top - padding.bottom, 0f);
    }
    
    public void setTexture(Texture tex) {
        ((QuadBackgroundComponent)iconDisplay.getBackground()).setTexture(tex);
    }
}
