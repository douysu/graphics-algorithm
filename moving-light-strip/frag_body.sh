#version 300 es
precision mediump float;
//接收从顶点着色器过来的参数
in vec3 gzqd;   //总光照强度
in  vec3 vPosition;//传给片元的顶点位置
out vec4 fragColor;//输出到的片元颜色
uniform float lineWidth;//传入线的宽度
uniform float lineBright;//传入线的亮度系数
uniform float obY;//传入线的Y位置
void main()
{
   const float ALPHA=0.5f;//颜色的透明通道
   const vec3 PFColor=vec3(0.11764705,0.43137254,0.97647058);//皮肤的颜色
   const vec3 LineColor=vec3(0.11764705,0.43137254,0.97647058);//光带的颜色
   vec3 finalColor=PFColor*gzqd;//得到光照后皮肤颜色
   float factor=abs(vPosition.y-obY)/lineWidth;//利用数学公式将人体分为三段  光带，光带以上，光带已下,光带部分的值以线的位置对称
   factor=(1.0-smoothstep(0.0,1.0,factor));//利用Hermite差值转换值，光带部分为0~1，1~0光滑的数，光带以上，光带已下部分均为0
   fragColor=vec4(finalColor+LineColor*factor*lineBright,ALPHA+(1.0-ALPHA)*factor);//将皮肤颜色线条颜色传给片元，更改透明通道，身体为0部分即没有光带
}