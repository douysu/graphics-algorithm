package wyf.lxg.beike;

import android.opengl.GLES30;

import com.bn.fbx.core.BNModel;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.load.LoadedObjectVertexNormalTexture;
import wyf.lxg.mywallpaper.MySurfaceView;
import wyf.lxg.particle.ParticleSystem;

public class AllBeiKe {
	public MySurfaceView mv;
	// 把Y和Z定义到常量类里面
	LoadedObjectVertexNormalTexture beikes;
	int texld;
	int zhenzhuTex;
	LoadedObjectVertexNormalTexture beiktop;
	SingleZhenZhu zhenzhus;
	public ParticleSystem particleSystem;//珍珠粒子系统
	int textureIdFire;//粒子系统纹理

	float BeiKeAngle=-5.0f;//贝壳的旋转角度
	// 创建鱼食的对象
	public AllBeiKe(int texld, LoadedObjectVertexNormalTexture beikes,LoadedObjectVertexNormalTexture beiktop, int zhenzhuTex, SingleZhenZhu zhenzhus,
					MySurfaceView mv, ParticleSystem particleSystem,int textureIdFire) {
		this.texld=texld;
		this.mv = mv;
		this.zhenzhus = zhenzhus;
		this.beiktop=beiktop;
		// 食物
		this.beikes = beikes;
		this.zhenzhuTex = zhenzhuTex;
		this.particleSystem=particleSystem;
		this.textureIdFire=textureIdFire;
		BeiKeThread bt=new BeiKeThread(this);//贝壳线程
		bt.start();
	}
	// 在绘制的标志位位true的时候开始绘制
	public void drawSelf()
	{
		MatrixState.pushMatrix();//保护矩阵
		MatrixState.translate(0f,-1.5f,0f);//平移
		//绘制下面的贝壳
		MatrixState.pushMatrix();
		MatrixState.translate(-1f,-8.5f,-10f);//平移
		MatrixState.rotate(20, 1, 0, 0);
		MatrixState.scale(0.567f, 0.378f, 0.378f);
		MatrixState.translate(-0.2f,0,0);
		beikes.drawSelf(texld);//下面的贝壳
		MatrixState.popMatrix();
		//绘制粒子系统
		MatrixState.pushMatrix();
		if(particleSystem!=null)//粒子系统
		{
			particleSystem.drawSelf(textureIdFire);
		}
		MatrixState.popMatrix();

		//绘制珍珠
		MatrixState.pushMatrix();
		MatrixState.translate(-1.05f,-8.6f,-9.0f);//平移
		MatrixState.scale(Constant.zhenZhuScaleNum,Constant.zhenZhuScaleNum,Constant.zhenZhuScaleNum);
		zhenzhus.drawSelf(zhenzhuTex);//珍珠
		MatrixState.popMatrix();

		//绘制上面的贝壳
		MatrixState.pushMatrix();//保护矩阵
		MatrixState.translate(-1f,-8.2f,-9.8f);//平移
		MatrixState.rotate(20, 1, 0, 0);
		MatrixState.scale(0.567f, 0.378f, 0.378f);
		MatrixState.translate(-0.2f,0,0);
		MatrixState.rotate(BeiKeAngle,1,0,0);
		//关闭深度检测
		beiktop.drawSelf(texld);//上面的贝壳
		//开启深度检测
		MatrixState.popMatrix();//保护矩阵

		MatrixState.popMatrix();//恢复矩阵
	}

}