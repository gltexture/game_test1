#version 430

in vec2 out_texture;
in float ambient_light;
out vec4 frag_color;
uniform sampler2D texture_sampler;
uniform int use_texture;

void main()
{
    frag_color = vec4(texture(texture_sampler, out_texture)) * ambient_light;
}
