uniform mat4 g_WorldViewProjectionMatrix;

uniform sampler2D m_TextureIdMap;
uniform float m_cellSize;

in vec3 inNormal;
in vec3 inPosition;
in vec2 inTexCoord;


out vec2 pass_uv_coords;

out VertexData
{
    vec2 texCoord;
    vec3 normal;
    uint textureId;
    uint rotation;
} vertex_out;


uint textureId;

void main(){
  
    float cx = round(inPosition.x / m_cellSize);
    float cz = round(inPosition.z / m_cellSize);
   
    vec4 color = texelFetch(m_TextureIdMap, ivec2(cx , cz ),0);

    vertex_out.textureId = uint(color.r * 255);
    vertex_out.rotation  = uint(color.g * 255);
   
    vertex_out.texCoord = inTexCoord;
    vertex_out.normal = inNormal;
   
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}