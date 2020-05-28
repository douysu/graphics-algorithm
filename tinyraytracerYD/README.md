# 光线追踪算法实践

发现对光追算法介绍的文章比较多，很少有从头到尾教学写一个光追的案例的文章。本文的学习路线非常平滑，从学习光追原理开始，到添加球体，到折射反射，阴影等，大概需要10多个小时就可以学懂（或者更快，-大笑）。

作者：憨豆酒（YinDou），yindou97@163.com，熟悉图形学，图像处理领域，经常更新的学习总结仓库：<https://github.com/douysu/person-summary> 如果对您有帮助还请帮忙点一个star，如果大家发现错误以及不合理之处，还希望多多指出。

- [我的知乎](https://zhuanlan.zhihu.com/c_1218472587279433728)
- [我的Github](https://github.com/douysu)
- [我的博客](https://blog.csdn.net/ModestBean)

# 实现效果

最终实现效果如下图所示：

<img src="../result/tinyraytracer.jpg" width=400>

# 对应解释文章：

[从零构建光线追踪案例](https://zhuanlan.zhihu.com/p/144189898)


# 需要

Linux
g++

# 运行

```bash
g++ tinyraytracer.cpp -o main
./main
```

建议使用VSCode的C++开发环境，更加便捷。