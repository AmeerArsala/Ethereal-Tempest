/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.text;

import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.jme3.font.Rectangle;
import com.simsilica.lemur.Command;

/**
 *
 * @author night
 */
public class TextProperties {
    public static class Builder {
        private int kerning = 3;
        private WrapMode wrapMode = WrapMode.Clip;
        private Align horizontalAlignment = Align.Left;
        private VAlign verticalAlignment = VAlign.Center;
        private Rectangle textBox = null;
        private boolean usesTextBox = false;
        
        public Builder kerning(int kerning) {
            this.kerning = kerning;
            return this;
        }
        
        public Builder wrapMode(WrapMode wrapMode) {
            this.wrapMode = wrapMode;
            return this;
        }
        
        public Builder horizontalAlignment(Align horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
            return this;
        }
        
        public Builder verticalAlignment(VAlign verticalAlignment) {
            this.verticalAlignment = verticalAlignment;
            return this;
        }
        
        public Builder textBox(Rectangle textBox) {
            this.textBox = textBox;
            usesTextBox = true;
            return this;
        }
        
        public TextProperties build() {
            TextProperties properties = new TextProperties(textBox, kerning, wrapMode, horizontalAlignment, verticalAlignment);
            properties.usesTextBox = usesTextBox;
            return properties;
        }
    }
    
    private final Rectangle textBox; // new Rectangle(x, y, width, height)
    
    private int kerning = 3;
    private WrapMode wrapMode = WrapMode.Clip;
    private Align horizontalAlignment = Align.Left;
    private VAlign verticalAlignment = VAlign.Center;
    private boolean usesTextBox = true;
    
    public TextProperties(Rectangle textBox) {
        this.textBox = textBox;
    }
    
    public TextProperties(float textBoxWidth, float textBoxHeight) {
        textBox = new Rectangle(0f, 0f, textBoxWidth, textBoxHeight);
    }
    
    public TextProperties(Rectangle textBox, int kerning, WrapMode wrapMode, Align horizontalAlignment, VAlign verticalAlignment) {
        this.textBox = textBox;
        this.kerning = kerning;
        this.wrapMode = wrapMode;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    //this is for modifying like 1 or 2 fields, and it is done by calling textProperties.modify().kerning(4).build();
    public Builder modify() {
        return new Builder()
            .kerning(kerning)
            .wrapMode(wrapMode)
            .horizontalAlignment(horizontalAlignment)
            .verticalAlignment(verticalAlignment)
            .textBox(textBox);
    }
    
    public Rectangle getTextBox() { return textBox; }
    public int getKerning() { return kerning; }
    public WrapMode getWrapMode() { return wrapMode; }
    public Align getHorizontalAlignment() { return horizontalAlignment; }
    public VAlign getVerticalAlignment() { return verticalAlignment; }
    public boolean usesTextBox() { return usesTextBox; }
    //public String getElipsis() { return elipsis; }
    //public int getOffset() { return offset; } 
    //public int getMaxLines() { return maxLines; }
    //public String getText() { return text; }    //public String getElipsis() { return elipsis; }
    //public int getOffset() { return offset; } 
    //public int getMaxLines() { return maxLines; }
    //public String getText() { return text; }

    void setUsesTextBox(boolean usesTextBox) {
        this.usesTextBox = usesTextBox;
    }
    
    public Command<StringContainer> createStyleCalls() {
        return (sc) -> {
            sc.setTextBox(textBox);
            sc.setKerning(kerning);
            sc.setWrapMode(wrapMode);
            sc.setAlignment(horizontalAlignment);
            sc.setVerticalAlignment(verticalAlignment);
            sc.setUseTextBox(usesTextBox);
        };
    }
}
