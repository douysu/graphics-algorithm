# 拖尾刀光

作者：憨豆酒（YinDou），yindou97@163.com，熟悉图形学，图像处理领域，经常更新的学习总结仓库：<https://github.com/douysu/person-summary> 如果对您有帮助还请帮忙点一个star，如果大家发现错误以及不合理之处，还希望多多指出。

- [我的Github](https://github.com/douysu)
- [我的博客](https://blog.csdn.net/ModestBean)
- [我的知乎](https://zhuanlan.zhihu.com/c_1218472587279433728)

# 介绍

模块运行平台为Android，其是使用OpenGL ES开发的切水果的刀光效果，模块可应用于割绳子游戏、飞机尾焰、武器挥舞特效等。
![这里写图片描述](./result/result.gif)

# 实现算法

[算法介绍](https://blog.csdn.net/ModestBean/article/details/79245439)

如若不需要OpenGL ES版本的，需要自己根据算法修改代码。

# 需要

- Android Studio全家桶
- OpenGL ES 3.0 
- Java

# 运行

git clone，打开Android Studio import project。 点击run。

核心代码块：（1）MySurfaceView类中的触控方法onTouchEvent。（2）com.bn.streak下的四个算法核心类。

# 模块重构

为了方便大家共同交流学习，我对模块进行了升级，现在为Version 2。修改详情如下。
 - 添加详细类注释，概括类的功能。
 - 修改了部分变量名称。
 - 升级项目环境为Android Studio 3.2，原版本为2.2，已经淘汰。
