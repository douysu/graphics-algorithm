package wyf.lxg.fishfood;

import java.util.ArrayList;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.fish.SingleFish;

//定时运动所有鱼类的线程
public class AttractThread extends Thread {
	// 线程的标志位
	public boolean Feeding = true;
	// 是否遍历单个鱼的的列表，得到产生力的的鱼的列表
	public boolean Fforcefish = true;
	public boolean Go = false;
	float Length;
	// TDRender的引用
	SingleFood Sf;
	// 鱼类的受力列表
	ArrayList<SingleFish> fl = new ArrayList<SingleFish>();

	public AttractThread(SingleFood sf) {
		this.Sf = sf;
	}
	// 记得诱惑力每次都变成空
	public void run() {
		while (Feeding) {
			try {
				// 添加能被看到食物的鱼类列表
				if (Go) {
					if (Fforcefish) {
						fl.clear();
						Fforcefish = false;
					}
//						// 注意错误
					if (fl != null ) {
						// 遍历鱼类的列表满足条件的放到fl里面，里面的鱼开始收到力的作用!
						for (int i = 0; i < Sf.mv.fishAl.size(); i++) {
							if (Sf.mv.fishAl.get(i).position.x > Sf.mv.Xposition
									&& Sf.mv.fishAl.get(i).speed.x < 0) {

								if (!fl.contains(Sf.mv.fishAl.get(i))) {
									fl.add(Sf.mv.fishAl.get(i));
								}
							}
							else if (Sf.mv.fishAl.get(i).position.x < Sf.mv.Xposition
									&& Sf.mv.fishAl.get(i).speed.x > 0) {
								if (!fl.contains(Sf.mv.fishAl.get(i))) {
									fl.add(Sf.mv.fishAl.get(i));
								}
							}
						}
					}
					// 给能看到食物的鱼加力的作用
					if (fl.size() != 0) {
						for (int i = 0; i < fl.size(); i++) {
							// 计算诱惑力的中间变量
							Vector3f VL = null;
							Vector3f Vl2 = null;
							Vl2 = new Vector3f(Sf.mv.Xposition,
									Sf.mv.singlefood.Ypositon, Sf.mv.Zposition);
							VL = Vl2.cutPc(fl.get(i).position);
							// 得到从Position到ConstantPosition的的向量长度如果这个长度小于阈值的时候就认为鱼食被吃掉吧所有的与之相关的线程的标志位置为false，并把受力的鱼的列表
							// 置空
							Length = VL.Vectormodule();
							if (Length != 0) {
								VL.ChangeStep(Length);
							}
							//鱼食被吃掉或者鱼食超过了阈值
							if (Length <= Constant.FoodFeedDistance || Sf.Ypositon < Constant.FoodPositionMin_Y) {
								StopAllThread();
							}
							// 诱惑力的比例
							VL.getforce(Constant.AttractForceScals);
							// 把计算得到的诱惑力赋给诱惑恒力ConstantForce!

							fl.get(i).attractforce.x = VL.x;
							fl.get(i).attractforce.y = VL.y;
							fl.get(i).attractforce.z = VL.z;
						}
					}
				}
				if (Sf.Ypositon < Constant.FoodPositionMin_Y) {
					StopAllThread();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void StopAllThread() {
		// 当食物到达一定位置的时候或者食物被鱼吃掉之后，线程的flag重置为false,并且受到力的鱼类列表变为null这个判断在其他地方执行，在鱼的受力列表的给鱼sh
		// 施加力的时候不断的判断，要是满足一定条件就把瘦到诱惑力的鱼的列表搞成空。把所有的标志位都变车false
		// 判断分两个条件第一食物的Y到达某个位置，或者鱼和实物之间的距离很小时，默认为实物被吃掉了，把所有的标志位都变成false并且把forceCondition.fl列表置成空
		// 注意清空!

		// 重置SingleY
		Sf.Ypositon = Constant.FoodPositionMax_Y;

		// On_TouchEvent里面的标志位变为true，使得第二次能点击
		this.Fforcefish = true;
		this.Go = false;
		Sf.Ft.Go = false;
		Constant.isFeed = true;
		// 绘制的标志位!
		Sf.mv.Fooddraw = false;
		// 喂食时标志位false，停止喂食的时后在搞回来

	}
}
