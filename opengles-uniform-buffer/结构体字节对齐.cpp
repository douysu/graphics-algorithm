#include <iostream.h>
using namespace std;
struct MyByteStruct
{
	char c;
	float a;
	int b;
};
/*按照正常理解为 4+4+1=9*/
/*此处使用了字节对齐，使机器访问更快,真是为4+4+4=12  详情见博客*/
int main() {
	printf("%d\n",sizeof(float));//4 byte
	printf("%d\n", sizeof(int));//4 byte
	printf("%d\n", sizeof(char));//1 byte
	printf("%d\n", sizeof(long));//4 byte
	printf("%d\n", sizeof(float *));//指针都为4字节
	MyByteStruct myTest;
	printf("%d", sizeof(myTest));//12 byte
	cin.get();
}