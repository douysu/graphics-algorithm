package wyf.lxg.fishschool;

import com.bn.fbx.core.BNModel;

import java.util.ArrayList;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.mywallpaper.MySurfaceView;

/**
 * Created by Administrator on 2017/7/20.
 */

public class SixFishSchoolControl {
    public ArrayList<SingleFishSchool> fishSchool = new ArrayList<SingleFishSchool>();//鱼群列表
    public MySurfaceView Tr;
    public SixFishschoolThread Thread;
    float scaleNum;//缩小的倍数
    int texid;
    float x;
    public SixFishSchoolControl(BNModel md, int texid, MySurfaceView tr, Vector3f weizhi, Vector3f sudu, float weight, float scaleNum) {
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
        /*鱼的位置
            5
            1
        4   0   2 6
            3
        */
        fishSchool.add(new SingleFishSchool(md,this.texid,
                weizhi, sudu,
                new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), weight,scaleNum));//第一条鱼


        fishSchool.add(new SingleFishSchool(md,this.texid,
                new Vector3f(weizhi.x, weizhi.y, weizhi.z- Constant.Radius), new Vector3f(x,
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
        fishSchool.add(new SingleFishSchool(md,this.texid,
                new Vector3f(weizhi.x- Constant.Radius, weizhi.y, weizhi.z), new Vector3f(x,
                0.00f, x), new Vector3f(0, 0, 0), new Vector3f(0,
                0, 0), weight,scaleNum));//第五条鱼


        fishSchool.add(new SingleFishSchool(md,this.texid,
        new Vector3f(weizhi.x, weizhi.y, weizhi.z- Constant.Radius2), new Vector3f(x,
                0.00f, x), new Vector3f(0, 0, 0), new Vector3f(0,
                0, 0), weight,scaleNum));//第六
        fishSchool.add(new SingleFishSchool(md,this.texid,
                new Vector3f(weizhi.x+Constant.Radius2, weizhi.y, weizhi.z), new Vector3f(x,
                0.00f, x), new Vector3f(0, 0, 0), new Vector3f(0,
                0, 0), weight,scaleNum));//第7
        Thread = new SixFishschoolThread(this);
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
