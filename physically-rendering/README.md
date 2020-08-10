# 1.Physical Based Rendering

基于OpenGL的Physical Based Rendering，采用IBL（Image Based Light），，实现辐射度量学，Cook-Torrance微表面模型，BRDF，以及Irradiance cube map等。

若有需要，完整的Commit提交记录见此[repository](https://github.com/douysu/physically-rendering).

# 2.运行效果

[[演示视频链接]](https://www.bilibili.com/video/BV1TV411z7qe)

<img src="../result/PBR-OpenGL.png" width=800 >

# 3.实现过程

[[总结文章]](https://zhuanlan.zhihu.com/p/176474625)

文章中概括需要了解学习的知识。

# 4.需要

- Visual Studio 2019（其他版本未测试）
- OpenGL 运行环境

# 5.运行

## 5.1 下载资源

下载PBR纹理和模型，解压到主目录。

Baidu：https://pan.baidu.com/s/1HKUUZzHlQR4yg99MM8lEyw 提取码：1s6e

## 5.2 项目属性设置

（1）绑定Assimp库dll，physically-rendering 属性->调试->环境-> ``PATH=$(SolutionDir)dlls``

（2）平台为Debug，Win32或x86。Release未测试

（3）其他配置下载已设置。包括C/C++附加包含目录，链接器附加库目录、输入等。

# 6 TODO

（1）基于Path Tracing的PBR。采用IBL空间球体光照采样最大的问题是无法实现实时环境反射。也就是说，整个空间的光照都是一开始固定好的，从CubeMap中采样，没有将小球周围的物体考虑在内。基于Path Tracing的PBR可以解决此问题。

（2）加载场景glTF。加载经典场景Sponza。
