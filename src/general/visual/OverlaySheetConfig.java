/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author night
 */
public class OverlaySheetConfig {
    private String overlaySpritesheetFileName;
    private boolean discardRegularSpritesheetTexture; //whether you want the background/original base of the overlay to be shown or not
    private float spriteWidthToHeightRatio; //the ratio of ONE sprite's width to height (width / height)
    private Scalar scalar;
    
    public OverlaySheetConfig(String overlaySpritesheetFileName, boolean discardRegularSpritesheetTexture, float spriteWidthToHeightRatio, Scalar scalar) {
        this.overlaySpritesheetFileName = overlaySpritesheetFileName;
        this.discardRegularSpritesheetTexture = discardRegularSpritesheetTexture;
        this.spriteWidthToHeightRatio = spriteWidthToHeightRatio;
        this.scalar = scalar;
    }
    
    public static class Scalar {
        private float factor;
        private boolean fromContainer; // if true, treats it as a scalar based on the height of a container, but if false, treats it as a raw scale factor
        
        public Scalar(float factor, boolean fromContainer) {
            this.factor = factor;
            this.fromContainer = fromContainer;
        }
        
        public float getFactor() { return factor; }
        public boolean isFromContainer() { return fromContainer; }
    }
    
    public String getOverlaySpritesheetFileName() { return overlaySpritesheetFileName; }
    public float getSpriteWidthToHeightRatio() { return spriteWidthToHeightRatio; }
    public boolean shouldDiscardRegularSpritesheetTexture() { return discardRegularSpritesheetTexture; }
    public Scalar getScalar() { return scalar; }
    
    public static OverlaySheetConfig deserialize(String jsonPath) { //example would be: "Battle\\Freeblade\\offense\\sword\\character_overlay\\no_overlay.json"
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get("assets\\Sprites\\" + jsonPath));
            return gson.fromJson(reader, OverlaySheetConfig.class);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
