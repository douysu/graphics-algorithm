package wyf.lxg.fishfood;

import wyf.lxg.Constant.MatrixState;

public class IntersectantUtil {
	/*
	 * 1、通过在屏幕上的触控位置，计算对应的近平面上坐标，
	 * 以便求出AB两点在摄像机坐标系中的坐标
	 * 2、将AB两点在摄像机中坐标系中的坐标乘以摄像机矩阵的逆矩阵，
	 * 以便求得AB两点在世界坐标系中的坐标
	 */
	public static float[] calculateABPosition
	(
			float x,//触屏X坐标
			float y,//触屏Y坐标
			float w,// 屏幕宽度
			float h,// 屏幕高度
			float left,//视角left值
			float top,//视角top值
			float near,//视角near值
			float far//视角far值
	)
	{
		//求视口的坐标中心在原点时，触控点的坐标
		float x0=x-w/2;
		float y0=h/2-y;
		//计算对应的near面上的x、y坐标
		float xNear=2*x0*left/w;
		float yNear=2*y0*top/h;
		//计算对应的far面上的x、y坐标
		float ratio=far/near;
		float xFar=ratio*xNear;
		float yFar=ratio*yNear;
		//摄像机坐标系中A的坐标
		float ax=xNear;
		float ay=yNear;
		float az=-near;
		//摄像机坐标系中B的坐标
		float bx=xFar;
		float by=yFar;
		float bz=-far;
		//通过摄像机坐标系中A、B两点的坐标，求世界坐标系中A、B两点的坐标
		float[] A = MatrixState.fromPtoPreP(new float[] { ax, ay, az });
		float[] B = MatrixState.fromPtoPreP(new float[] { bx, by, bz });
		return new float[] {//返回最终的AB两点坐标
				A[0],A[1],A[2],
				B[0],B[1],B[2]
		};
	}
}