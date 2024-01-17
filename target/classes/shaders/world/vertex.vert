layout (location=0) in vec3 Aposition;
layout (location=1) in vec2 Atexture;
layout (location=2) in vec3 Avertex_normal;
layout (location=3) in vec2 Atangent;
layout (location=4) in vec3 Abitangent;

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
    PointLight p_l[1024];
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

out vec2 texture_coordinates;
out vec3 mv_vertex_normal;
out vec3 mv_vert_pos;

uniform mat4 model_view_matrix;
uniform mat4 projection_matrix;
uniform mat4 model_matrix;

void main()
{
    vec4 mv_pos = model_view_matrix * vec4(Aposition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    texture_coordinates = Atexture;
    mv_vertex_normal = normalize(model_view_matrix * vec4(Avertex_normal, 0.0f)).xyz;
    mv_vert_pos = mv_pos.xyz;
}
