in vec2 texture_coordinates;
in vec3 mv_vertex_normal;
in vec3 mv_vert_pos;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

uniform vec3 camera_pos;
uniform sampler2D texture_sampler;

uniform int diffuse_mode;
uniform vec2 texture_scaling;
uniform samplerCube ambient_cubemap;
uniform vec4 diffuse_color;
uniform sampler2D diffuse_map;
uniform sampler2D normals_map;
uniform sampler2D emissive_map;
uniform sampler2D specular_map;
uniform sampler2D metallic_map;

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

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[1024];
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light();

vec2 scaled_coordinates() {
    return texture_coordinates * texture_scaling;
}

void main()
{
    vec4 diffuse_texture = texture(diffuse_map, scaled_coordinates());
    vec4 emissive_texture = texture(emissive_map, scaled_coordinates());
    vec4 diffuse = diffuse_mode == 1 ? diffuse_texture : diffuse_color;

    vec4 lightFactor = calc_light();
    vec4 final = diffuse * lightFactor;
    frag_color = final;

    float brightness = frag_color.r + frag_color.g + frag_color.b;
    float distance_to_tx = distance(mv_vert_pos, camera_pos);

    brightness *= distance_to_tx <= 30. ? 1. : (1. - smoothstep(0., 1., min(distance_to_tx / 90., 1.)));

    bright_color = brightness >= 8. ? frag_color : vec4(0., 0., 0., 1.);
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

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec3 new_normal = vNormal;
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(new_normal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(camera_pos - vPos);
    vec3 from_light = light_dir;
    vec3 reflectionF = normalize(from_light + camDir);
    specularF = max(dot(new_normal, reflectionF), 0.);
    specularF = pow(specularF, 16.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    return dot(vNormal, from_light) + 0.0001 >= 0 ? (diffuseC + specularC) : vec4(0.);
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