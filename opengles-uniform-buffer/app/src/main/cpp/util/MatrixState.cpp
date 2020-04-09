#include "MatrixState.h"
#include <math.h>

float MatrixState::currMatrix[16];
float MatrixState::mProjMatrix[16];
float MatrixState::mVMatrix[16];
float MatrixState::mMVPMatrix[16];
float MatrixState::mStack[10][16];
int MatrixState::stackTop=-1;

void MatrixState::setInitStack()
{
	Matrix::setIdentityM(currMatrix,0);
}

void MatrixState::pushMatrix()
{
	stackTop++;
	for(int i=0;i<16;i++)
	{
		mStack[stackTop][i]=currMatrix[i];
	}
}

void MatrixState::popMatrix()
{
	for(int i=0;i<16;i++)
	{
		currMatrix[i]=mStack[stackTop][i];
	}
	stackTop--;
}

void MatrixState::translate(float x,float y,float z)
{
	Matrix::translateM(currMatrix, 0, x, y, z);
}

void MatrixState::rotate(float angle,float x,float y,float z)
{
	Matrix::rotateM(currMatrix,0,angle,x,y,z);
}

void MatrixState::scale(float x,float y,float z)
{
	Matrix::scaleM(currMatrix,0, x, y, z);
}

void MatrixState::setCamera
(
 float cx,
 float cy,
 float cz,
 float tx,
 float ty,
 float tz,
 float upx,
 float upy,
 float upz
 )
{
	Matrix::setLookAtM
	(
	 mVMatrix,
	 0,
	 cx,
	 cy,
	 cz,

	 tx,
	 ty,
	 tz,

	 upx,
	 upy,
	 upz
	 );\
}

void MatrixState::setProjectFrustum
(
 float left,
 float right,
 float bottom,
 float top,
 float near,
 float far
 )
{
	Matrix::frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
}
void MatrixState::setProjectOrtho
		(
				float left,
				float right,
				float bottom,
				float top,
				float near,
				float far
		)
{
	Matrix::orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
}
float* MatrixState::getFinalMatrix()
{
	Matrix::multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
	Matrix::multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

	return mMVPMatrix;
}




