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
import enginetools.math.SpatialOperator;

/**
 *
 * @author NiteZ
 */
public class Text2D extends Node {
    public static final class FormatParams {
        public final ColorRGBA color;
        public final TextProperties textParams;
        public final FontProperties fontParams;
        public final TrueTypeFont font;
        
        public FormatParams(ColorRGBA color, TextProperties textParams, FontProperties fontParams, TrueTypeFont font) {
            this.color = color;
            this.textParams = textParams;
            this.fontParams = fontParams;
            this.font = font;
        }
        
        public FormatParams(ColorRGBA color, TextProperties textParams, FontProperties fontParams, AssetManager assetManager) {
            this(color, textParams, fontParams, fontParams.retrieveFont(assetManager));
        }
    }
    
    public static final String OUTLINE_MAT_ASSETKEYNAME = "MatDefs/TTF/TTF_BitmapOutlined.j3md";
    
    private final FontProperties fontProperties;
    private final TrueTypeFont ttf;
    private final StringContainer sc;
    private final TrueTypeContainer textContainer;
    private ColorRGBA textColor, outlineColor;
    
    public Text2D(String text, Text2D.FormatParams formatParams) {
        this(text, formatParams.color, formatParams.textParams, formatParams.fontParams, formatParams.font);
    }
    
    public Text2D(String text, ColorRGBA color, TextProperties textParams, FontProperties fontParams, AssetManager assetManager) {
        this(text, color, textParams, fontParams, fontParams.retrieveFont(assetManager));
    }
    
    public Text2D(String text, ColorRGBA color, TextProperties textParams, FontProperties fontParams, TrueTypeFont font) {
        super("Text2D: " + "\"" + text + "\"");
        fontProperties = fontParams;
        textColor = color;
        ttf = font;
        
        if (textParams.getTextBox() != null) {
            sc = new StringContainer(ttf, text, textParams.getKerning(), textParams.getTextBox());
        } else {
            sc = new StringContainer(ttf, text, textParams.getKerning());
            sc.setUseTextBox(false);
        }
        
        textContainer = ttf.getFormattedText(sc, ColorRGBA.White);
        textContainer.getMaterial().setColor("Color", textColor);
        textContainer.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        textContainer.getMaterial().getAdditionalRenderState().setDepthWrite(false);
        styleText(textParams.createStyleCalls());
        
        attachChild(textContainer);
    }
    
    public TrueTypeContainer getTextContainer() { return textContainer; } //use this method for all the other things to do with the text
    public FontProperties getFontProperties() { return fontProperties; }
    public ColorRGBA getTextColor() { return textColor; }
    
    public TextProperties salvageTextProperties() {
        TextProperties properties = new TextProperties(sc.getTextBox(), sc.getKerning(), sc.getWrapMode(), sc.getAlignment(), sc.getVerticalAlignment());
        properties.setUsesTextBox(sc.useTextBox());
        return properties;
    }
    
    public String getText() { return sc.getText(); }
    public int getOffset() { return sc.getOffset(); }
    public int getLineCount() { return sc.getLineCount(); }
    public int getNumNonSpaceCharacters() { return sc.getNumNonSpaceCharacters(); }
    public float getTextWidth() { return sc.getTextWidth(); }   // width of the actual text in pixels (bounds) from the upper left corner at the origin to the lowest point of the lowest character on the last line
    public float getTextHeight() { return sc.getTextHeight(); } // height of the actual text in pixels (bounds) from the upper left corner at the origin to the lowest point of the lowest character on the last line
    public Vector3f getTextBounds() { return new Vector3f(sc.getTextWidth(), sc.getTextHeight(), 0f); }
    
    public float getTextBoxWidth() { return textContainer.getWidth(); }   // width of the textbox with all its padding and everything 
    public float getTextBoxHeight() { return textContainer.getHeight(); } // height of the textbox with all its padding and everything
    public Vector3f getTextBoxBounds() { return new Vector3f(textContainer.getWidth(), textContainer.getHeight(), 0f); }
    
    public Material getMaterial() { 
        return textContainer.getMaterial(); 
    }
    
    public static Material createTextOutlineMaterial(AssetManager assetManager) {
        Material outlineMaterial = new Material(assetManager, OUTLINE_MAT_ASSETKEYNAME);
        outlineMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        outlineMaterial.getAdditionalRenderState().setDepthWrite(false);
        
        return outlineMaterial;
    }
    
    public ColorRGBA getTextOutlineColor() {
        if (!usingTextOutline()) {
            return null;
        }
        
        return outlineColor;
    }
    
    public boolean usingTextOutline() {
        String assetKeyName = textContainer.getMaterial().getAssetName();
        return assetKeyName != null && assetKeyName.equals(OUTLINE_MAT_ASSETKEYNAME); 
    }
    
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
    
    public void setUsesTextBox(boolean usesTextBox) {
        sc.setUseTextBox(usesTextBox);
        textContainer.updateGeometry();
    }
    
    @Override
    public void setMaterial(Material mat) {
        textContainer.setMaterial(mat);
    }
    
    public void setTexture(Texture tex) {
        textContainer.getMaterial().setTexture("Texture", tex);
    }
    
    public void setTextColor(ColorRGBA color) {
        textColor = color;
        textContainer.getMaterial().setColor("Color", textColor);
    }
    
    public void setTextOutlineColor(ColorRGBA color) throws IllegalStateException {
        if (!usingTextOutline()) {
            throw new IllegalStateException("You cannot set the outline color if it is not using an outline!");
        }
        
        outlineColor = color;
        textContainer.getMaterial().setColor("Outline", outlineColor);
    }
    
    public void setTextAndOutlineColors(ColorRGBA nativeColor, ColorRGBA textOutlineColor) throws IllegalStateException {
        if (!usingTextOutline()) {
            throw new IllegalStateException("You cannot set the outline color if it is not using an outline!");
        }
        
        textColor = nativeColor;
        outlineColor = textOutlineColor;
        
        textContainer.getMaterial().setColor("Color", textColor);
        textContainer.getMaterial().setColor("Outline", outlineColor);
    }
    
    public void setTextAlpha(float alpha) {
        setTextColor(new ColorRGBA(textColor.r, textColor.g, textColor.b, alpha));
        
        if (usingTextOutline()) {
            outlineColor = new ColorRGBA(outlineColor.r, outlineColor.g, outlineColor.b, alpha);
            textContainer.getMaterial().setColor("Outline", outlineColor);
        }
    }
    
    public SpatialOperator createSpatialOperator(float percentX, float percentY) {
        return new SpatialOperator(
            this,
            getTextBounds(),
            new Vector3f(percentX, percentY, 0),
            SpatialOperator.ORIGIN_TOP_LEFT
        );
    }
    
    public void fitInTextBox(float extraPaddingX, float extraPaddingY) {
        Vector3f textBounds = getTextBounds(), textBoxBounds = getTextBoxBounds();
        Rectangle textBox = new Rectangle(
            0,
            0,
            Math.max(textBounds.x, textBoxBounds.x) + extraPaddingX,
            Math.max(textBounds.y, textBoxBounds.y) + extraPaddingY
        );
        
        setTextBox(textBox);
    }
    
    public void fitInTextBox(float extraPadding) {
        fitInTextBox(extraPadding, extraPadding);
    }
}
