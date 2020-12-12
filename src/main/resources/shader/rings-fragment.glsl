#version 330

in vec3 f_uv;
in vec3 f_color;

out vec4 fragColor;

void main() {
    // fragColor = vec4(0.0, 0.5, 0.5, 1.0);
    float d = length(f_uv.xy - .5);
    if (d > .5) discard;
    fragColor = vec4(f_uv.xyx-.5, 1.);
    fragColor = vec4(f_color, 1);
}