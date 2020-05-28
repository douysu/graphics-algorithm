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
// @version 7 20200524 添加反射reflect
// @version 8 20200524 添加折射refract
// @version 9 20200524 添加棋盘平面
// @version 10 20200524 添加背景

#include <limits>
#include <cmath>
#include <iostream>
#include <fstream>
#include <vector>
#include "geometry.h"

#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

using namespace std;

int envmap_width, envmap_height;
vector<Vec3f> envmap;

struct Light
{
    Vec3f position;
    float intensity;

    Light(const Vec3f& p, const float& i) : position(p), intensity(i){}
};


struct Material
{
    float refractive_index; // 折射率
    Vec4f albedo; // diffuse, specular, 反射，折射比率
    Vec3f diffuse_color; // 漫反射颜色
    float specular_exponent; // 反光度

    Material(const float& r, const Vec4f& a, const Vec3f& color, const float& spec) : refractive_index(r), albedo(a), diffuse_color(color), specular_exponent(spec){}
    Material() : refractive_index(1), albedo(1, 0, 0, 0), diffuse_color(), specular_exponent(){}
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

// 反射计算
// reference:计算机图形学原理书籍
Vec3f reflect(const Vec3f& I, const Vec3f& N)
{
    return I - N * 2.f * (I * N);
}

// 折射计算，斯涅尔定律（Snell's Law）
// reference: https://zh.wikipedia.org/wiki/%E6%96%AF%E6%B6%85%E5%B0%94%E5%AE%9A%E5%BE%8B
Vec3f refract(const Vec3f& I, const Vec3f& N, const float& refractive_index)
{
    float cosi = -std::max(-1.f, std::min(1.f, I * N));
    float etai = 1;
    float etat = refractive_index;
    Vec3f n = N;
    if (cosi < 0)
    {
        cosi = -cosi;
        swap(etai, etat);
        n = -N;
    }
    float eta = etai / etat;
    float k = 1 - eta * eta * (1 - cosi * cosi);

    return k < 0 ? Vec3f(0, 0, 0) : I * eta + n * (eta * cosi - sqrt(k));
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
    
    float checkerboard_dist = std::numeric_limits<float>::max();
    if (abs(dir.y) > 1e-3)
    {
        // 计算射线是否与平面相交
        // 参考： https://zh.wikipedia.org/wiki/%E7%BA%BF%E9%9D%A2%E4%BA%A4%E7%82%B9
        // 这里平面的任意一点为(0, 0, 0)
        float d = -(orig.y + 4) / dir.y;
        Vec3f pt = orig + dir * d;

        // 限定平面y = -4 的大小长度和宽度
        if (d > 0 && abs(pt.x) < 10 && pt.z < -10 && pt.z > -30 && d < spheres_dist)
        {
            checkerboard_dist = d;
            hit = pt;
            N = Vec3f(0, 1, 0);
            material.diffuse_color = (int(0.5 * hit.x + 1000) + int(.5 * hit.z)) & 1 ? Vec3f(1, 1, 1) : Vec3f(1, .7, .3);
            material.diffuse_color = material.diffuse_color * .3;
        }
    }

    return std::min(spheres_dist, checkerboard_dist) < 1000;
}

Vec3f cast_ray(const Vec3f &orig, const Vec3f &dir, const vector<Sphere>& spheres, const vector<Light>& lights, size_t depth = 0) {
    Vec3f point, N;
    Material material;

    if (depth > 4 || !scene_intersect(orig, dir, spheres, point, N, material))
    {
        // 球形贴图
        Vec3f p = dir;
        float theta = acosf(p.y/p.norm());
        float phi = atan2f(p.z,p.x) + M_PI;

        int y = theta/(M_PI)*(envmap_height);
        int x = phi/(2*M_PI)*(envmap_width);

        return envmap[x + y * envmap_width];
    }

    // 计算反射和折射方向
    Vec3f reflect_dir = reflect(dir, N).normalize();
    Vec3f refract_dir = refract(dir, N, material.refractive_index).normalize();
    // 计算反射和折射起点
    Vec3f reflect_orig = reflect_dir * N < 0 ? point - N * 1e-3 : point + N * 1e-3;
    Vec3f refract_orig = refract_dir * N < 0 ? point - N * 1e-3 : point + N * 1e-3;
    // 迭代进行光追
    Vec3f reflect_color = cast_ray(reflect_orig, reflect_dir, spheres, lights, depth + 1);
    Vec3f refract_color = cast_ray(refract_orig, refract_dir, spheres, lights, depth + 1);

    // 计算diffuse和specular
    float diffuse_light_intensity = 0;
    float specular_light_intensity = 0;

    for (size_t i = 0; i < lights.size(); i++)
    {
        Vec3f light_dir = (lights[i].position - point).normalize();
        float light_distance = (lights[i].position - point).norm();

        Vec3f shadow_orig = light_dir * N < 0 ? point - N * 1e-3 : point + N * 1e-3;
        Vec3f shadow_pt, shadow_N;
        Material tmpmaterial;

        // 沿着灯方向走，看看有没有物体相交
        if (scene_intersect(shadow_orig, light_dir, spheres, shadow_pt, shadow_N, tmpmaterial) && (shadow_pt-shadow_orig).norm() < light_distance)
            continue;

        // 根据光照计算公式计算diffuse
        diffuse_light_intensity += max (0.f, light_dir * N) * lights[i].intensity;
        // 根据光照计算公式计算specular
        specular_light_intensity += pow(max(0.f, reflect(light_dir, N) * dir), material.specular_exponent) * lights[i].intensity; 
    }

    return material.diffuse_color * diffuse_light_intensity * material.albedo[0] + Vec3f(1.f, 1.f, 1.f) * specular_light_intensity * material.albedo[1] 
        + reflect_color * material.albedo[2] + refract_color * material.albedo[3];
}

void render(const vector<Sphere>& spheres, vector<Light>& lights) {
    const int width    = 1024;
    const int height   = 768;
    const int fov      = M_PI/3.;
    vector<Vec3f> framebuffer(width*height);

    #pragma omp parallel for
    for (size_t j = 0; j<height; j++) {
        for (size_t i = 0; i<width; i++) {
            float dir_x =  (i + 0.5) -  width/2.;
            float dir_y = -(j + 0.5) + height/2.;    // this flips the image at the same time
            float dir_z = -height/(2.*tan(fov/2.));
            framebuffer[i+j*width] = cast_ray(Vec3f(0,0,0), Vec3f(dir_x, dir_y, dir_z).normalize(), spheres, lights);
        }
    }

    vector<unsigned char> pixmap(width * height *3);
    for (size_t i = 0; i < width * height; i++)
    {
        Vec3f& c = framebuffer[i];
        float max = std::max(c[0], std::max(c[1], c[2]));
        if (max > 1) c = c * (1. / max);
        for (size_t j = 0; j < 3; j++)
        {
            pixmap[i * 3 + j] = (unsigned char)(255 * std::max(0.f, std::min(1.f, framebuffer[i][j])));
        }
    }

    stbi_write_jpg("out.jpg", width, height, 3, pixmap.data(), 100);
}

int main(int argc, char** argv) {
    int n = -1;
    unsigned char* pixmap = stbi_load("./envmap.jpg", &envmap_width, &envmap_height, &n, 0);
    if (!pixmap || n != 3)
    {
        cerr << "Error: can not load the environment map" << endl;
        return -1;
    }

    envmap = vector<Vec3f>(envmap_width * envmap_height);
    for (int j = envmap_height-1; j>=0 ; j--) 
    {
        for (int i = 0; i<envmap_width; i++) 
        {
            envmap[i+j*envmap_width] = Vec3f(pixmap[(i+j*envmap_width)*3+0], pixmap[(i+j*envmap_width)*3+1], pixmap[(i+j*envmap_width)*3+2])*(1/255.);
        }
    }

    stbi_image_free(pixmap);

    Material      ivory(1.0, Vec4f(0.6,  0.3, 0.1, 0.0), Vec3f(0.4, 0.4, 0.3),   50.); // 象牙白
    Material      glass(1.5, Vec4f(0.0,  0.5, 0.1, 0.8), Vec3f(0.6, 0.7, 0.8),  125.); // 玻璃
    Material red_rubber(1.0, Vec4f(0.9,  0.1, 0.0, 0.0), Vec3f(0.3, 0.1, 0.1),   10.); // 红橡胶
    Material     mirror(1.0, Vec4f(0.0, 10.0, 0.8, 0.0), Vec3f(1.0, 1.0, 1.0), 1425.); // 镜子
    
    vector<Sphere> spheres;
    spheres.push_back(Sphere(Vec3f(-3,    0,   -16), 2,      ivory));
    spheres.push_back(Sphere(Vec3f(-1.0, -1.5, -12), 2,      glass));
    spheres.push_back(Sphere(Vec3f( 1.5, -0.5, -18), 3, red_rubber));
    spheres.push_back(Sphere(Vec3f( 7,    5,   -18), 4,     mirror));
    
    vector<Light> lights;
    lights.push_back(Light(Vec3f(-20, 20, 20), 1.5f));
    lights.push_back(Light(Vec3f( 30, 50, -25), 1.8f));
    lights.push_back(Light(Vec3f( 30, 20,  30), 1.7f));

    render(spheres, lights);

    return 0;
}