// @author 憨豆酒 YinDou https://github.com/douysu
//
// @description 主文件
//
// @version1 20200311 绘制颜色像素
#include "../Utilities/tgaimage.h"

const TGAColor white = TGAColor(255, 255, 255, 255);
const TGAColor red = TGAColor(255, 0, 0, 255);

int main(int argc, char** argv)
{
	TGAImage image(100, 100, TGAImage::RGB);
	image.set(52, 41, red);
	image.flip_vertically(); 
	image.write_tga_file("output.tga"); 
	return 0;
}