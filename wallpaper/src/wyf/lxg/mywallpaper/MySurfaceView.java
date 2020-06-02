package wyf.lxg.mywallpaper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLES30;
import android.os.Build;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.bn.fbx.core.BNModel;
import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.background.background;
import wyf.lxg.beike.AllBeiKe;
import wyf.lxg.beike.SingleZhenZhu;
import wyf.lxg.bubble.BubbleControl;
import wyf.lxg.fish.FishControl;
import wyf.lxg.fish.SingleFish;
import wyf.lxg.fishfood.FeedFish;
import wyf.lxg.fishfood.SingleFood;
import wyf.lxg.fishschool.FishSchoolControl;
import wyf.lxg.fishschool.SixFishSchoolControl;
import wyf.lxg.load.LoadUtil;
import wyf.lxg.load.LoadedObjectVertexNormalTexture;
import wyf.lxg.particle.ParticleForDraw;
import wyf.lxg.particle.ParticleSystem;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static wyf.lxg.Constant.Constant.backgroundScaleX;
import static wyf.lxg.Constant.Constant.backgroundScaleY;
import static wyf.lxg.Constant.Constant.objectR;
import static wyf.lxg.Constant.Constant.particleR;
import static wyf.lxg.Constant.Constant.particleX;
import static wyf.lxg.Constant.Constant.particleY;
import static wyf.lxg.Constant.Constant.particleZ;
import static wyf.lxg.particle.ParticleDataConstant.Count;
import static wyf.lxg.particle.ParticleDataConstant.RADIS;


public class MySurfaceView  extends GLSurfaceView
		implements GLSurfaceView.Renderer,OpenGLES3WallpaperService.Renderer
{
	public MySurfaceView(Context context) {
		super(context);
		this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
	}
	public SingleFood singlefood;//鱼食
	public static AllBeiKe singlebeike;//贝壳
	public boolean Fooddraw = false;//是否喂鱼
	static FeedFish feedfish;//喂鱼
	boolean threadFlag=true;
	public float Zposition=0;//鱼食的Z位置
	public float Xposition=0;//鱼食的X位置
	public FishControl fishControl;//群鱼控制类
	public ArrayList<SingleFish> fishAl=new ArrayList<SingleFish>();//群鱼列表
	// 鱼群
	public FishSchoolControl fishSchool;
	public SixFishSchoolControl fishSchool1;
	public SixFishSchoolControl fishSchool2;
	public FishSchoolControl fishSchool3;
	public FishSchoolControl fishSchool4;//小丑鱼鱼群
	public FishSchoolControl fishSchool5;//紫色鱼群
	int back;//背景图纹理id
	int bubbles;//气球纹理ID
	int fishfood;//鱼食纹理id
	int dpm;//明暗效果图纹理ID
	int beike;//贝壳纹理ID
	int zhenzhu;//珍珠纹理ID
	int textureIdFire;//火焰的纹理
	//鱼的模型
	BNModel bnm1;
	BNModel bnm2;//红色的鱼
	BNModel bnm3;//橙色鱼
	BNModel bnm4;//淡蓝色小鱼
	BNModel bnm5;//蓝色白肚子小鱼
	BNModel bnm6;//小丑鱼
	BNModel bnm7;//紫色鱼
	BNModel bnm8;//海龟

	ParticleForDraw particleForDraw;//粒子系统的绘制者
	ParticleSystem particleSystem;//自创建的粒子系统
	background bg;//纹理矩形
	LoadedObjectVertexNormalTexture fishfoods;//鱼食
	LoadedObjectVertexNormalTexture beikes;//贝壳
	LoadedObjectVertexNormalTexture beikesTop;//上面的贝壳
	SingleZhenZhu zhenzhus;//珍珠
	BubbleControl bubble;//气泡
	BubbleControl bubble1;//气泡
	float time = 0;
	//计算FPS用
	int FPSCount=0;
	long starttime = System.nanoTime();


	@SuppressLint("NewApi")
	public void onDrawFrame(GL10 gl)
	{
		//清除深度缓冲与颜色缓冲
		GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
		MatrixState.pushMatrix();

		MatrixState.pushMatrix();
		MatrixState.translate(0,-7,0);
		MatrixState.scale(backgroundScaleX,backgroundScaleY,1.0f);
		if(bg!=null)
		{
			bg.drawSelf(back); //绘制背景图
		}
		MatrixState.popMatrix();
		if(singlefood!=null)
		{
			singlefood.drawSelf();//绘制鱼食
		}
		if (singlebeike != null) {//贝壳
			singlebeike.drawSelf();
		}

		//绘制三条鱼
		if (fishControl != null) {
			fishControl.drawSelf();//绘制三条鱼
		}
		//鱼群绘制
		if (fishSchool != null) {
			fishSchool.drawSelf();//紫色鱼鱼群
		}
		if (fishSchool1 != null) {
			fishSchool1.drawSelf();//橙色鱼群
		}
		if (fishSchool2 != null) {
			fishSchool2.drawSelf();//红色鱼群
		}
		if (fishSchool3 != null) {
			fishSchool3.drawSelf();//蓝色白肚子小鱼群
		}
		if(fishSchool4 != null) {
			fishSchool4.drawSelf();//小丑鱼
		}
		if(fishSchool5!=null){
			fishSchool5.drawSelf();//紫色鱼
		}
		MatrixState.popMatrix();//恢复矩阵

		GLES30.glEnable(GLES30.GL_BLEND);
		//设置混合因子c
		GLES30.glBlendFunc(GLES30.GL_SRC_COLOR, GLES30.GL_ONE_MINUS_SRC_COLOR);
		//保护现场
		MatrixState.pushMatrix();//保护矩阵
		if(bubble!=null)
		{
			bubble.drawSelf();//绘制气泡
		}
		MatrixState.popMatrix();//恢复矩阵
		GLES30.glDisable(GLES30.GL_BLEND);//关闭混合
		//计算帧数的
		FPSCount++;
		if(FPSCount==100)
		{
			long endtime = System.nanoTime();
			double fps = 1000*1000*1000*100.0/(endtime-starttime);
			FPSCount=0;
			starttime=endtime;
		}
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//设置视窗大小及位置
		GLES30.glViewport(0, 0, width, height);
		//计算GLSurfaceView的宽高比
		float ratio = (float) width / height;
		Constant.SCREEN_HEGHT=height;//获取屏幕高度
		Constant.SCREEN_WIDTH=width;//获取屏幕宽度
		Constant.leftABS=ratio*Constant.View_SCALE;
		Constant.topABS=1 * Constant.View_SCALE;
		Constant.SCREEN_SCALEX=Constant.View_SCALE*((ratio>1)?ratio:(1/ratio));
		// 调用此方法计算产生透视投影矩阵
		MatrixState.setProjectFrustum(-Constant.leftABS, Constant.leftABS, -Constant.topABS,
				Constant.topABS, Constant.nearABS,Constant.farABS);
		//调用此方法产生摄像机9参数位置矩阵
		MatrixState.setCamera(
				Constant.CameraX, // 人眼位置的X
				Constant.CameraY, // 人眼位置的Y
				Constant.CameraZ, // 人眼位置 的Z
				Constant.TargetX, // 人眼球看的点X
				Constant.TargetY, // 人眼球看的点Y
				Constant.TargetZ, // 人眼球看的点Z
				Constant.UpX, //Up向量
				Constant.UpY,
				Constant.UpZ);
	}
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//设置屏幕背景色RGBA
		GLES30.glClearColor(0.5f,0.5f,0.5f, 1.0f);
		MatrixState.setInitStack();//初始化矩阵
		//初始化光源位置
		MatrixState.setLightLocation(0,9,13);
		dpm=initTexture(MySurfaceView.this.getResources(),"dpm.png");//加载明暗效果图
		/*
		school bnm4
		school1 bnm3
		school2 bnm2
		school3 bnm5
		 */
		//原来的鱼
		bnm1=new BNModel("bnggdh/manfish.bnggdh", "pic/manfish.png", true, 0.1f, MySurfaceView.this);//鳗鱼
		bnm2=new BNModel("bnggdh/redrockfishdou.bnggdh", "pic/redrockfish.png", true, 0.05f, MySurfaceView.this);//红色的鱼
		bnm3=new BNModel("bnggdh/dayu.bnggdh", "pic/dayu.png", true, 0.1f, MySurfaceView.this);//橙色鱼
		bnm4=new BNModel("bnggdh/napoleondou.bnggdh", "pic/napoleon.png", true, 0.07f, MySurfaceView.this);//淡蓝色小鱼

		bnm5=new BNModel("bnggdh/bobadou.bnggdh", "pic/boba.png", true, 0.05f, MySurfaceView.this);//蓝色白肚子小鱼
		bnm6=new BNModel("bnggdh/clowngishdou.bnggdh", "pic/clownfish.png", true, 0.05f, MySurfaceView.this);//小丑鱼
		bnm7= new BNModel("bnggdh/piranhadou.bnggdh", "pic/piranha.png", true, 0.05f, MySurfaceView.this);//紫色鱼
		bnm8=new BNModel("bnggdh/tortoisedou.bnggdh", "pic/tortoise.png", true, 0.025f, MySurfaceView.this);//海龟
		if(fishAl.size() == 0)
		{//位置    速度     力     吸引力      重力
			fishAl.add(new SingleFish(bnm1,dpm,
					new Vector3f(-7, 5, -7), new Vector3f(-0.05f, 0.02f, 0.03f),
					new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 800, Constant.YaoScaleNum));//鳐鱼
			fishAl.add(new SingleFish(bnm1,dpm,
					new Vector3f(2, -4, -5), new Vector3f(-0.05f, 0.02f, 0.03f),
					new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 800, Constant.YaoScaleNum));//鳐鱼
			fishAl.add(new SingleFish(bnm4,dpm,
					new Vector3f(-2, 3, -6),
					new Vector3f(-0.05f, 0.0f, 0.03f), new Vector3f(0, 0, 0),
					new Vector3f(0, 0, 0), 80, Constant.DanScaleNum));//淡蓝
			fishAl.add(new SingleFish(bnm8,dpm,
					new Vector3f(3, -2, -5),
					new Vector3f(-0.005f, 0.01f, 0.00f), new Vector3f(0, 0, 0),
					new Vector3f(0, 0, 0), 400, Constant.HaiGuiNum));//海龟
		}
		//加载纹理//初始化纹理
		back=initTexture(MySurfaceView.this.getResources(),"background.jpg");
		fishfood=initTexture(MySurfaceView.this.getResources(),"fishfood.png");
		bubbles=initTexture(MySurfaceView.this.getResources(),"bubble.png");
		beike= initTexture(MySurfaceView.this.getResources(),"beike.png");
		zhenzhu= initTexture(MySurfaceView.this.getResources(),"zhenzhu.png");

		//创建三角形对对象
		bg=new background(MySurfaceView.this);
		//创建圆柱对象
		bubble = new BubbleControl(MySurfaceView.this,bubbles);
		bubble1 = new BubbleControl(MySurfaceView.this,bubbles);
		fishfoods=LoadUtil.loadFromFile("fishfood.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
		singlefood=new SingleFood(fishfood,fishfoods, MySurfaceView.this);

		//贝壳和珍珠
		//创建光圈粒子系统
		particleForDraw=new ParticleForDraw(MySurfaceView.this,RADIS);//实例绘画者
		//位置x、位置y、位置z,圆的半径，粒子带宽度，绘画者、粒子的数量
		particleSystem=new ParticleSystem(particleX,particleY,particleZ,objectR,particleR,particleForDraw,Count);//创建粒子
		textureIdFire=initTexture(MySurfaceView.this.getResources(),"fire.png");//创建纹理
		beikes=LoadUtil.loadFromFile("beike.obj", MySurfaceView.this.getResources(),MySurfaceView.this);//下面的贝壳
		beikesTop=LoadUtil.loadFromFile("beiketop.obj", MySurfaceView.this.getResources(),MySurfaceView.this);//上面的贝壳
		zhenzhus=LoadUtil.loadzhenzhuFromFile("zhenzhu.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
		singlebeike=new AllBeiKe(beike,beikes,beikesTop,zhenzhu, zhenzhus, MySurfaceView.this,particleSystem,textureIdFire);

		feedfish=new FeedFish(MySurfaceView.this);
		//创建对象鱼类的Control对象
		if (fishControl == null) {
			fishControl = new FishControl(fishAl, MySurfaceView.this);
		}
		// // 创建鱼群对象的Control
		//参数鱼的模型，纹理的ID，位置的坐标，速度向量，重力（重量）,缩小的倍数
		if (fishSchool == null) { //紫色鱼群
			fishSchool = new FishSchoolControl(bnm7,dpm,MySurfaceView.this,
					new Vector3f(5, -2, 4),new Vector3f(-0.05f, 0.0f, -0.05f),50,Constant.SchooleScaleNum);
		}
		if (fishSchool1 == null) {//橙色鱼群
			fishSchool1 = new SixFishSchoolControl(bnm3,dpm,MySurfaceView.this,
					new Vector3f(-5, -3, -10),new Vector3f(-0.05f, 0.0f, -0.05f),50,Constant.SchooleScaleNum1);
		}
		if (fishSchool2 == null) {//红色鱼群
			fishSchool2 = new SixFishSchoolControl(bnm2,dpm,MySurfaceView.this,
					//-5 -7 -10
					new Vector3f(-5, 0f, -10),new Vector3f(-0.05f, 0.0f, -0.05f),30,Constant.SchooleScaleNum2);
		}
		if (fishSchool3 == null) {//蓝色白肚子鱼群
			fishSchool3 = new FishSchoolControl(bnm5,dpm,MySurfaceView.this,
					new Vector3f(3.3f, 5.3f, 8f),new Vector3f(-0.05f, 0.0f, -0.05f),30,Constant.SchooleScaleNum3);
		}
		if (fishSchool4 == null) {//小丑鱼鱼群
			fishSchool4 = new FishSchoolControl(bnm6,dpm,MySurfaceView.this,
					new Vector3f(3.3f, 2.0f, 8f),new Vector3f(-0.05f, 0.0f, -0.05f),60,Constant.SchooleScaleNum4);
		}
		if (fishSchool5 == null) { //紫色鱼群
			fishSchool5 = new FishSchoolControl(bnm7,dpm,MySurfaceView.this,
					new Vector3f(5, 4, 4),new Vector3f(0.05f, 0.0f, 0.05f),50,Constant.SchooleScaleNum5);
		}
		//关闭背面剪裁
		GLES30.glDisable(GLES30.GL_CULL_FACE);
		//打开深度检测
		GLES30.glEnable(GLES30.GL_DEPTH_TEST);
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	public int initTexture(Resources res,String pname)//textureId
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