package com.bn.activty;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;

import com.bn.organ.ManBodyGroup;
import com.bn.manager.ShaderManager;
import com.bn.organ.LoadedObjectOrgan;
import com.bn.organ.WomanBodyGroup;
import com.bn.util.Constant;
import com.bn.util.LoadUtil;
import com.bn.util.MatrixState;
import com.bn.util.MyFunction;

import java.util.Collections;

/**
 * Created by Administrator on 2017/6/9.
 */

public class MySurfaceView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
    private float mPreviousX;//上次的触控位置X坐标

    //男女标志位
    boolean manOrWoman=true;//真为男，假为女

    public static float cx=0.0f;//摄像机x
    public static float cz=30.0f;//摄像机z
    public static float cheight=11.0f;//摄像机高度
    public static float cdistancep=30.0f;//摄像机与人物距离
    public static float scaleBi=0.05f;//人物缩放比

    public int selectOrgan=14;//选中的器官编号,初始值14为没有器官
    float tempbrightBreath[];//存放器官的呼吸参数
    float tempmanOrganColor[][];//存放男器官颜色数组
    float womanorganColor[][];//女器官颜色数组
    Activity activity;
    public MySurfaceView(Context context) {
        super(context);
        activity=(MainActivity) context;
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }
    //触摸事件回调方法
    float yAngle=0.0f;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;//计算触控笔X位移
                yAngle -= dx*TOUCH_SCALE_FACTOR;//设置沿y轴旋转角度
                requestRender();//重绘画面
        }
        cx=(float)(Math.sin(Math.toRadians(yAngle))*cdistancep); //计算新的摄像机x坐标
        cz=(float)((Math.cos(Math.toRadians(yAngle))*cdistancep)); //计算新的摄像机z坐标
        mPreviousX = x;//记录触控笔位置
        if(manOrWoman) {
            Collections.sort(mRenderer.manBodyGroup.alist);//男皮肤排序
        }else
        {
            Collections.sort(mRenderer.womanbodyGroup.aWomanlist);//女皮肤排序
        }
        return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        //从指定的obj文件中加载对象
        //男
        ManBodyGroup manBodyGroup;//人物皮肤组
        LoadedObjectOrgan allOrganArray[]=new LoadedObjectOrgan[14];//存放器官的数组
        //女
        WomanBodyGroup womanbodyGroup;//女的皮肤组
        LoadedObjectOrgan womanallOrganArray[]=new LoadedObjectOrgan[14];//存放器官的数组

        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.setCamera(cx, cheight, cz, 0.0f, cheight, 0.0f, 0.0f, 1.0f, 0.0f);//重新设置照相机
            MatrixState.setLightLocation(cx, cheight, cz);//设置光源位置
            //坐标系推远
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0f, 0.0f);
            //缩小比例
            MatrixState.scale(scaleBi, scaleBi, scaleBi);
            //画器官
            if(manOrWoman){
                for(int i=0;i<allOrganArray.length;i++)//画男的器官
                {
                    allOrganArray[i].drawSelf(tempmanOrganColor[i],tempbrightBreath[i]);
                }
            }else{
                for(int i=0;i<womanallOrganArray.length;i++)//画女的器官
                {
                    womanallOrganArray[i].drawSelf(womanorganColor[i],tempbrightBreath[i]);
                }
            }
            //开启混合
            GLES30.glEnable(GLES30.GL_BLEND);
            //设置混合因子,其中第一个为源因子，第二个为目标因子
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            //若加载的物体不为空则绘制物体
            if(manOrWoman){
                //传入线的宽度，亮度，位置
                manBodyGroup.drawSelf(Constant.lineWidth,Constant.lineBright,Constant.linePosition);//画男皮肤
            }else{
                //传入线的宽度，亮度，位置
                womanbodyGroup.drawSelf(Constant.lineWidth,Constant.lineBright,Constant.linePosition);//画女皮肤
            }
            //关闭混合
            GLES30.glDisable(GLES30.GL_BLEND);
            //恢复现场
            MatrixState.popMatrix();
        }
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx, cheight, cz, 0f, cheight, 0f, 0f, 1.0f, 0.0f);
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //关闭背面剪裁
            //GLES30.glDisable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(cx, cheight, cz);
            //初始化颜色呼吸数组
            tempbrightBreath= MyFunction.initBreath();//初始器官呼吸系数
            tempmanOrganColor=MyFunction.initManColor();//初始化男器官颜色
            womanorganColor=MyFunction.initWomanColor();//初始化女器官颜色
            //编译着色器
            ShaderManager.loadCodeFromFile(activity.getResources());
            ShaderManager.compileShader();
            //加载要绘制的物体
            manBodyGroup =new ManBodyGroup(MySurfaceView.this);//创建男皮肤组
            Collections.sort(mRenderer.manBodyGroup.alist);//排序
            womanbodyGroup=new WomanBodyGroup(MySurfaceView.this);//创建女皮肤组
            Collections.sort(mRenderer.womanbodyGroup.aWomanlist);//排序
            //加载男器官
            allOrganArray[0]=LoadUtil.LoadedObjectOrgan("organ/man_bone.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//骨骼模型
            allOrganArray[1]=LoadUtil.LoadedObjectOrgan("organ/man_brain.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//大脑
            allOrganArray[2]=LoadUtil.LoadedObjectOrgan("organ/man_esophagus.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//食道
            allOrganArray[3]=LoadUtil.LoadedObjectOrgan("organ/man_lung.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//肺
            allOrganArray[4]=LoadUtil.LoadedObjectOrgan("organ/man_heart.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//心脏
            allOrganArray[5]=LoadUtil.LoadedObjectOrgan("organ/man_liver.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//肝
            allOrganArray[6]=LoadUtil.LoadedObjectOrgan("organ/man_pancreatic.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//胰脏
            allOrganArray[7]=LoadUtil.LoadedObjectOrgan("organ/man_gallbladder.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//胆囊
            allOrganArray[8]=LoadUtil.LoadedObjectOrgan("organ/man_stomach.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//胃
            allOrganArray[9]=LoadUtil.LoadedObjectOrgan("organ/man_spleen.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//脾
            allOrganArray[10]=LoadUtil.LoadedObjectOrgan("organ/man_intestinal.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//肠模型
            allOrganArray[11]=LoadUtil.LoadedObjectOrgan("organ/man_kidney.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//大脑
            allOrganArray[12]=LoadUtil.LoadedObjectOrgan("organ/man_bladder.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//膀胱
            allOrganArray[13]=LoadUtil.LoadedObjectOrgan("organ/man_unknow.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//未知模型
            //加载女器官
            womanallOrganArray[0]=LoadUtil.LoadedObjectOrgan("organ/woman_bone.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//骨骼模型
            womanallOrganArray[1]=LoadUtil.LoadedObjectOrgan("organ/woman_brain.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//大脑
            womanallOrganArray[2]=LoadUtil.LoadedObjectOrgan("organ/woman_esophagus.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//食道
            womanallOrganArray[3]=LoadUtil.LoadedObjectOrgan("organ/woman_lung.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//肺
            womanallOrganArray[4]=LoadUtil.LoadedObjectOrgan("organ/woman_heart.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//心脏
            womanallOrganArray[5]=LoadUtil.LoadedObjectOrgan("organ/woman_liver.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//肝
            womanallOrganArray[6]=LoadUtil.LoadedObjectOrgan("organ/woman_pancreatic.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//胰脏
            womanallOrganArray[7]=LoadUtil.LoadedObjectOrgan("organ/woman_gallbladder.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//胆囊
            womanallOrganArray[8]=LoadUtil.LoadedObjectOrgan("organ/woman_stomach.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//胃
            womanallOrganArray[9]=LoadUtil.LoadedObjectOrgan("organ/woman_uterus.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//脾
            womanallOrganArray[10]=LoadUtil.LoadedObjectOrgan("organ/woman_intestinal.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//肠模型
            womanallOrganArray[11]=LoadUtil.LoadedObjectOrgan("organ/woman_kidney.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//大脑
            womanallOrganArray[12]=LoadUtil.LoadedObjectOrgan("organ/woman_bladder.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//膀胱
            womanallOrganArray[13]=LoadUtil.LoadedObjectOrgan("organ/woman_unknow.obj", MySurfaceView.this.getResources(),MySurfaceView.this,ShaderManager.getShader(1));//未知模型

            BreathThread rt=new BreathThread();//呼吸线程
            rt.start();
            LightThread lt=new LightThread();//光带线程
            lt.start();
        }
    }
    //呼吸线程
    boolean addOrDec=true;//真为系数+ 假为系数-
    public class BreathThread extends Thread
    {
        public boolean flag=true;
        @Override
        public void run()
        {
            while(flag)
            {
                //判断增加减小
                if(addOrDec){
                    tempbrightBreath[selectOrgan]+=0.05f;
                }else {
                    tempbrightBreath[selectOrgan]-=0.05f;
                }
                //如果小于1，呼吸系数等于1
                if(tempbrightBreath[selectOrgan]<1.0) {
                    tempbrightBreath[selectOrgan]=1.0f;
                    addOrDec=true;
                }
                //如果呼吸系数大于最大值
                if(tempbrightBreath[selectOrgan]>Constant.manMaxLight)
                {
                    tempbrightBreath[selectOrgan]=Constant.manMaxLight;
                    addOrDec=false;
                }
                try {Thread.sleep(50);}
                catch(Exception e) {e.printStackTrace();}
            }
        }
    }
    //光带线程
    public class LightThread extends Thread
    {
        public boolean flag=true;
        @Override
        public void run()
        {
            while(flag)
            {
                //光带位置自动增加
                Constant.linePosition+=10;
                if(Constant.linePosition>=Constant.manHeight) {
                    Constant.linePosition=0;
                }
                try {Thread.sleep(100);}
                catch(Exception e) {e.printStackTrace();}
            }
        }
    }
}
