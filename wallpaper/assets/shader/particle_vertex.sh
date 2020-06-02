#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform float maxLifeSpan;//最大允许生命期

uniform mediump float bj; //单个粒子的半径
uniform vec3 cameraPosition;//摄像机位置
uniform mat4 uMMatrix;//基本变换矩阵总矩阵

in vec4 aPosition;  //从渲染管线接收的顶点位置属性
out vec4 vPosition; //用于传递给片元着色器的顶点位置属性 
out float sjFactor;//用于传递给片元着色器的总衰减因子

void main()     
{//主函数          
   //求出变换后顶点在世界坐标系中的位置
   vec4 currPosition=uMMatrix * vec4(aPosition.xy,0.0,1);
   //求出顶点到摄像机的距离
   float d=distance(currPosition.xyz,cameraPosition);
   //求出距离缩放因子S的平方分之1
   float s=1.0/sqrt(0.01+0.05*d+0.001*d*d);
   gl_PointSize=bj*s;//设置点精灵对应点的尺寸
   gl_Position = uMVPMatrix * vec4(aPosition.xy,0.0,1); //根据总变换矩阵计算此次绘制此顶点位置
   vPosition=vec4(aPosition.xy,0.0,aPosition.w); //计算顶点位置属性，并将其传递给片元着色器
   sjFactor=(maxLifeSpan-aPosition.w)/maxLifeSpan;//计算总衰减因子，并将其传递给片元着色器
}                      