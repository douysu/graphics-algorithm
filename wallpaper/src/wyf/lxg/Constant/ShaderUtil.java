package wyf.lxg.Constant;

import android.content.res.Resources;

import android.opengl.GLES30;

import android.util.Log;

//~--- JDK imports ------------------------------------------------------------

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ShaderUtil {

    /**
     * 创建shaderProgram程序的方法
     * @param vertexSource 顶点脚本
     * @param fragmentSource 片元脚本
     * @return shaderProgram的id
     */
    public static int createProgram(String vertexSource, String fragmentSource) {

        // 加载顶点着色器
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);

        if (vertexShader == 0) {
            return 0;
        }

        // 加载片元着色器
        int pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);

        if (pixelShader == 0) {
            return 0;
        }

        // 创建程序
        int program = GLES30.glCreateProgram();

        // 若程序创建成功则向程序中加入顶点着色器与片元着色器
        if (program != 0) {

            // 向程序中加入顶点着色器
            GLES30.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");

            // 向程序中加入片元着色器
            GLES30.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");

            // 链接程序
            GLES30.glLinkProgram(program);

            // 存放链接成功program数量的数组
            int[] linkStatus = new int[1];

            // 获取program的链接情况
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);

            // 若链接失败则报错并删除程序
            if (linkStatus[0] != GLES30.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }

        return program;
    }

    /**
     *  创建shader的方法
     * @param shaderType shader的类型  GLES30.GL_VERTEX_SHADER   GLES30.GL_FRAGMENT_SHADER
     * @param source shader的脚本字符串
     * @return shader的id
     */
    private static int loadShader(int shaderType, String source) {

        // 创建一个shaderType类型的新shader
        int shader = GLES30.glCreateShader(shaderType);

        // 若创建成功则加载shader
        if (shader != 0) {

            // 加载shader的源代码
            GLES30.glShaderSource(shader, source);

            // 编译shader
            GLES30.glCompileShader(shader);

            // 存放编译成功shader数量的数组
            int[] compiled = new int[1];

            // 获取Shader的编译情况
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);

            if (compiled[0] == 0) {    // 若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }

        return shader;
    }

    /**
     * 检查每一步操作是否有错误的方法
     * @param op
     */
    private static void checkGlError(String op) {
        int error;

        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("ES20_ERROR", op + ": glError " + error);

            //throw new RuntimeException(op + ": glError " + error);
        }
    }

    /**
     * 用IO从Assets目录下读取文件(读取*.sh字符串)
     * @param fname 文件名
     * @param r 资源
     * @return *.sh字符串
     */
    public static String loadFromAssetsFile(String fname, Resources r) {
        String result = null;
        String name="shader/"+fname;
        try {
            InputStream           in   = r.getAssets().open(name);
            int                   ch   = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }

            byte[] buff = baos.toByteArray();

            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
