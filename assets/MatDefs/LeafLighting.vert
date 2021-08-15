#define ATTENUATION
//#define HQ_ATTENUATION

#import "Common/ShaderLib/Skinning.glsllib"

#import "MatDefs/VertScattering.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;
uniform mat4 g_ViewProjectionMatrix;
uniform vec3 g_CameraPosition;

#import "MatDefs/TreeInstancing.glsllib"

uniform vec4 m_Ambient;
uniform vec4 m_Diffuse;
uniform vec4 m_Specular;
uniform float m_Shininess;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

varying vec2 texCoord;
#ifdef SEPARATE_TEXCOORD
  varying vec2 texCoord2;
  attribute vec2 inTexCoord2;
#endif

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

attribute vec3 inPosition;
attribute vec4 inTexCoord;
attribute vec3 inNormal;
attribute float inSize;

varying vec3 lightVec;
//varying vec4 spotVec;

// Used for the alpha darkening 
varying float vDistance;

#ifdef USE_WIND
  uniform float g_Time; 
#endif

#import "MatDefs/TreeWind.glsllib"

#ifdef VERTEX_COLOR
  attribute vec4 inColor;
#endif

#ifndef VERTEX_LIGHTING
  attribute vec4 inTangent;

  #ifndef NORMALMAP
    varying vec3 vNormal;
  #endif
  //varying vec3 vPosition;
  varying vec3 vViewDir;
  varying vec4 vLightDir;
#else
  varying vec2 vertexLightValues;
  uniform vec4 g_LightDirection;
#endif

#ifdef USE_REFLECTION
    uniform vec3 g_CameraPosition;
    uniform mat4 g_WorldMatrix;

    uniform vec3 m_FresnelParams;
    varying vec4 refVec;


    /**
     * Input:
     * attribute inPosition
     * attribute inNormal
     * uniform g_WorldMatrix
     * uniform g_CameraPosition
     *
     * Output:
     * varying refVec
     */
    void computeRef(in vec4 modelSpacePos){
        vec3 worldPos = (g_WorldMatrix * modelSpacePos).xyz;

        vec3 I = normalize( g_CameraPosition - worldPos  ).xyz;
        vec3 N = normalize( (g_WorldMatrix * vec4(inNormal, 0.0)).xyz );

        refVec.xyz = reflect(I, N);
        refVec.w   = m_FresnelParams.x + m_FresnelParams.y * pow(1.0 + dot(I, N), m_FresnelParams.z);
    }
#endif

// JME3 lights in world space
void lightComputeDir(in vec3 worldPos, in vec4 color, in vec4 position, out vec4 lightDir){
    float posLight = step(0.5, color.w);
    vec3 tempVec = position.xyz * sign(posLight - 0.5) - (worldPos * posLight);
    lightVec = tempVec;  
    #ifdef ATTENUATION
     float dist = length(tempVec);
     lightDir.w = clamp(1.0 - position.w * dist * posLight, 0.0, 1.0);
     lightDir.xyz = tempVec / vec3(dist);
    #else
     lightDir = vec4(normalize(tempVec), 1.0);
    #endif
}

#ifdef VERTEX_LIGHTING
  float lightComputeDiffuse(in vec3 norm, in vec3 lightdir){
      return max(0.0, dot(norm, lightdir));
  }

  float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
      if (shiny <= 1.0){
          return 0.0;
      }
      #ifndef LOW_QUALITY
        vec3 H = (viewdir + lightdir) * vec3(0.5);
        return pow(max(dot(H, norm), 0.0), shiny);
      #else
        return 0.0;
      #endif
  }

vec2 computeLighting(in vec3 wvPos, in vec3 wvNorm, in vec3 wvViewDir, in vec4 wvLightPos){
     vec4 lightDir;
     lightComputeDir(wvPos, g_LightColor, wvLightPos, lightDir);
     float spotFallOff = 1.0;
     if(g_LightDirection.w != 0.0){
          vec3 L=normalize(lightVec.xyz);
          vec3 spotdir = normalize(g_LightDirection.xyz);
          float curAngleCos = dot(-L, spotdir);    
          float innerAngleCos = floor(g_LightDirection.w) * 0.001;
          float outerAngleCos = fract(g_LightDirection.w);
          float innerMinusOuter = innerAngleCos - outerAngleCos;
          spotFallOff = clamp((curAngleCos - outerAngleCos) / innerMinusOuter, 0.0, 1.0);
     }
     float diffuseFactor = lightComputeDiffuse(wvNorm, lightDir.xyz);
     float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, lightDir.xyz, m_Shininess);
     //specularFactor *= step(0.01, diffuseFactor);
     return vec2(diffuseFactor, specularFactor) * vec2(lightDir.w)*spotFallOff;
  }
#endif

void main(){
   vec4 modelSpacePos = vec4(inPosition, 1.0);
   vec3 modelSpaceNorm = inNormal;
   
   #ifndef VERTEX_LIGHTING
        vec3 modelSpaceTan  = inTangent.xyz;
   #endif

   #ifdef NUM_BONES
        #ifndef VERTEX_LIGHTING
        Skinning_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
        #else
        Skinning_Compute(modelSpacePos, modelSpaceNorm);
        #endif
   #endif

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
   #ifdef SCREEN_PARALLEL
        // Billboard corners are calculated in straight view
        // space and so will rotated to be parallel to the screen
        // even as the camera turns (which can be unnerving)
        vec3 wvPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
        wvPosition.x += (corner.x - 0.5) * inSize;
        wvPosition.y += (corner.y - 0.5) * inSize;
 
        vDistance = length(wvPosition);
    
        gl_Position = g_ProjectionMatrix * vec4(wvPosition, 1.0);

        vec3 wvNormal = normalize(vec3(corner.x - 0.5, corner.y - 0.5, 0.5));     
    #else

        // Get the world position (not world view) because
        // billboarding will be done in world space
        vec4 wPosition = worldMatrix * modelSpacePos; 

        #ifdef USE_WIND
            // Calculate the wind from the unprojected position so that
            // the whole leaf quad gets the same wind
            vec4 groundPos = worldMatrix * vec4(0.0, 0.0, 0.0, 1.0);
            float windStrength = 0.75;
            vec3 wind = calculateWind(groundPos.xyz, wPosition.xyz - groundPos.xyz, windStrength);
            wPosition.xyz += wind;
        #endif

        // Calculate the screen parallel axis vectors
        vec3 cameraOffset = wPosition.xyz - g_CameraPosition;
        vDistance = length(cameraOffset);        
        vec3 dir = cameraOffset/vDistance; //normalize(wPosition.xyz - g_CameraPosition);
        vec3 left = normalize(cross(dir, vec3(0.0, 1.0, 0.0)));
        vec3 up = normalize(cross(left, dir)); 
        vec3 billboardNormal = normalize(cross(left, up)); 

        // Move the corners out relative to our calculated
        // axes and scaled by inSize
        wPosition.xyz += left * (corner.x - 0.5) * inSize;
        wPosition.xyz += up * (corner.y - 0.5) * inSize;

        // Push it a little towards the camera (should maybe be a parameter)
        wPosition.xyz += billboardNormal * 0.5;

        #ifdef USE_SCATTERING
            calculateVertexGroundScattering(wPosition.xyz, g_CameraPosition);
        #endif

        // Calculate the world view position
        vec3 wvPosition = (g_ViewMatrix * wPosition).xyz; 

        gl_Position = g_ViewProjectionMatrix * wPosition;
 
        // Calculate a splayed set of normals based on the corner to simulate
        // curvature.  This allows the billboard to be lit no matter the
        // current direction.
        // Normal is calculated by mixing the real world-normal for the 
        // surface with the splayed normal.
        vec3 wNormal = (worldMatrix * vec4(modelSpaceNorm, 0.0)).xyz * 0.1; 
        wNormal += left * (corner.x - 0.5);        
        wNormal += up * (corner.y - 0.5);
        wNormal += billboardNormal * 0.5;
 
        // Now convert the world normal to world view space               
        vec3 wvNormal = normalize((g_ViewMatrix * vec4(wNormal, 0.0)).xyz); 
        
    #endif
   
   
   #ifdef SEPARATE_TEXCOORD
      texCoord2 = inTexCoord2;
   #endif

   // ** these calculations have been superceded by the above
   // vec3 wvPosition = (g_WorldViewMatrix * modelSpacePos).xyz;
   // vec3 wvNormal  = normalize(g_NormalMatrix * modelSpaceNorm);
   
   vec3 viewDir = normalize(-wvPosition);
  
       //vec4 lightColor = g_LightColor[gl_InstanceID];
       //vec4 lightPos   = g_LightPosition[gl_InstanceID];
       //vec4 wvLightPos = (g_ViewMatrix * vec4(lightPos.xyz, lightColor.w));
       //wvLightPos.w = lightPos.w;

   vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
   wvLightPos.w = g_LightPosition.w;
   vec4 lightColor = g_LightColor;

   #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
     vec3 wvTangent = normalize(TransformNormal(modelSpaceTan));
     vec3 wvBinormal = cross(wvNormal, wvTangent);

     mat3 tbnMat = mat3(wvTangent, wvBinormal * -inTangent.w,wvNormal);
     
     //vPosition = wvPosition * tbnMat;
     //vViewDir  = viewDir * tbnMat;
     vViewDir  = -wvPosition * tbnMat;
     lightComputeDir(wvPosition, lightColor, wvLightPos, vLightDir);
     vLightDir.xyz = (vLightDir.xyz * tbnMat).xyz;
   #elif !defined(VERTEX_LIGHTING)
     vNormal = wvNormal;

     //vPosition = wvPosition;
     vViewDir = viewDir;

     lightComputeDir(wvPosition, lightColor, wvLightPos, vLightDir);

     #ifdef V_TANGENT
        vNormal = normalize(TransformNormal(inTangent.xyz));
        vNormal = -cross(cross(vLightDir.xyz, vNormal), vNormal);
     #endif
   #endif

   //computing spot direction in view space and unpacking spotlight cos
//   spotVec = (g_ViewMatrix * vec4(g_LightDirection.xyz, 0.0) );
//   spotVec.w  = floor(g_LightDirection.w) * 0.001;
//   lightVec.w = fract(g_LightDirection.w);

   lightColor.w = 1.0;
   #ifdef MATERIAL_COLORS
      AmbientSum  = (m_Ambient  * g_AmbientLightColor).rgb;
      DiffuseSum  =  m_Diffuse  * lightColor;
      SpecularSum = (m_Specular * lightColor).rgb;
    #else
      AmbientSum  = vec3(0.2, 0.2, 0.2) * g_AmbientLightColor.rgb; // Default: ambient color is dark gray
      DiffuseSum  = lightColor;
      SpecularSum = vec3(0.0);
    #endif

    #ifdef VERTEX_COLOR
      AmbientSum *= inColor.rgb;
      DiffuseSum *= inColor;
    #endif

    #ifdef VERTEX_LIGHTING
       vertexLightValues = computeLighting(wvPosition, wvNormal, viewDir, wvLightPos);
    #endif

    #ifdef USE_REFLECTION
        computeRef(modelSpacePos);
    #endif 
}
