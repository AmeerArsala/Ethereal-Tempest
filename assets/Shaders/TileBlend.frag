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

uniform int m_CurrentPriority;
uniform int m_TopPriority;
uniform int m_BottomPriority;
uniform int m_RightPriority;
uniform int m_LeftPriority;

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

vec4 getColor(int index, vec2 coord) {
    return texture(m_TileTexArray, vec3(coord, index));
}

//the lower it is from 0.5, the more the first color dominates
float getAlpha(int priority, int comparedPriority) { 
    if (priority == 0) {
        return 0.5; //no blending
    }
    
    if (priority * -1.0 == comparedPriority) {
        return 0.375;
    }
    
    if (priority < comparedPriority) {
        return 0.9; //mostly give itself over to the compared texture
    }
    
    if (priority > comparedPriority) {
        return 0.0; //completely dominate the compared texture
    }
    
    return 0.5; // priority == comparedPriority
}

float getTopAlpha(bool usesPriority) {
    if (!usesPriority) {
        return 0.375;
    }
    
    return getAlpha(m_CurrentPriority, m_TopPriority);
}

float getBottomAlpha(bool usesPriority) {
    if (!usesPriority) {
        return 0.375;
    }
    
    return getAlpha(m_CurrentPriority, m_BottomPriority);
}

float getRightAlpha(bool usesPriority) {
    if (!usesPriority) {
        return 0.375;
    }
    
    return getAlpha(m_CurrentPriority, m_RightPriority);
}

float getLeftAlpha(bool usesPriority) {
    if (!usesPriority) {
        return 0.375;
    }
    
    return getAlpha(m_CurrentPriority, m_LeftPriority);
}

void main() {
    vec4 color;
    
    float blendAmp;
    #ifdef HAS_BLENDAMPLITUDE
        blendAmp = m_BlendAmplitude;
    #else
        blendAmp = 0.15;
    #endif
    
    int topIndex;
    #ifdef HAS_TOP
        topIndex = m_TopIndex;
    #else
        topIndex = m_CurrentIndex;
    #endif
    
    int bottomIndex;
    #ifdef HAS_BOTTOM
        bottomIndex = m_BottomIndex;
    #else
        bottomIndex = m_CurrentIndex;
    #endif
    
    int leftIndex;
    #ifdef HAS_LEFT
        leftIndex = m_LeftIndex;
    #else
        leftIndex = m_CurrentIndex;
    #endif
    
    int rightIndex;
    #ifdef HAS_RIGHT
        rightIndex = m_RightIndex;
    #else
        rightIndex = m_CurrentIndex;
    #endif
    
    vec2 variance;
    #ifdef HAS_BLENDMAP
        variance = texture2D(m_BlendMap, texCoord).rg;
    #else
        variance = vec2(varyBlendAmp(texCoord.x, blendAmp), varyBlendAmp(texCoord.y, blendAmp));
    #endif
    
    bool top =    (topIndex != m_CurrentIndex)    && (texCoord.y > (1.0 - variance.y)); //(texCoord.y >= (1 - blendAmp))
    bool bottom = (bottomIndex != m_CurrentIndex) && (texCoord.y < variance.y);         //(texCoord.y <= blendAmp)
    bool left =   (leftIndex != m_CurrentIndex)   && (texCoord.x < variance.x);         //(texCoord.x <= blendAmp)
    bool right =  (rightIndex != m_CurrentIndex)  && (texCoord.x > (1.0 - variance.y)); //(texCoord.x >= (1 - blendAmp))
    
    bool usesPriority;
    
    #ifdef USES_PRIORITY
        usesPriority = true;
    #else
        usesPriority = false;
    #endif
    
    if (top) { //top
        color = mix(getColor(m_CurrentIndex), getColor(topIndex, vec2(texCoord.x, 1.0 - texCoord.y)), getTopAlpha(usesPriority));
    } else if (bottom) { //bottom
        color = mix(getColor(m_CurrentIndex), getColor(bottomIndex, vec2(texCoord.x, 1.0 - texCoord.y)), getBottomAlpha(usesPriority));
    } else if (left) { //left
        color = mix(getColor(m_CurrentIndex), getColor(leftIndex, vec2(1.0 - texCoord.x, texCoord.y)), getLeftAlpha(usesPriority));
    } else if (right) { //right
        color = mix(getColor(m_CurrentIndex), getColor(rightIndex, vec2(1.0 - texCoord.x, texCoord.y)), getRightAlpha(usesPriority));
    } else { //regular
        color = getColor(m_CurrentIndex);
    }
    
    #ifdef HAS_COLOR
        color *= m_Color;
    #endif
    
    gl_FragColor = color;
}