package unige.coet.beny.speedracer;

import java.io.Serializable;

/**
 * Created by aurelien_coet on 30.11.16.
 */
public class Data3d implements Serializable {
    final static long serialVersionUID=1;

    public float[] vertices;
    public float[] uvs;
    public short[] elements;
}
