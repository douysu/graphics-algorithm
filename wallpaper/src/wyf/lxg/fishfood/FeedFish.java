package wyf.lxg.fishfood;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.mywallpaper.MySurfaceView;


public class FeedFish {
	MySurfaceView Tr;
	boolean start;

	public FeedFish(MySurfaceView tr) {
		start = true;
		this.Tr = tr;
	}
	public void startFeed(Vector3f Start,Vector3f End) {


		// 喂食的位置
		Vector3f dv=End.cutPc(Start);
		//根据地面的高度为Constant.Y_HEIGHT算出t值
		//float t=(Constant.Y_HEIGHT -Start.y)/dv.y;
		//根据t计算出交点的X、Y坐标值
		float t=(Constant.Y_HEIGHT -Start.y)/dv.y;
		//根据t计算出交点的X、Y坐标值
		float xd=Start.x+t*dv.x;
		float zd=Start.z+t*dv.z;

		//超出一定范围鱼食的大小不改变，并且位置不改变，同时食物就不重置了
		if(zd<=Constant.ZTouch_Min ||zd>Constant.ZTouch_Max)
		{
			Constant.isFeed=true;
			return;
		}
		//食物的位置
		Tr.Xposition = xd;
		Tr.Zposition = zd;


		// //绘制食物的标志位
		Tr.Fooddraw = true;
		// 同时把重置Yposition的标志位变为true
		Tr.singlefood.Ft.Fresit = true;
		//将吸引力线程的添加看到鱼食的标志位设为True
		Tr.singlefood.At.Go = true;
		//将喂食线程
		Tr.singlefood.Ft.Go = true;
		// 调用此方法开始移动食物的方法

		if (start) {

			Tr.singlefood.StartFeed();
			start = false;
		}
	}

}