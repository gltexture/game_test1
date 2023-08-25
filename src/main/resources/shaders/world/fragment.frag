#version 430

in vec2 out_texture;
in vec3 mv_vertex_normal;
in vec3 mv_vert_pos;
out vec4 frag_color;

uniform vec3 quads_c1;
uniform vec3 quads_c2;
uniform int quads_scaling;

uniform vec4 object_rgb;
uniform sampler2D texture_sampler;
uniform int use_texture;

uniform int enable_light;
uniform vec3 camera_pos;

uniform vec2 dimensions;

struct PointLight
{
    float plPosX;
    float plPosY;
    float plPosZ;
    float plR;
    float plG;
    float plB;
    float brightness;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[256];
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

layout (std140, binding = 3) uniform Misc {
    float w_tick;
};

vec4 get_quads(vec2);
vec4 setup_colors(vec2);
vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light();

void main()
{
    vec4 lightFactor = enable_light == 1 ? calc_light() : vec4(1.);
    frag_color = setup_colors(out_texture) * lightFactor;
}

vec4 calc_light() {
    vec4 lightFactors = vec4(0.);
    vec4 calcSunFactor = calc_sun_light(vec3(sunX, sunY, sunZ), mv_vert_pos, mv_vertex_normal);
    lightFactors += calcSunFactor;

    for (int i = 0; i < p_l.length(); i++) {
        vec4 calcPointLightFactor = calc_point_light(p_l[i], mv_vert_pos, mv_vertex_normal);
        lightFactors += calcPointLightFactor;
    }

    lightFactors += ambient;
    return lightFactors;
}

vec4 setup_colors(vec2 texture_c) {
    int i1 = use_texture;
    return i1 == 0 ? texture(texture_sampler, texture_c) : i1 == 1 ? object_rgb : i1 == 2 ? get_quads(texture_c) : vec4(1., 0., 0., 1.);
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

    return diffuseC + specularC;
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal) {
    return calc_light_factor(vec3(1., 0.97, 0.94), sunBright, vPos, normalize(sunPos), vNormal);
}

vec4 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal) {
    float bright = light.brightness;
    float at_base = 1.8 / (bright * 0.5);
    float linear = 2.25 / (bright * 2.75);
    float expo = 0.6 / (bright * 0.25f);
    vec3 pos = vec3(light.plPosX, light.plPosY, light.plPosZ);

    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec4 light_c = calc_light_factor(vec3(light.plR, light.plG, light.plB), bright, vPos, to_light, vNormal);

    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
}

vec4 get_quads(vec2 texture_c) {
    vec4 c1 = vec4(quads_c1, 1);
    vec4 c2 = vec4(quads_c2, 1);
    vec2 v2 = texture_c;
    int i = int(v2.x * quads_scaling);
    int j = int(v2.y * quads_scaling);
    return ((i % 2 == 0) != (j % 2 == 0)) ? c1 : c2;
}