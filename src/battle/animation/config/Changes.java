/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import general.math.ParametricFunctionStrings3f;
import general.math.ParametricFunctionStrings4f;
import general.math.function.ParametricFunction3f;
import general.math.function.RGBAFunction;

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
    
    public Vector3f getVelocity(int framesSinceActionFrame) {
        for (Change3f velocity : velocities) {
            if (velocity.getFrames() == null || framesSinceActionFrame < velocity.getFrames()) { //null acts as infinity in this case
                ParametricFunction3f v = velocity.getStrFunc().getFunction();
                return new Vector3f(v.x(framesSinceActionFrame), v.y(framesSinceActionFrame), v.z(framesSinceActionFrame));
            }
            
            framesSinceActionFrame -= velocity.getFrames();
        }
        
        return null;
    }
    
    public Vector3f getAngularVelocity(int framesSinceActionFrame) {
        for (Change3f thetaVelocity : thetaVelocities) {
            if (thetaVelocity.getFrames() == null || framesSinceActionFrame < thetaVelocity.getFrames()) { //null acts as infinity in this case
                ParametricFunction3f v = thetaVelocity.getStrFunc().getFunction();
                return new Vector3f(v.x(framesSinceActionFrame), v.y(framesSinceActionFrame), v.z(framesSinceActionFrame));
            }
            
            framesSinceActionFrame -= thetaVelocity.getFrames();
        }
        
        return null;
    }
    
    public Vector3f getLocalScale(int framesSinceActionFrame) {
        for (Change3f localScale : localScales) {
            if (localScale.getFrames() == null || framesSinceActionFrame < localScale.getFrames()) { //null acts as infinity in this case
                ParametricFunction3f s = localScale.getStrFunc().getFunction();
                return new Vector3f(s.x(framesSinceActionFrame), s.y(framesSinceActionFrame), s.z(framesSinceActionFrame));
            }
            
            framesSinceActionFrame -= localScale.getFrames();
        }
        
        return null;
    }
    
    public ColorRGBA getColor(int framesSinceActionFrame) {
        for (Change4f color : colors) {
            if (color.getFrames() == null || framesSinceActionFrame < color.getFrames()) { //null acts as infinity in this case
                RGBAFunction rgba = color.getStrFunc().getRGBAFunction();
                return rgba.rgba(framesSinceActionFrame);
            }
            
            framesSinceActionFrame -= color.getFrames();
        }
        
        return null;
    }
    
    public String getColorMatParam(int framesSinceActionFrame) {
        for (Change4f color : colors) {
            if (color.getFrames() == null || framesSinceActionFrame < color.getFrames()) { //null acts as infinity in this case
                return color.getStrFunc().getColorMatParam();
            }
            
            framesSinceActionFrame -= color.getFrames();
        }
        
        return null;
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
    }
}

class Change3f {
    private ParametricFunctionStrings3f strFunc;
    private Integer frames;
    
    public Change3f(ParametricFunctionStrings3f strFunc, int frames) {
        this.strFunc = strFunc;
        this.frames = frames;
    }
    
    public ParametricFunctionStrings3f getStrFunc() { return strFunc; }
    public Integer getFrames() { return frames; }
}

class Change4f {
    private ParametricFunctionStrings4f strFunc;
    private Integer frames;
    
    public Change4f(ParametricFunctionStrings4f strFunc, int frames) {
        this.strFunc = strFunc;
        this.frames = frames;
    }
    
    public ParametricFunctionStrings4f getStrFunc() { return strFunc; }
    public Integer getFrames() { return frames; }
}
