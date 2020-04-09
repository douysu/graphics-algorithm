package com.bn.Constant;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.annotation.SuppressLint;
import android.opengl.GLES30;

import com.bn.main.MySurfaceView;

//加载后的物体——仅携带顶点信息，颜色随机
@SuppressLint("NewApi") public class LoadedObjectVertexNormalTexture
{
	//绘制真实
	int mProgram;//自定义渲染管线着色器程序id
	int muMVPMatrixHandle;//总变换矩阵引用
	int muMMatrixHandle;//位置、旋转变换矩阵
	int maPositionHandle; //顶点位置属性引用
	int maNormalHandle; //顶点法向量属性引用
	int maLightLocationHandle;//光源位置属性引用
	int maCameraHandle; //摄像机位置属性引用
	int maTexCoorHandle; //顶点纹理坐标属性引用
	String mVertexShader;//顶点着色器代码脚本
	String mFragmentShader;//片元着色器代码脚本

	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
	int vCount=0;
	MySurfaceView mv;
	boolean initFlag=false;
	//只送入顶点
	int mProgramTwo;//自定义渲染管线着色器程序id
	int muMVPMatrixHandleTwo;//总变换矩阵引用
	int muMMatrixHandleTwo;//位置、旋转变换矩阵
	int maPositionHandleTwo; //顶点位置属性引用
	int maCameraHandleTwo; //摄像机位置属性引用
	int mablurWidth;//宽度引用
	int mablurPosition;//开始距离引用
	int maScreenWidth;//屏幕宽度引用
	int maScreenHeight;//屏幕高度引用

	String mVertexShaderTwo;//顶点着色器代码脚本
	String mFragmentShaderTwo;//片元着色器代码脚本

	boolean initFlagTwo=false;


	public LoadedObjectVertexNormalTexture(MySurfaceView mv,float[] vertices,float[] normals,float texCoors[])
	{
		this.mv=mv;
		//初始化顶点数据的方法
		initVertexData(vertices,normals,texCoors);
	}

	//初始化顶点数据的方法
	public void initVertexData(float[] vertices,float[] normals,float texCoors[])
	{
		//顶点坐标数据的初始化================begin============================
		vCount=vertices.length/3;

		//创建顶点坐标数据缓冲
		//vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
		mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);//设置缓冲区起始位置
		//特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		//转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		//顶点坐标数据的初始化================end============================

		//顶点法向量数据的初始化================begin============================
		ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
		cbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
		mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
		mNormalBuffer.position(0);//设置缓冲区起始位置
		//特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		//转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		//顶点着色数据的初始化================end============================

		//顶点纹理坐标数据的初始化================begin============================
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
		tbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mTexCoorBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
		mTexCoorBuffer.put(texCoors);//向缓冲区中放入顶点纹理坐标数据
		mTexCoorBuffer.position(0);//设置缓冲区起始位置
		//特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		//转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		//顶点纹理坐标数据的初始化================end============================
	}

	//初始化着色器的方法
	public void initShader()
	{
		//加载顶点着色器的脚本内容
		mVertexShader= ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
		//加载片元着色器的脚本内容
		mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
		//基于顶点着色器与片元着色器创建程序
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		//获取程序中顶点位置属性引用
		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		//获取程序中顶点颜色属性引用
		maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
		//获取程序中总变换矩阵引用
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
		//获取位置、旋转变换矩阵引用
		muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
		//获取程序中光源位置引用
		maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
		//获取程序中顶点纹理坐标属性引用
		maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
		//获取程序中摄像机位置引用
		maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera");
	}

	//初始化着色器的方法
	public void initShaderTwo()
	{
		//加载顶点着色器的脚本内容
		mVertexShaderTwo= ShaderUtil.loadFromAssetsFile("vertex_two.sh", mv.getResources());
		//加载片元着色器的脚本内容
		mFragmentShaderTwo=ShaderUtil.loadFromAssetsFile("frag_two.sh", mv.getResources());
		//基于顶点着色器与片元着色器创建程序
		mProgramTwo = ShaderUtil.createProgram(mVertexShaderTwo, mFragmentShaderTwo);
		//获取程序中顶点位置属性引用
		maPositionHandleTwo= GLES30.glGetAttribLocation(mProgramTwo, "aPosition");
		//获取程序中总变换矩阵引用
		muMVPMatrixHandleTwo = GLES30.glGetUniformLocation(mProgramTwo, "uMVPMatrix");
		//获取位置、旋转变换矩阵引用
		muMMatrixHandleTwo = GLES30.glGetUniformLocation(mProgramTwo, "uMMatrix");
		//获取程序中摄像机位置引用
		maCameraHandleTwo=GLES30.glGetUniformLocation(mProgramTwo, "uCamera");
		//虚化带的宽度
		mablurWidth=GLES30.glGetUniformLocation(mProgramTwo, "blurWidth");
		//虚化带的位置
		mablurPosition=GLES30.glGetUniformLocation(mProgramTwo, "blurPosition");
		//屏幕宽度
		maScreenWidth=GLES30.glGetUniformLocation(mProgramTwo, "screenWidth");
		//屏幕高度
		maScreenHeight=GLES30.glGetUniformLocation(mProgramTwo, "screenHeight");
	}
	public void drawSelf(int texId)
	{
		if(!initFlag)
		{
			initShader();
			initFlag=true;
		}
		//指定使用某套着色器程序
		GLES30.glUseProgram(mProgram);
		MatrixState.pushMatrix();//保护场景
		//将最终变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		//将位置、旋转变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
		//将光源位置传入渲染管线
		GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
		//将摄像机位置传入渲染管线
		GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
		// 将顶点位置数据传入渲染管线
		GLES30.glVertexAttribPointer
				(
						maPositionHandle,
						3,
						GLES30.GL_FLOAT,
						false,
						3*4,
						mVertexBuffer
				);
		//将顶点法向量数据传入渲染管线
		GLES30.glVertexAttribPointer
				(
						maNormalHandle,
						3,
						GLES30.GL_FLOAT,
						false,
						3*4,
						mNormalBuffer
				);
		//将顶点纹理数据传入渲染管线
		GLES30.glVertexAttribPointer
				(
						maTexCoorHandle,
						2,
						GLES30.GL_FLOAT,
						false,
						2*4,
						mTexCoorBuffer
				);
		//启用顶点位置、法向量、纹理坐标数据
		GLES30.glEnableVertexAttribArray(maPositionHandle);
		GLES30.glEnableVertexAttribArray(maNormalHandle);
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);
		//绑定纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
		//绘制加载的物体
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
		MatrixState.popMatrix();//恢复场景
	}
	public void drawSelfTwo(int texId)
	{
		if(!initFlagTwo)
		{
			initShaderTwo();
			initFlagTwo=true;
		}
		//指定使用某套着色器程序
		GLES30.glUseProgram(mProgramTwo);
		MatrixState.pushMatrix();//保护场景
		//将最终变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandleTwo, 1, false, MatrixState.getFinalMatrix(), 0);
		//将位置、旋转变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMMatrixHandleTwo, 1, false, MatrixState.getMMatrix(), 0);
		//将摄像机位置传入渲染管线
		GLES30.glUniform3fv(maCameraHandleTwo, 1, MatrixState.cameraFB);
		//传递宽度
		GLES30.glUniform1f(mablurWidth, Constant.blurWidth);
		//传递位置
		GLES30.glUniform1f(mablurPosition, Constant.blurPosition);
		//传递宽度
		GLES30.glUniform1f(maScreenWidth, Constant.screen_width);
		//传递高度
		GLES30.glUniform1f(maScreenHeight, Constant.screen_height);
		// 将顶点位置数据传入渲染管线
		GLES30.glVertexAttribPointer
				(
						maPositionHandleTwo,
						3,
						GLES30.GL_FLOAT,
						false,
						3*4,
						mVertexBuffer
				);
		//启用顶点位置
		GLES30.glEnableVertexAttribArray(maPositionHandleTwo);
		//绑定纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
		//绘制加载的物体
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
		MatrixState.popMatrix();//恢复场景
	}

}
