#ifndef ShaderUtil_hpp//防止重复定义
#define ShaderUtil_hpp
#include <GLES3/gl3.h>//导入需要的头文件
#include <GLES3/gl3ext.h>

class ShaderUtil
{
public:
    static GLuint createProgram(const char* vertexShaderSource,//创建着色器程序的函数
                                          const char* fragmentShaderSource);
    static GLuint loadShader(const char* source, GLenum shaderType);//编译着色器的函数
};

#endif
