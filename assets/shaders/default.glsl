#type vertex
#version 460 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 3) in vec2 aTextureCoordinates;
layout (location = 4) in float aTextureID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTextureCoordinates;
out float fTextureID;

void main(){
    fColor = aColor;
    fTextureCoordinates = aTextureCoordinates;
    fTextureID = aTextureID;

    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 460 core

in vec4 fColor;
in vec2 fTextureCoordinates;
in float fTextureID;

uniform sampler2D uTextures[8];

out vec4 color;

void main(){
    if(fTextureID > 0){
        int id = int(fTextureID);
        color = fColor * texture(uTextures[id], fTextureCoordinates);
    } else {
        color = fColor;
    }
}
