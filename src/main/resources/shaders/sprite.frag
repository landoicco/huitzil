#version 330 core

in vec2 v_tc;

uniform sampler2D tex;

out vec4 color;

void main() {
    color = texture(tex, v_tc);
}
