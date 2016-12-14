package unige.coet.beny.speedracer;

import android.app.AlertDialog;
import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by aurelien_coet on 30.11.16.
 */

public class GameRenderer  implements GLSurfaceView.Renderer, SensorEventListener {

    public SensorManager sensorManager;

    private GameActivity parentActivity;
    private float time;
    private Context context;

    private float[][] mModelMatrix = new float[10][16];
    private float[] mWorldMatrix = new float[16];

    // Stores the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
    // it positions things relative to the player's eye.
    private float[] mViewMatrix = new float[16];
    // Stores the projection matrix. This is used to project the scene onto a 2D viewport.
    private float[] mProjectionMatrix = new float[16];
    // Allocates storage for the final combined matrix. This will be passed into the shader program.
    private float[] mMVPMatrix = new float[16];

    // Creation of the buffers for OpenGL.
    private int bufferPointer = 0;
    private int[] vertexBufferObj = new int[20];
    private int[] textureBufferObj = new int[20];
    private int[] indexBufferObj = new int[20];
    private int[] uvBufferObj = new int[20];
    private int[] faceSizes = new int[20];

    private float[][] matrices = new float[20][16];
    private float[] coltest= new float[4];

   // private int objectPointer = 0;
   // private Object3D[] objects = new Object3D[20];
    private int playerIndex;

    // Integers referencing buffers on the graphics card (in OpenGL).
    private int mModelMatrixHandle;
    private int mWorldMatrixHandle;
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    //private int mColorHandle;
    private int mUvHandle;
    private int mtimeHandle;
    private int mZposeHandle;
    private int mTextureUniformHandle;

    public float rotScreen;
    public float rotZero = 0;

    public int objectPointer = 0;
    public Object3D[] objects = new Object3D[20];


    public GameRenderer(Context context, GameActivity parentActivity, SensorManager sensorManager){
        this.context = context;
        this.parentActivity = parentActivity;
        this.sensorManager = sensorManager;
        this.time = 0;
    }

    /**
     * Transforms arrays into buffers which can be sent on the graphics card. For each buffer created,
     * there's an integer referencing it. These references are stored in arrays (vertexBufferObj for vertice,
     * indexBufferObj for 3D-object faces). When an object needs to be rendered, it is bound with the indices
     * corresponding to it and the drawTriangles() method is called.
     * @param vertices
     * @param uvCoordinates
     * @param elements
     * @param drawable
     * @return
     */
    public int createBuffers(float[] vertices, float[] uvCoordinates, short[] elements, int drawable){
        // Creation of the buffers and sending to the GPU.
        FloatBuffer objPositionsBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        objPositionsBuffer.put(vertices).position(0);

        FloatBuffer uvBuffer = ByteBuffer.allocateDirect(uvCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        uvBuffer.put(uvCoordinates).position(0);

        ShortBuffer facesBuffer = ByteBuffer.allocateDirect(elements.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        facesBuffer.put(elements).position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObj[bufferPointer]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, objPositionsBuffer.capacity() * 4, objPositionsBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvBufferObj[bufferPointer]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, uvBuffer.capacity() * 4, uvBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferObj[bufferPointer]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, facesBuffer.capacity() * 2, facesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        textureBufferObj[bufferPointer] = loadTexture(this.context, drawable);
        faceSizes[bufferPointer] = facesBuffer.capacity();
        bufferPointer++;
        return bufferPointer-1;
    }

    /**
     * Adds a 3D object to the scene.
     * @param angle
     * @param z
     * @param r
     * @param index
     */


    public int addObject(float angle, float z, float r, int index){
        objects[objectPointer++] = new Object3D(angle, z, r, index);
        return objectPointer - 1;

    }


    /**
     * Loads a texture for an object.
     * @param context
     * @param resourceId
     * @return
     */
    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            //System.out.print("OUT BITMAP");
            // System.out.println(bitmap);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


    /**
     * Draws the triangles of an object on the screen.
     * @param object
     */
    private void drawTriangles(Object3D object, int depthMat)
    {

            // Pass in the position information
            //System.out.println(o.toString() + " " + vbo[o.index]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObj[object.index]);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
            /*
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObj[object.index]);
            GLES20.glEnableVertexAttribArray(mColorHandle);
            GLES20.glVertexAttribPointer(mColorHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
            */
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvBufferObj[object.index]);
            GLES20.glEnableVertexAttribArray(mUvHandle);
            GLES20.glVertexAttribPointer(mUvHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

            GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix[depthMat], 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mProjectionMatrix, 0);
            GLES20.glUniformMatrix4fv(mWorldMatrixHandle, 1, false, mWorldMatrix, 0);
            GLES20.glUniform1f(mtimeHandle, time);
            GLES20.glUniform1f(mZposeHandle, object.z);
            //TEXTURE
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBufferObj[object.index]);
            GLES20.glUniform1i(mTextureUniformHandle, 0);

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferObj[object.index]);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, faceSizes[object.index], GLES20.GL_UNSIGNED_SHORT, 0);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        rotScreen = event.values[1] - rotZero;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //EMPTY.
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int numBufferObj = vertexBufferObj.length;

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glGenBuffers(numBufferObj, vertexBufferObj, 0);
        GLES20.glGenBuffers(numBufferObj, uvBufferObj, 0);
        GLES20.glGenBuffers(numBufferObj, indexBufferObj, 0);
        //GLES20.glGenBuffers(numBufferObj, textureBufferObj, 0);

        // Generation of the cylinder for the tunnel in the game.
        Cylinder cylinder = new Cylinder(16, 70);
        Block block = new Block();
        Data3d playerData = parentActivity.readData();
        //Data3d dino = parentActivity.readData();

        // Creation of the buffers for the 3D models of the objects in the game.
        int indexBlock = createBuffers(block.vertices, block.uv, block.faces, R.drawable.red);
        int player = createBuffers(playerData.vertices, playerData.uvs, playerData.elements, R.drawable.tex_ship);

        // Objects are added in the game.
        // First, the tunnel is created with a cylinder.
        addObject(0, 2, 0, createBuffers(cylinder.vertices, cylinder.uv, cylinder.faces, R.drawable.bake));
        // The object representing the player is then added.
        playerIndex = addObject(0, 3, 0.5f, player);
        // Finally, obstacles are added.
        //addObject(0, -10f, 1f, indexBlock);
        //addObject(30, -13f, 1f, indexDino );
        //addObject(180, -18f, 1f, indexBlock);
        //addObject(180, -30f, 1f, indexBlock);
        //addObject(80, -40f, 1f, indexBlock);
        int a = addObject(20, -10f, 1f, indexBlock);
        float[] v= {0f, 10f, 0f};
        float[] p= {0f, -2f, 0f};
        objects[2].addObject(v, p, player);

        Matrix.setIdentityM(mWorldMatrix, 0);
        //Matrix.rotateM(mWorldMatrix, 0, 30f, 0.0f, 0.0f, 1.0f);

        // Registration of the sensor to capture user input in order to move in the game.
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);

        // Set the background clear color to black.
        GLES20.glClearColor(0.f, 0.f, 0.f, 1.f);

        // Defines the position of the camera. It is never modified : the world is displaced when the player
        // moves in the game.
        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 0.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose to.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        // VERTEX SHADERS
        // This code is executed on the GPU for each vertex being rendered.
        // The attributes contain values specific to each vertex.
        // Uniform attributes are common values for all vertices.
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;\n"               // A constant representing the combined model/view/projection matrix.
                        +"uniform mat4 u_ModelMatrix;\n"
                        +"uniform mat4 u_WorldMatrix;\n"
                        +"uniform float time;\n"
                        +"uniform float Zpose;\n"
                        +"attribute vec3 a_Position;\n"     // Per-vertex position information we will pass in.
                        +"attribute vec2 uv;\n"             // Per-vertex color information we will pass in.
                        +"varying vec2 v_uv;\n"

                        +"float f(float t, float z, float v) {\n "
                        +"  //return (z+ t*v);\n"
                        +"  //float zz = (z+ t*v + 1.);\n"
                        +"  //float r = sin(t*0.01)*4. + 12.;\n"
                        +"  //return sqrt(r*r-zz*zz)-r;\n"
                        +"   return sin((z + t*v)/4.)*2. - sin(t*v/4.)*2. - z*cos(t*v/4.)*0.5;\n"
                        +"}\n"

                        +" float g() {\n "
                        +" return 1.;\n"
                        +"}\n"

                        + "void main()\n"		            // The entry point for our vertex shader.
                        + "{ v_uv = uv;\n "
                        //+ "   v_Color = a_Color;\n"
                        + "   float v=0.1;\n"
                        + "   vec3 add;\n"
                        + "   vec4 pos;\n"
                        + "   if  (Zpose < 1.){\n"		    // Pass the color through to the fragment shader.
                        + "      add= vec3( f(time, Zpose + time*v, v), 0, Zpose + time*v );\n"
                        + "      //add= vec3(0, 0, 10.);\n"
                        + "      pos = u_WorldMatrix*(u_ModelMatrix* vec4(a_Position ,1) + vec4(add, 0));\n"
                        + "   }else if (Zpose > 2.){\n"
                        + "      add= vec3( 0, 1., -1.2 ); \n"
                        + "      pos = u_ModelMatrix*vec4(a_Position ,1)  + vec4(add, 0); \n"
                        + "   }else{\n"
                        + "    v_uv[0]+= 0.5;"
                        +"       v_uv[1]+= time*v;\n"
                        + "      add= vec3(f(time, a_Position[2], v), 0, 0);\n"
                        + "      pos = u_WorldMatrix*vec4(a_Position+ add ,1);\n"
                        + "   }\n"
                        // It will be interpolated across the triangle.
                        + "   gl_Position = u_MVPMatrix*pos ;\n"        // Multiply the vertex by the matrix to get the final point in
                        + "}\n";                                        // normalized screen coordinates.

        final String fragmentShader =
                "precision mediump float;\n"		            // Set the default precision to medium. We don't need as high of a
                        +"uniform sampler2D u_Texture;\n"
                        // precision in the fragment shader.
                        //+ "varying vec3 v_Color;\n"
                        + "varying vec2 v_uv;\n"
                        // triangle per fragment.
                        + "void main()\n"		                // The entry point for our fragment shader.
                        + "{         " +
                        " gl_FragColor = texture2D(u_Texture, v_uv);\n"
                        + " //gl_FragColor = vec4(vec3(1.,0,0),1);\n"	// Pass the color directly through the pipeline.
                        + "}\n";

        //------------------------------------------------------------------------------------------//
        // Compiles the shaders.
        // Load in the vertex shader.
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }

        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader.
            GLES20.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0) {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it.
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            //GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }
        //------------------------------------------------------------------------------------------//

        // Creation of handlers (variables that can be passed as arguments for the OpenGL shaders).
        // Set program handles. These will later be used to pass in values to the program.
        mModelMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_ModelMatrix");
        mWorldMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_WorldMatrix");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mtimeHandle = GLES20.glGetUniformLocation(programHandle, "time");
        mZposeHandle = GLES20.glGetUniformLocation(programHandle, "Zpose");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        //mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
        mUvHandle = GLES20.glGetAttribLocation(programHandle, "uv");
        mTextureUniformHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");

        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = 1.0f;
        final float top = -1.0f;
        final float near = 0.5f;
        final float far = 15.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void mult(float[] m1, float[] m2){

    }


    public void make(Object3D o,  int  depth){
        int i;
        o.sumV();
        float[] m= mModelMatrix[depth];
        for (i=0; i<16; i++){
            mModelMatrix[depth][i] = mModelMatrix[depth-1][i];
        }

        Matrix.translateM(m, 0,  o.p[0], o.p[1], o.p[2]);

        Matrix.rotateM(mModelMatrix[depth], 0, o.angle[0], 0.1f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix[depth], 0, o.angle[1], 0.0f, 0.1f, 0.0f);
        Matrix.rotateM(mModelMatrix[depth], 0, o.angle[2], 0.0f, 0.0f, 1.0f);



        drawTriangles(o,depth);

        if (o.z < 0 && o.z + 0.1*time>=0){
            o.z= o.z0 - 0.1f*time;
            //compute the absolut opengl coordinate of the objet o when its z coord == 0
            // we store that position in the vector "coltest"
            Matrix.multiplyMV(coltest,0,mWorldMatrix,0, mModelMatrix[depth], 12);
            //we want the distance beetween the player and the object (their centers)
            //becaus the player is at [0,1,0] this is = coltest - player_position
            coltest[1]-=1;
            // if squared distance is less than 0.2 then colision
            // TODO instead of 0.2 we might want to have different sizes for different objects
            if (coltest[0]*coltest[0] + coltest[1]*coltest[1] + coltest[2]*coltest[2] < 0.2){

                //System.out.println("BADABOUUM ! ");

                parentActivity.running = false;
                parentActivity.mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                parentActivity.gameOver();
                return;
            }
        }

        i=0;
        while (o.objects[i]!=null) {
            make(o.objects[i],  depth + 1);
            i++;
        }
    }

    // This function is called every time a frame is being rendered. All objects in the objects array
    // are rendered. The rendering is done by the method drawTriangles().
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        time++;
        //Matrix.setIdentityM(mWorldMatrix, 0);
        Matrix.rotateM(mWorldMatrix, 0, rotScreen/6.f, 0.0f, 0.0f, 1.0f);
        objects[playerIndex].theta = (rotScreen + objects[playerIndex].theta)/1.5f;
        Matrix.setIdentityM(mModelMatrix[0], 0);
        int i=0;
        while(objects[i]!=null){


            Matrix.setIdentityM(mModelMatrix[0], 0);

            Matrix.rotateM(mModelMatrix[0], 0, objects[i].theta, 0.0f, 0.0f, 1.0f);
            Matrix.translateM(mModelMatrix[0], 0, 0.0f, objects[i].r, 0.0f);
            //System.out.println("   call make rec "+ i);
            make(objects[i],  1);


            //drawTriangles(objects[i],0);
            i++;
        }
    }
}
