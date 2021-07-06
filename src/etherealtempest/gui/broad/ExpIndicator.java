/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etherealtempest.gui.broad;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import enginetools.MaterialCreator;
import general.ui.GeometryPanel;
import general.ui.text.Text2D;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;
import maps.data.MapTextures;

/**
 *
 * @author night
 */
public class ExpIndicator extends ValueIndicator {
    private final RadialProgressBar expbar;
    private final GeometryPanel levelUpTextPanel;
    
    public ExpIndicator(RadialProgressBar expbar, Text2D.FormatParams textFormatParams, AssetManager assetManager, int current, int max) {
        this(expbar, new Text2D("EXP: " + current + " / " + max, textFormatParams), assetManager, ((float)current) / max, max);
    } 
    
    public ExpIndicator(RadialProgressBar expbar, Text2D label, AssetManager assetManager, float basePercent, int max) {
        super("EXP", expbar.getChildrenNode(), label, basePercent, max);
        this.expbar = expbar;
        node.attachChild(text);
        
        float heightToWidthRatio = 25.5f / 100.5f;
        float width = expbar.getInnerRadius() * 2, height = width * heightToWidthRatio;
        levelUpTextPanel = new GeometryPanel(width, height, RenderQueue.Bucket.Gui);
        
        Material levelUpTextMat = new Material(assetManager, MaterialCreator.UNSHADED);
        levelUpTextMat.setTexture("ColorMap", MapTextures.GUI.Fighter.LevelUpLogo);
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
    
    /*
    @Override
    public void updateText() {
        text.setText("  EXP\n " + getCurrentNumber() + "/" + maxNumber);
    }
    */
    
    public void levelUp(float seconds, Runnable onFinish) { //seconds is typically 0.1f
        node.detachChild(text); //detach the "EXP: 75/100" text
        node.attachChild(levelUpTextPanel); //attach the "LEVEL UP!" text, which is an image
        
        VisualTransition VT = new VisualTransition(levelUpTextPanel, Animation.ZoomIn().setLength(seconds));
        VT.setResetProtocol(onFinish);
        
        addTransitionToGroup(VT);
    }
    
    public void levelUp(float seconds) {
        levelUp(seconds, () -> {});
    }
}
