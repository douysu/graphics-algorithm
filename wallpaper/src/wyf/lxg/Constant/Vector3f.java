package wyf.lxg.Constant;

public class Vector3f {
	public float x;
	public float y;
	public float z;
	// 构造器
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	// 向量的长度的模
	public float Vectormodule() {
		float Vectormodule = (float) Math.sqrt(this.x * this.x + this.y
				* this.y + this.z * this.z);
		return Vectormodule;
	}
	// 向量的加法，速度与鱼受到的力的做加法得到新的速度或者位移与速度做加法的到新的位移。
	public void plus(Vector3f Vector) {
		this.x += Vector.x;
		this.y += Vector.y;
		this.z += Vector.z;
	}
	// 向量进行一定的比例缩放
	public void ChangeStep(float Length) {
		this.x = this.x / Length;
		this.y = this.y / Length;
		this.z = this.z / Length;
	}

	// 获取力的大小，与质量成反比的关系
	public void getforce(float weight) {
		if (weight != 0) {
			this.x = this.x / weight;
			this.y = this.y / weight;
			this.z = this.z / weight;
		}
	}
	// 拿到指定半径的向量
	public Vector3f getNeedradiusvector(float R)
	{
		Vector3f Vnd = new Vector3f(this.x, this.y, this.z);
		// 此向量的模长
		float Length = Vnd.Vectormodule();
		if (Length != 0) {
			// 变成单位向量
			Vnd.ChangeStep(Length);
		}
		// 变成指定模长的向量
		Vnd.ChangeStep(1 / R);
		return Vnd;
	}

	// 恒力的向量归一化
	public Vector3f cutGetforce(Vector3f Vector)
	{
		/**
		 * 把当前向量与另一个向量做减法的到同一个新的向量，此向量为由另一条鱼指向本条鱼的向量，代表着力的方向并给他归一化
		 */
		Vector3f Vtr = new Vector3f(this.x - Vector.x, this.y - Vector.y,
				this.z - Vector.z);
//		float Length = Vtr.Vectormodule();
//		if (Length != 0)
//		{
//			Vtr.ChangeStep(Length);
//		}
		return Vtr;
	}

	// 拿到从到Position到ConstantPositon的向量
	public Vector3f cutPc(Vector3f Vector)
	{
		//把当前向量与另一个向量做减法的到同一个新的向量，此向量为由另一条鱼指向本条鱼的向量，代表着力的方向并给他归一化
		Vector3f Vtr = new Vector3f(this.x - Vector.x, this.y - Vector.y,
				this.z - Vector.z);
		return Vtr;
	}

	// 向量的减法主要是为了拿到两个鱼的连线向量只有在一定的距才会产生力的作用
	public Vector3f cut(Vector3f Vector, float MinDistances)
	{
		// 把当前向量与另一个向量做减法的到同一个新的向量，此向量为由另一条鱼指向本条鱼的向量，代表着力的方向并给他归一化
		Vector3f Vtr = new Vector3f(this.x - Vector.x, this.y - Vector.y,
				this.z - Vector.z);
		float Length = Vtr.Vectormodule();
		// 判断一下计算一下两条鱼的距离是不是在规定的阎值范围内，如果在则力存在将Vtr归一化之后就返回即可
		// 如果超出了一定的范围则不进行归一化直接将Vtr的个个分量全都变成0，返回就当次方向的力是不存在的
		// 随着两条鱼的距离的增加力在逐渐的变小大于Maxdistances之后鱼和鱼之间的力就消失了并且距离越近力愈大
		if (Length <= MinDistances){
			// 向量归一化，并且并且大小与鱼之间的距离成反比并且进行比例缩放
			if (Length != 0)
			{
				//缩小Length * Length倍数
				Vtr.ChangeStep(Length * Length);
			}
		}
		else
		{
			// 两条鱼的距离超出一定范围力消失
			Vtr.x = 0;
			Vtr.y = 0;
			Vtr.z = 0;
		}
		//如果在范围内将两条鱼之间的距离缩小模的平方倍
		//不在范围内的话，范围力消失
		return Vtr;
	}
}
