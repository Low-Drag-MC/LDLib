#version 150

in vec3 Position;// base on normalized screen postion

out vec2 screenPos;// before modified by gui scale
out vec2 guiPos;// modified by gui scale
out vec2 uv;// normalized into [-1,1]

uniform float GuiScale;
uniform vec2 ScreenSize;

void main() {
    gl_Position = vec4(Position.xy, 0.0, 1.0);
    vec2 normalizedPos = Position.xy * 0.5 + 0.5;
    screenPos = ScreenSize * vec2(normalizedPos.x, 1-normalizedPos.y);
    guiPos = screenPos / GuiScale;
    uv = gl_Position.xy;
}