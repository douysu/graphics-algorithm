// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version 1 20200519 绘制图像
// @version 2 20200519 添加圆数据结构
// @version 3 20200522 检测相交
// @version 4 20200524 添加多个圆
// @version 5 20200524 添加diffuse lighting
// @version 6 20200524 添加specular lighting

#include <limits>
#include <cmath>
#include <iostream>
#include <fstream>
#include <vector>
#include "geometry.h"

using namespace std;

struct Light
{
    Vec3f position;
    float intensity;

    Light(const Vec3f& p, const float& i) : position(p), intensity(i){}
};


struct Material
{
    Vec2f albedo; // 反射率
    Vec3f diffuse_color; // 漫反射颜色
    float specular_exponent; // 反光度

    Material(const Vec2f& a, const Vec3f& color, const float& spec) : albedo(a), diffuse_color(color), specular_exponent(spec){}
    Material() : albedo(1, 0), diffuse_color(), specular_exponent(){}
};


struct Sphere {
    Vec3f center;
    float radius;
    Material material;

    Sphere(const Vec3f &c, const float &r, const Material& m) : center(c), radius(r), material(m) {}

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

Vec3f reflect(const Vec3f& I, const Vec3f& N)
{
    return I - N * 2.f * (I * N);
}

bool scene_intersect(const Vec3f& orig, const Vec3f& dir, const vector<Sphere>& spheres, Vec3f& hit, Vec3f& N, Material& material)
{
    float spheres_dist = numeric_limits<float>::max();
    for (size_t i = 0; i < spheres.size(); i++)
    {
        float dist_i;
        if(spheres[i].ray_intersect(orig, dir, dist_i) && dist_i < spheres_dist)
        {
            spheres_dist = dist_i;
            // 计算投影点
            hit = orig + dir * dist_i;
            // 计算圆中心与投影的向量
            N = (hit - spheres[i].center).normalize();
            material = spheres[i].material;
        }
    }
    
    return spheres_dist < 1000;
}

Vec3f cast_ray(const Vec3f &orig, const Vec3f &dir, const vector<Sphere>& spheres, const vector<Light>& lights) {
    Vec3f point, N;
    Material material;

    if (!scene_intersect(orig, dir, spheres, point, N, material))
    {
        return Vec3f(0.2, 0.7, 0.8); // 背景色
    }

    // 计算diffuse和specular
    float diffuse_light_intensity = 0;
    float specular_light_intensity = 0;

    for (size_t i = 0; i < lights.size(); i++)
    {
        Vec3f light_dir = (lights[i].position - point).normalize();

        // 根据光照计算公式计算diffuse
        diffuse_light_intensity += max (0.f, light_dir * N) * lights[i].intensity;
        // 根据光照计算公式计算specular
        specular_light_intensity += pow(max(0.f, reflect(light_dir, N) * dir), material.specular_exponent) * lights[i].intensity; 
    }

    return material.diffuse_color * diffuse_light_intensity * material.albedo[0] + Vec3f(1.f, 1.f, 1.f) * specular_light_intensity * material.albedo[1];
}

void render(const vector<Sphere>& spheres, vector<Light>& lights) {
    const int width    = 1024;
    const int height   = 768;
    const int fov      = M_PI/2.;
    vector<Vec3f> framebuffer(width*height);

    #pragma omp parallel for
    for (size_t j = 0; j<height; j++) {
        for (size_t i = 0; i<width; i++) {
            // 计算射线光反向方向
            // *width/(float)height保持屏幕比
            // tan(fov / 2) 表示一个像素所对应的世界单位
            float x =  (2*(i + 0.5)/(float)width  - 1) * tan(fov/2.) * width / (float)height;
            float y = -(2*(j + 0.5)/(float)height - 1) * tan(fov/2.);
            Vec3f dir = Vec3f(x, y, -1).normalize();

            // 得到像素颜色
            framebuffer[i+j*width] = cast_ray(Vec3f(0,0,0), dir, spheres, lights);
        }
    }

    ofstream ofs; // save the framebuffer to file
    ofs.open("./out.ppm");
    ofs << "P6\n" << width << " " << height << "\n255\n";
    for (size_t i = 0; i < height*width; ++i) {
        for (size_t j = 0; j < 3; j++) {
            // 保证计算出的值范围在0~1之间
            Vec3f& c = framebuffer[i];
            float max = std::max(c[0], std::max(c[1], c[2]));
            if (max > 1) c = c * (1. / max);

            // camera为(0, 0, 0)，方向向量标准化
            ofs << (char)(255 * std::max(0.f, min(1.f, framebuffer[i][j])));
        }
    }
    ofs.close();
}

int main(int argc, char** argv) {
    Material      ivory(Vec2f(0.6,  0.3), Vec3f(0.4, 0.4, 0.3), 50.); // 象牙白
    Material red_rubber(Vec2f(0.9,  0.1), Vec3f(0.3, 0.1, 0.1), 10.); // 红橡胶
    
    vector<Sphere> spheres;
    spheres.push_back(Sphere(Vec3f(-3,    0,   -16), 2,      ivory));
    spheres.push_back(Sphere(Vec3f(-1.0, -1.5, -12), 2, red_rubber));
    spheres.push_back(Sphere(Vec3f( 1.5, -0.5, -18), 3, red_rubber));
    spheres.push_back(Sphere(Vec3f( 7,    5,   -18), 4,      ivory));
    
    vector<Light> lights;
    lights.push_back(Light(Vec3f(-20, 20, 20), 1.5f));
    lights.push_back(Light(Vec3f( 30, 50, -25), 1.8f));
    lights.push_back(Light(Vec3f( 30, 20,  30), 1.7f));

    render(spheres, lights);

    return 0;
}