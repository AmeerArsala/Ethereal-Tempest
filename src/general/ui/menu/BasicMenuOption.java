/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import enginetools.MaterialCreator;
import general.math.function.RGBAFunction;
import general.ui.GeometryPanel;
import general.ui.Padding;
import general.ui.menu.BasicMenu.Orientation;
import general.ui.text.FontProperties;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.utils.helpers.EngineUtils;
import general.utils.helpers.EngineUtils.CenterAxis;
import java.util.Arrays;

/**
 *
 * @author night
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 */
public abstract class BasicMenuOption<DATA> extends MenuOption<DATA> {
    private final TextProperties textParams;
    private final ColorRGBA textColor;
    
    private GeometryPanel option;
    private Material bgMat; // if using a shader, put a boolean to toggle it on and off depending on whether the option is hovered
    private RGBAFunction hoveredColorFunc, notHoveredColorFunc;
    
    private float alphaMultiplier = 0.8f;
    
    private Text2D text;
    private Vector3f deltaPadding;
    
    private BasicMenu.Commands universalOptionCommands;
    
    public BasicMenuOption(String optionName, TextProperties textParams, ColorRGBA textColor) {
        super(optionName);
        this.textParams = textParams;
        this.textColor = textColor;
    }
    
    public GeometryPanel getPanel() { return option; }
    public Text2D getText2D() { return text; }
    public float getAlphaMultiplier() { return alphaMultiplier; }
    
    public void setCommands(BasicMenu.Commands commands) {
        universalOptionCommands = commands;
    }
    
    public void setAlphaMultiplier(float alphaMult) {
        alphaMultiplier = alphaMult;
    }
    
    //returns the calculated size of the panel
    Vector3f initializeText(AssetManager assetManager, FontProperties uniformFontProperties, Padding textBoxPadding) {
        text = new Text2D(name, textColor, textParams, uniformFontProperties, assetManager);
        deltaPadding = new Vector3f(textBoxPadding.right - textBoxPadding.left, textBoxPadding.top - textBoxPadding.bottom, 1);
        
        return new Vector3f(text.getTextBoxWidth() + textBoxPadding.getTotalHorizontalPadding(), text.getTextBoxHeight() + textBoxPadding.getTotalVerticalPadding(), 1f);
    }
    
    void correctPanelPosition(float paddingBetweenOptions, Orientation menuOrientation) {
        if (menuOrientation == Orientation.Vertical) {
            option.move(0, index * (option.getHeight() + paddingBetweenOptions), 0);
        } else { // menuOrientation == Orientation.Horizontal
            option.move(index * (option.getWidth() + paddingBetweenOptions), 0, 0);
        }
    }
    
    public void initializeAssets(AssetManager assetManager, Vector3f panelSize, MaterialCreator matCreator, RGBAFunction hoveredColorFunction, RGBAFunction notHoveredColorFunction) {
        hoveredColorFunc = hoveredColorFunction;
        notHoveredColorFunc = notHoveredColorFunction;

        option = new GeometryPanel(panelSize.x, panelSize.y, RenderQueue.Bucket.Gui);
        
        Texture containerDefault = ((TbtQuadBackgroundComponent)new Container().getBackground()).getTexture();
        bgMat = matCreator.createMaterial(assetManager);
        bgMat.setTexture("ColorMap", containerDefault);
        option.setMaterial(bgMat);
        
        Vector3f displacement = EngineUtils.centerEntity(text.getTextBoxBounds(), panelSize, Arrays.asList(CenterAxis.X, CenterAxis.Y));
        text.move(0, text.getTextHeight(), 0);
        text.move(displacement.add(deltaPadding));
        
        option.attachChild(text);
        optionNode.attachChild(option);
    }
    
    @Override
    public void updateCustom(float tpf, float time) {
        ColorRGBA color;
        if (hovered) {
            color = hoveredColorFunc.rgba(time, alphaMultiplier);
        } else {
            color = notHoveredColorFunc.rgba(time, alphaMultiplier);
        }
        
        bgMat.setColor("Color", color); 
    }
    
    @Override
    protected void setHover(boolean hov) {
        if (hov) {
            universalOptionCommands.runSetHoveredOn(this);
        } else {
            universalOptionCommands.runSetHoveredOff(this);
        }
    }
}
