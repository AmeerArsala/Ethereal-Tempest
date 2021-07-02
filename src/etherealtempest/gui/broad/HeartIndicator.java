/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import etherealtempest.geometry.Heart;
import general.ui.text.Text2D;

/**
 *
 * @author night
 */
public class HeartIndicator extends ValueIndicator {
    private final Heart heart;
        
    public HeartIndicator(String name, Heart hpHeart, Text2D text, float basePercent, int max) {
        super(name, text, basePercent, max);
        heart = hpHeart;
        node.attachChild(heart);
        node.attachChild(text);
    }
    
    public Heart getHeart() {
        return heart;
    }

    @Override
    protected void updatePercentVisually() {
        heart.setPercentFilled(percentFull);
    }
}
