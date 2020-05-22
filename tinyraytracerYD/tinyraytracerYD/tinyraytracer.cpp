// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version 1 20200519 绘制图像
// @version 2 20200519 添加圆数据结构
// @version 3 20200522 检测相交

#define _USE_MATH_DEFINES

#include <cmath>
#include <limits>
#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include "geometry.h"

using namespace std;


struct Sphere
{
	Vec3f center;
	float radius;

	Sphere(const Vec3f& c, const float& r) : center(c), radius(r) {}

	bool ray_intersect(const Vec3f& orig, const Vec3f& dir, float& t0) const
	{
		Vec3f L = center - orig;
		float tca = L * dir;
		float d2 = L * L - tca * tca;
		if (d2 > radius* radius) return false;
		float thc = sqrt(radius * radius - d2);
		t0 = tca - thc;
		float  t1 = tca + thc;
		if (t0 < 0) t0 = t1;
		if (t0 < 0) return false;
		return true;
	}
};

Vec3f cast_ray(const Vec3f& orig, const Vec3f& dir, const Sphere& sphere)
{
	float sphere_dist = numeric_limits<float>::max();
	if (!sphere.ray_intersect(orig, dir, sphere_dist))
	{
		return Vec3f(0.2, 0.7, 0.8); // 背景颜色
	}

	return Vec3f(0.4, 0.4, 0.3);
}

void render(const Sphere& sphere)
{
	const int width = 1024;
	const int height = 768;
	const int fov = M_PI / 2.;
	std::vector<Vec3f> framebuffer(width * height);

	#pragma omp paralllel for
	for (size_t j = 0; j < height; j++)
	{
		for (size_t i = 0; i < width; i++)
		{
			double x = (2 * (i + 0.5) / (float)width - 1) * tan(fov / 2.) * width / (float)height;
			double y = -(2 * (j + 0.5) / (float)height - 1) * tan(fov / 2.);
			Vec3f dir = Vec3f(x, y, -1).normalize();
			framebuffer[i + j * width] = cast_ray(Vec3f(0, 0, 0), dir, sphere);
		}
	}

	std::ofstream ofs;
	ofs.open("./out.ppm");

	ofs << "P6\n" << width << " " << height << "\n255\n";
	for (size_t i = 0; i < height * width; i++)
	{
		for (size_t j = 0; j < 3; j++)
			// 限定取值范围
			ofs << (char)(255 * max(0.f, min(1.f, framebuffer[i][j])));
	}

	ofs.close();
}

int main()
{
	Sphere sphere(Vec3f(-3, 0, 16), 2);
	render(sphere);

	return 0;
}