package com.bn.main;
import java.io.IOException;
import java.io.InputStream;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.bn.Constant.Constant;
import com.bn.Constant.MatrixState;
import com.bn.background.Background;
import com.bn.streak.StreakForDraw;
import com.bn.streak.StreakCalculatePoints;

/**
 * Simple to Introduction
 * @Author          [苏伊 yindou97@163.com]
 * @Date            [2018-10-18]
 * @Description     [场景类，继承GLSurfaceView，包含绘制方法，触控等]
 * @version         [2.0]
 */
public class MySurfaceView extends GLSurfaceView
{
    private SceneRenderer mRenderer;//场景渲染器

    public static float cx=0.0f;//摄像机x
    public static float cz=5.0f;//摄像机z
    public static float cy=0.0f;//摄像机高度

    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标

    StreakForDraw streakForDraw;//绘制者
    StreakCalculatePoints streakCalculatePoints;//拖尾类
    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3);	//设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }
    int i=0;
    //触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean  onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                streakCalculatePoints.streakThread.isRun=Constant.THREAD_END;//关闭线程更新方法
                streakCalculatePoints.lsPoints.clear();//清空坐标列表
                mPreviousY = y;//将原来点与当前点重合
                mPreviousX = x;
                break;
            case MotionEvent.ACTION_MOVE://滑动
                streakCalculatePoints.moveCalculate(x,y,mPreviousX,mPreviousY);//更新粒子位置
                break;
            case MotionEvent.ACTION_UP://抬起
                streakCalculatePoints.streakThread.isRun=Constant.THREAD_START;//开启线程更新方法
                break;
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    public  class SceneRenderer implements GLSurfaceView.Renderer
    {
        Background background;
        int back;//背景纹理
        int streak;//拖尾图片
        public void onDrawFrame(GL10 gl)
        {
            //清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();//保护矩阵
            if(background!=null){//绘制背景图
               background.drawSelf(back);
            }
            MatrixState.popMatrix();//恢复矩阵

            MatrixState.pushMatrix();//保护矩阵
            if(streakCalculatePoints!=null){//绘制背景图
                streakCalculatePoints.drawSelf();
            }
            MatrixState.popMatrix();//恢复矩阵
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float) height / width;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectOrtho(-1, 1, -ratio, ratio, Constant.NEAR_DISTANCE, Constant.FAR_DISTANCE);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1.0f,0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0f,0f,0f, 1.0f);
            //初始化矩阵
            MatrixState.setInitStack();
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //创建背景对象
            background=new Background(MySurfaceView.this);
            back=initTexture(MySurfaceView.this.getResources(),"background.jpg");

            streakForDraw=new StreakForDraw(MySurfaceView.this);
            /**
             * @param	z平面
             * @param   绘制者
             * @param   纹理图片
             */
            streak=initTexture(MySurfaceView.this.getResources(),"streak.png");
            streakCalculatePoints=new StreakCalculatePoints(0.0f,streakForDraw,streak);
            //关闭背面剪裁
            GLES30.glDisable(GLES30.GL_CULL_FACE);
        }
        public int initTexture(Resources res, String pname)//textureId
        {
            //生成纹理ID
            int[] textures = new int[1];
            GLES30.glGenTextures
                    (
                            1,          //产生的纹理id的数量
                            textures,   //纹理id的数组
                            0           //偏移量
                    );
            int textureId=textures[0];
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);//拉伸方式为截取方式
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
            //通过输入流加载图片===============begin===================
            InputStream is = null;
            String name="pic/"+pname;
            try {
                is = res.getAssets().open(name);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Bitmap bitmapTmp;
            try {
                bitmapTmp = BitmapFactory.decodeStream(is);
            }
            finally {
                try {
                    is.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
            //通过输入流加载图片===============end=====================

            //实际加载纹理
            GLUtils.texImage2D
                    (
                            GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                            0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
                            bitmapTmp, 			  //纹理图像
                            0					  //纹理边框尺寸
                    );
            bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
            return textureId;
        }
    }
}
