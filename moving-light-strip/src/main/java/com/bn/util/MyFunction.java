package com.bn.util;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MyFunction {
    //初始化男器官颜色
    public static float[][] initManColor()
    {
        float tOrganColor[][]=new float[Constant.manOrganNumber][4];
        for(int i=0;i<Constant.organColor.length;i++)
        {
            tOrganColor[i]=Constant.organColor[i];
        }
        return tOrganColor;
    }
    //初始化女器官颜色
    public static float[][] initWomanColor()
    {
        float tOrganColor[][]=new float[Constant.WomanOrganNumber][4];
        for(int i=0;i<Constant.womanorganColor.length;i++)
        {
            tOrganColor[i]=Constant.womanorganColor[i];
        }
        return tOrganColor;
    }
    //初始化呼吸系数
    public static float[] initBreath()
    {
        float tOrganColor[]=new float[15];
        for(int i=0;i<tOrganColor.length;i++)
        {
            tOrganColor[i]=1.0f;
        }
        return tOrganColor;
    }
}
