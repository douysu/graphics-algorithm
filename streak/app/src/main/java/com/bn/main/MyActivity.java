package com.bn.main;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bn.main.MySurfaceView;
/**
 * Simple to Introduction
 * @Author          [苏伊 yindou97@163.com]
 * @Date            [2018-10-18]
 * @Description     [Activity]
 * @version         [2.0]
 */
public class MyActivity extends Activity {
    private MySurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //初始化GLSurfaceView
        mGLSurfaceView = new MySurfaceView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}



