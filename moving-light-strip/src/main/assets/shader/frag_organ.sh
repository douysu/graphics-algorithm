#version 300 es
precision mediump float;
//接收从顶点着色器过来的参数
in vec4 ambient;//顶点着色器传入的环境光强度
in vec4 diffuse;//顶点着色器传入的散射光强度
in vec4 specular;//顶点着色器传入的镜面光强度
uniform vec4 organColor;//传入的器官颜色
uniform float brightBreath;//传入的呼吸参数
out vec4 fragColor;//输出到的片元颜色
void main()
{
   vec4 finalColor=vec4(organColor.r*brightBreath,organColor.g*brightBreath,organColor.b*brightBreath,organColor.a);//计算呼吸颜色
   fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;//给此片元最终颜色
}