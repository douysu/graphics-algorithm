package com.bn.Constant;
/**
 * Simple to Introduction
 * @Author          [苏伊 yindou97@163.com]
 * @Date            [2018-10-18]
 * @Description     [屏幕坐标系转换类，用于绘制背景]
 * @version         [2.0]
 */
public class ScreenScaleUtil {
    public static float[] fromPixPositionToScreenPosition(float cx,float cy)
    {
        float xAndY_Position[]=new float[3];//0 x 1 y
        if (Constant.SCREEN_HEIGHT>Constant.SCREEN_WIDTH)
        {
            xAndY_Position[0]=(cx-Constant.SCREEN_WIDTH/2)/(Constant.SCREEN_WIDTH/2);//x最大是1
            xAndY_Position[1]=(Constant.SCREEN_HEIGHT/2-cy)/(Constant.SCREEN_HEIGHT/2)*(Constant.SCREEN_HEIGHT/Constant.SCREEN_WIDTH);//y最大是height/width
        }
        else if (Constant.SCREEN_HEIGHT<Constant.SCREEN_WIDTH) {
            xAndY_Position[1] = ((Constant.SCREEN_HEIGHT / 2) - cy) / (Constant.SCREEN_HEIGHT / 2);
            xAndY_Position[0] = (cx - (Constant.SCREEN_WIDTH / 2)) / (Constant.SCREEN_WIDTH / 2) * (Constant.SCREEN_WIDTH / Constant.SCREEN_HEIGHT);
        }
        return  xAndY_Position;
    }
}
