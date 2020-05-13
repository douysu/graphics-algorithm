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

