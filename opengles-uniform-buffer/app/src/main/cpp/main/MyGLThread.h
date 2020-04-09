#ifndef ONENDKTRIANGLE_MYGLTHREAD_H
#define ONENDKTRIANGLE_MYGLTHREAD_H
#include <jni.h>//导入头文件
#include <Triangle.h>

class MyGLThread {
public:
    static float Screen_Width;//屏幕宽度
    static float Srceen_Height;//屏幕高度
    static float ratio;//屏幕大小比例
    static Triangle* triangle;//三角形对象指针
    static unsigned int uboExampleBlock;//记录Unifrom缓冲的索引
    static bool setupGraphics(JNIEnv * env,jobject obj,int w, int h);
    static void drawSelf();//执行绘制的方法
    static void  iniUniformBuffer();//初始化一致变量缓冲的方法
};


#endif //ONENDKTRIANGLE_MYGLTHREAD_H
