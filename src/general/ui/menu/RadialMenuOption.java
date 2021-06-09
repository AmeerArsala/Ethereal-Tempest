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

/**
 *
 * @author night
 * @param <DATA> the type of the data holder (e.g. Conveyor)
 */
public abstract class RadialMenuOption<DATA> extends MenuOption<DATA> {
    public static final ColorRGBA HoveredOrange = new ColorRGBA(1f, 119f / 255f, 0f, 1f);
    
    public static final String DEFAULT_HOVERED = "Interface/GUI/action_menu/hoveredbg.png";
    public static final String DEFAULT_NOT_HOVERED = "Interface/GUI/action_menu/nothoveredbg.png";
    
    static final float SIZE = 50f;
    static final float icoSizeDim = SIZE *  200f / 256f;
    
    private static final Vector3f iconTranslation = new Vector3f(0.5f * (SIZE - icoSizeDim), 0.5f * (SIZE - icoSizeDim), 3f);
    private static final float MIN_PADDING = 30f;

    
    private final String iconPath;
    
    private final Quad panelBG = new Quad(SIZE, SIZE), icon = new Quad(icoSizeDim, icoSizeDim);
    private final Geometry panelGeom = new Geometry("panel", panelBG), iconGeom = new Geometry("icon", icon);
    private Texture panelHoveredTex, panelUnhoveredTex;
    
    private Vector3f startPos;
    private float angle;
    private float radius;
    
    private ParametricFunction idleMovement;
    private boolean idleOnHoveredOnly;
    
    private final VisualTransition transitionEvent = new VisualTransition(optionNode);
    
    public RadialMenuOption(String optionName, String icoPath) {
        super(optionName);
        
        iconPath = icoPath;
        
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
        initializeAssets(assetManager, new CustomMenuAssetParams(iconPath), idleGraph, idleOnlyOnHovered, startingAngle, menuListSize);
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
        
        panelHoveredTex = assetManager.loadTexture(params.getHoveredTexturePath());
        panelUnhoveredTex = assetManager.loadTexture(params.getNotHoveredTexturePath());
        
        setHovered(false);
        
        angle = startingAngle;
        radius = calculateRadius(menuListSize);
        startPos = new Vector3f((radius + SIZE) * FastMath.cos(startingAngle), (radius + SIZE) * FastMath.sin(startingAngle), 0f);
        optionNode.setLocalTranslation(startPos);
    }
    
    @Override
    protected void updateCustom(float tpf, float time) {
        if (hovered) {
            iconGeom.getMaterial().setColor("Color", HoveredOrange.mult(1 + (0.25f * FastMath.abs(FastMath.sin(time)))));
        }
        
        if (!idleOnHoveredOnly || (idleOnHoveredOnly && hovered)) {
            optionNode.setLocalTranslation(startPos.x + idleMovement.x(time), startPos.y + idleMovement.y(time), startPos.z);
        }
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
    private final String notHoveredTexturePath;
    
    public CustomMenuAssetParams(String iconPath) {
        this(iconPath, RadialMenuOption.DEFAULT_HOVERED, RadialMenuOption.DEFAULT_NOT_HOVERED);
    }
    
    public CustomMenuAssetParams(String iconPath, String hoveredTexPath, String notHoveredTexPath) {
        notHoveredTexturePath = notHoveredTexPath;
        panel = new TexturedMaterialCreator("ColorMap", hoveredTexPath);
        icon = new TexturedMaterialCreator("ColorMap", iconPath);
    }
    
    public CustomMenuAssetParams(TexturedMaterialCreator panel, TexturedMaterialCreator icon, String notHoveredTexturePath) {
        this.panel = panel;
        this.icon = icon;
        this.notHoveredTexturePath = notHoveredTexturePath;
    }
    
    public String getHoveredTexturePath() { return panel.getTexturePath(); }
    public String getNotHoveredTexturePath() { return notHoveredTexturePath; }
    
    public Material constructPanelMaterial(AssetManager assetManager) {
        return panel.createMaterial(assetManager);
    }
    
    public Material constructIconMaterial(AssetManager assetManager) {
        return icon.createMaterial(assetManager);
    }
}

