#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;

varying vec2 texCoord;

void main() {
    texCoord = inTexCoord;
    
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    
    gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
}