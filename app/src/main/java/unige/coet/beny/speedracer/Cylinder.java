package unige.coet.beny.speedracer;

/**
 * Created by aurelien_coet on 30.11.16.
 */

public class Cylinder {

    public float [] vertices;
    public float [] uv;
    public short [] faces;

    /*
    public MakeCylinder(int n) {
        float[] a = {
                0f,   0f,    0f,
                1f,   0f,    0f,
                1f, 1.0f,    0f,
                0f,  0.8f,    0f,
                0f,  2.0f,    0f,
                -1f, 1.5f,  0f};
        vertics = a;
        short[] b={3,2,4, 3,2,4, 3,2,4, 3,4,5};
        //short[] b={0,1,2, 0,2,3, 3,2,4, 3,4,5};
        faces = b;

    }
    */

    public Cylinder(int n, int nRow) {
        vertices = new float[(n+1)*(nRow+1)*3];
        faces = new short[n*nRow*2*3];
        uv = new float[vertices.length/3*2];

        float p = (float)Math.PI;

        //creation des points
        for ( int j=0; j < nRow+1; j++) {
            int k=0;
            int i;

            for (i=0; i < n*3; i += 3) {
                vertices[j*(n+1)*3 + i] = (float)Math.cos(k * 2 * p / n - p)*2;
                vertices[j*(n+1)*3 + i + 1] = (float) Math.sin(k * 2 * p / n - p)*2;
                vertices[j*(n+1)*3 + i + 2] = -(float)j;
                k++;
            }
            vertices[j*(n+1)*3 +i] = vertices[j*(n+1)*3 ];
            vertices[j*(n+1)*3 + i + 1] = vertices[j*(n+1)*3 +1];
            vertices[j*(n+1)*3 + i + 2] = vertices[j*(n+1)*3 +2];

        }

        //System.out.println(uv.length);
        for ( int j=0; j < nRow+1; j++) {
            int k=0;
            for (int i = 0; i < (n + 1)*2; i += 2) {
                uv[j*(n+1)*2 + i] = (float)k/n;
                uv[j*(n+1)*2 + i + 1] = (float)j/2;

                k++;
            }

        }

        // Creation of the faces of the cylinder.
        int i;
        for (i=0; i<faces.length; i+=3){
            faces[i]=666;
            faces[i+1]=666;
            faces[i+2]=666;
        }

        for ( int j=0; j < nRow; j++) {

            for (i = j*n; i < j*n + n; i++) {
                //triangle 1
                faces[6 * i] = (short)(1 + i + j);
                faces[6 * i + 1] = (short)(n + i + 1 + j);
                faces[6 * i + 2] = (short)(i + j);
                //triangle 2
                faces[6 * i + 3] = (short)(n + i + 1 + j);
                faces[6 * i + 4] = (short)(1 + i + j);
                faces[6 * i + 5] = (short)(n + i + 2 + j);

            }
            //System.out.println(6*i);
            //System.out.println(">>>>>");
            /*
            faces[6 * i] = (short) (j*n);
            faces[6 * i + 1] = (short)(i + n);
            faces[6 * i + 2] = (short)i;
            //triangle 2
            faces[6 * i + 3] = (short)(i + n);
            faces[6 * i + 4] = (short)(j*n);
            faces[6 * i + 5] = (short)((j+1)*n);
            */
        }
        /*
        for (i = 0; i <  faces.length; i+=3){
            System.out.println(faces[i] + " " + faces[i+1] + " " + faces[i+2]);
        }
        */
    }
}
