#ifndef __FileUtil_H__//避免重复定义
#define __FileUtil_H__

#include "android/asset_manager.h"//导入需要类的头文件
#include "android/asset_manager_jni.h"
#include <string>

using namespace std;
class FileUtil
{
  public:
	static AAssetManager* aam;//指向AAssetManager对象的指针
	static void setAAssetManager(AAssetManager* aamIn);//设置AAssetManager对象指针
	static string loadShaderStr(string fname);//加载着色器
};

#endif
