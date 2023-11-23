#version 430

in vec2 out_texture;
in vec3 mv_vertex_normal;
in vec3 mv_vert_pos;
in vec3 out_texture_3d;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

in vec3 out_view_position;
in vec4 out_world_position;
in mat4 out_model_view_matrix;
uniform vec3 camera_pos;
uniform vec2 dimensions;

uniform vec3 quads_c1;
uniform vec3 quads_c2;

uniform vec4 object_rgb;
uniform sampler2D texture_sampler;
uniform sampler2D normal_map;
uniform samplerCube cube_map_sampler;
uniform sampler2D shadow_map_sampler;

uniform vec2 texture_scaling;
uniform int use_texture;
uniform int use_normal_map;
uniform int enable_light;

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
vec4 setup_colors();
vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light();
float calc_shadows(vec4, int);
float calc_dist_proj_shadow(vec4, vec2, int);
float texture_proj(vec4, vec2, int);
vec3 calc_normal_map(vec3, mat4);
int calc_cascade_index();

struct CascadeShadow {
    mat4 projection_view_matrix;
    float split_distance;
};

uniform CascadeShadow CShadows[3];
uniform sampler2D shadowMap_0;
uniform sampler2D shadowMap_1;
uniform sampler2D shadowMap_2;

vec2 getVecTC() {
    return texture_scaling * out_texture;
}

void main()
{
    vec4 lightFactor = enable_light == 1 ? calc_light() : vec4(1.);
    vec4 fin_col = setup_colors() * lightFactor;
    frag_color = fin_col;

    float brightness = frag_color.r + frag_color.g + frag_color.b;
    bright_color = brightness >= 8.0 ? frag_color : vec4(0., 0., 0., 1.);
}

vec4 calc_light() {
    vec4 lightFactors = vec4(0.);
    vec4 calcSunFactor = calc_sun_light(vec3(sunX, sunY, sunZ), mv_vert_pos, mv_vertex_normal);

    int i = 0;
    while (p_l[i].brightness > 0) {
        PointLight p = p_l[i++];
        lightFactors += calc_point_light(p, mv_vert_pos, mv_vertex_normal);
    }

    lightFactors += calcSunFactor;
    lightFactors += ambient;
    return lightFactors;
}

vec3 calc_normal_map(vec3 vNorm, mat4 mvm) {
    vec3 normalMap = texture2D(normal_map, getVecTC()).rgb * 2.0 - 1.0;
    vec4 transformedNormal = mvm * vec4(normalMap, 0.0);
    vec3 correctedNormal = normalize(transformedNormal.xyz) + vNorm;
    return normalize(correctedNormal + vNorm);
}

int calc_cascade_index() {
    int cascadeIndex;
    cascadeIndex = int(out_view_position.z < CShadows[0].split_distance) + int(out_view_position.z < CShadows[1].split_distance);
    return cascadeIndex;
}

float calc_dist_proj_shadow(vec4 shadow_coord, vec2 offset, int idx) {
    float shadow = 1.0;
    float dist = texture(idx == 0 ? shadowMap_0 : idx == 1 ? shadowMap_1 : shadowMap_2, vec2(shadow_coord.xy + offset)).r;
    return shadow_coord.w > 0 && dist < shadow_coord.z - 0.0005 ? 0.25 : shadow;
}

float texture_proj(vec4 shadow_coord, vec2 offset, int idx) {
    return shadow_coord.z > -1.0 && shadow_coord.z < 1.0 ? calc_dist_proj_shadow(shadow_coord, offset, idx) : 1.0;
}

float calc_shadows(vec4 world_pos, int idx) {
    vec4 shadow_map_pos = CShadows[idx].projection_view_matrix * world_pos;
    float shadow = 1.0;
    vec4 shadow_coord = (shadow_map_pos / shadow_map_pos.w) * 0.5 + 0.5;
    shadow = texture_proj(shadow_coord, vec2(0), idx);
    if (shadow_map_pos.z > 1.0) {
        shadow = 1.0;
    }
    return shadow;
}

vec4 setup_colors() {
    int i1 = use_texture;
    return i1 == 0 ? texture2D(texture_sampler, getVecTC()) : i1 == 1 ? object_rgb : i1 == 2 ? get_quads(getVecTC()) : vec4(1., 0., 0., 1.);
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec3 new_normal = use_normal_map == 1 ? calc_normal_map(vNormal, out_model_view_matrix) : vNormal;
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(new_normal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(camera_pos - vPos);
    vec3 from_light = light_dir;
    vec3 reflectionF = normalize(from_light + camDir);
    specularF = max(dot(new_normal, reflectionF), 0.);
    specularF = pow(specularF, 10.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    vec4 reflected = texture(cube_map_sampler, reflectionF);

    return dot(vNormal, from_light) + 0.0001 >= 0 ? (diffuseC + specularC * reflected) : vec4(0.);
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
    int i = int(v2.x * 2);
    int j = int(v2.y * 2);
    return ((i % 2 == 0) != (j % 2 == 0)) ? c1 : c2;
}