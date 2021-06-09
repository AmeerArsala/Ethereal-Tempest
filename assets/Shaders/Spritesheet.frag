uniform vec4 m_Color;
uniform sampler2D m_ColorMap;
 
varying vec2 texCoord;
 
void main(){  
 
    vec4 color = texture2D(m_ColorMap, texCoord);
    
    #ifdef HAS_COLOR
        color *= m_Color;
    #endif
    
    gl_FragColor = color;
}