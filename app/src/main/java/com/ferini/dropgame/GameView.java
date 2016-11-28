package com.ferini.dropgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View implements Runnable {

    public static final double TICK_ACCELERATION = 0.9;

    public interface GameListener {
        void onScoreChanged(int score);
        void onGameOver(int score);
    }

    private static final int COLUMNS = 3;
    private static final int ROWS = 5;
    private static final int INITIAL_TICK = 500;

    private final Bitmap brokenEggBitmap;
    private final Bitmap eggBitmap;
    private final Bitmap basketBitmap;

    private int basketPosition = COLUMNS / 2;
    private int eggPosition = 0;
    private int eggHeight = ROWS - 1;
    private int tick = INITIAL_TICK;

    private int score = 0;
    private GameListener listener;

    private boolean gameOver = false;
    private boolean alreadySaved = false;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        brokenEggBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.broken_egg);
        eggBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.egg);
        basketBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public void newEgg() {
        alreadySaved = false;
        eggHeight = ROWS - 1;
        eggPosition = (int) (Math.random() * COLUMNS);
        invalidate();

        postDelayed(this, tick);
    }

    public void moveLeft() {
        if (gameOver)
            return;

        basketPosition = Math.max(basketPosition - 1, 0);

        invalidate();
    }

    public void moveRight() {
        if (gameOver)
            return;

        basketPosition = Math.min(basketPosition + 1, COLUMNS - 1);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int columnWidth = canvas.getWidth() / COLUMNS;
        int rowHeight = canvas.getHeight() / ROWS;

        if (eggHeight == 0 && eggPosition != basketPosition && !alreadySaved) {
            canvas.drawBitmap(brokenEggBitmap,
                    columnWidth * eggPosition + columnWidth / 2 - brokenEggBitmap.getWidth() / 2,
                    rowHeight * (ROWS - 1) + rowHeight / 2 - brokenEggBitmap.getHeight() / 2,
                    null);
        } else {
            canvas.drawBitmap(eggBitmap,
                    columnWidth * (alreadySaved ? basketPosition : eggPosition) + columnWidth / 2 - eggBitmap.getWidth() / 2,
                    rowHeight * (ROWS - 1 - eggHeight) + rowHeight / 2 - eggBitmap.getHeight() / 2,
                    null);
        }

        canvas.drawBitmap(basketBitmap,
                columnWidth * basketPosition + columnWidth / 2 - basketBitmap.getWidth() / 2,
                canvas.getHeight() - basketBitmap.getHeight(),
                null);
    }

    @Override
    public void run() {
        eggHeight--;
        if (eggHeight < 0) {
            newEgg();
            return;
        }

        invalidate();

        if (eggHeight == 0) {
            if (basketPosition == eggPosition) {
                score++;
                listener.onScoreChanged(score);
                tick *= TICK_ACCELERATION;
                alreadySaved = true;
                postDelayed(this, tick);
            } else {
                gameOver = true;
                listener.onGameOver(score);
            }
        } else {
            postDelayed(this, tick);
        }
    }
}
