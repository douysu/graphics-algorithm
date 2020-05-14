# [从零构建光栅渲染器] 5.移动摄像机

非常感谢和推荐Sokolov的教程，Sokolov使用500行C++代码实现一个光栅渲染器。教程学习过程非常平滑，从画点、线和三角形开始教学，在逐步深入三维变换，投影，再到顶点着色器，片段着色器等等。教程地址：<https://github.com/ssloy/tinyrenderer>。Sokolov的教程为英文，我翻译了其文章。

在学习过程中，有些内容可能您可能云里雾里，这时就需要查阅《计算机图形学》的书籍了，这里面的算法和公式可以帮助您理解代码。

作者：憨豆酒（YinDou），联系我yindou97@163.com，熟悉图形学，图像处理领域，本章的源代码可在此仓库中找到<https://github.com/douysu/person-summary>：如果对您有帮助，还请给一个star，如果大家发现错误以及不合理之处，还希望多多指出。

[我的知乎](https://zhuanlan.zhihu.com/c_1218472587279433728)

[我的Github](https://link.zhihu.com/?target=https%3A//github.com/douysu)

[我的博客](https://link.zhihu.com/?target=https%3A//blog.csdn.net/ModestBean)

# 本章运行结果

![图片](https://uploader.shimo.im/f/EXeeZw6L67N8KaWA.png!thumbnail)

# 几何当中最后一个重要的点

今天我们将要完成我比较喜欢的额部分，但是一部分读者可能会感到无聊。一旦你掌握了今天的资料，你可以转移到下一节课，今天我们要完成的是我很喜欢的部分，但很多读者觉得很无聊。一旦你掌握了今天的材料，你可以转移到下一节课，在那里我们将实际做渲染。为了让你眼前一亮，这里是我们已经知道的头，使用Gouraud着色。

![图片](https://uploader.shimo.im/f/ClaxjuD37n8bTKKY.png!thumbnail)

我把所有的纹理都去掉了。Gouraud的着色非常简单。我们的3D设计者给了我们模型的每个顶点的法线向量，它们可以在.obj文件的 "vn x y z "行中找到。我们计算出每个顶点的强度（而不是像以前的平面着色那样每个三角形的强度），然后简单地在每个三角形内插值，就像我们已经做了z或uv坐标一样。

顺便说一下，在3D艺术家不是那么好心的情况下，你可以重新计算法向量作为与点相关的所有面的平均向量。前我用来生成这个图像的代码可以在这里找到。[here](https://github.com/ssloy/tinyrenderer/tree/10723326bb631d081948e5346d2a64a0dd738557).

# 三维空间中的基准坐标系变换

在欧几里得空间中，坐标可以由一个点（原点）和一个基点给出。点P在坐标系(O，i，j，k)中的坐标是(x，y，z)意味着什么？意味着，向量OP使用如下表示：

![图片](https://uploader.shimo.im/f/0L3IbGHZkk43MbIb.png!thumbnail)

现在图片中有了另一个坐标系(O', i',j',k')。我们应该怎样从一个坐标系转换到另一个坐标系呢。首先我们需要明白(i,j,k) and (i',j',k') 都是3D的基准坐标系，这里存在一个（非退化）矩阵M可以完成这个操作：

![图片](https://uploader.shimo.im/f/vS5UEnO5yJin8Cqe.png!thumbnail)

让我们绘制一下插图：

![图片](https://uploader.shimo.im/f/y3VwJy8X7AUWbauK.png!thumbnail)

**翻译作者内容**：坐标系变换。

让我重新整理一下向量OP：

![图片](https://uploader.shimo.im/f/bCMeCznSZ0IjD9dM.png!thumbnail)

**翻译作者内容**：这个公式这么理解，向量OO'这里的O'就相当于下面公式中的P点，代入即可

![图片](https://uploader.shimo.im/f/3iERjMaipaOqDpfq.png!thumbnail)

现在，让我们用基矩阵替换右边的(i',j',k')，如下：

![图片](https://uploader.shimo.im/f/z2DQlakpvZjciQfZ.png!thumbnail)

这就给了我们转换公式，从一个基准坐标系到另一个基准坐标系：

![图片](https://uploader.shimo.im/f/gl5xDnxQk2k2zqDh.png!thumbnail)

# 让我们创建我们的视角矩阵gluLookAt

OpenGL，因此，我们的小渲染器只能用位于Z轴上的摄像头来绘制场景。如果我们想移动摄像头，没有问题，我们可以移动所有的场景，让摄像头不动。

让我们这么说吧：我们要画一个位于e点（眼睛）的摄像头的场景，摄像头应该对准c点（中心），这样，给定的向量u（向上）在最终的渲染中是垂直的。

说明图：

![图片](https://uploader.shimo.im/f/Csin8T1rTcjMbjKw.png!thumbnail)

这意味着我们要在坐标系(c,x',y',z')中进行渲染。但是，我们的模型是在坐标系(O,x,y,z)中给出的...... 没有问题，我们需要的是计算坐标的变换。下面是一个计算必要的4x4矩阵ModelView的C++代码。

```plain
void lookat(Vec3f eye, Vec3f center, Vec3f up) {
    Vec3f z = (eye-center).normalize();
    Vec3f x = cross(up,z).normalize();
    Vec3f y = cross(z,x).normalize();
    Matrix Minv = Matrix::identity();
    Matrix Tr   = Matrix::identity();
    for (int i=0; i<3; i++) {
        Minv[0][i] = x[i];
        Minv[1][i] = y[i];
        Minv[2][i] = z[i];
        Tr[i][3] = -center[i];
    }
    ModelView = Minv*Tr;
}
```
注意，z'是由向量ce给出的（不要忘记将其归一化，这对以后的计算有帮助）。我们如何计算x'？很简单，就是通过u和z'叉乘。然后我们计算y'，使其与已经计算出的x'和z'正交（让我提醒你，在我们的问题设置中，ce和u不一定是正交的）。最后一步是将原点平移到中心c，我们的变换矩阵就准备好了。现在只需在模型帧中得到坐标为(x,y,z,1)的任意点，乘以矩阵ModelView，就可以得到相机坐标系中的坐标。顺便说一下，ModelView这个名字来自于OpenGL的术语。

# Viewport视口矩阵

如果你从一开始就跟着这门课程走，你应该还记得像这样奇怪的代码。

```plain
screen_coords[j] = Vec2i((v.x+1.)*width/2., (v.y+1.)*height/2.);
```
它是什么意思？意思是我有一个点Vec2f v，它属于正方形[-1,1][-1,1]。我想把它画在（width，height）的图像中。值(v.x+1)在0和2之间变化，(v.x+1)/2在0和1之间变化，(v.x+1)*width/2也就是在0~width之间变化。这样，我们有效地将双单元方块映射到图像上。

但是现在我们要摆脱这些丑陋的构造，我想用矩阵形式重写所有的计算。让我们考虑一下下面的C++代码。

```plain
Matrix viewport(int x, int y, int w, int h) {
    Matrix m = Matrix::identity(4);
    m[0][3] = x+w/2.f;
    m[1][3] = y+h/2.f;
    m[2][3] = depth/2.f;
    m[0][0] = w/2.f;
    m[1][1] = h/2.f;
    m[2][2] = depth/2.f;
    return m;
}
```
![图片](https://uploader.shimo.im/f/ZPqotO4ja8CHrohc.png!thumbnail)

意思是把双单元立方体[-1,1][-1,1][-1,1][-1,1]映射到屏幕立方体[x,x+w][y,y+h]*[0,d]上。对了，是立方体，而不是矩形，这是因为用Z-缓冲区进行深度计算。这里d是z-缓冲区的分辨率。我喜欢把它等于255，因为这样做是为了简单地倾倒z-缓冲区的黑白图像进行调试。

在OpenGL的术语中，这个矩阵被称为视口矩阵。

# 坐标连续变换

所以，让我们总结一下。我们的模型（比如说角色）是在自己的局部框架（对象坐标）中创建的。它们被插入到以世界坐标表示的场景中。从一个到另一个的转换是用矩阵模型进行的。然后，我们要用相机（眼睛坐标）来表达，这个变换称为View。然后，我们用投影矩阵(第4课)对场景进行变形，产生透视变形，这个矩阵将场景变换成所谓的剪辑坐标。最后，我们绘制场景，将剪贴坐标转化为画面坐标的矩阵称为Viewport。

同样的，如果我们从.obj文件中读取一个点v，那么要在屏幕上画出这个点，需要经过以下的变换链。

```plain
Viewport * Projection * View * Model * v.
```
如果你看了这个提交 [this](https://github.com/ssloy/tinyrenderer/blob/10723326bb631d081948e5346d2a64a0dd738557/main.cpp)，你会看到以下几行。

```plain
Vec3f v = model->vert(face[j]);
screen_coords[j] =  Vec3f(ViewPort*Projection*ModelView*Matrix(v));
```
由于我只画了一个对象，所以矩阵模型等于本身，我将其与矩阵视图合并。

# 法向量变换

有一个广为人知的事实。

如果我们有一个模型，它的法向量是由设计者给定的，并且这个模型是用仿射映射进行变换的，那么法向量要用映射进行变换，等于原映射矩阵的逆矩阵的转置

什么--什么--什么--！？我遇到不少程序员都知道这个事实，但对他们来说，这仍然是一个黑魔法。其实，其实也没那么复杂。拿一支铅笔，画一个二维三角形(0,0)、(0,1)、(1,0)和一个向量n，在下图中的法线上。自然，n等于（1，1）。然后，让我们将所有的y坐标扩展2的系数，保留x坐标不变。这样，我们的三角形就变成了(0,0), (0,2), (1,0). 如果我们用同样的方法变换向量n，它就会变成(1，2)，并且它不再是三角形的正交边。

因此，为了消除所有的黑魔法，我们需要明白一个简单的道理：**我们不需要简单的变换法向量（因为它们可以变得不再是法向量），我们需要计算（新的）法向量到变换后的模型。**

回到三维，我们有一个向量n = (A,B,C)。我们知道经过原点的平面，法线为n，有一个方程Ax+By+Cz=0，让我们把它写成矩阵形式（我从一开始就用其次坐标）。

![图片](https://uploader.shimo.im/f/ACVn3dnRFjxH2qH7.png!thumbnail)

回想一下，(A,B,C)-是一个向量，所以我们在嵌入到4D中时将其增强为0，而(x,y,z)因为是一个点，所以将其增强为1。

让我们在两者之间插入一个身份矩阵（逆向M乘以M等于身份矩阵）。

![图片](https://uploader.shimo.im/f/3G7xOCqkbBMIXG9O.png!thumbnail)

右边括号中的表达式--是对被变换的对象的点。左边的表达式--是对被变换对象的法向量! 在标准的惯例中，我们通常把坐标写成列（请大家不要把反向量和共变向量的东西都提出来），所以我们可以把前面的表达式改写成如下。

![图片](https://uploader.shimo.im/f/gElq2wSG9BWB8Bb3.png!thumbnail)

而左边的括号告诉我们，通过应用仿射映射的反转置矩阵，可以从旧法线计算出被变换对象的法线。

请注意，如果我们变换矩阵M是缩放，旋转，平移组成，那么M和它的逆转职是相等的，这种情况下逆和转置是相互抵消的。但是如果包括了投影，其是仿射变换，就需要上面的技巧了。

在目前的代码中，我们没有使用法线向量的变换，但在下一节课中，它将会非常非常方便。

祝你编码愉快!

