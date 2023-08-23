#version 430

in vec2 out_texture;
in vec3 mv_vertex_normal;
in vec3 mv_vert_pos;
in float ambient_light;
in vec3 sun_pos;
out vec4 frag_color;

uniform float tick;
uniform vec4 colors;
uniform sampler2D texture_sampler;
uniform int use_texture;
uniform int disable_light;
uniform vec3 camera_pos;
uniform vec2 dimensions;

vec4 setup_colors(vec2);
vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);

void main()
{
    vec4 lightFactors = vec4(1.);
    if (disable_light == 0) {
        lightFactors = vec4(0.);
        vec4 calcSunFactor = calc_sun_light(sun_pos, mv_vert_pos, mv_vertex_normal);
        lightFactors += calcSunFactor;
    }
    frag_color = setup_colors(out_texture) * lightFactors;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(vNormal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(camera_pos - vPos);
    vec3 from_light = -light_dir;
    vec3 reflectionF = normalize(reflect(from_light, vNormal));
    specularF = max(dot(camDir, reflectionF), 0.);
    specularF = pow(specularF, 32.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    return diffuseC + specularC + (ambient_light * 0.45f);
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal) {
    return calc_light_factor(vec3(1.), 1., vPos, normalize(sunPos), vNormal);
}

vec4 setup_colors(vec2 texture_c) {
    if (use_texture == 0) {
        return texture(texture_sampler, texture_c);
    } else if (use_texture == 1) {
        return colors;
    } else if (use_texture == 3) {
        vec4 white = vec4(0.6, 0.6, 0.6, 1);
        vec4 gray = vec4(0.2, 0.2, 0.2, 1);
        vec4 v1;
        vec2 v2 = texture_c.xy;
        int i = int(v2.x * 6);
        int j = int(v2.y * 6);
        if ((i % 2 == 0) != (j % 2 == 0)) {
            v1 = white;
        } else {
            v1 = gray;
        }
        return v1;
    } else {
        vec4 pink = vec4(1, 0, 1, 1);
        vec4 black = vec4(0, 0, 0, 1);
        vec4 v1;
        vec2 v2 = texture_c.xy;
        int i = int(v2.x * 8);
        int j = int(v2.y * 8);
        if ((i % 2 == 0) != (j % 2 == 0)) {
            v1 = pink;
        } else {
            v1 = black;
        }
        return v1;
    }
}
