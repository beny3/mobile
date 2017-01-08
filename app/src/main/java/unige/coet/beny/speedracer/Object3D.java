package unige.coet.beny.speedracer;

import android.opengl.Matrix;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by aurelien_coet on 30.11.16.
 */
public class Object3D {

    public float theta;
    public float vtheta=0;
    public float vthetaInit=0;
    public float r;
    public float z;
    public float acc;
    public float vz=0;
    public int index;
    public boolean isAmmo=false;
    public float[] p={0,0,0};
    public float[] p0={0,0,0};
    public float[] angle0={0,0,0};
    public float[] angle={0,0,0};
    public float[] angularV = {0,0,0};
    public float[] V = new float[3];
    //public float[] m = new float[16];
    public int objectPointer = 0;
    public Object3D[] objects = new Object3D[20];

    public int addObject(int index, float x){
        objects[objectPointer++] = new Foot(index, x, z, acc);
        return objectPointer - 1;

    }
    Object3D(){

    }

    Object3D (float[] V, float[] p, float z, int index) {
        this.theta = 0;
        this.z =  z;
        this.p = p;
        this.angularV = V;
        this.index = index;
    }



    Object3D (float theta, float z, float r, int index, float acc, float vtheta) {
        this.theta = theta;
        this.angle[2]= theta;
        this.vtheta = vtheta;
        this.vthetaInit = vtheta;
        this.acc = acc;
        this.p[0]=(float)cos((theta+90)*2*3.14159/360)*r/2;
        this.p[1]=(float)sin((theta+90)*2*3.14159/360)*r/2;
        this.p0[0]=p[0];
        this.p0[1]=p[1];
        this.angle0[2]=  angle[2];
        this.z = z;
        //this.p[2]=z;
        this.r = r;
        this.index = index;
    }

    public void sumV(){
        int i;
        z+=vz;
        vz*=0.99;
        if (vtheta!=0 && vz >= 0 ){
            theta+=vtheta;
            this.angle[2]=theta;
            this.p[0]=(float)cos((theta+90 + 0)*2*3.14159/360)*r;
            this.p[1]=(float)sin((theta+90)*2*3.14159/360)*r;
        }

        for (i=0; i<3; i++){
            angle[i]+=angularV[i];
            p[i]+= V[i];
        }
        for(i=0; i< objectPointer; i++){
            objects[i].z=z;
            objects[i].acc = vtheta;
        }
    }

    public void reset(){
        int i;
        //acc = -0.005f*(z%10);
        vtheta=vthetaInit;
        z=z - 50;
        vz = 0;
        for (i=0; i<3; i++){
            p[i] = p0[i];
            V[i]  = 0;
            angularV[i] = 0;
            angle[i]= angle0[i];
        }


    }

    @Override
    public String toString() {
        return "theta "+ this.theta + " z" + this.z + " index:"+ this.index;
    }
}
