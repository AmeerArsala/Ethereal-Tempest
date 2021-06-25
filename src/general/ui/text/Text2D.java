/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.text;

import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.shape.TrueTypeContainer;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.jme3.asset.AssetManager;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Command;
import general.tools.universal.CustomException;

/**
 *
 * @author NiteZ
 */
public class Text2D extends Node {
    private final Material outlineMaterial;
    
    private final FontProperties fontProperties;
    private final TrueTypeFont ttf;
    private final StringContainer sc;
    private final TrueTypeContainer textContainer;
    private ColorRGBA textColor, outlineColor;
    
    public Text2D(String text, ColorRGBA color, TextProperties textParams, FontProperties fontParams, AssetManager assetManager) {
        super("2D Text: " + "\"" + text + "\"");
        fontProperties = fontParams;
        textColor = color;
        
        ttf = fontProperties.retrieveFont(assetManager);
        
        if (textParams.getTextBox() != null) {
            sc = new StringContainer(ttf, text, textParams.getKerning(), textParams.getTextBox());
        } else {
            sc = new StringContainer(ttf, text, textParams.getKerning());
        }
        
        textContainer = ttf.getFormattedText(sc, ColorRGBA.White);
        textContainer.getMaterial().setColor("Color", textColor);
        textContainer.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        styleText(textParams.createStyleCalls());
        
        attachChild(textContainer);
        
        outlineMaterial = new Material(assetManager, "MatDefs/TTF/TTF_BitmapOutlined.j3md");
        outlineMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
    }
    
    public TrueTypeContainer getTextContainer() { return textContainer; } // use this method for all the other things to do with the text
    public FontProperties getFontProperties() { return fontProperties; }
    public ColorRGBA getTextColor() { return textColor; }
    
    public ColorRGBA getOutlineColor() throws CustomException {
        if (textContainer.getMaterial() != outlineMaterial) {
            throw new CustomException("Text is Not Using Outline Material");
        }
        
        return outlineColor;
    }
    
    public boolean usingTextOutline() { 
        return textContainer.getMaterial() == outlineMaterial; 
    }
    
    public TextProperties salvageTextProperties() {
        return new TextProperties(sc.getTextBox(), sc.getKerning(), sc.getWrapMode(), sc.getAlignment(), sc.getVerticalAlignment());
    }
    
    public String getText() { return sc.getText(); }
    public int getOffset() { return sc.getOffset(); }
    public int getLineCount() { return sc.getLineCount(); }
    public int getNumNonSpaceCharacters() { return sc.getNumNonSpaceCharacters(); }
    public float getTextWidth() { return sc.getTextWidth(); } //width of the actual text in pixels (bounds) from the upper left corner at the origin to the lowest point of the lowest character on the last line
    public float getTextHeight() { return sc.getTextHeight(); } //height of the actual text in pixels (bounds) from the upper left corner at the origin to the lowest point of the lowest character on the last line
    public Vector3f getTextBounds() { return new Vector3f(sc.getTextWidth(), sc.getTextHeight(), 0f); }
    
    public float getTextBoxWidth() { return textContainer.getWidth(); } //width of the textbox with all its padding and everything 
    public float getTextBoxHeight() { return textContainer.getHeight(); } //height of the textbox with all its padding and everything
    public Vector3f getTextBoxBounds() { return new Vector3f(textContainer.getWidth(), textContainer.getHeight(), 0.0001f); }
    
    public Material getMaterial() { return textContainer.getMaterial(); }
    
    public final void styleText(Command<StringContainer> styleCalls) {
        styleCalls.execute(sc);
        textContainer.updateGeometry();
    }
    
    public void setText(String text) {
        sc.setText(text);
        textContainer.updateGeometry();
    }
    
    public void setTextBox(Rectangle textBox) {
        sc.setTextBox(textBox);
        textContainer.updateGeometry();
    }
    
    public void setKerning(int kerning) {
        sc.setKerning(kerning);
        textContainer.updateGeometry();
    }
    
    public void setWrapMode(WrapMode wrapMode) {
        sc.setWrapMode(wrapMode);
        textContainer.updateGeometry();
    }
    
    public void setHorizontalAlignment(Align h_align) {
        sc.setAlignment(h_align);
        textContainer.updateGeometry();
    }
    
    public void setVerticalAlignment(VAlign v_align) {
        sc.setVerticalAlignment(v_align);
        textContainer.updateGeometry();
    }
    
    public void setOffset(int offset) {
        sc.setOffset(offset);
        textContainer.updateGeometry();
    }
    
    public void setElipsis(String elipsis) {
        sc.setElipsis(elipsis);
        textContainer.updateGeometry();
    }
    
    public void setMaxLines(int max) {
        sc.setMaxLines(max);
        textContainer.updateGeometry();
    }
    
    @Override
    public void setMaterial(Material mat) {
        textContainer.setMaterial(mat);
    }
    
    public void setOutlineMaterial(ColorRGBA nativeColor, ColorRGBA textOutlineColor) {
        textColor = nativeColor;
        outlineColor = textOutlineColor;
        
        if (textContainer.getMaterial() != outlineMaterial) {
            textContainer.setMaterial(outlineMaterial);
        }
        
        outlineMaterial.setColor("Color", textColor);
        outlineMaterial.setColor("Outline", outlineColor);
    }
    
    public void setTextTexture(Texture tex) {
        textContainer.getMaterial().setTexture("Texture", tex);
    }
    
    public void setTextColor(ColorRGBA color) {
        textColor = color;
        textContainer.getMaterial().setColor("Color", textColor);
    }
    
    public void setTextAlpha(float alpha) {
        setTextColor(new ColorRGBA(textColor.r, textColor.g, textColor.b, alpha));
        
        if (textContainer.getMaterial() == outlineMaterial) {
            outlineColor = new ColorRGBA(outlineColor.r, outlineColor.g, outlineColor.b, alpha);
            outlineMaterial.setColor("Outline", outlineColor);
        }
    }
    
    public void setOutlineColorIfUsingOutline(ColorRGBA outlineColor) {
        if (textContainer.getMaterial() == outlineMaterial) {
            outlineMaterial.setColor("Outline", outlineColor);
        }
    }
}
