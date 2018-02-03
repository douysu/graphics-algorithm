package com.bn.streak;

import android.opengl.GLES30;

import com.bn.Constant.MatrixState;
import com.bn.Constant.ScreenScaleUtil;

import java.util.ArrayList;
import java.util.List;

import static com.bn.streak.StreakDataConstant.lock;

/**
 * Created by Douzi on 2017/8/29.
 */

public class StreakSystem {

    float positionZ=0.0f;//z平面
    StreakForDraw sfd;//绘制者
    public StreakThread  streakThread;//更新线程
    int streak;//拖尾纹理图片
    int streakNum;//最大顶点个数

    float maxLifeSpan;//最大生命周期
    float lifeSpanStep;//生命周期的步进
    float lineColor[];//拖尾颜色

    float Stroke;//条带的宽度
    float StreakNum;//列表最大长度

    int srcBlend;//源混合因子
    int dstBlend;//目标混合因子
    int blendFunc;//混合方式

    public List<float[]> lsPoints=new ArrayList<float[]>(StreakDataConstant.StreakNum);//存放位置的List

    public StreakSystem(float positionZ,StreakForDraw sfd,int streak){
        this.positionZ=positionZ;
        this.sfd=sfd;
        this.Stroke=StreakDataConstant.Stroke;
        this.StreakNum=StreakDataConstant.StreakNum;
        this.maxLifeSpan=StreakDataConstant.MAX_LIFE_SPAN;
        this.lifeSpanStep=StreakDataConstant.LIFE_SPAN_STEP;
        this.streak=streak;
        this.streakNum=StreakDataConstant.StreakNum;
        this.lineColor=StreakDataConstant.lineColor;
        this.blendFunc=StreakDataConstant.BLEND_FUNC;
        this.srcBlend=StreakDataConstant.SRC_BLEND;
        this.dstBlend=StreakDataConstant.DST_BLEND;
        //启动线程
        streakThread=new StreakThread(this);
        streakThread.start();
    }
    //计算点位置的方法
    /**
     * @param x1new	新触控x
     * @param y1new	新触控y
     * @param x2old	旧触控x
     * @param y2old 旧触控y
     */
    public void moveCalculate(float x1new,float y1new,float x2old,float y2old){
        float xyNew[];//坐标临时数组
        float xyOld[];
        /**
         * x,y,生命周期
         * */
        float kuanxy1[]=new float[3];//两个新的坐标数组
        float kuanxy2[]=new float[3];

        float x1,y1,x2,y2,xcenter,ycenter;//坐标位置
        //获取世界坐标系坐标
        xyNew= ScreenScaleUtil.fromPixPositionToScreenPosition(x1new,y1new);
        xyOld= ScreenScaleUtil.fromPixPositionToScreenPosition(x2old,y2old);
        //得到坐标
        x1=xyNew[0];y1=xyNew[1];//新坐标
        x2=xyOld[0];y2=xyOld[1];//旧坐标
        xcenter=(x1+x2)/2.0f;ycenter=(y1+y2)/2.0f;//中点坐标
        //限定长度
        if(lsPoints.size()== StreakDataConstant.StreakNum){
            lsPoints.remove(StreakDataConstant.StreakNum-1);
            lsPoints.remove(StreakDataConstant.StreakNum-2);
        }
        //递减生命周期(手指一直拖动保持其长度)
        for(int i=0;i<lsPoints.size();i++){
            lsPoints.get(i)[2]-=lifeSpanStep;
            if(lsPoints.get(i)[2]<=0){
                lsPoints.get(i)[2]=0;
            }
        }
        //求出线段两边点坐标,并添加
        if((y1==y2)&&(x1!=x2)){//横着划一条直线(无斜率)
            //第一个点
            kuanxy1[0]=xcenter;
            kuanxy1[1]=ycenter+Stroke;
            kuanxy1[2]=maxLifeSpan;
            //第二个点
            kuanxy2[0]=xcenter;
            kuanxy2[1]=ycenter-Stroke;
            kuanxy2[2]=maxLifeSpan;
            /**
             * 横着向右互动，将顶点1首先送入
             * 向左，将顶点2送入
             * */
            if(x1>x2){//横着向右滑动
                lsPoints.add(0,kuanxy1);
                lsPoints.add(0,kuanxy2);
            }else {//横着向左滑动
                lsPoints.add(0,kuanxy2);
                lsPoints.add(0,kuanxy1);
            }
        }else if((y1==y2)&&(x1==x2)){//点重合
            //第一个点
            kuanxy1[0]=xcenter;
            kuanxy1[1]=ycenter;
            kuanxy1[2]=maxLifeSpan;
            //第二个点
            kuanxy2[0]=xcenter;
            kuanxy2[1]=ycenter;
            kuanxy2[2]=maxLifeSpan;
            lsPoints.add(0,kuanxy1);
            lsPoints.add(0,kuanxy2);
        }else{
            float k=(x1-x2)/(y1-y2);//当前的斜率
            float t=Stroke*Stroke/(1.0f+k*k);
            //第一个点
            kuanxy1[0]=(float) Math.sqrt(t)+xcenter;
            kuanxy1[1]=(-(x1-x2)*(kuanxy1[0]-xcenter)/(y1-y2))+ycenter;
            kuanxy1[2]=maxLifeSpan;
            //第二个点
            kuanxy2[0]=-(float) Math.sqrt(t)+xcenter;
            kuanxy2[1]=(-(x1-x2)*(kuanxy2[0]-xcenter)/(y1-y2))+ycenter;
            kuanxy2[2]=maxLifeSpan;
            /**
             *判断手指滑动方向，根据大小送入缓冲
             * 直线斜率不存在时除外
             * 一二象限，将上新顶点首先送入顶点缓冲
             * 三四象限，将下新顶点首先送入顶点缓冲
             * */
            if(y1>y2) {//第一二象限
                if(kuanxy1[0]<kuanxy2[0]){
                    lsPoints.add(0,kuanxy1);
                    lsPoints.add(0,kuanxy2);
                }else{
                    lsPoints.add(0,kuanxy2);
                    lsPoints.add(0,kuanxy1);
                }
            }else{//第三四
                if(kuanxy1[0]>kuanxy2[0]){
                    lsPoints.add(0,kuanxy1);
                    lsPoints.add(0,kuanxy2);
                }else{
                    lsPoints.add(0,kuanxy2);
                    lsPoints.add(0,kuanxy1);
                }
            }
        }
        update();
    }
    public void update(){//更新画面的方法
        //顶点数组
        float[] points=new float[lsPoints.size()*3];
        for(int i=0;i<lsPoints.size();i++){
            points[i*3+0]=lsPoints.get(i)[0];
            points[i*3+1]=lsPoints.get(i)[1];
            points[i*3+2]=lsPoints.get(i)[2];
        }
        //纹理坐标数组
        float[] vpoints=new float[lsPoints.size()*2];
        float step=(float)1.0/(lsPoints.size()/2-1);//共分成多少段
        for(int i=0;i<lsPoints.size()/2;i++){
            vpoints[i*4+0]=i*step;//上顶点纹理坐标
            vpoints[i*4+1]=0;
            vpoints[i*4+2]=i*step;//下顶点纹理坐标
            vpoints[i*4+3]=1;
        }
        //加锁的目的原因
        synchronized(lock)
        {//加锁--防止在更新顶点坐标数据时，将顶点坐标数据送入渲染管线
            sfd.updatVertexData(points,vpoints);
        }
    }
    public void  drawSelf(){
        //关闭深度检测
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        //开启混合
        GLES30.glEnable(GLES30.GL_BLEND);
        //设置混合方式
        GLES30.glBlendEquation(blendFunc);
        //设置混合因子
        GLES30.glBlendFunc(srcBlend,dstBlend);
        MatrixState.pushMatrix();//保护矩阵
        MatrixState.translate(0,0,positionZ);
        if(sfd!=null){//绘制拖尾
            /**
             * 长度，最大周期，颜色
             * */
            sfd.drawSelf(streak,maxLifeSpan,lineColor);
        }
        MatrixState.popMatrix();//恢复矩阵
        //开启深度检测
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        //关闭混合
        GLES30.glDisable(GLES30.GL_BLEND);
    }
}
