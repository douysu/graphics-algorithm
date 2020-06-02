package wyf.lxg.fish;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.fish.FishControl;


//定时运动所有鱼类的线程
public class FishGoThread extends Thread {
	boolean flag = true;
	FishControl fishControl;

	public FishGoThread(FishControl fishGroupforcontrol) {
		this.fishControl = fishGroupforcontrol;
	}

	public void run() {
		while (flag) {// 循环定时移动鱼类
			try {
				// 动态的修改鱼受到的力的大小
				for (int i = 0; i < fishControl.fishAl.size(); i++) {
						// 计算鱼群对该鱼产生的力的大小
						Vector3f Vwall = null;

						// 一轮for循环之后第i条鱼所受到的单个鱼的合力计算出来
						inside:for(int j = 0; j < fishControl.fishAl.size(); j++){
						Vector3f V3 = null;
						if (i == j) {//自己与自己不再比较
							continue inside;
						}

						//当前和另一条鱼进行向量减法拿到力的改变方向并且力的大小和距离成反比.
							//通过该方法得到了两条鱼之间的作用力
						V3 = fishControl.fishAl.get(i).position.cut(
								fishControl.fishAl.get(j).position,
								Constant.MinDistances);
							//根据鱼的质量在对鱼的作用力在进行缩放
						V3.getforce(fishControl.fishAl.get(i).weight);
						// 两条鱼之间的力
							//鱼身上所受的力加上两条鱼之间的作用力
						fishControl.fishAl.get(i).force.plus(V3);
					}
					// 计算鱼群对每条单个鱼(第i条鱼)的作用力!
					// 当前和零号鱼进行向量减法拿到力的改变方向并且力的大小和距离成反比
					if (fishControl.My.fishSchool != null
							&& fishControl.My.fishSchool.fishSchool.size() != 0) {
						Vector3f V4 = fishControl.fishAl.get(i).position
								.cut(fishControl.My.fishSchool.fishSchool
										.get(0).position, Constant.MinDistances);
						V4.getforce(fishControl.fishAl.get(i).weight);
						// 两条鱼之间的力
						fishControl.fishAl.get(i).force.plus(V4);
					}
					// 判断鱼和墙壁的碰撞，如果碰到了，就在force的X轴方向上产生一个与其相反的力
					Vwall = new Vector3f(0, 0, 0);
					// 碰壁检测时，当接近地面和近平面时力要大写避免鱼出范围，产生穿地而过的视觉效果，左右和上面可以大写，理解为游出来了人的视角。
					if (fishControl.fishAl.get(i).position.x <= -8.5f) {
						Vwall.x = 0.0013215f;
					}
					if (fishControl.fishAl.get(i).position.x > 4.5f) {
						Vwall.x = -0.0013212f;
					}
					if (fishControl.fishAl.get(i).position.y >= 4f) {
						Vwall.y = -0.0013213f;
					}
					if (fishControl.fishAl.get(i).position.y <= -3) {
						Vwall.y = 0.002214f;
						if(fishControl.fishAl.get(i).position.y <= -4)
						{
							Vwall.y =0.006428f;
						}
					}
					if (fishControl.fishAl.get(i).position.z < -20f) {
						Vwall.z = 0.0014214f;
					}
					if (fishControl.fishAl.get(i).position.z > 2) {
						Vwall.z = -0.002213f;
					}
					Vwall.y -= 0.000009;
					fishControl.fishAl.get(i).force.plus(Vwall);
				}
				// 定时修改鱼的速度和位-*-移.'/
				for (int i = 0; i < fishControl.fishAl.size(); i++) {
					fishControl.fishAl.get(i).fishMove();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
