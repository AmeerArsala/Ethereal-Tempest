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
import com.simsilica.lemur.LayerComparator;
import enginetools.MaterialCreator;
import enginetools.math.SpatialOperator;
import enginetools.math.Vector2F;
import enginetools.math.Vector3F;
import general.ui.GeometryPanel;
import general.ui.text.Text2D;
import general.visual.animation.Animation;
import general.visual.animation.VisualTransition;
import maps.data.MapTextures;

/**
 *
 * @author night
 */
public class ExpIndicator extends AnchoredIndicator {
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
        
        LayerComparator.setLayer(text, 5);
        textAnchor.getOriginPointInPercents().set(SpatialOperator.ORIGIN_TOP_LEFT);
        equilibrium.set(0, 0);
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
    protected void updateText() {
        String ratio = getCurrentNumber() + "/" + maxNumber;
        String expStr = ratio.length() > 5 ? "   EXP\n" : " EXP\n";
        
        text.setText(expStr + ratio);
        text.fitInTextBox(1f);
        alignTextToEquilibrium();
    }
    
    public void levelUp(float seconds, Runnable onFinish) { //seconds is typically 0.1f
        node.detachChild(text); //detach the "EXP: 75/100" text
        node.attachChild(levelUpTextPanel); //attach the "LEVEL UP!" text, which is an image
        
        VisualTransition VT = new VisualTransition(levelUpTextPanel, Animation.ZoomIn().setLength(seconds));
        VT.onFinishTransitions(onFinish);
        
        addTransitionToGroup(VT);
    }
    
    public void levelUp(float seconds) {
        levelUp(seconds, () -> {});
    }

    @Override
    public SpatialOperator getAnchor() {
        return new SpatialOperator(expbar, Vector3F.fit(Vector2F.fill(expbar.getOuterRadius() * 2)), Vector3F.fit(equilibrium));
    }

    @Override
    public void alignTextToEquilibrium() {
        textAnchor.getDimensions().set(text.getTextBounds());
        textAnchor.alignToLocally(Vector3F.fit(equilibrium));
    }
}
