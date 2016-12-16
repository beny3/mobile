package unige.coet.beny.speedracer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        String message = intent.getStringExtra(GameActivity.EXTRA_MESSAGE);

        TextView scoreView = (TextView) findViewById(R.id.score);
        scoreView.setText("Your score : "+message);
    }

    public void newGame(View view){
        Intent newGame = new Intent(this, GameActivity.class);
        startActivity(newGame);
        this.finish();
    }

    public void mainMenu(View view){
        Intent mainMenu = new Intent(this, MainMenu.class);
        startActivity(mainMenu);
        this.finish();
    }

}
