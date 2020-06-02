package wyf.lxg.fishfood;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.load.LoadedObjectVertexNormalTexture;
import wyf.lxg.mywallpaper.MySurfaceView;

public class SingleFood {
	public FoodThread Ft;
	public AttractThread At;
	public MySurfaceView mv;
	// 把Y和Z定义到常量类里面
	public float Ypositon =Constant.FoodPositionMax_Y;
	LoadedObjectVertexNormalTexture fishFoods;
	int texld;
	// 创建鱼食的对象
	public SingleFood(int texld,LoadedObjectVertexNormalTexture fishfoods, MySurfaceView mv) {
		this.texld=texld;
		this.mv = mv;
		//start=true;
		// 食物
		fishFoods = fishfoods;
		// 动态改变Y的位置和X的位置X的位置主要是让食物抖动Tread里面有一个标准位，用于标准是否开始线程的标志，当次标志位位true
		Ft = new FoodThread(this);
		At = new AttractThread(this);

	}
	public void StartFeed()
	{
		Ft.start();//启动鱼食移动线程
		At.start();//启动吸引力线程
	}
	// 在绘制的标志位位true的时候开始绘制
	public void drawSelf()
	{
		MatrixState.pushMatrix();//保护矩阵
		MatrixState.translate(mv.Xposition,this.Ypositon,mv.Zposition);//平移
		fishFoods.drawSelf(texld);//绘制鱼食
		MatrixState.popMatrix();//恢复矩阵
	}

}