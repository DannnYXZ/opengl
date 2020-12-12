#version 330
#define PI 3.14159265359

layout (location=0) in vec3 quad_vertex;
layout (location=1) in vec3 i_uv;
layout (location=2) in float ring_thickness;
layout (location=3) in float slice_radius;
layout (location=4) in float rings_total;
layout (location=5) in mat4 slice_model;
layout (location=9) in vec3 color;
// projection * view * model
uniform mat4 transform;

out vec3 f_uv;
out vec3 f_color;

void main() {

    vec3 pos = quad_vertex;
    pos *= ring_thickness;
    float ang = float(gl_InstanceID)/rings_total * 2 * PI;
    pos += slice_radius * vec3(1, 0, 0); // move to the right
    pos = mat3(
         cos(ang), 0, sin(ang),
                0, 1, 0,
        -sin(ang), 0, cos(ang)
    ) * pos;
    vec4 pos4 = vec4(pos, 1);
//    // gl_Position = vec4(position, 1);

    f_uv = i_uv;
    f_color = color;

    gl_Position = transform * slice_model* pos4;
}