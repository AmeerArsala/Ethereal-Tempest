#import "Common/ShaderLib/Skinning.glsllib"
attribute vec3 inPosition;
attribute vec2 inTexCoord;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_ViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_ViewMatrix;
uniform mat3 g_NormalMatrix;

#import "MatDefs/TreeInstancing.glsllib"

varying vec2 texCoord;

#ifdef USE_WIND
  uniform float g_Time; 
#endif

#import "MatDefs/TreeWind.glsllib"

void main(){
    vec4 modelSpacePos = vec4(inPosition, 1.0);
  
    #ifdef NUM_BONES
       Skinning_Compute(modelSpacePos);
    #endif
    
    #ifdef USE_WIND   
        // some simple wind
        float windStrength = 0.75;
    
        // Need to know the model's ground position for noise basis
        // otherwise the tree will warp all over the place and it
        // will look strange as the trunk stretches and shrinks.
        vec4 groundPos = worldMatrix * vec4(0.0, 0.0, 0.0, 1.0);
    
        // Wind is applied to world space   
        vec4 wPos = worldMatrix * modelSpacePos;
    
        // Note: if the model position we pass in is not rotated
        // then the radial turbulence is technically not correct.
        // But... it's quicker and it adds a little unplanned variance.
        // Note: that batching will have to do something special, though
        // instancing will be ok.
        wPos.xyz += calculateWind(groundPos.xyz, inPosition, windStrength);
 
        gl_Position = g_ViewProjectionMatrix * wPos;
    #else        
        gl_Position = g_ViewProjectionMatrix * (worldMatrix * modelSpacePos);
    #endif
   
    texCoord = inTexCoord;
}
