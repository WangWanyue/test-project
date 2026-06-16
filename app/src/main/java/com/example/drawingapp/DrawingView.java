package com.example.drawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private List<Path> paths = new ArrayList<>();
    private Path currentPath;
    private Paint drawPaint;

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path path : paths) {
            canvas.drawPath(path, drawPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                paths.add(currentPath);
                currentPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    currentPath.lineTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    // currentPath.lineTo(touchX, touchY); // Optional: finalize path on ACTION_UP
                }
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void clearCanvas() {
        paths.clear();
        currentPath = null;
        invalidate();
    }

    // Getter for testing
    public int getPathsCount() {
        return paths.size();
    }
}
