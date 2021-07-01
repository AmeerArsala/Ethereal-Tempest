/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battle.animation.config.action;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import enginetools.math.Vector3F;
import enginetools.math.Vector4F;
import general.math.ParametricFunctionStrings3f.ConstantVector3f;
import general.math.ParametricFunctionStrings4f.ConstantColorRGBA;
import general.math.ParametricFunctionStrings4f.ConstantVector4f;

/**
 *
 * @author night
 * 
 * This class is for Constants for customization without having to program by using JSON
 * 
 * U and E correspond to the values passed in from the methods (for user and opponent respectively)
 * W and G correspond to the static fields above EXPRESSION_CONSTANTS (for user and opponent respectively)
 * R corresponds to random
 * J corresponds to javaRandom
 * 
 */
public class ConstantsDealer {
    public static final Vector3f USER_DIMENSIONS = new Vector3f(0, 0, 0), OPPONENT_DIMENSIONS = new Vector3f(0, 0, 0);
    public static final Vector4f USER_VEC4 = new Vector4f(0, 0, 0, 0), OPPONENT_VEC4 = new Vector4f(0, 0, 0, 0);
    public static final ColorRGBA USER_COLOR_CONSTANT = ColorRGBA.White, OPPONENT_COLOR_CONSTANT = ColorRGBA.White;
    
    public static final char[] EXPRESSION_CONSTANTS = { //these constants get replaced by their actual values so use whatever characters you want, but avoid using common mathematical chars such as 'e'
        'U',  // User starting value (typically denotes original position with Vector3f, in terms of BattleBox %)
        'E',  // Enemy starting value (typically denotes original position with Vector3f, in terms of BattleBox %)
        
        'W',  // User sprite dimensions value (in terms of BattleBox %)
        'G',  // Enemy sprite dimensions value (in terms of BattleBox %)
        
        'R',  // random()
        'J'   // javaRandom(), or in ColorRGBA's case, randomGaussian()
    };
    
    public static void swapFields() {
        //swaps vec3s
        Vector3f userDims = new Vector3f(USER_DIMENSIONS);
        USER_DIMENSIONS.set(OPPONENT_DIMENSIONS);
        OPPONENT_DIMENSIONS.set(userDims);
        
        //swap vec4s
        Vector4f uservec4 = new Vector4f(USER_VEC4);
        USER_VEC4.set(OPPONENT_VEC4);
        OPPONENT_VEC4.set(uservec4);
        
        //swap colors
        ColorRGBA userColorConst = new ColorRGBA(USER_COLOR_CONSTANT);
        USER_COLOR_CONSTANT.set(OPPONENT_COLOR_CONSTANT);
        OPPONENT_COLOR_CONSTANT.set(userColorConst);
    }
    
    private static ConstantVector3f[] vec3map(Vector3f... vec3s) {
        ConstantVector3f[] constantVec3fs = new ConstantVector3f[EXPRESSION_CONSTANTS.length];
        for (int i = 0; i < constantVec3fs.length; ++i) {
            constantVec3fs[i] = new ConstantVector3f(EXPRESSION_CONSTANTS[i], vec3s[i]);
        }
        
        return constantVec3fs;
    }
    
    private static ConstantVector4f[] vec4map(Vector4f... vec4s) {
        ConstantVector4f[] constantVec4fs = new ConstantVector4f[EXPRESSION_CONSTANTS.length];
        for (int i = 0; i < constantVec4fs.length; ++i) {
            constantVec4fs[i] = new ConstantVector4f(EXPRESSION_CONSTANTS[i], vec4s[i]);
        }
        
        return constantVec4fs;
    }
    
    private static ConstantColorRGBA[] rgbaMap(ColorRGBA... colors) {
        ConstantColorRGBA[] constantColors = new ConstantColorRGBA[EXPRESSION_CONSTANTS.length];
        for (int i = 0; i < constantColors.length; ++i) {
            constantColors[i] = new ConstantColorRGBA(EXPRESSION_CONSTANTS[i], colors[i]);
        }
        
        return constantColors;
    }
    
    public static ConstantVector3f[] Vec3f_Constants(Vector3f userVec3f, Vector3f opponentVec3f) {
        return vec3map(
            userVec3f,            // U: user startPos
            opponentVec3f,        // E: opponent startPos
            USER_DIMENSIONS,      // W: user sprite dimensions
            OPPONENT_DIMENSIONS,  // G: opponent sprite dimensions
            Vector3F.random(),    // R: random()
            Vector3F.javaRandom() // J: javaRandom()
        );
    }
    
    public static ConstantVector4f[] Vec4f_Constants(Vector4f userVec4f, Vector4f opponentVec4f) {
        return vec4map(
            userVec4f,            // U: user vec4
            opponentVec4f,        // E: opponent vec4
            USER_VEC4,            // W: user static vec4 constant
            OPPONENT_VEC4,        // G: opponent static vec4 constant
            Vector4F.random(),    // R: random()
            Vector4F.javaRandom() // J: javaRandom()
        );
    }
    
    public static ConstantColorRGBA[] ColorRGBA_Constants(ColorRGBA userColor, ColorRGBA opponentColor) {
        return rgbaMap(
            userColor,                      // U: user color
            opponentColor,                  // E: opponent color
            USER_COLOR_CONSTANT,            // W: user static ColorRGBA constant
            OPPONENT_COLOR_CONSTANT,        // G: opponent static ColorRGBA constant
            ColorRGBA.randomColor(),        // R: random()
            Vector4F.randomGaussianColor()  // J: randomGaussianColor()
        );
    }
}
