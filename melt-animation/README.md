# 物理动画流体模拟Fluid Simulation

作者：憨豆酒（YinDou），yindou97@163.com，熟悉图形学，图像处理领域，经常更新的学习总结仓库：[https://github.com/douysu/person-summary](https://github.com/douysu/person-summary) 如果对您有帮助还请帮忙点一个star，如果大家发现错误以及不合理之处，还希望多多指出。

[我的知乎](https://zhuanlan.zhihu.com/graphics-douysu)

[我的Github](https://github.com/douysu)

[我的博客](https://blog.csdn.net/ModestBean)

# 0 介绍

以下为在实验室完成的效果，特殊原因，实验室代码不方便开源，找到了其他开源代码，实现过程如下。

![图片](https://uploader.shimo.im/f/8znanwmtzgLT1Iuv.gif)


Reference：

* [https://github.com/doyubkim/fluid-engine-dev](https://github.com/doyubkim/fluid-engine-dev)
# 1 运行

环境：Ubuntu 18.04.4 LTS 其他操作系统查看原仓库，macOS和Win。

## 1.1 Clone

```plain
git clone https://github.com/doyubkim/fluid-engine-dev.git --recursive
cd fluid-engine-dev
```
## 1.2 Building from Ubuntu

```plain
sudo apt-get install build-essential python-dev python-pip cmake
```
```plain
sudo apt-get install libtbb-dev
```
```plain
mkdir build
cd build
cmake ..
make
```
## 1.3 Run Demo

生成每一帧数据。

```plain
./bin/hybrid_liquid_sim -f 10 -m pos
```
![图片](https://uploader.shimo.im/f/uv42zPUy69P3J4Rz.png!thumbnail)

生成obj。

```plain
./bin/particles2obj -i ./hybrid_liquid_sim_output/frame_000000.pos -r 100,200,100 -g 0.01 -k 0.04 -m zhu_bridson -o ./hybrid_liquid_sim_output/frame_000000.obj
```
## 1.4 导入obj到渲染器完成渲染

Blender渲染器或者Mitsuba renderer渲染器，或者其他您熟练的渲染器。 如果您使用Blender，这里可以供下载我使用的场景文件，场景可能不够完美，因为不是特别擅长美工......

下载场景：

* Google：[https://drive.google.com/file/d/1M511EWM5tiRGgowY9sVHgRbZGaweie1C/view?usp=sharing](https://drive.google.com/file/d/1M511EWM5tiRGgowY9sVHgRbZGaweie1C/view?usp=sharing)

Baidu：链接：[https://pan.baidu.com/s/1dgVHBwL607WLEwVw8vButQ](https://pan.baidu.com/s/1dgVHBwL607WLEwVw8vButQ)  提取码：o4li

![图片](https://uploader.shimo.im/f/xw5L5H7zmEkIcgJx.png!thumbnail)

### 

