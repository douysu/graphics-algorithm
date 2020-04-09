#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
uniform vec3 uCamera;//摄像机位置
uniform float blurWidth;//平滑过渡带的宽度
uniform float blurPosition;//平滑过渡带的开始位置
uniform float screenWidth;//屏幕宽度
uniform float screenHeight;//屏幕高度
in vec4 vPosition;//接收位置
out vec4 fragColor;//输出颜色

//获取高斯模糊片元颜色值的方法(进行5x5的卷积滤波)
vec4 gaussBlur(vec2 stCoord)
{
    //纹理偏移量单位步进,这个数字越小模糊度越大
	const float stStep = 512.0;
	//给出最终求和时的加权因子(为调整亮度)
    const float scaleFactor = 1.0/273.0;
    //给出卷积内核中各个元素对应像素相对于待处理像素的纹理坐标偏移量
    vec2 offsets[25]=vec2[25]
    (
      vec2(-2.0,-2.0),vec2(-1.0,-2.0),vec2(0.0,-2.0),vec2(1.0,-2.0),vec2(2.0,-2.0),
      vec2(-2.0,-1.0),vec2(-1.0,-1.0),vec2(0.0,-1.0),vec2(1.0,-1.0),vec2(2.0,-1.0),
      vec2(-2.0,0.0),vec2(-1.0,0.0),vec2(0.0,0.0),vec2(1.0,0.0),vec2(2.0,0.0),
      vec2(-2.0,1.0),vec2(-1.0,1.0),vec2(0.0,1.0),vec2(1.0,1.0),vec2(2.0,1.0),
      vec2(-2.0,2.0),vec2(-1.0,2.0),vec2(0.0,2.0),vec2(1.0,2.0),vec2(2.0,2.0)
    );
    //卷积内核中各个位置的值
    float kernelValues[25]=float[25]
    (
        1.0,4.0,7.0,4.0,1.0,
        4.0,16.0,26.0,16.0,4.0,
        7.0,26.0,41.0,26.0,7.0,
        4.0,16.0,26.0,16.0,4.0,
        1.0,4.0,7.0,4.0,1.0
    );
    //最终的颜色和
    vec4 sum=vec4(0,0,0,0);
    //颜色求和
    for(int i=0;i<25;i++)
    {
        sum=sum+kernelValues[i]*texture(sTexture, stCoord+offsets[i]/stStep);
    }
    return sum*scaleFactor;
}


void main()
{
    float dis=distance(vPosition.xyz,uCamera);//计算摄像机与顶点距离
    float s=gl_FragCoord.x/screenWidth;//换算纹理坐标
    float t=gl_FragCoord.y/screenHeight;//换算纹理坐标
    vec2 stCurr=vec2(s,t);//得到纹理坐标
    float factor=(dis-blurPosition)/blurWidth;//开始位置之前为小于0，过渡带为0~1，过渡带之后大于1
    factor=smoothstep(0.0,1.0,factor);//0,0~1,1 三部分
    //清晰阈的颜色值（直接进行采样）
    vec4 vividColor=texture(sTexture, stCurr);
    //模糊阈的颜色值（通过自定义高斯函数采样）
    vec4 blurColor=gaussBlur(stCurr);
    //根据距离因子计算最终颜色值
    fragColor=mix(vividColor,blurColor,factor);
}