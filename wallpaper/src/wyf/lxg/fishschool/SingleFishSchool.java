package wyf.lxg.fishschool;

import com.bn.fbx.core.BNModel;
import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.Constant.Vector3f;

public class SingleFishSchool {
	// 初始化鱼
	public Vector3f position;
	public Vector3f speed;
	public Vector3f force;
	// 每个鱼有一个自己的位置，从他自己到该点位有一个指向该方向的力的作用，当鱼离开该位置时，力就会产生
	public Vector3f ConstantPosition = new Vector3f(0, 0, 0);
	// 鱼受到的指向固定点位的大小恒定的力
	public Vector3f ConstantForce;
	// 鱼的质量，鱼本身受到的力与鱼的质量成反比
	public float weight;
	// 鱼类的身体的转动角度Z轴和Y轴!
	float yAngle;
	float zAngle;
	float tempY;
	float tempZ;
	BNModel mt;
	float scaleNum;//缩小的倍数
	int texid;
	// 构造器
	//force为鱼与鱼之间的力，ConstantForce为固定位置对鱼的吸引力
	public SingleFishSchool(BNModel md,int texid,
							Vector3f Position, Vector3f Speed, Vector3f force,
							Vector3f ConstantForce, float weight,float scaleNum) {
		this.texid=texid;
		this.position = Position;
		this.speed = Speed;
		this.force = force;
		this.weight = weight;
		this.mt=md;
		this.ConstantPosition.x = Position.x;//初始化固定点位置
		this.ConstantPosition.y = Position.y;
		this.ConstantPosition.z = Position.z;
		this.ConstantForce = ConstantForce;
		this.scaleNum=scaleNum;
	}
	// drawSelf方法！
	public void drawSelf() {
		MatrixState.pushMatrix();//保护矩阵
		MatrixState.translate(this.position.x, this.position.y, this.position.z);//平移
		MatrixState.rotate(yAngle, 0, 1, 0);//绕y轴旋转一定角度
		MatrixState.rotate(-zAngle, 0, 0, 1);//绕z轴旋转一定角度
		//画鱼群
		if (mt != null) {
			MatrixState.scale(scaleNum,scaleNum, scaleNum);
			this.mt.draw(texid);
		}
		MatrixState.popMatrix();//恢复矩阵
	}
	// 鱼类的游动的方法，根据当前的鱼类位置以及速度来计算鱼的下一个位置!
	public void fishschoolMove() {
		/**
		 * 一会注意判断x，z速度同时为0 的情况
		 */
		// 判断速度防止产生分母为零的现象产生
		//这里可能回出现错误。
		if (speed.x == 0 && speed.z == 0 && speed.y > 0) {
			tempZ = -90;
			tempY = 0;
		} else if (speed.x == 0 && speed.z == 0 && speed.y < 0) {
			tempZ = 90;
			tempY = 0;
		} else if (speed.x == 0 && speed.z == 0 && speed.y == 0) {
			tempZ = 90;
			tempY = 0;
		} else {
			float fz = (speed.x * speed.x + speed.y * 0 + speed.z * speed.z);
			// 分母
			float fm = (float) (Math.sqrt(speed.x * speed.x + speed.y * speed.y
					+ speed.z * speed.z) * Math.sqrt(speed.x * speed.x
					+ speed.z * speed.z));
			// cos值
			float zhi = fz / fm;
			// 反三角函数
			tempZ = (float) (180f / Math.PI) * (float) Math.acos(zhi);
			fz = (speed.x * Constant.initializeschool.x + speed.z
					* Constant.initializeschool.z);
			// 分母
			fm = (float) (Math.sqrt(Constant.initializeschool.x
					* Constant.initializeschool.x + Constant.initializeschool.z
					* Constant.initializeschool.z) * Math.sqrt(speed.x * speed.x
					+ speed.z * speed.z));
			// cos值
			zhi = fz / fm;
			// 反三角函数
			tempY = (float) (180f / Math.PI) * (float) Math.acos(zhi);
		}
		// 拿到夹角根据speed.y的正负性来确定夹角的正负性（上述计算的出的角度均为正值）
		if (speed.y <= 0) {
			zAngle = tempZ;
		} else {
			zAngle = -tempZ;
		}
		// 拿到夹角根据speed.z的正负性来确定夹角的正负性（上述计算的出的角度均为正值）
		if (speed.z <= 0) {
			yAngle = tempY;
		} else {
			yAngle = -tempY;
		}

		// 动态的修改鱼的速度，试探性的检测鱼的速度，如果超过规定的范围则鱼的速度不在增加
		if (Math.abs(speed.x + force.x) < Constant.SchoolMaxSpeed) {
			speed.x += force.x;
		}
		if (Math.abs(speed.y + force.y) < Constant.SchoolMaxSpeed) {
			speed.y += force.y;
		}
		if (Math.abs(speed.z + force.z) < Constant.SchoolMaxSpeed) {
			speed.z += force.z;
		}

		if (Math.abs(speed.x + ConstantForce.x) < Constant.SchoolMaxSpeed) {
			speed.x += ConstantForce.x;
		}else if (speed.x<0){
			speed.x=-0.05f;
		}else if(speed.x>0){
			speed.x=0.05f;
		}
		if (Math.abs(speed.y + ConstantForce.y) < Constant.SchoolMaxSpeed) {
			speed.y += ConstantForce.y;
		}

		if (Math.abs(speed.z + ConstantForce.z) < Constant.SchoolMaxSpeed) {
			speed.z += ConstantForce.z;
		}else if (speed.z<0){
			speed.z=-0.05f;
		}else if(speed.z>0){
			speed.z=0.05f;
		}
		/**
		 * 动态的修改鱼的位置
		 */
		position.plus(speed);//根据速度改变鱼的位置
		// 每次计算每条鱼的受力之前，把所受的力置零
		this.force.x = 0;
		this.force.y = 0;
		this.force.z = 0;
	}
}
