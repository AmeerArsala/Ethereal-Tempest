/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.ui.menu;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.lemur.event.MouseEventControl;
import enginetools.TexturedMaterialCreator;
import general.math.function.ParametricFunction;
import general.visual.animation.VisualTransition;
import maps.data.MapTextures;

/**
 *
 * @author night
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 */
public abstract class RadialMenuOption<DATA> extends MenuOption<DATA> {
    public static final ColorRGBA HoveredOrange = new ColorRGBA(1f, 119f / 255f, 0f, 1f);
    
    //public static final String DEFAULT_HOVERED = "Interface/GUI/action_menu/hoveredbg.png";
    //public static final String DEFAULT_NOT_HOVERED = "Interface/GUI/action_menu/nothoveredbg.png";
    
    static final float SIZE = 50f;
    static final float icoSizeDim = SIZE *  200f / 256f;
    
    private static final Vector3f iconTranslation = new Vector3f(0.5f * (SIZE - icoSizeDim), 0.5f * (SIZE - icoSizeDim), 3f);
    private static final float MIN_PADDING = 30f;

    
    private final Texture iconTex;
    
    private final Quad panelBG = new Quad(SIZE, SIZE), icon = new Quad(icoSizeDim, icoSizeDim);
    private final Geometry panelGeom = new Geometry("panel", panelBG), iconGeom = new Geometry("icon", icon);
    private Texture panelHoveredTex, panelUnhoveredTex;
    
    private Vector3f startPos;
    private float angle;
    private float radius;
    
    private ParametricFunction idleMovement;
    private boolean idleOnHoveredOnly;
    
    private final VisualTransition transitionEvent = new VisualTransition(optionNode);
    
    public RadialMenuOption(String optionName, Texture iconTex) {
        super(optionName);
        this.iconTex = iconTex;
        
        iconGeom.setLocalTranslation(iconTranslation); //fitting the geometry on top of the background
        
        optionNode.attachChild(panelGeom);
        optionNode.attachChild(iconGeom);
        
        //calibrate listener
        MouseEventControl.addListenersToSpatial(panelGeom, createMouseListener());
        
        hovered = true; //only here at the start
    }
    
    public float getAngle() { return angle; }
    public VisualTransition getTransitionEvent() { return transitionEvent; }
    
    public final void initializeAssets(AssetManager assetManager, ParametricFunction idleGraph, boolean idleOnlyOnHovered, float startingAngle, int menuListSize) {
        initializeAssets(assetManager, new CustomMenuAssetParams(iconTex), idleGraph, idleOnlyOnHovered, startingAngle, menuListSize);
    }
    
    //CustomMenuAssetParams is only accessible within the package, making this method follow that convention
    final void initializeAssets(AssetManager assetManager, CustomMenuAssetParams params, ParametricFunction idleGraph, boolean idleOnlyOnHovered, float startingAngle, int menuListSize) {
        idleMovement = idleGraph;
        idleOnHoveredOnly = idleOnlyOnHovered;
        
        Material panelMat = params.constructPanelMaterial(assetManager);
        panelMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        panelGeom.setMaterial(panelMat);
        
        Material iconMat = params.constructIconMaterial(assetManager);
        iconMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        iconGeom.setMaterial(iconMat);
        
        panelHoveredTex = params.getHoveredTexture();
        panelUnhoveredTex = params.getNotHoveredTexture();
        
        setHovered(false);
        
        angle = startingAngle;
        radius = calculateRadius(menuListSize);
        startPos = new Vector3f((radius + SIZE) * FastMath.cos(startingAngle), (radius + SIZE) * FastMath.sin(startingAngle), 0f);
        optionNode.setLocalTranslation(startPos);
    }
    
    @Override
    protected void updateCustom(float tpf, float time) {
        if (hovered) {
            iconGeom.getMaterial().setColor("Color", HoveredOrange.mult(1 + (0.25f * Math.abs(FastMath.sin(time)))));
        }
        
        if (!idleOnHoveredOnly || (idleOnHoveredOnly && hovered)) {
            optionNode.setLocalTranslation(startPos.add(optimizedIdleMovement(time)));
            //optionNode.setLocalTranslation(startPos.x + idleMovement.x(time), startPos.y + idleMovement.y(time), startPos.z);
        }
    }
    
    //bounces back and forth between a domain of [0, 10] so too many extra objects aren't created
    private Vector3f optimizedIdleMovement(float time) {
        float correspondingTime;
        
        int cutoff = 10; // domain [0, 10]
        if ((int)(time / cutoff) % 2 == 0) { //if even, bounce forward (positive direction)
            correspondingTime = time % cutoff;
        } else { //if odd, bounce backward (negative direction)
            correspondingTime = cutoff - (time % cutoff);
        }
        
        return new Vector3f(idleMovement.x(correspondingTime), idleMovement.y(correspondingTime), 0);
    }
    
    public void rotate(float theta) {
        optionNode.rotate(0, 0, theta);
        angle += theta;
    }
    
    @Override
    protected void setHover(boolean hov) {
        if (hov) { //hovered
            panelGeom.getMaterial().setTexture("ColorMap", panelHoveredTex);
            iconGeom.getMaterial().setColor("Color", HoveredOrange);
        } else if (!hov) { //not hovered
            panelGeom.getMaterial().setTexture("ColorMap", panelUnhoveredTex);
            iconGeom.getMaterial().setColor("Color", new ColorRGBA(0f, 0f, 0f, 1f)); //black
        }
    }
    
    
    public static float calculateRadius(int size) {
        return (MIN_PADDING / (FastMath.TWO_PI / size)); //MIN_PADDING over deltaTheta
    }
    
}

class CustomMenuAssetParams {
    private final TexturedMaterialCreator panel, icon;
    private final Texture notHoveredTexture;
    
    public CustomMenuAssetParams(Texture iconTex) {
        this(iconTex, MapTextures.GUI.ActionMenu.HoveredBG, MapTextures.GUI.ActionMenu.NotHoveredBG);
    }
    
    public CustomMenuAssetParams(Texture iconTex, Texture hoveredTex, Texture notHoveredTex) {
        notHoveredTexture = notHoveredTex;
        panel = new TexturedMaterialCreator("ColorMap", hoveredTex);
        icon = new TexturedMaterialCreator("ColorMap", iconTex);
    }
    
    public CustomMenuAssetParams(TexturedMaterialCreator panel, TexturedMaterialCreator icon, Texture notHoveredTexture) {
        this.panel = panel;
        this.icon = icon;
        this.notHoveredTexture = notHoveredTexture;
    }
    
    public Texture getHoveredTexture() { return panel.getTexture(); }
    public Texture getNotHoveredTexture() { return notHoveredTexture; }
    
    public Material constructPanelMaterial(AssetManager assetManager) {
        return panel.createMaterial(assetManager);
    }
    
    public Material constructIconMaterial(AssetManager assetManager) {
        return icon.createMaterial(assetManager);
    }
}

