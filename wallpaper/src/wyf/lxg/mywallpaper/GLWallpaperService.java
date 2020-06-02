package wyf.lxg.mywallpaper;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.Constant.Vector3f;
import wyf.lxg.fishfood.IntersectantUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import static wyf.lxg.Constant.Constant.lockCamera;
import static wyf.lxg.particle.ParticleDataConstant.lock;

public abstract class GLWallpaperService extends WallpaperService {

	public class GLEngine extends Engine {
		class WallpaperGLSurfaceView extends GLSurfaceView {
			//private static final String TAG = "WallpaperGLSurfaceView";

			WallpaperGLSurfaceView(Context context) {
				super(context);//获取上下文
			}

			@Override
			public SurfaceHolder getHolder() {
				return getSurfaceHolder();
			}

			public void onDestroy() {

				super.onDetachedFromWindow();
			}
		}

		//private static final String TAG = "GLEngine";

		private WallpaperGLSurfaceView glSurfaceView;
		private boolean rendererHasBeenSet;

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {

			super.onCreate(surfaceHolder);

			glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {

			super.onVisibilityChanged(visible);

			if (rendererHasBeenSet) {
				if (visible) {
					glSurfaceView.onResume();
				} else {
					glSurfaceView.onPause();
				}
			}
		}

		@Override
		public void onDestroy() {

			super.onDestroy();
			glSurfaceView.onDestroy();
		}

		protected void setRenderer(Renderer renderer) {
			glSurfaceView.setRenderer(renderer);
			rendererHasBeenSet = true;


		}

		private float mPreviousY;//上次的触控位置Y坐标
		private float mPreviousX;//上次的触控位置Y坐标
		@Override
		public void onTouchEvent(MotionEvent e)
		{
			float y = e.getY();
			float x = e.getX();
			switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Constant.feeding=true;
					break;
				case MotionEvent.ACTION_MOVE:
					@SuppressWarnings("unused")
					float dy = y - mPreviousY;// 计算触控笔Y位移
					float dx = x - mPreviousX;// 计算触控笔Y位移
					if (dx < 0)// 摸左边x为正，摸右边x为负
					{
						if (Constant.CameraX <Constant.MaxCameraMove) {
							if(dx<-Constant.Thold)
							{
								Constant.feeding = false;
							}
							// 移动摄像机的坐标
							Constant.CameraX = Constant.CameraX - dx / Constant.CameraMove_SCALE ;
							Constant.TargetX=Constant.CameraX;
						}
					} else {

						if (Constant.CameraX >-Constant.MaxCameraMove) {
							if(dx>Constant.Thold)
							{

								Constant.feeding =false;
							}
							// 移动摄像机的坐标
							Constant.CameraX = Constant.CameraX - dx / Constant.CameraMove_SCALE ;
							Constant.TargetX=Constant.CameraX;
						}
					}
					// 将摄像机的位置信息存入到矩阵中
					synchronized (lockCamera){
						MatrixState.updateCamera(
								Constant.CameraX, // 人眼位置的X
								Constant.CameraY, // 人眼位置的Y
								Constant.CameraZ, // 人眼位置 的Z
								Constant.TargetX, // 人眼球看的点X
								Constant.TargetY, // 人眼球看的点Y
								Constant.TargetZ, // 人眼球看的点Z
								Constant.UpX,
								Constant.UpY,
								Constant.UpZ);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (Constant.feeding) {
						if (Constant.isFeed) {
							// 把标志位置为false只有食物没有的时候才把isFeed置会true，否则点击第二2下第二节没反应
							Constant.isFeed = false;
							// 通过矩阵变换拿到世界坐标系中的点
							float[] AB = IntersectantUtil.calculateABPosition(
									x, // 触控点X坐标
									y, // 触控点Y坐标
									Constant.SCREEN_WIDTH, // 屏幕宽度
									Constant.SCREEN_HEGHT, // 屏幕长度
									Constant.leftABS, // 视角left、top绝对值
									Constant.topABS,
									Constant.nearABS, // 视角near、far值
									Constant.farABS);
							// Fposition(世界坐标系)
							Vector3f Start = new Vector3f(AB[0], AB[1], AB[2]);//起点
							Vector3f End = new Vector3f(AB[3], AB[4], AB[5]);//终点
							// 判断不位空启动
							if (MySurfaceView.feedfish != null) {

								MySurfaceView.feedfish.startFeed(Start, End);//开始喂食
							}
						}
					}
					break;
			}
			mPreviousY = y;//记录触控笔位置
			mPreviousX = x;//记录触控笔位置
			super.onTouchEvent(e);
		}
		//更新摄像机的方法
		@SuppressLint("NewApi")
		protected void setPreserveEGLContextOnPause(boolean preserve) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				glSurfaceView.setPreserveEGLContextOnPause(preserve);
			}
		}

		protected void setEGLContextClientVersion(int version) {
			glSurfaceView.setEGLContextClientVersion(version);
		}

	}
}
