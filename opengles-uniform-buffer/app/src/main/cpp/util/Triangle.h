#ifndef Triangle__h//防止重复定义
#define Triangle__h

#include <GLES3/gl3.h>//导入需要的头文件
#include <GLES3/gl3ext.h>

class Triangle {
    GLuint mProgram;//自定义着色器程序id
    GLuint mProgramTwo;//自定义着色器程序id

    GLuint muMVPMatrixHandle;//总变换矩阵引用
    GLuint muMVPMatrixHandleTwo;//总变换矩阵引用

    GLuint maPositionHandle;//顶点位置属性引用
    GLuint maPositionHandleTwo;//自定义着色器程序id

    unsigned int uniformBufferindex;//一致变量缓冲的引用
    const GLvoid* pCoords;//顶点坐标数据
    int vCount;//顶点数量
public:
    Triangle();//构造函数
    void initVertexData();//初始化顶点数据和着色数据的函数
    void initShader();//初始化着色器的函数
    void drawSelf(float shaderID);//绘制函数
};

#endif
