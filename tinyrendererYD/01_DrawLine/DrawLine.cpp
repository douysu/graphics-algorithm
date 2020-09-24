// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version1 20200311 绘制线段

#include <vector>
#include <cmath>

#include "../Utilities/tgaimage.h"
#include "../Utilities/model.h"
#include "../Utilities/geometry.h"


using namespace std;

const TGAColor white = TGAColor(255, 255, 255, 255);
const TGAColor red = TGAColor(255, 0, 0, 255);

Model* model = NULL;

const int width = 800;
const int height = 800;

// 尝试一 按照长度的增加绘制
//void line(int x0, int y0, int x1, int y1, TGAImage& image, TGAColor color)
//{
//	for (float t = 0.; t < 1; t += .01f)
//	{
//		int x = (int)((double)x0 * (1. - t) + (double)x1 * t);
//		int y = (int)((double)y0 * (1. - t) + (double)y1 * t);
//		image.set(x, y, color);
//	}
//}

// 尝试二 按照x增加绘制，计算y
//void line(int x0, int y0, int x1, int y1, TGAImage& image, TGAColor color)
//{
//	for (int x = x0; x <= x1; x++)
//	{
//		float t = (x - x0) / (float)(x1 - x0); // 计算占比
//		int y = (int)(y0 * (1. - t) + y1 * (double)t); // 计算Y
//		image.set(x, y, color);
//	}
//}

// 尝试三 
//void line(int x0, int y0, int x1, int y1, TGAImage& image, TGAColor color)
//{
//	bool steep = false;
//	
//	// 如果斜率 > 1，交换
//	if (abs(x0 - x1) < abs(y0 - y1))
//	{
//		swap(x0, y0);
//		swap(x1, y1);
//		steep = true;
//	}
//	// 如果 x0 > x1
//	if (x0 > x1)
//	{
//		swap(x0, x1);
//		swap(y0, y1);
//	}
//
//	for (int x = 0; x < x1; x++)
//	{
//		float t = (x - x0) / (float)(x1 - x0);
//		int y = (int)(y0 * (1. - t) + y1 * (double)t);
//		if (steep)
//			image.set(y, x, color);
//		else
//			image.set(x, y, color);
//	}
//}

// 尝试四
//void line(int x0, int y0, int x1, int y1, TGAImage& image, TGAColor color)
//{
//	bool steep = false;
//	if (abs(x0 - x1) < abs(y0 - y1))
//	{
//		swap(x0, y0);
//		swap(x1, y1);
//		steep = true;
//	}
//
//	if (x0 > x1)
//	{
//		swap(x0, x1);
//		swap(y0, y1);
//	}
//
//	int dx = x1 - x0;
//	int dy = y1 - y0;
//	float derror = abs(dy / float(dx));
//	float error = 0;
//
//	int  y = y0;
//	for (int x = x0; x < x1; x++)
//	{
//		if (steep)
//			image.set(y, x, color);
//		else
//			image.set(x, y, color);
//
//		error += derror; // x每增加，也要增加对应比值
//		if (error > .5)
//		{
//			y += (y1 > y0 ? 1 : -1);
//			error -= 1;
//		}
//	}
//}

// 尝试五
void line(int x0, int y0, int x1, int y1, TGAImage& image, TGAColor color)
{
	bool steep = false;
	if (abs(x0 - x1) < abs(y0 - y1))
	{
		swap(x0, y0);
		swap(x1, y1);
		steep = true;
	}

	if (x0 > x1)
	{
		swap(x0, x1);
		swap(y0, y1);
	}

	int dx = x1 - x0;
	int dy = y1 - y0;
	int derror2 = abs(dy) * 2;
	int error2 = 0;
	int y = y0;
	for (int x = x0; x <= x1; x++)
	{
		if (steep)
			image.set(y, x, color);
		else
			image.set(x, y, color);

		error2 += derror2;
		if (error2 > dx)
		{
			y += (y1 > y0 ? 1 : -1);
			error2 -= dx * 2;
		}
	}
}

int main(int argc, char** argv)
{
	// 绘制线
	/*TGAImage image(100, 100, TGAImage::RGB);
	line(13, 20, 80, 40, image, white);
	line(20, 13, 40, 80, image, red);
	line(80, 40, 13, 20, image, red);*/

	if (argc == 2)
		model = new Model(argv[1]);
	else
		model = new Model("../obj/DamagedHelmet.obj");

	// 绘制模型
	TGAImage image(width, height, TGAImage::RGB);
	for (int i = 0; i < model->nfaces(); i++)
	{
		vector<int> face = model->face(i);
		for (int j = 0; j < 3; j++)
		{
			Vec3f v0 = model->vert(face[j]);
			Vec3f v1 = model->vert(face[(j + 1) % 3]);
			int x0 = (v0.x + 1.) * width / 2;
			int y0 = (v0.y + 1.) * height / 2;
			int x1 = (v1.x + 1.) * height / 2;
			int y1 = (v1.y + 1.) * height / 2;
			line(x0, y0, x1, y1, image, white);
		}
	}

	image.flip_vertically();
	image.write_tga_file("output.tga");
	delete model;
	return 0;
}