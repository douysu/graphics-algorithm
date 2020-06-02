package com.bn.fbx.core;

import java.io.IOException;
import java.io.InputStream;

import com.bn.fbx.core.nonormal.BnggdhDrawNoNormal;
import com.bn.fbx.core.normal.BnggdhDraw;

import wyf.lxg.mywallpaper.MySurfaceView;

public class BNModel
{
	private float onceTime;// 一次动画所需的时间
	private boolean isNormal;//是否有法向量
	private float dt;//步长
	private float dtFactor;//速率

	private BnggdhDraw cd;// 带法向量的模型类
	private BnggdhDrawNoNormal cdnn;// 不带法向量的模型类


	/**
	 *
	 * @param sourceName	模型名称
	 * @param picName		图片名称
	 * @param isNormal		是否有光照
	 * @param dtFactor		速率，范围在0-1
	 * @param r				资源类引用
	 */
	public BNModel(String sourceName, String picName, boolean isNormal,
				   float dtFactor, MySurfaceView mv) {
		try {
			InputStream is = mv.getResources().getAssets().open(sourceName);
			if (isNormal == true) {
				cd = new BnggdhDraw(is, mv, picName);
				onceTime = cd.maxKeytime;
			} else {
				cdnn = new BnggdhDrawNoNormal(is, mv, picName);
				onceTime = cdnn.maxKeytime;
			}
			this.dtFactor = dtFactor;
			this.dt = dtFactor * onceTime;
			this.isNormal = isNormal;
			if (isNormal == true)
			{
				cd.setDt(this.dt);
			} else {
				cdnn.setDt(this.dt);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绘制方法
	 */
	public void draw(int texid)
	{
		if (isNormal == true)
		{
			cd.draw(texid);
		} else {
			cdnn.draw();
		}

	}

	/**
	 * 获取速率
	 * @return
	 */
	public float getDtFactor() {
		return dtFactor;
	}

	/**
	 * 设置速率
	 * @param dtFactor
	 */
	public void setDtFactor(float dtFactor) {
		if(dtFactor >= 0 && dtFactor <= 1){
			this.dtFactor = dtFactor;
			this.dt = dtFactor * onceTime;
		}
	}

	/**
	 * 设置当前时间
	 * @param time
	 */
	public void setTime(float time)
	{
		if(time >= 0 && time <= this.onceTime)
		{
			if (isNormal == true)
			{
				cd.time=time;
			} else {
				cdnn.time=time;
			}
		}
	}

	/**
	 * 获取当前时间
	 * @return
	 */
	public float getTime()
	{
		if (isNormal == true)
		{
			return cd.time;
		} else {
			return cdnn.time;
		}
	}

	/**
	 * 获取该模型的骨骼动画总的时间
	 * @return
	 */
	public float getOnceTime(){
		return this.onceTime;
	}
}
