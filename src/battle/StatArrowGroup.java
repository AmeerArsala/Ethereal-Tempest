/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle;

import com.atr.jme.font.shape.TrueTypeNode;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.Label;
import general.VisualTransition.Progress;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author night
 */
public class StatArrowGroup extends Node {
    private final BitmapFont levelUpBonus;
    private final TrueTypeNode dimensions;
    
    private Progress updateProgress = Progress.Progressing;
    
    private List<Geometry> arrows = new ArrayList<>();
    private List<Progress> arrowProgresses = new ArrayList<>();
    private ArrayList<Integer> growths = new ArrayList<>();
    
    enum ArrowStat {
        //column 1
        MAXHP(0),
        STR(1),
        ETHER(2),
        AGI(3),
        COMP(4),
        DEX(5),
        
        //column 2
        MAXTP(0),
        DEF(1),
        RSL(2),
        MOBILITY(3),
        PHYSIQUE(4),
        BaseADRENALINE(5);
        
        private final int value;
        private ArrowStat(int val) {
            value = val;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public StatArrowGroup(TrueTypeNode column1stats, float arrowDimensionX, AssetManager assetManager) {
        super();
        dimensions = column1stats;
        
        levelUpBonus = assetManager.loadFont("Interface/Fonts/dalek.fnt");
        
        Material arrowMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        arrowMat.setTexture("ColorMap", assetManager.loadTexture("Interface/GUI/levelup_panel/arrow.png"));
        arrowMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        arrowMat.setColor("Color", new ColorRGBA(1, 1, 1, 0));
        
        for (int i = 0; i < 6; i++) {
            Quad aquad = new Quad(arrowDimensionX, arrowDimensionX * (788f / 600f));
            Geometry geom = new Geometry("aquad" + i, aquad);
            geom.setMaterial(arrowMat.clone());
            arrows.add(geom);
            arrowProgresses.add(Progress.Fresh);
            attachChild(arrows.get(i));
        }
        
        spaceArrows();
    }
    
    private void spaceArrows() {
        for (int i = 0; i < arrows.size(); i++) {
            arrows.get(i).move(dimensions.getWidth(), -1 * i * (dimensions.getHeight() / arrows.size()), 0);
        }
    }
    
    public List<Geometry> getArrows() {
        return arrows;
    }
    
    public Progress getProgress() {
        return updateProgress;
    }
    
    public void setVisibilityOfAllArrows(boolean visible) {
        arrows.forEach((geo) -> {
            geo.getMaterial().setColor("Color", new ColorRGBA(1, 1, 1 , visible ? 1 : 0));
        });
    }
    
    public void setVisibilityOfArrow(int index, boolean visible) {
        arrows.get(index).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1 , visible ? 1 : 0));
    }
    
    private float distanceMoved = 0, opacity = 0;
    private final float MOVE_DISTANCE = 5, OPACITY_DIFFERENCE = 0.25f;
    
    public void update(float tpf) {
        int arrow = nextArrow();
        
        if (arrow == -1) {
            updateProgress = Progress.Finished;
        } else if (distanceMoved < 30f) {
            arrows.get(arrow).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, opacity));
            arrows.get(arrow).move(0, MOVE_DISTANCE, 0);
            
            distanceMoved += MOVE_DISTANCE;
            opacity += OPACITY_DIFFERENCE;
        } else { //end this stat arrow and go to the next one
            //TODO: play a sound
            arrows.get(arrow).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1, opacity));
            Label difference = new Label("+" + growths.get(arrow));
            difference.setShadowColor(ColorRGBA.White);
            difference.setColor(ColorRGBA.Black);
            difference.setFont(levelUpBonus);
            difference.setLocalTranslation(arrows.get(arrow).getLocalTranslation().add(32.5f, 40f, 1f));
            attachChild(difference);
            
            arrowProgresses.set(arrow, Progress.Finished);
            distanceMoved = 0;
        }
                    
        
    }
    
    private int nextArrow() {
        for (int i = 0; i < arrowProgresses.size(); i++) {
            if (arrowProgresses.get(i) == Progress.Progressing) {
                return i;
            }
        }
        
        return -1;
    } 
    
    void setVisibilityOfArrow(ArrowStat target, boolean visible) {
        arrows.get(target.getValue()).getMaterial().setColor("Color", new ColorRGBA(1, 1, 1 , visible ? 1 : 0));
    }
    
    void inputGrowth(int difference, ArrowStat AS) {
        growths.add(difference);
        arrowProgresses.set(AS.getValue(), difference > 0 ? Progress.Progressing : Progress.Finished);
    }
    
    Geometry getArrow(ArrowStat target) {
        return arrows.get(target.getValue());
    }
}
