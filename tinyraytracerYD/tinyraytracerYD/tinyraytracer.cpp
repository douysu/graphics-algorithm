// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version1 20200519 绘制图像

#include <cmath>
#include <limits>
#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include "geometry.h"

using namespace std;

void render()
{
	const int width = 1024;
	const int height = 768;
	std::vector<Vec3f> framebuffer(width * height);

	for (size_t j = 0; j < height; j++)
	{
		for (size_t i = 0; i < width; i++)
			framebuffer[i + j * width] = Vec3f(j / float(height), i / float(width), 0);
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
	render();
	return 0;
}