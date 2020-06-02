package wyf.lxg.mywallpaper;

public class LiveWallpaper extends OpenGLES3WallpaperService{
	@Override
	android.opengl.GLSurfaceView.Renderer getNewRenderer() {
		// TODO Auto-generated method stub
		return new MySurfaceView(this);//创建自定义场景渲染器
	}
}
