package wyf.lxg.fishschool;

import android.util.Log;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.fishschool.FishSchoolControl;

//定时运动所有鱼类的线程
public class SixFishschoolThread extends Thread {
    boolean flag = true;
    SixFishSchoolControl fishschools;
    float Length;

    public SixFishschoolThread(SixFishSchoolControl fishschools) {
        this.fishschools = fishschools;
    }
    public void run() {
        while (flag) {// 循环定时移动鱼类
            try {
                //设置作用点
                Vector3f Vwall = null;
                // 第零条鱼的当前位置
                float Cx = fishschools.fishSchool.get(0).position.x;
                float Cy = fishschools.fishSchool.get(0).position.y;
                float Cz = fishschools.fishSchool.get(0).position.z;
                //鱼群里面3条能动的鱼
                int j=1;
                //设置力的作用点  设置1~4条鱼
                for(int i=-90;i<=180.;i=i+90)
                {
                    fishschools.fishSchool.get(j).ConstantPosition.x = (float) (Cx+Constant.Radius*Math.cos(i));
                    fishschools.fishSchool.get(j).ConstantPosition.y = Cy;
                    fishschools.fishSchool.get(j).ConstantPosition.z = (float) (Cz+Constant.Radius*Math.sin(i));
                    j++;
                }
                //设置5以后的鱼
                j=5;
                for(int i=-90;i<=0;i=i+90)
                {
                    fishschools.fishSchool.get(j).ConstantPosition.x = (float) (Cx+Constant.Radius2*Math.cos(i));
                    fishschools.fishSchool.get(j).ConstantPosition.y = Cy;
                    fishschools.fishSchool.get(j).ConstantPosition.z = (float) (Cz+Constant.Radius2*Math.sin(i));
                    j++;
                }
                // 每条鱼受到从当前位置指向应该所在位置(Home)的力(恒力作用）,
                for (int i = 1; i < fishschools.fishSchool.size(); i++)
                {
                    // 计算恒力的中间变量
                    Vector3f VL = null;
                    VL = fishschools.fishSchool.get(i).ConstantPosition
                            .cutGetforce(fishschools.fishSchool.get(i).position);
                    //得到从Position到ConstantPosition的的向量长度
                    Length = VL.Vectormodule();
                    //更改力的大小
                    if ((Length) >= Constant.SMinDistaces)
                    {
                        VL.getforce(Constant.ConstantForceScals/2.0f);//25
                    }
                    else if (Length<= 0.8)// 距离小于没某一个值时不产生力的作用，从而解决鱼的抖动问题
                    {
                        VL.x = VL.y = VL.z = 0;
                    }
                    else
                    {
                        VL.getforce(Constant.ConstantForceScals);//200
                    }
                    //计算外力
                    //其他鱼对鱼群没有有影响后，鱼会游回原来的位置
                    float MediaLength = fishschools.fishSchool.get(i).force
                            .Vectormodule();
                    // 如果外力存在则把横恒力置为0，只受外力的作用，只有在外力消失的情况下恒力又产生了，从而鱼儿又游向ConstantPosition的位置
                    if (Math.abs(MediaLength) == 0) {
                        // 把计算得到的恒力赋给恒力ConstantForce.
                        fishschools.fishSchool.get(i).ConstantForce.x = VL.x;//恒力产生
                        fishschools.fishSchool.get(i).ConstantForce.y = VL.y;
                        fishschools.fishSchool.get(i).ConstantForce.z = VL.z;
                    } else {
                        //恒力改为0
                        fishschools.fishSchool.get(i).ConstantForce.x = 0;
                        fishschools.fishSchool.get(i).ConstantForce.y = 0;
                        fishschools.fishSchool.get(i).ConstantForce.z = 0;
                    }
                }
                // 判断鱼和墙壁的碰撞，如果碰到了，就在force的X轴方向上产生一个与其相反的力
                Vwall = new Vector3f(0, 0, 0);
                // 碰壁检测时，当接近地面和近平面时力要大写避免鱼出范围，产生穿地而过的视觉效果，左右和上面可以大写，理解为游出来了人的视角。
                //给的力大体上一样，但是尽量不要完全相同防止鱼在某一方向上的速度变为零（在旋转上，已经对有零的效果进行了处理）
                if (fishschools.fishSchool.get(0).position.x <= -8.5f) {
                    Vwall.x = 0.0013215f;
                }
                if (fishschools.fishSchool.get(0).position.x >4.5f) {
                    Vwall.x = -0.0013212f;
                }
                if (fishschools.fishSchool.get(0).position.y >= 7) {
                    Vwall.y = -0.0013213f;
                }
                if (fishschools.fishSchool.get(0).position.y <= -5f) {
                    Vwall.y = 0.002214f;
                }
                if (fishschools.fishSchool.get(0).position.z < -15) {
                    Vwall.z = 0.0014214f;
                }
                if (fishschools.fishSchool.get(0).position.z > 3) {
                    Vwall.z = -0.002213f;
                }
                fishschools.fishSchool.get(0).force.plus(Vwall);
                // }

                // 定时修改鱼的速度和位-*-移.'/
                for (int i = 0; i < fishschools.fishSchool.size(); i++) {
                    fishschools.fishSchool.get(i).fishschoolMove();
                }
                // 碰壁检测！只让中间的那条鱼受力，检测的范围大一点，当中间的鱼受力时，中间鱼位移和速度回改变，同时其他鱼的恒力也会改变，进而达到同时拐弯的效果
                // 并且距离的影响效果大一些
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
