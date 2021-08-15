#import "Common/ShaderLib/Skinning.glsllib"
attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;
attribute float inSize;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_ViewMatrix;
uniform mat4 g_ViewProjectionMatrix;
uniform mat4 g_ProjectionMatrix;
uniform mat3 g_NormalMatrix;
uniform vec3 g_CameraPosition;

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

    #ifdef WORLDSPACE 
        vec4 wPosition = g_WorldMatrix * modelSpacePos;
        vec4 groundPosition = g_WorldMatrix * vec4(0.0, 0.0, 0.0, 1.0);
        vec3 wNormal = (g_WorldMatrix * vec4(0.0, 1.0, 0.0, 0.0)).xyz; 
        vec3 dir = normalize(wPosition.xyz - g_CameraPosition);
    
        vec3 offset = normalize(cross(dir, wNormal));
        wPosition.xyz += offset * inSize;
    
        gl_Position = g_ViewProjectionMatrix * wPosition;
        
        vec3 groundDir = groundPosition.xyz - g_CameraPosition;
        groundDir.y = 0.0;
        
        // rotate the ground direction based on the current rotation
        // of the model
        groundDir = (g_WorldMatrix * vec4(groundDir, 0.0)).xyz;
         
        float x = step(0.0, groundDir.x);
        float z = step(0.0, groundDir.z);
 
        float uBase = (z * 0.5) + (x * 0.25); 
        texCoord.x = uBase + texCoord.x * 0.25;        
    #else
        // ** Calculate in viewspace... the billboarding will crawl
        //    as the camera turns but it's fine for shadows and
        //    requires fewer transforms    
        vec3 wvPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
    
        // ** The normal in this case is really the axis 
        //vec3 wvNormal = g_NormalMatrix * modelSpaceNorm;
        vec3 wvNormal = g_NormalMatrix * vec3(0.0, 1.0, 0.0);
 
        #ifdef USE_WIND
            // Calculate the wind from the unprojected position so that
            // the whole leaf quad gets the same wind
            vec4 wPos = g_WorldMatrix * modelSpacePos;
            
            vec4 flatGroundPosition = g_WorldMatrix * modelSpacePos;
 
            float windStrength = 0.75;
            vec3 localPos = vec3(inSize, abs(inSize) * texCoord.y * 2.0, 0.0);
            vec3 wind = calculateImpostorWind(flatGroundPosition.xyz, localPos, windStrength);
            wvPosition += (g_ViewMatrix * vec4(wind, 0.0)).xyz;
        #endif    
 
        // ** Simple x,y inversion works for an orthogonal vector       
        vec3 offset = normalize(vec3(wvNormal.y, -wvNormal.x, 0.0));
        wvPosition += offset * inSize;
 
        // For shadows, back the top away from us
        wvPosition.z -= abs(inSize) * inTexCoord.y * 2.0;
 
        // ** Now to projection space   
        gl_Position = g_ProjectionMatrix * vec4(wvPosition, 1.0);
  
        // ** old calculation replaced by the above
        // gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
        
        texCoord = inTexCoord;
        texCoord.x = texCoord.x * 0.25;
    #endif
}
