#version 430

in vec2 out_texture;
in float ambientLight;
out vec4 frag_color;
uniform sampler2D texture_sampler;

void main()
{
    frag_color = vec4(vec3(ambientLight), 1) * texture(texture_sampler, out_texture);
}
