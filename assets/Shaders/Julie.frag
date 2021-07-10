#import "Common/ShaderLib/GLSLCompat.glsllib"

precision mediump float;

uniform sampler2D m_ColorMap;
uniform vec4 m_Color;

varying vec2 texCoord;

void main() {
    vec4 color;
    vec4 factorColor;
    
    #ifdef HAS_COLORMAP
        color = texture2D(m_ColorMap, texCoord);
    #else
        color = vec4(1.0, 1.0, 1.0, 1.0);
    #endif
    
    #ifdef HAS_COLOR
        factorColor = m_Color;
    #else
        factorColor = vec4(1.0, 1.0, 1.0, 1.0);
    #endif
    
    vec2 st = texCoord;
    
    float trigAvg = tan((st.x + st.y) / 2.0);
    
    vec4 colorA = vec4(
        ((st.x + st.y) / 2.0) - sin(trigAvg) + 0.35, //red
        sin(st.y) + st.x - trigAvg + sin(trigAvg) - cos(trigAvg), //green 
        sin(st.y) + st.y - trigAvg + cos(trigAvg) - sin(trigAvg), //blue
        1.0 //alpha
    ) * factorColor;
    
    vec4 colorB = vec4(sin(st.y) + st.x - trigAvg, ((st.x + st.y) / 2.0) - trigAvg, sin(st.y) + st.y - trigAvg, 1.0);
    vec4 colorC = vec4(sin(st.x) + st.y - trigAvg + 0.05, trigAvg - cos(st.y) + sin(st.x) + 0.05, sin(st.x) + tan(st.y) - trigAvg, 1.0) * factorColor;
    colorA = (3.0 * (colorA + colorB) + colorC) / 7.0;
    
    if (factorColor.b >= 0.9 && factorColor.r + factorColor.g <= 0.5) {
        colorA.b = (((st.x + st.y) / 2.0) + trigAvg + ((sin(st.y) + cos(st.x)) / 2.0)) / 3.0;
        colorA.r += (abs(sin(st.y)) * color.b / 0.9);
        colorA.g += (abs(cos(st.x)) * color.b / 0.9);
    }
    
    gl_FragColor = color * colorA;
}