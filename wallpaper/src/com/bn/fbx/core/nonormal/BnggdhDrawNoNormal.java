package com.bn.fbx.core.nonormal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.Constant.ShaderUtil;
import wyf.lxg.mywallpaper.MySurfaceView;
import android.annotation.SuppressLint;
import android.opengl.GLES30;
import com.bn.fbx.core.LoadTextrueUtil;
import com.bn.jar.bnggdh.Bnggdh;;

@SuppressLint("UseSparseArrays")
public class BnggdhDrawNoNormal
{

	public float maxKeytime;
	private int texId;
	private Bnggdh bnn;
	private float dt;//步长
	public float time = 0;//当前时间
	private float interval = 0.0f;// 一组动画 和 一组动画 的间隔时间

	public Object lock=new Object();
	public boolean hasUpdateTask=false;

	float[] positionBuf;

	public BnggdhDrawNoNormal(InputStream is, MySurfaceView mv, String path)
	{
		bnn = new Bnggdh(is);
		try {
			bnn.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		maxKeytime = bnn.getMaxKeytime();

		this.texId = LoadTextrueUtil.initTextureRepeat(mv, path);
		initShader(mv);
		initBuffer();

		BNThreadGroupNoNormal.addTask(this);
	}

	public void finishSelf()
	{
		BNThreadGroupNoNormal.removeTask(this);
	}

	public void setDt(float dt)
	{
		this.dt=dt;
	}

	int mProgram;// 自定义渲染管线程序id
	int maPositionHandle;// 顶点位置属性引用id
	int maTexCoorHandle; // 顶点纹理坐标属性引用id
	int muMVPMatrixHandle;// 总变换矩阵引用id
	int muTexHandle;// 纹理属性id

	ByteBuffer vbb2;// 法向量坐标数据的映射缓冲
	private String mVertexShader;
	private String mFragmentShader;
	private int maLightLocationHandle;
	private int maCameraHandle;
	private int BenWl;
	private int muMMatrixHandle;

	//初始化着色器
	public void initShader(MySurfaceView mv){
		//加载顶点着色器的脚本内容
		mVertexShader=ShaderUtil.loadFromAssetsFile("fish_vertex.sh", mv.getResources());
		//加载片元着色器的脚本内容
		mFragmentShader=ShaderUtil.loadFromAssetsFile("fish_frag.sh", mv.getResources());
		//基于顶点着色器与片元着色器创建程序
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		//获取程序中顶点位置属性引用id
		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		//获取位置、旋转变换矩阵引用
		muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
		//获取程序中光源位置属性引用
		maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
		//获取程序中摄像机位置引用
		maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera");
		//获取程序中顶点纹理坐标属性引用id
		maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
		BenWl=GLES30.glGetUniformLocation(mProgram, "sTextureHd");
		//获取程序中总变换矩阵引用id
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	private FloatBuffer mTextureBuffer;// 顶点纹理数据缓冲
	private ShortBuffer mIndexBuffer;// 顶点索引数据缓存
	private int mTextureBufferId;
	private int mIndexBufferId;
	private int mVertexBufferId;// 顶点坐标数据缓冲 id

	ByteBuffer vbb1;// 顶点坐标数据的映射缓冲
	FloatBuffer mVertexMappedBuffer;// 顶点坐标映射缓冲对应的顶点坐标数据缓冲

	private void initBuffer() {
		int bufferIds[] = new int[3];
		GLES30.glGenBuffers(3, bufferIds, 0);
		mTextureBufferId = bufferIds[0];
		mIndexBufferId = bufferIds[1];
		mVertexBufferId = bufferIds[2];

		ByteBuffer mTex_bf = ByteBuffer
				.allocateDirect(bnn.getTextures().length * 4);// 创建顶点纹理数据缓冲
		mTex_bf.order(ByteOrder.nativeOrder());// 设置字节顺序
		mTextureBuffer = mTex_bf.asFloatBuffer();// 转换成FloatBuffer
		mTextureBuffer.put(bnn.getTextures());// 向 顶点纹理数据缓存区 中放 顶点纹理数据
		mTextureBuffer.position(0);// 设置缓冲区的起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		// 绑定到纹理坐标数据缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTextureBufferId);
		// 向纹理坐标数据缓冲送入数据
		GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
				bnn.getTextures().length * 4, mTextureBuffer,
				GLES30.GL_STATIC_DRAW);

		ByteBuffer mInd_bf = ByteBuffer
				.allocateDirect(bnn.getIndices().length * 2);// 创建顶点索引数据缓存
		mInd_bf.order(ByteOrder.nativeOrder());// 设置字节顺序
		mIndexBuffer = mInd_bf.asShortBuffer();// 转换成ShortBuffer
		mIndexBuffer.put(bnn.getIndices());// 向 顶点索引数据缓存 中放 顶点索引数据
		mIndexBuffer.position(0);// 设置缓冲区的起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		// 绑定到纹理坐标数据缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mIndexBufferId);
		// 向纹理坐标数据缓冲送入数据
		GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
				bnn.getIndices().length * 2, mIndexBuffer,
				GLES30.GL_STATIC_DRAW);

		// 顶点坐标数据的初始化================start============================
		// 绑定到顶点坐标数据缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVertexBufferId);
		// 向顶点坐标数据缓冲送入数据,分配vertices.length*4个存储单位（通常是字节）的
		// 内存，用于存储顶点数据或索引。以前所有与当前绑定对象相关联的数据都将删除。
		GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
				bnn.getPosition().length * 4, null, GLES30.GL_STATIC_DRAW);
		vbb1 = (ByteBuffer) GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER, // 表示顶点数据
				0, // 偏移量
				bnn.getPosition().length * 4, // 长度
				GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT// 访问标志
		);
		if (vbb1 == null) {
			return;
		}
		vbb1.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexMappedBuffer = vbb1.asFloatBuffer();// 转换为Float型缓冲

		mVertexMappedBuffer.put(bnn.getPosition());// 向映射的缓冲区中放入顶点坐标数据
		positionBuf=new float[bnn.getPosition().length];
		// verticesCube
		mVertexMappedBuffer.position(0);// 设置缓冲区起始位置
		if (GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER) == false) {
			return;
		}
		// 顶点坐标数据的初始化================end============================

		// 绑定到系统默认缓冲 系统的是0 要不然其他正常的就画不出来了
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
	}

	private void refreshBuffer()
	{
		if(!hasUpdateTask) return;
		synchronized(lock)
		{
			// 绑定到顶点坐标数据缓冲
			GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVertexBufferId);
			vbb1 = (ByteBuffer) GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER, 0, // 偏移量
					bnn.getPosition().length * 4, // 长度
					GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT// 访问标志
			);
			if (vbb1 == null) {
				return;
			}
			vbb1.order(ByteOrder.nativeOrder());// 设置字节顺序
			mVertexMappedBuffer = vbb1.asFloatBuffer();// 转换为Float型缓冲
			mVertexMappedBuffer.put(positionBuf);// 向映射的缓冲区中放入顶点坐标数据
			mVertexMappedBuffer.position(0);// 设置缓冲区起始位置
			if (GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER) == false) {
				return;
			}
			hasUpdateTask=false;
		}
	}

	public void updateTime()
	{
		time += dt;// 更新模型动画时间
		// 若当前播放时间大于总的动画时间则实际播放时间等于当前播放时间减去总的动画时间
		if (time >= (maxKeytime + dt + interval))
		{
			time = 0;
		}
		bnn.updata(time);

		synchronized(lock)
		{
			System.arraycopy(bnn.getPosition(), 0, positionBuf, 0, positionBuf.length);
			hasUpdateTask=true;
		}
	}

	public void draw()
	{
		refreshBuffer();// 更新缓存区

		GLES30.glUseProgram(mProgram); //指定使用某套shader程序
		MatrixState.copyMVMatrix();
		//将最终变换矩阵传入shader程序
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
//      //将光源位置传入着色器程序
		GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
		//将摄像机位置传入着色器程序
		GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

		GLES30.glEnableVertexAttribArray(maPositionHandle);// 允许顶点位置数据
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);// 允许顶点纹理数据

		// 顶点数据=======first============
		// 绑定到顶点坐标数据缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVertexBufferId);
		// 将顶点位置数据送入渲染管线
		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,
				false, 3 * 4, 0);
		// 顶点数据=======end============

		// 纹理数据=======first============
		// 绑定到顶点纹理坐标数据缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTextureBufferId);
		// 指定顶点纹理坐标数据
		GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT,
				false, 2 * 4, 0);
		// 绑定到系统默认缓冲 系统的是0 要不然其他正常的就画不出来了
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
		// 纹理数据=======end============

		// 绑定纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.texId);// 第二个参数如果用好几副纹理图的话
		// 应该写
		// this.vdfd.texId[i]
		GLES30.glUniform1i(muTexHandle, 0);

		// 索引数据=======first============
		// 根据索引缓存区来绘制
		GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferId);
		// 以三角形方式执行绘制
		GLES30.glDrawElements(GLES30.GL_TRIANGLES, bnn.getIndices().length,
				GLES30.GL_UNSIGNED_SHORT, 0);
		GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
		// 索引数据=======end============
		GLES30.glUniform1i(BenWl, 1);
		GLES30.glDisableVertexAttribArray(maPositionHandle);
		GLES30.glDisableVertexAttribArray(maTexCoorHandle);
//		 GLES30.glVertexAttribPointer(	//将顶点坐标缓冲送入渲染管线
//	        		maPositionHandle,   //顶点位置属性引用id
//	        		3,
//	        		GLES30.GL_FLOAT, 	//顶点类型
//	        		false,
//	                3*4,
//	                this.vertexCoordingBuffer[i]//顶点数组
//	            );
		//将顶点法向量数据传入渲染管线

	}
}
