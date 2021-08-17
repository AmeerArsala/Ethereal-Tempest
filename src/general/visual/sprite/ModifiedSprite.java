/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual.sprite;

import battle.animation.config.action.FlashColor;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import java.util.function.Consumer;

/**
 *
 * @author night
 */
public class ModifiedSprite extends Sprite {
    private Sprite overlay;
    
    public ModifiedSprite(Vector2f dimensions, AssetManager assetManager) {
        super(dimensions, assetManager);
    }

    public ModifiedSprite(float width, float height, AssetManager assetManager) {
        super(width, height, assetManager);
    }
    
    public Sprite getOverlay() { return overlay; }
    public boolean hasOverlay() { return overlay != null; }
    
    public void attemptAttachOverlay() {
        if (hasOverlay()) {
            attachChild(overlay);
        }
    }
    
    public void attachOverlay() {
        attachChild(overlay);
    }
    
    public void setOverlay(Sprite overlaySprite) {
        if (hasOverlay()) {
            detachChild(overlay);
        }
        
        overlay = overlaySprite;
    }
    
    public void setColor(String colorMatParam, ColorRGBA color) {
        getMaterial().setColor(colorMatParam, color);
        
        if (hasOverlay()) {
            overlay.getMaterial().setColor(colorMatParam, color);
        }
    }
    
    @Override
    public void setXFacing(int facing) {
        super.setXFacing(facing);
        
        if (hasOverlay()) {
            overlay.setXFacing(facing);
        }
    }
    
    @Override
    public void setSizeX(float x) {
        super.setSizeX(x);
        
        if (hasOverlay()) {
            overlay.setSizeX(x);
        }
    }
    
    @Override
    public void setSizeY(float y) {
        super.setSizeY(y);
        
        if (hasOverlay()) {
            overlay.setSizeY(y);
        }
    }
    
    @Override
    public void setSpritesheetPosition(float pos) {
        super.setSpritesheetPosition(pos);
        
        if (hasOverlay()) {
            overlay.setSpritesheetPosition(pos);
        }
    }
    
    @Override
    public void cleanAndStartFlashingColor(FlashColor flashColor) {
        super.cleanAndStartFlashingColor(flashColor);
        
        if (hasOverlay()) {
            overlay.cleanAndStartFlashingColor(flashColor);
        }
    }
    
    @Override
    public void startFlashingColorIfAllowed(FlashColor flashColor) {
        super.startFlashingColorIfAllowed(flashColor);
        
        if (hasOverlay()) {
            overlay.startFlashingColorIfAllowed(flashColor);
        }
    }
    
    @Override
    public void startFlashingColor(FlashColor flashColor) {
        super.startFlashingColor(flashColor);
        
        if (hasOverlay()) {
            overlay.startFlashingColor(flashColor);
        }
    }
    
    //stops flashing colors (if happening)
    @Override
    public void stopFlashing() {
        super.stopFlashing();
        
        if (hasOverlay()) {
            overlay.stopFlashing();
        }
    }
    
    public void commandMaterial(Consumer<Material> matConsumer) {
        matConsumer.accept(getMaterial());
        
        if (hasOverlay()) {
            matConsumer.accept(overlay.getMaterial());
        }
    }
}
