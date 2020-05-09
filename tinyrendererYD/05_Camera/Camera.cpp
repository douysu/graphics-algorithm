// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version1 20200330 摄像机

#include <vector>
#include <cmath>
#include <algorithm>
#include <limits>

#include "../Utilities/tgaimage.h"
#include "../Utilities/model.h"
#include "../Utilities/geometry.h"

using namespace std;

Vec3f light_dir(0, 0, -1);
Vec3f eye(0, 0, 4);
Vec3f center(0, 0, 0);
Vec3f up(0, 1, 0);

const TGAColor white = TGAColor(255, 255, 255, 255);
const TGAColor red = TGAColor(255, 0, 0, 255);

Model* model = NULL;

const int width = 800;
const int height = 800;
const int depth = 255;

Matrix ModelView;
Matrix ViewPort;
Matrix Projection;

// 构建LookAt矩阵
void lookat(Vec3f eye, Vec3f center, Vec3f up)
{
	Vec3f z = (eye - center).normalize();
	Vec3f x = cross(up, z).normalize();
	Vec3f y = cross(z, x).normalize();

	Matrix Minv = Matrix::identity();
	for (int i = 0; i < 3; i++)
	{
		Minv[0][i] = x[i];
		Minv[1][i] = y[i];
		Minv[2][i] = z[i];
		Minv[i][3] = -center[i];
	}

	ModelView = Minv;
}

void viewport(int x, int y, int w, int h)
{
	ViewPort = Matrix::identity();
	ViewPort[0][3] = x + w / 2.f;
	ViewPort[1][3] = y + h / 2.f;
	ViewPort[2][3] = depth / 2.f;
	ViewPort[0][0] = w / 2.f;
	ViewPort[1][1] = h / 2.f;
	ViewPort[2][2] = depth / 2.f;
}

void projection(float coeff)
{
	Projection = Matrix::identity();
	Projection[3][2] = coeff;
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

void triangle(Vec3f* pts, float* zbuffer, TGAImage& image, TGAColor color) {
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
			for (int i = 0; i < 3; i++)
				P.z += pts[i][2] * bc_screen[i];

			if (zbuffer[int(P.x + P.y * width)] < P.z)
			{
				zbuffer[int(P.x + P.y * width)] = P.z;
				image.set(P.x, P.y, color);
			}
		}
	}
}

Vec3f world2screen(Vec3f v)
{
	Vec4f  gl_vertex = embed<4>(v);
	gl_vertex = ViewPort * Projection * ModelView * gl_vertex;
	Vec3f v3 = proj<3>(gl_vertex / gl_vertex[3]);
	return Vec3f(int(v3.x + .5), int(v3.y + .5), v3.z);
}

int main(int argc, char** argv)
{
	TGAImage image(width, height, TGAImage::RGB);

	if (argc == 2)
		model = new Model(argv[1]);
	else
		model = new Model("../obj/african_head.obj");

	float* zbuffer = new float[width * height];
	for (int i = width * height; i--; zbuffer[i] = -numeric_limits<float>::max());

	lookat(eye, center, up);
	viewport(width / 8, height / 8, width * 3 / 4, height * 3 / 4);
	projection(-1.f / 4);

	std::cerr << ModelView << std::endl;
	std::cerr << Projection << std::endl;
	std::cerr << ViewPort << std::endl;
	Matrix z = (ViewPort * Projection * ModelView);
	std::cerr << z << std::endl;

	for (int i = 0; i < model->nfaces(); i++)
	{
		vector<int> face = model->face(i);
		Vec3f world_coords[3];
		Vec3f screen_coords[3];

		for (int i = 0; i < 3; i++)
		{
			world_coords[i] = model->vert(face[i]);
			screen_coords[i] = world2screen(world_coords[i]);
		}
		// 计算法向量个direction光
		Vec3f norm = cross((world_coords[2] - world_coords[0]), (world_coords[1] - world_coords[0]));
		norm.normalize();
		float intensity = norm * light_dir;
		if (intensity > 0)
			triangle(screen_coords, zbuffer, image, TGAColor(intensity * 255, intensity * 255, intensity * 255, 255));
	}

	image.flip_vertically(); // to place the origin in the bottom left corner of the image 
	image.write_tga_file("framebuffer.tga");

	delete model;
	delete[] zbuffer;

	return 0;
}