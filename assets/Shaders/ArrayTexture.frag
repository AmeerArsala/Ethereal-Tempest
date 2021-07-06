#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform sampler2DArray m_TexArray;
uniform int m_TexArrayIndex;
uniform vec4 m_Color;

varying vec2 texCoord;

vec4 getColorFromArray(int index) {
    return texture(m_TexArray, vec3(texCoord.x, texCoord.y, index));
}

void main() {
    vec4 color = vec4(1.0, 1.0, 1.0, 1.0);
    
    #ifdef HAS_ARRAY
        #ifdef HAS_INDEX
            color = getColorFromArray(m_TexArrayIndex);
        #endif
    #endif
    
    #ifdef HAS_COLOR
        color *= m_Color;
    #endif
    
    gl_FragColor = color;
}