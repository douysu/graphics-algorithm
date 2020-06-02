package wyf.lxg.particle;

import android.opengl.GLES30;

public class ParticleDataConstant
{
	//资源锁
	public static Object lock=new Object();

	public static float  RADIS=60*0.4f;//粒子的半径   6000 200
	public static int Count=8200;//粒子的数量
	public static final int GROUP_COUNT=200;//每次喷发的数量
	public static  float[] START_COLOR= {1.0f,0.9843f,0.7373f,0.2f};//淡黄

	public static float[] END_COLOR= {0.0f,0.0f,0.0f,0.0f};//终止颜色

	public static int SRC_BLEND= GLES30.GL_SRC_ALPHA;//源混合因子
	public static int DST_BLEND= GLES30.GL_ONE;//目标混合因子
	public static int BLEND_FUNC= GLES30.GL_FUNC_ADD;//混合方式

	public static final float MAX_LIFE_SPAN= 5.0f;//粒子最大生命期
	public static final float LIFE_SPAN_STEP= 0.08f;//粒子生命周期步进


	public static final int THREAD_SLEEP=134;//粒子更新物理线程休息时间
	public static final float VY=0.05f;//粒子Y方向升腾的速度

}
