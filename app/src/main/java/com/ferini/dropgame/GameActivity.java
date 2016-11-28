package com.ferini.dropgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RESULT_DELAY = 1000;
    public static final String EXTRA_SCORE = "score";
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final TextView scoreText = (TextView) findViewById(R.id.scoreText);

        gameView = (GameView) findViewById(R.id.gameView);
        gameView.newEgg();
        gameView.setListener(new GameView.GameListener() {
            @Override
            public void onScoreChanged(int score) {
                scoreText.setText(String.valueOf(score));
            }

            @Override
            public void onGameOver(final int score) {
                Toast.makeText(GameActivity.this, "Game over!", Toast.LENGTH_SHORT).show();
                scoreText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent data = new Intent();
                        data.putExtra(EXTRA_SCORE, score);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }, RESULT_DELAY);
            }
        });

        findViewById(R.id.imageButtonLeft).setOnClickListener(this);
        findViewById(R.id.imageButtonRight).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButtonLeft) {
            gameView.moveLeft();
        }

        if (view.getId() == R.id.imageButtonRight) {
            gameView.moveRight();
        }
    }
}
