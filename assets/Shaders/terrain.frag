#define M_PI 3.1415926535897932384626433832795

in VertexDataPass
{
    vec2 texCoord;
    vec3 normal;
    vec3 texture_ratio;
    flat vec3 textureIds;
    flat vec3 rotations;
} vertex_data;


uniform sampler2DArray m_AtlasArray;
 
out vec4 color;

vec4 GetColor(uint id, uint rotation, vec2 uv_coords) {
  
    float x = uv_coords.x;
    float y = uv_coords.y;
    
    //############# BEGIN Rotate UV coordinates around center ############
    float a = (M_PI /2.0) * float(rotation);    

    float cx = 0.5;
    float cy = 0.5;

    float s = sin(a);
    float c = cos(a);
   
    x -= cx;
    y -= cy;

    float xn = x * c - y * s;
    float yn = x * s + y * c;

    x = xn + cx;
    y = yn + cy;
    //############# END Rotate UV coordinates around center ############
    
    return texture(m_AtlasArray, vec3(x,y, float(id)));
}


void main(){
    
    vec2 uvs = vertex_data.texCoord;

    // Set fragment output color:
    color = 
        GetColor(uint(vertex_data.textureIds.x), uint(vertex_data.rotations.x), uvs) * vertex_data.texture_ratio.x +
        GetColor(uint(vertex_data.textureIds.y), uint(vertex_data.rotations.y), uvs) * vertex_data.texture_ratio.y +
        GetColor(uint(vertex_data.textureIds.z), uint(vertex_data.rotations.z), uvs) * vertex_data.texture_ratio.z;

}
