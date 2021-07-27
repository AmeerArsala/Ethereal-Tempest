#import "Common/ShaderLib/GLSLCompat.glsllib"

precision mediump float;

uniform sampler2D m_ColorMap;
uniform vec4 m_OnlyChangeColor; // example: if this is white, then it will only change the white pixels in the ColorMap
uniform vec4 m_Color;
uniform vec4 m_BaseColor;
uniform vec4 m_BackgroundColor;

uniform bool m_UsesGradient;
uniform float m_GradientCoefficient;
uniform float m_GradientStart;

uniform vec2 m_PercentFilled; //from 0.0 to 1.0
uniform vec2 m_PercentStart;  //from 0.0 to 1.0
uniform vec2 m_PercentEnd;    //from 0.0 to 1.0

varying vec2 texCoord;

bool isWithinBounds() {
    #ifndef HAS_PERCENT_FILLED
        return true;
    #endif
    
    if (texCoord.x >= m_PercentStart.x && texCoord.y >= m_PercentStart.y) {
        return texCoord.x <= m_PercentEnd.x && texCoord.y <= m_PercentEnd.y;
    }
    
    return false;
}

vec4 obtainColor(vec4 color) {
    #ifdef HAS_PERCENT_FILLED
        vec2 percentAreaLength = m_PercentEnd - m_PercentStart;
        vec2 currentActualPercent = m_PercentStart + (m_PercentFilled * percentAreaLength);
        if ((texCoord.x <= currentActualPercent.x && texCoord.y <= currentActualPercent.y) || !isWithinBounds()) {
            #ifdef HAS_BASECOLOR
                color = m_BaseColor;
            #endif
            
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
                if (baseColor.a == 0.0) {
                    discard;
                } else {
                    gl_FragColor = baseColor;
                }
                
                return;
            }
        #endif
    #endif
    
    #ifndef HAS_COLORMAP
        baseColor = vec4(1.0, 1.0, 1.0, 1.0);
    #endif
    
    gl_FragColor = obtainColor(baseColor);
}