package unige.coet.beny.speedracer;

import android.opengl.Matrix;

/**
 * Created by aurelien_coet on 30.11.16.
 */
public class Object3D {

    public float theta;
    public float r;
    public float z;
    public float z0;
    public float vz=0;
    public int index;
    public boolean isAmmo=false;
    public float[] p={0,0,0};
    public float[] angle={0,0,0};
    public float[] angularV = {0,0,0};
    public float[] V = new float[3];
    //public float[] m = new float[16];
    public int objectPointer = 0;
    public Object3D[] objects = new Object3D[20];

    public int addObject(float[] V, float[] p, int index){
        objects[objectPointer++] = new Object3D(V, p, z0,  index);
        return objectPointer - 1;

    }

    Object3D (float[] V, float[] p, float z, int index) {
        this.theta = 0;
        this.z =  z;
        this.z0 = z;
        this.p = p;
        this.angularV = V;
        this.index = index;
    }



    Object3D (float theta, float z, float r, int index) {
        this.theta = theta;
        this.z = z;
        this.z0 = z;
        this.r = r;
        this.index = index;
    }

    public void sumV(){
        int i;
        z+=vz;
        vz*=0.99;
        for (i=0; i<3; i++){
            angle[i]+=angularV[i];
            p[i]+= V[i];
        }

    }

    public void reset(){
        int i;
        z+=vz;
        vz*=0.99;
        for (i=0; i<3; i++){
            p[i] = 0;
            V[i]  = 0;
            angularV[i] = 0;
            angle[i]= 0;
        }
    }

    @Override
    public String toString() {
        return "theta "+ this.theta + " z" + this.z + " index:"+ this.index;
    }
}
