package bn.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;


public class MainActivity extends Activity {
    GL2JNIView mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GL2JNILib.nativeSetAssetManager(this.getAssets()); //将AssetManager传入C++
        mView = new GL2JNIView(getApplication());
        mView.requestFocus();					//获取焦点
        mView.setFocusableInTouchMode(true); 	//设置为可触控
        setContentView(mView);//跳转到相关界面
    }

    @Override protected void onPause() {//继承Activity后重写的onPause方法
        super.onPause();
        mView.onPause();//调用GL2JNIView类对象的onPause方法
    }

    @Override protected void onResume() {//继承Activity后重写的onResume方法
        super.onResume();
        mView.onResume();//调用GL2JNIView类对象的onResume方法
    }
    static {
        System.loadLibrary("native-lib");
    }
}
