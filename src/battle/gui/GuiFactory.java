/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.gui;

import battle.data.forecast.SingularForecast;
import com.jme3.asset.AssetManager;
import battle.participant.Combatant;
import battle.participant.Combatant.AttackType;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.LayerComparator;
import enginetools.MaterialCreator;
import enginetools.MaterialParamsProtocol;
import enginetools.math.SpatialOperator;
import etherealtempest.Globals;
import etherealtempest.gui.broad.ShapeIndicator;
import etherealtempest.gui.specific.LevelUpPanel;
import general.ui.GeometryPanel;
import general.ui.text.FontProperties;
import general.ui.text.Text2D;
import general.ui.text.TextProperties;
import general.ui.text.quickparams.TextDisplacementParams;
import general.ui.text.quickparams.UIFontParams;

/**
 *
 * @author night
 */
public class GuiFactory {
    public static Text2D generateText(String text, ColorRGBA color, Rectangle rectangle, UIFontParams params, TextDisplacementParams displacementParams, AssetManager assetManager) {
        TextProperties textParams = 
            TextProperties.builder()
                .horizontalAlignment(displacementParams.hAlign)
                .verticalAlignment(displacementParams.vAlign)
                .kerning(params.kerning)
                .wrapMode(displacementParams.wrapMode)
                .textBox(rectangle)
                .build();
        
        return new Text2D(text, color, textParams, params.createFontProperties(FontProperties.KeyType.BMP), assetManager);
    }
    
    //use for creating portrait and portraitFrame
    public static GeometryPanel createPanel(float width, float height, AssetManager assetManager, String texturePath, ColorRGBA color, boolean mirror) {
        GeometryPanel panel = new GeometryPanel(width, height, RenderQueue.Bucket.Gui);
        
        Material mat = new Material(assetManager, MaterialCreator.UNSHADED);
        mat.setTexture("ColorMap", assetManager.loadTexture(texturePath));
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        
        panel.setMaterial(mat);
        
        if (mirror) {
            panel.mirror();
        }
        
        return panel;
    }
    
    public static GeometryPanel createPanelWithText(Text2D panelText, float widthScalar, float heightScalar, AssetManager assetManager, String texturePath, ColorRGBA color, boolean mirror) {
        float width = widthScalar * panelText.getTextBoxWidth();
        float height = heightScalar * panelText.getTextBoxHeight();
        GeometryPanel battlePanel = createPanel(
            width,
            height,
            assetManager,
            texturePath,
            color,
            mirror
        );

        battlePanel.attachChild(panelText);
        
        return battlePanel;
    }
    
    public static GeometryPanel createNametag(String text, UIFontParams params, AssetManager assetManager) {
        Rectangle rect = new Rectangle(
            0f,  // x
            0f,  // y
            3 * text.length() * (params.fontSize + params.kerning), // width
            3 * (params.fontSize + params.kerning)  // height
        );
        
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.CharClip);
        
        Text2D nameText = generateText(text, ColorRGBA.White, rect, params, displacementParams, assetManager);
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
            "Interface/GUI/ui_boxes/emptyname.png", //texture path
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
    
    public static GeometryPanel createBattlePanelFromText(Text2D panelText, float heightScalar, AssetManager assetManager, ColorRGBA color) {
        float width = (591f / 559f) * panelText.getTextBoxWidth();
        float height = (205f / 176f) * heightScalar * panelText.getTextBoxHeight();
        GeometryPanel battlePanel = createPanel(
            width,
            height,
            assetManager,
            "Interface/GUI/ui_boxes/box5.png", //texture path
            color,
            false //dont mirror the texture
        );

        battlePanel.attachChild(panelText);
        
        return battlePanel;
    }
    
    public static GeometryPanel createEquippedIcon(String iconPath, Vector3f iconDimensions, Vector3f backgroundDimensions, AssetManager assetManager, boolean mirrorUI) {
        GeometryPanel equippedIcon = new GeometryPanel(iconDimensions.x, iconDimensions.y, RenderQueue.Bucket.Gui);
        Material iconMat = new Material(assetManager, "MatDefs/custom/Discard.j3md");
        iconMat.setTexture("ColorMap", assetManager.loadTexture(iconPath));
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
    
    public static ShapeIndicator createIndicator(String statName, Vector2f xyDimensions, UIFontParams params, MaterialParamsProtocol matParams, AssetManager assetManager, int current, int max) {
        String text = statName + ": " + current + "/" + max;
        
        Rectangle rect = new Rectangle(
            0f,  // x
            0f,  // y
            xyDimensions.x * 2, // width
            (4f / 5f) * xyDimensions.x  // height
        );
        
        TextDisplacementParams displacementParams = new TextDisplacementParams(Align.Left, VAlign.Top, WrapMode.CharClip);
        
        Text2D visibleText = generateText(text, ColorRGBA.White, rect, params, displacementParams, assetManager);
        //visibleText.move(0, 0, 3);
        //visibleText.setOutlineMaterial(ColorRGBA.White, ColorRGBA.Black);
        
        float currentPercent = ((float)current) / max;
        
        return new ShapeIndicator(statName, xyDimensions, matParams, assetManager, visibleText, currentPercent, max);
    }
    
    public static LevelUpPanel createLevelUpPanel(SingularForecast forecast, AssetManager assetManager) {
        return new LevelUpPanel(forecast.getCombatant().getUnit(), new Vector3f(Globals.getScreenWidth(), Globals.getScreenHeight(), 1f), assetManager);
    }
}
