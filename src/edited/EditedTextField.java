/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edited;

import java.util.Map;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Panel;
import static com.simsilica.lemur.Panel.LAYER_BACKGROUND;
import static com.simsilica.lemur.Panel.LAYER_BORDER;
import static com.simsilica.lemur.Panel.LAYER_INSETS;
import com.simsilica.lemur.VAlignment;

import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TextEntryComponent;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.event.KeyActionListener;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.FocusMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.style.StyleDefaults;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.StyleAttribute;
import com.simsilica.lemur.style.Styles;
import com.simsilica.lemur.text.DefaultDocumentModel;
import com.simsilica.lemur.text.DocumentModel;

public class EditedTextField extends Panel {
     public static final String ELEMENT_ID = "textField";

    public static final String LAYER_TEXT = "text";

    private EditedTextEntryComponent text;

    public EditedTextField( String text ) {
        this(new DefaultDocumentModel(text), true, new ElementId(ELEMENT_ID), null);
    }

    public EditedTextField( DocumentModel model ) {
        this(model, true, new ElementId(ELEMENT_ID), null);
    }

    public EditedTextField( String text, String style ) {
        this(new DefaultDocumentModel(text), true, new ElementId(ELEMENT_ID), style);
    }

    public EditedTextField( String text, ElementId elementId ) {
        this(new DefaultDocumentModel(text), true, elementId, null);
    }
    
    public EditedTextField( String text, ElementId elementId, String style ) {
        this(new DefaultDocumentModel(text), true, elementId, style);
    }

    public EditedTextField( DocumentModel model, String style ) {
        this(model, true, new ElementId(ELEMENT_ID), style);
    }

    protected EditedTextField( DocumentModel model, boolean applyStyles, ElementId elementId, String style ) {
        super(false, elementId, style);
 
        // Set our layer ordering
        getControl(GuiControl.class).setLayerOrder(LAYER_INSETS, 
                                                   LAYER_BORDER, 
                                                   LAYER_BACKGROUND,
                                                   LAYER_TEXT);

        setDocumentModel(model);

        addControl(new MouseEventControl(FocusMouseListener.INSTANCE));

        if( applyStyles ) {
            Styles styles = GuiGlobals.getInstance().getStyles();
            styles.applyStyles(this, elementId, style);
        }
    }
    
    public EditedTextEntryComponent getTextEntryComponent() { return text; }

    protected void setDocumentModel( DocumentModel model ) {
        if( model == null ) {
            return;
        }
        this.text = createTextEntryComponent(model);
        getControl(GuiControl.class).setComponent(LAYER_TEXT, text);
    }

    protected EditedTextEntryComponent createTextEntryComponent( DocumentModel model ) {
        Styles styles = GuiGlobals.getInstance().getStyles();
        BitmapFont font = styles.getAttributes(getElementId().getId(), getStyle()).get("font", BitmapFont.class);
        return new EditedTextEntryComponent(model, font);
    }

    @StyleDefaults(ELEMENT_ID)
    public static void initializeDefaultStyles( Attributes attrs ) {
        attrs.set("background", new QuadBackgroundComponent(new ColorRGBA(0,0,0,1)), false);
        attrs.set("singleLine", true);
    }

    public Map<KeyAction,KeyActionListener> getActionMap() {
        return text.getActionMap();
    }

    public DocumentModel getDocumentModel() {
        return text.getDocumentModel();
    }

    @StyleAttribute(value="text", lookupDefault=false)
    public void setText( String s ) {
        text.setText(s);
    }

    public String getText() {
        return text == null ? null : text.getText();
    }

    @StyleAttribute(value="textVAlignment", lookupDefault=false)
    public void setTextVAlignment( VAlignment a ) {
        text.setVAlignment(a);
    }

    public VAlignment getTextVAlignment() {
        return text.getVAlignment();
    }

    @StyleAttribute(value="textHAlignment", lookupDefault=false)
    public void setTextHAlignment( HAlignment a ) {
        text.setHAlignment(a);
    }

    public HAlignment getTextHAlignment() {
        return text.getHAlignment();
    }

    @StyleAttribute("font")
    public void setFont( BitmapFont f ) {
        text.setFont(f);
    }

    public BitmapFont getFont() {
        return text.getFont();
    }

    @StyleAttribute("color")
    public void setColor( ColorRGBA color ) {
        text.setColor(color);
    }

    public ColorRGBA getColor() {
        return text == null ? null : text.getColor();
    }

    @StyleAttribute("fontSize")
    public void setFontSize( float f ) {
        text.setFontSize(f);
    }

    public float getFontSize() {
        return text == null ? 0 : text.getFontSize();
    }

    @StyleAttribute("singleLine")
    public void setSingleLine( boolean f ) {
        text.setSingleLine(f);
    }

    public boolean isSingleLine() {
        return text.isSingleLine();
    }

    @StyleAttribute("preferredWidth")
    public void setPreferredWidth( float f ) {
        text.setPreferredWidth(f);
    }

    public float getPreferredWidth() {
        return text.getPreferredWidth();
    }

    @StyleAttribute("preferredLineCount")
    public void setPreferredLineCount( int i ) {
        text.setPreferredLineCount(i);
    }

    public float getPreferredLineCount() {
        return text.getPreferredLineCount();
    }

    /**
     *  Sets the preferred with of the cursor quad.  If set to null then
     *  the default behavior is used.  See TextEntryComponent.setPreferredCursorWidth().
     */
    @StyleAttribute("preferredCursorWidth")
    public void setPreferredCursorWidth( Float f ) {
        text.setPreferredCursorWidth(f);
    }

    public Float getPreferredCursorWidth() {
        return text.getPreferredCursorWidth();
    }

    @Override
    public String toString() {
        return getClass().getName() + "[text=" + getText() + ", color=" + getColor() + ", elementId=" + getElementId() + "]";
    }    
}
