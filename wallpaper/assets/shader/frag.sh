#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform sampler2D sTextureHd;//纹理内容数据

in vec3 vPosition;
in vec2 vMoveST;
in vec4 ambient;//环境光
in vec4 diffuse;//散射光
in vec4 specular;//镜面光
out vec4 fragColor;//输出到的片元颜色
void main()
{
   //给此片元从纹理中采样出颜色值
   vec4 finalColorDay;
   vec4 finalColorzj;
   //给此片元从纹理中采样出颜色值
   finalColorDay= texture(sTexture, vTextureCoord); 
   finalColorzj =finalColorDay;//*(1.0+f);     
   fragColor=finalColorzj*ambient+finalColorzj*specular+finalColorzj*diffuse; 
}               