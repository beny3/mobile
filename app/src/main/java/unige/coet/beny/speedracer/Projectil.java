package unige.coet.beny.speedracer;

import android.opengl.Matrix;

/**
 * Created by aurelien_coet on 30.11.16.
 */
public class Projectil {

    public boolean explode=false;
    public float theta;
    public float r;
    public float z;
    public float z0;
    public int index;
    public float[] p={0,0,0};
    public float[] m = new float[16];

    Projectil (float z, float r, int index) {
        this.theta = 0;
        this.z =  z;
        this.z0 = z;
        this.r  =r;
        this.index = index;
    }

    @Override
    public String toString() {
        return "theta "+ this.theta + " z" + this.z + " index:"+ this.index;
    }
}
