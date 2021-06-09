/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.utils;

/**
 *
 * @author night
 */
public class MathUtils {
    
    public static float pointSlopeForm(float x, float x1, float x2, float y1, float y2) {
        float m = ((y2 - y1) / (x2 - x1));
        return m * (x - x1) + y1;
    }
    
}
