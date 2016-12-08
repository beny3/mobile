package unige.coet.beny.speedracer;

/**
 * Created by aurelien_coet on 30.11.16.
 */
public class Object3D {

    public float theta;
    public float r;
    public float z;
    public float z0;
    public int index;

    Object3D (float theta, float z, float r, int index) {
        this.theta = theta;
        this.z = z;
        this.z0 = z;
        this.r = r;
        this.index = index;
    }

    @Override
    public String toString() {
        return "theta "+ this.theta + " z" + this.z + " index:"+ this.index;
    }
}
