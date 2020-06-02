package com.bn.util;//声明包
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import android.content.res.Resources;
import android.util.Log;

import com.bn.organ.LoadedObjectBody;
import com.bn.activty.MySurfaceView;
import com.bn.organ.LoadedObjectOrgan;

public class LoadUtil
{
	//求两个向量的叉积
	public static float[] getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2)
	{
		//求出两个矢量叉积矢量在XYZ轴的分量ABC
		float A=y1*z2-y2*z1;
		float B=z1*x2-z2*x1;
		float C=x1*y2-x2*y1;

		return new float[]{A,B,C};
	}

	//向量规格化
	public static float[] vectorNormal(float[] vector)
	{
		//求向量的模
		float module=(float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
		return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
	}

	//从obj文件中加载携带顶点信息的物体，并自动计算每个顶点的平均法向量
	public static LoadedObjectBody LoadedObjectBody(String fname, Resources r, MySurfaceView mv, int programId)
	{
		//加载后物体的引用
		LoadedObjectBody lo=null;
		//原始顶点坐标列表--直接从obj文件中加载
		ArrayList<Float> alv=new ArrayList<Float>();
		//结果顶点编号列表--根据面的信息从文件中加载
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();
		//结果顶点坐标列表--按面组织好
		ArrayList<Float> alvResult=new ArrayList<Float>();
		//平均前各个编号的点的法向量集合的Map,此HashMap的key为点的编号，
		//value为点所在的各个面的法向量的集合
		HashMap<Integer,HashSet<Normal>> hmn=new HashMap<Integer,HashSet<Normal>>();

		try
		{
			InputStream in=r.getAssets().open(fname);
			InputStreamReader isr=new InputStreamReader(in);
			BufferedReader br=new BufferedReader(isr);
			String temps=null;

			//扫描文件，根据行类型的不同执行不同的处理逻辑
			while((temps=br.readLine())!=null)
			{//读取一行文本

				String[] tempsa=temps.split("[ ]+");//将文本行用空格符切分
				if(tempsa[0].trim().equals("v"))
				{//顶点坐标行
					//若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
					alv.add(Float.parseFloat(tempsa[1]));
					alv.add(Float.parseFloat(tempsa[2]));
					alv.add(Float.parseFloat(tempsa[3]));
				}
				else if(tempsa[0].trim().equals("f"))
				{//面数据行
		      		/*
		      		 *若为三角形面行则根据 组成面的顶点的索引从原始顶点坐标列表中
		      		 *提取相应的顶点坐标值添加到结果顶点坐标列表中，同时根据三个
		      		 *顶点的坐标计算出此面的法向量并添加到平均前各个索引对应的点
		      		 *的法向量集合组成的Map中
		      		*/

					int[] index=new int[3];//此面中3个顶点编号值的数组

					//计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
					index[0]=Integer.parseInt(tempsa[1].split("/")[0])-1;//得到顶点编号
					//将三角形第1个顶点的x、y、z坐标取出
					float x0=alv.get(3*index[0]);
					float y0=alv.get(3*index[0]+1);
					float z0=alv.get(3*index[0]+2);
					alvResult.add(x0);
					alvResult.add(y0);
					alvResult.add(z0);

					//计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
					index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
					float x1=alv.get(3*index[1]);
					float y1=alv.get(3*index[1]+1);
					float z1=alv.get(3*index[1]+2);
					alvResult.add(x1);
					alvResult.add(y1);
					alvResult.add(z1);

					//计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
					index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
					float x2=alv.get(3*index[2]);
					float y2=alv.get(3*index[2]+1);
					float z2=alv.get(3*index[2]+2);
					alvResult.add(x2);
					alvResult.add(y2);
					alvResult.add(z2);

					//记录此三角形面3个顶点的索引
					alFaceIndex.add(index[0]);
					alFaceIndex.add(index[1]);
					alFaceIndex.add(index[2]);

					//通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
					//求三角形中第一个点到第二个点的向量
					float vxa=x1-x0;
					float vya=y1-y0;
					float vza=z1-z0;
					//求三角形中第一个点到第三个点的向量
					float vxb=x2-x0;
					float vyb=y2-y0;
					float vzb=z2-z0;
					//通过求两个向量的叉积计算出此三角形面的法向量
					float[] vNormal=vectorNormal(getCrossProduct
							(
									vxa,vya,vza,vxb,vyb,vzb
							));
					//将此三角形面的法向量记录到此面3个顶点各自的法向量集合中
					for(int tempInxex:index)
					{
						//获取指定编号顶点的法向量集合
						HashSet<Normal> hsn=hmn.get(tempInxex);
						if(hsn==null)
						{	//若集合不存在则创建
							hsn=new HashSet<Normal>();
						}
						//将此点的法向量添加到集合中
						//由于Normal类重写了equals方法，因此同样的法向量不会重复出现在此点
						//对应的法向量集合中
						hsn.add(new Normal(vNormal[0],vNormal[1],vNormal[2]));//将此点的法向量添加到集合中
						//将法向量集合放进HashMap中
						hmn.put(tempInxex, hsn);
					}
				}
			}

			//生成顶点数组
			int size=alvResult.size();
			float[] vXYZ=new float[size];
			for(int i=0;i<size;i++)
			{
				vXYZ[i]=alvResult.get(i);
			}

			//生成法向量数组
			float[] nXYZ=new float[alFaceIndex.size()*3];//用于存放法向量数据的数组
			int c=0;//法向量数组索引
			for(Integer i:alFaceIndex)
			{//对结果序列中每个顶点的编号循环
				//根据当前的编号从Map中取出一个顶点的法向量的集合
				HashSet<Normal> hsn=hmn.get(i);
				//求出集合中法向量的平均法向量
				float[] tn=Normal.getAverage(hsn);
				//将计算出的平均法向量存放到法向量数组中
				nXYZ[c++]=tn[0];
				nXYZ[c++]=tn[1];
				nXYZ[c++]=tn[2];
			}
			//将顶点和法向量数据传给LoadedObjectVertexNormal类
			//创建加载物体对象
			lo=new LoadedObjectBody(mv,vXYZ,nXYZ,programId);
		}
		catch(Exception e)
		{
			Log.d("load error", "load error");
			e.printStackTrace();
		}
		return lo;//返回创建的物体对象的引用
	}
	//从obj文件中加载携带顶点信息的物体，并自动计算每个顶点的平均法向量
	public static LoadedObjectOrgan LoadedObjectOrgan(String fname, Resources r, MySurfaceView mv, int programId)
	{
		//加载后物体的引用
		LoadedObjectOrgan lo=null;
		//原始顶点坐标列表--直接从obj文件中加载
		ArrayList<Float> alv=new ArrayList<Float>();
		//结果顶点编号列表--根据面的信息从文件中加载
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();
		//结果顶点坐标列表--按面组织好
		ArrayList<Float> alvResult=new ArrayList<Float>();
		//平均前各个编号的点的法向量集合的Map,此HashMap的key为点的编号，
		//value为点所在的各个面的法向量的集合
		HashMap<Integer,HashSet<Normal>> hmn=new HashMap<Integer,HashSet<Normal>>();

		try
		{
			InputStream in=r.getAssets().open(fname);
			InputStreamReader isr=new InputStreamReader(in);
			BufferedReader br=new BufferedReader(isr);
			String temps=null;

			//扫描文件，根据行类型的不同执行不同的处理逻辑
			while((temps=br.readLine())!=null)
			{//读取一行文本

				String[] tempsa=temps.split("[ ]+");//将文本行用空格符切分
				if(tempsa[0].trim().equals("v"))
				{//顶点坐标行
					//若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
					alv.add(Float.parseFloat(tempsa[1]));
					alv.add(Float.parseFloat(tempsa[2]));
					alv.add(Float.parseFloat(tempsa[3]));
				}
				else if(tempsa[0].trim().equals("f"))
				{//面数据行
		      		/*
		      		 *若为三角形面行则根据 组成面的顶点的索引从原始顶点坐标列表中
		      		 *提取相应的顶点坐标值添加到结果顶点坐标列表中，同时根据三个
		      		 *顶点的坐标计算出此面的法向量并添加到平均前各个索引对应的点
		      		 *的法向量集合组成的Map中
		      		*/

					int[] index=new int[3];//此面中3个顶点编号值的数组

					//计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
					index[0]=Integer.parseInt(tempsa[1].split("/")[0])-1;//得到顶点编号
					//将三角形第1个顶点的x、y、z坐标取出
					float x0=alv.get(3*index[0]);
					float y0=alv.get(3*index[0]+1);
					float z0=alv.get(3*index[0]+2);
					alvResult.add(x0);
					alvResult.add(y0);
					alvResult.add(z0);

					//计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
					index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
					float x1=alv.get(3*index[1]);
					float y1=alv.get(3*index[1]+1);
					float z1=alv.get(3*index[1]+2);
					alvResult.add(x1);
					alvResult.add(y1);
					alvResult.add(z1);

					//计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
					index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
					float x2=alv.get(3*index[2]);
					float y2=alv.get(3*index[2]+1);
					float z2=alv.get(3*index[2]+2);
					alvResult.add(x2);
					alvResult.add(y2);
					alvResult.add(z2);

					//记录此三角形面3个顶点的索引
					alFaceIndex.add(index[0]);
					alFaceIndex.add(index[1]);
					alFaceIndex.add(index[2]);

					//通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
					//求三角形中第一个点到第二个点的向量
					float vxa=x1-x0;
					float vya=y1-y0;
					float vza=z1-z0;
					//求三角形中第一个点到第三个点的向量
					float vxb=x2-x0;
					float vyb=y2-y0;
					float vzb=z2-z0;
					//通过求两个向量的叉积计算出此三角形面的法向量
					float[] vNormal=vectorNormal(getCrossProduct
							(
									vxa,vya,vza,vxb,vyb,vzb
							));
					//将此三角形面的法向量记录到此面3个顶点各自的法向量集合中
					for(int tempInxex:index)
					{
						//获取指定编号顶点的法向量集合
						HashSet<Normal> hsn=hmn.get(tempInxex);
						if(hsn==null)
						{	//若集合不存在则创建
							hsn=new HashSet<Normal>();
						}
						//将此点的法向量添加到集合中
						//由于Normal类重写了equals方法，因此同样的法向量不会重复出现在此点
						//对应的法向量集合中
						hsn.add(new Normal(vNormal[0],vNormal[1],vNormal[2]));//将此点的法向量添加到集合中
						//将法向量集合放进HashMap中
						hmn.put(tempInxex, hsn);
					}
				}
			}

			//生成顶点数组
			int size=alvResult.size();
			float[] vXYZ=new float[size];
			for(int i=0;i<size;i++)
			{
				vXYZ[i]=alvResult.get(i);
			}

			//生成法向量数组
			float[] nXYZ=new float[alFaceIndex.size()*3];//用于存放法向量数据的数组
			int c=0;//法向量数组索引
			for(Integer i:alFaceIndex)
			{//对结果序列中每个顶点的编号循环
				//根据当前的编号从Map中取出一个顶点的法向量的集合
				HashSet<Normal> hsn=hmn.get(i);
				//求出集合中法向量的平均法向量
				float[] tn=Normal.getAverage(hsn);
				//将计算出的平均法向量存放到法向量数组中
				nXYZ[c++]=tn[0];
				nXYZ[c++]=tn[1];
				nXYZ[c++]=tn[2];
			}
			//将顶点和法向量数据传给LoadedObjectVertexNormal类
			//创建加载物体对象
			lo=new LoadedObjectOrgan(mv,vXYZ,nXYZ,programId);
		}
		catch(Exception e)
		{
			Log.d("load error", "load error");
			e.printStackTrace();
		}
		return lo;//返回创建的物体对象的引用
	}
}
