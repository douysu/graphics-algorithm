package com.bn.Constant;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//存储系统矩阵状态的类
public class MatrixState
{
	private static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
	private static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
	private static float[] currMatrix;//当前变换矩阵
	public static float[] lightLocation=new float[]{0,0,0};//定位光光源位置
	public static float[] ZzlightLocation=new float[]{0,0,0};//珍珠光源的位置
	public static FloatBuffer cameraFB;
	public static FloatBuffer lightPositionFB;
	public static FloatBuffer ZzlightPositionFB;//珍珠的FloatBuffer

	//保护变换矩阵的栈
	static float[][] mStack=new float[10][16];
	static int stackTop=-1;

	public static void setInitStack()//获取不变换初始矩阵
	{
		currMatrix=new float[16];
		Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
	}

	public static void pushMatrix()//保护变换矩阵
	{
		stackTop++;
		for(int i=0;i<16;i++)
		{
			mStack[stackTop][i]=currMatrix[i];
		}
	}

	public static void popMatrix()//恢复变换矩阵
	{
		for(int i=0;i<16;i++)
		{
			currMatrix[i]=mStack[stackTop][i];
		}
		stackTop--;
	}

	public static void translate(float x,float y,float z)//设置沿xyz轴移动
	{
		Matrix.translateM(currMatrix, 0, x, y, z);
	}

	public static void rotate(float angle,float x,float y,float z)//设置绕xyz轴旋转
	{
		Matrix.rotateM(currMatrix,0,angle,x,y,z);
	}

	public static void scale(float x,float y,float z)
	{
		Matrix.scaleM(currMatrix,0, x, y, z);
	}


	//设置摄像机
	public static void setCamera
	(
			float cx,	//摄像机位置x
			float cy,   //摄像机位置y
			float cz,   //摄像机位置z
			float tx,   //摄像机目标点x
			float ty,   //摄像机目标点y
			float tz,   //摄像机目标点z
			float upx,  //摄像机UP向量X分量
			float upy,  //摄像机UP向量Y分量
			float upz   //摄像机UP向量Z分量
	)
	{
		Matrix.setLookAtM
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
				);

		float[] cameraLocation=new float[3];//摄像机位置
		cameraLocation[0]=cx;
		cameraLocation[1]=cy;
		cameraLocation[2]=cz;

		ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
		llbb.order(ByteOrder.nativeOrder());//设置字节顺序
		cameraFB=llbb.asFloatBuffer();
		cameraFB.put(cameraLocation);
		cameraFB.position(0);
	}
	//设置透视投影参数
	public static void setProjectFrustum
	(
			float left,		//near面的left
			float right,    //near面的right
			float bottom,   //near面的bottom
			float top,      //near面的top
			float near,		//near面距离
			float far       //far面距离
	)
	{
		Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}

	//设置正交投影参数
	public static void setProjectOrtho
	(
			float left,		//near面的left
			float right,    //near面的right
			float bottom,   //near面的bottom
			float top,      //near面的top
			float near,		//near面距离
			float far       //far面距离
	)
	{
		Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}
	//用于一帧内的摄像机矩阵
	private static float[] mVMatrixForSpecFrame = new float[16];//摄像机位置朝向9参数矩阵
	public static void copyMVMatrix()
	{
		for(int i=0;i<16;i++)
		{
			mVMatrixForSpecFrame[i]=mVMatrix[i];
		}
	}
	//获取具体物体的总变换矩阵
	public static float[] getFinalMatrix()
	{
		float[] mMVPMatrix=new float[16];
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}

	//获取具体物体的变换矩阵
	public static float[] getMMatrix()
	{
		return currMatrix;
	}

	//设置灯光位置的方法
	public static void setLightLocation(float x,float y,float z)
	{
		lightLocation[0]=x;
		lightLocation[1]=y;
		lightLocation[2]=z;
		ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
		llbb.order(ByteOrder.nativeOrder());//设置字节顺序
		lightPositionFB=llbb.asFloatBuffer();
		lightPositionFB.put(lightLocation);
		lightPositionFB.position(0);
	}

	public static float[] getViewProjMatrix()
	{
		float[] mMVPMatrix=new float[16];
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		return mMVPMatrix;
	}
	public static void setZzLightLocation(float x,float y,float z)
	{
		ZzlightLocation[0]=x;
		ZzlightLocation[1]=y;
		ZzlightLocation[2]=z;
	}
	//更新摄像机的方法
	public static void updateCamera(float cx,	//摄像机位置x
									float cy,   //摄像机位置y
									float cz,   //摄像机位置z
									float tx,   //摄像机目标点x
									float ty,   //摄像机目标点y
									float tz,   //摄像机目标点z
									float upx,  //摄像机UP向量X分量
									float upy,  //摄像机UP向量Y分量
									float upz
									)
	{
		Matrix.setLookAtM
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
				);
		float[] cameraLocation=new float[3];//摄像机位置
		cameraLocation[0]=cx;
		cameraLocation[1]=cy;
		cameraLocation[2]=cz;
		MatrixState.cameraFB.clear();//清空缓冲
		MatrixState.cameraFB.put(cameraLocation);//向缓冲区中放入顶点坐标数据
		MatrixState.cameraFB.position(0);//设置缓冲区起始位置
	}
	//获取投影及摄像机综合矩阵
	public static float[] getVPMatrix()
	{
		float[] mVPMatrix=new float[16];
		Matrix.multiplyMM(mVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		return mVPMatrix;
	}
	//获取摄像机矩阵的逆矩阵的方法
	public static float[] getInvertMvMatrix(){
		float[] invM = new float[16];
		Matrix.invertM(invM, 0, mVMatrix, 0);//求逆矩阵
		return invM;
	}
	//通过摄像机变换后的点求变换前的点的方法：乘以摄像机矩阵的逆矩阵
	public static float[] fromPtoPreP(float[] p){
		//通过逆变换，得到变换之前的点
		float[] inverM = getInvertMvMatrix();//获取逆变换矩阵
		float[] preP = new float[4];
		Matrix.multiplyMV(preP, 0, inverM, 0, new float[]{p[0],p[1],p[2],1}, 0);//求变换前的点
		return new float[]{preP[0],preP[1],preP[2]};//变换前的点就是变换之前的法向量
	}
}
