// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version1 20200316 绘制纹理

#include <vector>
#include <cmath>
#include <algorithm>
#include <limits>

#include "../Utilities/tgaimage.h"
#include "../Utilities/model.h"
#include "../Utilities/geometry.h"

using namespace std;

const TGAColor white = TGAColor(255, 255, 255, 255);
const TGAColor red = TGAColor(255, 0, 0, 255);
const TGAColor green = TGAColor(0, 255, 0, 255);

Model* model = NULL;

const int width = 800;
const int height = 800;

void line(Vec2i p0, Vec2i p1, TGAImage& image, TGAColor color)
{
	bool steep = false;
	if (abs(p0.x - p1.x) < abs(p0.y - p1.y))
	{
		swap(p0.x, p0.y);
		swap(p1.x, p1.y);
		steep = true;
	}

	if (p0.x > p1.x)
	{
		swap(p0.x, p1.x);
		swap(p0.y, p1.y);
	}

	int dx = p1.x - p0.x;
	int dy = p1.y - p0.y;
	int derror2 = abs(dy) * 2;
	int error2 = 0;
	int y = p0.y;
	for (int x = p0.x; x <= p1.x; x++)
	{
		if (steep)
			image.set(y, x, color);
		else
			image.set(x, y, color);

		error2 += derror2;
		if (error2 > dx)
		{
			y += (p1.y > p0.y ? 1 : -1);
			error2 -= dx * 2;
		}
	}
}

Vec3f barycentric(Vec3f A, Vec3f B, Vec3f C, Vec3f P)
{
	Vec3f s[2];
	for (int i = 2; i--;)
	{
		s[i][0] = C[i] - A[i];
		s[i][1] = B[i] - A[i];
		s[i][2] = A[i] - P[i];
	}

	Vec3f u = cross(s[0], s[1]);
	if (std::abs(u[2]) > 1e-2)
		return Vec3f(1.f - (u.x + u.y) / u.z, u.y / u.z, u.x / u.z);
	return Vec3f(-1, 1, 1);
}

void triangle(Vec3f* pts, Vec2f* texts, float* zbuffer, TGAImage& image) {
	constexpr float maxValue = std::numeric_limits<float>::max();
	Vec2f bboxmin(maxValue, maxValue);
	Vec2f bboxmax(-maxValue, -maxValue);
	Vec2f clamp(image.get_width() - 1, image.get_height() - 1);

	for (int i = 0; i < 3; i++)
	{
		for (int j = 0; j < 2; j++)
		{
			bboxmin[j] = max(0.f, min(bboxmin[j], pts[i][j]));
			bboxmax[j] = min(clamp[j], max(bboxmax[j], pts[i][j]));
		}
	}

	Vec3f P;
	for (P.x = bboxmin.x; P.x <= bboxmax.x; P.x++)
	{
		for (P.y = bboxmin.y; P.y <= bboxmax.y; P.y++)
		{
			Vec3f bc_screen = barycentric(pts[0], pts[1], pts[2], P);
			if (bc_screen.x < 0 || bc_screen.y < 0 || bc_screen.z < 0)
				continue;

			P.z = 0;
			Vec2f Ptext(0, 0);
			for (int i = 0; i < 3; i++)
			{
				P.z += pts[i][2] * bc_screen[i];
				Ptext[0] += texts[i][0] * bc_screen[i];
				Ptext[1] += texts[i][1] * bc_screen[i];
			}
				
			if (zbuffer[int(P.x + P.y * width)] < P.z)
			{
				TGAColor color = model->diffuse(Ptext); // 获得纹理颜色
				image.set(P.x, P.y, color);
				zbuffer[int(P.x + P.y * width)] = P.z;
			}
		}
	}
}

Vec3f world2screen(Vec3f v)
{
	return Vec3f(int((v.x + 1.) * width / 2. + .5), int((v.y + 1.) * height / 2. + .5), v.z);
}

int main(int argc, char** argv)
{
	TGAImage image(width, height, TGAImage::RGB);
	Vec3f light_dir(0, 0, -1);

	if (argc == 2)
		model = new Model(argv[1]);
	else
		model = new Model("../obj/DamagedHelmet.obj");

	float* zbuffer = new float[width * height];
	for (int i = width * height; i--; zbuffer[i] = -numeric_limits<float>::max());

	for (int i = 0; i < model->nfaces(); i++)
	{
		Vec3f world_coords[3];
		Vec3f screen_coords[3];
		Vec2f texts[3];
		for (int j = 0; j < 3; j++)
		{
			world_coords[j] = model->vert(i, j);
			screen_coords[j] = world2screen(world_coords[j]);
			texts[j] = model->uv(i, j);
		}
		triangle(screen_coords, texts, zbuffer, image);
	}

	image.flip_vertically(); // to place the origin in the bottom left corner of the image 
	image.write_tga_file("framebuffer.tga");
	delete model;
	delete[] zbuffer;

	return 0;
}
