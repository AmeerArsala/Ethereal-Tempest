/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edited;

import java.util.*;

import com.jme3.font.*;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Quad;

import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.AbstractGuiComponent;
import com.simsilica.lemur.component.ColoredComponent;
import com.simsilica.lemur.component.TextEntryComponent;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.GuiMaterial;
import com.simsilica.lemur.core.GuiUpdateListener;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.KeyActionListener;
import com.simsilica.lemur.event.KeyListener;
import com.simsilica.lemur.event.KeyModifiers;
import com.simsilica.lemur.event.ModifiedKeyInputEvent;
import com.simsilica.lemur.focus.FocusTarget;
import com.simsilica.lemur.focus.FocusNavigationState;
import com.simsilica.lemur.focus.FocusTraversal.TraversalDirection;
import com.simsilica.lemur.text.DocumentModel;
import com.simsilica.lemur.text.DefaultDocumentModel;

/**
 *
 * @author night
 */
public class EditedTextEntryComponent extends TextEntryComponent
                                implements FocusTarget, ColoredComponent {

    private static final Map<KeyAction,KeyActionListener> standardActions = new HashMap<KeyAction,KeyActionListener>();
    static {
        standardActions.put(new KeyAction(KeyInput.KEY_HOME), LINE_HOME);
        standardActions.put(new KeyAction(KeyInput.KEY_END), LINE_END);
        standardActions.put(new KeyAction(KeyInput.KEY_HOME, KeyModifiers.CONTROL_DOWN), DOC_HOME);
        standardActions.put(new KeyAction(KeyInput.KEY_END, KeyModifiers.CONTROL_DOWN), DOC_END);

        standardActions.put(new KeyAction(KeyInput.KEY_UP), PREV_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_DOWN), NEXT_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_LEFT), LEFT);
        standardActions.put(new KeyAction(KeyInput.KEY_RIGHT), RIGHT);

        standardActions.put(new KeyAction(KeyInput.KEY_BACK), BACKSPACE);
        standardActions.put(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
        standardActions.put(new KeyAction(KeyInput.KEY_DELETE), DELETE);
    }

    private BitmapFont font;
    private BitmapText bitmapText;
    private Rectangle textBox;
    private HAlignment hAlign = HAlignment.Left;
    private VAlignment vAlign = VAlignment.Top;
    private Vector3f preferredSize;
    private float preferredWidth;
    private int preferredLineCount;
    private KeyHandler keyHandler = new KeyHandler();
    private Quad cursorQuad;
    private Geometry cursor;
    private DocumentModel model;
    private boolean singleLine;
    private boolean focused;
    private boolean cursorVisible = true;
    private Float preferredCursorWidth = null;

    private VersionedReference<DocumentModel> modelRef;
    private VersionedReference<Integer> caratRef;
    private GuiUpdateListener updateListener = new ModelChecker();

    // This really only works properly in single-line mode.
    private int textOffset = 0;

    private Map<KeyAction,KeyActionListener> actionMap = new HashMap<KeyAction,KeyActionListener>(standardActions);

    public EditedTextEntryComponent( BitmapFont font ) {
        this( new DefaultDocumentModel(), font );
    }

    public EditedTextEntryComponent( DocumentModel model, BitmapFont font ) {
        super(model, font);
        
        this.font = font;
        this.bitmapText = new BitmapText(font);
        bitmapText.setLineWrapMode(LineWrapMode.Clip);
        // Can't really do this since we don't know what
        // bucket it will actually end up in Gui or regular.
        //bitmapText.setQueueBucket( Bucket.Transparent );
        this.model = model;
        
        // Create a versioned reference for watching for updates, external or otherwise
        this.modelRef = model.createReference();
        this.caratRef = model.createCaratReference();

        cursorQuad = new Quad(getCursorWidth(), bitmapText.getLineHeight());
        cursor = new Geometry( "cursor", cursorQuad );
        GuiMaterial mat = GuiGlobals.getInstance().createMaterial(new ColorRGBA(1,1,1,0.75f), false);
        cursor.setMaterial(mat.getMaterial());
        cursor.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        cursor.setUserData("layer", 1);
        bitmapText.attachChild(cursor);

        if( model.getText() != null ) {
            resetText();
        }
    }

    @Override
    public EditedTextEntryComponent clone() {
        EditedTextEntryComponent result = (EditedTextEntryComponent)super.clone();
        result.bitmapText = new BitmapText(font);
        bitmapText.setLineWrapMode(LineWrapMode.Clip);
        
        result.model = model.clone();
        result.preferredSize = null;
        result.textBox = null;
        result.keyHandler = result.new KeyHandler();
        result.cursorQuad = new Quad(getCursorWidth(), bitmapText.getLineHeight());
        result.cursor = new Geometry("cursor", cursorQuad);
        GuiMaterial mat = GuiGlobals.getInstance().createMaterial(new ColorRGBA(1,1,1,0.75f), false);
        result.cursor.setMaterial(mat.getMaterial());
        result.cursor.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        result.bitmapText.attachChild(cursor);
        result.resetText();

        return result;
    }

    @Override
    public void attach( GuiControl parent ) {
        super.attach(parent);
        parent.addUpdateListener(updateListener);
        getNode().attachChild(bitmapText);
        resetCursorPosition();
        resetCursorState();

        if( focused ) {
            GuiGlobals.getInstance().addKeyListener(keyHandler);
        }
    }

    @Override
    public void detach( GuiControl parent ) {
        GuiGlobals.getInstance().removeKeyListener(keyHandler);

        getNode().detachChild(bitmapText);
        parent.removeUpdateListener(updateListener);
        super.detach(parent);
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public boolean isFocusable() {
        return true; // should return isEnabled() when we have one
    }

    @Override
    public void focusGained() {
        if( this.focused )
            return;
        this.focused = true;
        GuiGlobals.getInstance().addKeyListener(keyHandler);
        resetCursorState();
    }

    @Override
    public void focusLost() {
        if( !this.focused )
            return;
        this.focused = false;
        GuiGlobals.getInstance().removeKeyListener(keyHandler);
        resetCursorState();
    }

    @Override
    public Map<KeyAction,KeyActionListener> getActionMap() {
        return actionMap;
    }
    
    @Override
    public DocumentModel getDocumentModel() {
        return model;
    }
    
    @Override
    public void setSingleLine( boolean f ) {
        this.singleLine = f;
        if( singleLine ) {
            actionMap.put(new KeyAction(KeyInput.KEY_RETURN), FOCUS_NEXT);
            actionMap.put(new KeyAction(KeyInput.KEY_NUMPADENTER), FOCUS_NEXT);
            actionMap.put(new KeyAction(KeyInput.KEY_TAB), FOCUS_NEXT);
            actionMap.put(new KeyAction(KeyInput.KEY_TAB, KeyModifiers.SHIFT_DOWN), FOCUS_PREVIOUS);
            actionMap.put(new KeyAction(KeyInput.KEY_UP), FOCUS_UP);
            actionMap.put(new KeyAction(KeyInput.KEY_DOWN), FOCUS_DOWN);
        } else {
            actionMap.put(new KeyAction(KeyInput.KEY_RETURN), NEW_LINE);
            actionMap.put(new KeyAction(KeyInput.KEY_NUMPADENTER), NEW_LINE);
            
            // We may choose to do something different with tab someday... but 
            // the user can also just remove the action if they like.
            actionMap.put(new KeyAction(KeyInput.KEY_TAB), FOCUS_NEXT);
            actionMap.put(new KeyAction(KeyInput.KEY_TAB, KeyModifiers.SHIFT_DOWN), FOCUS_PREVIOUS);
            
            actionMap.put(new KeyAction(KeyInput.KEY_UP), PREV_LINE);
            actionMap.put(new KeyAction(KeyInput.KEY_DOWN), NEXT_LINE);
        }
    }
    
    @Override
    public boolean isSingleLine() {
        return singleLine;
    }
    
    @Override
    public void setFont( BitmapFont font ) {
        if( font == bitmapText.getFont() )
            return;
    
        if( isAttached() ) {
            bitmapText.removeFromParent();
        }
        
        // Can't change the font once created so we'll
        // have to create it fresh
        BitmapText newText = new BitmapText(font);
        newText.setLineWrapMode(LineWrapMode.Clip);
        newText.setText(getText());
        newText.setColor(getColor());
        newText.setLocalTranslation(bitmapText.getLocalTranslation());
        newText.setSize(getFontSize());
        this.bitmapText = newText;
 
        // The cursor is attached to the bitmap text directly
        // so we need to move it.       
        bitmapText.attachChild(cursor);
        
         // we also need to change the font! as the font parameter is used in getVisibleWidth()
        this.font = font;
                
        resizeCursor();
        resetCursorPosition();
        resetText();
                
        if( isAttached() ) {
            getNode().attachChild(bitmapText);
        }
    }

    @Override
    public BitmapFont getFont() {
        return bitmapText.getFont();
    }

    public BitmapText getTextComponent() { return bitmapText; }
    
    @Override
    public void setFontSize( float f ) {
        this.bitmapText.setSize(f);
        resizeCursor();
        resetCursorPosition();
        resetText();
    }
    
    @Override
    public float getFontSize() {
        return bitmapText.getSize();
    }

    @Override
    protected void resetCursorColor() {
        float alpha = bitmapText.getAlpha();
        if( alpha == -1 ) {
            alpha = 1;
        }
        ColorRGBA color = bitmapText.getColor();
        
        if( alpha == 1 ) {
            cursor.getMaterial().setColor("Color", color);
        } else {
            ColorRGBA cursorColor = color != null ? color.clone() : ColorRGBA.White.clone();
            cursorColor.a = alpha;
            cursor.getMaterial().setColor("Color", cursorColor);
        }
    }

    @Override
    public void setColor( ColorRGBA color ) {
        float alpha = bitmapText.getAlpha();
        bitmapText.setColor(color);
        if( alpha != 1 ) {
            bitmapText.setAlpha(alpha);
        }
        resetCursorColor();
    }

    @Override
    public ColorRGBA getColor() {
        return bitmapText.getColor();
    }

    @Override
    public void setAlpha( float f ) {
        bitmapText.setAlpha(f);
        resetCursorColor();
    }
    
    @Override
    public float getAlpha() {
        return bitmapText.getAlpha();
    }

    @Override
    protected void resetText() {
        try {
            String text = model.getText();
            if( textOffset != 0 ) {
                textOffset = Math.min(textOffset, text.length());
                text = text.substring(textOffset);

                if( textBox != null ) {
                    // See if this offset even makes sense now
                    float x = getVisibleWidth(text);
                    if( x < textBox.width ) {
                        while( textOffset > 0 ) {
                            textOffset--;
                            text = model.getText().substring(textOffset);
                            x = getVisibleWidth(text);
                            if( x > textBox.width ) {
                                textOffset++;
                                text = model.getText().substring(textOffset);
                                break;
                            }
                        }

                    }
                }
            }

            if( text != null && text.equals(bitmapText.getText()) )
                return;

            bitmapText.setText(text);
            resetCursorPosition();
            invalidate();
        }
        catch (Exception e) {}
    }

    @Override
    protected float getVisibleWidth( String text ) {
        float x = font.getLineWidth(text + " ");
        x -= font.getLineWidth(" ");
        //x += 1;
        float scale = bitmapText.getSize() / font.getPreferredSize();
        x *= scale;
        return x;
    }
    
    @Override
    public void setPreferredCursorWidth( Float f ) {
        this.preferredCursorWidth = f;
        resizeCursor();
        resetCursorPosition();
    }
    
    @Override
    public Float getPreferredCursorWidth() {
        return preferredCursorWidth;
    }  
    
    @Override
    public float getCursorWidth() {
        if( preferredCursorWidth != null ) {
            return preferredCursorWidth;
        }
        // Because small cursor widths sometimes make the cursor invisible
        // for some reason, we'll try to detect the cases where pixels = units
        // and make sure that the width is never smaller than 1 pixel.
        try {
            float height = bitmapText.getLineHeight();
            if( height > 5 ) {
                return Math.max(1, height/16f);
            } 
            return height/16f;
        }
        catch (NullPointerException e) { return 0; }
    }
    
    @Override
    protected void resizeCursor() {
        cursorQuad.updateGeometry(getCursorWidth(), bitmapText.getLineHeight());
        cursorQuad.clearCollisionData(); 
    }
    
    @Override
    protected void resetCursorState() {
        if( isAttached() && focused && cursorVisible ) {
            cursor.setCullHint(CullHint.Inherit);
        } else {
            cursor.setCullHint(CullHint.Always);
        }
    }
    
    @Override
    protected void resetCursorPosition() {
        // Find the current cursor position.
        int line = model.getCaratLine();
        int column = model.getCaratColumn();

        if( column < textOffset ) {
            textOffset = column;
            resetText();
        }

        String row = model.getLine(line);
        row = row.substring(textOffset,column);

        // We add an extra space to properly advance (since often
        // the space character only has a width of 1 but will advance
        // far) then we subtract that space width back.
        float x = font.getLineWidth(row + " ");
        x -= font.getLineWidth(" ");

        // And pad it out just a bit...
        //x += 1;

        float scale = bitmapText.getSize() / font.getPreferredSize();
        x *= scale;

        float y = -line * bitmapText.getLineHeight();
        y -= bitmapText.getLineHeight();

        if( textBox != null && x > textBox.width ) {
            if( singleLine ) {
                // Then we can move the text offset and try again
                textOffset++;
                resetText();
                resetCursorPosition();
                return;
            } else {
                // Make it invisible
                cursorVisible = false;
                resetCursorState();
            }
        } else {
            cursorVisible = true;
            resetCursorState();
        }

        cursor.setLocalTranslation(x - getCursorWidth() * 0.5f, y, 0.01f);
    }
    
    @Override
    public void setText( String text ) {
        if( text != null && text.equals(model.getText()) )
            return;
        
        
        model.setText(text);
        //resetText();  ...should be automatic now
    }
    
    @Override
    public String getText() {
        return model.getText();
    }
    
    @Override
    public void setHAlignment( HAlignment a ) {
        if( hAlign == a )
            return;
        hAlign = a;
        resetAlignment();
    }
    
    @Override
    public HAlignment getHAlignment() {
        return hAlign;
    }
    
    @Override
    public void setVAlignment( VAlignment a ) {
        if( vAlign == a )
            return;
        vAlign = a;
        resetAlignment();
    }

    @Override
    public VAlignment getVAlignment() {
        return vAlign;
    }

    @Override
    public void setPreferredSize( Vector3f v ) {
        this.preferredSize = v;
        invalidate();
    }

    @Override
    public Vector3f getPreferredSize() {
        return preferredSize;
    }

    @Override
    public void setPreferredWidth( float f ) {
        this.preferredWidth = f;
        invalidate();
    }

    @Override
    public float getPreferredWidth() {
        return preferredWidth;
    }

    @Override
    public void setPreferredLineCount( int i ) {
        this.preferredLineCount = i;
        invalidate();
    }

    @Override
    public float getPreferredLineCount() {
        return preferredLineCount;
    }

    @Override
    public void reshape(Vector3f pos, Vector3f size) {
        bitmapText.setLocalTranslation(pos.x, pos.y, pos.z);
        textBox = new Rectangle(0, 0, size.x, size.y);
        bitmapText.setBox(textBox);
        resetAlignment();
    }

    @Override
    public void calculatePreferredSize( Vector3f size ) {
        if( preferredSize != null ) {
            size.set(preferredSize);
            return;
        }

        // Make sure that the bitmapText reports a reliable
        // preferred size
        bitmapText.setBox(null);

        if( preferredWidth == 0 ) {
            size.x = bitmapText.getLineWidth();
        } else {
            size.x = preferredWidth;
        }
        if( preferredLineCount == 0 ) {
            size.y = bitmapText.getHeight();
        } else {
            size.y = bitmapText.getLineHeight() * preferredLineCount;
        }

        // Reset any text box we already had
        bitmapText.setBox(textBox);
    }

    @Override
    protected void resetAlignment() {
        if( textBox == null )
            return;

        switch( hAlign ) {
            case Left:
                bitmapText.setAlignment(Align.Left);
                break;
            case Right:
                bitmapText.setAlignment(Align.Right);
                break;
            case Center:
                bitmapText.setAlignment(Align.Center);
                break;
        }
        switch( vAlign ) {
            case Top:
                bitmapText.setVerticalAlignment(VAlign.Top);
                break;
            case Bottom:
                bitmapText.setVerticalAlignment(VAlign.Bottom);
                break;
            case Center:
                bitmapText.setVerticalAlignment(VAlign.Center);
                break;
        }
    }

    private static class DocumentHome implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.home(false);
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class LineHome implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.home(true);
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class DocumentEnd implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.end(false);
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class LineEnd implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.end(true);
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class PreviousLine implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.up();
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class NextLine implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.down();
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class CaratLeft implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.left();
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class CaratRight implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.right();
            //source.resetCursorPosition(); should be automatic now
        }
    }

    private static class NullAction implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
        }
    }

    private static class Backspace implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.backspace();
            //source.resetText(); // should be automic now
        }
    }

    private static class NewLine implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.insertNewLine();
            //source.resetText(); // should be automic now
        }
    }

    private static class Delete implements KeyActionListener {
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            ((EditedTextEntryComponent)source).model.delete();
            //source.resetText(); // should be automic now
        }
    }
    
    private static class FocusChange implements KeyActionListener {
        private TraversalDirection dir;
        
        public FocusChange( TraversalDirection dir ) {
            this.dir = dir;
        }
    
        @Override
        public void keyAction( TextEntryComponent source, KeyAction key ) {
            FocusNavigationState nav = GuiGlobals.getInstance().getFocusNavigationState();
            if( nav == null ) {
                return;
            }
            Spatial current = GuiGlobals.getInstance().getCurrentFocus();
            nav.requestChangeFocus(current, dir);    
        } 
    }


    private class KeyHandler implements KeyListener {
        private boolean shift = false;
        private boolean control = false;

        @Override
        public void onKeyEvent( KeyInputEvent evt ) {
            ModifiedKeyInputEvent mEvt = (ModifiedKeyInputEvent)evt;
            if( mEvt.isPressed() || mEvt.isRepeating() ) {
                KeyAction key = mEvt.toKeyAction(); //new KeyAction(code, (control?KeyAction.CONTROL_DOWN:0), (shift?KeyAction.SHIFT_DOWN:0) );
                KeyActionListener handler = actionMap.get(key);
                if( handler != null ) {
                    handler.keyAction(EditedTextEntryComponent.this, key);
                    evt.setConsumed();
                    return;
                }

                // Making sure that no matter what, certain
                // characters never make it directly to the
                // document
                if( evt.getKeyChar() >= 32 ) {
                    model.insert(evt.getKeyChar());
                    evt.setConsumed();
                    //resetText(); ...should be automatic now
                }
            }
        }
    }
 
    /**
     *  Checks for changes in the model and updates the text display
     *  or cursor position as necessary.
     */   
    private class ModelChecker implements GuiUpdateListener {
    
        @Override       
        public void guiUpdate( GuiControl source, float tpf ) {
            if( modelRef.update() ) {
                resetText();
            }
            if( caratRef.update() ) {
                resetCursorPosition();   
            }
        }
    }
    
}
