/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import etherealtempest.gui.RadialProgressBar;
import etherealtempest.gui.ValueIndicator;
import general.ui.GeometryPanel;
import general.ui.text.Text2D;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;

/**
 *
 * @author night
 */
public class ExpIndicator extends ValueIndicator {
    private final RadialProgressBar expbar;
    private final GeometryPanel levelUpTextPanel;
    
    public ExpIndicator(RadialProgressBar expbar, Text2D label, AssetManager assetManager, float basePercent, int max) {
        super(expbar.getChildrenNode(), label, basePercent, max);
        this.expbar = expbar;
        
        float heightToWidthRatio = 25.5f / 100.5f;
        float width = expbar.getInnerRadius() * 2, height = width * heightToWidthRatio;
        levelUpTextPanel = new GeometryPanel(width, height);
        
        Material levelUpTextMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        levelUpTextMat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/common/levelup.png"));
        levelUpTextMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        levelUpTextPanel.setMaterial(levelUpTextMat);
    }
    
    public RadialProgressBar getExpCircle() {
        return expbar;
    }
    
    public GeometryPanel getLevelUpTextPanel() {
        return levelUpTextPanel;
    }

    @Override
    protected void updatePercentVisually() {
        expbar.setCirclePercent(percentFull);
        //TODO: play a sound
    }
    
    @Override
    public void updateText() {
        text.setText("  EXP\n " + getCurrentNumber() + "/" + maxNumber);
    }
    
    public void levelUp(float seconds, Runnable onFinish) { //seconds is typically 0.1f
        node.detachChild(text); //detach the "EXP: 75/100" text
        node.attachChild(levelUpTextPanel); //attach the "LEVEL UP!" text, which is an image
        
        VisualTransition VT = new VisualTransition(levelUpTextPanel, Animation.ZoomIn().setLength(seconds));
        VT.setResetProtocol(onFinish);
        
        addTransitionToQueue(VT);
    }
    
    public void levelUp(float seconds) {
        levelUp(seconds, () -> {});
    }
}