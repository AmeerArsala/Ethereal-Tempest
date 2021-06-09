/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edited;

import com.atr.jme.font.TrueTypeFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author night
 */
public class CircularProgressBar extends Node {
    private Node[] barNodes;
    private float tickRadius;
    
    private QuadBackgroundComponent bg, nonbg;
    
    public CircularProgressBar(int specificity, float radius, float radiusOuter) { //default specificity would be 1, and it must be a positive integer
        barNodes = new Node[specificity * 360];
        bg = CustomProgressBar.getWhiteSquare().clone();
        nonbg = bg.clone();
        nonbg.setColor(new ColorRGBA(0.22f, 0.22f, 0.22f, 1f));
        tickRadius = radius;
        float diameter = (2 * FastMath.PI) * radiusOuter;
        
        for (int i = 0; i < barNodes.length; i++) {
            barNodes[i] = new Node();
            Panel bar = new Panel(diameter * (1.0f / barNodes.length), radiusOuter - radius);
            bar.setBackground(bg);
            
            Quaternion rotation = new Quaternion();
            rotation.fromAngles(0, 0, (((float)i / barNodes.length) * (FastMath.PI * 2))); //Z: (((float)i / barPieces.length) * (FastMath.PI * 2))
                                                                                            //Alternative Z: ((float)i * (FastMath.PI / 180))

            barNodes[i].setLocalRotation(rotation);
            
            bar.move(0, radius, 0);
            
            barNodes[i].attachChild(bar);
            
            attachChild(barNodes[i]);
            
            /*float[] euler = new float[3];
            rotation.toAngles(euler);
            System.out.println("X: " + euler[0] + "\nY: " + euler[1] + "\nZ: " + euler[2]);*/
            //System.out.println(Arrays.stream(rotation.toAngles(null)).map(angle -> angle * FastMath.RAD_TO_DEG).collect(Collectors.joining(", ")));
            
        }
    }
        

    public CircularProgressBar(int specificity, float radius, float radiusOuter, int startDegree) { //default specificity would be 1, and it must be a positive integer
        barNodes = new Node[specificity * 360];
        bg = CustomProgressBar.getWhiteSquare().clone();
        nonbg = bg.clone();
        nonbg.setColor(new ColorRGBA(0.22f, 0.22f, 0.22f, 1f));
        tickRadius = radius;
        float diameter = (2 * FastMath.PI) * radiusOuter;
        
        for (int i = startDegree; i < startDegree + 1; i++) {
            barNodes[i] = new Node();
            Panel bar = new Panel(diameter * (1.0f / barNodes.length), radiusOuter - radius);

            bar.setBackground(bg);
            
            Quaternion rotation = new Quaternion();
            rotation.fromAngles(0, 0, (((float)i / barNodes.length) * (FastMath.PI * 2))); //Z: (((float)i / barPieces.length) * (FastMath.PI * 2))
                                                                                           //Alternative Z: ((float)i * (FastMath.PI / 180))
            barNodes[i].setLocalRotation(rotation);
            
            bar.move(0, radius, 0);
            
            barNodes[i].attachChild(bar);
            
            attachChild(barNodes[i]);
            
        }
    }
    
    public void optimize(double percentage) {
        for (int i = 0; i < barNodes.length; i++) {
            if(Math.abs(percentage - i) > (2.59 * percentage)) {
                detachChild(barNodes[i]);
                barNodes[i] = null;
            }
        }
    }
    
    public void optimize2(double percentage) {
        for (int i = 0; i < barNodes.length; i++) {
            if(Math.abs(percentage - i) > percentage * Math.E) {
                detachChild(barNodes[i]);
                barNodes[i] = null;
            }
        }
    }
    
    public void setBarColor(ColorRGBA color) {
        bg.setColor(color);
    }
    
    public void setMinorColor(ColorRGBA color) {
        nonbg.setColor(color);
    }
    
    public void setBarPercent(double percent) {
        double decimal = percent / 100.0;
        for (int i = 0; i < barNodes.length; i++) {
            if (i <= (int)(decimal * (barNodes.length - 1))) {
                //((Panel)barNodes[i].getChild(0)).setBackground(bg);
                try {
                    ((Panel)barNodes[i].getChild(0)).setBackground(bg);
                    }
                catch (NullPointerException e) {}
            } else {
                //((Panel)barNodes[i].getChild(0)).setBackground(nonbg);
                try {
                    ((Panel)barNodes[i].getChild(0)).setBackground(nonbg);
                    }
                catch (NullPointerException e) {}
            }
        }
    }
    
}