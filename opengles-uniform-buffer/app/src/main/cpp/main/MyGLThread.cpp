#include "MyGLThread.h"
#include <GLES3/gl3.h>
#include "MatrixState.h"
#include "FileUtil.h"
#include "mylog.h"
#include <Triangle.h>
float MyGLThread::Screen_Width;//屏幕宽度
float MyGLThread::Srceen_Height;//屏幕高度
float MyGLThread::ratio;//屏幕大小比例
Triangle* MyGLThread::triangle;//三角形对象指针
unsigned int MyGLThread::uboExampleBlock;//记录Uniform缓冲的索引
bool MyGLThread::setupGraphics(JNIEnv * env,jobject obj,int w, int h)//初始化函数
{
    glViewport(0, 0, w, h);//设置视口
    Screen_Width=w;
    Srceen_Height=h;
    ratio = (float) w/h;//计算宽长比
    MatrixState::setInitStack();//初始化变换矩阵
    glClearColor(0.0f, 0.0f, 0.0f, 1);//设置背景颜色
    iniUniformBuffer();//初始话Uniform缓冲
    triangle = new Triangle();	//创建三角形对象
    return true;
}
void MyGLThread::drawSelf(){
    //设置视窗大小及位置
    glViewport(0,0,Screen_Width,Srceen_Height);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);//绑定帧缓冲id
    glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);//清空颜色缓冲和深度缓冲
    MatrixState::setProjectFrustum(-ratio, ratio, -1, 1, 1, 100);//设置投影矩阵
    MatrixState::setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);//设置摄像机矩阵
    //绘制红色三角形
    MatrixState::pushMatrix();
    MatrixState::translate(0,1,0);
    triangle->drawSelf(0);
    MatrixState::popMatrix();

    //绘制绿色三角形
    MatrixState::pushMatrix();
    MatrixState::translate(0,-1,0);
    triangle->drawSelf(1);
    MatrixState::popMatrix();
}
void MyGLThread::iniUniformBuffer(){
    glGenBuffers(1, &uboExampleBlock);
    glBindBuffer(GL_UNIFORM_BUFFER, uboExampleBlock);
    glBufferData(GL_UNIFORM_BUFFER, 32, NULL, GL_STATIC_DRAW); // 分配152字节的内存
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
}