package wyf.lxg.fish;

import java.util.ArrayList;

import wyf.lxg.Constant.MatrixState;
import wyf.lxg.mywallpaper.MySurfaceView;



//鱼的控制类
public class FishControl {
	//鱼群列表
	public ArrayList<SingleFish> fishAl;
	//鱼Go线程
	FishGoThread  fgt;
	//渲染器
	public MySurfaceView My;
	//构造器
	public FishControl(ArrayList<SingleFish> fishAl,MySurfaceView my)
	{
		this.fishAl = fishAl;
		this.My=my;
		//启动鱼的移动线程
		fgt= new FishGoThread(this);
		fgt.start();
	}
	public void drawSelf()
	{
		try {
			//循环绘制每一条鱼
			for(int i=0;i<this.fishAl.size();i++)
			{
				MatrixState.pushMatrix();//保护矩阵
				fishAl.get(i).drawSelf();//绘制鱼
				MatrixState.popMatrix();//恢复矩阵
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}