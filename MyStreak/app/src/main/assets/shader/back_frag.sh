#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
out vec4 fragColor;
void main()
{
   //将计算出的颜色给此片元
   vec4 finalColor=texture(sTexture, vTextureCoord);
   //计算片元的最终颜色值
   fragColor = finalColor;
}              