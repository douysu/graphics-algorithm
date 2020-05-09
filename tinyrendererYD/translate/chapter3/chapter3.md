# [从零构建光栅渲染器] 3.隐藏面剃除 z-buffer（深度缓冲）
非常感谢和推荐Sokolov的教程，Sokolov使用500行C++代码实现一个光栅渲染器。教程学习过程非常平滑，从画点、线和三角形开始教学，在逐步深入三维变换，投影，再到顶点着色器，片段着色器等等。教程地址：<https://github.com/ssloy/tinyrenderer>。Sokolov的教程为英文，我翻译了其文章。

在学习过程中，有些内容可能您可能云里雾里，这时就需要查阅《计算机图形学》的书籍了，这里面的算法和公式可以帮助您理解代码。

作者：憨豆酒（YinDou），联系我yindou97@163.com，熟悉图形学，图像处理领域，本章的源代码可在此仓库中找到<https://github.com/douysu/person-summary>：如果对您有帮助，还请给一个star，如果大家发现错误以及不合理之处，还希望多多指出。

[我的知乎](https://zhuanlan.zhihu.com/c_1218472587279433728)

[我的Github](https://github.com/douysu)

[我的博客](https://blog.csdn.net/ModestBean)

# 本章运行结果
![图片](https://uploader.shimo.im/f/FPR5XMl9FAOoobKM.png!thumbnail)

# 引言
你好，让我来给你介绍一下我的黑人朋友z-buffer。他会帮助我们摆脱隐藏面的我们上节课中的视觉假象。

![图片](https://uploader.shimo.im/f/vrbIzT7fobf64Ikv.png!thumbnail)

顺便提一下，我在课程中经常使用创建于[Vidar Rapp](https://se.linkedin.com/in/vidarrapp)的模型。他给了我许可去教学渲染课程并且允许我修改模型，但是我向你承诺接下来会加上眼睛。

好的，回到话题上。理论上在没有丢弃三角形的情况下我可以绘制所有的三角形。如果我从后到前正确的操作，前面的面会挡住后面的面。这个叫做画家算法[painter's algorithm](http://en.wikipedia.org/wiki/Painter%27s_algorithm)。不幸的是，这个伴随着比较高的计算资源：每一次摄像机移动，我们都得对场景进行重新排序。然后，全是动态场景。。。。这不是主要的问题，主要的问题是并非总是能够确定正确的顺序。

# 让我们尝试渲染一个简单的场景
想象一个只有三个三角形的简单场景：摄像机从上而下，我们将这些彩色的三角形投影到一个白色的屏幕上：

![图片](https://uploader.shimo.im/f/duZ8Pir0hJcJ4E4J.png!thumbnail)

这个渲染看起来像这样：

![图片](https://uploader.shimo.im/f/4qpWVYbGIQXR6g7h.png!thumbnail)

蓝色面-在红色的后面还是前面？画家算法在这里是不起作用的。它会将蓝色的面分割成两部分。（一部分在红色前面，一部分在红色后面）然后在红色前面的蓝色部分还会被绿色分割成两部分-一部分在绿色前面，一部分在绿色后面。。。。我认为你会有烦恼：场景中有数百万的三角形，进行计算是非常昂贵的，它可能使用BSP树 [BSP trees](https://en.wikipedia.org/wiki/Binary_space_partitioning) 去解决。顺便提一下，这个数据结构对于相机的移动是不变的，但是它真的很混乱。人生苦短，不能乱了阵脚。

# 更简单的例子：让我们减小一维度，Y-buffer！！！
让我们丢掉一个维度，用黄色的平面去切割场景：

![图片](https://uploader.shimo.im/f/JJzfcaX3oZUtSTs2.png!thumbnail)

我的意思是，现在我们的场景由三个线段构成，黄色平面和每个三角形的交线。最终的渲染具有正常的宽度，但高度为1像素。

![图片](https://uploader.shimo.im/f/1txnxeE7hwG41uBZ.png!thumbnail)

一如既往，这里是[commit](https://github.com/ssloy/tinyrenderer/tree/d9c4b14c0d8c385937bc87cee1178f1e42966b7c)。我们的场景是二维的，所以使用我们第一节课写的line()方法去绘制它是非常简单的。


```
    { // just dumping the 2d scene (yay we have enough dimensions!)
        TGAImage scene(width, height, TGAImage::RGB);

        // scene "2d mesh"
        line(Vec2i(20, 34),   Vec2i(744, 400), scene, red);
        line(Vec2i(120, 434), Vec2i(444, 400), scene, green);
        line(Vec2i(330, 463), Vec2i(594, 200), scene, blue);

        // screen line
        line(Vec2i(10, 10), Vec2i(790, 10), scene, white);

        scene.flip_vertically(); // i want to have the origin at the left bottom corner of the image
        scene.write_tga_file("scene.tga");
    }
```

如果我们从侧面去看，我们的场景是这样的：

![图片](https://uploader.shimo.im/f/vpzJ35r31CMJKSu3.png!thumbnail)

让我们来渲染它。回顾一下，我们的渲染器只有1像素高。在我们的源码中，我创建16像素高的图像为了在高分辨率的屏幕上去查看， rasterize()方法在图像渲染中的第一行写入。

```
        TGAImage render(width, 16, TGAImage::RGB);
        int ybuffer[width];
        for (int i=0; i<width; i++) {
            ybuffer[i] = std::numeric_limits<int>::min();
        }
        rasterize(Vec2i(20, 34),   Vec2i(744, 400), render, red,   ybuffer);
        rasterize(Vec2i(120, 434), Vec2i(444, 400), render, green, ybuffer);
        rasterize(Vec2i(330, 463), Vec2i(594, 200), render, blue,  ybuffer);
```

所以，我声明了一个维度是（width，1）的数组ybuffer，数组初始化值为负无穷大、然后我使用ybuffer和渲染图像作为参数，这个方法是这样：

```
void rasterize(Vec2i p0, Vec2i p1, TGAImage &image, TGAColor color, int ybuffer[]) {
    if (p0.x>p1.x) {
        std::swap(p0, p1);
    }
    for (int x=p0.x; x<=p1.x; x++) {
        float t = (x-p0.x)/(float)(p1.x-p0.x);
        int y = p0.y*(1.-t) + p1.y*t;
        if (ybuffer[x]<y) {
            ybuffer[x] = y;
            image.set(x, 0, color);
        }
    }
}
```
真的非常简单，我从p0.x与p1.x之间开始迭代并且计算线段对应的y坐标。然后我用当前的x索引检查我们在数组ybuffer中得到了什么。如果当前的y值比ybuffer里面的值更加接近相机，我会将它绘制在屏幕上并且更新ybuffer中的值。

让我们一步步的看。在执行rasterize()以后，红色线段在内存中是这样的：

屏幕：

![图片](https://uploader.shimo.im/f/LHFtJODhOQImsXJq.png!thumbnail)

ybuffer：![图片](https://uploader.shimo.im/f/ahkaWCZiQB56EJm9.png!thumbnail)

品红代表着负无穷大。这些对应着我们没有接触到的屏幕。其他接触到的都是灰色。越浅代表越接近摄像机，越黑代表离摄像机越远。

然后我们绘制绿色的线段：

屏幕：

![图片](https://uploader.shimo.im/f/lQr6i55owMgy9sS1.png!thumbnail)

ybuffer：

![图片](https://uploader.shimo.im/f/AljetX3wSCrZabOE.png!thumbnail)

最后是蓝色的。

屏幕：

![图片](https://uploader.shimo.im/f/2r493fAun09Oks2E.png!thumbnail)

ybuffer：

![图片](https://uploader.shimo.im/f/nDRc30CCRxXTmbg0.png!thumbnail)

祝贺，我们在1D屏幕上绘了2D场景。让我们再一次欣赏一下渲染：

![图片](https://uploader.shimo.im/f/KL0eBCKo7QrlEqaP.png!thumbnail)

# 回到3D
所以为了绘制在2D屏幕上，我们需要使用一个二维数组：

```
int *zbuffer = new int[width*height];
```

就我个人而言，我把一个二维的缓冲区打包成一维，转换起来很简单。

```
int idx = x + y*width;
```

转换成x,y：

```
int x = idx % width;int y = idx / width;
```

然后在代码中我迭代了所以三角形，然后用当前三角形和zbuffer的引用来调用rasterizer函数。

比较困难的就是如何计算想绘制像素的z值，让我们回顾我们怎样在y-buffer中计算的y值。

```
        int y = p0.y*(1.-t) + p1.y*t;
```

t变量的性质是什么？（1 - t, t）是点（x，y）相对于线段p0，p1的重心坐标：（x，y）= p0*（1 - t）+ p1 * t。因此，我的想法是采用三角形光栅化的重心坐标，对于我们要绘制的每一个像素，只需要将其重心坐标乘以我们光栅化的三角形顶点z值即可。

```
triangle(screen_coords, float *zbuffer, image, TGAColor(intensity*255, intensity*255, intensity*255, 255));

[...]

void triangle(Vec3f *pts, float *zbuffer, TGAImage &image, TGAColor color) {
    Vec2f bboxmin( std::numeric_limits<float>::max(),  std::numeric_limits<float>::max());
    Vec2f bboxmax(-std::numeric_limits<float>::max(), -std::numeric_limits<float>::max());
    Vec2f clamp(image.get_width()-1, image.get_height()-1);
    for (int i=0; i<3; i++) {
        for (int j=0; j<2; j++) {
            bboxmin[j] = std::max(0.f,      std::min(bboxmin[j], pts[i][j]));
            bboxmax[j] = std::min(clamp[j], std::max(bboxmax[j], pts[i][j]));
        }
    }
    Vec3f P;
    for (P.x=bboxmin.x; P.x<=bboxmax.x; P.x++) {
        for (P.y=bboxmin.y; P.y<=bboxmax.y; P.y++) {
            Vec3f bc_screen  = barycentric(pts[0], pts[1], pts[2], P);
            if (bc_screen.x<0 || bc_screen.y<0 || bc_screen.z<0) continue;
            P.z = 0;
            for (int i=0; i<3; i++) P.z += pts[i][2]*bc_screen[i];
            if (zbuffer[int(P.x+P.y*width)]<P.z) {
                zbuffer[int(P.x+P.y*width)] = P.z;
                image.set(P.x, P.y, color);
            }
        }
    }
}
```

我们在上一节课中做了很小的改动，将隐藏的部分丢弃，真的太好了！这是渲染结果：

![图片](https://uploader.shimo.im/f/DMUTkvtbWe8xbxFb.png!thumbnail)

源码在这里 [here](https://github.com/ssloy/tinyrenderer/tree/68a5ae382135d679891423fb5285fdd582ca389d).

# 好的，我们对z进行了插值，我们还能做什么？
纹理！这是我们的作业。

**翻译作者内容**：我已经实现这个效果了，代码在这里可以找到：[https://github.com/douysu/tinyrendererYD/tree/master/03_00_Texture](https://github.com/douysu/tinyrendererYD/tree/master/03_00_Texture)

在.obj文件中有一行是以"vt u v”开始的，这是纹理坐标数组。"f x/x/x x/x/x x/x/x"中间的x是这个三角形的纹理坐标，将其插入三角形内，乘以纹理图像的宽度和高度，你会得到你需要放到渲染器中的颜色。

漫反射纹理可以在这里找到。 [here](https://github.com/ssloy/tinyrenderer/raw/master/obj/african_head/african_head_diffuse.tga).

我期望你能得到下面的结果：

![图片](https://uploader.shimo.im/f/wARxgaWRNRohzhNw.png!thumbnail)

