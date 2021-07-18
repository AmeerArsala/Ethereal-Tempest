/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import battle.data.forecast.SingularForecast;
import com.jme3.asset.AssetManager;
import battle.data.participant.Combatant;
import battle.data.participant.Combatant.AttackType;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.atr.jme.font.util.Style;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.simsilica.lemur.LayerComparator;
import enginetools.MaterialCreator;
import enginetools.math.SpatialOperator;
import etherealtempest.Globals;
import etherealtempest.gui.broad.ExpIndicator;
import etherealtempest.gui.broad.RadialProgressBar;
import etherealtempest.gui.broad.ShapeIndicator;
import etherealtempest.gui.specific.LevelUpPanel;
import general.ui.GeometryPanel;
import general.ui.text.FontProperties;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.ui.text.quickparams.TextDisplacementParams;
import general.ui.text.quickparams.UIFontParams;
import java.util.function.Consumer;
import maps.data.MapTextures;

/**
 *
 * @author night
 */
public class GuiFactory {
    public static Text2D generateText(String text, ColorRGBA color, Rectangle rectangle, UIFontParams params, TextDisplacementParams displacementParams, AssetManager assetManager) {
        return new Text2D(
            text, 
            color, 
            displacementParams.createTextProperties(params.kerning, rectangle), 
            params.createFontProperties(FontProperties.KeyType.BMP), 
            assetManager
        );
    }
    
    public static Text2D generateText(String text, ColorRGBA color, UIFontParams params, TextDisplacementParams displacementParams, AssetManager assetManager) {
        return new Text2D(
            text, 
            color, 
            displacementParams.createTextProperties(params.kerning), 
            params.createFontProperties(FontProperties.KeyType.BMP), 
            assetManager
        );
    }
    
    //use for creating portrait and portraitFrame
    public static GeometryPanel createPanel(float width, float height, AssetManager assetManager, Texture texture, ColorRGBA color, boolean mirror) {
        GeometryPanel panel = new GeometryPanel(width, height, RenderQueue.Bucket.Gui);
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        mat.setTexture("ColorMap", texture);
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        panel.setMaterial(mat);
        
        if (mirror) {
            panel.mirror();
        }
        
        return panel;
    }
    
    public static GeometryPanel createPanelWithText(Text2D panelText, float widthScalar, float heightScalar, AssetManager assetManager, Texture texture, ColorRGBA color, boolean mirror) {
        float width = widthScalar * panelText.getTextBoxWidth();
        float height = heightScalar * panelText.getTextBoxHeight();
        GeometryPanel battlePanel = createPanel(
            width,
            height,
            assetManager,
            texture,
            color,
            mirror
        );

        battlePanel.attachChild(panelText);
        
        return battlePanel;
    }
    
    public static GeometryPanel createNametag(String text, UIFontParams params, AssetManager assetManager) {
        /*
        Rectangle rect = new Rectangle(
            0f,  // x
            0f,  // y
            3 * text.length() * (params.fontSize + params.kerning), // width
            3 * (params.fontSize + params.kerning)  // height
        );
        */
        
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.CharClip);
        
        Text2D nameText = generateText(text, ColorRGBA.White, params, displacementParams, assetManager);
        //nameText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        float width = (626f / 475f) * nameText.getTextWidth();
        float height = (216f / 125f) * nameText.getTextHeight();
        
        if (text.length() < 10) {
            width *= 10f / text.length();
        }
        
        GeometryPanel nametagPanel = createPanel(
            width,
            height,
            assetManager,
            MapTextures.GUI.Nametag,
            ColorRGBA.White,
            false //dont mirror texture
        );

        nametagPanel.attachChild(nameText);
        
        SpatialOperator nametagAnchor = nameText.createSpatialOperator(0.5f, 0.5f);
        nametagAnchor.alignTo(nametagPanel.getOperator(0.5f, 0.5f));
        //nameText.move(0, nameText.getTextHeight(), 5);
        
        LayerComparator.setLayer(nametagPanel, 3);
        LayerComparator.setLayer(nameText, 4);
        return nametagPanel;
    }
    
    public static GeometryPanel createBattlePanelFromText(Text2D panelText, Vector2f padding, AssetManager assetManager, ColorRGBA color) {
        //padding is illusory and not the ACTUAL padding there would be, since it is multiplied by a ratio to have the image fit
        float width =  (591f / 559f) * (panelText.getTextWidth()  + padding.x);
        float height = (205f / 176f) * (panelText.getTextHeight() + padding.y);
        GeometryPanel battlePanel = createPanel(
            width,
            height,
            assetManager,
            MapTextures.GUI.GlowBox1,
            color,
            false //dont mirror the texture
        );

        battlePanel.attachChild(panelText);
        
        return battlePanel;
    }
    
    public static GeometryPanel createEquippedIcon(Texture iconTex, Vector3f iconDimensions, Vector3f backgroundDimensions, AssetManager assetManager, boolean mirrorUI) {
        GeometryPanel equippedIcon = new GeometryPanel(iconDimensions.x, iconDimensions.y, RenderQueue.Bucket.Gui);
        Material iconMat = new Material(assetManager, "MatDefs/custom/Discard.j3md");
        iconMat.setTexture("ColorMap", iconTex);
        iconMat.setFloat("MaxAlphaDiscard", 0.1f);
        iconMat.getAdditionalRenderState().setDepthWrite(false);
        equippedIcon.setMaterial(iconMat);
        
        //in percent
        float x, leeway = 0.1f;
        float y = 0.5f;
        if (mirrorUI) {
            x = 0.0f + leeway;
        } else {
            x = 1.0f - leeway;
        }
        
        Vector3f factor = new Vector3f(x, y, 0);
        equippedIcon.getOperator(x, y).alignToLocally(backgroundDimensions.mult(factor).addLocal(equippedIcon.getLocalTranslation()));
        return equippedIcon;
    }
    
    public static ShapeIndicator createIndicator(String statName, Vector2f xyDimensions, UIFontParams params, Consumer<Material> matParams, AssetManager assetManager, int current, int max) {
        String text = statName + ": " + current + "/" + max;
        
        /*
        Rectangle rect = new Rectangle(
            0f,  // x
            0f,  // y
            xyDimensions.x * 2, // width
            (4f / 5f) * xyDimensions.x  // height
        );
        */
        
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.CharClip);
        
        Text2D visibleText = generateText(text, ColorRGBA.White, params, displacementParams, assetManager);
        //visibleText.move(0, 0, 3);
        //visibleText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        float currentPercent = ((float)current) / max;
        
        return new ShapeIndicator(statName, xyDimensions, matParams, assetManager, visibleText, currentPercent, max);
    }
    
    public static ExpIndicator createExpBar(UIFontParams params, ColorRGBA color, ColorRGBA textColor, float outerRadius, int currentEXP, int maxEXP, AssetManager assetManager) {
        int specificity = 2;
        float innerToOuterRadiusRatio = 52.5f / 70.75f;
        
        RadialProgressBar expCircle = new RadialProgressBar(innerToOuterRadiusRatio * outerRadius, outerRadius, color, specificity, assetManager);
        
        //Rectangle rectangle = new Rectangle(0f, 0f, innerToOuterRadiusRatio * outerRadius * 2, innerToOuterRadiusRatio * outerRadius * 2); //x, y, width, height
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.Word);
        
        Text2D.FormatParams textFormat = new Text2D.FormatParams(
            textColor,
            displacementParams.createTextProperties(params.kerning),
            params.createFontProperties(FontProperties.KeyType.BMP),
            assetManager
        );
        
        return new ExpIndicator(expCircle, textFormat, assetManager, currentEXP, maxEXP);
    }
    
    public static LevelUpPanel createLevelUpPanel(SingularForecast forecast, AssetManager assetManager) {
        return new LevelUpPanel(forecast.getCombatant().getUnit(), new Vector3f(Globals.getScreenWidth(), Globals.getScreenHeight(), 1f), assetManager);
    }
}
