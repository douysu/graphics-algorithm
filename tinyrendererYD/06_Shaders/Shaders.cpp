/**
 @author 憨豆酒 YinDou https://github.com/douysu

 @description 主文件

 @version1 20200330 着色器 YinDou
 @version2 20200404 着色器添加纹理 YinDou
 @version3 20200404 着色器添加法向量贴图 YinDou
*/

#include <vector>
#include <iostream>
#include <algorithm>

#include "tgaimage.h"
#include "model.h"
#include "geometry.h"
#include "our_gl.h"

using namespace std;

Model* model = NULL;
int* zbuffer = NULL;

const int width  = 800;
const int height = 800;

Vec3f light_dir(1, 1, 1);
Vec3f eye(1, 1, 3);
Vec3f center(0, 0, 0);
Vec3f up(0, 1, 0);

struct GouraudShader : public IShader
{
	//Vec3f varying_intensity; // vertex写入，fragment读入

	//virtual Vec4f vertex(int iface, int nthvert)
	//{
	//	varying_intensity[nthvert] = max(0.f, model->normal(iface, nthvert) * light_dir);
	//	Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert));

	//	return Viewport * Projection * ModelView * gl_Vertex;
	//}

	//virtual bool fragment(Vec3f bar, TGAColor& color)
	//{
	//	// 测试1
	//	/*float intensity = varying_intensity * bar;
	//	color = TGAColor(255, 255, 255) * intensity;

	//	return false;*/

	//	// 测试2
	//	/*float intensity = varying_intensity * bar;
	//	if (intensity > .85f) intensity = 1;
	//	else if (intensity > .60f) intensity = .80f;
	//	else if (intensity > .45f) intensity = .45f;
	//	else if (intensity > .30f) intensity = .30f;
	//	else intensity = 0;
	//	color = TGAColor(255, 155, 0) * intensity;

	//	return false;*/
	//}

	// 测试3 - 添加纹理
	//Vec3f varying_intensity;
	//mat<2, 3, float> varing_uv;

	//virtual Vec4f vertex(int iface, int nthvert)
	//{
	//	varing_uv.set_col(nthvert, model->uv(iface, nthvert));
	//	varying_intensity[nthvert] = max(0.f, model->normal(iface, nthvert) * light_dir);

	//	Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert));
	//	return Viewport * Projection * ModelView * gl_Vertex;
	//}

	//virtual bool fragment(Vec3f bar, TGAColor& color)
	//{
	//	float intensity = varying_intensity * bar; // 当前强度差值
	//	Vec2f uv = varing_uv * bar; // 当前uv差值
	//	color = model->diffuse(uv) * intensity;

	//	return false;
	//}

	// 测试4 - 法向量贴图
	mat<2, 3, float> varying_uv; // same as above
	mat<4, 4, float> uniform_M; // Projection * ModelView
	mat<4, 4, float> uniform_MIT; // (Projection * ModelView).invert_transpose

	virtual Vec4f vertex(int iface, int nthvert)
	{
		varying_uv.set_col(nthvert, model->uv(iface, nthvert));
		Vec4f gl_Vertex = embed<4>(model->vert(iface, nthvert));
		return Viewport * Projection * ModelView * gl_Vertex;
	}

	virtual bool fragment(Vec3f bar, TGAColor& color)
	{
		Vec2f uv = varying_uv * bar; // UV差值

		// 都变换到z = 0 的平面内，与OpenGL的计算不太相同
		Vec3f n = proj<3>(uniform_MIT * embed<4>(model->normal(uv))).normalize();
		Vec3f l = proj<3>(uniform_M * embed<4>(light_dir)).normalize();
		/*float intensity = max(0.f, n * l);
		color = model->diffuse(uv) * intensity;
		return false;*/

		// 测试5 - Phong 光照模型
		Vec3f r = (n * (n * l * 2.f) - l).normalize();
		float spec = pow(max(r.z, 0.0f), model->specular(uv));
		float diff = max(0.f, n * l);
		TGAColor c = model->diffuse(uv);
		color = c;
		for (int i = 0; i < 3; i++)
			color[i] = min<float>(5 + c[i] * (diff + .6 * spec), 255);

		return false;
	}
};

int main(int argc, char** argv)
{
	if (argc == 2)
		model = new Model(argv[1]);
	else
		model = new Model("../obj/african_head.obj");

	lookat(eye, center, up);
	viewport(width / 8, height / 8, width * 3 / 4, height * 3 / 4);
	projection(-1.f / (eye - center).norm());
	light_dir.normalize();

	TGAImage image(width, height, TGAImage::RGB);
	TGAImage zbuffer(width, height, TGAImage::GRAYSCALE);

	GouraudShader shader;
	shader.uniform_M = Projection * ModelView;
	shader.uniform_MIT = (Projection * ModelView).invert_transpose();

	for (int i = 0; i < model->nfaces(); i++)
	{
		Vec4f screen_coords[3];
		for (int j = 0; j < 3; j++)
			screen_coords[j] = shader.vertex(i, j);

		triangle(screen_coords, shader, image, zbuffer);
	}

	image.flip_vertically(); // to place the origin in the bottom left corner of the image
	zbuffer.flip_vertically();
	image.write_tga_file("output.tga");
	zbuffer.write_tga_file("zbuffer.tga");

	delete model;

	return 0;
}


