#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uLightLocation;	//光源位置
uniform vec3 uCamera;	//摄像机位置
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量
//用于传递给片元着色器的变量
out vec3 gzqd;//总光照强度
out vec3 vPosition;//物体坐标系位置
//定位光光照计算的方法
vec4 pointLight(
  in vec3 normal,
  in vec3 lightLocation,
  in vec4 lightAmbient,
  in vec4 lightDiffuse,
  in vec4 lightSpecular
){
  vec4 result=vec4(0.0);
  result=result+lightAmbient;
  vec3 normalTarget=aPosition+normal;
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal);
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);
  vp=normalize(vp);
  vec3 halfVector=normalize(vp+eye);
  float shininess=50.0;
  float nDotViewPosition=max(0.0,dot(newNormal,vp));
  result=result+lightDiffuse*nDotViewPosition;
  float nDotViewHalfVector=dot(newNormal,halfVector);
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess));
  result=result+lightSpecular*powerFactor;
  return result;
}
void main()
{
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   //得到最终的光照强度
   gzqd=pointLight(normalize(aNormal),uLightLocation,vec4(0.2,0.2,0.2,1.0),vec4(0.9,0.9,0.9,1.0),vec4(0.4,0.4,0.4,1.0)).rgb;
   //将物体位置传给片元着色器
   vPosition=aPosition;
}