#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;
layout (location=2) in vec3 vertex_normal;

out vec2 out_texture;
out vec3 mv_vertex_normal;
out vec3 mv_vert_pos;

uniform mat4 model_view_matrix;
uniform mat4 projection_matrix;

void main()
{
    vec4 mv_pos = model_view_matrix * vec4(position, 1.0f);
    gl_Position = projection_matrix * mv_pos;
    out_texture = texture;
    mv_vertex_normal = normalize(model_view_matrix * vec4(vertex_normal, 0.0f)).xyz;
    mv_vert_pos = mv_pos.xyz;
}
