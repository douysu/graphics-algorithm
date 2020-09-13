# 拖尾刀光

# 介绍

模块运行平台为Android，其是使用OpenGL ES开发的切水果的刀光效果，模块可应用于割绳子游戏、飞机尾焰、武器挥舞特效等。

<img src = "./result/result.gif" width = 200>
<img src = "./result/streak2.png" width = 200>

# 实现算法

[算法介绍](https://zhuanlan.zhihu.com/p/112252151)

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
