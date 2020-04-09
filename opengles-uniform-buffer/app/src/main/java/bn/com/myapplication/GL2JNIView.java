package bn.com.myapplication;//声明包名

import android.annotation.SuppressLint;
import android.content.Context;//相关类的引入
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class GL2JNIView extends GLSurfaceView
{
    Renderer renderer;//自定义渲染器的引用

    public GL2JNIView(Context context) //构造器
    {
        super(context);
        this.setEGLContextClientVersion(3);//使用OpenGL ES 3.0需设置该参数为3
        renderer=new Renderer();//创建Renderer类的对象
        this.setRenderer(renderer);	//设置渲染器
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private  class Renderer implements GLSurfaceView.Renderer
    {
        public void onDrawFrame(GL10 gl)
        {
            GL2JNILib.step();//调用本地方法刷新场景
        }

        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            GL2JNILib.init(GL2JNIView.this,width, height);//调用本地方法初始化
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {

        }
    }
    //加载纹理的方法
    @SuppressLint("NewApi")
    public static int initTexture(GLSurfaceView gsv,String pname)
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
        try {
            is = gsv.getResources().getAssets().open(pname);
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
        //实际加载纹理
        GLUtils.texImage2D
                (
                        GLES30.GL_TEXTURE_2D,   //纹理类型
                        0, 					  	//纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmapTmp, 			  	//纹理图像
                        0					  	//纹理边框尺寸
                );
        bitmapTmp.recycle(); 		  	//纹理加载成功后释放图片
        return textureId;
    }
}
