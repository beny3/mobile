package unige.coet.beny.speedracer;

/**
 * Created by aurelien_coet on 30.11.16.
 */

public class Block {

    public  float [] vertices = { -0.2f,0f,-0.1f,
            0.2f, 0f,-0.1f,
            0.2f, 1f,-0.1f,
            -0.2f, 1f,-0.1f,

            -0.2f,0f, 0.1f,
            0.2f, 0f, 0.1f,
            0.2f, 1f, 0.1f,
            -0.2f, 1f, 0.1f,
    };

    public float [] uv= { 0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f,
    };

    public short [] faces ={ 0,1,2,
            2,1,3,
            1,5,2,
            2,5,6,
            4,0,7,
            7,0,3,
            3,2,7,
            7,2,6,
            4,5,0,
            0,5,1,
            4,5,7,
            7,5,6
    };
}
