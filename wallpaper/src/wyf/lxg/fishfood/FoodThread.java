package wyf.lxg.fishfood;

import wyf.lxg.Constant.Constant;

//定时运动实物的线程
public class FoodThread extends Thread {
	//线程的标志位
	public  boolean flag1 = true;
	//SingleFood的Y和Z是否重置的标志位位置重置标志位
	public boolean Fresit=true;
	//移动x方向的标志位
	boolean FxMove=true;
	//线程里面的算法是否走的标志位
	public boolean Go=false;
	public  SingleFood SingleF;
	public FoodThread(SingleFood singleF)
	{
		this.SingleF=singleF;
	}
	public void run()
	{
		while (flag1) {
			try
			{
				//如果标志位为true
				if(Go)
				{
					//修改SingleFood的xyz位置产生实物晃动的效果
					if(FxMove)
					{
						SingleF.mv.Xposition+=Constant.FoodMove_X;
						FxMove=!FxMove;
					}
					else
					{
						SingleF.mv.Xposition-=Constant.FoodMove_X;
						FxMove=!FxMove;
					}
					//定时的修改Y坐标！食物是有下落速度的
					SingleF.Ypositon-=Constant.FoodSpeed;
				}
			}

			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}
}
