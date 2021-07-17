/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.text.quickparams;

import com.atr.jme.font.util.StringContainer.Align;
import com.atr.jme.font.util.StringContainer.VAlign;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.jme3.font.Rectangle;
import general.ui.text.TextProperties;

/**
 *
 * @author night
 */
public class TextDisplacementParams {
    public final Align hAlign;
    public final VAlign vAlign;
    public final WrapMode wrapMode;
        
    public TextDisplacementParams(Align hAlign, VAlign vAlign, WrapMode wrapMode) {
        this.hAlign = hAlign;
        this.vAlign = vAlign;
        this.wrapMode = wrapMode;
    }
    
    public TextProperties createTextProperties(int kerning, Rectangle rectangle) { 
        return TextProperties.builder()
                .horizontalAlignment(hAlign)
                .verticalAlignment(vAlign)
                .kerning(kerning)
                .wrapMode(wrapMode)
                .textBox(rectangle)
                .build();
    }
    
    //does not use Rectangle
    public TextProperties createTextProperties(int kerning) {
        return TextProperties.builder()
                .horizontalAlignment(hAlign)
                .verticalAlignment(vAlign)
                .kerning(kerning)
                .wrapMode(wrapMode)
                .build();
    } 
}
