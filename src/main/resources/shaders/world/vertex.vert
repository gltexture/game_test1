#version 430

layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;
layout (location=2) in vec3 vertex_normal;

struct PointLight
{
    float plPosX;
    float plPosY;
    float plPosZ;
    float plR;
    float plG;
    float plB;
    float brightness;
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[256];
};

layout (std140, binding = 3) uniform Misc {
    float w_tick;
};

out vec2 out_texture;
out vec3 mv_vertex_normal;
out vec3 mv_vert_pos;
out vec3 out_view_position;
out vec3 out_texture_3d;
out vec4 out_world_position;
out mat4 out_model_view_matrix;

uniform mat4 model_view_matrix;
uniform mat4 projection_matrix;
uniform mat4 model_matrix;

void main()
{
    vec4 mv_pos = model_view_matrix * vec4(position, 1.0f);
    gl_Position = projection_matrix * mv_pos;
    out_texture = texture;
    mv_vertex_normal = normalize(model_view_matrix * vec4(vertex_normal, 0.0f)).xyz;
    mv_vert_pos = mv_pos.xyz;

    out_world_position = model_matrix * vec4(position, 1.0);
    out_view_position = mv_pos.xyz;
    out_texture_3d = position;

    out_model_view_matrix = model_view_matrix;
}
