## 拖尾刀光
模块运行平台为Android，其是使用OpenGL ES开发的切水果的刀光效果，模块可应用于割绳子游戏、飞机尾焰、武器挥舞特效等。开发环境为Android Studio 3.2，OpenGL ES 3.0，SDK,打开项目时请使用import project。模块关键部分:（1）MySurfaceView类中的触控方法onTouchEvent。（2）包com.bn.streak下的四个算法核心类。

## 实现效果
![这里写图片描述](./result/result.gif)
## 实现算法
如若不需要Android平台的，需要通过刀光算法去实现。  算法在我的一篇博客，链接为  https://blog.csdn.net/ModestBean/article/details/79245439
## 模块重构

为了方便大家共同交流学习，我对模块进行了升级，现在为Version 2。Version 2版本按照Google C++编程规范修改了部分注释和变量名称，具体如下。

 - 添加详细类注释，概括类的功能。
 - 修改了部分变量名称。
 - 升级项目环境为Android Studio 3.2，原版本为2.2，已经淘汰。

## PS
项目的重构真耗费时间和精力，年轻时写的程序给自己埋了很多雷，现已修改了部分，还有部分未解决。