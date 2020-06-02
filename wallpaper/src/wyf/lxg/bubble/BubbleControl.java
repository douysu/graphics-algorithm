package wyf.lxg.bubble;

import java.util.ArrayList;
import java.util.Collections;

import wyf.lxg.Constant.Constant;
import wyf.lxg.Constant.MatrixState;
import wyf.lxg.mywallpaper.MySurfaceView;
public class BubbleControl {
	// 气泡类的列表
	public ArrayList<SingleBubble> BubbleSingle = new ArrayList<SingleBubble>();
	// 气泡的纹理ID
	int texld;
	MySurfaceView mv;
	public BubbleControl(MySurfaceView mv,int texld ) {
		this.mv=mv;
		// 拿到ID
		this.texld = texld;

		// 创建气泡
		for (int i = 0; i <Constant.BUBBLE_NUM; i++) {
			BubbleSingle.add(new SingleBubble(mv,this.texld));//添加气泡对象
		}
		// 创建并启动气泡移动线程
		BubbleThread Bgt = new BubbleThread(this);
		Bgt.start();
	}

	public void drawSelf() {
		try {
			Collections.sort(this.BubbleSingle);
			// 绘制气泡
			for (int i = 0; i < this.BubbleSingle.size(); i++) {
				MatrixState.pushMatrix();//保护矩阵
				BubbleSingle.get(i).drawSelf();//绘制气球
				MatrixState.popMatrix();//恢复矩阵
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
