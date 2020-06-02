package wyf.lxg.fish;

import com.bn.fbx.core.BNModel;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.Constant.Vector3f;


public class SingleFish {
	// 初始化单个鱼的位置，速度，受力情况
	public Vector3f position;
	public Vector3f speed;
	public Vector3f force;
	// 鱼和鱼食之之间的吸引力
	public Vector3f attractforce;
	// 鱼的质量，鱼本身受到的力与鱼的质量成反比
	public float weight;
	// 鱼类的身体的转动角度Z轴和Y轴!
	float yAngle;
	float zAngle;
	float tempY;
	float tempZ;
	// 初始化鱼类对象
	BNModel md;
	int texid;
	float ScaleNum;
	float time;//位置    速度     力     吸引力      重力
	public SingleFish(BNModel  mx,int texid,
					  Vector3f Position, Vector3f Speed, Vector3f force,
					  Vector3f attractforce, float weight, float ScaleNum) {
		this.md=mx;
		this.texid=texid;
		this.position = Position;
		this.speed = Speed;
		this.force = force;
		this.attractforce = attractforce;
		this.weight = weight;
		this.ScaleNum = ScaleNum;
	}
	public void drawSelf() {

		MatrixState.pushMatrix();
		MatrixState.translate(this.position.x, this.position.y, this.position.z);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);
		if (md != null) {
			MatrixState.scale(ScaleNum, ScaleNum, ScaleNum);
			this.md.draw(texid);
		}
		MatrixState.popMatrix();
	}
	// 鱼类的游动的方法，根据当前的鱼类位置以及速度来计算鱼的下一个位置!
	public void fishMove() {
		/**
		 * 一会注意判断x，z速度同时为0 的情况
		 */
		// zAngle的计算
		float fz = (speed.x * speed.x + speed.y * 0 + speed.z * speed.z);
		// 分母
		float fm = (float) (Math.sqrt(speed.x * speed.x + speed.y * speed.y
				+ speed.z * speed.z) * Math.sqrt(speed.x * speed.x + speed.z
				* speed.z));
		//cos值
		float angle = fz / fm;
		// 绕Z轴的旋转角度
		tempZ = (float) (180f / Math.PI) * (float) Math.acos(angle);//反余弦  弧长



		// yAngle的计算
		fz = (speed.x * Constant.initialize.x + speed.z * Constant.initialize.z);
		// 分母
		fm = (float) (Math.sqrt(Constant.initialize.x * Constant.initialize.x
				+ Constant.initialize.z * Constant.initialize.z) * Math
				.sqrt(speed.x * speed.x + speed.z * speed.z));
		// cos值
		angle = fz / fm;
		// 绕Y轴的旋转角度
		tempY = (float) (180f / Math.PI) * (float) Math.acos(angle);


		// 拿到夹角根据Speed.y的正负性来确定夹角的正负性（上述计算的出的角度均为正值）
		if (speed.y <= 0) {
			zAngle = -tempZ;
		} else {
			zAngle = tempZ;
		}
		// 拿到夹角根据Speed.z的正负性来确定夹角的正负性（上述计算的出的角度均为正值）
		if (speed.z <= 0) {
			yAngle = tempY;
		} else {
			yAngle = -tempY;
		}
		// 动态的修改鱼的速度，试探性的检测鱼的速度，如果超过规定的范围则鱼的速度不在增加
		// 鱼和外力
		//方向相同为加速，方向相反为减速,当速度达到最大值的时候，通过判断速度的最大值来确保鱼的速度不会增加
		if (Math.abs(speed.x + force.x) < Constant.MaxSpeed)
		{
			speed.x += force.x;
		}
		if (Math.abs(speed.y + force.y) < Constant.MaxSpeed)
		{
			speed.y += force.y;
		}
		if (Math.abs(speed.z + force.z) < Constant.MaxSpeed)
		{
			speed.z += force.z;
		}

		// 动态的修改鱼的速度
		// 鱼和鱼与鱼食之间的吸引力
		//与外力的原理相同
		//力越大速度也就越大
		if (Math.abs(speed.x + attractforce.x) < Constant.MaxSpeed)
		{
			speed.x += attractforce.x;
		}
		if (Math.abs(speed.y + attractforce.y) < Constant.MaxSpeed)
		{
			speed.y += attractforce.y;
		}
		if (Math.abs(speed.z + attractforce.z) < Constant.MaxSpeed)
		{
			speed.z += attractforce.z;
		}
		//加上速度，鱼的位置改变
		// 改变鱼的位置
		position.plus(speed);
		//防止鱼穿过地面

		// 每次计算每条鱼的受力之后，把所受的力置零
		// 外力
		this.force.x = 0;
		this.force.y = 0;
		this.force.z = 0;
		// 鱼食对鱼的吸引力
		attractforce.x = 0;
		attractforce.y = 0;
		attractforce.z = 0;
	}
}
