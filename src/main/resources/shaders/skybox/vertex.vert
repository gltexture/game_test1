#version 430

layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

out vec2 out_texture;
uniform mat4 projection_matrix;
uniform mat4 model_view_matrix;

void main()
{
    gl_Position = projection_matrix * model_view_matrix * vec4(position, 1.0f);
    out_texture = texture;
}
