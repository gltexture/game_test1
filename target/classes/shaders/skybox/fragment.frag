#version 430

in vec2 out_texture;
out vec4 frag_color;
uniform sampler2D texture_sampler;
uniform int use_texture;

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

void main()
{
    frag_color = vec4(texture(texture_sampler, out_texture)) * sunBright;
}
