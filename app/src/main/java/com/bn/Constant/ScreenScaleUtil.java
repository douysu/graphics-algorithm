package com.bn.Constant;

/**
 * Created by Administrator on 2017/8/28.
 */

public class ScreenScaleUtil {
    //返回屏幕3d位置
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
