package bn.com.myapplication;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

/**
 * Created by Administrator on 2017/10/15.
 */

public class GL2JNILib {
    static
    {
        System.loadLibrary("native-lib");
    }
    public static native void init(GLSurfaceView mv, int width, int height);//本地初始化方法
    public static native void step();//本地刷新场景方法
    public static native void nativeSetAssetManager(AssetManager am); 	//将AssetManager传入C++的方法
}
