package com.example.drawingapp;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

public class MainActivity extends AppCompatActivity {
    private GameBoard gameBoard;
    private TextView scoreTextView;
    private TextView bestScoreTextView;
    private GestureDetectorCompat gestureDetector;
    private int currentScore = 0;
    private int bestScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBoard = findViewById(R.id.game_board);
        scoreTextView = findViewById(R.id.score_text);
        bestScoreTextView = findViewById(R.id.best_score_text);

        setupGestureDetector();
        updateScoreDisplay();
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                // Swipe right
                                moveTiles(GameBoard.Direction.RIGHT);
                            } else {
                                // Swipe left
                                moveTiles(GameBoard.Direction.LEFT);
                            }
                        }
                    } else {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                // Swipe down
                                moveTiles(GameBoard.Direction.DOWN);
                            } else {
                                // Swipe up
                                moveTiles(GameBoard.Direction.UP);
                            }
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void moveTiles(GameBoard.Direction direction) {
        int points = gameBoard.moveTiles(direction);
        if (points > 0) {
            currentScore += points;
            if (currentScore > bestScore) {
                bestScore = currentScore;
            }
            updateScoreDisplay();
        }
        
        if (gameBoard.isGameOver()) {
            showGameOverDialog();
        }
    }

    private void updateScoreDisplay() {
        scoreTextView.setText("Score: " + currentScore);
        bestScoreTextView.setText("Best: " + bestScore);
    }

    private void showGameOverDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Game Over!")
               .setMessage("Your score: " + currentScore + "\nBest score: " + bestScore)
               .setPositiveButton("New Game", (dialog, which) -> {
                   resetGame();
               })
               .setNegativeButton("Exit", (dialog, which) -> {
                   finish();
               })
               .setCancelable(false)
               .show();
    }

    private void resetGame() {
        currentScore = 0;
        gameBoard.resetGame();
        updateScoreDisplay();
    }
}
