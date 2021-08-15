#import "Common/ShaderLib/GLSLCompat.glsllib"
#extension GL_EXT_gpu_shader4 : enable

uniform float g_Time;

uniform vec4 m_Color;
uniform sampler2D m_ColorMap;
uniform float m_SizeX;
uniform float m_SizeY;
uniform float m_Position;

uniform vec2 m_Pivot;
uniform float m_RotationSpeed;
uniform bool m_IsRotating;
uniform bool m_RotationUsesTime;
uniform float m_Angle;
 
in vec2 texCoord;

vec2 cellDimensionsPercent() {
    return vec2(1.0 / m_SizeX, 1.0 / m_SizeY);
}
 
void main(){
    if (m_IsRotating) {
        float t = m_Position;
        float tPointerY = 1.0 - ((floor(t / m_SizeX)) / m_SizeY) - 1.0 / m_SizeY;
        float tPointerYOffset = (floor(t / m_SizeX)) / m_SizeY;
        float tPointerX = (t - (tPointerYOffset * m_SizeX * m_SizeY)) / m_SizeX;
        
        vec2 cellDims = cellDimensionsPercent();
        if (texCoord.x < tPointerX || texCoord.y < tPointerY ||
            texCoord.x > tPointerX + cellDims.x || texCoord.y > tPointerY + cellDims.y
           ) {
            discard;
            return;
        }
        
        float angle;
        if (m_RotationUsesTime) {
            angle = g_Time * m_RotationSpeed;
        } else {
            angle = m_Angle;
        }
        
        float s = sin(angle);
        float c = cos(angle);
        
        mat2 rotationMatrix = mat2( c, -s,
                                    s,  c);
                                    
        vec2 pivot;
        #ifdef HAS_PIVOT
            pivot = m_Pivot;
        #else
            pivot = vec2(0.5, 0.5);
        #endif
        
        pivot /= vec2(m_SizeX, m_SizeY);
        pivot += vec2(tPointerX, tPointerY);

        texCoord = rotationMatrix * (texCoord - pivot) + pivot;
        
        if (texCoord.x < tPointerX || texCoord.y < tPointerY ||
            texCoord.x > tPointerX + cellDims.x || texCoord.y > tPointerY + cellDims.y
           ) {
            discard;
            return;
        }
    }
    
    vec4 color = texture2D(m_ColorMap, texCoord);
    
    #ifdef HAS_COLOR
        color *= m_Color;
    #endif
    
    gl_FragColor = color;
}