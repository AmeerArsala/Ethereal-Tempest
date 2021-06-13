#import "Common/ShaderLib/GLSLCompat.glsllib"

precision mediump float;

uniform sampler2D m_ColorMap;
uniform vec4 m_OnlyChangeColor; // example: if this is white, then it will only change the white pixels in the ColorMap

uniform vec4 m_Color;
uniform vec4 m_BackgroundColor;
//uniform vec4 m_OutlineColor;
//uniform float m_OutlineThickness; //in percent, from 0.0 to 1.0
uniform bool m_UsesGradient;
uniform float m_GradientCoefficient;
uniform float m_GradientStart;
uniform float m_PercentFilled; //from 0.0 to 1.0
uniform float m_PercentStart;  //from 0.0 to 1.0
uniform float m_PercentEnd;    //from 0.0 to 1.0

varying vec2 texCoord;

vec4 obtainColor(vec4 color) {
    #ifdef HAS_PERCENT_FILLED
        float percentAreaLength = m_PercentEnd - m_PercentStart;
        
        if (texCoord.y <= m_PercentStart + (m_PercentFilled * percentAreaLength)) {
            #ifdef HAS_COLOR
                color *= m_Color;
                
                #ifdef HAS_USES_GRADIENT
                    float horizontalShift = 0.0;
                    #ifdef HAS_GRADIENT_START
                        horizontalShift = m_GradientStart;
                    #endif
                    
                    //m_GradientCoefficient
                    color *= m_GradientCoefficient * abs(cos(((texCoord.x + texCoord.y) / 2.0) + horizontalShift)); 
                #endif
            #endif
        } else {
            #ifdef HAS_BACKGROUND_COLOR
                color = m_BackgroundColor;
            #endif
        }
    #endif
    
    return color;
}

void main() {
    vec4 baseColor;
    
    #ifdef HAS_COLORMAP
        baseColor = texture2D(m_ColorMap, texCoord);
        
        #ifdef HAS_ONLY_CHANGE_COLOR
            if (baseColor != m_OnlyChangeColor) {
                gl_FragColor = baseColor;
                return;
            }
        #endif
    #endif
    
    #ifndef HAS_COLORMAP
        baseColor = vec4(1.0, 1.0, 1.0, 1.0);
    #endif
    
    /*
    #ifdef HAS_OUTLINE_THICKNESS
        if (texCoord.x <= m_OutlineThickness || texCoord.y <= m_OutlineThickness || (1.0 - texCoord.x) <= m_OutlineThickness || (1.0 - texCoord.y) <= m_OutlineThickness) {
            #ifdef HAS_OUTLINE_COLOR
                gl_FragColor = m_OutlineColor;
            #endif
            
            #ifndef HAS_OUTLINE_COLOR
                gl_FragColor = baseColor;
            #endif
            
            return;
        }
    #endif
    */
    
    gl_FragColor = obtainColor(baseColor);
}