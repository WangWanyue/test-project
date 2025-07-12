package com.example.drawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class GameBoard extends View {
    private static final int BOARD_SIZE = 4;
    private static final int TILE_COUNT = BOARD_SIZE * BOARD_SIZE;
    
    private int[][] board;
    private Paint paint;
    private Paint textPaint;
    private Random random;
    private float cellSize;
    private float padding = 10f;
    
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    
    public GameBoard(Context context) {
        super(context);
        init();
    }
    
    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        paint = new Paint();
        paint.setAntiAlias(true);
        
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        
        random = new Random();
        resetGame();
    }
    
    public void resetGame() {
        // Clear the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }
        
        // Add two initial tiles
        addRandomTile();
        addRandomTile();
        invalidate();
    }
    
    private void addRandomTile() {
        int emptyCells = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCells++;
                }
            }
        }
        
        if (emptyCells == 0) return;
        
        int target = random.nextInt(emptyCells);
        int current = 0;
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    if (current == target) {
                        board[i][j] = random.nextFloat() < 0.9f ? 2 : 4;
                        return;
                    }
                    current++;
                }
            }
        }
    }
    
    public int moveTiles(Direction direction) {
        int points = 0;
        boolean moved = false;
        
        switch (direction) {
            case UP:
                points = moveUp();
                break;
            case DOWN:
                points = moveDown();
                break;
            case LEFT:
                points = moveLeft();
                break;
            case RIGHT:
                points = moveRight();
                break;
        }
        
        if (points > 0) {
            addRandomTile();
            invalidate();
        }
        
        return points;
    }
    
    private int moveLeft() {
        int points = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            points += mergeRow(board[i], true);
        }
        return points;
    }
    
    private int moveRight() {
        int points = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            points += mergeRow(board[i], false);
        }
        return points;
    }
    
    private int moveUp() {
        int points = 0;
        for (int j = 0; j < BOARD_SIZE; j++) {
            int[] column = new int[BOARD_SIZE];
            for (int i = 0; i < BOARD_SIZE; i++) {
                column[i] = board[i][j];
            }
            points += mergeRow(column, true);
            for (int i = 0; i < BOARD_SIZE; i++) {
                board[i][j] = column[i];
            }
        }
        return points;
    }
    
    private int moveDown() {
        int points = 0;
        for (int j = 0; j < BOARD_SIZE; j++) {
            int[] column = new int[BOARD_SIZE];
            for (int i = 0; i < BOARD_SIZE; i++) {
                column[i] = board[i][j];
            }
            points += mergeRow(column, false);
            for (int i = 0; i < BOARD_SIZE; i++) {
                board[i][j] = column[i];
            }
        }
        return points;
    }
    
    private int mergeRow(int[] row, boolean leftToRight) {
        int points = 0;
        int[] temp = new int[BOARD_SIZE];
        int index = 0;
        
        // Move non-zero tiles to the front
        for (int i = 0; i < BOARD_SIZE; i++) {
            int pos = leftToRight ? i : BOARD_SIZE - 1 - i;
            if (row[pos] != 0) {
                temp[index++] = row[pos];
            }
        }
        
        // Merge adjacent tiles
        for (int i = 0; i < index - 1; i++) {
            if (temp[i] == temp[i + 1]) {
                temp[i] *= 2;
                points += temp[i];
                temp[i + 1] = 0;
            }
        }
        
        // Remove zeros and update row
        int[] result = new int[BOARD_SIZE];
        index = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (temp[i] != 0) {
                result[index++] = temp[i];
            }
        }
        
        // Copy back to original row
        System.arraycopy(result, 0, row, 0, BOARD_SIZE);
        
        return points;
    }
    
    public boolean isGameOver() {
        // Check if there are empty cells
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        
        // Check if any merges are possible
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int current = board[i][j];
                // Check right neighbor
                if (j < BOARD_SIZE - 1 && board[i][j + 1] == current) {
                    return false;
                }
                // Check bottom neighbor
                if (i < BOARD_SIZE - 1 && board[i + 1][j] == current) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = Math.min(w, h) / (float) BOARD_SIZE;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw background
        paint.setColor(Color.rgb(187, 173, 160));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        
        // Draw cells
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                float left = j * cellSize + padding;
                float top = i * cellSize + padding;
                float right = left + cellSize - padding;
                float bottom = top + cellSize - padding;
                
                // Draw cell background
                paint.setColor(getTileColor(board[i][j]));
                canvas.drawRoundRect(new RectF(left, top, right, bottom), 8f, 8f, paint);
                
                // Draw tile value
                if (board[i][j] != 0) {
                    textPaint.setTextSize(getTextSize(board[i][j]));
                    textPaint.setColor(getTextColor(board[i][j]));
                    canvas.drawText(String.valueOf(board[i][j]), 
                                  left + (right - left) / 2, 
                                  top + (bottom - top) / 2 + textPaint.getTextSize() / 3, 
                                  textPaint);
                }
            }
        }
    }
    
    private int getTileColor(int value) {
        switch (value) {
            case 0: return Color.rgb(205, 193, 180);
            case 2: return Color.rgb(238, 228, 218);
            case 4: return Color.rgb(237, 224, 200);
            case 8: return Color.rgb(242, 177, 121);
            case 16: return Color.rgb(245, 149, 99);
            case 32: return Color.rgb(246, 124, 95);
            case 64: return Color.rgb(246, 94, 59);
            case 128: return Color.rgb(237, 207, 114);
            case 256: return Color.rgb(237, 204, 97);
            case 512: return Color.rgb(237, 200, 80);
            case 1024: return Color.rgb(237, 197, 63);
            case 2048: return Color.rgb(237, 194, 46);
            default: return Color.rgb(60, 58, 50);
        }
    }
    
    private int getTextColor(int value) {
        if (value <= 4) {
            return Color.rgb(119, 110, 101);
        } else {
            return Color.WHITE;
        }
    }
    
    private float getTextSize(int value) {
        if (value < 100) {
            return cellSize * 0.4f;
        } else if (value < 1000) {
            return cellSize * 0.35f;
        } else {
            return cellSize * 0.25f;
        }
    }
}