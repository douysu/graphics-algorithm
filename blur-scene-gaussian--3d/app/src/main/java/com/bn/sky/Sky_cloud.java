package com.bn.sky;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.opengl.GLES30;

import com.bn.Constant.MatrixState;
import com.bn.Constant.ShaderUtil;
import com.bn.main.MySurfaceView;

@SuppressLint("NewApi") public class Sky_cloud
{
	final float UNIT_SIZE=300.0f;
	//自定义渲染程序的引用
	int mProgram;
	//总变换矩阵的引用
	int muMVPMatrixHandle;
	//顶点属性的引用
	int maPositionHandle;
	//顶点纹理坐标属性的引用
	int maTexCoorHandle;
	//顶点数据缓冲以及顶点纹理坐标数据缓冲
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	//顶点数量
	int vCount=0;

	public Sky_cloud(MySurfaceView mv)
	{
		initVertexData(UNIT_SIZE);//半径
		initShader(mv);
	}
	//初始化顶点数据的方法
	public void initVertexData(float radius)
	{
		float ANGLE_SPAN=18f;
		float angleV=90;
		ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList

		for(float vAngle=angleV;vAngle>0;vAngle=vAngle-ANGLE_SPAN)//垂直方向angleSpan度一份
		{
			for(float hAngle=360;hAngle>0;hAngle=hAngle-ANGLE_SPAN)//水平方向angleSpan度一份
			{
				//纵向横向各到一个角度后计算对应的此点在球面上的四边形顶点坐标
				//并构建两个组成四边形的三角形
				double xozLength=radius*Math.cos(Math.toRadians(vAngle));
				float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
				float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
				float y1=(float)(radius*Math.sin(Math.toRadians(vAngle)));

				xozLength=radius*Math.cos(Math.toRadians(vAngle-ANGLE_SPAN));
				float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
				float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
				float y2=(float)(radius*Math.sin(Math.toRadians(vAngle-ANGLE_SPAN)));

				xozLength=radius*Math.cos(Math.toRadians(vAngle-ANGLE_SPAN));
				float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-ANGLE_SPAN)));
				float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-ANGLE_SPAN)));
				float y3=(float)(radius*Math.sin(Math.toRadians(vAngle-ANGLE_SPAN)));

				xozLength=radius*Math.cos(Math.toRadians(vAngle));
				float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-ANGLE_SPAN)));
				float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-ANGLE_SPAN)));
				float y4=(float)(radius*Math.sin(Math.toRadians(vAngle)));

				//构建第一三角形
				alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
				alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
				alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);

				//构建第二三角形
				alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
				alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
				alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
			}
		}
		vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
		//将alVertix中的坐标值转存到一个float数组中
		float vertices[]=new float[vCount*3];
		for(int i=0;i<alVertix.size();i++)
		{
			vertices[i]=alVertix.get(i);
		}
		//创建绘制顶点数据缓冲
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
		mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);//设置缓冲区起始位置

		//创建纹理坐标缓冲
		float[] textureCoors=generateTexCoor
				(
						(int)(360/ANGLE_SPAN), //纹理图切分的列数
						(int)(angleV/ANGLE_SPAN)  //纹理图切分的行数
				);
		ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoors.length*4);
		tbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mTexCoorBuffer = tbb.asFloatBuffer();//转换为float型缓冲
		mTexCoorBuffer.put(textureCoors);//向缓冲区中放入顶点着色数据
		mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}
	//初始化着色器程序的方法
	public void initShader(MySurfaceView mv)
	{
		String mVertexShader= ShaderUtil.loadFromAssetsFile("vertex_sky.sh", mv.getResources());
		String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_sky.sh", mv.getResources());
		mProgram=ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		//获取程序中顶点位置属性引用id
		maPositionHandle = GLES30.glGetAttribLocation(mProgram,"aPosition");
		//获取程序中顶点纹理坐标属性引用id
		maTexCoorHandle= GLES30.glGetAttribLocation(mProgram,"aTexCoor");
		//获取程序中总变换矩阵引用id
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram,"uMVPMatrix");
	}
	//自定义的绘制方法
	public void drawSelf(int texId)
	{
		//指定使用某套着色器程序
		GLES30.glUseProgram(mProgram);
		//将最终变换矩阵传入到渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		//将顶点位置数据传送进渲染管线
		GLES30.glVertexAttribPointer
				(
						maPositionHandle,
						3,
						GLES30.GL_FLOAT,
						false,
						3*4,
						mVertexBuffer
				);
		//将顶点纹理坐标数据传送进渲染管线
		GLES30.glVertexAttribPointer
				(
						maTexCoorHandle,
						2,
						GLES30.GL_FLOAT,
						false,
						2*4,
						mTexCoorBuffer
				);
		//允许顶点位置数据数组
		GLES30.glEnableVertexAttribArray(maPositionHandle);
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);

		//绑定纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

		//绘制纹理矩形
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
	}
	//自动切分纹理产生纹理数组的方法
	public float[] generateTexCoor(int bw,int bh)
	{
		float[] result=new float[bw*bh*6*2];
		float sizew=1.0f/bw;//列数
		float sizeh=1.0f/bh;//行数
		int c=0;
		for(int i=0;i<bh;i++)
		{
			for(int j=0;j<bw;j++)
			{
				//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
				float s=j*sizew;
				float t=i*sizeh;

				result[c++]=s;
				result[c++]=t;

				result[c++]=s+sizew;
				result[c++]=t;

				result[c++]=s;
				result[c++]=t+sizeh;

				result[c++]=s;
				result[c++]=t+sizeh;

				result[c++]=s+sizew;
				result[c++]=t;

				result[c++]=s+sizew;
				result[c++]=t+sizeh;
			}
		}
		return result;
	}
}