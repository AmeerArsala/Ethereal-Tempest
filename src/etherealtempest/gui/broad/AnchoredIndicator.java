/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import enginetools.math.SpatialOperator;
import general.ui.text.Text2D;

/**
 *
 * @author night
 */
public abstract class AnchoredIndicator extends ValueIndicator {
    protected final Vector2f equilibrium = new Vector2f(0, 0);
    
    public AnchoredIndicator(String name, Text2D text2D, float basePercent, int max) {
        super(name, text2D, basePercent, max);
    }
    
    public AnchoredIndicator(String name, Node primeNode, Text2D text2D, float basePercent, int max) {
        super(name, primeNode, text2D, basePercent, max);
    }
    
    @Override
    protected void updateText() {
        super.updateText();
        alignTextToEquilibrium();
    }
    
    public abstract SpatialOperator getAnchor();
    public abstract void alignTextToEquilibrium();
    
    public final void alignTextTo(float percentX, float percentY) {
        equilibrium.set(percentX, percentY);
        alignTextToEquilibrium();
    }
}
