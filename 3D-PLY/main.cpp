#include <stdio.h> 
#include "rply.h"
#include <fstream>
#include <iostream>
using namespace std;
ofstream outfile;
static int vertex_cb(p_ply_argument argument) {
	long eol;
	ply_get_argument_user_data(argument, NULL, &eol);
	float temp = ply_get_argument_value(argument);
	outfile << temp  << endl;
	printf("%g", ply_get_argument_value(argument));
	if (eol) printf("\n");
	else printf(" ");
	return 1;
}

static int face_cb(p_ply_argument argument) {
	long length, value_index;
	ply_get_argument_property(argument, NULL, &length, &value_index);
	switch (value_index) {
	case 0:
	case 1:
		printf("%g ", ply_get_argument_value(argument));
		break;
	case 2:
		printf("%g\n", ply_get_argument_value(argument));
		break;
	default:
		break;
	}
	return 1;
}

int main(void) {
	outfile.open("mean_curvature_vertex.txt");

	long nvertices;
	p_ply ply = ply_open("Mean_01.ply", NULL);
	if (!ply) return 1;
	if (!ply_read_header(ply)) return 1; //运行检查
	nvertices = ply_set_read_cb(ply, "vertex", "x", vertex_cb, NULL, 0);
	ply_set_read_cb(ply, "vertex", "y", vertex_cb, NULL, 0);
	ply_set_read_cb(ply, "vertex", "z", vertex_cb, NULL, 0);
	ply_set_read_cb(ply, "vertex", "quality", vertex_cb, NULL, 1);
	//printf("%ld\n", nvertices);
	if (!ply_read(ply)) return 1;
	ply_close(ply);

	outfile.close();//关闭文件流
	getchar();
	return 0;
}

