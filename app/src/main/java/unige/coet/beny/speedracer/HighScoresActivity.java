package unige.coet.beny.speedracer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HighScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        SharedPreferences highScores = this.getSharedPreferences(getString(R.string.scores_file), MODE_PRIVATE);
        int bestScore = highScores.getInt(getString(R.string.high_score1), 0);

        TextView bestScoreView = (TextView) findViewById(R.id.best_score);
        bestScoreView.setText("Best Score : "+bestScore);
    }

    public void mainMenu(View view){
        Intent mainMenu = new Intent(this, MainMenu.class);
        startActivity(mainMenu);
        this.finish();
    }
}
