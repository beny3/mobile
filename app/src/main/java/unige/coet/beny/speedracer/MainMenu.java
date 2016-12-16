package unige.coet.beny.speedracer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    public boolean gameStarted = false;
    private AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void newGame(View view){

        // When the game must be launched, we check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2){
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            this.finish();
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Compatibility Error")
                    .setMessage("Sorry, your device is not compatible with the version of OpenGL this game uses.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }

    }

    public void highScores(View view){
        Intent highScores = new Intent(this, HighScoresActivity.class);
        startActivity(highScores);
    }

    public void settings(View view){
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }
}
