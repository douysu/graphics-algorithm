#include <jni.h>//导入头文件
#include <GLES3/gl3.h>
#include "MatrixState.h"
#include "FileUtil.h"
#include "mylog.h"
#include <Triangle.h>
#include "MyGLThread.h"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-activity", __VA_ARGS__))

void renderFrame() {	//渲染函数
    MyGLThread::drawSelf();//绘制纹理矩形
}
//对应于Java那边的本地方法的实现
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_bn_com_myapplication_GL2JNILib_init
        (JNIEnv * env, jclass jc, jobject obj, jint width, jint height)//调用初始化函数
{
    MyGLThread::setupGraphics(env,obj,width, height);
}

JNIEXPORT void JNICALL Java_bn_com_myapplication_GL2JNILib_step
        (JNIEnv *, jclass)//调用渲染函数
{
    renderFrame();
}

JNIEXPORT void JNICALL Java_bn_com_myapplication_GL2JNILib_nativeSetAssetManager
        (JNIEnv* env, jclass cls, jobject assetManager)//调用加载着色器脚本函数
{
    AAssetManager* aamIn = AAssetManager_fromJava( env, assetManager );//初始化AAssetManager对象
    FileUtil::setAAssetManager(aamIn);//设置AAssetManager
}

#ifdef __cplusplus
}
#endif



