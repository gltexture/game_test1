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

vec2 mn(vec2 z) {
    return vec2(z.x * z.x - z.y * z.y, 2.0 * z.x * z.y);
}

float f(vec2 z) {
    float f1 = tick * 0.5f;
    float zoom = (0.75 + (sin(f1) + 0.2f) * 0.75f) * 10.;
    vec2 c = 4. * z - vec2(0.5, 0.);
    c /= zoom * zoom * zoom * zoom;
    c -= vec2(0.65, 0.45);
    vec2 s = vec2(0, 0);
    int id = 0;
    const int m = 128;
    for (int i = 0; i < m; i++) {
        s = mn(s) + c;
        if (length(s) > 4.) {
            return float(id) / float(m);
        }
        id += 1;
    }
    return 0.0;
}

vec4 setup_colors(vec2 texture_c) {
    if (use_texture == 0) {
        return texture(texture_sampler, texture_c);
    } else if (use_texture == 1) {
        return vec4(colors, 1.0f);
    } else if (use_texture == 2) {
        vec2 uv = (2. * texture_c.xy - 0.5);
        vec3 color = vec3(0);
        float res = f(uv);
        float f1 = tick * 0.1f;

        float r = fract(cos(res + f1) * 12.25);
        float g = fract(sin(r + res + f1) * 4.125);
        float b = fract(cos(r + g + f1) * 2.5);

        color += vec3(r, g, b);
        color = pow(color, vec3(0.75));

        if (res <= 0) {
            color = vec3(0.);
        }

        return vec4(color, 1);
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
