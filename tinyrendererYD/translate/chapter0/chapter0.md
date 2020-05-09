# [从零构建光栅渲染器] 1.引言

常感谢和推荐Sokolov的教程，Sokolov使用500行C++代码实现一个光栅渲染器。教程学习过程非常平滑，从画点、线和三角形开始教学，在逐步深入三维变换，投影，再到顶点着色器，片段着色器等等。教程地址：<https://github.com/ssloy/tinyrenderer>。Sokolov的教程为英文，我翻译了其文章。

在学习过程中，有些内容可能您可能云里雾里，这时就需要查阅《计算机图形学》的书籍了，这里面的算法和公式可以帮助您理解代码。

[知乎](https://zhuanlan.zhihu.com/c_1218472587279433728)

[Github](https://github.com/douysu)

[博客](https://blog.csdn.net/ModestBean)

# 介绍

这是一个微型的软件渲染器，如果你正在寻找一个微型光线追踪器，在这里找到[here](https://github.com/ssloy/tinyraytracer)。

我的源码是不相关的。阅读这个教程和实现你自己的渲染器。只有你经历了所有小细节后，你才能学会。

我非常希望你可以发反馈给我，邮箱：dmitry.sokolov@univ-lorraine.fr，当你有问题时，不要犹豫，赶紧联系我。

如果你是一名教师，并想采在课堂上用这个资料。非常欢迎这么去做，不需要任何授权。只需要发邮件通知我就行，可以帮助我提高这个课程。

在这系列文章中，我编写一个简单版本的软件来描述OpenGL是如何工作的。令人惊讶的是，我经常遇到学不会OpenGL/DirectX的人。因此，我准备了一个简短的讲义，我的学生都做出了不错的渲染器。

所以，这个任务如下：不使用第三方库，渲染得到下面这张图片：

![](https://raw.githubusercontent.com/ssloy/tinyrenderer/gh-pages/img/00-home/africanhead.png)

提醒：这是一份可以轻松重复OpenGL结构的材料，它使一个渲染器。**我不想教学如何去写OpenGL程序，我想教学OpenGL是如何工作的**，我深信如果不理解工作原理是不会写出高效的3D程序的。

最后的代码只有500行。我的学生需要10-20小时制作这个渲染器。输入：包含多边形信息的文件+纹理图像。输出：程序会生成一幅图像。

目标是最大程度地减少外部的依赖，我为我的学生提供了一堂课去学习TGA文件。TGA是一种支持RGB/RGBA/Black/White的一种图像格式。所以，开始，我们获得一种简单的处理图片的方式。应该注意，除了加载和保存图像外，一开始的功能就是设置一个像素的颜色。

没有方法去绘制直线和三角形。我们需要自己去写，我提供我与我的学生共同抒写的源代码。但是我不建议你使用它，这是没有意义的。完整的代码在github上可以找到，在这里[here](https://github.com/ssloy/tinyrenderer/tree/909fe20934ba5334144d2c748805690a1fa4c89f)你可以找到我给我学生的源代码。

```C++
#include "tgaimage.h"
const TGAColor white = TGAColor(255, 255, 255, 255);
const TGAColor red   = TGAColor(255, 0,   0,   255);
int main(int argc, char** argv) {
        TGAImage image(100, 100, TGAImage::RGB);
        image.set(52, 41, red);
        image.flip_vertically(); // i want to have the origin at the left bottom corner of the image
        image.write_tga_file("output.tga");`
        return 0;
}
```

output.tga 是这样的

![](https://raw.githubusercontent.com/ssloy/tinyrenderer/gh-pages/img/00-home/reddot.png)

# 使用此渲染器渲染的一些案例

![](https://raw.githubusercontent.com/ssloy/tinyrenderer/gh-pages/img/00-home/demon.png)

![](https://raw.githubusercontent.com/ssloy/tinyrenderer/gh-pages/img/00-home/diablo-glow.png)

![](https://raw.githubusercontent.com/ssloy/tinyrenderer/gh-pages/img/00-home/boggie.png) 

![](https://raw.githubusercontent.com/ssloy/tinyrenderer/gh-pages/img/00-home/diablo-ssao.png)


# Reference

1. https://github.com/ssloy/tinyrenderer

2. https://zhuanlan.zhihu.com/cgnotes
