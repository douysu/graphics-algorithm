package com.bn.fbx.core.nonormal;

import java.util.ArrayList;

public class TaskThreadNoNormal extends Thread
{
	public ArrayList<BnggdhDrawNoNormal> taskGroup=new ArrayList<BnggdhDrawNoNormal>();
	public Object lock=new Object();

	public TaskThreadNoNormal(int i)
	{
		this.setName("TaskThread"+i);
	}

	public void addTask(BnggdhDrawNoNormal bd)
	{
		synchronized(lock)
		{
			taskGroup.add(bd);
		}
	}

	public void removeTask(BnggdhDrawNoNormal bd)
	{
		synchronized(lock)
		{
			taskGroup.remove(bd);
		}
	}

	public void run()
	{
		while(true)
		{
			synchronized(lock)
			{
				for(BnggdhDrawNoNormal bd:taskGroup)
				{
					bd.updateTime();
				}
			}
			try {Thread.sleep(15);} catch (InterruptedException e){e.printStackTrace();}
		}
	}
}
