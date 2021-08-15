#import "Common/ShaderLib/Skinning.glsllib"
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat3 g_NormalMatrix;

uniform mat4 g_WorldMatrix;
uniform mat4 g_ViewMatrix;
uniform mat4 g_ViewProjectionMatrix;
uniform vec3 g_CameraPosition;

uniform mat4 g_ProjectionMatrix;
uniform mat4 g_WorldViewMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec4 inTexCoord;
attribute float inSize;

varying vec3 normal;
varying vec2 texCoord;

void main(void)
{
   vec4 modelSpacePos = vec4(inPosition, 1.0);
   vec3 modelSpaceNorm = inNormal;
   
   // ** Added sections indicated with '**' comments

   // ** inTexCoord contains the real tex coord for the atlas
   // ** and the corner indicator
   texCoord = inTexCoord.zw;
   vec2 corner = inTexCoord.xy;

   // ** Project the model space position down the normal just 
   // ** slightly
   modelSpacePos.xyz += modelSpaceNorm * 0.1; 

   // ** original gl_Position calculation bypassed
   // gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
   // texCoord = inTexCoord;

   // ** #if block for position and normal calculation depending
   // ** on billboard type.
   //
   // For some reason, the non-parallel version is not working.
   // SCREEN_PARALLEL is good enough for my normal map generation
   // but will probably look strange for SSAO
   #define SCREEN_PARALLEL    
   #ifdef SCREEN_PARALLEL
        // Billboard corners are calculated in straight view
        // space and so will rotated to be parallel to the screen
        // even as the camera turns (which can be unnerving)
        vec3 wvPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
        wvPosition.x += (corner.x - 0.5) * inSize;
        wvPosition.y += (corner.y - 0.5) * inSize;
 
        gl_Position = g_ProjectionMatrix * vec4(wvPosition, 1.0);

        vec3 wvNormal = normalize(vec3(corner.x - 0.5, corner.y - 0.5, 0.5));     
    #else

        // Get the world position (not world view) because
        // billboarding will be done in world space
        vec4 wPosition = g_WorldMatrix * modelSpacePos; 

        // Calculate the screen parallel axis vectors
        vec3 dir = normalize(wPosition.xyz - g_CameraPosition);
        vec3 left = normalize(cross(dir, vec3(0.0, 1.0, 0.0)));
        vec3 up = normalize(cross(left, dir)); 
        vec3 billboardNormal = normalize(cross(left, up)); 

        // Move the corners out relative to our calculated
        // axes and scaled by inSize
        wPosition.xyz += left * (corner.x - 0.5) * inSize;
        wPosition.xyz += up * (corner.y - 0.5) * inSize;

        // Push it a little towards the camera (should maybe be a parameter)
        wPosition.xyz += billboardNormal * 0.5;

        // Calculate the world view position
        vec3 wvPosition = (g_ViewMatrix * wPosition).xyz; 

        gl_Position = g_ViewProjectionMatrix * wPosition;
 
        // Calculate a splayed set of normals based on the corner to simulate
        // curvature.  This allows the billboard to be lit no matter the
        // current direction.
        // Normal is calculated by mixing the real world-normal for the 
        // surface with the splayed normal.
        vec3 wNormal = (g_WorldMatrix * vec4(modelSpaceNorm, 0.0)).xyz * 0.1; 
        wNormal += left * (corner.x - 0.5);        
        wNormal += up * (corner.y - 0.5);
        wNormal += billboardNormal * 0.5;
 
        // Now convert the world normal to world view space               
        vec3 wvNormal = normalize((g_ViewMatrix * vec4(wNormal, 0.0)).xyz); 
        
    #endif
    
    
    normal = wvNormal;
}
