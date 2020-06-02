package com.bn.activty;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bn.util.Constant;
import com.bn.util.MyFunction;


public class MainActivity extends Activity  {
    private MySurfaceView mGLSurfaceView;//自己SurfaceView
    String organStr[]=new String[]{"骨骼","大脑","食道","肺","心脏","肝脏","胰脏","胆囊","胃","脾","肠","肾","膀胱"};//男生器官字符串
    String womanorganStr[]=new String[]{"骨骼","大脑","食道","肺","心脏","肝脏","胰脏","胆囊","胃","子宫","肠","肾","膀胱"};//女生器官字符串
    int oldSelect=14;//记录上次点击的值
    float colorRed[]=new float[]{1.0f,0.0f,0.0f,1.0f};//红色
    ListView  ls;//器官的listView
    BaseAdapter ba;//男的Adapter
    BaseAdapter baWoman;//女的Adapter
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为横屏模式
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
        mGLSurfaceView = new MySurfaceView(this);

        //将自定义的SurfaceView添加到外层LinearLayout中
        LinearLayout ll=(LinearLayout)findViewById(R.id.main_liner);
        ll.addView(mGLSurfaceView);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        //获得单选按钮
        RadioButton RadioMan=(RadioButton)this.findViewById(R.id.RadioMan);
        RadioButton RadioWoman=(RadioButton)this.findViewById(R.id.RadioWoman);
        //获得ListView
        ls=(ListView)this.findViewById(R.id.listOrgan);
        //男Adapter
         ba=new BaseAdapter()
        {
            @Override
            public int getCount() {
                return organStr.length;
            }

            @Override
            public Object getItem(int arg0) { return null; }

            @Override
            public long getItemId(int arg0) { return 0; }

            @Override
            public View getView(int arg0, View arg1, ViewGroup arg2) {
                LinearLayout ll=new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);//设置朝向
                //初始化TextView
                TextView tv=new TextView(MainActivity.this);
                tv.setText(organStr[arg0]);
                tv.setTextSize(24);//设置字体大小
                tv.setTextColor(MainActivity.this.getResources().getColor(R.color.white));//设置字体颜色
                tv.setGravity(Gravity.LEFT);//靠左
                ll.addView(tv);
                return ll;
            }
        };
        //女Adapter
        baWoman=new BaseAdapter()
        {
            @Override
            public int getCount() {
                return womanorganStr.length;
            }

            @Override
            public Object getItem(int arg0) { return null; }

            @Override
            public long getItemId(int arg0) { return 0; }

            @Override
            public View getView(int arg0, View arg1, ViewGroup arg2) {
                LinearLayout ll=new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);//设置朝向
                //初始化TextView
                TextView tv=new TextView(MainActivity.this);
                tv.setText(womanorganStr[arg0]);
                tv.setTextSize(24);//设置字体大小
                tv.setTextColor(MainActivity.this.getResources().getColor(R.color.white));//设置字体颜色
                tv.setGravity(Gravity.LEFT);//靠左
                ll.addView(tv);
                return ll;
            }
        };
        //男单选按钮
        RadioMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ls.setAdapter(ba);//设置男Adapter
                mGLSurfaceView.manOrWoman=true;//更改性别标志位
                mGLSurfaceView.womanorganColor= MyFunction.initWomanColor();//初始化女生颜色
                oldSelect=14;//初始化选择器官
                mGLSurfaceView.selectOrgan=14;//初始化选择器官
            }
        });
        //女单选按钮
        RadioWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ls.setAdapter(baWoman);//设置女Adapter
                mGLSurfaceView.manOrWoman=false;//更改性别标志位
                mGLSurfaceView.tempmanOrganColor= MyFunction.initManColor();//初始化男生颜色
                oldSelect=14;//初始化选择器官
                mGLSurfaceView.selectOrgan=14;//初始化选择器官
            }
        });
        //更改菜单界面的方法
        ls.setAdapter(ba);
        //设置选项被单击的监听器
        ls.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {//重写选项被单击事件的处理方法
                        mGLSurfaceView.addOrDec=true;//设置循环标志位
                        if(oldSelect>=0&&oldSelect<=13)
                        {
                            mGLSurfaceView.tempbrightBreath[oldSelect]=1.0f;//将上一器官的呼吸系数初始化为1
                            mGLSurfaceView.tempmanOrganColor[oldSelect]=Constant.organColor[oldSelect];//将原颜色给男器官
                            mGLSurfaceView.womanorganColor[oldSelect]=Constant.womanorganColor[oldSelect];//将原颜色给女器官
                        }
                        setColor(arg2);//上红色
                        setSelectOrgan(arg2);//更改选中器官
                        oldSelect=arg2;//记录上一次选择的值
                    }
                }
        );
    }
    //设置颜色
    public void setColor(int select)
    {
        if(mGLSurfaceView.manOrWoman){
            mGLSurfaceView.tempmanOrganColor[select]=colorRed;
        }else
        {
            mGLSurfaceView.womanorganColor[select]=colorRed;
        }

    }
    //设置选中器官
    public void setSelectOrgan(int select)
    {
        mGLSurfaceView.selectOrgan=select;
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