#include <MyGLThread.h>
#include "Triangle.h"//导入头文件
#include "ShaderUtil.h"
#include "MatrixState.h"
#include "FileUtil.h"
#include "mylog.h"
Triangle::Triangle()//构造函数
{
    initVertexData();//调用初始化顶点数据函数
    initShader();//初始化着色器
}

void Triangle::initVertexData()//初始化顶点数据
{
    vCount = 3;//顶点个数
    const float vertices[]=//顶点坐标数据
            {
                    -0.8    ,0      ,0,
                    0       ,0.8    ,0,
                    0.8     ,0      ,0
            };
    pCoords = &vertices[0];//给顶点坐标数据指针赋值
}

void Triangle::initShader() //创建并初始化着色器的函数
{
	string vertexShader=FileUtil::loadShaderStr("shader/vert.sh");//加载顶点着色器
	string fragmentShader=FileUtil::loadShaderStr("shader/frag.sh");//加载片元着色器

    string vertexShaderTwo=FileUtil::loadShaderStr("shader/vertTwo.sh");//加载顶点着色器
    string fragmentShaderTwo=FileUtil::loadShaderStr("shader/fragTwo.sh");//加载片元着色器

    mProgram = ShaderUtil::createProgram(vertexShader.c_str(), fragmentShader.c_str());//创建着色器
    mProgramTwo = ShaderUtil::createProgram(vertexShaderTwo.c_str(), fragmentShaderTwo.c_str());//创建着色器

    maPositionHandle = glGetAttribLocation(mProgram, "Position");//获取顶点位置引用
    muMVPMatrixHandle = glGetUniformLocation(mProgram, "Modelview");//获取总变换矩阵引用

    maPositionHandleTwo = glGetAttribLocation(mProgramTwo, "Position");//获取顶点位置引用
    muMVPMatrixHandleTwo = glGetUniformLocation(mProgramTwo, "Modelview");//获取总变换矩阵引用

    uniformBufferindex=glGetUniformBlockIndex(mProgram, "myColor");//得到Uniform缓冲的引用
}

void Triangle::drawSelf(float shaderID)//自定义的绘制三角形的方法
{
    if(shaderID==1){//使用第一套着色器
        glUseProgram(mProgram);//指定使用的着色器程序
        glUniformMatrix4fv(muMVPMatrixHandle, 1, 0, MatrixState::getFinalMatrix());   //将最终变换矩阵传入渲染管线
        glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, GL_FALSE, 3*4, pCoords);   //将顶点位置数据传送进渲染管线
        glEnableVertexAttribArray(maPositionHandle);//启用顶点位置数据
    }else{//使用第二套着色器
        glUseProgram(mProgramTwo);//指定使用的着色器程序
        glUniformMatrix4fv(muMVPMatrixHandleTwo, 1, 0, MatrixState::getFinalMatrix());   //将最终变换矩阵传入渲染管线
        glVertexAttribPointer(maPositionHandleTwo, 3, GL_FLOAT, GL_FALSE, 3*4, pCoords);   //将顶点位置数据传送进渲染管线
        glEnableVertexAttribArray(maPositionHandleTwo);//启用顶点位置数据
    }
    glUniformBlockBinding(mProgram, uniformBufferindex, 0);//设置绑定点为0
    glBindBufferBase(GL_UNIFORM_BUFFER, 0, MyGLThread::uboExampleBlock);//绑定Uniform缓冲对象到对应的绑定点上
    //使用以下方式使用Uniform缓冲对象
    glBindBuffer(GL_UNIFORM_BUFFER, MyGLThread::uboExampleBlock);
    float colorArray[8]={
            1.0f,0.0f,0.0f,1.0f,
            0.0f,1.0f,0.0f,1.0f
    };
    glBufferSubData(GL_UNIFORM_BUFFER, 0, 32, &colorArray);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);

    glDrawArrays(GL_TRIANGLES, 0, vCount);//执行绘制
}
