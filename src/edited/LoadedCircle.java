/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edited;

import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.shape.TrueTypeNode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.QuadBackgroundComponent;

/**
 *
 * @author night
 */
public class LoadedCircle extends Node {
        private TrueTypeFont ttf, ttf2;
        private TrueTypeNode fontText, fontText2;
        
        private float inRadius, outRadius;
        private ColorRGBA foreground, background;
        
        private double rotationPercent;
        private boolean translationOccurred = false;
        
        //protected CircularProgressBar[] bars = new CircularProgressBar[360];
        
        public Node[] ticks = new Node[360];
        
        private QuadBackgroundComponent bg, nonbg;
        
        /*public LoadedCircle(int specificity, float innerRadius, float outerRadius) { //specificity^-1 to see the amount of a circle you have
            inRadius = innerRadius;
            outRadius = outerRadius;
            
            foreground = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f);
            background = new ColorRGBA(0.22f, 0.22f, 0.22f, 1f);
            
            for (int i = 0; i < bars.length; i++) {
                bars[i] = new CircularProgressBar(specificity, innerRadius, outerRadius, i);
                bars[i].setBarColor(foreground); //default blue
                bars[i].setBarPercent((i + 1) * (100.0 / bars.length)); //kdeltax is the parameter
                attachChild(bars[i]);
            }
        }*/
        
        public LoadedCircle(float innerRadius, float outerRadius) {
            inRadius = innerRadius;
            outRadius = outerRadius;
            
            foreground = new ColorRGBA(0.012f, 0.58f, 0.988f, 1f);
            background = new ColorRGBA(0.22f, 0.22f, 0.22f, 1f);
            
            bg = CustomProgressBar.getWhiteSquare().clone();
            bg.setColor(foreground);
            
            nonbg = bg.clone();
            nonbg.setColor(new ColorRGBA(background));
            
            for (int i = 0; i < 360; i++) {
                ticks[i] = getBarNode(i);
                attachChild(ticks[i]);
                /*bars[i] = new CircularProgressBar(1, innerRadius, outerRadius, i);
                bars[i].setBarColor(foreground); //default blue
                bars[i].setBarPercent((i + 1) * (100.0 / bars.length)); //kdeltax is the parameter
                attachChild(bars[i]);*/
            }
        }
        
        public void reset() {
            /*detachAllChildren();
            for (CircularProgressBar tick : bars) {
                attachChild(tick);
            }*/
        }
        
        public TrueTypeFont getFont() { return ttf; }
        
        public TrueTypeFont getFont2() { return ttf2; }
        
        public void setFont(TrueTypeFont ttf) {
            this.ttf = ttf;
        }
        
        public void setFont2(TrueTypeFont ttf2) {
            this.ttf2 = ttf2;
        }
        
        public void setRotationPercent(double percent) { //default: 100.0%
            rotationPercent = percent;
            double decimal = percent / 100.0;
            clearRotation();
            for (int j = 0; j < 360; j++) {
                if (j <= (int)(decimal * 360)) {
                    ((Panel)ticks[j].getChild(0)).setBackground(bg.clone());
                } else { ((Panel)ticks[j].getChild(0)).setBackground(nonbg.clone()); }
            }
        }
        
        public void clearRotation() {
            for (int j = 0; j < 360; j++) {
                ((Panel)ticks[j].getChild(0)).setBackground(nonbg.clone());
            }
        }
        
        public double getRotationPercent() { return rotationPercent; }
        
        public float getInnerRadius() { return inRadius; }
        
        public float getOuterRadius() { return outRadius; }
        
        public void setForegroundColor(ColorRGBA fgcolor) { //default: a blue color
            bg.setColor(fgcolor);
        }
        
        public void setBackgroundColor(ColorRGBA bgcolor) { //default: a dark gray color
            nonbg.setColor(bgcolor);
        }
        
        public ColorRGBA getForegroundColor() { return foreground; }
        
        public ColorRGBA getBackgroundColor() { return background; }
        
        public TrueTypeNode getTextNode() { return fontText; }
        
        public TrueTypeNode getTextNode2() { return fontText; }
        
        public void setSingleText(String str) {
            if (hasChild(fontText)) {
                detachChild(fontText);
            }
            
            fontText = ttf.getText(str, 3, ColorRGBA.White);
            attachChild(fontText);
            
            fontText.move(((str.length() + 1f) / 2f) * -0.375f * ((outRadius + inRadius) / 2f), 0.625f * ((outRadius + inRadius) / 2f), 0);
        }
        
        
        public void setText(String str) {
            if (hasChild(fontText)) {
                detachChild(fontText);
            }
            
            fontText = ttf.getText(str, 3, ColorRGBA.White);
            attachChild(fontText);
        }
        
        public void setText(String str, ColorRGBA color) {
            if (hasChild(fontText)) {
                detachChild(fontText);
            }
            
            fontText = ttf.getText(str, 3, color);
            attachChild(fontText);
        }
        
        public void setText2(String str) {
            if (hasChild(fontText2)) {
                detachChild(fontText2);
            }
            
            fontText2 = ttf2.getText(str, 3, ColorRGBA.White);
            attachChild(fontText2);
        }
        
        public void setText2(String str, ColorRGBA color) {
            if (hasChild(fontText2)) {
                detachChild(fontText2);
            }
            
            fontText2 = ttf2.getText(str, 3, color);
            attachChild(fontText2);
        }
        
        public void setBothText(String str1, String str2) {
            if (fontText != null && hasChild(fontText)) {
                detachChild(fontText);
            }
            if (fontText2 != null && hasChild(fontText2)) {
                detachChild(fontText2);
            }
            
            fontText = ttf.getText(str1, 3, ColorRGBA.White);
            fontText2 = ttf2.getText(str2, 3, ColorRGBA.White);
            
            attachChild(fontText);
            fontText.attachChild(fontText2);
        }
        
        public void birthTranslation(float x, float y) {
            if (!translationOccurred) {
                move(x, y, 0f);
                translationOccurred = true;
            }
        }
        
        public Node getBarNode(int startDegree) {
            Node X = new Node();
            
            float diameter = (2 * FastMath.PI) * outRadius;
            
            Panel bar = new Panel(diameter * (1.0f / 360f), outRadius - inRadius);
            bar.setBackground(bg.clone());
            
            Quaternion rotation = new Quaternion();
            rotation.fromAngles(0, 0, (((float)startDegree / 360f) * (FastMath.PI * 2)));
            X.setLocalRotation(rotation);
            
            bar.move(0, inRadius, 0);
            
            X.attachChild(bar);
            
            return X;
        }
    
}
