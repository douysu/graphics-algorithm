package com.bn.organ;

import com.bn.activty.MySurfaceView;
import com.bn.manager.ShaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/10.
 */

public class ManBodyGroup {
    public List<ManBodySingle> alist=new ArrayList<ManBodySingle>();//创建皮肤的列表
    public ManBodyGroup(MySurfaceView mv)
    {
        //加载男皮肤
        alist.add(new ManBodySingle("body/man_body_left_leg.obj",mv, ShaderManager.getShader(0),38.284f,12.333f,150.12f));//左腿
        alist.add(new ManBodySingle("body/man_body_right_leg.obj",mv,ShaderManager.getShader(0),-38.862f,12.333f,135.832f));//右腿
        alist.add(new ManBodySingle("body/man_body_left_arm.obj",mv,ShaderManager.getShader(0),80.458f,7.83f,324.217f));//左胳膊
        alist.add(new ManBodySingle("body/man_body_right_arm.obj",mv,ShaderManager.getShader(0),-80.458f,7.83f,324.217f));//右胳膊
        alist.add(new ManBodySingle("body/man_body_top.obj",mv,ShaderManager.getShader(0),0f,7.83f,324.217f));//身体中部
    }
    //绘制皮肤
    public void drawSelf(float lineWidth,float lineBright,float linePosition)
    {
        for(int i=0;i<alist.size();i++)
        {
            alist.get(i).drawSelf(lineWidth,lineBright,linePosition);//调用SingleTree对象的drawSelf方法绘制植物
        }
    }
}
