// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version 1 20200519 绘制图像
// @version 2 20200519 添加圆数据结构
// @version 3 20200522 检测相交

#include <limits>
#include <cmath>
#include <iostream>
#include <fstream>
#include <vector>
#include "geometry.h"

struct Sphere {
    Vec3f center;
    float radius;

    Sphere(const Vec3f &c, const float &r) : center(c), radius(r) {}

    bool ray_intersect(const Vec3f &orig, const Vec3f &dir, float &t0) const {
        //  点c在射线上的投影是pc，向量以v开头
        Vec3f vpc = center - orig;
        float c = vpc * dir; // dir标准化向量，模为1，得到pc到p点距离。
        float b_square = vpc * vpc - c * c; // 得到圆中心c到射线距离平方
        if (b_square > radius * radius) return false; //不相交，距离大于半径
        float thc = sqrt(radius * radius - b_square); // 计算相交距离的长度

        // 计算相交点，判断射线起点是否在圆内
        t0 = c - thc;
        float t1 = c + thc;
        if (t0 < 0) t0 = t1;
        if (t0 < 0) return false;

        return true;
    }
};

Vec3f cast_ray(const Vec3f &orig, const Vec3f &dir, const Sphere &sphere) {
    float sphere_dist = std::numeric_limits<float>::max();
    if (!sphere.ray_intersect(orig, dir, sphere_dist)) {
        return Vec3f(0.2, 0.7, 0.8); // 背景色
    }

    return Vec3f(0.4, 0.4, 0.3);
}

void render(const Sphere &sphere) {
    const int width    = 1024;
    const int height   = 768;
    const int fov      = M_PI/2.;
    std::vector<Vec3f> framebuffer(width*height);

    #pragma omp parallel for
    for (size_t j = 0; j<height; j++) {
        for (size_t i = 0; i<width; i++) {
            // 计算射线光反向方向
            // *width/(float)height保持屏幕比
            // tan(fov / 2) 表示一个像素所对应的世界单位
            float x =  (2*(i + 0.5)/(float)width  - 1) * tan(fov/2.) * width / (float)height;
            float y = -(2*(j + 0.5)/(float)height - 1) * tan(fov/2.);
            Vec3f dir = Vec3f(x, y, -1).normalize();
            framebuffer[i+j*width] = cast_ray(Vec3f(0,0,0), dir, sphere);
        }
    }

    std::ofstream ofs; // save the framebuffer to file
    ofs.open("./out.ppm");
    ofs << "P6\n" << width << " " << height << "\n255\n";
    for (size_t i = 0; i < height*width; ++i) {
        for (size_t j = 0; j<3; j++) {
			// camera为(0, 0, 0)，方向向量标准化
            ofs << (char)(255 * std::max(0.f, std::min(1.f, framebuffer[i][j])));
        }
    }
    ofs.close();
}

int main() {
    Sphere sphere(Vec3f(-3, 0, -16), 2);
    render(sphere);

    return 0;
}