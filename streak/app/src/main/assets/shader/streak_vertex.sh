#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform float maxLifeSpan;//最大生命期

layout (location = 0) in vec3 aPosition;//顶点位置
in vec2 aTexCoor;//顶点纹理坐标

out vec3 vPosition;//传递给片元着色器顶点位置和周期
out float sjFactor;//用于传递给片元着色器的总衰减因子
out vec2 vTextureCoord;//用于传递给片元着色器的变量
void main()
{
   gl_Position = uMVPMatrix * vec4(aPosition.xy,0,1); //根据总变换矩阵计算此次绘制此顶点位置
   vPosition=aPosition;//x,y,0,周期
   //sjFactor=0.5;//计算总衰减因子，并将其传递给片元着色器
   sjFactor=aPosition.z/maxLifeSpan;//计算总衰减因子，并将其传递给片元着色器
   vTextureCoord=aTexCoor;//将纹理坐标传给片元着色器
}