package com.bn.organ;

import com.bn.activty.MySurfaceView;
import com.bn.manager.ShaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/14.
 */

public class WomanBodyGroup {
    public List<WomanBodySingle> aWomanlist=new ArrayList<WomanBodySingle>();//创建皮肤的列表
    public WomanBodyGroup(MySurfaceView mv)
    {
        //加载女皮肤
        aWomanlist.add(new WomanBodySingle("body/woman_body_left_leg.obj",mv, ShaderManager.getShader(0),31.123f,9.775f,150.12f));//左腿
        aWomanlist.add(new WomanBodySingle("body/woman_body_right_leg.obj",mv,ShaderManager.getShader(0),-31.123f,9.775f,157.458f));//右腿
        aWomanlist.add(new WomanBodySingle("body/woman_body_left_arm.obj",mv,ShaderManager.getShader(0),59.195f,13.239f,301.681f));//左胳膊
        aWomanlist.add(new WomanBodySingle("body/woman_body_right_arm.obj",mv,ShaderManager.getShader(0),-59.195f,13.239f,301.681f));//右胳膊
        aWomanlist.add(new WomanBodySingle("body/woman_body_left.obj",mv,ShaderManager.getShader(0),12.101f,13.239f,301.681f));//身体左部
        aWomanlist.add(new WomanBodySingle("body/woman_body_right.obj",mv,ShaderManager.getShader(0),-12.101f,13.239f,301.681f));//身体右部
    }
    //绘制皮肤
    public void drawSelf(float lineWidth,float lineBright,float linePosition)
    {
        for(int i=0;i<aWomanlist.size();i++)
        {
            aWomanlist.get(i).drawSelf(lineWidth,lineBright,linePosition);//调用SingleTree对象的drawSelf方法绘制植物
        }
    }
}
