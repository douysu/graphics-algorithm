package com.bn.fbx.core.normal;

public class BNThreadGroup
{
	//线程数量
	private static final int THREAD_COUNT=3;
	//执行任务的线程组
	private static TaskThread[] threadGroup=new TaskThread[THREAD_COUNT];
	//任务分配锁
	public static Object lock=new Object();
	//静态成员初始化
	static
	{
		for(int i=0;i<THREAD_COUNT;i++)
		{
			threadGroup[i]=new TaskThread(i);
			threadGroup[i].start();
		}
	}
	//添加任务
	public static void addTask(BnggdhDraw bd)
	{
		synchronized(lock)
		{
			int min=Integer.MAX_VALUE;
			int curr=-1;
			for(int i=0;i<THREAD_COUNT;i++)
			{
				TaskThread tt=threadGroup[i];
				if(tt.taskGroup.size()<min)
				{
					min=tt.taskGroup.size();
					curr=i;
				}
			}
			threadGroup[curr].addTask(bd);
		}
	}

	//移除任务
	public static void removeTask(BnggdhDraw bd)
	{
		synchronized(lock)
		{
			for(int i=0;i<THREAD_COUNT;i++)
			{
				TaskThread tt=threadGroup[i];
				tt.removeTask(bd);
			}
		}
	}
}
