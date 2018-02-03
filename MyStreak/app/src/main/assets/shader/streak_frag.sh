#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
uniform vec4 lineColor;//纹理内容数据

in vec3 vPosition;//接收x，y，0，周期
in float sjFactor;//接收衰减因子
in vec2 vTextureCoord;//用于传递给片元着色器的变量
out vec4 fragColor;//输出到的片元颜色
void main()
{
   vec4 stColor=texture(sTexture, vTextureCoord);//采样出纹理颜色
   fragColor=lineColor*sjFactor*stColor.a;//给此片元颜色值(线条颜色*衰减因子*Alpha值)
}