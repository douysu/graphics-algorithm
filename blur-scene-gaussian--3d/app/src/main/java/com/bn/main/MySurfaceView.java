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
import com.bn.Constant.LoadedObjectVertexNormalTextureMian;
import com.bn.Constant.MatrixState;
import com.bn.sky.Sky_cloud;

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
	float cy=22;
	float cz=-90;
	//目标点
	float tx=0;
	float ty=22;
	float tz=40;
	//方向向量
	float dx=0;
	float dy=1;
	float dz=0;
	//线程循环的标志位
	boolean flag=true;
	//摄像机的位置角度
	float cr=50;//摄像机半径
	float cAngle=0;
	float xAngle=0;

	float maxAngle=30.0f;//最大角度
	float objScale=0.0241f;//教室缩放系数
	float objBeiZi=0.02f;//杯子缩放系数
	//投影矩阵系数
	float near=11.0f;


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
				if(cAngle<=-maxAngle){//控制角度的大小
					cAngle=-maxAngle;
				}
				if(cAngle>=maxAngle){
				cAngle=maxAngle;
				}
				tx=(float)(Math.sin(Math.toRadians(cAngle))*cr)+cx;
				tz=(float)(Math.cos(Math.toRadians(cAngle))*cr)+cz;
		}
		mPreviousY = y;//记录触控笔位置
		mPreviousX = x;//记录触控笔位置
		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer
	{
		//加载茶壶绘制对象

		int frameBufferId;//帧缓冲id
		int renderDepthBufferId;//渲染深度缓冲id
		int textureId;//最后生成的纹理id

		int textureSky;//天空纹理
		int textWhiteBan;//白色地板
		int texchuanghu;//窗户纹理
		int texfangding;//房顶纹理
		int texdiban;//地板纹理
		int texheiban;//黑板纹理
		int texthongqi;//红旗
		int texchair;//椅子
		int texbeizi;//杯子纹理

		LoadedObjectVertexNormalTexture diban;//地板
		LoadedObjectVertexNormalTextureMian fangding;//房顶
		LoadedObjectVertexNormalTextureMian qiang1;//墙壁
		LoadedObjectVertexNormalTexture chuanghu;//窗户
		LoadedObjectVertexNormalTextureMian men;//门
		LoadedObjectVertexNormalTexture menkuang;//门框
		LoadedObjectVertexNormalTexture huangzhu;//黄柱
		LoadedObjectVertexNormalTexture baizhu;//白柱
		LoadedObjectVertexNormalTexture jiangtai;//讲台
		LoadedObjectVertexNormalTexture heiban;//黑板
		LoadedObjectVertexNormalTexture hongqi;//红旗
		LoadedObjectVertexNormalTexture jiangzhuo;//红旗
		LoadedObjectVertexNormalTexture baiban;//白色黑板
		LoadedObjectVertexNormalTexture zhuoheyi;//桌子和椅子
		LoadedObjectVertexNormalTexture beizi;//杯子
		LoadedObjectVertexNormalTexture beiziba;//杯子把
		LoadedObjectVertexNormalTexture beizigai;//杯子盖
		Sky_cloud sky;//天空穹
		public void initFRBuffers()//初始化帧缓冲和渲染缓冲的方法
		{
			int tia[]=new int[1];//用于存放产生的帧缓冲id的数组
			GLES30.glGenFramebuffers(1, tia, 0);//产生一个帧缓冲id
			frameBufferId=tia[0];//将帧缓冲id记录到成员变量中
			//绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);

			GLES30.glGenRenderbuffers(1, tia, 0);//产生一个渲染缓冲id
			renderDepthBufferId=tia[0];//将渲染缓冲id记录到成员变量中
			//绑定指定id的渲染缓冲
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
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
			textureId=tempIds[0];//将纹理id记录到成员变量
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureId);//绑定纹理id
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
							textureId, 						//纹理id
							0								//层次
					);
			GLES30.glFramebufferRenderbuffer	//设置自定义帧缓冲的深度缓冲附件
					(
							GLES30.GL_FRAMEBUFFER,
							GLES30.GL_DEPTH_ATTACHMENT,		//深度缓冲附件
							GLES30.GL_RENDERBUFFER,			//渲染缓冲
							renderDepthBufferId				//渲染深度缓冲id
					);
		}
		//第一次绘制纹理
		public void generateTextImage()//通过绘制产生纹理
		{
			//设置视窗大小及位置
			GLES30.glViewport(0, 0, GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
			//绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
			//清除深度缓冲与颜色缓冲
			GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
			//调用此方法计算产生透视投影矩阵
			MatrixState.setProjectFrustum(-ratio*5, ratio*5, -5, 5, near, 3000);
			//调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera(cx,cy,cz, tx,ty,tz, dx,dy,dz);
			//绘制杯子
			MatrixState.pushMatrix();//保护现场
			MatrixState.setLightLocation(0, 18, -72);//设置灯光位置
			GLES30.glDisable(GLES30.GL_CULL_FACE);
			MatrixState.translate(-4.0f,15,-66);
			MatrixState.scale(objBeiZi,objBeiZi,objBeiZi);
			if(beizi!=null){
				beizi.drawSelf(texbeizi);
			}
			if(beiziba!=null){
				beiziba.drawSelf(texfangding);
			}
			if(beizigai!=null){
				beizigai.drawSelf(texfangding);
			}
			//打开背面剪裁
			GLES30.glEnable(GLES30.GL_CULL_FACE);
			MatrixState.popMatrix();//恢复

			//绘制杯子
			MatrixState.pushMatrix();//保护现场
			GLES30.glDisable(GLES30.GL_CULL_FACE);
			MatrixState.translate(5.0f,15,-66);
			MatrixState.scale(objBeiZi,objBeiZi,objBeiZi);
			if(beizi!=null){
				beizi.drawSelf(texbeizi);
			}
			if(beiziba!=null){
				beiziba.drawSelf(texfangding);
			}
			if(beizigai!=null){
				beizigai.drawSelf(texfangding);
			}
			//打开背面剪裁
			GLES30.glEnable(GLES30.GL_CULL_FACE);
			MatrixState.popMatrix();//恢复
			//将灯光调回教室中间位置
			MatrixState.setLightLocation(0, 100, -70);
			//绘制场景
			if(sky!=null){//绘制天空穹
				sky.drawSelf(textureSky);
			}

			MatrixState.pushMatrix();//保护现场
			MatrixState.scale(objScale,objScale,objScale);
			if(diban!=null)//绘制地板
			{
				diban.drawSelf(texdiban);
			}
			//开启混合
			GLES30.glEnable(GLES30.GL_BLEND);
			//设置混合因子,其中第一个为源因子，第二个为目标因子
			GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
			if(fangding!=null)//绘制房顶
			{
				fangding.drawSelf(texfangding);
			}
			if(chuanghu!=null)//绘制窗户
			{
				chuanghu.drawSelf(texchuanghu);
			}
			//关闭混合
			GLES30.glDisable(GLES30.GL_BLEND);
			if(qiang1!=null)//绘制墙壁
			{
				qiang1.drawSelf(texdiban);
			}
			if(men!=null)//绘制门
			{
				men.drawSelf(textWhiteBan);
			}
			if(menkuang!=null)//绘制门框
			{
				menkuang.drawSelf(textWhiteBan);
			}
			if(huangzhu!=null)//绘制黄色柱子
			{
				huangzhu.drawSelf(texdiban);
			}
			if(baizhu!=null)//绘制白色柱子
			{
				baizhu.drawSelf(texfangding);
			}
			if(jiangtai!=null)//绘制讲台
			{
				jiangtai.drawSelf(texdiban);
			}
			if(hongqi!=null)//绘制红旗
			{
				hongqi.drawSelf(texthongqi);
			}
			if(jiangzhuo!=null)//绘制讲桌
			{
				jiangzhuo.drawSelf(texchair);
			}
			if(baiban!=null)//绘制白板
			{
				baiban.drawSelf(texheiban);
			}
			if(heiban!=null)//绘制黑板
			{
				heiban.drawSelf(texheiban);
			}
			MatrixState.popMatrix();//恢复现场

			MatrixState.pushMatrix();//绘制椅子桌子
			MatrixState.scale(objScale,objScale,objScale);
			if(zhuoheyi!=null)//绘制第一排
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(0,0,-1200);//第二排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(0,0,-1200);//第三排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(0,0,-1200);//第四排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(0,0,-1200);//第五排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelf(texchair);
			}
			MatrixState.popMatrix();//恢复现场
		}
		//第二次绘制通过距离绘制
		public void drawBlur()
		{
			//设置视窗大小及位置
			GLES30.glViewport(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);//绑定帧缓冲id
			//清除深度缓冲与颜色缓冲
			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT |GLES30.GL_COLOR_BUFFER_BIT);
			//调用此方法计算产生透视投影矩阵
			MatrixState.setProjectFrustum(-ratio*5, ratio*5, -5, 5, near, 3000);
			//调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera(cx,cy,cz, tx,ty,tz, dx,dy,dz);
			//绘制场景
			//绘制杯子
			MatrixState.pushMatrix();//保护现场
			GLES30.glDisable(GLES30.GL_CULL_FACE);
			MatrixState.translate(-4.0f,15,-66);
			MatrixState.scale(objBeiZi,objBeiZi,objBeiZi);
			if(beizi!=null){
				beizi.drawSelfTwo(textureId);
			}
			if(beiziba!=null){
				beiziba.drawSelfTwo(textureId);
			}
			if(beizigai!=null){
				beizigai.drawSelfTwo(textureId);
			}
			//打开背面剪裁
			GLES30.glEnable(GLES30.GL_CULL_FACE);
			MatrixState.popMatrix();//恢复

			//绘制杯子
			MatrixState.pushMatrix();//保护现场
			GLES30.glDisable(GLES30.GL_CULL_FACE);
			MatrixState.translate(5.0f,15,-66);
			MatrixState.scale(objBeiZi,objBeiZi,objBeiZi);
			if(beizi!=null){
				beizi.drawSelfTwo(textureId);
			}
			if(beiziba!=null){
				beiziba.drawSelfTwo(textureId);
			}
			if(beizigai!=null){
				beizigai.drawSelfTwo(textureId);
			}
			//打开背面剪裁
			GLES30.glEnable(GLES30.GL_CULL_FACE);
			MatrixState.popMatrix();//恢复

			MatrixState.pushMatrix();//保护现场
			MatrixState.scale(objScale,objScale,objScale);
			if(diban!=null)//绘制地板
			{
				diban.drawSelfTwo(textureId);
			}
			if(qiang1!=null)//绘制墙壁
			{
				qiang1.drawSelfTwo(textureId);
			}
			if(fangding!=null)//绘制房顶
			{
				fangding.drawSelfTwo(textureId);
			}
			if(chuanghu!=null)//绘制窗户
			{
				chuanghu.drawSelfTwo(textureId);
			}
			if(men!=null)//绘制门
			{
				men.drawSelfTwo(textureId);
			}
			if(menkuang!=null)//绘制门框
			{
				menkuang.drawSelfTwo(textureId);
			}
			if(huangzhu!=null)//绘制黄色柱子
			{
				huangzhu.drawSelfTwo(textureId);
			}
			if(baizhu!=null)//绘制白色柱子
			{
				baizhu.drawSelfTwo(textureId);
			}
			if(jiangtai!=null)//绘制讲台
			{
				jiangtai.drawSelfTwo(textureId);
			}
			if(hongqi!=null)//绘制红旗
			{
				hongqi.drawSelfTwo(textureId);
			}
			if(jiangzhuo!=null)//绘制讲桌
			{
				jiangzhuo.drawSelfTwo(textureId);
			}
			if(baiban!=null)//绘制白板
			{
				baiban.drawSelfTwo(textureId);
			}
			if(heiban!=null)//绘制黑板
			{
				heiban.drawSelfTwo(textureId);
			}
			MatrixState.popMatrix();//恢复现场

			MatrixState.pushMatrix();//绘制椅子桌子
			MatrixState.scale(objScale,objScale,objScale);
			if(zhuoheyi!=null)//绘制第一排
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(0,0,-1200);//第二排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(0,0,-1200);//第三排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(0,0,-1200);//第四排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(-2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(0,0,-1200);//第五排
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.translate(2000,0,0);
			if(zhuoheyi!=null)
			{
				zhuoheyi.drawSelfTwo(textureId);
			}
			MatrixState.popMatrix();//恢复现场
		}
		public void onDrawFrame(GL10 gl)
		{
			generateTextImage();//通过绘制产生矩形纹理
			drawBlur();//绘制真实场景
		}
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			SCREEN_WIDTH=width;
			SCREEN_HEIGHT=height;
			Constant.screen_width=width;
			Constant.screen_height=height;
			ratio = (float) width / height;//计算GLSurfaceView的宽高比
			initFRBuffers();//初始化帧缓冲和渲染缓冲的方法
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
			//加载纹理
			textureSky=initTexture(MySurfaceView.this.getResources(),"sky_cloud.png");
			texheiban=initTexture(MySurfaceView.this.getResources(),"heiban.jpg");
			texdiban=initTexture(MySurfaceView.this.getResources(),"diban.jpg");
			texfangding=initTexture(MySurfaceView.this.getResources(),"fangding.jpg");
			texchuanghu=initTexture(MySurfaceView.this.getResources(),"chuanghu.png");
			textWhiteBan=initTexture(MySurfaceView.this.getResources(),"whiteban.jpg");
			texthongqi=initTexture(MySurfaceView.this.getResources(),"guoqi.jpg");
			texchair=initTexture(MySurfaceView.this.getResources(),"chair.jpg");
			texbeizi=initTexture(MySurfaceView.this.getResources(),"beizi.jpg");
			sky=new Sky_cloud(MySurfaceView.this);//创建天空穹
			//加载模型
			diban=LoadUtil.loadFromFile("obj/diban.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			fangding=LoadUtil.loadFromFileMian("obj/fangdingtest.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			qiang1=LoadUtil.loadFromFileMian("obj/qiang1.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			chuanghu=LoadUtil.loadFromFile("obj/chuanghu.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			men=LoadUtil.loadFromFileMian("obj/men.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			menkuang=LoadUtil.loadFromFile("obj/menkuang.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			huangzhu=LoadUtil.loadFromFile("obj/huangzhu.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			baizhu=LoadUtil.loadFromFile("obj/baizhu.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			jiangtai=LoadUtil.loadFromFile("obj/jiangtai.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			heiban=LoadUtil.loadFromFile("obj/heiban.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			hongqi=LoadUtil.loadFromFile("obj/hongqi.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			jiangzhuo=LoadUtil.loadFromFile("obj/jiangzhuotest.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			baiban=LoadUtil.loadFromFile("obj/baiban.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			zhuoheyi=LoadUtil.loadFromFile("obj/zhuoheyi.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			beizi=LoadUtil.loadFromFile("obj/beizi.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			beiziba=LoadUtil.loadFromFile("obj/beiziba.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
			beizigai=LoadUtil.loadFromFile("obj/hugai.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
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
