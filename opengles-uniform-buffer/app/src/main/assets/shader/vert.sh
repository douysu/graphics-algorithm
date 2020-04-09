#version 300 es
uniform mat4 Modelview;//总变换矩阵
in vec3 Position;//顶点位置
out vec4 vColor1;//输出到片元着色器的颜色
layout (std140) uniform myColor{//输入的Uniform缓冲
    vec4 color1;
    vec4 color2;
};
void main()
{
    gl_Position = Modelview * vec4(Position,1);//根据总变换矩阵计算此次绘制此顶点位置
    vColor1=color1;
}