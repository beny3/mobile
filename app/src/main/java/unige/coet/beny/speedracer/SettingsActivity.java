package unige.coet.beny.speedracer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void mainMenu(View view){
        Intent mainMenu = new Intent(this, MainMenu.class);
        startActivity(mainMenu);
        this.finish();
    }

    public void setSensitivityLow(View view){
        SharedPreferences settings = this.getSharedPreferences(getString(R.string.settings_file), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(getString(R.string.sensitivity), 7.f);
        editor.commit();

    }

    public void setSensitivityMed(View view){
        SharedPreferences settings = this.getSharedPreferences(getString(R.string.settings_file), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(getString(R.string.sensitivity), 5.f);
        editor.commit();
    }

    public void setSensitivityHigh(View view){
        SharedPreferences settings = this.getSharedPreferences(getString(R.string.settings_file), MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(getString(R.string.sensitivity), 2.f);
        editor.commit();
    }
}
