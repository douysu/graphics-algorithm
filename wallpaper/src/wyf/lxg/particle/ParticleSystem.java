package wyf.lxg.particle;//声明包

import android.opengl.GLES30;

import wyf.lxg.Constant.Constant;
import  wyf.lxg.Constant.MatrixState;

import static wyf.lxg.particle.ParticleDataConstant.BLEND_FUNC;
import static wyf.lxg.particle.ParticleDataConstant.DST_BLEND;
import static wyf.lxg.particle.ParticleDataConstant.END_COLOR;
import static wyf.lxg.particle.ParticleDataConstant.GROUP_COUNT;
import static wyf.lxg.particle.ParticleDataConstant.LIFE_SPAN_STEP;
import static wyf.lxg.particle.ParticleDataConstant.MAX_LIFE_SPAN;
import static wyf.lxg.particle.ParticleDataConstant.RADIS;
import static wyf.lxg.particle.ParticleDataConstant.SRC_BLEND;
import static wyf.lxg.particle.ParticleDataConstant.START_COLOR;
import static wyf.lxg.particle.ParticleDataConstant.THREAD_SLEEP;
import static wyf.lxg.particle.ParticleDataConstant.VY;
import static wyf.lxg.particle.ParticleDataConstant.lock;

public class ParticleSystem implements Comparable<ParticleSystem>
{
	//起始颜色
	public float[] startColor;
	//终止颜色
	public float[] endColor;
	//源混合因子
	public int srcBlend;
	//目标混合因子
	public int dstBlend;
	//混合方式
	public int blendFunc;
	//粒子最大生命期
	public float maxLifeSpan;
	//粒子生命期步进
	public float lifeSpanStep;
	//粒子更新线程休眠时间间隔
	public int sleepSpan;
	//每次喷发的例子数量
	public int groupCount;
	//发射的次数
	public int emissionNum;
	//基础发射点
	public float sx;
	public float sy;
	public float sz;
	//绘制位置
	float positionX;
	float positionY;
	float positionZ;
	//粒子发射的速度
	public float vParticle;
	//旋转角度
	float yAngle=0;
	//绘制者
	ParticleForDraw fpfd;
	//工作标志位
	boolean flag=true;

	float halfSize;//最大周期
	float circleR;//空心圆半径
	float width;//粒子带的宽度
	public float[] points;//粒子数（包括顶点的位置等信息）
	/*
	 位置x、位置y、位置z,圆的半径，粒子带宽度，绘画者、粒子的数量
	*/
	public ParticleSystem(float positionx,float positiony,float positionz,float circleR,float width,ParticleForDraw fpfd,int count)
	{
		this.circleR=circleR;//空心圆半径
		this.width=width;
		this.startColor=START_COLOR;//开始颜色
		this.endColor=END_COLOR;//终止的颜色
		this.srcBlend=SRC_BLEND; //原混合因子
		this.dstBlend=DST_BLEND;//目标混合因子
		this.blendFunc=BLEND_FUNC;//混合方式
		this.maxLifeSpan=MAX_LIFE_SPAN;//粒子最大生命周期
		this.lifeSpanStep=LIFE_SPAN_STEP;//粒子生命期步进
		this.groupCount=GROUP_COUNT;//每次喷发的粒子数量
		this.sleepSpan=THREAD_SLEEP;//粒子更新时间间隔
		this.emissionNum=count/groupCount;
		this.sx=positionx;//基础出发点
		this.sy=positiony;
		this.sz=positionz;
		this.positionX=positionx;
		this.positionY=positiony;
		this.positionZ=positionz;
		this.vParticle=VY;
		this.halfSize=RADIS;//粒子系统的半径
		this.fpfd=fpfd;//给绘画者
		this.points=initPoints(count);//初始化粒子顶点数据数组
		/*顶点位置包括，x,y,v,粒子当前生命周期*/
		fpfd.initVertexData(points);//调用初始化顶点坐标的方法
		new Thread()
		{
			public void run()
			{
				while(flag)
				{
					update();
					try
					{
						Thread.sleep(sleepSpan);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	public float[] initPoints(int zcount)//遍历所有的粒子
	{//初始化粒子顶点数据的方法
		float[] points=new float[zcount*4];//临时存放顶点数据的数组-每个粒子对应1个顶点，每个顶点包含4个值
		float bfbWidth=0;//宽度的百分比
		float bfbJia=(float) 1.0/emissionNum;//百分比的自加
		float angleJia=(float) 360.0/groupCount;//角度的自加
		for(int i=0;i<emissionNum;i++)//每一组进行循环
		{
			float angle=0;//自增的角度
			for(int j=0;j<groupCount;j++)
			{
				float px=(float)(sx+(circleR+width*bfbWidth)* Math.cos(Math.toRadians(angle)));//计算粒子位置x坐标
				float py=(float)(sy+(circleR+width*bfbWidth)* Math.sin(Math.toRadians(angle)));//计算粒子位置y坐标
				points[(i*groupCount+j)*4]=px;//将粒子位置的x坐标存入points数组中
				points[(i*groupCount+j)*4+1]=py;//将粒子位置的y坐标存入points数组中
				points[(i*groupCount+j)*4+2]=0;//将粒子x方向的速度存入points数组中
				points[(i*groupCount+j)*4+3]=10.0f;//将粒子的当前生命期存入points数组中----为10时，粒子处于没有被激活状态，不为10时，粒子处于活跃状态
				angle+=angleJia;//角度自加
			}
			bfbWidth+=bfbJia;//百分比自加
		}
		//循环遍历第一批的粒子
		for(int j=0;j<groupCount;j++)//将一组粒子设置成了活跃状态
		{
			points[4*j+3]=maxLifeSpan;//设置粒子生命期，不为10时，表示粒子处于活跃状态
		}

		return points;//返回所有粒子顶点属性数据
	}

	public void drawSelf(int texId)
	{
		//关闭深度检测
		GLES30.glDisable(GLES30.GL_DEPTH_TEST);
		//开启混合
		GLES30.glEnable(GLES30.GL_BLEND);
		//设置混合方式
		GLES30.glBlendEquation(blendFunc);
		//设置混合因子
		GLES30.glBlendFunc(srcBlend,dstBlend);
		MatrixState.translate(0, 0, positionZ);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.pushMatrix();//保护现场
		fpfd.drawSelf(texId,startColor,endColor,maxLifeSpan);//绘制粒子群
		MatrixState.popMatrix();//恢复现场
		//开启深度检测
		GLES30.glEnable(GLES30.GL_DEPTH_TEST);
		//关闭混合
		GLES30.glDisable(GLES30.GL_BLEND);
	}

	int count=1;//激活粒子的位置计算器
	int fancount=1;//count正反标志位
	boolean isUp=true;//是否在上升
	int minHuan=0;//回收到最小的圈数
	public void update()//更新粒子状态的方法
	{
		//重复完后重新记数
		if(count>=emissionNum-1)//计算器超过激活粒子位置时 组数
		{
			fancount=-fancount;//索引取反
			isUp=false;//更改标志位
		}else if(count<=minHuan){
			fancount=-fancount;//索引取反
			isUp=true;//更改标志位
		}

		if(isUp){ //扩散时发生的动作
			//查看生命期以及计算下一位置
			for(int i=0;i<points.length/4;i++)//遍历每一个粒子
			{
				if(points[i*4+3]!=10.0f)//当前为活跃粒子时   //通过生命周期来改变粒子的颜色
				{
					points[i*4+3]-=lifeSpanStep;//计算当前生命期
				}
			}
			for(int i=0;i<groupCount;i++)//释放
			{//循环发射一批激活计数器所指定位置的粒子
				if(points[groupCount*count*4+4*i+3]==10.0f)//如果粒子处于未激活状态时   一个粒子包括四个位置，必须乘4
				{
					points[groupCount*count*4+4*i+3]=maxLifeSpan;//激活粒子--设置粒子当前的生命期
				}
			}
		}
		else//销毁时发生的动作,将粒子收回
		{
			for(int i=0;i<points.length/4;i++)//遍历每一个粒子
			{
				if(points[i*4+3]!=10.0f)//当前为活跃粒子时   //通过生命周期来改变粒子的颜色
				{
					points[i*4+3]+=lifeSpanStep;//计算当前生命期
				}
			}
		}
		//加锁的目的原因
		synchronized(lock)
		{//加锁--防止在更新顶点坐标数据时，将顶点坐标数据送入渲染管线
			fpfd.updatVertexData(points);//更新顶点坐标数据缓冲的方法
		}
		//下次激活粒子的位置
		count=count+fancount;
	}
	public void calculateBillboardDirection()
	{
		//根据摄像机位置计算火焰朝向
		float xspan=positionX-Constant.CameraX;
		float zspan=positionZ-Constant.CameraZ;
		if(zspan<=0)
		{
			yAngle=(float) Math.toDegrees(Math.atan(xspan/zspan));
		}
		else
		{
			yAngle=180+(float) Math.toDegrees(Math.atan(xspan/zspan));
		}
	}
	@Override
	public int compareTo(ParticleSystem another) {
		//重写的比较两个火焰离摄像机距离的方法
		float xs=positionX-Constant.CameraX;
		float zs=positionZ-Constant.CameraZ;

		float xo=another.positionX-Constant.CameraX;
		float zo=another.positionZ-Constant.CameraZ;

		float disA=(float)(xs*xs+zs*zs);
		float disB=(float)(xo*xo+zo*zo);
		return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;
	}

}
