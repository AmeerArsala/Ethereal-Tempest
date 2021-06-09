/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import etherealtempest.geometry.Heart;
import etherealtempest.gui.ValueIndicator;
import general.ui.text.Text2D;

/**
 *
 * @author night
 */
public class HeartIndicator extends ValueIndicator {
    private final Heart heart;
        
    public HeartIndicator(Heart hpHeart, Text2D text, float basePercent, int max) {
        super(text, basePercent, max);
        heart = hpHeart;
        node.attachChild(heart);
    }
    
    public Heart getHeart() {
        return heart;
    }

    @Override
    protected void updatePercentVisually() {
        heart.setPercentFilled(percentFull);
    }
}
