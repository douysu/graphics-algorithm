#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
//接收从顶点着色器过来的参数
in vec4 gzqd;//总光照强度
in vec2 vTextureCoord;
out vec4 fragColor;
void main()
{
   //将计算出的颜色给此片元
   vec4 finalColor=texture(sTexture, vTextureCoord);
   //给此片元颜色值
   fragColor = finalColor*gzqd;
}