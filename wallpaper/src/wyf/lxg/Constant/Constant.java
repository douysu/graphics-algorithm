package wyf.lxg.Constant;

public class Constant {
	// 屏幕的长
	public static float SCREEN_HEGHT=1280;
	// 屏幕的宽
	public static float SCREEN_WIDTH=720;
	// 屏幕的长宽比例
	public static float leftABS;
	public static float topABS;
	public static float UNIT_SIZE=1.0f;

	public static float nearABS =2.1f ;
	public static float farABS = 100.0f;
	//视角的缩放比
	public static float View_SCALE = 0.6f;
	//背景图的缩放比
	public static float SCREEN_SCALEX=1.0f;
	public static float backgroundScaleX=18.0f;
	public static float backgroundScaleY=18.0f;
	//摄像机的位置
	public static float CameraX = 0;
	public static float CameraY = 5.0f;
	public static float CameraZ = 23.0f;
	public static Object lockCamera=new Object();
	//摄像机的Up向量
	public static float UpX = 0.0f;
	public static float UpY = 0.9746f;//(float) 23/ (float) Math.sqrt(554)23.5372
	public static float UpZ = -0.2124f;//(float) 5/ (float) Math.sqrt(554)23.5372
	//摄像机移动的缩放比
	public static float CameraMove_SCALE = 120;
	//摄像机的最大移动距离
	public static float MaxCameraMove = 4;
	//摄像机观测的位置
	public static float TargetX = 0;
	public static float TargetY = 0f;
	public static float TargetZ = 0;
	//地面的高度
	public static float Y_HEIGHT =  -6;
	//Z轴的触控范围
	public static float ZTouch_Max =  -1;
	public static float ZTouch_Min =  -9;
	//鱼食在X轴的晃动距离
	public static float FoodMove_X = 0.01f;
	//鱼食每次下落的距离
	public static float FoodSpeed = 0.1f;
	//鱼吃到鱼食的判定范围
	public static float FoodFeedDistance = 0.5f;
	// 鱼食在Y轴方向上的最小距离
	public static float FoodPositionMin_Y =-6f;
	// 食物的初始Yposition  食物在Y轴的最大距离
	public static float FoodPositionMax_Y = 8f;
	// 气泡的数量
	public static int BUBBLE_NUM = 24;
	//气泡每次移动的距离
	public static float BubbleMoveDistance = 0.005f;
	// 鱼类速度变化最大值用它除以位移能得到没个方向上的力的变化
	public static float MaxChangeSpeed = 0.01f;
	// 两条鱼之间的碰撞距离  小于这个距离之后将产生力的作用
	public static float MinDistances = 5.0f;
	// 单条鱼与鱼群之间的碰撞距离  小于这个距离之后产生力的作用
	public static float SMinDistaces =2.0f;
	// 鱼群的半径
	public static float Radius = 0.5f;
	//大圈鱼群半径
	public static float Radius2=0.8f;
	// 鱼类的初始位置向量
	public static Vector3f initialize = new Vector3f(1, 0, 0);//鱼的初始方向
	public static Vector3f initializeschool = new Vector3f(1, 0, 0);//鱼群的初始方向
	// 恒力的缩放比例
	public static float ConstantForceScals = 200;
	// 鱼群和单个鱼之间的缩放比
	public static float WeightScals = 11;
	//触控阈值
	public static float Thold=10;
	// 诱惑力的缩放比例
	public static float AttractForceScals = 20.1f;
	// 触控是否
	public static boolean isFeed = true;
	//速度的阈值
	public static float MaxSpeed=0.05f;
	//鱼群里面的鱼的最大游动速度
	public static float SchoolMaxSpeed=0.05f;
	//喂食
	public static boolean feeding=false;
	//鱼的缩放
	public static float SchooleScaleNum=0.0028f;//紫色鱼鱼群缩放系数
	public static float SchooleScaleNum1=0.0023f;//橙色鱼群
	public static float SchooleScaleNum2=0.0035f;//红色鱼鱼群
	public static float SchooleScaleNum3=0.0025f;//蓝色白肚子鱼鱼群
	public static float SchooleScaleNum4=0.0025f;//小丑鱼鱼群
	public static float SchooleScaleNum5=0.0023f;//紫色鱼鱼群缩放系数

	//淡蓝鱼绘制时的缩放比例
	public static float DanScaleNum=0.0043f;
	//鳐鱼绘制时的缩放比例
	public static float YaoScaleNum=0.01f;
	//海龟的缩放比例
	public static float HaiGuiNum=0.012f;
	//珍珠的缩放倍数
	public static float zhenZhuScaleNum=0.560f;
	//粒子系统的xyz坐标
	public static float particleX=-1.05f;//x
	public static float particleY=-8.6f;//y
	public static float particleZ=-9.0f;//z
	public static float objectR=0.38416f;//物体的半径
	public static float particleR=1f;//光圈的半径
}
