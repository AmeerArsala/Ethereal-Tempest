/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edited;

import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.util.StringContainer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.StyleDefaults;
import com.simsilica.lemur.style.Styles;

/**
 *
 * @author night
 */
public class CustomProgressBar extends Panel {
    public static final String ELEMENT_ID = "progress";
    public static final String CONTAINER_ID = "container";
    public static final String LABEL_ID = "editedlabel";
    public static final String VALUE_ID = "value";
    
    private BorderLayout layout;
    protected EditedLabel label;
    protected Panel value;
    
    private static QuadBackgroundComponent whitesquare;
    private QuadBackgroundComponent bg;
    
    protected RangedValueModel model;
    protected VersionedReference<Double> state;
    
    TrueTypeFont trtyfo;
    ElementId elm = new ElementId(ELEMENT_ID);
    
    public CustomProgressBar(TrueTypeFont ttf, ColorRGBA textColor) {
        super(false, new ElementId(ELEMENT_ID).child(CONTAINER_ID), null);
        model = new DefaultRangedValueModel();
        trtyfo = ttf;
        
        // Because the ProgressBar accesses styles (for its children) before
        // it has applied its own, it is possible that its default styles
        // will not have been applied.  So we'll make sure.
        Styles styles = GuiGlobals.getInstance().getStyles();
        styles.initializeStyles(getClass());
        
        // Having a label as a child is both nice for the caller as
        // well as convenient for us.  It means we have an easy component
        // to use to get the 'inner size' minus any background margins
        // or insets.
        this.layout = new BorderLayout();
        getControl(GuiControl.class).setLayout(layout);
        
        // Add the label child.
        /*label = new EditedLabel("HP: ", ttf, textColor);
        layout.addChild(label);*/
        label = new EditedLabel("HP: ", ttf, textColor);
        label.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        
        
        value = new Panel(elm.child(VALUE_ID), null);
        attachChild(value);
        layout.addChild(label);
        //attachChild(label.text.getTTFNode());
        
        bg = whitesquare.clone();
        
        QuadBackgroundComponent qbc = whitesquare.clone();
        qbc.setColor(new ColorRGBA(0.22f, 0.22f, 0.22f, 1f));
        setBorder(qbc);
    }
    
    public CustomProgressBar(TrueTypeFont ttf) {
        super(false, new ElementId(ELEMENT_ID).child(CONTAINER_ID), null);
        model = new DefaultRangedValueModel();
        trtyfo = ttf;
        
        // Because the ProgressBar accesses styles (for its children) before
        // it has applied its own, it is possible that its default styles
        // will not have been applied.  So we'll make sure.
        Styles styles = GuiGlobals.getInstance().getStyles();
        styles.initializeStyles(getClass());
        
        // Having a label as a child is both nice for the caller as
        // well as convenient for us.  It means we have an easy component
        // to use to get the 'inner size' minus any background margins
        // or insets.
        this.layout = new BorderLayout();
        getControl(GuiControl.class).setLayout(layout);
        
        // Add the label child.
        /*label = new EditedLabel("HP: ", ttf, textColor);
        layout.addChild(label);*/
        label = new EditedLabel("HP: ", ttf);
        label.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        
        
        value = new Panel(elm.child(VALUE_ID), null);
        attachChild(value);
        layout.addChild(label);
        //attachChild(label.text.getTTFNode());
        
        bg = whitesquare.clone();
        
        QuadBackgroundComponent qbc = whitesquare.clone();
        qbc.setColor(new ColorRGBA(0.22f, 0.22f, 0.22f, 1f));
        setBorder(qbc);
    }
    
    public static void setWhiteSquare(Texture Tx) {
        whitesquare = new QuadBackgroundComponent(Tx);
    }
    
    public static QuadBackgroundComponent getWhiteSquare() { return whitesquare; }
    
    public static QuadBackgroundComponent getEmptySquare() {
        QuadBackgroundComponent qbc = whitesquare.clone();
        qbc.setColor(new ColorRGBA(0.22f, 0.22f, 0.22f, 1f));
        return qbc;
    }
    
    public EditedLabel getLabel() { return label; }
    
    public void setMessage(String msg) {
        //label.text.getTTFNode().setText(msg);
        layout.removeChild(label);
        label = new EditedLabel(msg, trtyfo);
        label.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        label.text.getTTFNode().move(10f, 0f, 0f);
        layout.addChild(label);
    }
    
    public void setMessage(String msg, ColorRGBA textColor) {
        //label.text.getTTFNode().setText(msg);
        layout.removeChild(label);
        label = new EditedLabel(msg, trtyfo, textColor);
        label.text.getTTFNode().setHorizontalAlignment(StringContainer.Align.Center);
        label.text.getTTFNode().move(10f, 0f, 0f);
        layout.addChild(label);
    }
    
    public String getMessage() { return label.text.getTTFNode().getText(); }
    
    /**
     *  Sets the current progress value as a percentage (0-1.0) of
     *  the current range.
     */
    public void setProgressPercent( double percent ) {
        this.model.setPercent(percent);
    }
    
    /**
     *  Returns the current progress value as a percentage (0-1.0) of
     *  the current range.
     */
    public double getProgressPercent() {
        return this.model.getPercent();
    }

    /**
     *  Sets the raw progress value.
     */
    public void setProgressValue( double val ) {
        this.model.setValue(val);
    }
    
    /**
     *  Returns the raw progress value.
     */
    public double getProgressValue() {
        return this.model.getValue();
    }
 
    /**
     *  Sets the ranged value model that will be used to 
     *  calculate progress percentage.  The default model is
     *  is a DefaultRangedValueModel() where the range is 0 to 100.
     *  If setModel(null) is called then a new default range is
     *  created. 
     */   
    public void setModel( RangedValueModel model ) {
        if( this.model == model ) {
            return;
        }
        if( model == null ) {
            model = new DefaultRangedValueModel();
        }
        this.model = model;
        this.state = null;
    }
 
    /**
     *  Returns the current range model for this progress bar.
     */   
    public RangedValueModel getModel() {
        return model;
    }
    
    /**
     *  Returns the GUI element that is used for the value indicator.
     *  This can be used to apply special styling.
     */   
    public Panel getValueIndicator() {
        return value;
    }
    
    public void setBarColor(ColorRGBA rgba) {
        bg.setColor(rgba);
        value.setBackground(bg);
    }
    
    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

        if( state == null || state.update() ) {
            resetStateView();
        }
    }
            
    protected void resetStateView() {
        if( state == null ) {
            state = model.createReference();
        }

        Vector3f labelSize = label.getSize();
        Vector3f labelPos = label.getLocalTranslation();
        double width = model.getPercent() * labelSize.x;
        value.setSize(new Vector3f((float)width, labelSize.y, labelSize.z));
        
        // The way we order these layers is both fragile and inflexible.
        value.setLocalTranslation(labelPos.x, labelPos.y, labelPos.z * 0.5f);
    }
    
    @StyleDefaults(ELEMENT_ID)
    public static void initializeDefaultStyles( Styles styles, Attributes attrs ) {
        GuiGlobals globals = GuiGlobals.getInstance();
        ElementId parent = new ElementId(ELEMENT_ID);        
        styles.getSelector(parent.child(CONTAINER_ID), null).set("background", 
                                                new QuadBackgroundComponent(globals.srgbaColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 0.5f)), 2, 2)); 
        styles.getSelector(parent.child(VALUE_ID), null).set("background", 
                                                new QuadBackgroundComponent(globals.srgbaColor(new ColorRGBA(0.1f, 0.7f, 0.3f, 1)))); 
        //styles.getSelector(parent.child(LABEL_ID), null).set("textHAlignment", HAlignment.Center, false);
        
    }
    
}
