#version 150

in vec3 Position;
in vec2 UV;
out vec2 texCoord;

void main(){
    float x = -1.0;
    float y = -1.0;
    if (Position.x > 0.001){
        x = 1.0;
    }
    if (Position.y > 0.001){
        y = 1.0;
    }
    gl_Position = vec4(Position.x, Position.y, 0, 1.0);
    texCoord = UV.xy;
}