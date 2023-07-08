#version 330

in vec2 out_texture;
in vec3 mv_vertex_normal;
in vec3 mv_vert_pos;
out vec4 frag_color;

struct Attenuation {
    float constant;
    float linear;
    float exp;
};

struct PointLight {
    vec3 colour;
    vec3 pos;
    float intensity;
    Attenuation at;
};

struct SpotLight {
    vec3 cone;
    float cut;
    PointLight spl;
};

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct Material {
    vec4 ambient;
    vec4 specular;
    float reflect;
};

vec4 calc_light_colour(vec3, float, vec3, vec3, vec3);

uniform vec4 colour;
uniform sampler2D texture_sampler;
uniform int use_texture;

uniform vec3 ambient_light;
uniform float specular_power;
uniform Material material;
uniform PointLight point_light[64];
uniform DirectionalLight directional_light;
uniform SpotLight spot_light[64];
uniform vec3 camera_pos;
uniform int disable_light;

vec4 ambient_c;
vec4 diffuse_c;
vec4 specular_c;

void setup_colors(vec2 texture_c) {
    if (use_texture == 1) {
        ambient_c = texture(texture_sampler, texture_c);
        diffuse_c = ambient_c;
        specular_c = ambient_c;
    } else if (use_texture == -1) {
        ambient_c = vec4(1, 0, 1, 1);
        diffuse_c = ambient_c;
        specular_c = material.specular;
    } else {
        ambient_c = colour;
        diffuse_c = ambient_c;
        specular_c = material.specular;
    }
}

vec4 calc_point_light(PointLight light, vec3 pos, vec3 normal) {
    vec3 light_dir = light.pos - pos;
    vec3 to_light = normalize(light_dir);
    vec4 colour = calc_light_colour(light.colour, light.intensity, pos, to_light, normal);

    float dist = length(light_dir);
    float attenuation_inv = light.at.constant + light.at.linear * dist + light.at.exp * dist * dist;

    return colour / attenuation_inv;
}

vec4 calc_directional_light(DirectionalLight light, vec3 pos, vec3 normal) {
    return calc_light_colour(light.colour, light.intensity, pos, normalize(light.direction), normal);
}

vec4 calc_spot_light(SpotLight light, vec3 pos, vec3 normal) {
    vec3 light_direction = light.spl.pos - pos;
    vec3 to_light_dir = normalize(light_direction);
    vec3 from_light_dir = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.cone));

    vec4 color = vec4(0, 0, 0, 0);

    if (spot_alfa >= light.cut)
    {
        color = calc_point_light(light.spl, pos, normal);
        color *= (1.0f - (1.0f - spot_alfa) / (1.0f - light.cut));
    }

    return color;
}

void main()
{
    vec4 diffuse_spec_comp = vec4(0, 0, 0, 1);
    setup_colors(out_texture);

    for (int i = 0; i < 64; i++) {
        if (point_light[i].intensity > 0) {
            diffuse_spec_comp += calc_point_light(point_light[i], mv_vert_pos, mv_vertex_normal);
        }
    }

    for (int j = 0; j < 64; j++) {
        if (spot_light[j].spl.intensity > 0) {
            diffuse_spec_comp += calc_spot_light(spot_light[j], mv_vert_pos, mv_vertex_normal);
        }
    }

    diffuse_spec_comp += calc_directional_light(directional_light, mv_vert_pos, mv_vertex_normal);

    if (disable_light == 0) {
        frag_color = ambient_c * vec4(ambient_light, 1) + diffuse_spec_comp;
    } else {
        frag_color = ambient_c;
    }
}

vec4 calc_light_colour(vec3 light_colour, float intensity, vec3 pos, vec3 to_light, vec3 normal) {
    vec4 diffuse_colour = vec4(0, 0, 0, 0);
    vec4 spec_colour = vec4(0, 0, 0, 0);
    float diffuse_factor = max(dot(normal, to_light), 0.0f);
    diffuse_colour = diffuse_c * vec4(light_colour, 1.0f) * intensity * diffuse_factor;
    vec3 camera_direction = normalize(camera_pos - pos);
    vec3 from_light = -to_light;
    vec3 reflected_light = normalize(reflect(from_light, normal));
    float specular_factor = max(dot(camera_direction, reflected_light), 0.0f);
    specular_factor = pow(specular_factor, specular_power);
    spec_colour = specular_c * intensity * specular_factor * material.reflect * vec4(light_colour, 1.0f);
    return diffuse_colour + spec_colour;
}
