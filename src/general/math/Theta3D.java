/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.math;

import com.jme3.math.FastMath;

/**
 *
 * @author night
 */
public class Theta3D {
    public float thetaXY, thetaZ;
    
    public Theta3D(float thetaXY, float thetaZ) {
        this.thetaXY = thetaXY;
        this.thetaZ = thetaZ;
    }
    
    //these parameters refer to the relative translation of a point
    public Theta3D(float deltaX, float deltaY, float deltaZ) {
        thetaXY = FastMath.atan(deltaY / deltaX);
        
        float c = FastMath.sqrt(FastMath.pow(deltaX, 2) + FastMath.pow(deltaY, 2));
        
        thetaZ = FastMath.atan(c / deltaZ); //used to be deltaZ / c
    }
    
    public float getXYAngle() { return thetaXY; }
    public float getZAngle() { return thetaZ; }
    
    
}
