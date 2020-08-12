/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.shape.TrueTypeNode;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.simsilica.lemur.Label;
import general.Submenu;

/**
 *
 * @author night
 */
public class DamageNumber extends Node {
    enum VisibilityState {
        Visible,
        Invisible
    }
    
    private Node parent;
    private float opacity = 0f;
    private VisibilityState visibility = VisibilityState.Invisible;
    private Submenu.TransitionState tState = Submenu.TransitionState.Standby;
    
    private ColorRGBA color = ColorRGBA.Red;
    
    private final TrueTypeFont ttf;
    
    private String name;
    private TrueTypeNode child;
    
    public DamageNumber(String name, Node parent, TrueTypeFont ttf) {
        super(name);
        this.name = name;
        this.parent = parent;
        this.ttf = ttf;
        
        child = ttf.getText(name, 3, color);
        
        move(0, -400, 0);
        attachChild(child);
        
        parent.attachChild(this);
    }
    
    public void update(float tpf) {
        updateText();
        //child = ttf.getText(name, 3, new ColorRGBA(1, 0, 0, opacity));
        
        if (tState == Submenu.TransitionState.TransitioningIn) {
            opacity += 0.1f;
            move(0, 0.5f, 0);
            if (opacity == 1f) {
                tState = Submenu.TransitionState.Standby;
                visibility = VisibilityState.Visible;
            }
        } else if (tState == Submenu.TransitionState.TransitioningOut) {
            opacity -= 0.1f;
            move(0, -0.5f, 0);
            if (opacity == 0f) {
                tState = Submenu.TransitionState.Standby;
                visibility = VisibilityState.Invisible;
                setLocalTranslation(0, 0, 0);
                parent.detachChild(this);
            }
        }
    }
    
    public VisibilityState getVisibility() {
        return visibility;
    }
    
    public void setVisibility(VisibilityState vis) {
        visibility = vis;
    }
    
    public ColorRGBA getColor() {
        return color;
    }
    
    public void setColor(ColorRGBA C) {
        color = C;
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public void setOpacity(float opa) {
        opacity = opa;
    }
    
    public Submenu.TransitionState getTransitionState() {
        return tState;
    }
    
    public void setTransitionState(Submenu.TransitionState trans) {
        tState = trans;
        
        if (tState == Submenu.TransitionState.TransitioningIn) {
            parent.attachChild(this);
        }
    }
    
    public void setText(String text) {
        name = text;
        updateText();
    }
    
    private void updateText() {
        detachChild(child);
        child = ttf.getText(name, 3, new ColorRGBA(color.r, color.g, color.b, opacity));
        attachChild(child);
    }
    
}