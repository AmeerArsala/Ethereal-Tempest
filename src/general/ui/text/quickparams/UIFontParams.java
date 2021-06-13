/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.text.quickparams;

import com.atr.jme.font.util.Style;
import general.ui.text.FontProperties;
import general.ui.text.FontProperties.KeyType;

/**
 *
 * @author night
 */
public class UIFontParams {
    public final String fontPath;
    public final float fontSize;
    public final Style style;
    public final int kerning;
        
    public UIFontParams(String fontPath, float fontSize, Style style, int kerning) {
        this.fontPath = fontPath;
        this.fontSize = fontSize;
        this.style = style;
        this.kerning = kerning;
    }
        
    public FontProperties createFontProperties(KeyType keyType) { //FontProperties.KeyType
        return new FontProperties(fontPath, keyType, style, fontSize);
    }
}
