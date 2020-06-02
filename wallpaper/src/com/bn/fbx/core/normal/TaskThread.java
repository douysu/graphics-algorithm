package com.bn.fbx.core.normal;

import java.util.ArrayList;

public class TaskThread extends Thread
{
	public ArrayList<BnggdhDraw> taskGroup=new ArrayList<BnggdhDraw>();
	public Object lock=new Object();

	public TaskThread(int i)
	{
		this.setName("TaskThread"+i);
	}

	public void addTask(BnggdhDraw bd)
	{
		synchronized(lock)
		{
			taskGroup.add(bd);
		}
	}

	public void removeTask(BnggdhDraw bd)
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
				for(BnggdhDraw bd:taskGroup)
				{
					bd.updateTime();
				}
			}
			try {Thread.sleep(15);} catch (InterruptedException e){e.printStackTrace();}
		}
	}
}
