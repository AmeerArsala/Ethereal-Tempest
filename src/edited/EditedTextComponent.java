/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edited;

import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.shape.TrueTypeNode;
import com.simsilica.lemur.component.TextComponent;

import com.jme3.font.*;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.Rectangle;
import com.jme3.math.*;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.LayerComparator;
import com.simsilica.lemur.VAlignment;

/**
 *
 * @author night
 */
public class EditedTextComponent extends TextComponent {
    private TrueTypeFont ttf;
    private TrueTypeNode text;
    private ColorRGBA color = ColorRGBA.White;
    
    //private BitmapText bitmapText; //must be blank as in ""
    private Rectangle textBox;
    
    public EditedTextComponent(String text, TrueTypeFont ttf, BitmapFont placeholder) {
        super("", placeholder);
        this.ttf = ttf;
        this.text = this.ttf.getText(text, 2, color);
    }
    
    public EditedTextComponent(String text, TrueTypeFont ttf, BitmapFont placeholder, ColorRGBA color) {
        super("", placeholder);
        this.ttf = ttf;
        this.text = this.ttf.getText(text, 2, color);
        this.color = color;
    }
    
    public EditedTextComponent(String text, TrueTypeFont ttf, BitmapFont placeholder, ColorRGBA color, int kerning) {
        super("", placeholder);
        this.ttf = ttf;
        this.text = this.ttf.getText(text, kerning, color);
        this.color = color;
    }
    
    public EditedTextComponent(String text, TrueTypeFont ttf, BitmapFont placeholder, ColorRGBA color, ColorRGBA oColor) {
        super("", placeholder);
        this.ttf = ttf;
        this.text = ((TrueTypeBMP)this.ttf).getText(text, 2, color, oColor);
        this.color = color;
    }
    
    public void resetText(String str, ColorRGBA color) {
        text.detachAllChildren();
        text = ttf.getText(str, 2, color);
        getNode().detachChild(text);
        getNode().attachChild(text);
    }
    
    public void resetText(String str, ColorRGBA color, TrueTypeFont ttf) {
        text.detachAllChildren();
        this.ttf = ttf;
        text = this.ttf.getText(str, 2, color);
        getNode().detachChild(text);
        getNode().attachChild(text);
    }
    
    @Override
    public EditedTextComponent clone() {
        EditedTextComponent result = (EditedTextComponent)super.clone();
        result.ttf = null;
        //result.bitmapText = bitmapText.clone();
        result.textBox = null;
        return result;
    }
    
    public TrueTypeNode getTTFNode() { return text; }
    public TrueTypeFont getTTF() { return ttf; }
    
    @Override
    public void attach( GuiControl parent ) {
        super.attach(parent);
        getNode().attachChild(text);
    }

    @Override
    public void detach( GuiControl parent ) {
        getNode().detachChild(text);
        super.detach(parent);
    }
    
    @Override
    public void setText( String text ) {
        try {
            
        if( text != null && text.equals(this.text.getText()) )
            return;

        this.text.setText(text);
        invalidate();
        
        }
        catch (NullPointerException e) { }
    }
    
    @Override
    public String getText() {
        return this.text.getText();
    }
    
    public void setFont(TrueTypeFont font) { //this.ttf = font;
        if( font == this.ttf )
            return;
            
        if( isAttached() ) {
            text.removeFromParent();
        }

        // Can't change the font once created so we'll
        // have to create it fresh
        ttf = font;
        TrueTypeNode newText = ttf.getText(text.getText(), 0, color);
        newText.setLocalTranslation(text.getLocalTranslation());
        ttf.setScale(getFontSize());
        this.text = newText;
        resetLayer();

        // Need to invalidate because we probably changed size
        // And that will realign us, etc. anyway.
        invalidate();

        if( isAttached() ) {
            getNode().attachChild(text);
        }
    }
    
    @Override
    public void setFontSize( float size ) {
        float scale, actualSize;
        if (size < 32f) {
            actualSize = (int)Math.floor(size / 0.73f);
            scale = size / (float)actualSize;
            ttf.setScale(scale);
        } else if (size < 53f) {
            actualSize = (int)Math.floor(size / 0.84f);
            scale = size / (float)actualSize;
            ttf.setScale(scale);
        }
        
        super.setFontSize(size);
        invalidate();
        
    }
    
    public float getScale() { return ttf.getScale(); }
    
    @Override
    public void setColor(ColorRGBA rgba) {
        color = rgba;
        text = ttf.getText(text.getText(), 2, rgba);
    }
    
    @Override
    public ColorRGBA getColor() { return color; }
    
    @Override
    public float getAlpha() {
        return color.getAlpha();
    }
    
    
    
}
