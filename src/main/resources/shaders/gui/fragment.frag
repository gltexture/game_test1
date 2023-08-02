#version 430

in vec2 out_texture;
out vec4 frag_color;

uniform vec4 colour;
uniform sampler2D texture_sampler;

void main()
{
    frag_color = colour * texture(texture_sampler, out_texture);
}
