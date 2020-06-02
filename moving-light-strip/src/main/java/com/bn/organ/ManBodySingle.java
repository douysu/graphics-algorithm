package com.bn.organ;

import com.bn.activty.MySurfaceView;
import com.bn.util.LoadUtil;
/**
 * Created by Administrator on 2017/6/10.
 */

public class ManBodySingle implements Comparable<ManBodySingle>{
    float centerX;//中心X坐标
    float centerY;//中心Y坐标
    float centerZ;//中心Z坐标
    LoadedObjectBody body;
    String objNanem="";
    MySurfaceView mv;
    public ManBodySingle(String objName, MySurfaceView mv, int programId, float centerX, float centerY, float centerZ)
    {
        body= LoadUtil.LoadedObjectBody(objName, mv.getResources(),mv,programId);//皮肤模型
        this.mv=mv;
        this.centerX=centerX;
        this.centerY=centerY;
        this.centerZ=centerZ;
        this.objNanem=objName;
    }
    public void drawSelf(float lineWidth,float lineBright,float linePosition)
    {
        //传入线的宽度，线的亮度系数，线的位置
        body.drawSelf(lineWidth,lineBright,linePosition);
    }
    @Override
    public int compareTo(ManBodySingle another)
    {
        //根据植物到摄像机的距离比较两个植物“大小”的方法
        float xs=centerX*mv.scaleBi-mv.cx;//计算从模型位置到摄像机位置向量的x分量
        float ys=centerY*mv.scaleBi-10;   //计算从模型位置到摄像机位置向量的y分量
        float zs=centerZ*mv.scaleBi-mv.cz;//计算从本模型位置到摄像机位置向量的z分量

        float xo=another.centerX*mv.scaleBi-mv.cx;//计算另一个模型位置到摄像机位置向量的x分量
        float yo=another.centerY*mv.scaleBi-10;   //计算另一个模型位置到摄像机位置向量的y分量
        float zo=another.centerZ*mv.scaleBi-mv.cz;//计算另一个模型位置到摄像机位置向量的z分量

        float disA=(float)Math.sqrt(xs*xs+zs*zs+ys*ys);//计算当前植物到摄像机的距离
        float disB=(float)Math.sqrt(xo*xo+zo*zo+yo*yo);//计算另一植物到摄像机的距离
        return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;  //根据距离大小决定方法返回值
    }
}
