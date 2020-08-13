/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edited;

/**
 *
 * @author night
 */

import com.simsilica.lemur.Label;
        
import com.simsilica.lemur.style.StyleDefaults;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.StyleAttribute;
import com.simsilica.lemur.style.Styles;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.component.TextComponent;
import com.simsilica.lemur.core.GuiComponent;

import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.asset.TrueTypeKeyBMP;
import com.atr.jme.font.asset.TrueTypeKeyMesh;
import com.atr.jme.font.asset.TrueTypeLoader;
import com.atr.jme.font.util.Style;
import com.simsilica.lemur.GuiGlobals;
import static com.simsilica.lemur.Label.LAYER_ICON;
import static com.simsilica.lemur.Label.LAYER_SHADOW_TEXT;
import static com.simsilica.lemur.Label.LAYER_TEXT;
import static com.simsilica.lemur.Panel.LAYER_BACKGROUND;
import static com.simsilica.lemur.Panel.LAYER_BORDER;
import static com.simsilica.lemur.Panel.LAYER_INSETS;

public class EditedLabel extends Label {
    protected TrueTypeFont ttf;
    protected EditedTextComponent shadow;
    public EditedTextComponent text;
    
    public static final String ELEMENT_ID = "editedlabel";
    
    public EditedLabel(String s, TrueTypeFont ttf) {
        this( s, true, new ElementId(ELEMENT_ID), null, ttf );
    }
    
    public EditedLabel(String s, TrueTypeFont ttf, ColorRGBA c) {
        this( s, true, new ElementId(ELEMENT_ID), null, ttf, c );
    }
    
    public EditedLabel(String s, TrueTypeFont ttf, ColorRGBA c, ColorRGBA o) {
        this( s, true, new ElementId(ELEMENT_ID), null, ttf, c, o );
    }
    
    public EditedLabel(String s, TrueTypeFont ttf, ColorRGBA c, int kerning) {
        this( s, true, new ElementId(ELEMENT_ID), null, ttf, c, kerning );
    }
    
    public EditedLabel( String s, String style, TrueTypeFont ttf ) {
        this( s, true, new ElementId(ELEMENT_ID), style, ttf );
    }

    public EditedLabel( String s, ElementId elementId, TrueTypeFont ttf ) {
        this( s, true, elementId, null, ttf );
    }
    
    public EditedLabel( String s, ElementId elementId, String style, TrueTypeFont ttf ) {
        this( s, true, elementId, style, ttf );
    }
    
    protected EditedLabel( String s, boolean applyStyles, ElementId elementId, String style, TrueTypeFont ttf ) {
        super(" ", false, elementId, style);
        this.ttf = ttf;
        
        // Set our layers
        getControl(GuiControl.class).setLayerOrder(LAYER_INSETS, 
                                                   LAYER_BORDER, 
                                                   LAYER_BACKGROUND,
                                                   LAYER_ICON,
                                                   LAYER_SHADOW_TEXT,
                                                   LAYER_TEXT);

        // Retrieve the font before creation so that if the font is
        // customized by the style then we don't end up creating a
        // BitmapText object just to throw it away when a new font
        // is set right after.  It's a limitation of BitmapText that
        // can't have it's font changed post-creation.
        Styles styles = GuiGlobals.getInstance().getStyles();
        
        BitmapFont font = styles.getAttributes(elementId.getId(), style).get("font", BitmapFont.class);
        
        text = new EditedTextComponent(s, ttf, font);
        //this.text = new TextComponent(s, font);
        text.setLayer(3);

        getControl(GuiControl.class).setComponent(LAYER_TEXT, text);

        if( applyStyles ) {
            styles.applyStyles(this, elementId, style);
        }
    }
     
    protected EditedLabel( String s, boolean applyStyles, ElementId elementId, String style, TrueTypeFont ttf, ColorRGBA tColor ) {
        super(" ", false, elementId, style);
        this.ttf = ttf;
        
        // Set our layers
        getControl(GuiControl.class).setLayerOrder(LAYER_INSETS, 
                                                   LAYER_BORDER, 
                                                   LAYER_BACKGROUND,
                                                   LAYER_ICON,
                                                   LAYER_SHADOW_TEXT,
                                                   LAYER_TEXT);

        // Retrieve the font before creation so that if the font is
        // customized by the style then we don't end up creating a
        // BitmapText object just to throw it away when a new font
        // is set right after.  It's a limitation of BitmapText that
        // can't have it's font changed post-creation.
        Styles styles = GuiGlobals.getInstance().getStyles();
        
        BitmapFont font = styles.getAttributes(elementId.getId(), style).get("font", BitmapFont.class);
        
        text = new EditedTextComponent(s, ttf, font, tColor);
        //this.text = new TextComponent(s, font);
        text.setLayer(3);

        getControl(GuiControl.class).setComponent(LAYER_TEXT, text);

        if( applyStyles ) {
            styles.applyStyles(this, elementId, style);
        }
    }
    
    protected EditedLabel( String s, boolean applyStyles, ElementId elementId, String style, TrueTypeFont ttf, ColorRGBA tColor, int kerning ) {
        super(" ", false, elementId, style);
        this.ttf = ttf;
        
        // Set our layers
        getControl(GuiControl.class).setLayerOrder(LAYER_INSETS, 
                                                   LAYER_BORDER, 
                                                   LAYER_BACKGROUND,
                                                   LAYER_ICON,
                                                   LAYER_SHADOW_TEXT,
                                                   LAYER_TEXT);

        // Retrieve the font before creation so that if the font is
        // customized by the style then we don't end up creating a
        // BitmapText object just to throw it away when a new font
        // is set right after.  It's a limitation of BitmapText that
        // can't have it's font changed post-creation.
        Styles styles = GuiGlobals.getInstance().getStyles();
        
        BitmapFont font = styles.getAttributes(elementId.getId(), style).get("font", BitmapFont.class);
        
        text = new EditedTextComponent(s, ttf, font, tColor, kerning);
        //this.text = new TextComponent(s, font);
        text.setLayer(3);

        getControl(GuiControl.class).setComponent(LAYER_TEXT, text);

        if( applyStyles ) {
            styles.applyStyles(this, elementId, style);
        }
    }
    
    protected EditedLabel( String s, boolean applyStyles, ElementId elementId, String style, TrueTypeFont ttf, ColorRGBA tColor, ColorRGBA oColor) {
        super(" ", false, elementId, style);
        this.ttf = ttf;
        
        // Set our layers
        getControl(GuiControl.class).setLayerOrder(LAYER_INSETS, 
                                                   LAYER_BORDER, 
                                                   LAYER_BACKGROUND,
                                                   LAYER_ICON,
                                                   LAYER_SHADOW_TEXT,
                                                   LAYER_TEXT);

        // Retrieve the font before creation so that if the font is
        // customized by the style then we don't end up creating a
        // BitmapText object just to throw it away when a new font
        // is set right after.  It's a limitation of BitmapText that
        // can't have it's font changed post-creation.
        Styles styles = GuiGlobals.getInstance().getStyles();
        
        BitmapFont font = styles.getAttributes(elementId.getId(), style).get("font", BitmapFont.class);
        
        text = new EditedTextComponent(s, ttf, font, tColor, oColor);
        //this.text = new TextComponent(s, font);
        text.setLayer(3);

        getControl(GuiControl.class).setComponent(LAYER_TEXT, text);

        if( applyStyles ) {
            styles.applyStyles(this, elementId, style);
        }
    }
    
    @Override
    public String getText() {
        return text == null ? null : text.getText();
    }
    
    @StyleAttribute(value="text", lookupDefault=false)
    @Override
    public void setText( String s ) {
        text.setText(s);
        /*if( shadow != null ) {
            shadow.setText(s);
        }*/
    }
    
}
