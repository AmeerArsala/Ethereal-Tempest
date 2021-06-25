#import "Common/ShaderLib/GLSLCompat.glsllib"

precision mediump float;

uniform sampler2D m_ColorMap;
uniform vec4 m_Color;
uniform vec4 m_DiscardColor;
uniform float m_MaxAlphaDiscard;

varying vec2 texCoord;

void main() {
    vec4 color;
    
    #ifdef HAS_COLORMAP
        color = texture2D(m_ColorMap, texCoord);
    #else
        color = vec4(1.0, 1.0, 1.0, 1.0);
    #endif
    
    #ifdef HAS_COLOR
        color *= m_Color;
    #endif
    
    #ifdef HAS_DISCARDCOLOR
        if (color == m_DiscardColor) {
            discard;
            return;
        }
    #endif
    
    #ifdef HAS_MAX_ALPHA_DISCARD
        if (color.a <= m_MaxAlphaDiscard) {
            discard;
            return;
        }
    #endif
    
    gl_FragColor = color;
}