package com.bn.fbx.core.nonormal;

public class BNThreadGroupNoNormal
{
	//线程数量
	private static final int THREAD_COUNT=3;
	//执行任务的线程组
	private static TaskThreadNoNormal[] threadGroup=new TaskThreadNoNormal[THREAD_COUNT];
	//任务分配锁
	public static Object lock=new Object();
	//静态成员初始化
	static
	{
		for(int i=0;i<THREAD_COUNT;i++)
		{
			threadGroup[i]=new TaskThreadNoNormal(i);
			threadGroup[i].start();
		}
	}
	//添加任务
	public static void addTask(BnggdhDrawNoNormal bd)
	{
		synchronized(lock)
		{
			int min=Integer.MAX_VALUE;
			int curr=-1;
			for(int i=0;i<THREAD_COUNT;i++)
			{
				TaskThreadNoNormal tt=threadGroup[i];
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
	public static void removeTask(BnggdhDrawNoNormal bd)
	{
		synchronized(lock)
		{
			for(int i=0;i<THREAD_COUNT;i++)
			{
				TaskThreadNoNormal tt=threadGroup[i];
				tt.removeTask(bd);
			}
		}
	}
}
