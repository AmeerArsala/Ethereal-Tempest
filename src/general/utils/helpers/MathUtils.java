/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils.helpers;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

/**
 *
 * @author night
 */
public class MathUtils {
    
    public static float pointSlopeForm(float x, float x1, float x2, float y1, float y2) {
        float m = ((y2 - y1) / (x2 - x1));
        return m * (x - x1) + y1;
    }
    
    public static float hypotenuse(Vector2f mona) {
        return FastMath.sqrt((mona.x*mona.x) + (mona.y*mona.y));
    }
    
    public static float hypotenuse(float x, float y) {
        return FastMath.sqrt((x*x) + (y*y));
    }
    
    /**
     * 
     * @param edgePosPercent battleBoxInfo.getLeftEdgePositionPercent() or battleBoxInfo.getRightEdgePositionPercent()
     * @param sideLength the dimension of the side to which you are calling this on (ex. battleBoxInfo.getBoxDimensions().x or battleBoxInfo.getBoxDimensions().y)
     * @param localTranslation the local translation of whichever entity this is being used for of the dimension in sideLength
     * @return the percent difference from an edge
     */
    public static float percentDiffFromEdge(float edgePosPercent, float sideLength, float localTranslation) {
        float actualEdgePos = edgePosPercent * sideLength;
        float diffFromEdge = localTranslation - actualEdgePos;
        return (diffFromEdge / sideLength);
    }
    
    public static float max(float... nums) {
        float max = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > max) {
                max = nums[i];
            }
        }
        
        return max;
    }
    
    public static float min(float... nums) {
        float min = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < min) {
                min = nums[i];
            }
        }
        
        return min;
    }
}
