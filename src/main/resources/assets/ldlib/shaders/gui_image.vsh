#version 150

in vec3 Position;
in vec2 UV;
out vec2 texCoord;

void main(){
    gl_Position = vec4(Position.x, Position.y, 0, 1.0);
    texCoord = UV.xy;
}