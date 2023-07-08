#version 330

in vec2 out_texture;
out vec4 frag_color;

uniform vec3 ambient;
uniform sampler2D texture_sampler;

void main()
{
    frag_color = vec4(ambient, 1) * texture(texture_sampler, out_texture);
}
