#import "Common/ShaderLib/GLSLCompat.glsllib"

precision mediump float;

#if defined(HAS_GLOWMAP) || defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

#if defined(DISCARD_ALPHA)
    uniform float m_AlphaDiscardThreshold;
#endif

uniform float g_Time;

uniform vec4 m_Color;
uniform sampler2D m_ColorMap;
uniform sampler2D m_LightMap;

uniform float m_Frequency;
uniform float m_SideDisplacement;
uniform float m_Thickness;
uniform float m_CoefficientIncrement;
uniform float m_MinimumAmplitude;
uniform float m_MaximumAmplitude;
uniform float m_Amplitude;
uniform float m_RequiredOpacityToAnimate;

uniform vec4 m_SwishColor;

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;

vec4 diagonals(float frequency) {
    float focalValue = tan(frequency * g_Time);
    
    vec2 pos_mid = vec2(focalValue, focalValue);
    vec2 pixelPos = texCoord1;
    
    float deltaY = pixelPos.y - pos_mid.y;
    float deltaX = pixelPos.x - pos_mid.x;
    
    float deltaDiagonalX = (deltaX - deltaY) / 2.0;
    float deltaDiagonalY = (deltaX + deltaY) / 2.0;
    
    return vec4(vec2(deltaDiagonalX, deltaDiagonalY), vec2(deltaX, deltaY));
}

float calculateAmp(float min, float increment, float frequency) {
    vec4 deltas = diagonals(frequency);
    float deltaDiagonalY = deltas.g;
    return (min + (abs(deltaDiagonalY) * increment));
}

bool applicable(float frequency, vec2 min, vec2 max) {
    vec4 deltas = diagonals(frequency);
    
    float deltaX = deltas.b;
    float deltaY = deltas.a;
    float deltaDiagonalX = deltas.r;
    float deltaDiagonalY = deltas.g;
    
    return (deltaDiagonalX >= min.x && deltaDiagonalY >= min.y && deltaDiagonalX <= max.x && deltaDiagonalY <= max.y)
        || (deltaDiagonalX <= min.x && deltaDiagonalY >= min.y && deltaDiagonalX <= max.x && deltaDiagonalY <= max.y);
}

void main() {
    vec4 color = vec4(1.0);

    #ifdef HAS_COLORMAP
        color *= texture2D(m_ColorMap, texCoord1);     
    #endif

    #ifdef HAS_VERTEXCOLOR
        color *= vertColor;
    #endif

    #ifdef HAS_COLOR
        color *= m_Color;
    #endif

    #ifdef HAS_LIGHTMAP
        #ifdef SEPARATE_TEXCOORD
            color.rgb *= texture2D(m_LightMap, texCoord2).rgb;
        #else
            color.rgb *= texture2D(m_LightMap, texCoord1).rgb;
        #endif
    #endif

    #if defined(DISCARD_ALPHA)
        if(color.a < m_AlphaDiscardThreshold){
           discard;
        }
    #endif
    
    float frequency = 0.9;
    
    #ifdef HAS_FREQUENCY
        frequency = m_Frequency;
    #endif
    
    vec2 min = vec2(0.0, 0.0); //you can set the x value of this
    
    #ifdef HAS_SIDE_DISPLACEMENT
        min.x = m_SideDisplacement;
    #endif
    
    vec2 max = vec2(1.0, 0.25); //you can set the y value of this
    
    #ifdef HAS_THICKNESS
        max.y = m_Thickness;
    #endif
    
    float opacityRequirementForAnimation = 0.0;
    
    #ifdef HAS_OPACITY_REQUIREMENT
        opacityRequirementForAnimation = m_RequiredOpacityToAnimate;
    #endif
    
    if (applicable(frequency, min, max) && color.a > opacityRequirementForAnimation) {
        float amplitude;
        
        #ifdef SPECIFIES_AMPLITUDE
            amplitude = m_Amplitude;
        #else
            float minAmp = 1.1;
            
            #ifdef HAS_MINIMUM_AMPLITUDE
                minAmp = m_MinimumAmplitude;
            #endif
        
            float increment = 3.0;
            
            #ifdef HAS_INCREMENT
                increment = m_CoefficientIncrement;
            #endif
        
            amplitude = calculateAmp(minAmp, increment, frequency);
        #endif
        
        #ifdef SPECIFIES_SWISH_COLOR
            color = m_SwishColor;
        #endif
        
        color.rgb *= vec3(amplitude);
    }

    gl_FragColor = color;
}

