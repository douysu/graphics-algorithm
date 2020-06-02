package com.bn.manager;

import com.bn.util.ShaderUtil;

import android.content.res.Resources;

public class ShaderManager
{
	final static int shaderCount=2;
	final static String[][] shaderName=
			{
					{"shader/vertex_body.sh", "shader/frag_body.sh"},//皮肤
					{"shader/vertex_organ.sh", "shader/frag_organ.sh"},//器官

			};
	static String[]mVertexShader=new String[shaderCount];
	static String[]mFragmentShader=new String[shaderCount];
	static int[] program=new int[shaderCount];
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=0;i<shaderCount;i++)
		{
			//加载顶点着色器的脚本内容
			mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
			//加载片元着色器的脚本内容
			mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}
	}
	//编译3D物体的shader
	public static void compileShader()
	{
		for(int i=0;i<shaderCount;i++)
		{
			//存放着色器
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//返回着色器
	public static int getShader(int index)
	{
		return program[index];
	}
}
