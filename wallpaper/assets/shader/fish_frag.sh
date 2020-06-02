#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform sampler2D sTextureHd;//纹理内容数据

in vec3 vNormal;//法向量
in vec3 vPosition;
in vec4 ambient;//环境光
in vec4 diffuse;//散射光
in vec4 specular;//镜面光
out vec4 fragColor;
void main()
{
    float f;
   //给此片元从纹理中采样出颜色值
   vec4 finalColorDay;
   vec4 finalColorNight;
   vec4 finalColorzj;
      //给此片元从纹理中采样出颜色值
   finalColorDay= texture(sTexture, vTextureCoord);//采样鱼的纹理
   vec2 tempTexCoor=vec2((vPosition.x+20.8)/5.2,(vPosition.z+18.0)/2.5); //8*8
   if(vNormal.y>0.2)
   {
       finalColorNight = texture(sTextureHd, tempTexCoor); //采样光影纹理
       f=(finalColorNight.r+finalColorNight.g+finalColorNight.b)/3.0; 
  }else if(vNormal.y<=0.2&&vNormal.y>=-0.2){
       if(vNormal.y>=0.0&&vNormal.y<=0.2)
       {
           finalColorNight = texture(sTextureHd, tempTexCoor)*(1.0-2.5*(0.20-vNormal.y));
           f=(finalColorNight.r+finalColorNight.g+finalColorNight.b)/3.0;
       }else if(vNormal.y<0.0&&vNormal.y>=-0.2){
       
           finalColorNight = texture(sTextureHd, tempTexCoor)*(0.5+2.5*vNormal.y); 
           f=(finalColorNight.r+finalColorNight.g+finalColorNight.b)/3.0;
       }
   }else if(vNormal.y<-0.2){
        f=0.0;
   }  
   finalColorzj =finalColorDay*(1.0+f*1.5);     
   fragColor=finalColorzj*ambient+finalColorzj*specular+finalColorzj*diffuse; 
}  

   