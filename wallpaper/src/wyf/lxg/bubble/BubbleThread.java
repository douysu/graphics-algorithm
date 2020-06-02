package wyf.lxg.bubble;

//定时运动所有鱼类的线程
public class BubbleThread extends Thread {
	float x;
	float y;
	//	float num;
	boolean flag = true;
	BubbleControl Bcl;//气泡的控制类
	public BubbleThread(BubbleControl Bcl) {
		this.Bcl=Bcl;
	}
	public void run()
	{
		// 循环定时移动气泡
		while (flag) {
			try {
				for(int i=0;i<Bcl.BubbleSingle.size();i++)
				{
					if((i+3)%3==0)//将气泡的总数量，切分为3份
					{
						if(((i+3)/3)%2==0)//将切分出来的一个气泡队列，进行奇偶判断，为气泡的x、z轴方向偏移左准备
						{
							y=1;//偶数位气泡标志位为1
						}else{
							y=-1;//奇数位气泡标志位-1
						}
						x=1;//第一处气泡位置队列标志位
					}else if((i+3)%3==1){//将气泡的总数量，切分为3份
						if(((i+3)/3)%2==0)//将切分出来的一个气泡队列，进行奇偶判断，为气泡的x、z轴方向偏移左准备
						{
							y=1;//偶数位气泡标志位为1
						}else{
							y=-1;//奇数位气泡标志位-1
						}
						x=-1;//第二处气泡位置队列标志位
					}else if((i+3)%3==2)//将气泡的总数量，切分为3份
					{
						if(((i+3)/3)%2==0)//将切分出来的一个气泡队列，进行奇偶判断，为气泡的x、z轴方向偏移左准备
						{
							y=1;//偶数位气泡标志位为1
						}else{
							y=-1;//奇数位气泡标志位-1
						}
						x=0;//第二处气泡位置队列标志位
					}
					Bcl.BubbleSingle.get(i).bubbleMove(x,y);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
