package com.bn.streak;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
import android.opengl.GLES30;

import com.bn.Constant.MatrixState;
import com.bn.Constant.ShaderUtil;
import com.bn.main.MySurfaceView;

import static com.bn.streak.StreakDataConstant.lock;

public class StreakForDraw
{
    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int muLifeSpan;//最大生命周期
    int maTexCoorHandle; //顶点纹理坐标属性引用id
    int maLineColor;//线条颜色

    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本

    FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer    mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;
    final float UNIT_SIZE=0.2f;
    public StreakForDraw(MySurfaceView mv)
    {
        //调用初始化顶点数据的initVertexData方法
        initVertexData();
        //调用初始化着色器的intShader方法
        initShader(mv);
    }
    //更新顶点坐标数据缓冲的方法
    public void updatVertexData(float[] points,float[] vpoints)
    {
        vCount=points.length/3;
        ByteBuffer vbb = ByteBuffer.allocateDirect(points.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为浮点(Float)型缓冲
        mVertexBuffer.clear();
        mVertexBuffer.put(points);//在缓冲区内写入数据
        mVertexBuffer.position(0);//设置缓冲区起始位置


        ByteBuffer cbb = ByteBuffer.allocateDirect(vpoints.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.clear();
        mTexCoorBuffer.put(vpoints);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }

    public void initVertexData()//初始化顶点数据的方法
    {
        vCount=0;
        //顶点坐标数据的初始化
        float vertices[]=new float[]//顶点坐标数组
         {
        };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为浮点(Float)型缓冲
        mVertexBuffer.put(vertices);//在缓冲区内写入数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //顶点纹理数据的初始化================begin============================
        //创建纹理坐标缓冲
        float textureCoors[]=new float[]//顶点纹理S、T坐标值数组
        {
        };
        //创建顶点纹理数据缓冲
        //textureCoors.length×4是因为一个float型整数四个字节
        ByteBuffer cbb = ByteBuffer.allocateDirect(textureCoors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(textureCoors);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理数据的初始化================end============================
    }

    //初始化着色器的方法
    @SuppressLint("NewApi")
    public void initShader(MySurfaceView mv)
    {
        //加载顶点着色器的脚本内容
        mVertexShader= ShaderUtil.loadFromAssetsFile("streak_vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("streak_frag.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中衰减因子引用id
        muLifeSpan= GLES30.glGetUniformLocation(mProgram, "maxLifeSpan");
        //获取颜色
        maLineColor= GLES30.glGetUniformLocation(mProgram, "lineColor");

    }

    @SuppressLint("NewApi")
    public void drawSelf(int texId,float maxLifeSpan,float[] lineColor)
    {
        //指定使用某套shader程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将最大生命周期传入渲染管线
        GLES30.glUniform1f(muLifeSpan, maxLifeSpan);
        //将线条颜色
        GLES30.glUniform4fv(maLineColor, 1, lineColor, 0);

        synchronized(lock)
        {//加锁--防止在将顶点坐标数据送入渲染管线时，更新顶点坐标数据
        //将顶点位置数据传送进渲染管线
            GLES30.glVertexAttribPointer(
                    maPositionHandle,
                    3,
                    GLES30.GL_FLOAT,
                    false,
                    3*4,
                    mVertexBuffer
            );
            //为画笔指定顶点纹理坐标数据
            GLES30.glVertexAttribPointer(
                    maTexCoorHandle,
                    2,
                    GLES30.GL_FLOAT,
                    false,
                    2*4,
                    mTexCoorBuffer
                    );
        }
        GLES30.glEnableVertexAttribArray(maPositionHandle);//启用顶点位置数据
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);//允许纹理顶点数组

        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

        //绘制三角形 GL_TRIANGLE_STRIP, GL_LINE_STRIP
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vCount);
    }
}