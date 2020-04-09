#ifndef __OpenGL3_0Demo__Matrix__
#define __OpenGL3_0Demo__Matrix__
#include <math.h>
#include <assert.h>

class Matrix {
public:
    static void multiplyMM(float* result, int resultOffset, float* mlIn, int lhsOffset, float* mrIn, int rhsOffset)
    {
        double ml[16];
		double mr[16];
		for(int i=0;i<16;i++)
		{
			ml[i]=mlIn[i];
			mr[i]=mrIn[i];
		}
		
		result[0+resultOffset]=(float) (ml[0+lhsOffset]*mr[0+rhsOffset]+ml[4+lhsOffset]*mr[1+rhsOffset]+ml[8+lhsOffset]*mr[2+rhsOffset]+ml[12+lhsOffset]*mr[3+rhsOffset]);
		result[1+resultOffset]=(float) (ml[1+lhsOffset]*mr[0+rhsOffset]+ml[5+lhsOffset]*mr[1+rhsOffset]+ml[9+lhsOffset]*mr[2+rhsOffset]+ml[13+lhsOffset]*mr[3+rhsOffset]);
		result[2+resultOffset]=(float) (ml[2+lhsOffset]*mr[0+rhsOffset]+ml[6+lhsOffset]*mr[1+rhsOffset]+ml[10+lhsOffset]*mr[2+rhsOffset]+ml[14+lhsOffset]*mr[3+rhsOffset]);
		result[3+resultOffset]=(float) (ml[3+lhsOffset]*mr[0+rhsOffset]+ml[7+lhsOffset]*mr[1+rhsOffset]+ml[11+lhsOffset]*mr[2+rhsOffset]+ml[15+lhsOffset]*mr[3+rhsOffset]);
		
		result[4+resultOffset]=(float) (ml[0+lhsOffset]*mr[4+rhsOffset]+ml[4+lhsOffset]*mr[5+rhsOffset]+ml[8+lhsOffset]*mr[6+rhsOffset]+ml[12+lhsOffset]*mr[7+rhsOffset]);
		result[5+resultOffset]=(float) (ml[1+lhsOffset]*mr[4+rhsOffset]+ml[5+lhsOffset]*mr[5+rhsOffset]+ml[9+lhsOffset]*mr[6+rhsOffset]+ml[13+lhsOffset]*mr[7+rhsOffset]);
		result[6+resultOffset]=(float) (ml[2+lhsOffset]*mr[4+rhsOffset]+ml[6+lhsOffset]*mr[5+rhsOffset]+ml[10+lhsOffset]*mr[6+rhsOffset]+ml[14+lhsOffset]*mr[7+rhsOffset]);
		result[7+resultOffset]=(float) (ml[3+lhsOffset]*mr[4+rhsOffset]+ml[7+lhsOffset]*mr[5+rhsOffset]+ml[11+lhsOffset]*mr[6+rhsOffset]+ml[15+lhsOffset]*mr[7+rhsOffset]);
		
		result[8+resultOffset]=(float) (ml[0+lhsOffset]*mr[8+rhsOffset]+ml[4+lhsOffset]*mr[9+rhsOffset]+ml[8+lhsOffset]*mr[10+rhsOffset]+ml[12+lhsOffset]*mr[11+rhsOffset]);
		result[9+resultOffset]=(float) (ml[1+lhsOffset]*mr[8+rhsOffset]+ml[5+lhsOffset]*mr[9+rhsOffset]+ml[9+lhsOffset]*mr[10+rhsOffset]+ml[13+lhsOffset]*mr[11+rhsOffset]);
		result[10+resultOffset]=(float) (ml[2+lhsOffset]*mr[8+rhsOffset]+ml[6+lhsOffset]*mr[9+rhsOffset]+ml[10+lhsOffset]*mr[10+rhsOffset]+ml[14+lhsOffset]*mr[11+rhsOffset]);
		result[11+resultOffset]=(float) (ml[3+lhsOffset]*mr[8+rhsOffset]+ml[7+lhsOffset]*mr[9+rhsOffset]+ml[11+lhsOffset]*mr[10+rhsOffset]+ml[15+lhsOffset]*mr[11+rhsOffset]);
		
		result[12+resultOffset]=(float) (ml[0+lhsOffset]*mr[12+rhsOffset]+ml[4+lhsOffset]*mr[13+rhsOffset]+ml[8+lhsOffset]*mr[14+rhsOffset]+ml[12+lhsOffset]*mr[15+rhsOffset]);
		result[13+resultOffset]=(float) (ml[1+lhsOffset]*mr[12+rhsOffset]+ml[5+lhsOffset]*mr[13+rhsOffset]+ml[9+lhsOffset]*mr[14+rhsOffset]+ml[13+lhsOffset]*mr[15+rhsOffset]);
		result[14+resultOffset]=(float) (ml[2+lhsOffset]*mr[12+rhsOffset]+ml[6+lhsOffset]*mr[13+rhsOffset]+ml[10+lhsOffset]*mr[14+rhsOffset]+ml[14+lhsOffset]*mr[15+rhsOffset]);
		result[15+resultOffset]=(float) (ml[3+lhsOffset]*mr[12+rhsOffset]+ml[7+lhsOffset]*mr[13+rhsOffset]+ml[11+lhsOffset]*mr[14+rhsOffset]+ml[15+lhsOffset]*mr[15+rhsOffset]);
    }
    static void multiplyMV (float* resultVec, int resultVecOffset, float* mlIn, int lhsMatOffset,
                            float* vrIn, int rhsVecOffset)
	{
		double ml[16];
		double vr[4];
		for(int i=0;i<16;i++)
		{
			ml[i]=mlIn[i];
		}
		vr[0]=vrIn[0];
		vr[1]=vrIn[1];
		vr[2]=vrIn[2];
		vr[3]=vrIn[3];
		
		resultVec[0+resultVecOffset]=(float) (ml[0+lhsMatOffset]*vr[0+rhsVecOffset]+ml[4+lhsMatOffset]*vr[1+rhsVecOffset]+ml[8+lhsMatOffset]*vr[2+rhsVecOffset]+ml[12+lhsMatOffset]*vr[3+rhsVecOffset]);
		resultVec[1+resultVecOffset]=(float) (ml[1+lhsMatOffset]*vr[0+rhsVecOffset]+ml[5+lhsMatOffset]*vr[1+rhsVecOffset]+ml[9+lhsMatOffset]*vr[2+rhsVecOffset]+ml[13+lhsMatOffset]*vr[3+rhsVecOffset]);
		resultVec[2+resultVecOffset]=(float) (ml[2+lhsMatOffset]*vr[0+rhsVecOffset]+ml[6+lhsMatOffset]*vr[1+rhsVecOffset]+ml[10+lhsMatOffset]*vr[2+rhsVecOffset]+ml[14+lhsMatOffset]*vr[3+rhsVecOffset]);
		resultVec[3+resultVecOffset]=(float) (ml[3+lhsMatOffset]*vr[0+rhsVecOffset]+ml[7+lhsMatOffset]*vr[1+rhsVecOffset]+ml[11+lhsMatOffset]*vr[2+rhsVecOffset]+ml[15+lhsMatOffset]*vr[3+rhsVecOffset]);
	}
    
    static void setIdentityM (float* sm, int smOffset)
	{
		for(int i=0;i<16;i++)
		{
			sm[i]=0;
		}
		
		sm[0]=1;
		sm[5]=1;
		sm[10]=1;
		sm[15]=1;
	}

	static void translateM(float* m, int mOffset,float x, float y, float z)
	{
		for (int i=0 ; i<4 ; i++)
		{
			int mi = mOffset + i;
			m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
		}
	}
	
    static void rotateM(float* m, int mOffset,float a, float x, float y, float z)
	{
		float rm[16];
		setRotateM(rm, 0, a, x, y, z);
		float rem[16];
		multiplyMM(rem, 0, m, 0, rm, 0);
		for(int i=0;i<16;i++)
		{
			m[i]=rem[i];
		}
	}
    
    static void setRotateM(float* m, int mOffset,float a, float x, float y, float z)
    {
        float radians = a * 3.14159f / 180.0f;
        float s = sin(radians);
        float c = cos(radians);
        float sm[16];
        setIdentityM(sm, 0);
        sm[0] = c + (1 - c) * x * x;
        sm[1] = (1 - c) * x * y - z * s;
        sm[2] = (1 - c) * x * z + y * s;
        sm[4] = (1 - c) * x * y + z * s;
        sm[5] = c + (1 - c) * y * y;
        sm[6] = (1 - c) * y * z - x * s;
        sm[8] = (1 - c) * x * z - y * s;
        sm[9] = (1 - c) * y * z + x * s;
        sm[10] = c + (1 - c) * z * z;
        
        for(int i=0;i<16;i++)
		{
			m[i]=sm[i];
		}
    }
    
    static void scaleM(float* m, int mOffset, float x, float y, float z)
    {
        float sm[16];
        setIdentityM(sm, 0);
        sm[0] = x;
        sm[5] = y;
        sm[10] = z;
        sm[15] = 1;
        float tm[16];
        multiplyMM(tm,0,m,0,sm,0);
        for(int i=0;i<16;i++)
		{
			m[i]=tm[i];
		}
    }
    
    static void transposeM(float* mTrans, int mTransOffset, float* m, int mOffset)
    {
        for (int i = 0; i < 4; i++)
        {
            int mBase = i * 4 + mOffset;
            mTrans[i + mTransOffset] = m[mBase];
            mTrans[i + 4 + mTransOffset] = m[mBase + 1];
            mTrans[i + 8 + mTransOffset] = m[mBase + 2];
            mTrans[i + 12 + mTransOffset] = m[mBase + 3];  
        }
    }
         
    static void frustumM(float* m, int offset, float left, float right, float bottom, float top, float near, float far)
    {
        const float r_width  = 1.0f / (right - left);
        const float r_height = 1.0f / (top - bottom);
        const float r_depth  = 1.0f / (near - far);
        const float x = 2.0f * (near * r_width);
        const float y = 2.0f * (near * r_height);
        const float A = 2.0f * ((right + left) * r_width);
        const float B = (top + bottom) * r_height;
        const float C = (far + near) * r_depth;
        const float D = 2.0f * (far * near * r_depth);
        m[offset + 0] = x;
        m[offset + 5] = y;
        m[offset + 8] = A;
        m[offset +  9] = B;
        m[offset + 10] = C;
        m[offset + 14] = D;
        m[offset + 11] = -1.0f;
        m[offset +  1] = 0.0f;
        m[offset +  2] = 0.0f;
        m[offset +  3] = 0.0f;
        m[offset +  4] = 0.0f;
        m[offset +  6] = 0.0f;
        m[offset +  7] = 0.0f;
        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 15] = 0.0f;
    }

    static void orthoM(float * m, int mOffset, float left, float right, float bottom, float top, float near, float far)
    {
        assert(left != right);
        assert(bottom != top);
        assert(near != far);

        float r_width  = 1.0f / (right - left);
        float r_height = 1.0f / (top - bottom);
        float r_depth  = 1.0f / (far - near);
        float x =  2.0f * (r_width);
        float y =  2.0f * (r_height);
        float z = -2.0f * (r_depth);
        float tx = -(right + left) * r_width;
        float ty = -(top + bottom) * r_height;
        float tz = -(far + near) * r_depth;

        m[mOffset + 0] = x;
        m[mOffset + 5] = y;
        m[mOffset +10] = z;
        m[mOffset +12] = tx;
        m[mOffset +13] = ty;
        m[mOffset +14] = tz;
        m[mOffset +15] = 1.0f;
        m[mOffset + 1] = 0.0f;
        m[mOffset + 2] = 0.0f;
        m[mOffset + 3] = 0.0f;
        m[mOffset + 4] = 0.0f;
        m[mOffset + 6] = 0.0f;
        m[mOffset + 7] = 0.0f;
        m[mOffset + 8] = 0.0f;
        m[mOffset + 9] = 0.0f;
        m[mOffset + 11] = 0.0f;
    }
    
    static void setLookAtM(float* rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
    {
        float fx = centerX - eyeX;
        float fy = centerY - eyeY;
        float fz = centerZ - eyeZ;
        float rlf = 1.0f /sqrt(fx*fx + fy*fy +fz*fz);
        fx *= rlf;
        fy *= rlf;
        fz *= rlf;
        float sx = fy * upZ - fz * upY;
        float sy = fz * upX - fx * upZ;
        float sz = fx * upY - fy * upX;
        float rls = 1.0f /sqrt(sx*sx + sy*sy +sz*sz);
        sx *= rls;
        sy *= rls;
        sz *= rls;
        float ux = sy * fz - sz * fy;
        float uy = sz * fx - sx * fz;
        float uz = sx * fy - sy * fx;
        rm[rmOffset + 0] = sx;
        rm[rmOffset + 1] = ux;
        rm[rmOffset + 2] = -fx;
        rm[rmOffset + 3] = 0.0f;
        rm[rmOffset + 4] = sy;
        rm[rmOffset + 5] = uy;
        rm[rmOffset + 6] = -fy;
        rm[rmOffset + 7] = 0.0f;
        rm[rmOffset + 8] = sz;
        rm[rmOffset + 9] = uz;
        rm[rmOffset + 10] = -fz;
        rm[rmOffset + 11] = 0.0f;
        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
        translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
    }
};

#endif
