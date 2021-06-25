/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import com.google.gson.annotations.Expose;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import general.math.ParametricFunctionStrings3f;
import general.math.ParametricFunctionStrings4f;
import general.utils.helpers.GeneralUtils;

/**
 *
 * @author night
 */
public class Changes {
    //use the arrays as piecewise
    private Change3f[] velocities; //velocity; units: localTranslation/frame
    private Change3f[] thetaVelocities; //angular velocity in degrees; units: degrees/frame 
    private Change3f[] localScales; //not a velocity function
    private Change4f[] colors; //not a velocity function
    
    //@Expose(deserialize = false) 
    //private ChangePack[] finiteChanges; //changes at all FINITE frames since action frame; this includes infinites up to the highest index of the arrays above
    
    public Changes() {}
    
    public Changes(Change3f[] velocities, Change3f[] thetaVelocities, Change3f[] localScales, Change4f[] colors) {
        this.velocities = velocities;
        this.thetaVelocities = thetaVelocities;
        this.localScales = localScales;
        this.colors = colors;
    }
    
    Change3f[] getVelocities() { return velocities; }
    Change3f[] getAngularVelocities() { return thetaVelocities; }
    Change3f[] getLocalScales() { return localScales; }
    Change4f[] getColors() { return colors; }
    
    /*public ChangePack getChangePack(int framesSinceActionFrame) {
        return finiteChanges.length > 0 && framesSinceActionFrame < finiteChanges.length ? finiteChanges[framesSinceActionFrame] : generateChangePack(framesSinceActionFrame);
    }*/
    
    public ChangePack generateChangePack(int framesSinceActionFrame) {
        ParametricFunctionStrings4f colorStrings = getColorStrings(framesSinceActionFrame);
        
        ColorRGBA rgba = null;
        String colorMatParam = null;
        if (colorStrings != null) {
            rgba = colorStrings.getRGBAFunction().rgba(framesSinceActionFrame);
            colorMatParam = colorStrings.getColorMatParam();
        }
        
        return new ChangePack(
            getVelocity(framesSinceActionFrame),
            getAngularVelocity(framesSinceActionFrame),
            getLocalScale(framesSinceActionFrame),
            rgba,
            colorMatParam
        );
    }
    
    private static int getChangeIndex(DomainParse[] arr, int framesSinceActionFrame, String name) {
        //System.out.println("[framesSinceActionFrame: " + framesSinceActionFrame + "]: " + name);
        for (int i = 0; i < arr.length; ++i) {
            boolean infinite = arr[i].isInfinite();
            //System.out.println(name + "[" + i + "].isInfinite() == " + infinite);
            if (infinite || (!infinite && arr[i].frameWithinSpecifiedBounds(framesSinceActionFrame))) {
                return i;
            }
        }
        
        return -1;
    }
    
    public Vector3f getVelocity(int framesSinceActionFrame) {
        int index = getChangeIndex(velocities, framesSinceActionFrame, "velocity");
        return index == -1 ? null : velocities[index].getStrFunc().output(framesSinceActionFrame);
    }
    
    public Vector3f getAngularVelocity(int framesSinceActionFrame) {
        int index = getChangeIndex(thetaVelocities, framesSinceActionFrame, "thetaVelocity");
        return index == -1 ? null : thetaVelocities[index].getStrFunc().output(framesSinceActionFrame);
    }
    
    public Vector3f getLocalScale(int framesSinceActionFrame) {
        int index = getChangeIndex(localScales, framesSinceActionFrame, "localScale");
        return index == -1 ? null : localScales[index].getStrFunc().output(framesSinceActionFrame);
    }
    
    public ParametricFunctionStrings4f getColorStrings(int framesSinceActionFrame) {
        int index = getChangeIndex(colors, framesSinceActionFrame, "color");
        return index == -1 ? null : colors[index].getStrFunc();
    }
    
    public void initializeAll() {
        for (Change3f velocity : velocities) {
            velocity.getStrFunc().initialize();
        }
        
        for (Change3f thetaVelocity : thetaVelocities) {
            thetaVelocity.getStrFunc().initialize();
        }
        
        for (Change3f localScale : localScales) {
            localScale.getStrFunc().initialize();
        }
        
        for (Change4f color : colors) {
            color.getStrFunc().initialize();
        }
        
        /*
        finiteChanges = new ChangePack[
            GeneralUtils.highestInt(
                new int[]{velocities.length, thetaVelocities.length, localScales.length, colors.length}
            )
        ];
        
        for (int i = 0; i < finiteChanges.length; ++i) { //i = framesSince
            finiteChanges[i] = generateChangePack(i);
        }
        
        */
    }
}

class DomainParse {
    private String domain;
    
    public DomainParse(String domain) { //something like "1, 2" or "0, ?" which is 0 to infinity
        this.domain = domain;
    }
    
    public int getStartFrame() { return Integer.parseUnsignedInt(domain.substring(0, 1)); } //parse first character
    public int getEndFrame() { return Integer.parseUnsignedInt(domain.substring(domain.length() - 1)); } //can fail if infinite so check first
    public boolean isInfinite() { return domain.charAt(domain.length() - 1) == '?'; }
    
    public boolean frameWithinSpecifiedBounds(int frame) {
        return frame >= getStartFrame() && frame <= getEndFrame();
    }
}

class Change3f extends DomainParse {
    private ParametricFunctionStrings3f strFunc;
    
    public Change3f(ParametricFunctionStrings3f strFunc, String domain) {
        super(domain);
        this.strFunc = strFunc;
    }
    
    public ParametricFunctionStrings3f getStrFunc() { return strFunc; }
}

class Change4f extends DomainParse {
    private ParametricFunctionStrings4f strFunc;
    
    public Change4f(ParametricFunctionStrings4f strFunc, String domain) {
        super(domain);
        this.strFunc = strFunc;
    }
    
    public ParametricFunctionStrings4f getStrFunc() { return strFunc; }
}
