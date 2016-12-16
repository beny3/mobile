package unige.coet.beny.speedracer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    public GLSurfaceView mGLSurfaceView;
    private TextView scoreView;
    private TextView ammoView;
    private GameRenderer gameRenderer;
    public boolean running = false;
    private AssetManager assetManager;

    OrientationEventListener mOrientationEventListener;

    public final static String EXTRA_MESSAGE = "";

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
                        int ammoLeft = gameRenderer.addProjectile();

                        if (ammoLeft>=0){
                            updateAmmoOnScreen(ammoLeft);
                        }
                }
                return true;
            }
        };

        // The sensitivity of the controls selected by the player in the settings is retrieved and
        // set as the sensitivity for the rotation in the game renderer.
        SharedPreferences settings = this.getSharedPreferences(getString(R.string.settings_file), MODE_PRIVATE);
        float sensitivity = settings.getFloat(getString(R.string.sensitivity), 5.f);

        this.gameRenderer = new GameRenderer(this, this, sensitivity);
        this.mGLSurfaceView.setEGLContextClientVersion(2);
        this.mGLSurfaceView.setRenderer(gameRenderer);
        this.mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(this.mGLSurfaceView);

        // A TextView indicating his score to the player is added to the view in the GameActivity.
        scoreView = new TextView(this);
        scoreView.setText("Score : 0");
        addContentView(scoreView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // A TextView indicating to the player the ammo he has left is added to the view in the GameActivity.
        ammoView = new TextView(this);
        ammoView.setText("Ammo : 5");
        ammoView.setGravity(Gravity.RIGHT);
        addContentView(ammoView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (!running) {
            gameRenderer.rotZero = 270; // Initial orientation is always at 270.
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
     * showing a game over screen. Before doing so, it saves the player's score if
     * it is better than his current highest score.
     */
    public void gameOver(float score) {
        // We retrieve the best score of the player.
        SharedPreferences scoresFile = this.getSharedPreferences(getString(R.string.scores_file), MODE_PRIVATE);
        int highScore = scoresFile.getInt(getString(R.string.high_score1), 0);
        // if the current score of the player is better than his best one, the best one is replaced.
        if (score > highScore){
            SharedPreferences.Editor editor = scoresFile.edit();
            editor.putInt(getString(R.string.high_score1), (int) score);
            editor.commit();
        }

        // The game over activity is launched.
        Intent gameOver = new Intent(this, GameOverActivity.class);
        gameOver.putExtra(EXTRA_MESSAGE, ""+(int)score);
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

    public void updateAmmoOnScreen(final int ammo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ammoView.setText("Ammo : "+ammo);
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
