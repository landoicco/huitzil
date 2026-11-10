#version 330 core

in vec2 v_tc;

uniform sampler2D tex;
// Multiplies the texture color; white leaves it unchanged
uniform vec3 tint;

out vec4 color;

void main() {
    vec4 texel = texture(tex, v_tc);
    color = vec4(texel.rgb * tint, texel.a);
}
