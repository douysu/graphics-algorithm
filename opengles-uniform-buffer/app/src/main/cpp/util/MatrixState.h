#ifndef MatrixState_hpp//防止重复定义
#define MatrixState_hpp

#include "Matrix.h"//导入需要的头文件

class MatrixState
{
private:
    static float currMatrix[16];//当前变换矩阵
	static float mProjMatrix[16];//投影矩阵
    static float mVMatrix[16];//摄像机矩阵
    static float mMVPMatrix[16];//总矩阵
public:
    static float mStack[10][16];//保护变换矩阵的栈
    static int stackTop;//栈顶位置

    static void setInitStack();//初始化矩阵
    
    static void pushMatrix();//保护变换矩阵
    
    static void popMatrix();//恢复变换矩阵
    
    static void translate(float x,float y,float z);//沿x、y、z轴平移
    
    static void rotate(float angle,float x,float y,float z);//绕指定轴旋转
    
    static void scale(float x,float y,float z);//矩阵缩放
    static void setCamera//设置摄像机
    (
     float cx,
     float cy,
     float cz,
     float tx,
     float ty,
     float tz,
     float upx,
     float upy,
     float upz
     );

    static void setProjectFrustum//设置透视投影参数
    (
     float left,
     float right,
     float bottom,
     float top,
     float near,
     float far
     );
    static void setProjectOrtho//设置透视投影参数
            (
                    float left,
                    float right,
                    float bottom,
                    float top,
                    float near,
                    float far
            );
    static float* getFinalMatrix();//获取最终矩阵
};


#endif
