#version 430

in vec2 out_texture;
out vec4 frag_color;
uniform sampler2D texture_sampler;

vec4 negate_c(vec4);
vec4 gamma_cor(vec4, float);

void main()
{
    vec4 texture = texture(texture_sampler, out_texture);
    frag_color = gamma_cor(texture, 0.9);
}

vec4 gamma_cor(vec4 in_col, float gamma) {
    vec4 c = in_col;
    vec3 g_cor = pow(c.rgb, vec3(1.0 / gamma));
    return vec4(g_cor, c.a);
}

vec4 negate_c(vec4 in_col) {
    return vec4(1.) - in_col;
}