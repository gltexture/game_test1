#version 330

in vec2 out_texture;
in vec3 mv_vertex_normal;
in vec3 mv_vert_pos;
out vec4 frag_color;

uniform float tick;
uniform vec3 colors;
uniform sampler2D texture_sampler;
uniform int use_texture;
uniform vec3 camera_pos;
uniform vec2 dimensions;

vec4 setup_colors(vec2 texture_c) {
    if (use_texture == 0) {
        return texture(texture_sampler, texture_c);
    } else if (use_texture == 1) {
        return vec4(colors, 1.0f);
    } else if (use_texture == 2) {
        float f1 = tick;
        vec2 uv = gl_FragCoord.xy;
        float r = sin(f1 + cos(uv.x * uv.y)) * cos(length(texture_c) - sin(exp(texture_c.y)));
        float g = cos(f1 - sin(uv.x * uv.y)) * sin(length(texture_c) + cos(exp(texture_c.x)));
        float b = sin(f1 * sin(cos(r * g))) / cos(texture_c.x + uv.x * uv.y) / sin(exp(r) * tan(g));
        return vec4(r, g, b, 1);
    } else {
        vec4 pink = vec4(1, 0, 1, 1);
        vec4 black = vec4(0, 0, 0, 1);
        vec2 v2 = texture_c.xy;
        int i = int(v2.x * 8);
        int j = int(v2.y * 8);
        if ((i % 2 == 0) != (j % 2 == 0)) {
            return pink;
        } else {
            return black;
        }
    }
}

void main()
{
    frag_color = setup_colors(out_texture);
}
