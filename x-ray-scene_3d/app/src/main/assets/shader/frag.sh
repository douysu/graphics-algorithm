#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据

//接收从顶点着色器过来的参数
in vec4 gzqd;//总光照强度
in vec2 vTextureCoord;
in vec4 vPosition; //接收从顶点着色器过来的顶点位置
in vec3 fragCamera;//接收摄像机位置
layout (location=0)out vec4 fragColor0;
layout (location=1)out float fragColor1;
void main()
{
   //将计算出的颜色给此片元
   vec4 finalColor=texture(sTexture, vTextureCoord);
   vec4 fragColor= finalColor*gzqd;
   float dis=distance(vPosition.xyz,fragCamera);//计算被照射片元到光源的距离
   fragColor0=fragColor;//正常颜色
   fragColor1=dis;//深度纹理
}