layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;


in VertexData
{
    vec2 texCoord;
    vec3 normal;
    uint textureId;
    uint rotation;
} vertex_in[];


out VertexDataPass
{
    vec2 texCoord;
    vec3 normal;
    vec3 texture_ratio;    
    flat vec3 textureIds;
    flat vec3 rotations;
} vertex_out;




void main() {
    
    for(int i = 0; i < gl_in.length(); i++)
    {
        gl_Position = gl_in[i].gl_Position;

        vertex_out.texCoord = vertex_in[i].texCoord;
        vertex_out.normal = vertex_in[i].normal;

        vertex_out.textureIds.x = vertex_in[0].textureId;
        vertex_out.textureIds.y = vertex_in[1].textureId;
        vertex_out.textureIds.z = vertex_in[2].textureId;

        vertex_out.rotations.x = vertex_in[0].rotation;
        vertex_out.rotations.y = vertex_in[1].rotation;
        vertex_out.rotations.z = vertex_in[2].rotation;        
   
        vertex_out.texture_ratio = vec3(0,0,0);
        vertex_out.texture_ratio[int(mod(i,3))] = 1;
 
        EmitVertex();
    }
    
    EndPrimitive();    
}
