#version 300 es
precision mediump float;
uniform sampler2D sTexture;//真实纹理内容
uniform sampler2D sTextureDepth;//深度纹理内容
uniform sampler2D sTexturePeople;//人物纹理内容
uniform vec3 uCamera;//摄像机位置
uniform float screenWidth;//屏幕宽度
uniform float screenHeight;//屏幕高度
in vec4 vPosition;//接收位置
out vec4 fragColor;//输出颜色
void main()
{
    float dis=distance(vPosition.xyz,uCamera);//计算摄像机与顶点距离
    float s=gl_FragCoord.x/screenWidth;//换算纹理坐标
    float t=gl_FragCoord.y/screenHeight;//换算纹理坐标
    vec2 stCurr=vec2(s,t);//得到纹理坐标
    float depth=texture(sTextureDepth, stCurr).r;//采样出深度
    vec4 peopleColor=texture(sTexturePeople, stCurr);//采样正常人物纹理（正常人物）
    vec4 realColor=texture(sTexture, stCurr);//采样出正常场景纹理（无人物）
    if(dis>depth){//挡住了
       float realFactor=(realColor.r+realColor.g+realColor.b)/3.0;
       vec4 realGray=vec4(realFactor,realFactor,realFactor,1.0);//计算场景的灰度值
       float peopleFactor=(peopleColor.r+peopleColor.g+peopleColor.b)/3.0;
       vec4 peopleGray=vec4(peopleFactor,peopleFactor,peopleFactor,1.0);//计算人物的灰度值
       fragColor=mix(realGray,peopleGray,0.3);//混合颜色
    }else{
    fragColor=peopleColor;//人物颜色
    }
}