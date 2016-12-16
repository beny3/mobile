package unige.coet.beny.speedracer;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    public GLSurfaceView mGLSurfaceView;
    private TextView scoreView;
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
                    Intent pause = new Intent(gameContext, PauseActivity.class);
                    startActivity(pause);
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

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gameRenderer.addProjectile();
                }
                return true;
            }
        };

        this.gameRenderer = new GameRenderer(this, this);
        this.mGLSurfaceView.setEGLContextClientVersion(2);
        this.mGLSurfaceView.setRenderer(gameRenderer);
        this.mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(this.mGLSurfaceView);

        // A textview indicating his score to the player is added to the view in the GameActivity.
        scoreView = new TextView(this);
        scoreView.setText("Score : 0");
        addContentView(scoreView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (!running) {
            gameRenderer.rotZero = 270;
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            running=true;
        }
    }

    Data3d readData(String name){
        return ObjectLoader.load(assetManager, name);
    }

    /**
     * This method is called when the player enters in a collision with an
     * object in the game. It stops the current game and launches an activity
     * showing a game over screen.
     */
    public void gameOver() {
        Intent gameOver = new Intent(this, GameOverActivity.class);
        startActivity(gameOver);
        this.finish();
    }

    /**
     * Updates the score of the player on the screen during the game.
     * @param score the score of the player.
     */
    public void updateScoreOnScreen(final float score){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreView.setText("Score : "+score);
            }
        });
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
