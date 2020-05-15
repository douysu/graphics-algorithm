# [从零构建光栅渲染器] 6. 顶点和片元着色器的工作原理

非常感谢和推荐Sokolov的教程，Sokolov使用500行C++代码实现一个光栅渲染器。教程学习过程非常平滑，从画点、线和三角形开始教学，在逐步深入三维变换，投影，再到顶点着色器，片段着色器等等。教程地址：<https://github.com/ssloy/tinyrenderer>。Sokolov的教程为英文，我翻译了其文章。

在学习过程中，有些内容可能您可能云里雾里，这时就需要查阅《计算机图形学》的书籍了，这里面的算法和公式可以帮助您理解代码。

作者：憨豆酒（YinDou），联系我yindou97@163.com，熟悉图形学，图像处理领域，本章的源代码可在此仓库中找到<https://github.com/douysu/person-summary>：如果对您有帮助，还请给一个star，如果大家发现错误以及不合理之处，还希望多多指出。

[我的知乎](https://zhuanlan.zhihu.com/c_1218472587279433728)

[我的Github](https://link.zhihu.com/?target=https%3A//github.com/douysu)

[我的博客](https://link.zhihu.com/?target=https%3A//blog.csdn.net/ModestBean)

# 本章运行结果

![图片](https://uploader.shimo.im/f/LG8vivkG49eUTGqM.png!thumbnail)

# 开始

请记住，我的代码只是帮你进行参考，不要用我的代码，写你自己的代码。我是个糟糕的程序员。请你做最疯狂的着色器，并把图片发给我，我会把它们贴在这里。

有趣的时间，首先让我们检查一下我们现在的代码。[source code](https://github.com/ssloy/tinyrenderer/tree/f037c7a0517a632c7391b35131f9746a8f8bb235)

* geometry.cpp+.h — 218 行
* model.cpp+.h — 139 行
* our_gl.cpp+.h — 102 行
* main.cpp — 66 行

总共525行，正是我们想要的。请注意，只有our_gl.*和main.cpp两个文件负责实际渲染，总共168行。

![图片](https://uploader.shimo.im/f/opFGq73o8JQxp4Aa.png!thumbnail)

# 重构代码

main.cpp中的代码太多了，让我们分割成两部分：

* our_gl.h+cpp——这部分开发者接触不到，说白了是OpenGL的library。
* main.cpp——这是我们想要的重构的。

现在我们应该放什么到our_gl中？ModelView，Viewport 和Projection矩阵初始化函数和三角光栅化。就这些。

下面是文件our_gl.h的内容（我稍后会介绍IShader结构）。

```plain
#include "tgaimage.h"
#include "geometry.h"
extern Matrix ModelView;
extern Matrix Viewport;
extern Matrix Projection;
void viewport(int x, int y, int w, int h);
void projection(float coeff=0.f); // coeff = -1/c
void lookat(Vec3f eye, Vec3f center, Vec3f up);
struct IShader {
    virtual ~IShader();
    virtual Vec3i vertex(int iface, int nthvert) = 0;
    virtual bool fragment(Vec3f bar, TGAColor &color) = 0;
};
void triangle(Vec4f *pts, IShader &shader, TGAImage &image, TGAImage &zbuffer);
```
**翻译作者内容**：从上面的代码可以看到vertex()方法和fragment()方法，这里就是我们常用的顶点着色器和片元着色器，从这两个函数中，我们可以明白着色器的工作原理。

文件main.cpp现在只有66行，因此我把它完整的列出来（很抱歉代码太长，但我仍然把他完整的列出来，因为我很喜欢它）。

```plain
#include <vector>
#include <iostream>
#include "tgaimage.h"
#include "model.h"
#include "geometry.h"
#include "our_gl.h"
Model *model     = NULL;
const int width  = 800;
const int height = 800;
Vec3f light_dir(1,1,1);
Vec3f       eye(1,1,3);
Vec3f    center(0,0,0);
Vec3f        up(0,1,0);
struct GouraudShader : public IShader {
    Vec3f varying_intensity; // written by vertex shader, read by fragment shader
    virtual Vec4f vertex(int iface, int nthvert) {
        varying_intensity[nthvert] = std::max(0.f, model->normal(iface, nthvert)*light_dir); // get diffuse lighting intensity
        Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert)); // read the vertex from .obj file
        return Viewport*Projection*ModelView*gl_Vertex; // transform it to screen coordinates
    }
    virtual bool fragment(Vec3f bar, TGAColor &color) {
        float intensity = varying_intensity*bar;   // interpolate intensity for the current pixel
        color = TGAColor(255, 255, 255)*intensity; // well duh
        return false;                              // no, we do not discard this pixel
    }
};
int main(int argc, char** argv) {
    if (2==argc) {
        model = new Model(argv[1]);
    } else {
        model = new Model("obj/african_head.obj");
    }
    lookat(eye, center, up);
    viewport(width/8, height/8, width*3/4, height*3/4);
    projection(-1.f/(eye-center).norm());
    light_dir.normalize();
    TGAImage image  (width, height, TGAImage::RGB);
    TGAImage zbuffer(width, height, TGAImage::GRAYSCALE);
    GouraudShader shader;
    for (int i=0; i<model->nfaces(); i++) {
        Vec4f screen_coords[3];
        for (int j=0; j<3; j++) {
            screen_coords[j] = shader.vertex(i, j);
        }
        triangle(screen_coords, shader, image, zbuffer);
    }
    image.  flip_vertically(); // to place the origin in the bottom left corner of the image
    zbuffer.flip_vertically();
    image.  write_tga_file("output.tga");
    zbuffer.write_tga_file("zbuffer.tga");
    delete model;
    return 0;
}
```
让我们看看它使如何工作的，跳过标题，我们声明几个全局常量：屏幕尺寸、摄像头位置等。我将在下一段解释GouraudShader结构，所以我们跳过它。然后是main()函数的实际内容：

* 解析.obj文件
* 初始化ModelView、Projection和Viewport矩阵（记得这些矩阵的实际实例都在our_gl模块中）。
* 通过模型中的所有三角形进行迭代，并对每个三角形进行栅格化。

最后一步是最有意思的。外循环迭代所有的三角形，内循环迭代当前三角形的所有顶点，并为每个顶点调用一个顶点着色器。（这就是顶点着色器的功能）

**顶点着色器的主要目标是转换顶点的坐标。次要目标是为片段着色器准备数据。**

那之后呢？我们称之为光栅化例程。我们不知道栅格化器内部会发生什么（好吧，我们知道，因为我们编写了程序！），但有一个例外。我们知道光栅化器会对每个像素调用我们的例程，即片段着色器。同样，对于三角形内的每个像素，光栅器会调用我们自己的回调，即片段着色器。

**片段着色器的主要目标--是确定当前像素的颜色。次要目标--我们可以通过返回true来丢弃当前像素。**

OpenGL 2的渲染管道可以用以下方式表示（事实上，对于较新的版本也差不多）。

![图片](https://uploader.shimo.im/f/doykNskXZr0759QE.png!thumbnail)


由于我的课程时间有限，所以我只限于OpenGL 2流水线，因此只限于片段和顶点着色器。在较新版本的OpenGL中，还有其他的着色器，比如说几何着色器，计算着色器。

好了，在上图中，所有我们不能触及的阶段都用蓝色显示，而我们的回调则用橙色显示。其实，我们的main()函数--就是原始处理例程。它调用的是顶点着色器。我们在这里并没有进行基元装配，因为我们只画最基本的三角形（在我们的代码中，它与基元处理合并在一起）。 triangle()函数--是光栅化器，对于三角形内的每一个点，它调用片段着色器，然后执行深度检查（z-buffer）之类的。

好了，你知道了什么是着色器了并且可以写自己的着色器了。

# 我实现的 Gouraud着色的着色器

![图片](https://uploader.shimo.im/f/XeOyBksqN1TklYCL.png!thumbnail)

我们来看看我上面列出的main.cpp中的着色器。根据它的名字，它是一个Gouraud着色器。让我重新列举一下代码。

```plain
    Vec3f varying_intensity; // written by vertex shader, read by fragment shader
    virtual Vec4f vertex(int iface, int nthvert) {
        varying_intensity[nthvert] = std::max(0.f, model->normal(iface, nthvert)*light_dir); // get diffuse lighting intensity
        Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert)); // read the vertex from .obj file
        return Viewport*Projection*ModelView*gl_Vertex; // transform it to screen coordinates
    }
```
varying 是GLSL语言中的一个保留关键字，我用variable_intensity作为名称来表示对应关系（我们在第9课中会讲到GLSL）。在 varying变量中，我们在三角形内部存储要插值的数据，片段着色器得到插值（针对当前像素）。

让我们重新列举一下片元着色器：

```plain
  Vec3f varying_intensity; // written by vertex shader, read by fragment shader
// [...]
    virtual bool fragment(Vec3f bar, TGAColor &color) {
        float intensity = varying_intensity*bar;   // interpolate intensity for the current pixel
        color = TGAColor(255, 255, 255)*intensity; // well duh
        return false;                              // no, we do not discard this pixel
    }
```
这个例程是针对我们绘制的三角形内的每一个像素点来调用的；作为输入，它接收到的是双心坐标，用于variing_数据的插值。因此，插值后的强度可以计算为variing_intensity[0]*bar[0]+variing_intensity[1]*bar[1]+variing_intensity[2]*bar[2]，或者简单地计算为两个向量之间的点积：variing_intensity*bar。当然，在真正的GLSL中，片段着色器接收的是现成的插值。

注意，片元着色器返回一个bool值。如果我们看一下rasterizer内部（our_gl.cpp，triangle()函数）就很容易理解它的作用。

```plain
         TGAColor color;
            bool discard = shader.fragment(c, color);
            if (!discard) {
                zbuffer.set(P.x, P.y, TGAColor(P.z));
                image.set(P.x, P.y, color);
            }
```
Fragment 着色器可以丢弃当前像素的绘制，然后光栅化器简单地跳过它。如果我们想创建二进制蒙版或其他什么东西，它就很方便了（请查看第9课的一个非常酷的丢弃像素的例子）。

当然，光栅器无法想象你可以编程的所有奇怪的东西，因此它不能和你的着色器一起预编译。这里我们用抽象的类IShader作为两者之间的一个中间件。哇，我用抽象类是相当少见的，但如果没有它，我们在这里会很痛苦。函数的指针是很难懂的。

# 首次着色器的修改

```plain
    virtual bool fragment(Vec3f bar, TGAColor &color) {
        float intensity = varying_intensity*bar;
        if (intensity>.85) intensity = 1;
        else if (intensity>.60) intensity = .80;
        else if (intensity>.45) intensity = .60;
        else if (intensity>.30) intensity = .45;
        else if (intensity>.15) intensity = .30;
        else intensity = 0;
        color = TGAColor(255, 155, 0)*intensity;
        return false;
    }
```
Gourad着色简单的修改，把强度改成6个阶段：如下

![图片](https://uploader.shimo.im/f/2dCkMaRa6KkgCS0Y.png!thumbnail)

# 纹理

我们先跳过Phong着色[Phong shading](https://en.wikipedia.org/wiki/Phong_shading)，但是先看一下这个文章。还记得我给你布置的纹理作业吗？我们必须要插补紫外线坐标。所以，我创建了一个2x3矩阵。2行代表u和v，3列（每个顶点一个）。

```plain
struct Shader : public IShader {
    Vec3f          varying_intensity; // written by vertex shader, read by fragment shader
    mat<2,3,float> varying_uv;        // same as above

    virtual Vec4f vertex(int iface, int nthvert) {
        varying_uv.set_col(nthvert, model->uv(iface, nthvert));
        varying_intensity[nthvert] = std::max(0.f, model->normal(iface, nthvert)*light_dir); // get diffuse lighting intensity
        Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert)); // read the vertex from .obj file
        return Viewport*Projection*ModelView*gl_Vertex; // transform it to screen coordinates
    }
    
    virtual bool fragment(Vec3f bar, TGAColor &color) {
        float intensity = varying_intensity*bar;   // interpolate intensity for the current pixel
        Vec2f uv = varying_uv*bar;                 // interpolate uv for the current pixel
        color = model->diffuse(uv)*intensity;      // well duh
        return false;                              // no, we do not discard this pixel
    }
};
```
这里是结果：

![图片](https://uploader.shimo.im/f/G0xRBAVN17FCVk0o.png!thumbnail)

# 法线贴图

好了，现在我们有了纹理坐标。我们可以在纹理图像中存储什么？其实，几乎什么都可以。它可以是颜色、方向、温度等等。让我们加载这个纹理。

![图片](https://uploader.shimo.im/f/KqPREY4GHUgmj1Yq.png!thumbnail)

**如果我们将RGB值解释为xyz方向，那么这个图像就可以为我们的渲染的每个像素提供法线向量，而不仅仅是像之前一样为每个顶点提供法线向量。**

**翻译作者内容：**上面是基于顶点计算的法向量，三角形里面的颜色是基于差值得到的，所以不太好，使用法向量纹理是每个像素的法向量，计算颜色比较好**。**

顺便说一下，把这张图和另一张相比，它给出的信息完全一样，但在另一维度中。

![图片](https://uploader.shimo.im/f/56f6ocyxt9pOMFf0.png!thumbnail)

其中一个图像给出了全局（笛卡尔）坐标系中的法向量，另一个图像给出了Darboux框架（所谓的切线空间）中的法向量。在Darboux坐标系中，z向量是物体的法线，x--主曲率方向，y--它们的叉积。

维基内容：

![图片](https://uploader.shimo.im/f/YYokKNBDzUIHmMiE.png!thumbnail)

**练习1**：你能告诉我哪个图像是在Darboux坐标系中，哪个是全局坐标系中吗

**练习2**：你能说出哪种表现形式比较好，如果能，为什么？

```plain
struct Shader : public IShader {
    mat<2,3,float> varying_uv;  // same as above
    mat<4,4,float> uniform_M;   //  Projection*ModelView
    mat<4,4,float> uniform_MIT; // (Projection*ModelView).invert_transpose()
    virtual Vec4f vertex(int iface, int nthvert) {
        varying_uv.set_col(nthvert, model->uv(iface, nthvert));
        Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert)); // read the vertex from .obj file
        return Viewport*Projection*ModelView*gl_Vertex; // transform it to screen coordinates
   }
    virtual bool fragment(Vec3f bar, TGAColor &color) {
        Vec2f uv = varying_uv*bar;                 // interpolate uv for the current pixel
        Vec3f n = proj<3>(uniform_MIT*embed<4>(model->normal(uv))).normalize();
        Vec3f l = proj<3>(uniform_M  *embed<4>(light_dir        )).normalize();
        float intensity = std::max(0.f, n*l);
        color = model->diffuse(uv)*intensity;      // well duh
        return false;                              // no, we do not discard this pixel
    }
};
[...]
    Shader shader;
    shader.uniform_M   =  Projection*ModelView;
    shader.uniform_MIT = (Projection*ModelView).invert_transpose();
    for (int i=0; i<model->nfaces(); i++) {
        Vec4f screen_coords[3];
        for (int j=0; j<3; j++) {
            screen_coords[j] = shader.vertex(i, j);
        }
        triangle(screen_coords, shader, image, zbuffer);
    }
```
Uniform是GLSL中的一个保留关键字，它允许向着色器传递常量。这里我传递了矩阵 Projection*ModelView 和它的反转置来变换法向量（参考第 5 课的结尾）。所以，光照强度的计算和以前一样，只有一个例外：我们不是插值法向量，而是从法线贴图纹理中获取信息（不要忘记变换光向量和法向量）。

![图片](https://uploader.shimo.im/f/MQ4MpN9E3PEO9Act.png!thumbnail)

# 镜面贴图

好了，让我们继续开始吧。所有的计算机图形学都是骗人的艺术。为了（廉价地）欺骗眼睛，我们使用Phong的近似照明模型。Phong提出将最终的光照看成（加权）三种光照强度的（加权）之和：环境光照（每个场景的常数）、漫射光照（我们计算到此刻的那个）和镜面光照。

看一下下面的图片，不言而喻。

![图片](https://uploader.shimo.im/f/L2lDudLN7N2cEhTl.png!thumbnail)

我们将漫反射光的计算方法为法线矢量与光的方向矢量之间的余弦角。我的意思是，这假设光在各个方向上都是均匀反射的。那么对于有光泽的表面会怎样呢？在极限情况下（镜面），如果并且只有当我们能看到这个像素反射的光源时，这个像素才会被照亮。

**翻译作者内容**：光照计算可以OpenGL的书籍或者LearnOpenGL。

![图片](https://uploader.shimo.im/f/hYFtxVGdVbczCIiZ.png!thumbnail)

对于漫射光，我们计算了向量n和l之间的(余弦角)，现在我们更加关注的是向量r(反射光方向)和v(视线方向)之间的(余弦)角。

**练习3**：给定向量n和l，得到向量r

答案：如果n和l都是规则化的， r = 2n<n,l> - l

对于漫射光，我们计算光强为余弦。但是，一个有光泽的表面在一个方向上的反射率要比其他方向上的反射率高得多! 那么，如果我们取余弦的第10次幂会怎样呢？回想一下，所有小于1的数字在我们应用这个幂的时候都会减小。这意味着，余弦的第10次幂的余弦会使反射光束的半径变小。而第100次幂就会得到更小的光束半径。这个功率被存储在一个特殊的纹理（镜面映射纹理）中，它告诉每个点是否有光泽。

```plain
struct Shader : public IShader {
    mat<2,3,float> varying_uv;  // same as above
    mat<4,4,float> uniform_M;   //  Projection*ModelView
    mat<4,4,float> uniform_MIT; // (Projection*ModelView).invert_transpose()
    virtual Vec4f vertex(int iface, int nthvert) {
        varying_uv.set_col(nthvert, model->uv(iface, nthvert));
        Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert)); // read the vertex from .obj file
        return Viewport*Projection*ModelView*gl_Vertex; // transform it to screen coordinates
    }
    virtual bool fragment(Vec3f bar, TGAColor &color) {
        Vec2f uv = varying_uv*bar;
        Vec3f n = proj<3>(uniform_MIT*embed<4>(model->normal(uv))).normalize();
        Vec3f l = proj<3>(uniform_M  *embed<4>(light_dir        )).normalize();
        Vec3f r = (n*(n*l*2.f) - l).normalize();   // reflected light
        float spec = pow(std::max(r.z, 0.0f), model->specular(uv));
        float diff = std::max(0.f, n*l);
        TGAColor c = model->diffuse(uv);
        color = c;
        for (int i=0; i<3; i++) color[i] = std::min<float>(5 + c[i]*(diff + .6*spec), 255);
        return false;
    }
};
```
我认为我不需要在上面的代码中注释任何东西，除了系数之外。

```plain
        for (int i=0; i<3; i++) color[i] = std::min<float>(5 + c[i]*(diff + .6*spec), 255);
```
我对环境分量取5，漫射分量取1，镜面分量取0.6。选择什么样的系数--是你的选择。不同的选择会给物体带来不同的外观。通常是由美术来决定的。

请注意，通常情况下，共价之和必须等于1，但你知道。我喜欢创造光。

![图片](https://uploader.shimo.im/f/KfXyryYGDolHJ5XS.png!thumbnail)

![图片](https://uploader.shimo.im/f/2Pk3QyGLBBENKq2m.png!thumbnail)

# 结论

我们知道如何渲染一个好的场景，但是还不够真实，需要加上阴影，下节课会介绍。享受。

