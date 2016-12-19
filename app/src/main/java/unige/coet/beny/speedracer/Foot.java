package unige.coet.beny.speedracer;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by beny on 19/12/16.
 */

public class Foot extends Object3D {
    float time=0;
    float shift=0;

    Foot(int ind, float x, float z0){
        this.z0 = z0;
        z = z0;
        p[1]=0.5f;
        p[0]=x;
        if (x <0){
            shift = (float)PI;
        }
        index = ind;
    }
    @Override
    public void sumV(){
        z+=vz;
        time+=0.3;
        p[2] = (float)cos(time + shift)/2;
        angle[0] = (float)cos(time + shift);
        /*
        for (int i=0; i<3; i++){
            angle[i]+=angularV[i];
            p[i]+= V[i];
        }
        */
    }
    @Override
    public void reset(){
        z=z - 50;
    }
}
