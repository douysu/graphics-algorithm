package com.bn.streak;

import com.bn.Constant.Constant;

import static com.bn.Constant.Constant.THREAD_END;
import static com.bn.Constant.Constant.THREAD_START;

/**
 * Created by Administrator on 2017/8/31.
 */

public class StreakThread extends Thread {
    boolean flag = true;//标志位
    public int  isRun= THREAD_END;//0为false 1为true

    StreakSystem streakSystem;//获得拖尾主要类
    float maxLifeSpan;//最大生命周期
    float lifeSpanStep;//生命周期的步进

    public StreakThread(StreakSystem streakSystem) {
        this.streakSystem = streakSystem;
        this.maxLifeSpan = StreakDataConstant.MAX_LIFE_SPAN;
        this.lifeSpanStep = StreakDataConstant.LIFE_SPAN_STEP;
    }
    @Override
    public void run() {
        while (flag) {
            try {
               if(isRun==THREAD_START){//线程启动
                   if(streakSystem.lsPoints.size()!=0){//防止空指针
                       for(int i=0;i<streakSystem.lsPoints.size();i++){//递减生命周期
                           streakSystem.lsPoints.get(i)[2]-=lifeSpanStep;
                           if(streakSystem.lsPoints.get(i)[2]<=0){
                               streakSystem.lsPoints.remove(i);//移除列表
                           }
                       }
                       streakSystem.update();//更新位置
                   }
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(StreakDataConstant.threadTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
