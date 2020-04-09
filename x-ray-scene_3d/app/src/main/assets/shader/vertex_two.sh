#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uLightLocation;	//光源位置
uniform vec3 uCamera;	//摄像机位置
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量
in vec2 aTexCoor;    //顶点纹理坐标
//用于传递给片元着色器的变量
out vec4 gzqd;//总光照强度
out vec2 vTextureCoord;
//定位光光照计算的方法
vec4 pointLight(					//定位光光照计算的方法
  in vec3 normal,				//法向量
  in vec3 lightLocation,			//光源位置
  in vec4 lightAmbient,			//环境光强度
  in vec4 lightDiffuse,			//散射光强度
  in vec4 lightSpecular			//镜面光强度
){
  vec4 result=vec4(0.0);
    result=result+lightAmbient;
    vec3 normalTarget=aPosition+normal;	//计算变换后的法向量
    vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
    newNormal=normalize(newNormal); 	//对法向量规格化
    //计算从表面点到摄像机的向量
    vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
    //计算从表面点到光源位置的向量vp
    vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);
    vp=normalize(vp);//格式化vp
    vec3 halfVector=normalize(vp+eye);	//求视线与光线的半向量
    float shininess=50.0;				//粗糙度，越小越光滑
    float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
    result=result+lightDiffuse*nDotViewPosition;
    float nDotViewHalfVector=dot(newNormal,halfVector);	//法线与半向量的点积
    float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//镜面反射光强度因子
    result=result+lightSpecular*powerFactor;
    return result;
}
void main()
{
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   vec4 ambientTemp, diffuseTemp, specularTemp;   //存放环境光、散射光、镜面反射光的临时变量
   gzqd=pointLight(normalize(aNormal),uLightLocation,vec4(0.5,0.5,0.5,1.0),vec4(0.5,0.5,0.5,1.0),vec4(0.0,0.0,0.0,1.0));
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}