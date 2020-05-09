# [从零构建光栅渲染器] 2.三角形栅格化和背面剪裁
非常感谢和推荐Sokolov的教程，Sokolov使用500行C++代码实现一个光栅渲染器。教程学习过程非常平滑，从画点、线和三角形开始教学，在逐步深入三维变换，投影，再到顶点着色器，片段着色器等等。教程地址：<https://github.com/ssloy/tinyrenderer>。Sokolov的教程为英文，我翻译了其文章。

在学习过程中，有些内容可能您可能云里雾里，这时就需要查阅《计算机图形学》的书籍了，这里面的算法和公式可以帮助您理解代码。

作者：憨豆酒（YinDou），联系我yindou97@163.com，熟悉图形学，图像处理领域，本章的源代码可在此仓库中找到<https://github.com/douysu/person-summary>：如果对您有帮助，还请给一个star，如果大家发现错误以及不合理之处，还希望多多指出。

[我的知乎](https://zhuanlan.zhihu.com/c_1218472587279433728)

[我的Github](https://github.com/douysu)

[我的博客](https://blog.csdn.net/ModestBean)

# 本章运行结果

![图片](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91cGxvYWRlci5zaGltby5pbS9mL2ozTlRWUkRBUXhnSk80cTgucG5nIXRodW1ibmFpbA?x-oss-process=image/format,png)

# 开始
嗨，朋友们，这是我。

![图片](https://uploader.shimo.im/f/1ofvkE8r4cmU24lu.png!thumbnail)

更准确的说，这是程序渲染的我的脸部模型。我们也会在后面一个或两个小时学习。上一次课程我们绘制了三维线面模型。这次，我们将会填充多边形或者三角形。事实上，OpenGL对多边形进行了三角分解，因此不需要考虑特别复杂的情况。

提醒：这系列文章是教学让你自己写程序，当我说两个小时内你就能画出像上面的图时，我并不是说的是你阅读我的代码。是时候从头开始创建你自己的代码了。我的代码是为了和你自己的程序进行比较。我是一个糟糕的程序员，你可能是一个很好的程序员。请不要简单的复制粘贴我的代码。欢迎任何的评论和意见。

# 旧方法，扫线法
因此，此任务是绘制二维三角形，对于有上进心的学生，一般需要几个小时，即使他们变成能力比较差。上次课程我们学习了Bresenham线绘制算法，今天的任务是填充三角形，非常有趣，但是这次任务不简单，我不知道为什么，但是确实是这样。我的学生们大多数在这个课程上困惑了许久。所以，最初的代码是这样的：

```
void triangle(Vec2i t0, Vec2i t1, Vec2i t2, TGAImage &image, TGAColor color) { 
    line(t0, t1, image, color); 
    line(t1, t2, image, color); 
    line(t2, t0, image, color); 
}
// ...
Vec2i t0[3] = {Vec2i(10, 70),   Vec2i(50, 160),  Vec2i(70, 80)}; 
Vec2i t1[3] = {Vec2i(180, 50),  Vec2i(150, 1),   Vec2i(70, 180)}; 
Vec2i t2[3] = {Vec2i(180, 150), Vec2i(120, 160), Vec2i(130, 180)}; 
triangle(t0[0], t0[1], t0[2], image, red); 
triangle(t1[0], t1[1], t1[2], image, white); 
triangle(t2[0], t2[1], t2[2], image, green);
```
![图片](https://uploader.shimo.im/f/Dfo5egViPVI49odw.png!thumbnail)

如往常一样，相应的commit可以在这里找到[here](https://github.com/ssloy/tinyrenderer/tree/7e46cc57fa3f5a41129d6b6fefe4e77f77b8aa84)，这里的代码很简单：我提供三个三角形用来初始的调试。如果我们在triangle方法中调用line()函数，就会得到三角形的轮廓，如何画出一个填充的三角形？

一个好的绘制三角形的方法应该有以下几个特点：

* 应该是简单和高效的
* 对称的，图片不应该取决于传递给绘制函数的顶点顺序
* 我们可以增加更多的需求，但是先做这些吧。传统的线段扫描使用的是：
1. 通过y坐标对顶点进行排序。
2. 对左右两边的三角形同时进行光栅化。
3. 在左右边界点绘制一条水平的直线。

此时，我的学生开始不知所措：那一块是左边部分，那一块是右边部分。此外，在这个三角形里有三部分......通常，我会给我的学生介绍一个小时：在重复一次，把你自己的代码与我的进行比较要比直接阅读我的代码有意义的多。

【一个小时过去】

我应该如何绘制一个三角形？（再说一次，如果你有好的方法，我非常希望你能采用它。）让我们假设三角形的三个点t0，t1, t2，通过y坐标的大小排序成升序。 那么，边界A在是t0和t2之间的线段，边界B是t0和t1之间线段，最后的在t1和t2之间的线段。

边界A是红色的，边界B是绿色的，如图所示：

![图片](https://uploader.shimo.im/f/E0wOvxSt1csLNxpb.png!thumbnail)

不幸的是，边界B有两部分。让我们先绘制底部部分（通过水平线分割成的上下两部分）。

```
void triangle(Vec2i t0, Vec2i t1, Vec2i t2, TGAImage &image, TGAColor color) { 
    // sort the vertices, t0, t1, t2 lower−to−upper (bubblesort yay!) 
    if (t0.y>t1.y) std::swap(t0, t1); 
    if (t0.y>t2.y) std::swap(t0, t2); 
    if (t1.y>t2.y) std::swap(t1, t2); 
    int total_height = t2.y-t0.y; 
    for (int y=t0.y; y<=t1.y; y++) { 
        int segment_height = t1.y-t0.y+1; 
        float alpha = (float)(y-t0.y)/total_height; 
        float beta  = (float)(y-t0.y)/segment_height; // be careful with divisions by zero 
        Vec2i A = t0 + (t2-t0)*alpha; 
        Vec2i B = t0 + (t1-t0)*beta; 
        image.set(A.x, y, red); 
        image.set(B.x, y, green); 
    } 
}
```
![图片](https://uploader.shimo.im/f/xywdxk5bBkolyjbm.png!thumbnail)

注意线段不是连续的，上次当我们绘制直线的时候我们绘制了连续的线段，但是在这里，我们没有理会旋转图像（还记得上次课程的x，y交换吗？）为什么？因为我们在后面进行三角形填充，如果我们用水平线连接相应的点对，空隙就会消失。

![图片](https://uploader.shimo.im/f/l9PTANRx5nWpsNVn.png!thumbnail)

现在，让我们绘制上面的一半三角形，我们可以在加入一个循环：

```
void triangle(Vec2i t0, Vec2i t1, Vec2i t2, TGAImage &image, TGAColor color) { 
    // sort the vertices, t0, t1, t2 lower−to−upper (bubblesort yay!) 
    if (t0.y>t1.y) std::swap(t0, t1); 
    if (t0.y>t2.y) std::swap(t0, t2); 
    if (t1.y>t2.y) std::swap(t1, t2); 
    int total_height = t2.y-t0.y; 
    for (int y=t0.y; y<=t1.y; y++) { 
        int segment_height = t1.y-t0.y+1; 
        float alpha = (float)(y-t0.y)/total_height; 
        float beta  = (float)(y-t0.y)/segment_height; // be careful with divisions by zero 
        Vec2i A = t0 + (t2-t0)*alpha; 
        Vec2i B = t0 + (t1-t0)*beta; 
        if (A.x>B.x) std::swap(A, B); 
        for (int j=A.x; j<=B.x; j++) { 
            image.set(j, y, color); // attention, due to int casts t0.y+i != A.y 
        } 
    } 
    for (int y=t1.y; y<=t2.y; y++) { 
        int segment_height =  t2.y-t1.y+1; 
        float alpha = (float)(y-t0.y)/total_height; 
        float beta  = (float)(y-t1.y)/segment_height; // be careful with divisions by zero 
        Vec2i A = t0 + (t2-t0)*alpha; 
        Vec2i B = t1 + (t2-t1)*beta; 
        if (A.x>B.x) std::swap(A, B); 
        for (int j=A.x; j<=B.x; j++) { 
            image.set(j, y, color); // attention, due to int casts t0.y+i != A.y 
        } 
    } 
}
```
![图片](https://uploader.shimo.im/f/rJaeBrYLtKkUQBKO.png!thumbnail)

这些可能是够了，但是代码的重复太多了。这就是我为什么我会把代码弄得可读性稍微差一点，去掉重复代码是为了方便修改和维护：

```
void triangle(Vec2i t0, Vec2i t1, Vec2i t2, TGAImage &image, TGAColor color) { 
    if (t0.y==t1.y && t0.y==t2.y) return; // I dont care about degenerate triangles 
    // sort the vertices, t0, t1, t2 lower−to−upper (bubblesort yay!) 
    if (t0.y>t1.y) std::swap(t0, t1); 
    if (t0.y>t2.y) std::swap(t0, t2); 
    if (t1.y>t2.y) std::swap(t1, t2); 
    int total_height = t2.y-t0.y; 
    for (int i=0; i<total_height; i++) { 
        bool second_half = i>t1.y-t0.y || t1.y==t0.y; 
        int segment_height = second_half ? t2.y-t1.y : t1.y-t0.y; 
        float alpha = (float)i/total_height; 
        float beta  = (float)(i-(second_half ? t1.y-t0.y : 0))/segment_height; // be careful: with above conditions no division by zero here 
        Vec2i A =               t0 + (t2-t0)*alpha; 
        Vec2i B = second_half ? t1 + (t2-t1)*beta : t0 + (t1-t0)*beta; 
        if (A.x>B.x) std::swap(A, B); 
        for (int j=A.x; j<=B.x; j++) { 
            image.set(j, t0.y+i, color); // attention, due to int casts t0.y+i != A.y 
        } 
    } 
}
```
[这里的commit](https://github.com/ssloy/tinyrenderer/tree/024ad4619b824f9179c86dc144145e2b8b155f52)是绘制2D三角形的。
# 我的代码所采用的方法
虽然不是很复杂，但是线段扫描的代码有点乱。此外，这真是一个很老的为单线程的CPU编程设计的方法。让我们来看下面的伪代码：

**翻译作者内容：**作者的意思是可以更好的更快的方法去实现填充三角形，例如多线程。

```
triangle(vec2 points[3]) { 
    vec2 bbox[2] = find_bounding_box(points); 
    for (each pixel in the bounding box) { 
        if (inside(points, pixel)) { 
            put_pixel(pixel); 
        } 
    } 
}
```
你喜欢这个吗？寻找边界盒是真的很简单的。检查一个点是否属于二维三角形（任何凸边形）是没问题的。
题外话：如果我不得不实现代码去检查顶点是否属于多边形，这个程序会局限在平面内，我可能将会困于平面的检查。事实证明，可靠地解决一项非常困难的任务，是不容易的。我们这里只绘制像素，我还是能接受这个。

关于这段伪代码，我还有一件喜欢的事情，编程菜鸟会非常热情的接受这段代码，有经验的程序员会笑着说“哪一个白痴写的？”一个计算机图形学专家则会耸耸肩说：“这就是现实生活中的工作方式。”多线程并行计算改变了人们的思考方式。

**翻译作者内容**：作者的意思就是说这段代码没有使用并行计算，用的是比较老的代码。

好的，让我们开始：首先我们需要知道什么是[重心坐标](https://en.wikipedia.org/wiki/Barycentric_coordinate_system)。一个三角形包括三个点A、B、C，还有一个点P，都是笛卡尔坐标系(xy)。我们的目标是寻找点P相对于三角形ABC的重心坐标。它意味着我们需要寻找三个值(1 - u - v, u, v)，我们找到的P点如下所示：

![图片](https://uploader.shimo.im/f/qPPqWa19mYnjTZzq.png!thumbnail)

乍一看有点吓人，其实非常简单。想象一下我们同时给顶点A，B，C设置三个权重(1-u-v, u, v)。P点是重心，也就是说，点P可以使用下面的公式可以表示：

![图片](https://uploader.shimo.im/f/z50kGGNq0EoHOE8a.png!thumbnail)

**翻译作者内容**：涉及到向量的知识，空间的任意一个向量都可以使用其他两个向量的和来表示。

这是一个非常简单的向量公式，带有两个变量x坐标、y坐标的线性方程如下所示：

![图片](https://uploader.shimo.im/f/4b8BBzi2Xdse0I6p.png!thumbnail)

我非常懒，不想以学术的方式来解决这个线性系统，让我们以矩阵来编写它。

![图片](https://uploader.shimo.im/f/IiDJgpK8950jzZe2.png!thumbnail)

这意味着我们正在寻找同时与（ABx，ACx，PAx）和（ABy，ACy，PAy）正交的向量（u，v，1）。希望您能明白我的思路。这里有一个小提示：要在平面内找到两条直线的叫点，只需要计算叉积即可。顺便考考自己，如果找到通过两个定点的直线？

因此，让我们来编程我们新的光栅化路线：我们对给定三角形的所有像素进行迭代。对于每个像素，我们计算它的双心坐标。然后，它至少有一个负分量，那么这个像素是在三角形之外。这样看程序就比较清楚了。

```
#include <vector> 
#include <iostream> 
#include "geometry.h"
#include "tgaimage.h" 
 
const int width  = 200; 
const int height = 200; 
 
Vec3f barycentric(Vec2i *pts, Vec2i P) { 
    Vec3f u = cross(Vec3f(pts[2][0]-pts[0][0], pts[1][0]-pts[0][0], pts[0][0]-P[0]), Vec3f(pts[2][1]-pts[0][1], pts[1][1]-pts[0][1], pts[0][1]-P[1]));
    /* `pts` and `P` has integer value as coordinates
       so `abs(u[2])` < 1 means `u[2]` is 0, that means
       triangle is degenerate, in this case return something with negative coordinates */
    if (std::abs(u[2])<1) return Vec3f(-1,1,1);
    return Vec3f(1.f-(u.x+u.y)/u.z, u.y/u.z, u.x/u.z); 
} 
 
void triangle(Vec2i *pts, TGAImage &image, TGAColor color) { 
    Vec2i bboxmin(image.get_width()-1,  image.get_height()-1); 
    Vec2i bboxmax(0, 0); 
    Vec2i clamp(image.get_width()-1, image.get_height()-1); 
    for (int i=0; i<3; i++) { 
        for (int j=0; j<2; j++) { 
            bboxmin[j] = std::max(0,        std::min(bboxmin[j], pts[i][j])); 
            bboxmax[j] = std::min(clamp[j], std::max(bboxmax[j], pts[i][j])); 
        } 
    } 
    Vec2i P; 
    for (P.x=bboxmin.x; P.x<=bboxmax.x; P.x++) { 
        for (P.y=bboxmin.y; P.y<=bboxmax.y; P.y++) { 
            Vec3f bc_screen  = barycentric(pts, P); 
            if (bc_screen.x<0 || bc_screen.y<0 || bc_screen.z<0) continue; 
            image.set(P.x, P.y, color); 
        } 
    } 
} 
 
int main(int argc, char** argv) { 
    TGAImage frame(200, 200, TGAImage::RGB); 
    Vec2i pts[3] = {Vec2i(10,10), Vec2i(100, 30), Vec2i(190, 160)}; 
    triangle(pts, frame, TGAColor(255, 0, 0)); 
    frame.flip_vertically(); // to place the origin in the bottom left corner of the image 
    frame.write_tga_file("framebuffer.tga");
    return 0; 
}
```
barycentric()函数计算一个给定三角形中的P点坐标，我们已经看到了细节。现在让我们来看看triangle()函数是如何工作的。首先，它计算边界盒，它被两个顶点描述：左下角和右上角。为了找到这些角，我们迭代了三角形的所有顶点并且找到最小/最大的坐标。我也给屏幕加入了边界框剪裁，减少CPU计算时间（去掉屏幕外的三角形）。恭喜你，你知道了如何绘制三角形。

![图片](https://uploader.shimo.im/f/PPzUHBunoVYuiusE.png!thumbnail)

# 平面着色渲染
我们早已经知道如何绘制基于空的三角形的模型。让我们使用随机颜色来填充它们。这些会帮助我们看到如何编码去填充。代码如下：

```
for (int i=0; i<model->nfaces(); i++) { 
    std::vector<int> face = model->face(i); 
    Vec2i screen_coords[3]; 
    for (int j=0; j<3; j++) { 
        Vec3f world_coords = model->vert(face[j]); 
        screen_coords[j] = Vec2i((world_coords.x+1.)*width/2., (world_coords.y+1.)*height/2.); 
    } 
    triangle(screen_coords[0], screen_coords[1], screen_coords[2], image, TGAColor(rand()%255, rand()%255, rand()%255, 255)); 
}
```
这非常简单，好像之前，我们迭代所有三角形，将世界坐标转换为屏幕坐标并绘制三角形。我会后面的文章详细描述各种坐标系，现在的效果这样：
![图片](https://uploader.shimo.im/f/aoUM7CH8hbvN9A5D.png!thumbnail)

让我们摆脱这些丑陋的颜色并放上一些光线。一个道理：在相同的光照强度下，当多边形与光照方向正交时，多边形被照得最亮。

让我们来比较一下：

![图片](https://uploader.shimo.im/f/rsBopMBvzXUusr8Q.png!thumbnail)

![图片](https://uploader.shimo.im/f/xSbUcTAPyJo0MuXc.png!thumbnail)

当多边形与光照方向平行时，我们几乎看不到光照。解释： 光照强度等于光向量和给定三角形的法线的标量乘积。三角形的法线可以简单地计算为其两边的交叉乘积。

话说回来，在这个课程中，我们将对颜色进行线性计算，我们现在忽视gamma校正并且容忍我们不正确的颜色。

```
for (int i=0; i<model->nfaces(); i++) { 
    std::vector<int> face = model->face(i); 
    Vec2i screen_coords[3]; 
    Vec3f world_coords[3]; 
    for (int j=0; j<3; j++) { 
        Vec3f v = model->vert(face[j]); 
        screen_coords[j] = Vec2i((v.x+1.)*width/2., (v.y+1.)*height/2.); 
        world_coords[j]  = v; 
    } 
    Vec3f n = (world_coords[2]-world_coords[0])^(world_coords[1]-world_coords[0]); 
    n.normalize(); 
    float intensity = n*light_dir; 
    if (intensity>0) { 
        triangle(screen_coords[0], screen_coords[1], screen_coords[2], image, TGAColor(intensity*255, intensity*255, intensity*255, 255)); 
    } 
}
```
但是，点积可以是负数。这意味着什么呢？这意味着光可能从多边形后面照过来。如果场景建模很好（通常情况下是这样的），我们可以很简单的丢弃这个三角形。这会允许我们快速的剃除一些看不到的三角，这个叫做背面剪裁。

![图片](https://uploader.shimo.im/f/j3NTVRDAQxgJO4q8.png!thumbnail)

注意，嘴巴的内腔是画在嘴唇上面的。这是因为我们剪裁了不可见的三角形：它只对凸面形状完美地工作。下次我们在编码Z型缓冲区的时候，我们将摆脱这个现象。

[这里是当前渲染器的代码](https://github.com/ssloy/tinyrenderer/tree/e1a3f2b0f9638fa6db9e0437c621132e1baa3fb1)。你发现我的脸部头像有更多的细节了吗？好，我作弊了：里面有25万个三角形，而这个头像模型只有1000个。但是我的脸确实是用上面的代码渲染出来的。我向你保证，在接下来的文章中，我们会给它添加更多的细节。

