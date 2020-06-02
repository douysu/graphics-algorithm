package wyf.lxg.bubble;

import wyf.lxg.Constant.MatrixState;
import wyf.lxg.mywallpaper.MySurfaceView;

public class SingleBubble  implements Comparable<SingleBubble>
{
	Bubble bubble;//气泡
	float cuerrentX=0;
	float cuerrentY=1;
	float cuerrentZ=0;

	float border;
	int TexId; //纹理id
	public SingleBubble(MySurfaceView mySurfaceView,int TexId)
	{

		this.TexId=TexId;
		bubble=new Bubble(mySurfaceView);	//创建气泡对象
		newposition(-1);

	}
	public void drawSelf()
	{
		//顶面
		MatrixState.pushMatrix();
		MatrixState.translate(cuerrentX, cuerrentY, cuerrentZ);
		bubble.drawSelf(TexId);
		MatrixState.popMatrix();

	}
	public void bubbleMove(float x,float y) {
		//气泡移动
		this.cuerrentY += 0.08f;
		this.cuerrentX +=(float)(0.01*y);
		this.cuerrentZ +=(float)(0.015*y)+0.1;
		if (this.cuerrentY > border)
		{
			newposition(x);//重置气泡位置
		}
	}

	public void newposition(float x) {
		if(x==-1)//第一处气泡位置
		{
			cuerrentX = 2.6f;
			cuerrentY = -11.5f;
			cuerrentZ = -26.5f;
		}else if(x==1){//第二处气泡位置
			cuerrentX = 5f;
			cuerrentY = -12.5f;
			cuerrentZ = -25f;
		}else if(x==0){//第三处气泡位置
			cuerrentX = -5f;
			cuerrentY = -16f;
			cuerrentZ = -26.2f;
		}
		//气泡上升的高度
		border = (float) (2 * Math.random() + 3);
	}
	public int compareTo(SingleBubble another) {
		return ((this.cuerrentZ-another.cuerrentZ)==0)?0:((this.cuerrentZ-another.cuerrentZ)>0)?1:-1;
	}
}
