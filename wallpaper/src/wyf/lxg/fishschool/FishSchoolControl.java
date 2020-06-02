package wyf.lxg.fishschool;

import java.util.ArrayList;
import com.bn.fbx.core.BNModel;
import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.mywallpaper.MySurfaceView;

public class FishSchoolControl {
	public ArrayList<SingleFishSchool> fishSchool = new ArrayList<SingleFishSchool>();//鱼群列表
	public MySurfaceView Tr;
	public FishschoolThread Thread;
	float scaleNum;//缩小的倍数
	int texid;
	float x;
	public FishSchoolControl(BNModel md,int texid, MySurfaceView tr,Vector3f weizhi,Vector3f sudu,float weight,float scaleNum) {
		this.Tr = tr;
		this.texid=texid;
		this.scaleNum=scaleNum;
		//产生偏移的感觉，不是同一个动作
		if(sudu.x>0)//根据第一条鱼的速度计算后面三条鱼的速度
		{
			x=sudu.x;
		}else
		{
			x=sudu.x;
		}
		// 添加鱼类!//位置    速度     方向(力)     外力      重力
		fishSchool.add(new SingleFishSchool(md,this.texid,
				weizhi, sudu,//第一条鱼
				new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), weight,scaleNum));
		fishSchool.add(new SingleFishSchool(md,this.texid,
				new Vector3f(weizhi.x, weizhi.y, weizhi.z-Constant.Radius), new Vector3f(x,
				0.00f, x), new Vector3f(0, 0, 0), new Vector3f(0,
				0, 0), weight,scaleNum));//第二条鱼

		fishSchool.add(new SingleFishSchool(md,this.texid,
				new Vector3f(weizhi.x+Constant.Radius, weizhi.y, weizhi.z), new Vector3f(x,
				0.00f, x), new Vector3f(0, 0, 0), new Vector3f(0,
				0, 0), weight,scaleNum));//第三条鱼

		fishSchool.add(new SingleFishSchool(md,this.texid,
				new Vector3f(weizhi.x, weizhi.y, weizhi.z+Constant.Radius), new Vector3f(x,
				0.00f, x), new Vector3f(0, 0, 0), new Vector3f(0,
				0, 0), weight,scaleNum));//第四条鱼
		Thread = new FishschoolThread(this);
		Thread.start();//启动线程
	}
	public void drawSelf() {
		// 鱼的绘制
		try {
			for (int i = 0; i < this.fishSchool.size(); i++)
			{
				MatrixState.pushMatrix();//保护矩阵
				fishSchool.get(i).drawSelf();//绘制鱼群
				MatrixState.popMatrix();//恢复矩阵
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
