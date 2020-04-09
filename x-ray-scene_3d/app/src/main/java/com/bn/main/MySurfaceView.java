package com.bn.main;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Constant.Constant;
import com.bn.Constant.LoadUtil;
import com.bn.Constant.LoadedObjectVertexNormalTexture;
import com.bn.Constant.MatrixState;
import com.bn.rect.TextureRect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@SuppressLint("NewApi")
public class MySurfaceView extends GLSurfaceView
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	private SceneRenderer mRenderer;//场景渲染器

	private float mPreviousY;//上次的触控位置Y坐标
	private float mPreviousX;//上次的触控位置X坐标


	float ratio;

	static final int GEN_TEX_WIDTH=1024;
	static final int GEN_TEX_HEIGHT=1024;

	int SCREEN_WIDTH;
	int SCREEN_HEIGHT;
	float cx=0;
	float cy=20;
	float cz=35;
	float cxPrevious=0;
	float cyPrevious=0;
	float czPrevious=0;
	//目标点
	float tx=0;
	float ty=20;
	float tz=0;
	//方向向量
	float dx=0;
	float dy=1;
	float dz=0;
	//摄像机的位置角度
	float cr=35;//摄像机半径
	float cAngle=0;
	float xAngle=0;

	float objScale=0.0241f;//教室缩放系数
	float peopleScale=20.0f;//人物缩放系数
	//投影矩阵系数
	float near=1.6f;//
	float qiangDis=85;//四面墙离中心点的位置

	public MySurfaceView(Context context) {
		super(context);
		this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
		mRenderer = new SceneRenderer();	//创建场景渲染器
		setRenderer(mRenderer);				//设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
	}

	//触摸事件回调方法
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		float y = e.getY();
		float x = e.getX();
		switch (e.getAction()) {
			case MotionEvent.ACTION_MOVE:
				float dy = y - mPreviousY;//计算触控笔Y位移
				float dx = x - mPreviousX;//计算触控笔X位移
				cAngle+=dx * TOUCH_SCALE_FACTOR;
				xAngle+=dy*TOUCH_SCALE_FACTOR;
				cx=(float)(Math.sin(Math.toRadians(cAngle))*cr);
				cz=(float)(Math.cos(Math.toRadians(cAngle))*cr);
		}
		mPreviousY = y;//记录触控笔位置
		mPreviousX = x;//记录触控笔位置
		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer
	{
		//第一次绘制的缓冲id(正常场景和深度纹理)
		int frameBufferId;//帧缓冲id
		int renderDepthBufferId;//渲染深度缓冲id
		int[] textureIds = new int[2];//用于存放产生纹理id的数组
		//第二次帧缓冲（正常人物纹理）
		int frameBufferIdTwo;//帧缓冲id
		int renderDepthBufferIdTwo;//渲染深度缓冲id
		int textureIdTwo;//用于存放产生纹理id

		int texdiban;//地板纹理
		int texqiang;//墙的纹理
		int texxiati;//下面纹理
		int texfoot;//脚的纹理
		int texshenti;//身体纹理
		int texhead;//头部纹理
		int texbozi;//脖子纹理
		int texbianzi;//辫子纹理
		int zhuantou;//砖头纹理

		TextureRect trect;//纹理矩形

		LoadedObjectVertexNormalTexture diban;//地板
		LoadedObjectVertexNormalTexture qiang;//墙模型
		LoadedObjectVertexNormalTexture xiati;//下体
		LoadedObjectVertexNormalTexture houti;//后部
		LoadedObjectVertexNormalTexture foot;//脚
		LoadedObjectVertexNormalTexture shenti;//身体
		LoadedObjectVertexNormalTexture head;//头部
		LoadedObjectVertexNormalTexture bozi;//脖子
		LoadedObjectVertexNormalTexture bianzi;//辫子
		LoadedObjectVertexNormalTexture touding;//头顶
		//初始化第一次MRT，一为正常场景，二为深度纹理
		public boolean initMRTBuffers()
		{
			int[] attachments=new int[]{
					GLES30.GL_COLOR_ATTACHMENT0,
					GLES30.GL_COLOR_ATTACHMENT1,
			};
			int tia[]=new int[1];//用于存放产生的帧缓冲id的数组

			//帧缓冲========start==========
			GLES30.glGenFramebuffers(1, tia, 0);//产生一个帧缓冲id
			frameBufferId=tia[0];//将帧缓冲id记录到成员变量中
			//绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
			//帧缓冲========end==========

			//渲染缓冲=========start============
			GLES30.glGenRenderbuffers(1, tia, 0);//产生一个渲染缓冲id
			renderDepthBufferId=tia[0];//将渲染缓冲id记录到成员变量中
			//绑定指定id的渲染缓冲
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
			//为渲染缓冲初始化存储
			GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER,
					GLES30.GL_DEPTH_COMPONENT16,GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
			//设置自定义帧缓冲的深度缓冲附件
			GLES30.glFramebufferRenderbuffer
					(
							GLES30.GL_FRAMEBUFFER,
							GLES30.GL_DEPTH_ATTACHMENT,		//深度缓冲附件
							GLES30.GL_RENDERBUFFER,			//渲染缓冲
							renderDepthBufferId				//渲染深度缓冲id
					);
			//渲染缓冲=========end============


			GLES30.glGenTextures//产生4个纹理id
					(
							textureIds.length,			//产生的纹理id的数量
							textureIds,	//纹理id的数组
							0			//偏移量
					);


			//初始化正常场景缓冲
				GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureIds[0]);//绑定纹理id
				GLES30.glTexImage2D//设置颜色附件纹理图的格式
						(
								GLES30.GL_TEXTURE_2D,
								0,						//层次
								GLES30.GL_RGBA, 		//内部格式
								GEN_TEX_WIDTH,			//宽度
								GEN_TEX_HEIGHT,			//高度
								0,						//边界宽度
								GLES30.GL_RGBA,			//格式
								GLES30.GL_UNSIGNED_BYTE,//每个像素数据格式
								null
						);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MIN采样方式
						GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MAG采样方式
						GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置S轴拉伸方式
						GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置T轴拉伸方式
						GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

				GLES30.glFramebufferTexture2D		//将指定纹理绑定到帧缓冲
						(
								GLES30.GL_DRAW_FRAMEBUFFER,
								attachments[0],					//颜色附件
								GLES30.GL_TEXTURE_2D,
								textureIds[0], 					//纹理id
								0								//层次
						);



			//初始化深度纹理
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[1]);//绑定纹理id
			//设置min、mag的采样方式
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
			//设置纹理s、t轴的拉伸方式
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
			GLES30.glTexImage2D//设置颜色附件纹理图的格式
					(
							GLES30.GL_TEXTURE_2D,
							0, //层次
							GLES30.GL_R16F, //内部格式
							GEN_TEX_WIDTH, //宽度
							GEN_TEX_HEIGHT, //高度
							0, //边界宽度
							GLES30.GL_RED,//格式
							GLES30.GL_FLOAT, //每像素数据格式
							null
					);
			GLES30.glFramebufferTexture2D		//将指定纹理绑定到帧缓冲
					(
							GLES30.GL_DRAW_FRAMEBUFFER,
							attachments[1],					//颜色附件
							GLES30.GL_TEXTURE_2D,
							textureIds[1], 					//纹理id
							0								//层次
					);

			GLES30.glDrawBuffers(attachments.length, attachments,0);
			if(GLES30.GL_FRAMEBUFFER_COMPLETE !=
					GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER))
			{
				return false;
			}
			return true;
		}
		//初始化第二次FBO
		public void initFBOBuffers(){
			int tia[]=new int[1];//用于存放产生的帧缓冲id的数组
			GLES30.glGenFramebuffers(1, tia, 0);//产生一个帧缓冲id
			frameBufferIdTwo=tia[0];//将帧缓冲id记录到成员变量中
			//绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIdTwo);

			GLES30.glGenRenderbuffers(1, tia, 0);//产生一个渲染缓冲id
			renderDepthBufferIdTwo=tia[0];//将渲染缓冲id记录到成员变量中
			//绑定指定id的渲染缓冲
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferIdTwo);
			//为渲染缓冲初始化存储
			GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER,
					GLES30.GL_DEPTH_COMPONENT16,GEN_TEX_WIDTH, GEN_TEX_HEIGHT);

			int[] tempIds = new int[1];//用于存放产生纹理id的数组
			GLES30.glGenTextures//产生一个纹理id
					(
							1,         //产生的纹理id的数量
							tempIds,   //纹理id的数组
							0           //偏移量
					);
			textureIdTwo=tempIds[0];//将纹理id记录到成员变量
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureIdTwo);//绑定纹理id
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MIN采样方式
					GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MAG采样方式
					GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置S轴拉伸方式
					GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置T轴拉伸方式
					GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
			GLES30.glTexImage2D//设置颜色附件纹理图的格式
					(
							GLES30.GL_TEXTURE_2D,
							0,						//层次
							GLES30.GL_RGBA, 		//内部格式
							GEN_TEX_WIDTH,			//宽度
							GEN_TEX_HEIGHT,			//高度
							0,						//边界宽度
							GLES30.GL_RGBA,			//格式
							GLES30.GL_UNSIGNED_BYTE,//每个像素数据格式
							null
					);
			GLES30.glFramebufferTexture2D		//设置自定义帧缓冲的颜色缓冲附件
					(
							GLES30.GL_FRAMEBUFFER,
							GLES30.GL_COLOR_ATTACHMENT0,	//颜色缓冲附件
							GLES30.GL_TEXTURE_2D,
							textureIdTwo, 						//纹理id
							0								//层次
					);
			GLES30.glFramebufferRenderbuffer	//设置自定义帧缓冲的深度缓冲附件
					(
							GLES30.GL_FRAMEBUFFER,
							GLES30.GL_DEPTH_ATTACHMENT,		//深度缓冲附件
							GLES30.GL_RENDERBUFFER,			//渲染缓冲
							renderDepthBufferIdTwo				//渲染深度缓冲id
					);
		}
		//第一次绘制纹理
		public void drawOne()//通过绘制产生纹理
		{
			//设置视窗大小及位置
			GLES30.glViewport(0, 0, GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
			//绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
			//清除深度缓冲与颜色缓冲
			GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
			//开启深度检测
			GLES30.glEnable(GLES30.GL_DEPTH_TEST);
			//设置投影矩阵
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, near, 3000);
			//调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera(cx,cy,cz, tx,ty,tz, dx,dy,dz);
			MatrixState.pushMatrix();//保护现场
			MatrixState.scale(objScale,objScale,objScale);
			if(qiang!=null)//绘制墙
			{
				qiang.drawSelf(zhuantou);
			}
			if(diban!=null)//绘制地板
			{
				diban.drawSelf(texdiban);
			}
			MatrixState.popMatrix();//恢复现场

			MatrixState.pushMatrix();//右手边的墙
			MatrixState.translate(qiangDis,80,0);
			MatrixState.rotate(90,0,0,1);
			MatrixState.rotate(90,0,1,0);
			MatrixState.scale(objScale,objScale,objScale);
			if(diban!=null)//绘制地板
			{
				diban.drawSelf(texdiban);
			}
			MatrixState.popMatrix();//恢复现场

			MatrixState.pushMatrix();//左手边的墙
			MatrixState.translate(-qiangDis,80,0);
			MatrixState.rotate(-90,0,0,1);
			MatrixState.rotate(90,0,1,0);
			MatrixState.scale(objScale,objScale,objScale);
			if(diban!=null)//绘制地板
			{
				diban.drawSelf(texdiban);
			}
			MatrixState.popMatrix();//恢复现场

			MatrixState.pushMatrix();
			MatrixState.translate(0,80,qiangDis);
			MatrixState.rotate(-90,1,0,0);
			MatrixState.scale(objScale,objScale,objScale);
			if(diban!=null)//绘制地板
			{
				diban.drawSelf(texdiban);
			}
			MatrixState.popMatrix();//恢复现场

			MatrixState.pushMatrix();//对面的墙
			MatrixState.translate(0,80,-qiangDis);
			MatrixState.rotate(90,1,0,0);
			MatrixState.scale(objScale,objScale,objScale);
			if(diban!=null)//绘制地板
			{
				diban.drawSelf(texdiban);
			}
			MatrixState.popMatrix();//恢复现场
		}
		//第二次绘制正常人物
		public void drawTwo(){
			GLES30.glViewport(0, 0, GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
			//绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIdTwo);
			//清除深度缓冲与颜色缓冲
			GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
			GLES30.glDisable(GLES30.GL_CULL_FACE);//关闭背面剪裁
			//设置投影矩阵
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, near, 3000);
			//调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera(cx,cy,cz, tx,ty,tz, dx,dy,dz);
			//存储此次摄像机位置
			cxPrevious=cx;
			cyPrevious=cy;
			czPrevious=cz;
			//绘制人物
			MatrixState.pushMatrix();//保护现场
			MatrixState.translate(10,0,0);
			MatrixState.rotate(-90,0,1,0);
			MatrixState.scale(peopleScale,peopleScale,peopleScale);
			if(houti!=null){
				houti.drawSelfTwo(texxiati);
			}
			if(xiati!=null){
				xiati.drawSelfTwo(texxiati);
			}
			if(foot!=null){
				foot.drawSelfTwo(texfoot);
			}
			if(shenti!=null){
				shenti.drawSelfTwo(texshenti);
			}
			if(head!=null){
				head.drawSelfTwo(texhead);
			}
			if(bozi!=null){
				bozi.drawSelfTwo(texbozi);
			}
			if(bianzi!=null){
				bianzi.drawSelfTwo(texbianzi);
			}
			if(touding!=null){
				touding.drawSelfTwo(texbianzi);
			}
			MatrixState.popMatrix();//恢复现场
			GLES30.glEnable(GLES30.GL_CULL_FACE);//打开背面剪裁
		}
		//第三次绘制真实场景
		public void drawThree()
		{
			//设置视窗大小及位置
			GLES30.glViewport(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);//绑定帧缓冲id
			//清除深度缓冲与颜色缓冲
			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT |GLES30.GL_COLOR_BUFFER_BIT);
			//关闭深度检测
			GLES30.glDisable(GLES30.GL_DEPTH_TEST);
			//设置正交投影
			MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 2, 100);
			//调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera(0,0,3,0f,0f,0f,0f,1.0f,0.0f);
			MatrixState.pushMatrix();
			trect.drawSelf(textureIds[0]);//绘制纹理矩形
			MatrixState.popMatrix();

			GLES30.glDisable(GLES30.GL_CULL_FACE);//关闭背面剪裁
			//设置投影矩阵
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, near, 3000);
			//调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera(cxPrevious,cyPrevious,czPrevious, tx,ty,tz, dx,dy,dz);
			//绘制人物
			MatrixState.pushMatrix();//保护现场
			MatrixState.translate(10,0,0);
			MatrixState.rotate(-90,0,1,0);
			MatrixState.scale(peopleScale,peopleScale,peopleScale);
			/**
			 * 正常纹理
			 * 深度纹理
			 * 人物纹理
			 */
			if(houti!=null){
				houti.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			if(xiati!=null){
				xiati.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			if(foot!=null){
				foot.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			if(shenti!=null){
				shenti.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			if(head!=null){
				head.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			if(bozi!=null){
				bozi.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			if(bianzi!=null){
				bianzi.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			if(touding!=null){
				touding.drawSelfFour(textureIds[0],textureIds[1],textureIdTwo);
			}
			MatrixState.popMatrix();//恢复现场
			GLES30.glEnable(GLES30.GL_CULL_FACE);//打开背面剪裁
		}
		public void onDrawFrame(GL10 gl)
		{
			drawOne();//绘制真实场景与深度纹理
			drawTwo();//第二次绘制真实人物
			drawThree();//绘制人物和真实场景
		}
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			SCREEN_WIDTH=width;
			SCREEN_HEIGHT=height;
			Constant.screen_width=width;
			Constant.screen_height=height;
			ratio = (float) width / height;//计算GLSurfaceView的宽高比
			trect=new TextureRect(MySurfaceView.this,ratio);
			initMRTBuffers();//初始化多重渲染目标帧缓冲
			initFBOBuffers();//初始化第二次帧缓冲
		}
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//设置屏幕背景色RGBA
			GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);
			//打开深度检测
			GLES30.glEnable(GLES30.GL_DEPTH_TEST);
			//打开背面剪裁
			GLES30.glEnable(GLES30.GL_CULL_FACE);
			//初始化变换矩阵
			MatrixState.setInitStack();
			//设置灯光
			MatrixState.setLightLocation(0, 20, 0);
			//加载纹理
			texdiban=initTexture(MySurfaceView.this.getResources(),"diban.jpg");
			texqiang=initTexture(MySurfaceView.this.getResources(),"qiang.jpg");
			texxiati=initTexture(MySurfaceView.this.getResources(),"xiati.jpg");
			texfoot=initTexture(MySurfaceView.this.getResources(),"foot.jpg");
			texshenti=initTexture(MySurfaceView.this.getResources(),"shenti.jpg");
			texhead=initTexture(MySurfaceView.this.getResources(),"head.jpg");
			texbozi=initTexture(MySurfaceView.this.getResources(),"bozi.jpg");
			texbianzi=initTexture(MySurfaceView.this.getResources(),"bianzi.jpg");
			zhuantou=initTexture(MySurfaceView.this.getResources(),"zhuantou.jpg");
			//加载模型
			diban=LoadUtil.loadFromFile("obj/diban.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			qiang=LoadUtil.loadFromFile("obj/qiang.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			xiati=LoadUtil.loadFromFile("obj/xiati.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			houti=LoadUtil.loadFromFile("obj/houti.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			foot=LoadUtil.loadFromFile("obj/foot.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			shenti=LoadUtil.loadFromFile("obj/shenti.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			head=LoadUtil.loadFromFile("obj/head.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			bozi=LoadUtil.loadFromFile("obj/bozi.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			bianzi=LoadUtil.loadFromFile("obj/bianzi.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			touding=LoadUtil.loadFromFile("obj/touding.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
		}
	}
	public int initTexture(Resources res, String pname)//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
				(
						1,          //产生的纹理id的数量
						textures,   //纹理id的数组
						0           //偏移量
				);
		int textureId=textures[0];
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);//拉伸方式为截取方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
		//通过输入流加载图片===============begin===================
		InputStream is = null;
		String name="pic/"+pname;
		try {
			is = res.getAssets().open(name);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Bitmap bitmapTmp;
		try {
			bitmapTmp = BitmapFactory.decodeStream(is);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		//通过输入流加载图片===============end=====================

		//实际加载纹理
		GLUtils.texImage2D
				(
						GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
						0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
						bitmapTmp, 			  //纹理图像
						0					  //纹理边框尺寸
				);
		bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
		return textureId;
	}
}
