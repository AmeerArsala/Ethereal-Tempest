/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.text;

import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.TrueTypeMesh;
import com.atr.jme.font.asset.TrueTypeKeyBMP;
import com.atr.jme.font.asset.TrueTypeKeyMesh;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author night
 */
public class FontProperties {
    public enum KeyType {
        BMP,
        MESH;
    }
    
    public static final HashMap<FontProperties, TrueTypeFont> TTF_DICTIONARY = new HashMap<>(); 
    public static final float DEFAULT_UNCHANGED_QUALITY = 1;
    
    private final String fontPath;
    private final KeyType keyType;
    private final Style fontStyle;
    private final float fontSize;
    
    private final float fontQualityMultiplier;
    private final int literalFontSize; //font size that factors in quality; this field will be the one that the text sets as "font size"
    
    public FontProperties(String fontPath, KeyType keyType, Style fontStyle, float fontSize) { //automatically does quality
        this.fontPath = fontPath;
        this.keyType = keyType;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        
        //rule of thumb in action
        if (fontSize < 32) {
            fontQualityMultiplier = (1 / 0.73f);
        } else if (fontSize < 53) {
            fontQualityMultiplier = (1 / 0.84f);
        } else {
            fontQualityMultiplier = 1f;
        }

        literalFontSize = (int)Math.floor(fontSize * fontQualityMultiplier);
    }

    public FontProperties(String fontPath, KeyType keyType, Style fontStyle, float fontSize, double fontSizeQuality) { // fontSizeQuality is what you actually want the font to look like
        this.fontPath = fontPath;
        this.keyType = keyType;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        
        fontQualityMultiplier = 1;
        
        literalFontSize = (int)(fontSizeQuality);
    }
    
    public FontProperties(String fontPath, KeyType keyType, Style fontStyle, float fontSize, float fontQualityMultiplier) {
        this.fontPath = fontPath;
        this.keyType = keyType;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        this.fontQualityMultiplier = fontQualityMultiplier;
        
        literalFontSize = (int)(fontSize * fontQualityMultiplier);
    }
    
    public String getFontPath() { return fontPath; }
    public KeyType getKeyType() { return keyType; }
    public Style getStyle() { return fontStyle; }
    public float getFontSize() { return fontSize; }
    
    public float getQuality() { return fontQualityMultiplier; }
    public int getLiteralFontSize() { return literalFontSize; }
    
    public TrueTypeFont retrieveFont(AssetManager assetManager) {
        //checks if there is a match in the TTF_DICTIONARY for fonts; if so, returns it
        TrueTypeFont match = TTF_DICTIONARY.get(this);
        if (match != null) {
            return match;
        }
        
        //create and add new font to dictionary, then return it
        TrueTypeFont font = createFont(assetManager);
        TTF_DICTIONARY.put(this, font);
        
        return font;
    }
    
    private TrueTypeFont createFont(AssetManager assetManager) {
        TrueTypeFont ttf;
        
        if (keyType == KeyType.BMP) {
            TrueTypeKeyBMP ttkBMP = new TrueTypeKeyBMP(fontPath, fontStyle, literalFontSize);
            ttf = (TrueTypeBMP)assetManager.loadAsset(ttkBMP);
        } else { // keyType == KeyType.MESH
            TrueTypeKeyMesh ttkMesh = new TrueTypeKeyMesh(fontPath, fontStyle, literalFontSize);
            ttf = (TrueTypeMesh)assetManager.loadAsset(ttkMesh);
        }
       
        ttf.setScale(fontSize / literalFontSize);
        initializeTTF(ttf);
        return ttf;
    }
    
    private void initializeTTF(TrueTypeFont ttf) {
        //Request all the characters we want to be able to use.
        try {
            ttf.getGlyphs(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789!@#$%^&*()-_+=*/" +
                "\\:;\"<>,.?{}[]|`~'" + SpecialCharacter.allCharacters()
            );
        }
        catch (Exception e) {
            ttf.getGlyphs(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789!@#$%^&*()-_+=*/" +
                "\\:;\"<>,.?{}[]|`~'"
            );
        }
        
        ttf.lock(true);
    }
    
    //these are for if you just want the same font but change a few things
    public FontProperties modifyFontPath(String fontPath) {
        return new FontProperties(fontPath, keyType, fontStyle, fontSize, fontQualityMultiplier);
    }
    
    public FontProperties modifyKeyType(KeyType keyType) {
        return new FontProperties(fontPath, keyType, fontStyle, fontSize, fontQualityMultiplier);
    }
    
    public FontProperties modifyFontStyle(Style fontStyle) {
        return new FontProperties(fontPath, keyType, fontStyle, fontSize, fontQualityMultiplier);
    }
    
    public FontProperties modifyFontSize(int fontSize) {
        return new FontProperties(fontPath, keyType, fontStyle, fontSize, fontQualityMultiplier);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FontProperties)) {
            return false;
        }
        
        FontProperties co = (FontProperties)o;
        return 
            super.equals(o) || 
            (
                fontPath.equals(co.fontPath) && 
                keyType == co.keyType && 
                fontStyle == co.fontStyle && 
                literalFontSize == co.literalFontSize
            );
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.fontPath);
        hash = 97 * hash + Objects.hashCode(this.keyType);
        hash = 97 * hash + Objects.hashCode(this.fontStyle);
        hash = 97 * hash + Float.floatToIntBits(this.fontSize);
        hash = 97 * hash + Float.floatToIntBits(this.fontQualityMultiplier);
        hash = 97 * hash + this.literalFontSize;
        return hash;
    }
}
