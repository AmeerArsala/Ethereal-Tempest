/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.visual;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import edited.CustomProgressBar;

/**
 *
 * @author night
 */
public class RadialProgressBar extends Node {
    private float innerRadius, outerRadius;
    private ColorRGBA color;
    
    private final RadialMesh outerMeshComplete; //z = 0 (bottom layer)
    private RadialMesh outerMeshCurrent; //z = 1 (middle layer)
    private final RadialMesh innerMeshCurrent; //z = 2 (top layer)
    
    private final Geometry outerComplete, innerCurrent;
    private Geometry outerCurrent;
    
    private Node childrenNode;
    private float percentage = 0.5f; //default to 50%
    
    private final int specificity;
    
    public RadialProgressBar(float innerRadius, float outerRadius, ColorRGBA color, int specificity) { 
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.color = color;
        this.specificity = specificity;
        
        if (specificity < 1) {
            specificity = 1;
        }
        
        Material progress, empty;
        
        progress = CustomProgressBar.getWhiteSquare().getMaterial().getMaterial().clone();
        progress.setColor("Color", color);
        
        empty = progress.clone();
        empty.setColor("Color", new ColorRGBA(0.22f, 0.22f, 0.22f, 1f));
        
        outerMeshComplete = new RadialMesh(outerRadius, 1f, specificity);
        innerMeshCurrent = new RadialMesh(innerRadius, 1f, specificity);
        
        outerComplete = new Geometry("completed outer mesh", outerMeshComplete);
        outerComplete.setMaterial(empty);
        //outerComplete.setQueueBucket(Bucket.Opaque);
        
        innerCurrent = new Geometry("current inner mesh", innerMeshCurrent);
        innerCurrent.setMaterial(empty.clone());
        
        outerMeshCurrent = new RadialMesh(outerRadius, percentage, specificity);
        outerCurrent = new Geometry("current outer mesh", outerMeshCurrent);
        outerCurrent.setMaterial(progress);
        //outerCurrent.setQueueBucket(Bucket.Opaque);
        
        childrenNode = new Node();
        
        attachChild(outerComplete);
        attachChild(innerCurrent);
        attachChild(outerCurrent); //actual bar
        
        outerCurrent.move(0, 0, 1);
        innerCurrent.move(0, 0, 2);
        
        attachChild(childrenNode);
        
        rotate(0, 0, FastMath.PI / 2);
        childrenNode.rotate(0, 0, FastMath.PI / -2f);
        childrenNode.move(0, 0, 3);
    }
    
    private void updatePercentage() {
        outerMeshCurrent = new RadialMesh(outerRadius, percentage, specificity);
        outerCurrent.setMesh(outerMeshCurrent);
    }
    
    public float getInnerRadius() { return innerRadius; }
    public float getOuterRadius() { return outerRadius; }
    public float getPercentage() { return percentage; }
    
    public ColorRGBA getColor() { return color; }
    
    public void setColor(ColorRGBA C) {
        color = C;
        outerCurrent.getMaterial().setColor("Color", color);
    }
    
    public void setCirclePercent(float percent) { // <= 1 and >= 0 [0, 1]
        percentage = percent;
        updatePercentage();
    }
    
    public Node getChildrenNode() {
        return childrenNode;
    }
    
}
