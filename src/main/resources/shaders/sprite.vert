#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;

uniform mat4 pr_matrix;
uniform mat4 ml_matrix;

// Which part of the texture to show: lets one quad draw sprite sheet frames,
// flipped sprites and a scrolling background
uniform vec2 uv_offset;
uniform vec2 uv_scale;

out vec2 v_tc;

void main() {
    gl_Position = pr_matrix * ml_matrix * position;
    v_tc = uv_offset + tc * uv_scale;
}
