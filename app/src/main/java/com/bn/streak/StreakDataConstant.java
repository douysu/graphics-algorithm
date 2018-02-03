package com.bn.streak;

import android.opengl.GLES30;

/**
 * Created by Administrator on 2017/8/29.
 */

public class StreakDataConstant {
    public static Object lock=new Object(); //资源锁
    public static float Stroke=0.06f;//条带的宽度
    public static int StreakNum=30*2;//拖尾的最大长度(必须是2的倍数)
    public static int threadTime=10;//拖尾的消失时间(手指离开后线程休息时间)
    public static float[] lineColor={1.0f,1.0f,0.0f,1.0f};//拖尾的颜色

    public static final float MAX_LIFE_SPAN= 1.5f; //最大生命周期
    public static final float LIFE_SPAN_STEP= 0.05f;//生命周期步进

    public static int SRC_BLEND= GLES30.GL_SRC_ALPHA;//源混合因子
    public static int DST_BLEND= GLES30.GL_ONE;//目标混合因子(得到背景全部颜色)
    public static int BLEND_FUNC= GLES30.GL_FUNC_ADD;//混合方式

}
