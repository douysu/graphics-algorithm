package wyf.lxg.mywallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.view.SurfaceHolder;

public abstract class OpenGLES3WallpaperService extends GLWallpaperService {
	public interface Renderer {

	}
	@Override
	public Engine onCreateEngine() {
		return new OpenGLES2Engine();
	}

	class OpenGLES2Engine extends GLWallpaperService.GLEngine {

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			// Check if the system supports OpenGL ES 3.0.
			//创建Activity管理器
			final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			//获取当前设备配置信息
			final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
			//判断是否支持OpenGL ES 3.0
			final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
			if (supportsEs2)
			{
				setEGLContextClientVersion(3);//设置使用OpenGL ES 3.0
				setPreserveEGLContextOnPause(true);
				setRenderer(getNewRenderer());//设置场景渲染器
			}
			else
			{
				return;
			}
		}


	}

	abstract android.opengl.GLSurfaceView.Renderer getNewRenderer();
}
