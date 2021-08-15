#import "Common/ShaderLib/Skinning.glsllib"
attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;
attribute float inSize;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_ProjectionMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_ViewMatrix;

#import "MatDefs/TreeInstancing.glsllib"

varying vec2 texCoord;

#ifdef USE_WIND
  uniform float g_Time; 
#endif

#import "MatDefs/TreeWind.glsllib"

void main(){
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    vec3 modelSpaceNorm = inNormal;

   #ifdef NUM_BONES
       Skinning_Compute(modelSpacePos);
   #endif
 
    // ** Calculate in viewspace... the billboarding will crawl
    //    as the camera turns but it's fine for shadows and
    //    requires fewer transforms    
    vec4 wPos = worldMatrix * modelSpacePos;
    vec3 wvPosition = (g_ViewMatrix * wPos).xyz;
    
    // ** The normal in this case is really the axis 
    vec3 wvNormal = TransformNormal(modelSpaceNorm);
    
    #ifdef USE_WIND
        // Calculate the wind from the unprojected position so that
        // the whole leaf quad gets the same wind
        vec4 groundPos = worldMatrix * vec4(0.0, 0.0, 0.0, 1.0);
        float windStrength = 0.75;
        vec3 wind = calculateWind(groundPos.xyz, wPos.xyz - groundPos.xyz, windStrength);
        wvPosition += (g_ViewMatrix * vec4(wind, 0.0)).xyz;
    #endif    
 
    // ** Simple x,y inversion works for an orthogonal vector       
    vec3 offset = normalize(vec3(wvNormal.y, -wvNormal.x, 0.0));
    wvPosition += offset * inSize;
 
    // ** Now to projection space   
    gl_Position = g_ProjectionMatrix * vec4(wvPosition, 1.0);
  
    // ** old calculation replaced by the above
    // gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
    
    texCoord = inTexCoord;
}
