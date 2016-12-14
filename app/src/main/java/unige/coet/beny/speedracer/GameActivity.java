package unige.coet.beny.speedracer;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

public class GameActivity extends AppCompatActivity {

    public GLSurfaceView mGLSurfaceView;
    private GameRenderer gameRenderer;
    public boolean running = false;
    private AssetManager assetManager;

    OrientationEventListener mOrientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();

        final Context gameContext = this;
        mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_GAME){
            @Override
            public void onOrientationChanged(int arg0) {

                if (arg0 == ORIENTATION_UNKNOWN){
                    Toast.makeText(gameContext, "Can't detect orientation -> pause", Toast.LENGTH_SHORT).show();
                }
                else {
                    gameRenderer.onRotationChanged(arg0);
                }
            }};

        // The GLSurfaceView for the game. Overrides onTouchEvent() to react
        // to touch events triggered by the player.
        this.mGLSurfaceView = new GLSurfaceView(this){
            @Override
            public boolean onTouchEvent(MotionEvent e) {
                //if (gameRenderer.time % 5 == 0)
                gameRenderer.addProjectile();
                return true;
            }
        };

        this.gameRenderer = new GameRenderer(this, this);
        this.mGLSurfaceView.setEGLContextClientVersion(2);
        this.mGLSurfaceView.setRenderer(gameRenderer);
        this.mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(this.mGLSurfaceView);

        if (!running) {
            gameRenderer.rotZero = 270;
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            running=true;
        }
    }

    Data3d readData(String name){
        return ObjectLoader.load(assetManager, name);
    }

    public void gameOver() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrientationEventListener.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationEventListener.disable();
    }

}
