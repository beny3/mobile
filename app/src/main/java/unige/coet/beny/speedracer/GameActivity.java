package unige.coet.beny.speedracer;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.content.Context.SENSOR_SERVICE;

public class GameActivity extends AppCompatActivity {

    public GLSurfaceView mGLSurfaceView;
    private GameRenderer gameRenderer;
    public boolean running = false;
    private AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();

        this.mGLSurfaceView = new GLSurfaceView(this);
        this.gameRenderer = new GameRenderer(this, this, (SensorManager) getSystemService(SENSOR_SERVICE));
        this.mGLSurfaceView.setEGLContextClientVersion(2);
        this.mGLSurfaceView.setRenderer(gameRenderer);
        this.mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(this.mGLSurfaceView);

        if (!running) {
            gameRenderer.rotZero = gameRenderer.rotScreen;
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            running=true;
        }
    }

    Data3d readData(){
        return ObjectLoader.load(assetManager);
    }

    public void gameOver() {
        this.finish();
    }
}
