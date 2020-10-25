#import "Common/ShaderLib/GLSLCompat.glsllib"
#define M_PI 3.1415926535897932384626433832795

precision mediump float;

uniform sampler2DArray m_TileTexArray;
uniform vec4 m_Color;
uniform sampler2D m_BlendMap;
uniform float m_BlendAmplitude;

uniform int m_CurrentIndex;
uniform int m_TopIndex;
uniform int m_BottomIndex;
uniform int m_RightIndex;
uniform int m_LeftIndex;

varying vec2 texCoord;

// 2D Random
float random (in vec2 st) {
    return fract(sin(dot(st.xy,
                         vec2(12.9898,78.233)))
                 * 43758.5453123);
}

float varyBlendAmp(float t, float blendAmp) {
    float bigPortion = ((2.5 / 3.5) * (blendAmp));
    float smallPortion = blendAmp - bigPortion;
    
    return bigPortion * random(vec2(t)) + smallPortion;
}

vec4 getColor(int index) {
    return texture(m_TileTexArray, vec3(texCoord.x, texCoord.y, index));
}

void main() {
    vec4 color;
    
    float blendAmp = 0.15;
    #ifdef HAS_BLENDAMPLITUDE
        blendAmp = m_BlendAmplitude;
    #endif
    
    int topIndex = m_CurrentIndex;
    #ifdef HAS_TOP
        topIndex = m_TopIndex;
    #endif
    
    int bottomIndex = m_CurrentIndex;
    #ifdef HAS_BOTTOM
        bottomIndex = m_BottomIndex;
    #endif
    
    int leftIndex = m_CurrentIndex;
    #ifdef HAS_LEFT
        leftIndex = m_LeftIndex;
    #endif
    
    int rightIndex = m_CurrentIndex;
    #ifdef HAS_RIGHT
        rightIndex = m_RightIndex;
    #endif
    
    float xVariance, yVariance;
    
    #ifdef HAS_BLENDMAP
        xVariance = texture2D(m_BlendMap, texCoord).r;
        yVariance = texture2D(m_BlendMap, texCoord).g;
    #endif
    
    #ifndef HAS_BLENDMAP
        xVariance = varyBlendAmp(texCoord.x, blendAmp);
        yVariance = varyBlendAmp(texCoord.y, blendAmp);
    #endif
    
    bool top = (texCoord.y > (1 - xVariance)); //(texCoord.y >= (1 - blendAmp))
    bool bottom = (texCoord.y < xVariance); //(texCoord.y <= blendAmp)
    bool left = (texCoord.x < yVariance); //(texCoord.x <= blendAmp)
    bool right = (texCoord.x > (1 - yVariance)); //(texCoord.x >= (1 - blendAmp))
    
    if (top) { //top
        color = mix(getColor(topIndex), getColor(m_CurrentIndex), pow(texCoord.y, 2));
    } else if (bottom) { //bottom
        color = mix(getColor(bottomIndex), getColor(m_CurrentIndex), pow(texCoord.y, 2));
    } else if (left) { //left
        color = mix(getColor(leftIndex), getColor(m_CurrentIndex), pow(texCoord.x, 2));
    } else if (right) { //right
        color = mix(getColor(rightIndex), getColor(m_CurrentIndex), pow(texCoord.x, 2));
    } else { //regular
        color = getColor(m_CurrentIndex);
    }
    
    #ifdef HAS_COLOR
        color *= m_Color;
    #endif
    
    gl_FragColor = color;
}