#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵
in vec3 aPosition; //顶点位置
out vec4 vPosition;//传递给片元着色器位置
void main()
{
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   vPosition=uMMatrix*vec4(aPosition,1);//世界坐标系顶点传递给片元着色器
}